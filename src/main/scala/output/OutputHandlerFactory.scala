package output

object OutputHandlerFactory {
  def getOutputHandler(argsMap: Map[String, String]): OutputHandler = {
    if (argsMap.contains("stdout")) {
      new StdoutOutputHandler
    } else if (argsMap.contains("output-file")) {
      new FileOutputHandler(argsMap("output-file"))
    } else {
      // Default to stdout if no file and no stdout flag provided
      new StdoutOutputHandler
    }
  }
}
