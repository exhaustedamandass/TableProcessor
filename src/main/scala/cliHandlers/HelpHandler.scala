package cliHandlers

class HelpHandler extends ParameterHandler {
  override def handle(args: List[String], config: AppConfig): (List[String], AppConfig) = {
    args match {
      case ("--help" | "-h") :: tail =>
        (tail, config.copy(showHelp = true))
      case _ => (args, config) // Not handled, move on
    }
  }

  override def helpMessage: String =
    "--help or -h : Show this help message"
}
