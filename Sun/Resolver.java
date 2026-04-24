package Sun;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

class Resolver implements Expr.Visitor<Void>, Stmt.Visitor<Void> {
  private final Interpreter interpreter;

  // This field keeps track of the stack of scopes currently, uh, in scope. Each
  // element in the stack is a Map representing a single block scope. Keys, as in
  // Environment, are variable names.
  private final Stack<Map<String, Boolean>> scopes = new Stack<>();
  // Invalid return errors
  private FunctionType currentFunction = FunctionType.NONE;

  Resolver(Interpreter interpreter) {
    this.interpreter = interpreter;
  }

  private enum FunctionType {
    NONE,
    FUNCTION,
    INITIALIZER,
    METHOD
  }

  private enum ClassType {
    NONE,
    CLASS
  }

  private ClassType currentClass = ClassType.NONE;

  @Override
  public Void visitBlockStmt(Stmt.Block stmt) {
    // This begins a new scope, traverses into the statements inside the block, and
    // then discards the scope.
    beginScope();
    resolve(stmt.statements);
    endScope();
    return null;
  }

  // a class should have a closing brace at the end,
  // but it ensures the parser doesn’t get stuck in an infinite loop if the user
  // has a syntax error and forgets to correctly end the class body.
  @Override
  public Void visitClassStmt(Stmt.Class stmt) {
    // it's nothing but the declaration that it iniated
    ClassType enclosingClass = currentClass;
    currentClass = ClassType.CLASS;

    declare(stmt.name);
    define(stmt.name);

    //handle empty and uncessary class inheritance edge case reslover mentioned in inheritance line problem
    if(stmt.superclass != null && stmt.name.lexeme.equals(stmt.superclass.name.lexeme) ){
      Lox.error(stmt.superclass.name,"A class can't inherit from itself");
      
    }
    //inheritance resolver
    if(stmt.superclass != null ){
      resolve(stmt.superclass);
    }

    // it will make the this keywrd in scope
    beginScope();
    scopes.peek().put("this", true);

    // We iterate through the methods in the class body and call the
    // resolveFunction() method
    for (Stmt.Function method : stmt.methods) {
      FunctionType declaration = FunctionType.METHOD;
       if (method.name.lexeme.equals("init")) {
        declaration = FunctionType.INITIALIZER;
      }
      resolveFunction(method, declaration);
    }

    endScope();
    currentClass = enclosingClass;

    return null;
  }

  // An expression statement contains a single expression to traverse.
  @Override
  public Void visitExpressionStmt(Stmt.Expression stmt) {
    resolve(stmt.expression);
    return null;
  }

  // Resolving function declarations
  @Override
  public Void visitFunctionStmt(Stmt.Function stmt) {
    declare(stmt.name);
    define(stmt.name);

    resolveFunction(stmt, FunctionType.FUNCTION);
    return null;
  }

  // An if statement has an expression for its condition and one or two statements
  // for the branches.
  @Override
  public Void visitIfStmt(Stmt.If stmt) {
    resolve(stmt.condition);
    resolve(stmt.thenBranch);
    if (stmt.elseBranch != null)
      resolve(stmt.elseBranch);
    return null;
  }

  // a print statement contains a single subexpression.
  @Override
  public Void visitPrintStmt(Stmt.Print stmt) {
    resolve(stmt.expression);
    return null;
  }

  // Same deal for return.
  @Override
  public Void visitReturnStmt(Stmt.Return stmt) {
    if (currentFunction == FunctionType.NONE) {
      Lox.error(stmt.keyword, "Can't return from top-level code.");
    }
    if (stmt.value != null) {
       if (currentFunction == FunctionType.INITIALIZER) {
        Lox.error(stmt.keyword,
            "Can't return a value from an initializer.");
      }
      resolve(stmt.value);
    }

    return null;
  }

  @Override
  public Void visitVarStmt(Stmt.Var stmt) {
    declare(stmt.name);
    if (stmt.initializer != null) {
      resolve(stmt.initializer);
    }
    define(stmt.name);
    return null;
  }

  // As in if statements, with a while statement, we resolve its condition and
  // resolve the body exactly once.
  @Override
  public Void visitWhileStmt(Stmt.While stmt) {
    resolve(stmt.condition);
    resolve(stmt.body);
    return null;
  }

  // Resolving assignment expressions
  @Override
  public Void visitAssignExpr(Expr.Assign expr) {
    resolve(expr.value);
    resolveLocal(expr, expr.name);
    return null;
  }

  // the binary expression. We traverse into and resolve both operands.
  @Override
  public Void visitBinaryExpr(Expr.Binary expr) {
    resolve(expr.left);
    resolve(expr.right);
    return null;
  }

