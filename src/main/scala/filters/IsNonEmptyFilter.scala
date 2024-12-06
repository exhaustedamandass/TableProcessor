package filters

case class IsNonEmptyFilter(column: String) extends Filter {
  override def apply(row: Map[String, String]): Boolean = {
    row.get(column).exists(_.nonEmpty)
  }
}