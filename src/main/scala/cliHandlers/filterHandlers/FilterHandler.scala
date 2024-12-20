package cliHandlers.filterHandlers

import cliHandlers.ParameterHandler
import cliHandlers.config.AppConfig
import filters.ColumnFilter

class FilterHandler extends ParameterHandler {
  override def handle(args: List[String], config: AppConfig): (List[String], AppConfig) = {
    args match {
      case "--filter" :: column :: operator :: value :: tail =>
        val filter = ColumnFilter(column, operator, value.toDouble)
        (tail, config.copy(filters = config.filters :+ filter))
      case _ => (args, config)
    }
  }

  override def helpMessage: String =
    "--filter [COLUMN] [OPERATOR] [VALUE] : apply a value filter"
}
