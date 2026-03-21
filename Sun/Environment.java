package Sun;

import java.util.HashMap;
import java.util.Map;

class Environment {
    final Environment enclosing;

  private final Map<String, Object> values = new HashMap<>();
  Environment() {
    enclosing = null;
  }

  Environment(Environment enclosing) {
    this.enclosing = enclosing;
  }

   Object get(Token name) {
    if (values.containsKey(name.lexeme)) {
      return values.get(name.lexeme);
    }
//If the variable isn’t found in this environment, we simply try the enclosing one. That in turn does the same thing recursively,
        if (enclosing != null) return enclosing.get(name);
//f we reach an environment with no enclosing one and still don’t find the variable, then we give up and report an error as before.



    throw new RuntimeError(name,
        "Undefined variable '" + name.lexeme + "'.");
  }
  void assign(Token name, Object value) {
    if (values.containsKey(name.lexeme)) {
      values.put(name.lexeme, value);
      return;
    }
   if (enclosing != null) {
      enclosing.assign(name, value);
      return;
    }
    throw new RuntimeError(name,
        "Undefined variable '" + name.lexeme + "'.");
  }


   void define(String name, Object value) {
    values.put(name, value);
  }
}