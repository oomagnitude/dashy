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

  /*
  .svg-container {
    display: inline-block;
    position: relative;
    width: 100%;
    padding-bottom: 100%;
    vertical-align: middle;
    overflow: hidden;
  }
   */
  val svgContainer = style(display.inlineBlock, position.relative, width(100 %%), paddingBottom(100 %%),
    verticalAlign.middle, overflow.hidden)


  /*
  .svg-content {
    display: inline-block;
    position: absolute;
    top: 0;
    left: 0;
  }
   */
  val svgContent = style(display.inlineBlock, position.absolute, top.`0`, left.`0`)
}