> **Note:** Please ignore any comments in the code that refer to dev notes. The web interface is in development in the `dev` directory.

# Sun Language

Sun is a lightweight, dynamically typed, interpreted programming language built in Java.
It is based on an Abstract Syntax Tree (AST) interpreter and supports object-oriented programming features.

## Features

* Dynamically typed language
* Tree-walking AST interpreter
* REPL (interactive shell)
* Object-oriented programming support (classes, instances, functions)
* Lexical scoping with environments
* Custom error handling
* Inheritance and method overriding
* Static resolution for variable scoping

## Download

Prebuilt binaries are available on the Releases page:

https://github.com/Sunny-esc/Sun-Lang/releases

Download the appropriate executable for your system and run:

```
./sunlang file.sun
```

## Project Structure

```
Sun-Lang/
├── Sun/                    # Core language implementation
│   ├── Lox.java            # Entry point (main class)
│   ├── Interpreter.java    # Core execution engine
│   ├── Parser.java         # Converts tokens to AST
│   ├── Scanner.java        # Tokenizer (lexer)
│   ├── Expr.java           # Expression definitions (AST)
│   ├── Stmt.java           # Statement definitions (AST)
│   ├── Environment.java    # Variable scope handling
│   ├── Resolver.java       # Static resolution
│   ├── SunClass.java       # Class implementation
│   ├── SunFunction.java    # Function implementation
│   ├── SunInstance.java    # Object instance representation
│   ├── Token.java          # Token representation
│   ├── TokenType.java      # Token type definitions
│   ├── AstPrinter.java     # AST debugging utility
│   ├── Return.java         # Return statement handling
│   ├── RuntimeError.java   # Runtime error representation
│   ├── LoxCallable.java    # Callable interface
│   ├── test/               # Test cases and examples
│   └── logs/               # Log files
├── docs/                   # Documentation
├── tool/                   # Development tools
├── sunlang.jar            # Pre-built JAR archive
├── sunlang.exe            # Windows executable
└── sunlang1               # Linux/Unix executable
```

## Getting Started

### Compile

```
javac Sun/*.java
```

### Run a file

```
java Sun.Lox program.txt
```

### REPL mode

```
java Sun.Lox
```

This starts an interactive shell where you can execute code line by line.

## Language Syntax Examples

### Classes and Inheritance

```
class Animal {
  speak() {
    print "Some sound";
  }
}

class Dog < Animal {
  speak() {
    print "Bark";
  }
}

var d = Dog();
d.speak();
```

### Functions and Variables

```
fun fibonacci(n) {
  if (n <= 1) return n;
  return fibonacci(n - 1) + fibonacci(n - 2);
}

print fibonacci(10);
```

### Control Flow

```
for (var i = 0; i < 5; i = i + 1) {
  print i;
}

var x = 10;
if (x > 5) {
  print "x is greater than 5";
} else {
  print "x is not greater than 5";
}
```

## Test Cases

Test cases and examples are available in the `Sun/test` directory. You can use these to understand the language capabilities better.

## Native Executable (Optional)

You can build a native executable using GraalVM:

```
native-image -jar sunlang.jar
```

## Development

* The web interface is currently in development in the `dev` directory
* This project is an interpreter, not a compiler
* Build artifacts such as `.class`, `.jar`, and binaries are not included in the repository

## Language Specifications

* **Type System:** Dynamic typing with runtime type checking
* **Scope:** Lexical scoping with nested environments
* **Callable Objects:** Functions and classes with support for method overriding
* **File Extension:** `.txt`

## Notes

* This is a tree-walking interpreter implementation
* The interpreter executes code directly from the AST
* Comments in the code may contain dev notes that can be safely ignored
* The project serves as an educational example of language implementation

## License

This project is for educational purposes.