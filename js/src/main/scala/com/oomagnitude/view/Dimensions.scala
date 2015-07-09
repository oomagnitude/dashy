package com.oomagnitude.view

case class Margin(top: Int, right: Int, bottom: Int, left: Int)

case class Dimensions(innerHeight: Int, innerWidth: Int, margin: Margin) {
  val height = innerHeight + margin.top + margin.bottom
  val width = innerWidth + margin.left + margin.right
}
