import handlers.DefaultHandler
import handlers.input.{InputFileHandler, InputSeparatorHandler}

import scala.collection.mutable

object TableProcessorApp {
  def main(args: Array[String]): Unit = {
    if (args.isEmpty) {
      println(
        """
          |Usage:
          |  --input-file [FILE]        the input CSV file (required)
          |  --input-separator [STRING] the separator for input (optional, defaults to ",")
        """.stripMargin)
      sys.exit(1)
    }

    val argsMap = mutable.Map[String, String]()

    // Build the chain of responsibility
    val inputFileHandler = new InputFileHandler()
    val inputSeparatorHandler = new InputSeparatorHandler()
    val defaultHandler = new DefaultHandler()

    inputFileHandler
      .setNext(inputSeparatorHandler)
      .setNext(defaultHandler)
    
    try {
      // Start processing CLI arguments using the chain
      inputFileHandler.handle(argsMap, args.toList)

      // Ensure the required arguments are present
      if (!argsMap.contains("input-file")) {
        throw new IllegalArgumentException("The --input-file argument is required.")
      }

      println("Parsed CLI arguments:")
      argsMap.foreach { case (key, value) => println(s"$key -> $value") }

      // Perform application logic with parsed arguments
      println(s"Processing file: ${argsMap("input-file")}")
      println(s"Using separator: ${argsMap.getOrElse("input-separator", ",")}")

    } catch {
      case e: IllegalArgumentException =>
        println(s"Error: ${e.getMessage}")
        println(
          """
            |Usage:
            |  --input-file [FILE]        the input CSV file (required)
            |  --input-separator [STRING] the separator for input (optional, defaults to ",")
          """.stripMargin)
        sys.exit(1)
    }
  }
}
