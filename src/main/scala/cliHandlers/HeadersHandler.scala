package cliHandlers

class HeadersHandler extends ParameterHandler {
  override def handle(args: List[String], config: AppConfig): (List[String], AppConfig) = {
    args match {
      case "--headers" :: tail =>
        (tail, config.copy(includeHeaders = true))
      case _ => (args, config)
    }
  }

  override def helpMessage: String =
    "--headers : include headers in the output (optional)"
}
