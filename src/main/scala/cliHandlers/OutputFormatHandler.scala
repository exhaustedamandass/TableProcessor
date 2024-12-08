package cliHandlers

class OutputFormatHandler extends ParameterHandler {
  override def handle(args: List[String], config: AppConfig): (List[String], AppConfig) = {
    args match {
      case "--output-format" :: fmt :: tail =>
        (tail, config.copy(outputFormat = fmt))
      case _ => (args, config)
    }
  }

  override def helpMessage: String =
    "--output-format (csv|md) : the format of the output (optional, defaults to \"csv\")"
}