package cliHandlers.outputHandlers

import cliHandlers.ParameterHandler
import cliHandlers.config.AppConfig

class OutputFileHandler extends ParameterHandler {
  override def handle(args: List[String], config: AppConfig): (List[String], AppConfig) = {
    args match {
      case "--output-file" :: f :: tail =>
        (tail, config.copy(outputFile = Some(f), stdout = false))
      case _ => (args, config)
    }
  }

  override def helpMessage: String =
    "--output-file [FILE] : the file to output the table to (optional)"
}