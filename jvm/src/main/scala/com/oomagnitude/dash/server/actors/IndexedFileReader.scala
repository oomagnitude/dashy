package com.oomagnitude.dash.server.actors

import java.io.{BufferedReader, FileReader}
import java.nio.file.Path

import akka.actor.{Actor, ActorLogging, Props}
import com.oomagnitude.dash.server.actors.IndexedFileReader._
import com.oomagnitude.api.StreamControl.Seek

object IndexedFileReader {
  case class Advance(amount: Long)

  sealed trait Position {
    def >=(other: Long): Boolean

    def +(other: Long): Long
  }
  case class Index(index: Long) extends Position {
    override def >=(other: Long): Boolean = index >= other

    override def +(other: Long): Long = index + other
  }
  case object Eof extends Position {
    override def >=(other: Long): Boolean = false

    override def +(other: Long): Long = 0
  }

  // Will be able to parse [index] from strings that begin with:
  // {"timestep":[index],"
  val parseIndex: String => Long = { text =>
    try {
      val colonIndex = text.indexOf(":")
      val commaIndex = text.indexOf(",")
      text.substring(colonIndex + 1, commaIndex).trim.toLong
    } catch {
      case e: Exception => throw new IllegalArgumentException(s"unable to parse text due to exception ($text)", e)
    }
  }

  def props(path: Path, bufferSize: Int)(toIndex: String => Long = parseIndex) =
    Props(classOf[IndexedFileReader], path, toIndex, bufferSize)
}

class IndexedFileReader(path: Path,
                        toIndex: String => Long,
                        bufferSize: Int) extends Actor with ActorLogging with Subscribable {
  var reader: Option[BufferedReader] = None
  var currentIndex: Position = Eof

  private def open(): Unit = {
    currentIndex = Eof
    if (reader.isEmpty) {
      reader = try {
        Some(new BufferedReader(new FileReader(path.toFile), bufferSize))
      } catch {
        // TODO: signal failure to supervisor
        case e: Exception =>
          log.error(e, s"exception in opening file $path")
          None
      }
    }
  }

  private def close(): Unit = {
    currentIndex = Eof
    reader.foreach(_.close())
    reader = None
  }

  override def kill(): Unit = {
    close()
  }

  // TODO: BUG: when the end of the stream is reached, it wraps around back to the beginning
  private def seek(destination: Long): Option[String] = {
    if (reader.isEmpty) open()

    if (currentIndex >= destination) {
      // re-open the file (start at the beginning)
      close(); open()
      seek(destination)
    } else {
      val text = reader.get.readLine()
      if (text == null) {
        close()
        None
      } else {
        val position = toIndex(text)
        if (position >= destination) {
          currentIndex = Index(position)
          Some(text)
        } else seek(destination)
      }
    }
  }

  private def doSeek(destination: Long): Unit = {
    subscriber.foreach { sub =>
      seek(destination).foreach(sub ! _)
    }
  }

  val handleControls: Receive = {
    case Advance(amount) => doSeek(currentIndex + amount)
    case Seek(destination) => doSeek(destination)
    case Close => close()
  }

  override def receive: Receive = handleControls orElse handleSubsciption
}
