package parsing

import parsing.traits.AstNode

case class CellReferenceNode(column: String, row: Int) extends AstNode

