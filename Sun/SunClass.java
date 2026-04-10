package Sun;


import java.util.List;
import java.util.Map;

class SunClass implements LoxCallable {
  final String name;

   private final Map<String, SunFunction> methods;

  SunClass(String name, Map<String, SunFunction> methods) {
    this.name = name;
    this.methods = methods;
  }

SunFunction findMethod(String name) {
    if (methods.containsKey(name)) {
      return methods.get(name);
    }

    return null;
  }
  @Override
  public String toString() {
    return name;
  }


  //“call” a class, it instantiates a new LoxInstance for the called class and returns it.
    @Override
  public Object call(Interpreter interpreter,
                     List<Object> arguments) {
    //ref to loxinstance as suninstance
    SunInstance instance = new SunInstance(this);
    return instance;
  }


  //validates that you passed the right number of arguments to a callable.
  @Override
  public int arity() {
    // For now, we’ll say you can’t pass any.
    return 0;
  }
}