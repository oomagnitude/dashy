package com.oomagnitude.css

import scalacss.Attr
import scalacss.ValueT.{Number, TypedAttrT1, TypedAttr_Color}

object SvgCss {
  object fill extends TypedAttr_Color {
    override val attr = Attr.real("fill")
  }

  object stroke extends TypedAttr_Color {
    override val attr = Attr.real("stroke")
  }

  object strokeWidth extends TypedAttrT1[Number] {
    override val attr = Attr.real("stroke-width")
  }

  object strokeOpacity extends TypedAttrT1[Number] {
    override val attr = Attr.real("stroke-opacity")
  }
}
