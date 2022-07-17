package com.patseev.compat.validations

import com.patseev.compat.nodes.Function

import cats.data.ValidatedNec
import cats.syntax.all._

object function {
  sealed trait Validation extends Product with Serializable {
    def validate(fn1: Function, fn2: Option[Function]): ValidatedNec[String, Unit]
  }

  object Validation {
    case object Removal extends Validation {
      override def validate(fn1: Function, fn2: Option[Function]): ValidatedNec[String, Unit] =
        (fn2 match {
          case None =>
            Option
              .when(!fn1.annotations.contains(annotation.DEPRECATED))(
                s"Function ${fn1.name} got removed but it was NOT deprecated"
              )
              .toInvalidNec(())
          case Some(_) =>
            ().validNec[String]
        }).leftMap(_.map(_.prependedAll(s"${fn1.name}: ")))
    }

    case object ReturnTypeEquality extends Validation {
      override def validate(fn1: Function, fn2: Option[Function]): ValidatedNec[String, Unit] =
        (fn2 match {
          case Some(fn2) =>
            Option
              .when(fn1.returnType != fn2.returnType)(
                s"""
                   |Return types are NOT equal.
                   |${fn1.returnType} != ${fn2.returnType}
                   |""".stripMargin
              )
              .toInvalidNec(())
          case None => ().validNec[String]
        }).leftMap(_.map(_.prependedAll(s"${fn1.name}: ")))
    }
    case class ParamValidations(validations: Set[parameter.Validation]) extends Validation {
      override def validate(fn1: Function, fn2: Option[Function]): ValidatedNec[String, Unit] =
        fn2 match {
          case Some(fn2) =>
            fn2._underlying.mods
            val fn1ParamsMap = fn1.parameters.map(p => p.name -> p).toMap
            val fn2ParamsMap = fn2.parameters.map(p => p.name -> p).toMap

            (fn1ParamsMap.keySet ++ fn2ParamsMap.keySet)
              .toList
              .map { fName =>
                validations
                  .toList
                  .map(_.validate(fn1ParamsMap.get(fName), fn2ParamsMap.get(fName)))
                  .combineAll
                  .leftMap(_.map(_.prependedAll(s"${fn1.name}: ")))
              }
              .combineAll
          case None =>
            ().validNec[String]
        }
    }
  }

  def validate(
      fn1: Function,
      fn2: Option[Function],
    )(
      validations: Set[Validation]
    ): ValidatedNec[String, Unit] =
    validations.toList.map(_.validate(fn1, fn2)).combineAll
}