  // Calls are similar—we walk the argument list and resolve them all. The thing
  // being called is also an expression (usually a variable expression), so that
  // gets resolved too.
  @Override
  public Void visitCallExpr(Expr.Call expr) {
    resolve(expr.callee);

    for (Expr argument : expr.arguments) {
      resolve(argument);
    }

    return null;
  }

  // get keyword for instance access
  @Override
  public Void visitGetExpr(Expr.Get expr) {
    resolve(expr.object);
    return null;
  }

  // Parentheses are easy.
  @Override
  public Void visitGroupingExpr(Expr.Grouping expr) {
    resolve(expr.expression);
    return null;
  }

  // Literals are easiest of all.
  // A literal expression doesn’t mention any variables and doesn’t contain any
  // subexpressions so there is no work to do.
  @Override
  public Void visitLiteralExpr(Expr.Literal expr) {
    return null;
  }

  // logical expressions are exactly the same as other binary operators.
  @Override
  public Void visitLogicalExpr(Expr.Logical expr) {
    resolve(expr.left);
    resolve(expr.right);
    return null;
  }

  // handles the parsed code for set opp
  @Override
  public Void visitSetExpr(Expr.Set expr) {
    resolve(expr.value);
    resolve(expr.object);
    return null;
  }

  // handle reslove for this keyword
  @Override
  public Void visitThisExpr(Expr.This expr) {
    //runtime reslove error 
    if (currentClass == ClassType.NONE) {
      Lox.error(expr.keyword,
          "Can't use 'this' outside of a class.");
      return null;
    }
    resolveLocal(expr, expr.keyword);
    return null;
  }

  // We resolve its one operand.
  @Override
  public Void visitUnaryExpr(Expr.Unary expr) {
    resolve(expr.right);
    return null;
  }

  @Override
  public Void visitVariableExpr(Expr.Variable expr) {
    if (!scopes.isEmpty() &&
        scopes.peek().get(expr.name.lexeme) == Boolean.FALSE) {
      Lox.error(expr.name,
          "Can't read local variable in its own initializer.");
    }

    resolveLocal(expr, expr.name);
    return null;
  }

  private void resolve(Stmt stmt) {
    stmt.accept(this);
  }

  private void resolve(Expr expr) {
    expr.accept(this);
  }

  void resolve(List<Stmt> statements) {
    // This walks a list of statements and resolves each one. It in turn calls:
    for (Stmt statement : statements) {
      resolve(statement);
    }
  }

  private void resolveFunction(
      Stmt.Function function, FunctionType type) {
    FunctionType enclosingFunction = currentFunction;
    currentFunction = type;
    beginScope();
    for (Token param : function.params) {
      declare(param);
      define(param);
    }
    resolve(function.body);
    endScope();
    // We store the previous value in a local on the Java stack. When we’re done
    // resolving the function body, we restore the field to that value
    currentFunction = enclosingFunction;

  }

  private void beginScope() {
    // Lexical scopes nest in both the interpreter and the resolver. They behave
    // like a stack. The interpreter implements that stack using a linked list—the
    // chain of Environment objects.
    // In the resolver, we use an actual Java Stack.
    scopes.push(new HashMap<String, Boolean>());
  }

  private void endScope() {
    scopes.pop();
  }

  private void declare(Token name) {
    // Declaration adds the variable to the innermost scope so that it shadows any
    // outer one and so that we know the variable exists
    if (scopes.isEmpty())
      return;

    Map<String, Boolean> scope = scopes.peek();
    // Resolution Errors
    if (scope.containsKey(name.lexeme)) {
      Lox.error(name,
          "Already a variable with this name in this scope.");
    }
    scope.put(name.lexeme, false);
  }

  private void define(Token name) {
    if (scopes.isEmpty())
      return;
    scopes.peek().put(name.lexeme, true);
  }

  private void resolveLocal(Expr expr, Token name) {
    /*
     * This looks, for good reason, a lot like the code in Environment for
     * evaluating a variable.
     * We start at the innermost scope and work outwards, looking in each map for a
     * matching name.
     * If we find the variable, we resolve it, passing in the number of scopes
     * between the current innermost scope and the scope where the variable was
     * found.
     * So, if the variable was found in the current scope,
     * we pass in 0. If it’s in the immediately enclosing scope, 1.
     */
    for (int i = scopes.size() - 1; i >= 0; i--) {
      if (scopes.get(i).containsKey(name.lexeme)) {
        interpreter.resolve(expr, scopes.size() - 1 - i);
        return;
      }
    }
  }

}