package handlers.output

import traits.CliHandler

import scala.collection.mutable

// Concrete handler for the --range argument
class RangeHandler extends CliHandler {
  override def handle(args: mutable.Map[String, String], input: List[String]): Unit = {
    val index = input.indexOf("--range")
    if (index != -1 && index + 2 < input.length) {
      args("range-from") = input(index + 1)
      args("range-to") = input(index + 2)
    } else if (index != -1) {
      throw new IllegalArgumentException("Missing values for --range [FROM] [TO]")
    }
    super.handle(args, input)
  }
}
