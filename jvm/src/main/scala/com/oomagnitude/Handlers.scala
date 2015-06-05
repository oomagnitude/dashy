package com.oomagnitude

import java.nio.file.Path

import akka.http.scaladsl.model.ws.{Message, TextMessage}
import akka.stream.io.SynchronousFileSource
import akka.stream.scaladsl._
import akka.stream.stage._
import akka.util.ByteString

import scala.annotation.tailrec
import scala.concurrent.duration.{FiniteDuration, _}
import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.language.postfixOps

object Handlers {
  object Tick

  def dirListing(dirPath: Path)(implicit ex: ExecutionContextExecutor): Future[Seq[String]] = {
    Future(dirPath.toFile.listFiles().filterNot(_.isHidden)).map {
      case null => Seq.empty[String]
      case files => files.map(_.getName).toSeq
    }
  }

  def streamFile(path: Path): Flow[Message, Message, Any] = {
    Flow() { implicit builder =>
      import FlowGraph.Implicits._

      val merge = builder.add(Merge[String](2))
      val filter = builder.add(Flow[String].filter(_ => false))

      // convert to string so we can connect to merge
      val mapMsgToEmpty = builder.add(Flow[Message].map[String]{ msg => "" })
      val mapStringToMsg = builder.add(Flow[String].map[Message]{TextMessage.Strict})

      val fileSource = builder.add(
        throttledSource(0 seconds, 1 second,
          SynchronousFileSource(path.toFile).transform(() => parseLines("\n")).concat(Source.single("END"))))

      mapMsgToEmpty ~> filter ~> merge
      fileSource ~> merge ~> mapStringToMsg

      (mapMsgToEmpty.inlet, mapStringToMsg.outlet)
    }
  }

  /**
   * Create a source which is throttled to a number of message per second.
   */
  def throttledSource[T](delay: FiniteDuration, interval: FiniteDuration, messageSource: Source[T, _]): Source[T, Unit] = {
    Source[T]() { implicit b =>
      import FlowGraph.Implicits._

      // two source
      val tickSource = Source(delay, interval, Tick)

      // we use zip to throttle the stream
      val zip = b.add(Zip[Tick.type, T]())
      val unzip = b.add(Flow[(Tick.type, T)].map(_._2))

      // setup the message flow
      tickSource ~> zip.in0
      messageSource ~> zip.in1
      zip.out ~> unzip

      unzip.outlet
    }
  }

  // lifted from:
  // http://doc.akka.io/docs/akka-stream-and-http-experimental/1.0-RC1/scala/stream-cookbook.html#cookbook-parse-lines-scala
  def parseLines(separator: String/*, maximumLineBytes: Int*/) =
    new StatefulStage[ByteString, String] {
      private val separatorBytes = ByteString(separator)
      private val firstSeparatorByte = separatorBytes.head
      private var buffer = ByteString.empty
      private var nextPossibleMatch = 0

      def initial = new State {
        override def onPush(chunk: ByteString, ctx: Context[String]): SyncDirective = {
          buffer ++= chunk
//          if (buffer.size > maximumLineBytes)
//            ctx.fail(new IllegalStateException(s"Read ${buffer.size} bytes " +
//              s"which is more than $maximumLineBytes without seeing a line terminator"))
//          else
            emit(doParse(Vector.empty).iterator, ctx)
        }

        @tailrec
        private def doParse(parsedLinesSoFar: Vector[String]): Vector[String] = {
          val possibleMatchPos = buffer.indexOf(firstSeparatorByte, from = nextPossibleMatch)
          if (possibleMatchPos == -1) {
            // No matching character, we need to accumulate more bytes into the buffer
            nextPossibleMatch = buffer.size
            parsedLinesSoFar
          } else if (possibleMatchPos + separatorBytes.size > buffer.size) {
            // We have found a possible match (we found the first character of the terminator
            // sequence) but we don't have yet enough bytes. We remember the position to
            // retry from next time.
            nextPossibleMatch = possibleMatchPos
            parsedLinesSoFar
          } else {
            if (buffer.slice(possibleMatchPos, possibleMatchPos + separatorBytes.size)
              == separatorBytes) {
              // Found a match
              val parsedLine = buffer.slice(0, possibleMatchPos).utf8String
              val sizeBefore = buffer.size
              val numDropped = possibleMatchPos + separatorBytes.size
              buffer = buffer.drop(possibleMatchPos + separatorBytes.size)
              val sizeAfter = buffer.size
//              println(List(sizeBefore, sizeAfter, numDropped).mkString(","))
              nextPossibleMatch -= possibleMatchPos + separatorBytes.size
              doParse(parsedLinesSoFar :+ parsedLine)
            } else {
              nextPossibleMatch += 1
              doParse(parsedLinesSoFar)
            }
          }

        }
      }
    }
}
