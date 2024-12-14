package prettyPrinting

object PrettyPrinterFactory {
  def getPrinter(format: String): PrettyPrinter = format.toLowerCase match {
    case "csv"      => new CSVPrettyPrinter
    case "md"       => new MarkdownPrettyPrinter
    case other      => throw new IllegalArgumentException(s"Unsupported output format: $other")
  }
}
//TODO: should be a normal class
//TODO: parsing the argument, shouldn't be a factory
