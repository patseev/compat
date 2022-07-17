package com.patseev.compat.validations

import cats.data.ValidatedNec
import cats.syntax.all._
import com.patseev.compat.nodes._

object interface {
  sealed trait Validation extends Product with Serializable {
    def validate(int1: Interface, int2: Option[Interface]): ValidatedNec[String, Unit]
  }

  object Validation {
    case object Removal extends Validation {
      override def validate(int1: Interface, int2: Option[Interface]): ValidatedNec[String, Unit] =
        (int2 match {
          case None =>
            Option
              .when(!int1.annotations.contains(annotation.DEPRECATED))(
                s"Interface ${int1.name} was removed but it was NOT deprecated"
              )
              .toInvalidNec(())
          case _ =>
            ().validNec[String]
        }).leftMap(_.map(_.prependedAll(s"${int1.name}: ")))
    }

    case class FunctionValidations(validations: Set[function.Validation]) extends Validation {
      override def validate(int1: Interface, int2: Option[Interface]): ValidatedNec[String, Unit] =
        (int2 match {
          case Some(int2) =>
            val int2FnsByName = int2.functions.map(f => f.name -> f).toMap

            int1
              .functions
              .map { fn1 =>
                validations.toList.map(_.validate(fn1, int2FnsByName.get(fn1.name))).combineAll
              }
              .combineAll
          case None => ().validNec[String]
        }).leftMap(_.map(_.prependedAll(s"${int1.name}.")))
    }
  }

  def validate(
      int1: Interface,
      int2: Option[Interface],
    )(
      validations: Set[Validation]
    ): ValidatedNec[String, Unit] =
    validations.toList.map(_.validate(int1, int2)).combineAll
}
