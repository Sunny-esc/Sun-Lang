package Sun;


import java.util.List;
import java.util.Map;

class SunClass implements LoxCallable {
  final String name;
  final SunClass superclass;

   private final Map<String, SunFunction> methods;

  SunClass(String name,SunClass superClass, Map<String, SunFunction> methods) {
    this.name = name;
    this.superclass = superClass;
    this.methods = methods;
  }

SunFunction findMethod(String name) {
    if (methods.containsKey(name)) {
      return methods.get(name);
    }

     if (superclass != null) {
      return superclass.findMethod(name);
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
        //constructor creation
        SunFunction initializer = findMethod("init");
          if (initializer != null) {
           initializer.bind(instance).call(interpreter, arguments);
            }
    return instance;
  }


  //validates that you passed the right number of arguments to a callable.
  @Override
  public int arity() {
   SunFunction initializer = findMethod("init");
    if (initializer == null) return 0;
    return initializer.arity();
  }
}