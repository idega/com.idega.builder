package com.idega.builder.data;

import com.idega.data.*;
import java.sql.*;

/**
 * Title:        IB
 * Description:
 * Copyright:    Copyright (c) 2001 idega.is All Rights Reserved
 * Company:      idega margmiðlun
 * @author idega 2001 - <a href="mailto:idega@idega.is">idega team</a>
 * @version 1.0
 */

public class IBJspPage extends GenericEntity {



  public IBJspPage() {
    super();
  }

  public IBJspPage(int id) throws SQLException {
    super(id);
  }


  public void initializeAttributes() {
    addAttribute(getIDColumnName());
    addAttribute("url","Url",true,true,"java.lang.String");
    addAttribute("attribute_name","Attribute Name",true,true,"java.lang.String");
    addAttribute("attribute_value","Attribute Value",true,true,"java.lang.String");
  }


  public String getEntityName() {
    return "ib_jsp_page";
  }



  public String getUrlColumnName(){
    return "url";
  }


  public String getUrl(){
    return getStringColumnValue("url");
  }

  public void setUrl( String Url){
    setColumn("url", Url);
  }


  public String getAttributeNameColumnName(){
    return "attribute_name";
  }

  public String getAttributeName(){
    return (String)getColumnValue("attribute_name");
  }

  public void setAttributeName( String attribute_name){
    setColumn("attribute_name",attribute_name);
  }



  public String getAttributeValueColumnName(){
    return "attribute_value";
  }

  public String getAttributeValue(){
    return getStringColumnValue("attribute_value");
  }

  public void setAttributeValue (String attribute_value){
    setColumn("attribute_value", attribute_value);
  }




}