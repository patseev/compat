package com.patseev.compat

import com.patseev.compat.nodes.Parameter
import com.patseev.compat.syntax.all._
import munit._

import scala.meta._

class ParameterSpec extends FunSuite {
  test("Non-optional parameter") {
    assertEquals(
      parseParameters("def foo(bar: Int): String"),
      List(
        Parameter(
          name = "bar",
          argumentType = "Int",
          optional = false,
          hasDefault = false,
        )
      ),
    )
  }

  test("Optional parameter") {
    assertEquals(
      parseParameters("def foo(bar: Option[Int]): String"),
      List(
        Parameter(
          name = "bar",
          argumentType = "Int",
          optional = true,
          hasDefault = false,
        )
      ),
    )
  }

  test("Curly-brace named parameter - braces removed") {
    assertEquals(
      parseParameters("def find(`name`: String): User"),
      List(
        Parameter(
          name = "name",
          argumentType = "String",
          optional = false,
          hasDefault = false,
        )
      ),
    )
  }

  test("Default argument") {
    assertEquals(
      parseParameters("""def find(name: Option[String] = Some("abc")): User"""),
      List(
        Parameter(
          name = "name",
          argumentType = "String",
          optional = true,
          hasDefault = true,
        )
      ),
    )
  }

  test("Default argument for non-optional field") {
    assertEquals(
      parseParameters("""def find(age: Int = 18): User"""),
      List(
        Parameter(
          name = "age",
          argumentType = "Int",
          optional = false,
          hasDefault = true,
        )
      ),
    )
  }

  test("Multiple Arguments") {
    assertEquals(
      parseParameters("def find(name: String, age: Option[Int]): Option[Person]"),
      List(
        Parameter(
          name = "name",
          argumentType = "String",
          optional = false,
          hasDefault = false,
        ),
        Parameter(
          name = "age",
          argumentType = "Int",
          optional = true,
          hasDefault = false,
        ),
      ),
    )
  }

  test("Nested option") {
    assertEquals(
      parseParameters("def foo(bar: Option[Option[Int]]): String"),
      List(
        Parameter(
          name = "bar",
          argumentType = "Option[Int]",
          optional = true,
          hasDefault = false,
        )
      ),
    )
  }

  test("Some prints") {
    val functions = List(
      "def foo(bar: Option[Option[Int]]): String",
      "def foo(bar: Option[Option[Int]]): F[String]",
      "def foo(bar: Option[Option[Int]]): F[Option[Either[F[Option[Int]], String]]]",
    )

    functions.foreach(f => println(parseReturnType(f)))
    assert(true)
  }
//

//
//  test("Nested option parameter") {}

  def getFunction(stat: Stat): Decl.Def =
    stat.collect {
      case d: Decl.Def => d
    }.head

  def getFunctionParameters(defDecl: Decl.Def): List[Term.Param] =
    defDecl.paramss.flatten

  def getParams(stat: Stat): List[Term.Param] =
    getFunctionParameters(getFunction(stat))

  def parseParameters(
      raw: String
    ): List[Parameter] = {
    val function = raw.parse[Stat].get
    val rawParams = getParams(function)
    rawParams.map(Parameter.from)
  }
}
