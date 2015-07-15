package com.oomagnitude.css

import com.oomagnitude.css.SvgCss._

import scala.language.postfixOps
import scalacss.Defaults._

object Styles extends StyleSheet.Inline {
  import dsl._

  val salmonBackground = style(backgroundColor(c"#F08080"))
  val greyBackground = style(backgroundColor(c"#d8d8d8"))
  val whiteBackground = style(backgroundColor(white))

  val graphNode = style(stroke(white), strokeWidth(1.5))
  val graphLink = style(stroke(c"#999"), strokeOpacity(0.6))

  // TODO: how do you get none to work as a color type?
  val treeLink = style(fill:=!"none", stroke(c"#ccc"), strokeWidth(1))
  val treeNode = style(fontSize(4 px), fontFamily:="sans-serif")
  val treeNodeCircle = style(fill(white), stroke(steelblue), strokeWidth(1))
  // TODO: fill-rule: evenodd
  val partitionPath = style(stroke(white))
}