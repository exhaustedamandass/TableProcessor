package operator

trait ArithmeticOperator extends Operator[Double, Double] {
  def precedence: Int
}
