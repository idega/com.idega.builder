
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

import com.idega.jmodule.image.presentation.ImageInserter;

import java.util.Map;

/**
*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
*@version 1.0 alpha
*/

public class IBPropertyHandler{

    public final static String METHOD_PROPERTY_IDENTIFIER = "iw_method_identifier";
    public final static String METHOD_PROPERTY_DESCRIPTION = "iw_method_description";
    public static final String METHOD_PROPERTY_ALLOW_MULTIVALUED = "iw_method_option_multiv";

    public static final String PAGE_CHOOSER_NAME     = "ib_page_chooser";
    public static final String TEMPLATE_CHOOSER_NAME = "ib_template_chooser";
    public static final String FILE_CHOOSER_NAME     = "ic_file_chooser";

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

    public void setMethod(IWBundle iwb,String componentKey,String methodIdentifier,String methodDescription,Map options){
      IWPropertyList methods = getMethods(iwb,componentKey);
      if(methods!=null){
        IWProperty method = methods.getIWProperty(methodIdentifier);
        if(method!=null){
          methods.removeProperty(methodIdentifier);
        }
        IWPropertyList methodprop = methods.getNewPropertyList(methodIdentifier);
        methodprop.setProperty(METHOD_PROPERTY_IDENTIFIER,methodIdentifier);
        methodprop.setProperty(METHOD_PROPERTY_DESCRIPTION,methodDescription);
        methodprop.setProperties(options);
        //methods.setProperty(methodIdentifier,methodDescription);
      }
    }

    public IWPropertyList getMethods(int ic_object_instance_id,IWMainApplication iwma)throws Exception{
      String componentKey = null;
      IWBundle iwb = null;
      //Hardcoded -1 for the top page
      if(ic_object_instance_id==-1){
        componentKey = "com.idega.jmodule.object.Page";
        iwb = iwma.getBundle(com.idega.jmodule.object.Page.IW_BUNDLE_IDENTIFIER);
      }
      else{
        ICObjectInstance icoi = new ICObjectInstance(ic_object_instance_id);
        ICObject obj = icoi.getObject();
        iwb = obj.getBundle(iwma);
        componentKey = obj.getClassName();
      }
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

    /*public static ModuleObject[] getInterfaceComponent(Class[] classes,String[] names){
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
    }*/


    public static ModuleObject getPropertySetterComponent(Class parameterClass,String name,String stringValue){
      ModuleObject obj =null;
      //String className = parameterClass.getName();
      if(parameterClass.equals(java.lang.Integer.class) || parameterClass.equals(Integer.TYPE)){
          obj = new IntegerInput(name);
          if(stringValue!=null){
            ((IntegerInput)obj).setContent(stringValue);
          }
      }
      else if(parameterClass.equals(java.lang.String.class) ){
          obj = new TextInput(name);
          if(stringValue!=null){
            ((TextInput)obj).setContent(stringValue);
          }
      }
      else if(parameterClass.equals(java.lang.Boolean.class) || parameterClass.equals(Boolean.TYPE)){
          obj = new BooleanInput(name);
          if(stringValue!=null){
            ((BooleanInput)obj).setSelected(Boolean.getBoolean(stringValue));
          }
      }
      else if(parameterClass.equals(java.lang.Float.class) || parameterClass.equals(Float.TYPE)){
          obj = new FloatInput(name);
          if(stringValue!=null){
            ((FloatInput)obj).setContent(stringValue);
          }
      }
      else if(parameterClass.equals(java.lang.Double.class) || parameterClass.equals(Double.TYPE)){
          obj = new FloatInput(name);
          if(stringValue!=null){
            ((FloatInput)obj).setContent(stringValue);
          }
      }
      else if(parameterClass.equals(com.idega.jmodule.object.Image.class)){
          obj = new ImageInserter(name);
      }
      /**@todo : handle page,template,file if the inputs already hava a value
       *
       */
      else if(parameterClass.equals(com.idega.core.data.ICFile.class)){
          obj = new com.idega.builder.presentation.IBFileChooser(name);
      }
      else if(parameterClass.equals(com.idega.builder.data.IBPage.class)){
          obj = new com.idega.builder.presentation.IBPageChooser(name);
      }
      /*else if(parameterClass.equals(com.idega.builder.data.IBTemplatePage.class)){
          obj = new com.idega.builder.presentation.IBTemplateChooser(name);
      }*/
      else{
        obj = new TextInput(name);
        if(stringValue!=null){
          ((TextInput)obj).setContent(stringValue);
        }
      }
      return obj;
    }

    public String getMethodIdentifier(IWProperty methodProperty){
      if(methodProperty.getType().equals(IWProperty.MAP_TYPE)){
          return methodProperty.getPropertyList().getProperty(METHOD_PROPERTY_IDENTIFIER);
      }
      else{
        return methodProperty.getKey();
      }
    }

    public String getMethodDescription(IWProperty methodProperty){
      if(methodProperty.getType().equals(IWProperty.MAP_TYPE)){
          return methodProperty.getPropertyList().getProperty(METHOD_PROPERTY_DESCRIPTION);
      }
      else{
        return methodProperty.getValue();
      }
    }

}
