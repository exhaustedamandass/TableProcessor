package handlers.input

import traits.CliHandler

import scala.collection.mutable

// Concrete handler for the input file
class InputFileHandler extends CliHandler{
  override def handle(args: mutable.Map[String, String], input: List[String]): Unit = {
    val index = input.indexOf("--input-file")
    if (index != -1 && index + 1 < input.length) {
      args("input-file") = input(index + 1)
    } else if (index != -1) {
      throw new IllegalArgumentException("Missing value for --input-file")
    }
    super.handle(args, input)
  }
}
