package outputOptions

object PrettyPrinterFactory {
  def getPrinter(format: String): PrettyPrinter = format.toLowerCase match {
    case "csv"      => new CSVPrettyPrinter
    case "md"       => new MarkdownPrettyPrinter
    case other      => throw new IllegalArgumentException(s"Unsupported output format: $other")
  }
}
