package com.idega.builder.presentation;

import com.idega.presentation.*;
import com.idega.presentation.ui.*;
import com.idega.presentation.text.*;

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

  public static final String IC_OBJECT_INSTANCE_ID_PARAMETER = IBPropertiesWindow.IC_OBJECT_INSTANCE_ID_PARAMETER;
  public static final String IB_PAGE_PARAMETER = IBPropertiesWindow.IB_PAGE_PARAMETER;
  final static String METHOD_ID_PARAMETER= IBPropertiesWindow.METHOD_ID_PARAMETER;
  final static String VALUE_SAVE_PARAMETER = IBPropertiesWindow.VALUE_SAVE_PARAMETER;
  final static String VALUE_PARAMETER = IBPropertiesWindow.VALUE_PARAMETER;

  static final String LIST_FRAME = "ib_prop_list_frame";
  static final String PROPERTY_FRAME = "ib_prop_frame";

  public IBPropertiesWindowList() {
    this.setAllMargins(0);
  }

  public void main(IWContext iwc)throws Exception{
    String ic_object_id = getUsedICObjectInstanceID(iwc);
    if(ic_object_id!=null){
      add(getPropertiesList(ic_object_id,iwc));
      System.out.println("IBPropertiesWindowList: Getting IC_OBJECT_ID");
    }
    else{
      System.out.println("IBPropertiesWindowList: Not getting IC_OBJECT_ID");
    }
  }

  public String getUsedICObjectInstanceID(IWContext iwc){
    return iwc.getParameter(IC_OBJECT_INSTANCE_ID_PARAMETER);
  }

  public PresentationObject getPropertiesList(String ic_object_id,IWContext iwc)throws Exception{
    Table table = new Table();
    int icObjectInstanceID = Integer.parseInt(ic_object_id);
    List methodList = IBPropertyHandler.getInstance().getMethodsListOrdered(icObjectInstanceID,iwc);
    Iterator iter = methodList.iterator();
    int counter=1;
    while (iter.hasNext()) {
      IWProperty methodProp = (IWProperty)iter.next();
      String methodIdentifier = IBPropertyHandler.getInstance().getMethodIdentifier(methodProp);
      String methodDescr = IBPropertyHandler.getInstance().getMethodDescription(methodProp,iwc);
      Link link = new Link(methodDescr);
      /*link.setTarget(PROPERTY_FRAME);
      link.maintainParameter(Page.IW_FRAME_CLASS_PARAMETER,iwc);
      link.maintainParameter(IC_OBJECT_ID_PARAMETER,iwc);
      link.maintainParameter(IB_PAGE_PARAMETER,iwc);
      link.addParameter(METHOD_ID_PARAMETER,methodIdentifier);*/
      link.setURL("javascript:parent."+PROPERTY_FRAME+"."+IBPropertiesWindowSetter.CHANGE_PROPERTY_FUNCTION_NAME+"('"+methodIdentifier+"')");
      table.add(link,1,counter);
      counter++;
    }
    return table;
  }



}