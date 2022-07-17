package com.patseev.compat.validations

import cats.data.ValidatedNec
import cats.syntax.all._
import com.patseev.compat.nodes.Parameter

object parameter {
  sealed trait Validation extends Product with Serializable {

    /* Validates parameters against each other
     *  In case if p1 is not provided, but p2 is provided, Addition rules are checked
     *  In case if p1 is provided, but p2 is not provided, Removal rules are checked
     *  */
    def validate(p1: Option[Parameter], p2: Option[Parameter]): ValidatedNec[String, Unit]
  }

  object Validation {
    case object TypeEquality extends Validation {
      override def validate(
          p1: Option[Parameter],
          p2: Option[Parameter],
        ): ValidatedNec[String, Unit] =
        (p1, p2) match {
          case (Some(p1), Some(p2)) =>
            Option
              .when(p1.argumentType != p2.argumentType)(
                s"""
                 |Types are NOT equal for parameter ${p1.name}.
                 |${p1.argumentType} != ${p2.argumentType}
                 |""".stripMargin
              )
              .toInvalidNec(())
          case _ => ().validNec[String]
        }
    }

    case object Removal extends Validation {
      override def validate(
          p1: Option[Parameter],
          p2: Option[Parameter],
        ): ValidatedNec[String, Unit] =
        (p1, p2) match {
          case (Some(p1), None) =>
            Option
              .when(!p1.optional)(
                s"Parameter ${p1.name} was removed but it was NOT optional"
              )
              .toInvalidNec(())
          case _ => ().validNec[String]
        }
    }
    case object Addition extends Validation {
      override def validate(
          p1: Option[Parameter],
          p2: Option[Parameter],
        ): ValidatedNec[String, Unit] =
        (p1, p2) match {
          case (None, Some(p2)) =>
            Option
              .when(!p2.optional && !p2.hasDefault)(
                s"Parameter ${p2.name} was added as NON optional with NO default value"
              )
              .toInvalidNec(())
          case _ => ().validNec[String]
        }
    }
  }
}
