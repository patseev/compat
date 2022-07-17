package com.patseev.compat.nodes

import scala.meta._

case class Parameter(
    name: String,
    argumentType: String,
    optional: Boolean,
    hasDefault: Boolean,
    _underlying: Term.Param,
  )

object Parameter {
  def from(param: Term.Param): Parameter = {
    /* Removes default argument part */
    val (optional, argumentType) = param.syntax.split(" =").head match {
      /* Handles case of `bar: Option[Int]`, removing outer option */
      case s"$_: Option[$argumentType]" =>
        true -> argumentType
      /* Handles regular case with no outer join */
      case s"$_: $other" =>
        false -> other
      /* Fallback, should never happen */
      case other =>
        false -> other
    }

    Parameter(
      name = param.name.value,
      optional = optional,
      argumentType = argumentType,
      hasDefault = param.default.nonEmpty,
      _underlying = param,
    )
  }
}
