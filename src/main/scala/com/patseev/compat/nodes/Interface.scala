package com.patseev.compat.nodes

import com.patseev.compat.validations.annotation

import scala.meta._

case class Interface(
    name: String,
    annotations: List[String],
    functions: List[Function],
    _underlying: Defn.Trait,
  )

object Interface {
  /* Returns None in case if Trait is not an interface, but is an ADT */
  def from(traitDef: Defn.Trait): Option[Interface] =
    Option.when(
      !traitDef.mods.exists { case _: Mod.Sealed => true; case _ => false }
    ) {
      Interface(
        name = traitDef.name.value,
        annotations = traitDef.mods.collect { case m: Mod.Annot => annotation(m) },
        functions = traitDef.templ.stats.collect {
          case defDecl: Decl.Def => Function.from(defDecl)
        },
        _underlying = traitDef,
      )
    }
}
