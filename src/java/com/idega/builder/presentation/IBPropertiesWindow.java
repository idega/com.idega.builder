package com.idega.builder.presentation;

import com.idega.builder.business.BuilderLogic;
import com.idega.builder.business.IBPropertyHandler;
import com.idega.jmodule.object.ModuleInfo;

import com.idega.idegaweb.IWProperty;
import com.idega.idegaweb.IWPropertyList;
import com.idega.idegaweb.IWPropertyListIterator;

import com.idega.jmodule.object.*;
import com.idega.jmodule.object.textObject.*;
import com.idega.jmodule.object.interfaceobject.*;

import com.idega.core.data.ICObject;

/**
 * Title:        idegaclasses
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href="tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */

public class IBPropertiesWindow extends IBAdminWindow{

  private static final String ic_object_id_parameter = BuilderLogic.ic_object_id_parameter;
  private static final String ib_page_parameter = BuilderLogic.ib_page_parameter;
  private final static String METHOD_ID_PARAMETER="iw_method_identifier";
  private final static String VALUE_SAVE_PARAMETER = "ib_method_save";
  private final static String VALUE_PARAMETER = "ib_method_value";

  public void main(ModuleInfo modinfo)throws Exception{
      super.addTitle("IBPropertiesWindow");
      setParentToReload();
      String ib_page_id = modinfo.getParameter(ib_page_parameter);
      String ic_objectinstance_id = modinfo.getParameter(ic_object_id_parameter);
      if(ic_objectinstance_id!=null){
        String methodIdentifier = modinfo.getParameter(METHOD_ID_PARAMETER);
        if(methodIdentifier==null){
          add(getPropertiesList(ic_objectinstance_id,modinfo));
        }
        else{
          if(modinfo.isParameterSet(VALUE_SAVE_PARAMETER)){
            String value = modinfo.getParameter(VALUE_PARAMETER);
            //add("value="+value);
            if(value!=null){
              if(!value.equals("")){
                setProperty(methodIdentifier,value,ic_objectinstance_id,ib_page_id);
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
        add("All is null");
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
      String methodIdentifier = methodProp.getKey();
      String methodDescr = methodProp.getValue();
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
      TextInput input = new TextInput(VALUE_PARAMETER);
      String value = BuilderLogic.getInstance().getProperty(pageID,Integer.parseInt(icObjectInstanceID),methodIdentifier);
      if(value!=null){
        input.setContent(value);
      }
      table.add(input,1,1);
      SubmitButton button = new SubmitButton(VALUE_SAVE_PARAMETER,"Save");
      table.add(button,1,2);
      return table;
  }

  public void setProperty(String key,String value,String icObjectInstanceID,String pageKey){
    BuilderLogic.getInstance().setProperty(pageKey,Integer.parseInt(icObjectInstanceID),key,value);
  }

}