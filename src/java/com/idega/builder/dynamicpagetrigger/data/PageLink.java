package com.idega.builder.dynamicpagetrigger.data;

import com.idega.data.*;
import com.idega.core.data.ICFile;
import com.idega.core.data.ICObjectInstance;
import com.idega.core.data.ICObject;
import com.idega.builder.data.IBPage;

import java.sql.SQLException;


/**
 * Title:        idegaWeb
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href="gummi@idega.is">Guðmundur Ágúst Sæmundsson</a>
 * @version 1.0
 */

public class PageLink extends GenericEntity {

  public static final String _COLUMNNAME_LINK_IMAGE = "link_image";
  public static final String _COLUMNNAME_ONCLICK_IMAGE = "onclick_image";
  public static final String _COLUMNNAME_ONMOUSEOVER_IMAGE = "onmouseover_image";
  public static final String _COLUMNNAME_PAGE_ID = "ib_page_id";
  public static final String _COLUMNNAME_PAGE_TRIGGER_INFO_ID = "dpt_pti_id";
  public static final String _COLUMNNAME_DEFAULT_LINK_TEXT = "link_text";
  //public static final String _COLUMNNAME_IS_VISIBLE = "is_visible";
  public static final String _COLUMNNAME_REFERENCED_DATA_ID = "referenced_data_id";
  public static final String _COLUMNNAME_STANDARD_PRM = "standard_prm";


  public PageLink() {
    super();
  }

  public PageLink(int id) throws SQLException{
    super(id);
  }

  public void initializeAttributes() {
    this.addAttribute(this.getIDColumnName());
    this.addAttribute(_COLUMNNAME_LINK_IMAGE,"link image",true,true,Integer.class,"one-to-many",ICFile.class);
    this.addAttribute(_COLUMNNAME_ONCLICK_IMAGE,"onClick image",true,true,Integer.class,"one-to-many",ICFile.class);
    this.addAttribute(_COLUMNNAME_ONMOUSEOVER_IMAGE,"onMouseOver image",true,true,Integer.class,"one-to-many",ICFile.class);
    this.addAttribute(_COLUMNNAME_PAGE_ID,"síðu id",true,true,Integer.class,"one-to-many",IBPage.class);
    this.addAttribute(_COLUMNNAME_PAGE_TRIGGER_INFO_ID,"trigger upplýsingar",true,true,Integer.class,"one-to-many",PageTriggerInfo.class);
    this.addAttribute(_COLUMNNAME_DEFAULT_LINK_TEXT,"",true,true,String.class,250);
    //this.addAttribute(_COLUMNNAME_IS_VISIBLE,"",true,true,String.class,250);
    this.addAttribute(_COLUMNNAME_REFERENCED_DATA_ID,"",true,true,String.class,250);
    this.addAttribute(_COLUMNNAME_STANDARD_PRM,"",true,true,String.class,250);

  }

  public String getEntityName() {
    return "dpt_page_link";
  }

  public String getDefaultLinkText(){
    return this.getStringColumnValue(_COLUMNNAME_DEFAULT_LINK_TEXT);
  }

  public int getPageId(){
    return this.getIntColumnValue(this._COLUMNNAME_PAGE_ID);
  }

  public int getLinkImageId(){
    return this.getIntColumnValue(this._COLUMNNAME_LINK_IMAGE);
  }

  public int getOnClickImageId(){
    return this.getIntColumnValue(this._COLUMNNAME_ONCLICK_IMAGE);
  }

  public int getOnMouseOverImageId(){
    return this.getIntColumnValue(this._COLUMNNAME_ONMOUSEOVER_IMAGE);
  }

  public int getPageTriggerInfoId(){
    return this.getIntColumnValue(this._COLUMNNAME_PAGE_TRIGGER_INFO_ID);
  }

  public String getReferencedDataId(){
    return this.getStringColumnValue(this._COLUMNNAME_REFERENCED_DATA_ID);
  }

  public String getStandardParameters(){
    return this.getStringColumnValue(this._COLUMNNAME_STANDARD_PRM);
  }




  public void setDefaultLinkText(String value){
    this.setColumn(_COLUMNNAME_DEFAULT_LINK_TEXT, value);
  }

  public void setPageId(int value){
    this.setColumn(this._COLUMNNAME_PAGE_ID, value);
  }

  public void setLinkImageId(int value){
    this.setColumn(this._COLUMNNAME_LINK_IMAGE, value);
  }

  public void setOnClickImageId(int value){
    this.setColumn(this._COLUMNNAME_ONCLICK_IMAGE, value);
  }

  public void setOnMouseOverImageId(int value){
    this.setColumn(this._COLUMNNAME_ONMOUSEOVER_IMAGE, value);
  }

  public void setPageTriggerInfoId(int value){
    this.setColumn(this._COLUMNNAME_PAGE_TRIGGER_INFO_ID, value);
  }

  public void setReferencedDataId(String value){
    this.setColumn(this._COLUMNNAME_REFERENCED_DATA_ID, value);
  }

  public void setStandardParameters(String value){
    this.setColumn(this._COLUMNNAME_STANDARD_PRM, value);
  }





}