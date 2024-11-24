package handlers

import traits.CliHandler

import scala.collection.mutable

// Default handler for unrecognized arguments
class DefaultHandler extends CliHandler {
  override def handle(args: mutable.Map[String, String], input: List[String]): Unit = {
    val unrecognized = input.filterNot(arg => arg.startsWith("--") || args.values.toSet.contains(arg))
    if (unrecognized.nonEmpty) {
      throw new IllegalArgumentException(s"Unrecognized arguments: ${unrecognized.mkString(" ")}")
    }
    super.handle(args, input)
  }
}
