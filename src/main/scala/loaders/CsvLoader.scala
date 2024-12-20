package loaders

import scala.io.Source
import scala.util.Using

class CsvLoader extends Loader {
  override def load(source: String, separator: String = ","): List[List[String]] = {
    Using(Source.fromFile(source)) { bufferedSource =>
      bufferedSource.getLines()
        .map { line =>
          line.split(separator).map(_.trim).toList
        }
        .toList
    }.getOrElse {
      throw new RuntimeException(s"Failed to load or parse the file: $source")
    }
  }
}
