package cliHandlers

class RangeHandler extends ParameterHandler {
  override def handle(args: List[String], config: AppConfig): (List[String], AppConfig) = {
    args match {
      case "--range" :: from :: to :: tail =>
        (tail, config.copy(range = Some((from, to))))
      case _ => (args, config)
    }
  }

  override def helpMessage: String =
    "--range [FROM] [TO] : specify a range of the table to output"
}
