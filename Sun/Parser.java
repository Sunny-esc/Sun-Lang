package Sun;

import java.util.List;

import static Sun.TokenType.*;

class Parser {
      private static class ParseError extends RuntimeException {}

    // the parser consumes a flat input sequence, only now we’re reading tokens
    // instead of characters.
    // We store the list of tokens and use current to point to the next token
    // eagerly waiting to be parsed
    private final List<Token> tokens;
    private int current = 0;

    Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    // the expression grammar now and translate each rule to Java code.
    // The first rule, expression, simply expands to the equality rule,
    private Expr expression() {
        return equality();
    }

    // body of the rule contains a nonterminal—a reference to another rule—we call
    // that other rule’s method
    // equality → comparison ( ( "!=" | "==" ) comparison )* ;
  private Expr equality() {
{/*
    This function parses expressions like:

a == b != c == d

and builds a syntax tree (AST).
1. Start with left side
Expr expr = comparison();

👉 First, it parses left operand

Example:

a == b == c

comparison() returns → a

So now:

expr = a
2. Enter the loop (if operator found)
while (match(BANG_EQUAL, EQUAL_EQUAL))

👉 If it sees == or !=, it enters loop

3. Take operator + right side
Token operator = previous();
Expr right = comparison();

Example:

a == b

operator → ==

right → b

4. Build tree node
expr = new Expr.Binary(expr, operator, right);

👉 Combine:

expr = (a == b)
5. Loop again (important!)

Now input:

a == b == c

Second iteration:

expr = (a == b)

operator = ==

right = c

Now:

expr = ((a == b) == c)


*/}


    Expr expr = comparison();

    while (match(BANG_EQUAL, EQUAL_EQUAL)) {
      Token operator = previous();
      Expr right = comparison();
      expr = new Expr.Binary(expr, operator, right);
    }
//The first comparison nonterminal in the body translates to the first call to comparison() in the method.
// We take that result and store it in a local variable.
//  comparison     → term ( ( ">" | ">=" | "<" | "<=" ) term )* ;

  private Expr comparison() {
    Expr expr = term();

    while (match(GREATER, GREATER_EQUAL, LESS, LESS_EQUAL)) {
      Token operator = previous();
      Expr right = term();
      expr = new Expr.Binary(expr, operator, right);
    }

    return expr;
  }
  private Expr term() {
    Expr expr = factor();

    while (match(MINUS, PLUS)) {
      Token operator = previous();
      Expr right = factor();
      expr = new Expr.Binary(expr, operator, right);
    }

    return expr;
  }
  private Expr factor() {
    Expr expr = unary();

    while (match(SLASH, STAR)) {
      Token operator = previous();
      Expr right = unary();
      expr = new Expr.Binary(expr, operator, right);
    }

    return expr;
  }

    private Expr unary() {
    if (match(BANG, MINUS)) {
      Token operator = previous();
      Expr right = unary();
      return new Expr.Unary(operator, right);
    }

    return primary();
  }

//primary        → NUMBER | STRING | "true" | "false" | "nil"
 //              | "(" expression ")" ;
   private Expr primary() {
    if (match(FALSE)) return new Expr.Literal(false);
    if (match(TRUE)) return new Expr.Literal(true);
    if (match(NIL)) return new Expr.Literal(null);

    if (match(NUMBER, STRING)) {
      return new Expr.Literal(previous().literal);
    }

    if (match(LEFT_PAREN)) {
      Expr expr = expression();
      consume(RIGHT_PAREN, "Expect ')' after expression.");
      return new Expr.Grouping(expr);
    }
        throw error(peek(), "Expect expression.");

  }


  private boolean match(TokenType... types) {
    // This checks to see if the current token has any of the given types. 
    // If so, it consumes the token and returns true. Otherwise, it returns false and leaves the current token alone. 
    // The match() method is defined in terms of two more fundamental operations.
    for (TokenType type : types) {
      if (check(type)) {
        advance();
        return true;
      }
    }

    return false;
  }
  ///After parsing the expression, the parser looks for the closing ) by calling consume().

  private Token consume(TokenType type, String message) {
    if (check(type)) return advance();

    throw error(peek(), message);
  }


  //The check() method returns true if the current token is of the given type. 
  // Unlike match(), it never consumes the token, it only looks at it.
  private boolean check(TokenType type) {
    if (isAtEnd()) return false;
    return peek().type == type;
  }

 // The advance() method consumes the current token and returns it, 
 // similar to how our scanner’s corresponding method crawled through characters.

  private Token advance() {
    if (!isAtEnd()) current++;
    return previous();
  }


//  These methods bottom out on the last handful of primitive operations.

  private boolean isAtEnd() {
    return peek().type == EOF;
  }

  private Token peek() {
    return tokens.get(current);
  }

  private Token previous() {
    return tokens.get(current - 1);
  }
    private ParseError error(Token token, String message) {
    Lox.error(token, message);
    return new ParseError();
  }

//   isAtEnd() checks if we’ve run out of tokens to parse.
//  peek() returns the current token we have yet to consume, and 
// previous() returns the most recently consumed token.
//    The latter makes it easier to use match() and then access the just-matched token.




{/*It discards tokens until it thinks it has found a statement boundary. 
After catching a ParseError, we’ll call this and then we are hopefully back in sync.
 When it works well, we have discarded tokens that would have likely caused cascaded errors anyway, 
and now we can parse the rest of the file starting at the next statement.*/}
  private void synchronize() {
    advance();

    while (!isAtEnd()) {
      if (previous().type == SEMICOLON) return;

      switch (peek().type) {
        case CLASS:
        case FUN:
        case VAR:
        case FOR:
        case IF:
        case WHILE:
        case PRINT:
        case RETURN:
          return;
      }

      advance();
    }
  }
    return expr;
  }



}
  Expr parse() {
    try {
      return expression();
    } catch (ParseError error) {
      return null;
    }
  }