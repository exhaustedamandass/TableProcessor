package output.outputHandlers

import output.OutputHandler

class StdoutOutputHandler extends OutputHandler {
  override def write(content: String): Unit = println(content)
}
