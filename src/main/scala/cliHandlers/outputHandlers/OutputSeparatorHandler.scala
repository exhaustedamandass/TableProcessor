package cliHandlers.outputHandlers

import cliHandlers.ParameterHandler
import cliHandlers.config.AppConfig

class OutputSeparatorHandler extends ParameterHandler {
  override def handle(args: List[String], config: AppConfig): (List[String], AppConfig) = {
    args match {
      case "--output-separator" :: sep :: tail =>
        (tail, config.copy(outputSeparator = sep))
      case _ => (args, config)
    }
  }

  override def helpMessage: String =
    "--output-separator [STRING] : for CSV output, the separator in the output file (optional, defaults to \",\")"
}
