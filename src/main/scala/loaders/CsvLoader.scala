package loaders

import traits.Loader

import scala.io.Source
import scala.util.Using

class CsvLoader extends Loader {
  override def load(source: String, separator: String = ","): List[List[String]] = {
    // Open the file, read lines, and split by separator
    Using(Source.fromFile(source)) { bufferedSource =>
      bufferedSource.getLines()
        .map(_.split(separator).toList) // Split each line by the separator
        .toList
    }.getOrElse {
      throw new RuntimeException(s"Failed to load or parse the file: $source")
    }
  }
}
