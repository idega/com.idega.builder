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

import com.idega.presentation.PresentationObject;
import com.idega.presentation.ui.*;

import com.idega.core.data.ICFile;
import com.idega.builder.data.IBPage;
//import com.idega.builder.data.IBTemplatePage;
import com.idega.presentation.Image;

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

     void setReflectionProperty(PresentationObject instance,String methodIdentifier,Vector stringValues){
      Method method = com.idega.util.reflect.MethodFinder.getInstance().getMethod(methodIdentifier,instance.getClass());
      if(method==null){
        throw new RuntimeException("Method: "+methodIdentifier+" not found");
      }
      else{
        setReflectionProperty(instance,method,stringValues);
      }
    }

     void setReflectionProperty(PresentationObject instance,Method method,Vector stringValues){
        //Object[] args = getObjectArguments(stringValues);
        //method.invoke(instance,args);
        Object[] args = new Object[stringValues.size()];
        Class[] parameterTypes = method.getParameterTypes();
        for (int i = 0; i < parameterTypes.length; i++) {
          if(parameterTypes[i]!=null){
            args[i] = handleParameter(parameterTypes[i],(String)stringValues.get(i));
          }
        }
        try{
          method.invoke(instance,args);
        }
        catch(Exception e){
          e.printStackTrace();
        }
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

    static Object handleParameter(Class parameterType,String stringValue){
        Object argument=null;
        if(parameterType.equals(Integer.class) || parameterType.equals(Integer.TYPE)){
          try{
            argument = new Integer(stringValue);
          }
          catch(NumberFormatException e){
            e.printStackTrace(System.out);
          }
        }
        else if(parameterType.equals(String.class)){
            argument =  stringValue;
        }
        else if(parameterType.equals(Boolean.class) || parameterType.equals(Boolean.TYPE)){
          if(stringValue.equals("Y")){
            argument = Boolean.TRUE;
          }
          else if(stringValue.equals("N")){
            argument = Boolean.FALSE;
          }
          else{
            argument = new Boolean(stringValue);
          }
        }
        else if(parameterType.equals(Float.class) || parameterType.equals(Float.TYPE)){
          argument = new Float(stringValue);
        }
        else if(parameterType.equals(IBPage.class)){
          try {
            argument = new IBPage(Integer.parseInt(stringValue));
          }
          catch (Exception ex) {
            ex.printStackTrace(System.err);
          }
        }
        else if(parameterType.equals(ICFile.class)){
          try {
            argument = new ICFile(Integer.parseInt(stringValue));
          }
          catch (Exception ex) {
            ex.printStackTrace(System.err);
          }
        }/*
        else if(parameterType.equals(IBTemplatePage.class)){
          try {
            argument = new IBTemplatePage(Integer.parseInt(stringValue));
          }
          catch (Exception ex) {
            ex.printStackTrace(System.err);
          }
        }*/
        else if(parameterType.equals(Image.class)){
          try {
            argument = new Image(Integer.parseInt(stringValue));
          }
          catch (Exception ex) {
            ex.printStackTrace(System.err);
          }
        }
        return argument;
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


    public PresentationObject getSetPropertyComponent(String className,String name){
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