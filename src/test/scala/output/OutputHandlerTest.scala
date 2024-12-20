package output

import org.scalatest.funsuite.AnyFunSuite
import output.outputHandlerRegistries.DefaultOutputHandlerRegistry
import output.outputHandlers.{FileOutputHandler, StdoutOutputHandler}
import java.nio.file.Files

class OutputHandlerTest extends AnyFunSuite {

  test("FileOutputHandler writes content to file") {
    val tempFile = Files.createTempFile("test_output_handler", ".txt")
    val filePath = tempFile.toAbsolutePath.toString
    val handler = new FileOutputHandler(filePath)

    val testContent = "Hello, file!"
    handler.write(testContent)

    // Read back the file content
    val readContent = new String(Files.readAllBytes(tempFile), "UTF-8")
    assert(readContent == testContent, s"Expected '$testContent', got '$readContent'")

    // Cleanup (optional, depending on your environment)
    Files.deleteIfExists(tempFile)
  }

  test("FileOutputHandler overwrites existing file") {
    val tempFile = Files.createTempFile("test_output_handler_overwrite", ".txt")
    val filePath = tempFile.toAbsolutePath.toString
    Files.write(tempFile, "Old content".getBytes("UTF-8"))

    val handler = new FileOutputHandler(filePath)
    val newContent = "New content"
    handler.write(newContent)

    val readContent = new String(Files.readAllBytes(tempFile), "UTF-8")
    assert(readContent == newContent, s"Expected '$newContent' after overwrite, got '$readContent'")

    // Cleanup
    Files.deleteIfExists(tempFile)
  }

  test("DefaultOutputHandlerRegistry returns FileOutputHandler if 'output-file' is specified") {
    val handler = DefaultOutputHandlerRegistry.getOutputHandler(Map("output-file" -> "/tmp/dummy.txt"))
    assert(handler.isInstanceOf[FileOutputHandler], s"Expected FileOutputHandler, got ${handler.getClass}")
  }

  test("DefaultOutputHandlerRegistry returns StdoutOutputHandler if no 'output-file' is specified") {
    val handler = DefaultOutputHandlerRegistry.getOutputHandler(Map.empty)
    assert(handler.isInstanceOf[StdoutOutputHandler], s"Expected StdoutOutputHandler, got ${handler.getClass}")
  }

  test("DefaultOutputHandlerRegistry throws error for unregistered output type") {
    // The current registry chooses between 'file' and 'stdout' automatically based on 'output-file'.
    // To force an unknown type scenario, we can temporarily alter the registry if it's allowed,
    // or rely on the given logic: As is, it never chooses an unknown type because it defaults to "stdout" if no file.
    // Let's simulate a scenario with a different approach:
    // Since the registry code chooses the outputType internally, let's cause an unexpected scenario:

    // Create a test registry that doesn't register 'stdout' or 'file'
    val testRegistry = new OutputHandlerRegistry {
      // no registration
    }

    // With no "output-file", it tries "stdout" by default
    // This should fail because we didn't register anything.
    try {
      testRegistry.getOutputHandler(Map.empty)
      assert(false, "Expected exception for unsupported output type")
    } catch {
      case e: IllegalArgumentException =>
        assert(e.getMessage.contains("No output handler registered"), s"Expected error message about no handler, got ${e.getMessage}")
      case other: Throwable =>
        assert(false, s"Expected IllegalArgumentException, got ${other.getClass}")
    }
  }

}
