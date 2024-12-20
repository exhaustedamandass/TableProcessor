package cliHandlers.filterHandlers

import cliHandlers.ParameterHandler
import cliHandlers.config.AppConfig
import filters.IsEmptyFilter

class IsEmptyFilterHandler extends ParameterHandler {
  override def handle(args: List[String], config: AppConfig): (List[String], AppConfig) = {
    args match {
      case "--filter-is-empty" :: column :: tail =>
        val filter = IsEmptyFilter(column)
        (tail, config.copy(filters = config.filters :+ filter))
      case _ => (args, config)
    }
  }

  override def helpMessage: String =
    "--filter-is-empty [COLUMN] : filter out lines with non-empty cells on the specified column"
}
