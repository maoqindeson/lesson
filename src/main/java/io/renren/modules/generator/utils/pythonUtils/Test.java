package io.renren.modules.generator.utils.pythonUtils;
import org.python.core.PyFunction;
import org.python.core.PyInteger;
import org.python.core.PyObject;
import org.python.util.PythonInterpreter;

import java.util.Properties;

public class Test {
    public static void main(String[] args) {

        PythonInterpreter interpreter = new PythonInterpreter();
        String execCode = "print('Hello Java! I am Python ,Are you OK?')";
//        interpreter.exec(execCode);
//        PyFunction func = (PyFunction)interpreter.get("execPrint",PyFunction.class);
//        PyObject pyobj = func.__call__(new PyInteger(8));
//        System.out.println(pyobj);

        interpreter.exec(execCode);
        PyObject pyObject = interpreter.getLocals();
        System.out.println(pyObject.toString());
    }
}
