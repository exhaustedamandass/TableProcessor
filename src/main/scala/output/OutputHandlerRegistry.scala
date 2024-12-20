package output

trait OutputHandlerRegistry {
  private val registry = scala.collection.mutable.Map[String, Map[String, String] => OutputHandler]()

  protected def register(key: String, builder: Map[String, String] => OutputHandler): Unit = {
    registry.update(key.toLowerCase, builder)
  }

  def getOutputHandler(argsMap: Map[String, String]): OutputHandler = {
    // Determine which key to use. For example, if argsMap contains "output-file", we might choose "file"
    // Otherwise, fallback to "stdout"
    val outputType =
    if (argsMap.get("output-file").exists(_.nonEmpty)) "file"
    else "stdout"

    registry.get(outputType.toLowerCase) match {
      case Some(builder) => builder(argsMap)
      case None => throw new IllegalArgumentException(s"No output handler registered for type: $outputType")
    }
  }
}

