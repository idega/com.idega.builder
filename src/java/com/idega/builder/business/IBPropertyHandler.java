
//idega 2001 - Tryggvi Larusson
/*
*Copyright 2001 idega.is All Rights Reserved.
*/
package com.idega.builder.business;

import com.idega.jmodule.object.*;
import com.idega.jmodule.object.interfaceobject.*;
import com.idega.jmodule.object.textObject.*;

/**
*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
*@version 0.5 alpha
*/

public class IBPropertyHandler{


    public static ModuleObject[] getInterfaceComponent(Class[] classes,String[] names){
      ModuleObject[] objects = new ModuleObject[classes.length];
      for (int i = 0; i < classes.length; i++) {
        if(names==null){
            objects[i]=getInterfaceComponent(classes[i],"parameter_nr_"+i);
        }
        else{
          objects[i]=getInterfaceComponent(classes[i],names[i]);
        }
      }
      return objects;
    }


      public static ModuleObject getInterfaceComponent(Class theClass,String name){
      ModuleObject obj =null;
      String className = theClass.getName();
      if(className.equals("java.lang.Integer") || className.equals("int")){
          obj = new IntegerInput(name);
      }
      else if(className.equals("java.lang.String") ){
          obj = new TextInput(name);
      }
      else if(className.equals("java.lang.Boolean") || className.equals("boolean")){
          obj = new BooleanInput(name);
      }
      else if(className.equals("java.lang.Float") || className.equals("float")){
          obj = new FloatInput(name);
      }
      else if(className.equals("java.lang.Double") || className.equals("double")){
          obj = new FloatInput(name);
      }
      else if(theClass.getName().equals("com.idega.jmodule.object.Image")){
          obj = new Link("Veldu mynd");
      }
      return obj;
    }


}
