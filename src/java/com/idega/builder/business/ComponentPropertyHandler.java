package com.idega.builder.business;

/**
 * Title:        idegaclasses
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href="tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */

import java.lang.reflect.Method;

import java.util.List;
import java.util.Iterator;
import java.util.Enumeration;
import java.util.Vector;
import java.util.Map;
import java.util.Hashtable;

import org.jdom.Element;
import org.jdom.Attribute;

import com.idega.jmodule.object.ModuleObject;
import com.idega.jmodule.object.interfaceobject.*;

public class ComponentPropertyHandler {

  private static ComponentPropertyHandler instance;

  private static final String XML_TYPE_TAG = "type";
  private static final String XML_VALUE_TAG = "value";


  private ComponentPropertyHandler() {
  }

  public static ComponentPropertyHandler getInstance(){
    if(instance==null){
      instance = new ComponentPropertyHandler();
    }
    return instance;
  }

     void setReflectionProperty(ModuleObject instance,String methodIdentifier,Element values){
      Method m = com.idega.util.reflect.MethodFinder.getInstance().getMethod(methodIdentifier);
      setReflectionProperty(instance,m,values);
    }

     void setReflectionProperty(ModuleObject instance,Method method,Element values){
        Object[] args = getObjectArguments(values);
        //method.invoke(instance,args);
    }

    private  Object[] getObjectArguments(Element value){
      List children = value.getChildren();
      Object[] theReturn;
      if(children!=null){
        theReturn = new Object[children.size()];
        Iterator iter = children.iterator();
        int counter = 0;
        while (iter.hasNext()) {
          Element item = (Element)iter.next();
          theReturn[counter]=handleElementProperty(item);
          counter++;
        }
      }
      else{
        theReturn = new Object[0];
      }
      return theReturn;
    }

    public Object handleElementProperty(Element el){
      Element typeEl = el.getChild(XML_TYPE_TAG);
      Element valueEl = el.getChild(XML_VALUE_TAG);
      String className = typeEl.getText();
      String valueString = valueEl.getText();
      if(className.equals("int")){
        return new Integer(valueString);
      }
      else if(className.equals("java.lang.String")){
        return valueString;
      }
      else if(className.equals("boolean")){
        return new Boolean(valueString);
      }
      return null;
    }


    public ModuleObject getSetPropertyComponent(String className,String name){
      if(className.equals("int")){
        return new IntegerInput(name);
      }
      else if(className.equals("java.lang.String")){
        return new TextInput(name);
      }
      else if(className.equals("boolean")){
        return new BooleanInput(name);
      }
      return null;
    }


}