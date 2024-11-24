package handlers.output

import traits.CliHandler

import scala.collection.mutable

// Concrete handler for the --output-format argument
class OutputFormatHandler extends CliHandler {
  override def handle(args: mutable.Map[String, String], input: List[String]): Unit = {
    val index = input.indexOf("--output-format")
    if (index != -1 && index + 1 < input.length) {
      val format = input(index + 1)
      if (format != "csv" && format != "md") {
        throw new IllegalArgumentException("Invalid value for --output-format (must be 'csv' or 'md')")
      }
      args("output-format") = format
    } else {
      args.getOrElseUpdate("output-format", "csv") // Default format is csv
    }
    super.handle(args, input)
  }
}
