package Sun;

import static Sun.TokenType.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//The scanner works its way through the source code, adding tokens until it runs out of characters. Then it appends one final “end of file” token. That isn’t strictly needed, but it makes our parser a little cleaner.
class Scanner {
    private static final Map<String, TokenType> keywords;

  static {
    keywords = new HashMap<>();
    keywords.put("and",    AND);
    keywords.put("class",  CLASS);
    keywords.put("else",   ELSE);
    keywords.put("false",  FALSE);
    keywords.put("for",    FOR);
    keywords.put("fun",    FUN);
    keywords.put("if",     IF);
    keywords.put("nil",    NIL);
    keywords.put("or",     OR);
    keywords.put("print",  PRINT);
    keywords.put("return", RETURN);
    keywords.put("super",  SUPER);
    keywords.put("this",   THIS);
    keywords.put("true",   TRUE);
    keywords.put("var",    VAR);
    keywords.put("while",  WHILE);
  }
  private final String source;
  private final List<Token> tokens = new ArrayList<>();
  // The start and current fields are offsets that index into the string. The
  // start field points to the first character in the lexeme being scanned, and
  // current points at the character currently being considered. The line field
  // tracks what source line current is on so we can produce tokens that know
  // their location.
  private int start = 0;
  private int current = 0;
  private int line = 1;

  Scanner(String source) {
    this.source = source;
  }

  // We store the raw source code as a simple string, and we have a list ready to
  // fill with tokens we’re going to generate.
  List<Token> scanTokens() {
    while (!isAtEnd()) {
      // We are at the beginning of the next lexeme.
      start = current;
      scanToken();
    }

    tokens.add(new Token(EOF, "", null, line));
    return tokens;
  }

  // In each turn of the loop, we scan a single token. This is the real heart of
  // the scanner. We’ll start simple. Imagine if every lexeme were only a single
  // character long. All you would need to do is consume the next character and
  // pick a token type for it. Several lexemes are only a single character in Lox,
  // so let’s start with those.
  private void scanToken() {
    char c = advance();
    switch (c) {
      case '(':
        addToken(LEFT_PAREN);
        break;
      case ')':
        addToken(RIGHT_PAREN);
        break;
      case '{':
        addToken(LEFT_BRACE);
        break;
      case '}':
        addToken(RIGHT_BRACE);
        break;
      case ',':
        addToken(COMMA);
        break;
      case '.':
        addToken(DOT);
        break;
      case '-':
        addToken(MINUS);
        break;
      case '+':
        addToken(PLUS);
        break;
      case ';':
        addToken(SEMICOLON);
        break;
      case '*':
        addToken(STAR);
        break;
      case '!':
        addToken(match('=') ? BANG_EQUAL : BANG);
        break;
      case '=':
        addToken(match('=') ? EQUAL_EQUAL : EQUAL);
        break;
      case '<':
        addToken(match('=') ? LESS_EQUAL : LESS);
        break;
      case '>':
        addToken(match('=') ? GREATER_EQUAL : GREATER);
        break;
      // Longer Lexemes
      case '/':
        if (match('/')) {
          // A comment goes until the end of the line.
          while (peek() != '\n' && !isAtEnd())
            advance();
        } else {
          addToken(SLASH);
        }
        break;
        case ' ':
      case '\r':
      case '\t':
        // Ignore whitespace.
        break;

      case '\n':
        line++;
        break;

      //String literals
      case '"': string(); break;
      case 'o':
  if (match('r')) {
    addToken(OR);
  }
  break;

      //Number literals


      default:
         if (isDigit(c)) {
          number();
        }  else if (isAlpha(c)) {
          identifier();}
          else {
          Lox.error(line, "Unexpected character.");
        }

    }
  }


    private void string() {
    while (peek() != '"' && !isAtEnd()) {
      if (peek() == '\n') line++;
      advance();
    }

    if (isAtEnd()) {
      Lox.error(line, "Unterminated string.");
      return;
    }

    // The closing ".
    advance();

    // Trim the surrounding quotes.
    String value = source.substring(start + 1, current - 1);
    addToken(STRING, value);
  }
  // Using match(), we recognize these lexemes in two stages.
  // When we reach, for example, !, we jump to its switch case. That means we know
  // the lexeme starts with !
  // . Then we look at the next character to determine if we’re on a != or merely
  // a !.
  private boolean match(char expected) {
    if (isAtEnd())
      return false;
    if (source.charAt(current) != expected)
      return false;

    current++;
    return true;
  }

//It’s sort of like advance(), but doesn’t consume the character.
//  This is called lookahead. Since it only looks at the current unconsumed character, 
// we have one character of lookahead. The smaller this number is, generally, the faster the scanner runs.
    private char peek() {
    if (isAtEnd()) return '\0';
    return source.charAt(current);
  }

 private char peekNext() {
    if (current + 1 >= source.length()) return '\0';
    return source.charAt(current + 1);
  } 


   private void identifier() {
    while (isAlphaNumeric(peek())) advance();
   String text = source.substring(start, current);
    TokenType type = keywords.get(text);
    if (type == null) type = IDENTIFIER;
    addToken(type);
    addToken(IDENTIFIER);
  }
//We define that in terms of these helpers:


  private boolean isAlpha(char c) {
    return (c >= 'a' && c <= 'z') ||
           (c >= 'A' && c <= 'Z') ||
            c == '_';
  }

  private boolean isAlphaNumeric(char c) {
    return isAlpha(c) || isDigit(c);
  }
  //Number literals
  private boolean isDigit(char c) {
    return c >= '0' && c <= '9';
  } 

    private void number() {
    while (isDigit(peek())) advance();

    // Look for a fractional part.
    if (peek() == '.' && isDigit(peekNext())) {
      // Consume the "."
      advance();

      while (isDigit(peek())) advance();
    }

    addToken(NUMBER,
        Double.parseDouble(source.substring(start, current)));
  }
  // Then we have one little helper function that tells us if we’ve consumed all
  // the characters.
  private boolean isAtEnd() {
    return current >= source.length();
  }

  // The advance() method consumes the next character in the source file and
  // returns it.
  // Where advance() is for input, addToken() is for output. It grabs the text of
  // the current lexeme and creates a new token for it.
  // We’ll use the other overload to handle tokens with literal values soon
  private char advance() {
    return source.charAt(current++);
  }

  private void addToken(TokenType type) {
    addToken(type, null);
  }

  private void addToken(TokenType type, Object literal) {
    String text = source.substring(start, current);
    tokens.add(new Token(type, text, literal, line));
  }
}
