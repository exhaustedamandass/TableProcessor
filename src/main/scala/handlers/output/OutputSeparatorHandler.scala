package handlers.output

import traits.CliHandler

import scala.collection.mutable

// Concrete handler for the --output-separator argument
class OutputSeparatorHandler extends CliHandler {
  override def handle(args: mutable.Map[String, String], input: List[String]): Unit = {
    val index = input.indexOf("--output-separator")
    if (index != -1 && index + 1 < input.length) {
      args("output-separator") = input(index + 1)
    } else {
      args.getOrElseUpdate("output-separator", ",") // Default separator
    }
    super.handle(args, input)
  }
}
