package handlers.output

import traits.CliHandler

import scala.collection.mutable

// Concrete handler for the --stdout argument
class StdoutHandler extends CliHandler {
  override def handle(args: mutable.Map[String, String], input: List[String]): Unit = {
    if (input.contains("--stdout")) {
      args("stdout") = "true"
    } else {
      args.getOrElseUpdate("stdout", "true") // Default is true
    }
    super.handle(args, input)
  }
}