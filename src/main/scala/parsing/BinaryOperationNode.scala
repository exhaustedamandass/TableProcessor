package parsing

import parsing.traits.{AstNode, Operator}

case class BinaryOperationNode(operator: Operator, left: AstNode, right: AstNode) extends AstNode
