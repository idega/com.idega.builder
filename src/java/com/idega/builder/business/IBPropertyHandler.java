
//idega 2001 - Tryggvi Larusson
/*
*Copyright 2001 idega.is All Rights Reserved.
*/
package com.idega.builder.business;

import com.idega.presentation.*;
import com.idega.presentation.ui.*;
import com.idega.presentation.text.*;

import com.idega.idegaweb.*;
import com.idega.util.reflect.*;
import com.idega.core.data.ICObject;
import com.idega.core.data.ICObjectInstance;

import com.idega.block.media.presentation.ImageInserter;

import java.util.Map;
import java.util.List;
import java.util.Iterator;
import java.util.Vector;

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

    public static final String METHODS_KEY = "iw_component_methods";
    public static final String METHOD_PARAMETERS_KEY = "iw_method_params";
    public static final String METHOD_PARAMETER_PROPERTY_DESCRIPTION = "iw_method_param_desc";
    public static final String METHOD_PARAMETER_PROPERTY_HANDLER_CLASS = "iw_method_param_handler";

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

    public IWProperty getMethodProperty(int ic_object_instance_id,String methodPropertyKey,IWMainApplication iwma)throws Exception{
      IWPropertyList list = getMethods(ic_object_instance_id,iwma);
      if(list!=null){
        return list.getIWProperty(methodPropertyKey);
      }
      return null;
    }

    public IWPropertyList getMethods(int ic_object_instance_id,IWMainApplication iwma)throws Exception{
      String componentKey = null;
      IWBundle iwb = null;
      //Hardcoded -1 for the top page
      if(ic_object_instance_id==-1){
        componentKey = "com.idega.presentation.Page";
        iwb = iwma.getBundle(com.idega.presentation.Page.IW_BUNDLE_IDENTIFIER);
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

    public List getMethodsListOrdered(int ic_object_instance_id,IWContext iwc){
      try{
        IWPropertyList methodList = getMethods(ic_object_instance_id,iwc.getApplication());
        if(methodList!=null){
            java.util.Vector vect = new Vector();
            Iterator iter = methodList.iterator();
            while (iter.hasNext()) {
              IWProperty item = (IWProperty)iter.next();
              vect.add(item);
            }
            java.util.Collections.sort(vect,com.idega.util.Comparators.getMethodDescriptionComparator(iwc));
            return vect;
        }
      }
      catch(Exception e){
        e.printStackTrace(System.err);
      }
      return com.idega.util.ListUtil.getEmptyList();
    }

    /*public IWPropertyList getMethods(IWBundle iwb,String componentKey){
      IWPropertyList compList = iwb.getComponentList();
      IWPropertyList methodList = compList.getPropertyList(METHODS_KEY);
      if(methodList==null){
          methodList = getPropertyList().getNewPropertyList(METHODS_KEY);
      }
      return methodList;
    }*/

    /*public static PresentationObject[] getInterfaceComponent(Class[] classes,String[] names){
      PresentationObject[] objects = new PresentationObject[classes.length];
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

    public static PresentationObject getPropertySetterComponent(Class parameterClass,String name,String stringValue){
      PresentationObject obj =null;
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
      else if(parameterClass.equals(com.idega.presentation.Image.class)){
          //obj = new com.idega.jmodule.image.presentation.ImageInserter(name,false);
          obj = new ImageInserter(name,false);
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

    /*public String getMethodDescription(IWProperty methodProperty){
      if(methodProperty.getType().equals(IWProperty.MAP_TYPE)){
          return methodProperty.getPropertyList().getProperty(METHOD_PROPERTY_DESCRIPTION);
      }
      else{
        return methodProperty.getValue();
      }
    }*/


    /**
     * @todo: Change so that this returns the Localized description
     */
    public String getMethodDescription(IWProperty methodProperty,IWContext iwc){
      if(methodProperty.getType().equals(IWProperty.MAP_TYPE)){
          return methodProperty.getPropertyList().getProperty(METHOD_PROPERTY_DESCRIPTION);
      }
      else{
        return methodProperty.getValue();
      }
    }


    /**
     * @todo: Change so that this returns the Localized description
     */
    public String getMethodDescription(int icObjectInstanceID,String methodPropertyKey,IWContext iwc){
      try{
        IWProperty methodProperty = getMethodProperty(icObjectInstanceID,methodPropertyKey,iwc.getApplication());
        if(methodProperty!=null){
          return getMethodDescription(methodProperty,iwc);
        }
      }
      catch(Exception e){
        e.printStackTrace(System.err);
      }
      return null;
    }

    /**
     * @todo: Change so that this returns the Localized description
     */
    public String getMethodParameterDescription(IWProperty methodProperty,int parameterIndex, IWContext iwc){
      IWPropertyList parameter = getMethodParameterPropertyList(methodProperty,parameterIndex);
      if(parameter!=null){
          return parameter.getProperty(METHOD_PARAMETER_PROPERTY_DESCRIPTION);
      }
      else{
        return "Property "+parameterIndex;
      }
    }


    public String getMethodParameterProperty(IWProperty methodProperty,int parameterIndex, String propertyKey){
      IWPropertyList parameter = getMethodParameterPropertyList(methodProperty,parameterIndex);
      if(parameter!=null){
          return parameter.getProperty(propertyKey);
      }
      else{
        return null;
      }
    }

    public String getMethodParameterHandlerClassName(IWProperty methodProperty,int parameterIndex){
      String theReturn = getMethodParameterProperty(methodProperty,parameterIndex,METHOD_PARAMETER_PROPERTY_HANDLER_CLASS);
      if(theReturn == null){
        return this.getClass().getName();
      }
      else{
        return theReturn;
      }
    }


    public IWPropertyList getMethodParameterPropertyList(IWProperty methodProperty){
      if(methodProperty.getType().equals(IWProperty.MAP_TYPE)){
          return methodProperty.getPropertyList().getPropertyList(METHOD_PARAMETERS_KEY);
      }
      else{
        return null;
      }
    }

    public IWPropertyList getMethodParameterPropertyList(IWProperty methodProperty,int parameter){
      IWPropertyList parameters = getMethodParameterPropertyList(methodProperty);
      if(parameters!=null){
        IWPropertyList list = parameters.getIWPropertyList(Integer.toString(parameter));
        return list;
      }
      else{
        return null;
      }
    }
}
