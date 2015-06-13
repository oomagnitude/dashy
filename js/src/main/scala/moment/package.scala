import scala.scalajs.js

package object moment {
  val DefaultDateFormat = "YYYY-MM-DD-HH-mm-ss"
  val momentjs = js.Dynamic.global.moment

  def humanReadableDurationFromNow(before: String, format: String = DefaultDateFormat): String = {
    momentjs(before, format).fromNow().asInstanceOf[String]
  }

  def calendarDate(date: String, format: String = DefaultDateFormat): String = {
    momentjs(date, format).format("MMMM Do YYYY, h:mm:ss a").toString
  }
}
