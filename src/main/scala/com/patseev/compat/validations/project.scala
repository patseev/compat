package com.patseev.compat.validations

import cats.data.ValidatedNec
import cats.syntax.all._
import com.patseev.compat.nodes.{ Interface, Project }
import pprint.pprintln

object project {
  sealed trait Validation extends Product with Serializable {
    def validate(p1: Option[Project], p2: Option[Project]): ValidatedNec[String, Unit]
  }

  object Validation {
    case object Removal extends Validation {
      override def validate(
          p1: Option[Project],
          p2: Option[Project],
        ): ValidatedNec[String, Unit] =
        (p1, p2) match {
          case (Some(p1), None) =>
            s"Project ${p1.location} was removed".invalidNec
          case _ =>
            ().validNec[String]
        }
    }

    case class Interfaces(rules: Set[interface.Validation]) extends Validation {
      override def validate(p1: Option[Project], p2: Option[Project]): ValidatedNec[String, Unit] =
        (p1, p2) match {
          case (Some(p1), Some(p2)) =>
            val p2InterfacesMap = p2.interfaces.map(i => i.name -> i).toMap

            p1.interfaces
              .map { i1 =>
                interface.validate(i1, p2InterfacesMap.get(i1.name))(rules)
              }
              .combineAll

          case _ =>
            ().validNec[String]
        }
    }
  }

  def validate(
      p1: Option[Project],
      p2: Option[Project],
    )(
      rules: Set[Validation]
    ): ValidatedNec[String, Unit] =
    rules.toList.map(_.validate(p1, p2)).combineAll
}
