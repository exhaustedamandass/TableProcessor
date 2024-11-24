package handlers.filters

import traits.CliHandler

import scala.collection.mutable

// Concrete handler for the --filter argument
class FilterHandler extends CliHandler {
  override def handle(args: mutable.Map[String, List[Map[String, String]]], input: List[String]): Unit = {
    val filters = input.sliding(4).collect {
      case List("--filter", column, operator, number) if Set("<", ">", "<=", ">=", "==", "!=").contains(operator) =>
        Map("column" -> column, "operator" -> operator, "value" -> number)
    }.toList

    if (filters.nonEmpty) {
      args.update("filters", args.getOrElse("filters", List()) ++ filters)
    }
    super.handle(args, input)
  }
}

