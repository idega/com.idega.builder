package com.idega.builder.presentation;

import com.idega.builder.business.BuilderLogic;
import com.idega.builder.business.IBPropertyHandler;
import com.idega.jmodule.object.ModuleInfo;

import com.idega.idegaweb.IWProperty;
import com.idega.idegaweb.IWPropertyList;
import com.idega.idegaweb.IWPropertyListIterator;
import com.idega.idegaweb.IWMainApplication;

import com.idega.jmodule.object.*;
import com.idega.jmodule.object.textObject.*;
import com.idega.jmodule.object.interfaceobject.*;

import com.idega.util.reflect.MethodFinder;

import com.idega.core.data.ICObject;

import java.util.List;
import java.util.Iterator;

/**
 * Title:        idegaclasses
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href="tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */

public class IBPropertiesWindow extends IBAdminWindow{

  private static final String ic_object_id_parameter = BuilderLogic.IC_OBJECT_ID_PARAMETER;
  private static final String ib_page_parameter = BuilderLogic.IB_PAGE_PARAMETER;
  private final static String METHOD_ID_PARAMETER="iw_method_identifier";
  private final static String VALUE_SAVE_PARAMETER = "ib_method_save";
  private final static String VALUE_PARAMETER = "ib_method_value";

  public void main(ModuleInfo modinfo)throws Exception{
      super.addTitle("IBPropertiesWindow");
      //setParentToReload();
      String ib_page_id = modinfo.getParameter(ib_page_parameter);
      String ic_objectinstance_id = modinfo.getParameter(ic_object_id_parameter);
      if(ic_objectinstance_id!=null){
        String methodIdentifier = modinfo.getParameter(METHOD_ID_PARAMETER);
        if(methodIdentifier==null){
          add(getPropertiesList(ic_objectinstance_id,modinfo));
        }
        else{
          if(modinfo.isParameterSet(VALUE_SAVE_PARAMETER)){
            String[] valueParams = modinfo.getParameterValues(VALUE_PARAMETER);
            //add("value="+value);
            if(valueParams!=null){
              boolean deleteProperty=true;
              String[] values = new String[valueParams.length];
              for (int i = 0; i < valueParams.length; i++) {
                values[i]=modinfo.getParameter(valueParams[i]);
                if(!values[i].equals("")){deleteProperty=false;}
              }
              //System.out.println("setting property 1");
              if(deleteProperty){
                removeProperty(methodIdentifier,ic_objectinstance_id,ib_page_id);
              }
              else{
                setProperty(methodIdentifier,values,ic_objectinstance_id,ib_page_id,modinfo.getApplication());
                setParentToReload();
                close();
              }
            }
          }
          else{
            Form form = new Form();
            add(form);
            form.maintainAllParameters();
            form.add(getPropertySetterBox(methodIdentifier,modinfo,ib_page_id,ic_objectinstance_id));
          }
        }
      }
      else {
        add("IWPropertiesWindow: ICObjectInstanceID is null");
      }
  }

  public ModuleObject getPropertiesList(String ic_object_id,ModuleInfo modinfo)throws Exception{
    Table table = new Table();
    int icObjectInstanceID = Integer.parseInt(ic_object_id);
    IWPropertyList methodList = IBPropertyHandler.getInstance().getMethods(icObjectInstanceID,modinfo.getApplication());
    IWPropertyListIterator iter = methodList.getIWPropertyListIterator();
    int counter=1;
    while (iter.hasNext()) {
      IWProperty methodProp = iter.nextProperty();
      String methodIdentifier = IBPropertyHandler.getInstance().getMethodIdentifier(methodProp);
      String methodDescr = IBPropertyHandler.getInstance().getMethodDescription(methodProp);
      Link link = new Link(methodDescr);
      link.maintainParameter(ic_object_id_parameter,modinfo);
      link.maintainParameter(ib_page_parameter,modinfo);
      link.addParameter(METHOD_ID_PARAMETER,methodIdentifier);
      table.add(link,1,counter);
      counter++;
    }
    return table;
  }

  public ModuleObject getPropertySetterBox(String methodIdentifier,ModuleInfo modinfo,String pageID,String icObjectInstanceID)throws Exception{
      Table table = new Table();
      int ypos = 1;
      /*TextInput input = new TextInput(VALUE_PARAMETER);
      String value = BuilderLogic.getInstance().getProperty(pageID,Integer.parseInt(icObjectInstanceID),methodIdentifier);
      if(value!=null){
        input.setContent(value);
      }
      table.add(input,1,1);*/
      Class ICObjectClass = null;
      int icObjectInstanceIDint = Integer.parseInt(icObjectInstanceID);
      if(icObjectInstanceIDint == -1){
        ICObjectClass = com.idega.jmodule.object.Page.class;
      }
      else{
        ICObjectClass = BuilderLogic.getInstance().getObjectClass(icObjectInstanceIDint);
      }
      String namePrefix = "ib_property_";
      java.lang.reflect.Method method = MethodFinder.getInstance().getMethod(methodIdentifier,ICObjectClass);
      Class[] parameters = method.getParameterTypes();
      //System.out.println("parameters.length="+parameters.length);
      //System.out.println("method.toString()="+method.toString());
      List list = BuilderLogic.getInstance().getPropertyValues(pageID,Integer.parseInt(icObjectInstanceID),methodIdentifier);
      Iterator iter = null;
      if(list!=null){
        iter = list.iterator();
      }
      for (int i = 0; i < parameters.length; i++) {
        Class parameterClass = parameters[i];
        String sValue=null;
        try{
          if(iter!=null){sValue = (String)iter.next();}
        }
        catch(java.util.NoSuchElementException e){
        }
        String sName=namePrefix+i;
        ModuleObject handlerBox = IBPropertyHandler.getInstance().getPropertySetterComponent(parameterClass,sName,sValue);
        Parameter param = new Parameter(VALUE_PARAMETER,sName);
        table.add(param,2,ypos);
        table.add(handlerBox,2,ypos);
        ypos++;
      }
      SubmitButton button = new SubmitButton(VALUE_SAVE_PARAMETER,"Save");
      table.add(button,ypos,2);
      return table;
  }

  public void setProperty(String key,String[] values,String icObjectInstanceID,String pageKey,IWMainApplication iwma){
    BuilderLogic.getInstance().setProperty(pageKey,Integer.parseInt(icObjectInstanceID),key,values,iwma);
  }

  public void removeProperty(String key,String icObjectInstanceID,String pageKey){
    /**
     * @todo Change so that it removes properties of specific values for multivalued properties
     */
    String value = "";
    BuilderLogic.getInstance().removeProperty(pageKey,Integer.parseInt(icObjectInstanceID),key,value);
  }


}