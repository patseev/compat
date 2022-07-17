package com.patseev.compat.nodes

import java.nio.file.{ Files, Path }
import scala.meta._

case class FileNodes(
    fileName: String,
    interfaces: List[Interface],
    dataClasses: List[DataClass],
    enumerations: List[Enumeration],
    _underlying: Tree,
  )

object FileNodes {
  def from(fileName: String, tree: Tree): FileNodes =
    FileNodes(
      fileName = fileName,
      interfaces = tree.collect { case t: Defn.Trait => Interface.from(t) }.flatten,
      dataClasses = List.empty,
      enumerations = List.empty,
      _underlying = tree,
    )

  def read(path: Path): FileNodes = {
    val bytes = Files.readAllBytes(path)
    val text = new String(bytes, "UTF-8")
    val input = Input.VirtualFile(path.toString, text)

    FileNodes.from(path.getFileName.toString, input.parse[Source].get)
  }
}
