package svg

import com.oomagnitude.css.Styles

import scalatags.JsDom.{TypedTag, svgAttrs => sa, svgTags => st}
import scalatags.JsDom.all._

object Svg {

  /**
   *
   * @param aspectRatio ratio of width / height that should be preserved by this SVG
   * @return
   */
  def apply(aspectRatio: Double) = {
    val width = 500
    val height = (width / aspectRatio).toInt
    st.svg(sa.viewBox:=s"0 0 $width $height", sa.preserveAspectRatio:="xMinYMin meet")
  }
}
