package prettyPrinting

object PrettyPrinterRegistry {
  private var registry: Map[String, () => PrettyPrinter] = Map.empty

  /** Register a printer constructor function under a given format name */
  def register(format: String, constructor: () => PrettyPrinter): Unit = {
    registry += (format.toLowerCase -> constructor)
  }

  /** Retrieve a printer by format name */
  def getPrinter(format: String): PrettyPrinter = {
    registry.get(format.toLowerCase) match {
      case Some(ctor) => ctor()
      case None => throw new IllegalArgumentException(s"Unsupported output format: $format")
    }
  }
}
