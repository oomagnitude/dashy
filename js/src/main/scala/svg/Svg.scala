package svg

import scalatags.JsDom.all._
import scalatags.JsDom.{svgAttrs => sa, svgTags => st}

object Svg {

  /**
   *
   * @param aspectRatio ratio of width / height that should be preserved by this SVG
   * @return
   */
  def apply(aspectRatio: Double) = {
    val (width, height) = dimensions(aspectRatio)
    st.svg(sa.viewBox:=s"0 0 $width $height", sa.preserveAspectRatio:="xMinYMin meet")
  }

  def dimensions(aspectRatio: Double) = {
    val width = 500.0
    (width, width / aspectRatio)
  }
}
