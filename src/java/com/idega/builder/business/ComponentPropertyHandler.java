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
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import com.idega.presentation.PresentationObject;
import com.idega.util.reflect.Property;
import com.idega.xml.XMLElement;

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

     
     private static String[] emptyStringArray = new String[0];
     void setReflectionProperty(PresentationObject instance,Method method,Vector stringPropertyValues){
     	Property property = new Property(method);
     	String[] sPropertyValuesArray = (String[])stringPropertyValues.toArray(emptyStringArray);
     	property.setPropertyValues(sPropertyValuesArray);
     	
     	instance.addReflectionProperty(property);
     	
     }
     
     /*
     void setReflectionProperty(PresentationObject instance,Method method,Vector stringValues){
        //Object[] args = getObjectArguments(stringValues);
        //method.invoke(instance,args);
        try{
          Object[] args = new Object[stringValues.size()];
          Class[] parameterTypes = method.getParameterTypes();
          for (int i = 0; i < parameterTypes.length; i++) {
            if(parameterTypes[i]!=null){
              args[i] = handleParameter(parameterTypes[i],(String)stringValues.get(i));
            }
          }
          method.invoke(instance,args);
        }
        catch(Exception e){
          System.err.println("Error in property '"+method.toString()+"' for ICObjectInstance="+instance.getICObjectInstanceID());
          e.printStackTrace();
        }
    }


    static Object handleParameter(Class parameterType,String stringValue)throws Exception{
        Object argument=null;
        if(parameterType.equals(Integer.class) || parameterType.equals(Integer.TYPE)){
          //try{
            argument = new Integer(stringValue);
          //}
          //catch(NumberFormatException e){
          //  e.printStackTrace(System.out);
          //}
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
        else if(parameterType.equals(ICPage.class)){
          //try {
            argument = ((com.idega.core.builder.data.ICPageHome)com.idega.data.IDOLookup.getHomeLegacy(ICPage.class)).findByPrimaryKeyLegacy(Integer.parseInt(stringValue));
          //}
          //catch (Exception ex) {
          //  ex.printStackTrace(System.err);
          //}
        }
        else if(parameterType.equals(ICFile.class)){
          try {
            argument = ((com.idega.core.file.data.ICFileHome)com.idega.data.IDOLookup.getHome(ICFile.class)).findByPrimaryKey(new Integer(stringValue));
          }
          catch (Exception ex) {
            ex.printStackTrace(System.err);
          }
        }
//        else if(parameterType.equals(IBTemplatePage.class)){
//          try {
//            argument = new IBTemplatePage(Integer.parseInt(stringValue));
//          }
//          catch (Exception ex) {
//            ex.printStackTrace(System.err);
//          }
//        }
        else if(parameterType.equals(Image.class)){
          //try {
            argument = new Image(Integer.parseInt(stringValue));
          //}
          //catch (Exception ex) {
          //  ex.printStackTrace(System.err);
          //}
        }
        //REMOVE AND MAKE GENERIC! ask tryggvi and eiki
        else if(parameterType.equals(Group.class)){
          try {
            argument = (Group) ((GroupHome)com.idega.data.IDOLookup.getHome(Group.class)).findByPrimaryKey(new Integer( stringValue.substring(stringValue.lastIndexOf('_')+1, stringValue.length()) ));
          }
          catch (Exception ex) {
            ex.printStackTrace(System.err);
          }
        }
        return argument;
    }*/

     private  Object[] getObjectArguments(XMLElement value){
        List children = value.getChildren();
        Object[] theReturn;
        if(children!=null){
          theReturn = new Object[children.size()];
          Iterator iter = children.iterator();
          int counter = 0;
          while (iter.hasNext()) {
            XMLElement item = (XMLElement)iter.next();
            theReturn[counter]=handleElementProperty(item);
            counter++;
          }
        }
        else{
          theReturn = new Object[0];
        }
        return theReturn;
      }  
     
     
    public Object handleElementProperty(XMLElement el){
      XMLElement typeEl = el.getChild(XML_TYPE_TAG);
      XMLElement valueEl = el.getChild(XML_VALUE_TAG);
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

/*
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
*/

}
