package com.oomagnitude.dash.server

import akka.stream.scaladsl.Flow
import com.oomagnitude.dash.server.streams.Flows._
import com.oomagnitude.metrics.model._
import upickle.Reader

import scala.reflect.runtime.universe._

trait MetadataImplicits {
  def toType(className: String): Type = {
    val clazz = Class.forName(className)
    val m = runtimeMirror(getClass.getClassLoader)
    m.classSymbol(clazz).toType
  }

  implicit class CountExt(c: Count.type) {
    def transformFlow: Flow[String, DataPoint[Double], Any] = parseJson[DataPoint[Double]].via(counterFlow)
  }

  implicit class ScalarExt(s: Scalar.type) {
    def transformFlow: Flow[String, DataPoint[Double], Any] = parseJson[DataPoint[Double]]
  }

  implicit class TimeExt(t: Time) {
    def transformFlow: Flow[String, DataPoint[Double], Any] = parseJson[DataPoint[TimerSample]].via(timerFlow)
  }

  implicit class VectorExt(v: Vector) {
    def transformFlow: Flow[String, DataPoint[Seq[Double]], Any] = parseJson[DataPoint[Seq[Double]]]
  }

  implicit class InterpretationExt(int: Interpretation) {
    def isConvertibleTo[T: TypeTag]: Boolean = {
      typeOf[T] =:= targetType
    }

    def targetType: Type = {
      int match {
        case Count => typeOf[Double]
        case Scalar => typeOf[Double]
        case _: Time => typeOf[Double]
        case Info(dataType) => toType(dataType)
        case _: Vector => typeOf[Seq[Double]]
      }
    }

    def parseFlow[T: Reader: TypeTag]: Flow[String, DataPoint[T], Any] = {
      require(isConvertibleTo[T], s"can't convert metric with interpretation $int to type ${typeOf[T]}")
      parseJson[DataPoint[T]]
    }
  }
}
