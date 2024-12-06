package filters

trait Filter {
  def apply(row: Map[String, String]): Boolean
}
