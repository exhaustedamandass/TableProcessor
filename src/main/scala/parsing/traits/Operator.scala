package parsing.traits

trait Operator {
  def apply(lhs: Double, rhs: Double): Double
  def precedence: Int
}
