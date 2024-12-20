package output.outputHandlerRegistries

import output.outputHandlers.{FileOutputHandler, StdoutOutputHandler}
import output.OutputHandlerRegistry

object DefaultOutputHandlerRegistry extends OutputHandlerRegistry {
  // For file output, we expect "output-file" to be present
  register("file", argsMap => new FileOutputHandler(argsMap("output-file")))

  // For stdout, we don't need any additional arguments
  register("stdout", _ => new StdoutOutputHandler)
}

