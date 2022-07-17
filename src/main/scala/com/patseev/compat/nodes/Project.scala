package com.patseev.compat.nodes

import com.patseev.compat.nodes.FileNodes.read

import java.nio.file.{ Files, Path }
import scala.jdk.CollectionConverters._

case class Project(
    location: String,
    files: List[FileNodes],
    _underlying: Path,
  ) {
  lazy val interfaces: List[Interface] = files.flatMap(_.interfaces)
  lazy val dataClasses: List[DataClass] = files.flatMap(_.dataClasses)
  lazy val enumerations: List[Enumeration] = files.flatMap(_.enumerations)
}

object Project {
  def walk(path: Path): Project =
    Project(
      location = path.toString,
      files = Files
        .walk(path)
        .filter(Files.isRegularFile(_))
        .iterator()
        .asScala
        .toList
        .map(read),
      _underlying = path,
    )
}
