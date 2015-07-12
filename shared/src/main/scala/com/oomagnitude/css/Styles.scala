package com.oomagnitude.css

import com.oomagnitude.css.SvgCss._

import scala.language.postfixOps
import scalacss.Defaults._

object Styles extends StyleSheet.Inline {
  import dsl._

  val salmonBackground = style(backgroundColor(c"#F08080"))
  val greyBackground = style(backgroundColor(c"#d8d8d8"))

  val graphNode = style(stroke(c"#fff"), strokeWidth(1.5))
  val graphLink = style(stroke(c"#999"), strokeOpacity(0.6))

}