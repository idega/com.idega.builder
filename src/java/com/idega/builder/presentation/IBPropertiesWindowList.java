package com.idega.builder.presentation;

import com.idega.jmodule.object.*;
import com.idega.jmodule.object.interfaceobject.*;
import com.idega.jmodule.object.textObject.*;

import com.idega.idegaweb.*;

import com.idega.builder.business.IBPropertyHandler;
import com.idega.builder.business.BuilderLogic;

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

public class IBPropertiesWindowList extends Page{

  public static final String IC_OBJECT_ID_PARAMETER = IBPropertiesWindow.IC_OBJECT_ID_PARAMETER;
  public static final String IB_PAGE_PARAMETER = IBPropertiesWindow.IB_PAGE_PARAMETER;
  final static String METHOD_ID_PARAMETER= IBPropertiesWindow.METHOD_ID_PARAMETER;
  final static String VALUE_SAVE_PARAMETER = IBPropertiesWindow.VALUE_SAVE_PARAMETER;
  final static String VALUE_PARAMETER = IBPropertiesWindow.VALUE_PARAMETER;

  static final String LIST_FRAME = "ib_prop_list_frame";
  static final String PROPERTY_FRAME = "ib_prop_frame";

  public IBPropertiesWindowList() {
    this.setAllMargins(0);
  }

  public void main(ModuleInfo modinfo)throws Exception{
    String ic_object_id = getICObjectID(modinfo);
    if(ic_object_id!=null){
      add(getPropertiesList(ic_object_id,modinfo));
      System.out.println("IBPropertiesWindowList: Getting IC_OBJECT_ID");
    }
    else{
      System.out.println("IBPropertiesWindowList: Not getting IC_OBJECT_ID");
    }
  }

  public String getICObjectID(ModuleInfo modinfo){
    return modinfo.getParameter(IC_OBJECT_ID_PARAMETER);
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
      /*link.setTarget(PROPERTY_FRAME);
      link.maintainParameter(Page.IW_FRAME_CLASS_PARAMETER,modinfo);
      link.maintainParameter(IC_OBJECT_ID_PARAMETER,modinfo);
      link.maintainParameter(IB_PAGE_PARAMETER,modinfo);
      link.addParameter(METHOD_ID_PARAMETER,methodIdentifier);*/
      link.setURL("javascript:parent."+PROPERTY_FRAME+"."+IBPropertiesWindowSetter.CHANGE_PROPERTY_FUNCTION_NAME+"('"+methodIdentifier+"')");
      table.add(link,1,counter);
      counter++;
    }
    return table;
  }



}