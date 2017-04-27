/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trainme;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import org.python.core.PyInstance;
import org.python.core.PyObject;
import org.python.util.PythonInterpreter;

/**
 *
 * @author Sajeewa
 */
public class PythonConnection {

    PythonInterpreter interpreter = null;
    PythonConnection test = null;
    PyInstance target = null;

    public PythonConnection() {
        PythonInterpreter.initialize(System.getProperties(),
                System.getProperties(), new String[0]);

        this.interpreter = new PythonInterpreter();
    }

    void execfile(final String fileName) {
        this.interpreter.execfile(fileName);
    }

    PyInstance createClass(final String className, final String opts) {
        return (PyInstance) this.interpreter.eval(className + "(" + opts + ")");
    }

    public void callPython() {
        PythonConnection test = new PythonConnection();

        test.execfile("SensorApp.py");//give the python file name here
        
        PyInstance hello = test.createClass("readTarget", "None"); 

        PyObject s = hello.invoke("run");
        System.out.println("test");
    }

    public void connectWalabot() {
        PyObject s = target.invoke("SensorApp");
        System.out.println("test");
    }

    public String getCoordinates() {
        //PyObject s = target.invoke("coordinates"); //change this into the python function name
        
        String s = "";
        BufferedReader br;
        try{
            br = new BufferedReader(new InputStreamReader(new FileInputStream("output.txt")));
            s = br.readLine().trim();
            br.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        
        return s;
    }
    
    public void disconnectWalabot(){
        target.invoke("disconnect");
    }
}
