package outputOptions

trait PrettyPrinter {
  def printTable(rows: Seq[(Int, Seq[String])], headers: Option[Seq[String]], separator: String = ","): String
}
