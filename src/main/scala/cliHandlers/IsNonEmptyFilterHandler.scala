package cliHandlers

import filters.IsNonEmptyFilter

class IsNonEmptyFilterHandler extends ParameterHandler {
  override def handle(args: List[String], config: AppConfig): (List[String], AppConfig) = {
    args match {
      case "--filter-is-non-empty" :: column :: tail =>
        val filter = IsNonEmptyFilter(column)
        (tail, config.copy(filters = config.filters :+ filter))
      case _ => (args, config)
    }
  }

  override def helpMessage: String =
    "--filter-is-non-empty [COLUMN] : filter out lines with empty cells on the specified column"
}
