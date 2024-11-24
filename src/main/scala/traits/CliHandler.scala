package traits

import scala.collection.mutable

trait CliHandler {
  protected var next: Option[CliHandler] = None

  // Set the next handler in the chain
  def setNext(handler: CliHandler): CliHandler = {
    next = Some(handler)
    handler
  }

  // Handle the input or pass it to the next handler
  def handle(args: mutable.Map[String, String], input: List[String]): Unit = {
    if (next.isDefined) next.get.handle(args, input)
  }
}
