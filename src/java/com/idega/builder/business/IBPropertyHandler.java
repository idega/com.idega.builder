
//idega 2001 - Tryggvi Larusson
/*
*Copyright 2001 idega.is All Rights Reserved.
*/
package com.idega.builder.business;

import com.idega.jmodule.object.*;
import com.idega.jmodule.object.interfaceobject.*;
import com.idega.jmodule.object.textObject.*;

import com.idega.idegaweb.*;
import com.idega.util.reflect.*;
import com.idega.core.data.ICObject;
import com.idega.core.data.ICObjectInstance;

/**
*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
*@version 0.5 alpha
*/

public class IBPropertyHandler{

    private static final String METHODS_KEY = "iw_component_methods";
    private static IBPropertyHandler instance;

    private IBPropertyHandler(){}

    public static IBPropertyHandler getInstance(){
      if(instance==null){
        instance = new IBPropertyHandler();
      }
      return instance;
    }

    public void removeMethod(IWBundle iwb,String componentKey,String methodIdentifier){
      IWPropertyList methods = getMethods(iwb,componentKey);
      if(methods!=null){
        methods.removeProperty(methodIdentifier);
      }
    }

    public void setMethod(IWBundle iwb,String componentKey,String methodIdentifier,String methodDescription){
      IWPropertyList methods = getMethods(iwb,componentKey);
      if(methods!=null){
        methods.setProperty(methodIdentifier,methodDescription);
      }
    }

    public IWPropertyList getMethods(int ic_object_instance_id,IWMainApplication iwma)throws Exception{
      ICObjectInstance icoi = new ICObjectInstance(ic_object_instance_id);
      ICObject obj = icoi.getObject();
      IWBundle iwb = obj.getBundle(iwma);
      String componentKey = obj.getClassName();
      return getMethods(iwb,componentKey);
    }

    public IWPropertyList getMethods(IWBundle iwb,String componentKey){
      IWPropertyList compList = iwb.getComponentList();
      IWPropertyList componentProperties = compList.getPropertyList(componentKey);
      if(componentProperties!=null){
        IWPropertyList methodList = componentProperties.getPropertyList(METHODS_KEY);
        if(methodList==null){
            methodList = componentProperties.getNewPropertyList(METHODS_KEY);
        }
        return methodList;
      }
      return null;
    }

    /*public IWPropertyList getMethods(IWBundle iwb,String componentKey){
      IWPropertyList compList = iwb.getComponentList();
      IWPropertyList methodList = compList.getPropertyList(METHODS_KEY);
      if(methodList==null){
          methodList = getPropertyList().getNewPropertyList(METHODS_KEY);
      }
      return methodList;
    }*/

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
