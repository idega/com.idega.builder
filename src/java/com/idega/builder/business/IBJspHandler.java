package com.idega.builder.business;

import com.idega.jmodule.object.*;
import com.idega.builder.data.*;
import java.lang.*;
import java.sql.*;

/**
 * Title:        IB
 * Description:
 * Copyright:    Copyright (c) 2001 idega.is All Rights Reserved
 * Company:      idega margmiðlun
 * @author idega 2001 - <a href="mailto:idega@idega.is">idega team</a>
 * @version 1.0
 */

public class IBJspHandler {

  public IBJspHandler() {

  }

  public static int setIBJspPage(String Url, String AttributeName, String AttributeValue) throws SQLException{
    IBJspPage page = new IBJspPage();
    page.setUrl(Url);
    page.setAttributeName(AttributeName);
    page.setAttributeValue(AttributeValue);
    page.insert();
    return page.getID();
  }


  public static void setJspPageInstanceID(ModuleInfo modinfo, String AttributeName, String AttributeValue) throws SQLException{

    IBJspHandler.IBJspHandlerVariables variables;
    Object SessionObject = modinfo.getSession().getAttribute("IBJspHandlerVariables");
    String Url = modinfo.getRequest().getRequestURI();

    if (SessionObject == null){
      variables = (new IBJspHandler()).new IBJspHandlerVariables();
    }else{
      variables = (IBJspHandlerVariables)SessionObject;
    }

    if (variables.getUrl() != Url || variables.getAttributeName() != AttributeName || variables.getAttributeValue() != AttributeValue ){
      IBJspPage page = new IBJspPage();

      IBJspPage Pages[] = (IBJspPage[])page.findAll("SELECT * FROM " + page.getEntityName() + " WHERE " + page.getUrlColumnName() + " = " + Url + " AND " + page.getAttributeNameColumnName() + " = " + AttributeName + " AND " + page.getAttributeValueColumnName() + " = " + AttributeValue );

      if (Pages == null){
        modinfo.setSessionAttribute("JspPageInstanceID", new Integer(setIBJspPage(Url, AttributeName, AttributeValue)));
      }else{
        modinfo.setSessionAttribute("JspPageInstanceID", new Integer(Pages[0].getID()));
      }
      variables.setUrl(Url);
      variables.setAttributeName(AttributeName);
      variables.setAttributeValue(AttributeValue);
      modinfo.getSession().setAttribute("IBJspHandlerVariables",variables);
    }

  }


  public static int getJspPageInstanceID(ModuleInfo modinfo){
    return ((Integer)modinfo.getSessionAttribute("JspPageInstanceID")).intValue();
  }

  public static IBJspPage getIBJspPage(ModuleInfo modinfo) throws SQLException{
    return new IBJspPage(getJspPageInstanceID(modinfo));
  }






  public class IBJspHandlerVariables {

    String URL;
    String Attribute_name;
    String Attribute_value;


    public IBJspHandlerVariables(){

    }


    public String getUrl(){
      return URL;
    }

    public String getAttributeName(){
      return Attribute_name;
    }

    public String getAttributeValue(){
      return Attribute_value;
    }


    public void setUrl(String url){
      URL = url;
    }

    public void setAttributeName(String AttributeName){
      Attribute_name =AttributeName;
    }

    public void setAttributeValue(String AttributeValue){
      Attribute_value =AttributeValue;
    }



  }  // inner Class IBJspHandlerVariables




} // class IBJspHandler