package com.oomagnitude.css

import com.oomagnitude.css.SvgCss._

import scala.language.postfixOps
import scalacss.Defaults._

object Styles extends StyleSheet.Inline {
  import dsl._


  val bootstrapButton = style(
    addClassName("btn btn-default"),
    fontSize(200 %%)
  )

  val blueWithOliveOutline = style(fill(blue),stroke(darkolivegreen),strokeWidth(1))

  val greyBackground = style(backgroundColor(c"#F08080"))
}