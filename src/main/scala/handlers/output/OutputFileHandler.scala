package handlers.output

import traits.CliHandler

import scala.collection.mutable

// Concrete handler for the --output-file argument
class OutputFileHandler extends CliHandler {
  override def handle(args: mutable.Map[String, String], input: List[String]): Unit = {
    val index = input.indexOf("--output-file")
    if (index != -1 && index + 1 < input.length) {
      args("output-file") = input(index + 1)
    }
    super.handle(args, input)
  }
}
