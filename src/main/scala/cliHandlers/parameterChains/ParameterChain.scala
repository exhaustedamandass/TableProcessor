package cliHandlers.parameterChains

import cliHandlers.ParameterHandler
import cliHandlers.config.AppConfig

class ParameterChain(handlers: List[ParameterHandler]) {
  def parse(args: List[String]): AppConfig = {
    var currentArgs = args
    var currentConfig = AppConfig()
    var changed = true

    // Keep trying to parse until no handler consumes any arguments
    while (changed && currentArgs.nonEmpty) {
      changed = false
      for (handler <- handlers if currentArgs.nonEmpty) {
        val (newArgs, newConfig) = handler.handle(currentArgs, currentConfig)
        if (newArgs != currentArgs || newConfig != currentConfig) {
          // Handler consumed something
          currentArgs = newArgs
          currentConfig = newConfig
          changed = true
          // Start again from the first handler for the updated arguments
          // to allow any handler to process newly exposed arguments
        }
      }
    }

    // If there are still unrecognized arguments, show help
    if (currentArgs.nonEmpty && !currentConfig.showHelp) {
      // We have leftover arguments that no handler recognized
      currentConfig = currentConfig.copy(showHelp = true)
    }

    currentConfig
  }

  def help: String = {
    handlers.map(_.helpMessage).mkString("\n")
  }
}
