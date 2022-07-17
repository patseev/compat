package com.patseev.compat

import cats.data.{ Validated, ValidatedNec }
import cats.syntax.all._
import com.patseev.compat.validations.{ function, interface, parameter, project }
import com.patseev.compat.nodes.Project
import pprint.pprintln

import java.nio.file.Path

object Main extends App {
  val RESOURCES_PATH = "/Users/xittz/Learning/compat/src/main/resources"

  val project1 = Project.walk(Path.of(RESOURCES_PATH, "files/v1"))
  val project2 = Project.walk(Path.of(RESOURCES_PATH, "files/v2"))

  /* List of validations applied to each function parameter */
  val paramValidations = Set(
    parameter.Validation.Removal,
    parameter.Validation.Addition,
    parameter.Validation.TypeEquality,
  )

  /* List of validations applied to each function */
  val functionValidations = Set(
    function.Validation.Removal,
    function.Validation.ParamValidations(paramValidations),
    function.Validation.ReturnTypeEquality,
  )

  /* List of validations applied to each interface */
  val interfaceValidations = Set(
    interface.Validation.Removal,
    interface.Validation.FunctionValidations(functionValidations),
  )

  /* List of validations applied to each project */
  val projectValidations = Set(
    project.Validation.Removal,
    project.Validation.Interfaces(interfaceValidations),
  )

  val outcome: ValidatedNec[String, Unit] =
    project.validate(Some(project1), Some(project2))(projectValidations)

  outcome match {
    case Validated.Valid(_) =>
      pprintln("=======================")
      pprintln("Projects are compatible")
      pprintln("=======================")
    case Validated.Invalid(e) =>
      pprintln("===========================")
      pprintln("Projects are NOT compatible")
      pprintln("===========================")
      pprintln("Errors:")
      e.toList.foreach(err => pprintln(err))
  }
}
