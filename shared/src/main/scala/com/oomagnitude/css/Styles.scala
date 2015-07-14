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

  // TODO: how do you get none to work as a color type?
  val treeLink = style(fill:=!"none", stroke(c"#ccc"), strokeWidth(1.5))
  val treeNode = style(fontSize(6 px), fontFamily:="sans-serif")
  // TODO: why does color.steelblue not work in stroke?
  val treeNodeCircle = style(fill(c"#fff"), stroke:=!"steelblue", strokeWidth(1.5))
}