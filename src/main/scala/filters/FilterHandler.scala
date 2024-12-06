package filters

import traits.CliHandler

import scala.collection.mutable

class FilterHandler extends CliHandler {
  override def handle(args: mutable.Map[String, String], input: List[String]): Unit = {
    val filters = input.zipWithIndex.collect {
      case ("--filter", index) if index + 3 < input.length =>
        val column = input(index + 1)
        val operator = input(index + 2)
        val value = input(index + 3).toDouble
        ColumnFilter(column, operator, value)
    }

    args("filters") = filters.mkString(",") // Serialize filters into args map
    super.handle(args, input)
  }
}
