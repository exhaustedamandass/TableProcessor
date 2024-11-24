package handlers.filters

import traits.CliHandler

import scala.collection.mutable

// Concrete handler for the --filter-is-empty argument
class FilterIsEmptyHandler extends CliHandler {
  override def handle(args: mutable.Map[String, List[Map[String, String]]], input: List[String]): Unit = {
    val filters = input.sliding(2).collect {
      case List("--filter-is-empty", column) =>
        Map("column" -> column, "operator" -> "is-empty")
    }.toList

    if (filters.nonEmpty) {
      args.update("filters", args.getOrElse("filters", List()) ++ filters)
    }
    super.handle(args, input)
  }
}
