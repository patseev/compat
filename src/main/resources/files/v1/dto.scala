package com.patseev.compat.files.v1

object dto {
  case class Image(
      width: Int,
      height: Int,
      name: String,
      extension: Extension,
    )

  sealed trait Extension extends Product with Serializable

  object Extension {
    case object JPG extends Extension
    case object PNG extends Extension
  }
}
