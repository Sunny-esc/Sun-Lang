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
## Download

Prebuilt binaries are available on the Releases page:

https://github.com/Sunny-esc/Sun-Lang/releases

Download the appropriate executable for your system and run:

./sunlang file.sun
## Project Structure

```
Sun/
 ├── Lox.java            # Entry point (main class)
 ├── Interpreter.java    # Core execution engine
 ├── Parser.java         # Converts tokens to AST
 ├── Scanner.java        # Tokenizer (lexer)
 ├── Expr.java           # Expression definitions (AST)
 ├── Stmt.java           # Statement definitions (AST)
 ├── Environment.java    # Variable scope handling
 ├── Resolver.java       # Static resolution
 └── ...
```

## Getting Started

### Compile

```
javac Sun/*.java
```

### Run a file

```
java Sun.Lox program.sun
```

### REPL mode

```
java Sun.Lox
```

This starts an interactive shell where you can execute code line by line.

## Example

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

some test case are available in the Sun/test directory
```

## Native Executable (Optional)

You can build a native executable using GraalVM:

```
native-image -jar sunlang.jar
```

## Notes

* This project is an interpreter, not a compiler.
* Source files use a custom extension (e.g., `.sun`).
* Build artifacts such as `.class`, `.jar`, and binaries are not included in the repository.

## License

This project is for educational purposes.
