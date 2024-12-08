package outputOptions

trait OutputHandler {
  def write(content: String): Unit
}
