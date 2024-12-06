package parsing.traits

sealed trait AST
case class Number(value: Double) extends AST
case class Reference(col: String, row: Int) extends AST
case class BinaryOp(operator: String, left: AST, right: AST) extends AST
