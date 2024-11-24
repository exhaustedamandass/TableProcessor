package traits

trait Loader {
  /**
   * Loads and parses data into a table format.
   *
   * @param source The data source (e.g., file path, URL, etc.).
   * @param separator The column separator (default is comma).
   * @return A 2D table as List[List[String]].
   */
  def load(source: String, separator: String = ","): List[List[String]]
}
