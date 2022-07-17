package com.patseev.compat.validations

import scala.meta.Mod

object annotation {
  val DEPRECATED: String = "deprecated"

  def apply(mod: Mod.Annot): String =
    mod.syntax.drop(1)
}
