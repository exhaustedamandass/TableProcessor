package prettyPrinting

trait PrettyPrinterRegistry {
  private val registry = scala.collection.mutable.Map.empty[String, PrettyPrinter]

  protected def register(format: String, printer: PrettyPrinter): Unit = {
    registry.update(format.toLowerCase, printer)
  }

  def getPrinter(format: String): PrettyPrinter = {
    registry.get(format.toLowerCase) match {
      case Some(printer) => printer
      case None => throw new IllegalArgumentException(s"Unsupported output format: $format")
    }
  }
}
