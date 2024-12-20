package cliHandlers.outputHandlers

import cliHandlers.ParameterHandler
import cliHandlers.config.AppConfig

class StdoutHandler extends ParameterHandler {
  override def handle(args: List[String], config: AppConfig): (List[String], AppConfig) = {
    args match {
      case "--stdout" :: tail =>
        (tail, config.copy(stdout = true, outputFile = None))
      case _ => (args, config)
    }
  }

  override def helpMessage: String =
    "--stdout : print the table to the standard output (optional, by default true)"
}
