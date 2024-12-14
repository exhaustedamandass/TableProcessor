package operator

trait Operator[I, O] {
  def apply(lhs: I, rhs: I): O
}
