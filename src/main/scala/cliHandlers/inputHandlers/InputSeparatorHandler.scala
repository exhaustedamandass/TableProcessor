package cliHandlers.inputHandlers

import cliHandlers.ParameterHandler
import cliHandlers.config.AppConfig

class InputSeparatorHandler extends ParameterHandler {
  override def handle(args: List[String], config: AppConfig): (List[String], AppConfig) = {
    args match {
      case "--input-separator" :: sep :: tail =>
        (tail, config.copy(inputSeparator = sep))
      case _ => (args, config)
    }
  }

  override def helpMessage: String =
    "--input-separator [STRING] : the separator for input (optional, defaults to \",\")"
}
