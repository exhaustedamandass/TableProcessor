package output

trait OutputHandler {
  def write(content: String): Unit
}
