package Sun;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.*;
import java.nio.file.Files;
import java.nio.file.Paths;

import java.util.*;
//Lox is a scripting language,
//  which means it executes directly from source. 
// Our interpreter supports two ways of running code.
//  If you start jlox from the command line and give it a path to a file, it reads the file and executes it.

public class Lox {
  //static so that successive calls to run() inside a REPL session reuse the same interpreter
  private static final Interpreter interpreter = new Interpreter();

  static boolean hadError = false;
  static boolean hadRuntimeError = false;

  public static void main(String[] args) throws IOException {
    if (args.length > 1) {
      System.out.println("Usage : jlox [script]");
      System.exit(64);
    } else if (args.length == 1) {
      runFile(args[0]);
    } else {
      runPrompt();
    }
  }

  private static void runFile(String path) throws IOException {
    byte[] bytes = Files.readAllBytes(Paths.get(path));
    run(new String(bytes, Charset.defaultCharset()));
    // Indicate an error in the exit code.
    if (hadError)
      System.exit(65);
    if (hadRuntimeError)
      System.exit(70);

  }

  private static void runPrompt() throws IOException {
    InputStreamReader input = new InputStreamReader(System.in);
    BufferedReader reader = new BufferedReader(input);

    for (;;) {
      System.out.print("> ");
      String line = reader.readLine();
      if (line == null)
        break;
      run(line);
      hadError = false;

    }
  }
  // An interactive prompt is also called a “REPL”.
  // Fire up jlox without any arguments, and it drops you into a prompt
  // where you can enter and execute code one line at a time.

  private static void run(String source) {
    Scanner scanner = new Scanner(source);
    List<Token> tokens = scanner.scanTokens();

    Parser parser = new Parser(tokens);
    Expr expression = parser.parse();

    // Stop if there was a syntax error.
    if (hadError)
      return;

    interpreter.interpret(expression);
  }

  // Error handling. This error() function and its report() helper tells the user
  // some syntax error occurred on a given line.
  // Error: Unexpected "," somewhere in your code. Good luck finding it!

  static void error(int line, String message) {
    report(line, "", message);
  }

  private static void report(int line, String where, String message) {
    System.err.println(
        "[line " + line + "] Error" + where + ": " + message);
    hadError = true;
  }

  /*
   * This reports an error at a given token. It shows the token’s location and the
   * token itself.
   * This will come in handy later since we use tokens throughout the
   * interpreter to track locations in code.
   */
  static void error(Token token, String message) {
    if (token.type == TokenType.EOF) {
      report(token.line, " at end", message);
    } else {
      report(token.line, " at '" + token.lexeme + "'", message);
    }
  }

  static void runtimeError(RuntimeError error) {
    // I use the token associated with the RuntimeError to tell the user what line
    // of code was executing when the error occurred.
    System.err.println(error.getMessage() +
        "\n[line " + error.token.line + "]");
    hadRuntimeError = true;
  }

}
