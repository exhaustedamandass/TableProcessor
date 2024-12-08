package cliHandlers

trait ParameterHandler {
  def handle(args: List[String], config: AppConfig): (List[String], AppConfig)

  def helpMessage: String
}
