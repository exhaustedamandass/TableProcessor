package parsing.traits

trait Operator {
  def symbol: String
  def apply(left: Double, right: Double): Double
}
