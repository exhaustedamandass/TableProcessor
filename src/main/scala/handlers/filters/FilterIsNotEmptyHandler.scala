package handlers.filters

import traits.CliHandler

import scala.collection.mutable

// Concrete handler for the --filter-is-not-empty argument
class FilterIsNotEmptyHandler extends CliHandler {
  override def handle(args: mutable.Map[String, List[Map[String, String]]], input: List[String]): Unit = {
    val filters = input.sliding(2).collect {
      case List("--filter-is-not-empty", column) =>
        Map("column" -> column, "operator" -> "is-not-empty")
    }.toList

    if (filters.nonEmpty) {
      args.update("filters", args.getOrElse("filters", List()) ++ filters)
    }
    super.handle(args, input)
  }
}
