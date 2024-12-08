package outputOptions

class FileOutputHandler(filePath: String) extends OutputHandler {
  override def write(content: String): Unit = {
    import java.nio.file.{Files, Paths, StandardOpenOption}
    Files.write(Paths.get(filePath), content.getBytes("UTF-8"), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)
  }
}
