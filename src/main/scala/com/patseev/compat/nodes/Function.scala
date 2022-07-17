package com.patseev.compat.nodes

import com.patseev.compat.validations.annotation

import scala.meta._

case class Function(
    name: String,
    returnType: String,
    annotations: List[String],
    /* Parameter lists get combined into one. Maybe this simplification is not correct */
    parameters: List[Parameter],
    _underlying: Decl.Def,
  )

object Function {
  def from(fnDef: Decl.Def): Function =
    Function(
      name = fnDef.name.value,
      returnType = fnDef.decltpe.syntax,
      annotations = fnDef.mods.collect { case m: Mod.Annot => annotation(m) },
      parameters = fnDef.paramss.flatten(_.map(Parameter.from)),
      _underlying = fnDef,
    )
}
