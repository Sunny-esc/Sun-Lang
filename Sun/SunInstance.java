package Sun;

import java.util.HashMap;
import java.util.Map;

class SunInstance {
  private SunClass klass;
  private final Map<String, Object> fields = new HashMap<>();

  SunInstance(SunClass klass) {
    this.klass = klass;
  }

  //edge case we need to handle is what happens if the instance doesn’t have a property with the given name
    Object get(Token name) {
    if (fields.containsKey(name.lexeme)) {
      return fields.get(name.lexeme);
    }
        SunFunction method = klass.findMethod(name.lexeme);
   // if (method != null) return method; //update on class by adding this cmd on 11/04/26
    if (method != null) return method.bind(this);

    throw new RuntimeError(name, 
        "Undefined property '" + name.lexeme + "'.");
  }

  void set(Token name, Object value) {
    fields.put(name.lexeme, value);
  }


  @Override
  public String toString() {
    return klass.name + " instance";
  }
}