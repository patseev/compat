ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.8"

lazy val root = (project in file("."))
  .settings(
    name := "compat",
    libraryDependencies ++= Seq(
      "org.scalameta" %% "scalameta" % "4.5.9",
      "com.lihaoyi"   %% "pprint"    % "0.7.0",
      "org.typelevel" %% "cats-core" % "2.7.0",
      "org.scalameta" %% "munit"     % "0.7.29" % Test,
    ),
    testFrameworks += new TestFramework("munit.Framework"),
  )
