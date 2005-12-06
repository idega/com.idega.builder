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
import javax.faces.component.UIComponent;
import com.idega.repository.data.Instantiator;
import com.idega.repository.data.Singleton;
import com.idega.repository.data.SingletonRepository;
import com.idega.util.reflect.ComponentProperty;
import com.idega.util.reflect.Property;
import com.idega.util.reflect.PropertyCache;
import com.idega.xml.XMLElement;

public class ComponentPropertyHandler implements Singleton {

  private static Instantiator instantiator = new Instantiator() { public Object getInstance() { return new ComponentPropertyHandler();}};

  private static final String XML_TYPE_TAG = "type";
  private static final String XML_VALUE_TAG = "value";


  private ComponentPropertyHandler() {
  	// empty
  }

  public static ComponentPropertyHandler getInstance(){
  	return (ComponentPropertyHandler) SingletonRepository.getRepository().getInstance(ComponentPropertyHandler.class,instantiator);
  }

     void setReflectionProperty(UIComponent instance,String methodIdentifier,List stringValues){
      Method method = com.idega.util.reflect.MethodFinder.getInstance().getMethod(methodIdentifier,instance.getClass());
      if(method==null){
        throw new RuntimeException("Method: "+methodIdentifier+" not found");
      }
      setReflectionProperty(instance,method,stringValues);
     }
     
     void setComponentProperty(UIComponent instance, String componentProperty, List stringValues) {
    	 ComponentProperty property = new ComponentProperty(componentProperty,instance.getClass());
    	 setPropertyValues(instance, property, stringValues);
     }
     
     private static String[] emptyStringArray = new String[0];
     
     void setReflectionProperty(UIComponent instance,Method method,List stringPropertyValues){
     	Property property = new Property(method);
     	setPropertyValues(instance, property, stringPropertyValues);
     }
     
     void setPropertyValues(UIComponent instance, Property property, List stringPropertyValues) {
     	String[] sPropertyValuesArray = (String[])stringPropertyValues.toArray(emptyStringArray);
     	property.setPropertyValues(sPropertyValuesArray);
     	
     	PropertyCache.getInstance().addProperty(BuilderLogic.getInstance().getInstanceId(instance),property);
     	property.setPropertyOnInstance(instance);
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
