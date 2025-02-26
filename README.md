# TableProcessor

## Overview
TableProcessor is a Scala-based application for processing tabular data using object-oriented principles and the Chain of Responsibility pattern. The application provides robust handling for parsing, filtering, evaluating formulas, and formatting table data with a modular and extensible architecture.

## Features
- **Modular Table Processing**: Supports parsing, filtering, and evaluating table data with a clean separation of concerns.
- **Chain of Responsibility**: CLI handlers use the Chain of Responsibility pattern to process command-line parameters in a flexible and maintainable way.
- **Formula Evaluation**: Implements arithmetic operations and formula parsing for table data.
- **Filtering Mechanism**: Includes predefined column filters (e.g., empty/non-empty row filtering) with an extendable operator registry.
- **Configurable Output**: Supports multiple output formats such as CSV and Markdown.

## Project Structure
### Core Components
- **CLI Handlers (`cliHandlers/`)**: Handles command-line parameters and delegates responsibilities via the Chain of Responsibility pattern.
  - `filterHandlers/` (e.g., `IsEmptyFilterHandler.scala`)
  - `inputHandlers/` (e.g., `InputFileHandler.scala`)
  - `outputHandlers/` (e.g., `StdoutHandler.scala`)
  - `parameterChains/` (e.g., `ParameterChain.scala`)
- **Evaluation (`evaluation/`)**: Defines `Table`, `Cell`, and `FormulaEvaluator` for processing tabular data.
- **Filters (`filters/`)**: Implements various column filters and a registry for custom filtering operators.
- **Parsing (`parsing/`)**: Implements an abstract syntax tree (`AstNode.scala`) and a formula parser (`FormulaParser.scala`).
- **Operators (`operator/`)**: Defines arithmetic and filtering operators with a registry for extensibility.
- **Pretty Printing (`prettyPrinting/`)**: Handles formatted output, supporting different formats.
- **Loaders (`loaders/`)**: Supports loading CSV files into structured tables.
- **Table Processing (`tableProcessor/`)**: Centralized processing logic for handling tables.

## Object-Oriented Design
- **Encapsulation**: Each component is encapsulated in its respective package, promoting modularity.
- **Polymorphism**: Operators, filters, and handlers are designed using trait-based abstraction, allowing for easy extension.
- **Composition over Inheritance**: Registries and handlers compose behaviors dynamically rather than relying on deep inheritance trees.
- **Extensibility**: New handlers, filters, and output formats can be added with minimal changes to existing code.

## Getting Started

### Build and Run
```sh
sbt compile
sbt run
```

### Running Tests
```sh
sbt test
```
