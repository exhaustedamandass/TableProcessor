package cliHandlers.inputHandlers

import cliHandlers.ParameterHandler
import cliHandlers.config.AppConfig

class InputFileHandler extends ParameterHandler {
  override def handle(args: List[String], config: AppConfig): (List[String], AppConfig) = {
    args match {
      case "--input-file" :: file :: tail =>
        (tail, config.copy(inputFile = Some(file)))
      case _ => (args, config)
    }
  }

  override def helpMessage: String =
    "--input-file [FILE] : the input CSV file (required)"
}
