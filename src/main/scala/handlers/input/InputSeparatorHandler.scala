package handlers.input

import traits.CliHandler

import scala.collection.mutable

// Concrete handler for the input separator
class InputSeparatorHandler extends CliHandler {
  override def handle(args: mutable.Map[String, String], input: List[String]): Unit = {
    val index = input.indexOf("--input-separator")
    if (index != -1 && index + 1 < input.length) {
      args("input-separator") = input(index + 1)
    } else if (index != -1) {
      throw new IllegalArgumentException("Missing value for --input-separator")
    } else {
      // Set default separator if not provided
      args.getOrElseUpdate("input-separator", ",")
    }
    super.handle(args, input)
  }
}
