
package object viz {
  def dimensions(aspectRatio: Double) = {
    val width = 500.0
    (width, width / aspectRatio)
  }
}
