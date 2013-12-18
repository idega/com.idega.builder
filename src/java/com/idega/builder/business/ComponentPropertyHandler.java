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

import javax.faces.component.UIComponent;

import com.idega.repository.data.Instantiator;
import com.idega.repository.data.Singleton;
import com.idega.repository.data.SingletonRepository;
import com.idega.util.reflect.ComponentProperty;
import com.idega.util.reflect.Property;
import com.idega.util.reflect.PropertyCache;
import com.idega.xml.XMLElement;

public class ComponentPropertyHandler implements Singleton {

  private static Instantiator instantiator = new Instantiator() { @Override
public Object getInstance() { return new ComponentPropertyHandler();}};

  private static final String XML_TYPE_TAG = "type";
  private static final String XML_VALUE_TAG = "value";

  protected ComponentPropertyHandler() {
  	// empty
  }

  public static ComponentPropertyHandler getInstance(){
  	return (ComponentPropertyHandler) SingletonRepository.getRepository().getInstance(ComponentPropertyHandler.class,instantiator);
  }

     void setReflectionProperty(UIComponent instance,String methodIdentifier,List<String> stringValues){
      Method method = com.idega.util.reflect.MethodFinder.getInstance().getMethod(methodIdentifier,instance.getClass());
      if(method==null){
    	  throw new RuntimeException("Method: "+methodIdentifier+" not found for component: " + instance.getClass().getName());
      }
      setReflectionProperty(instance,method,stringValues);
     }

     void setComponentProperty(UIComponent instance, String componentProperty, List<String> stringValues) {
    	 ComponentProperty property = new ComponentProperty(componentProperty,instance.getClass());
    	 setPropertyValues(instance, property, stringValues);
     }

     private static String[] emptyStringArray = new String[0];

     void setReflectionProperty(UIComponent instance,Method method,List<String> stringPropertyValues){
     	Property property = new Property(method);
     	setPropertyValues(instance, property, stringPropertyValues);
     }

     void setPropertyValues(UIComponent instance, Property property, List<String> stringPropertyValues) {
     	String[] sPropertyValuesArray = stringPropertyValues.toArray(emptyStringArray);
     	property.setPropertyValues(sPropertyValuesArray);

     	PropertyCache.getInstance().addProperty(BuilderLogic.getInstance().getInstanceId(instance),property);
     	property.setPropertyOnInstance(instance);
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

}
