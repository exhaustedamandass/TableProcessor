package prettyPrinting.prettyPrinterRegistries

import prettyPrinting.prettyPrinters.{CSVPrettyPrinter, MarkdownPrettyPrinter}
import prettyPrinting.PrettyPrinterRegistry

object DefaultPrettyPrinterRegistry extends PrettyPrinterRegistry {
  // Register all known printers here:
  register("csv", new CSVPrettyPrinter)
  register("md", new MarkdownPrettyPrinter)
}
