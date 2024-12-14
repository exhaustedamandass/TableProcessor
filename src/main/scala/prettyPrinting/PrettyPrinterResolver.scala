package prettyPrinting

class PrettyPrinterResolver {
  // A normal class that resolves the printer from the registry
  class PrettyPrinterResolver(format: String) {
    private val printer: PrettyPrinter = PrettyPrinterRegistry.getPrinter(format)

    def getPrinter: PrettyPrinter = printer
  }
}
