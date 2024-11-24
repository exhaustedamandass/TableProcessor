package handlers.output

import traits.CliHandler

import scala.collection.mutable

class HeadersHandler extends CliHandler{
  override def handle(args: mutable.Map[String, String], input: List[String]): Unit = {
    if (input.contains("--headers")) {
      args("headers") = "true"
    } else {
      args.getOrElseUpdate("headers", "false") // Default is false
    }
    super.handle(args, input)
  }
}
