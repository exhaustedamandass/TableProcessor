package filters

case class IsEmptyFilter(column: String) extends Filter {
  override def apply(row: Map[String, String]): Boolean = {
    row.get(column).forall(_.isEmpty)
  }
}