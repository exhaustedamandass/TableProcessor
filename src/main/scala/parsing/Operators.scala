package parsing

object Operators {
  val ops: Map[String, Operator] = Map(
    "+" -> new Operator {
      def apply(lhs: Double, rhs: Double): Double = lhs + rhs
      def precedence: Int = 1
    },
    "-" -> new Operator {
      def apply(lhs: Double, rhs: Double): Double = lhs - rhs
      def precedence: Int = 1
    },
    "*" -> new Operator {
      def apply(lhs: Double, rhs: Double): Double = lhs * rhs
      def precedence: Int = 2
    },
    "/" -> new Operator {
      def apply(lhs: Double, rhs: Double): Double = lhs / rhs
      def precedence: Int = 2
    }
  )
}
