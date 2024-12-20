import cliHandlers.filterHandlers.{FilterHandler, IsEmptyFilterHandler, IsNonEmptyFilterHandler}
import cliHandlers.headerHandlers.HeadersHandler
import cliHandlers.helpHandlers.HelpHandler
import cliHandlers.inputHandlers.{InputFileHandler, InputSeparatorHandler}
import cliHandlers.outputHandlers.{OutputFileHandler, OutputFormatHandler, OutputSeparatorHandler, StdoutHandler}
import cliHandlers.parameterChains.ParameterChain
import cliHandlers.ParameterHandler
import cliHandlers.rangeHandlers.RangeHandler
import output.outputHandlerRegistries.DefaultOutputHandlerRegistry
import tableProcessor.TableProcessor

object TableProcessorApp {
  def main(args: Array[String]): Unit = {
    val handlers: List[ParameterHandler] = List(
      new HelpHandler(),
      new InputFileHandler(),
      new InputSeparatorHandler(),
      new FilterHandler(),
      new IsNonEmptyFilterHandler(),
      new IsEmptyFilterHandler(),
      new RangeHandler(),
      new HeadersHandler(),
      new OutputFormatHandler(),
      new OutputSeparatorHandler(),
      new OutputFileHandler(),
      new StdoutHandler()
    )

    val chain = new ParameterChain(handlers)
    val config = chain.parse(args.toList)

    if (config.showHelp || config.inputFile.isEmpty) {
      println("Usage:")
      println(chain.help)
      return
    }

    // The heavy-lifting logic is now handled by the tableProcessor.TableProcessor class.
    val processor = new TableProcessor(config, chain)
    val content = processor.process()

    val outputHandler = DefaultOutputHandlerRegistry.getOutputHandler(
      Map(
        "stdout" -> config.stdout.toString,
        "output-file" -> config.outputFile.getOrElse("")
      )
    )
    outputHandler.write(content)
  }
}





