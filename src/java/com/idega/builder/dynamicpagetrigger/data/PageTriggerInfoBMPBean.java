package com.idega.builder.dynamicpagetrigger.data;

import java.sql.SQLException;
import java.util.Collection;

import javax.ejb.FinderException;

import com.idega.core.builder.data.ICPage;
import com.idega.core.component.data.ICObject;
import com.idega.core.component.data.ICObjectInstance;
import com.idega.core.data.GenericGroup;


/**
 * Title:        idegaWeb
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href="gummi@idega.is">Guðmundur Ágúst Sæmundsson</a>
 * @version 1.0
 */

public class PageTriggerInfoBMPBean extends com.idega.data.GenericEntity implements com.idega.builder.dynamicpagetrigger.data.PageTriggerInfo {


  public static final String _COLUMNNAME_NAME = "NAME";
  public static final String _COLUMNNAME_REFERENCED_IC_OBJECT_ID = "referenced_ic_object_id";
  public static final String _COLUMNNAME_DEFAULT_TEMPLATE_ID = "default_template_id";
  public static final String _COLUMNNAME_ROOT_PAGE_ID = "root_page_id";

  public static final String _TABLENAME_THIS_IBPAGE = "dpt_pti_ib_page";
  public static final String _TABLENAME_THIS_ICOBJECTINSTANCE = "dpt_pti_ic_object_instance";
  public static final String _TABLENAME_THIS_IC_GROUP = "dpt_pti_ic_group";


  public PageTriggerInfoBMPBean() {
    super();
  }


  public PageTriggerInfoBMPBean(int id) throws SQLException {
    super(id);
  }

  public void initializeAttributes() {
    this.addAttribute(this.getIDColumnName());
    this.addAttribute(_COLUMNNAME_NAME,"Name",true,true,String.class);
    this.addAttribute(_COLUMNNAME_REFERENCED_IC_OBJECT_ID,"ICObject sem tengst er við",true,true,Integer.class,ONE_TO_MANY,ICObject.class);
    this.addAttribute(_COLUMNNAME_DEFAULT_TEMPLATE_ID,"default tempalte",true,true,Integer.class,ONE_TO_MANY,ICPage.class);
    this.addAttribute(_COLUMNNAME_ROOT_PAGE_ID,"root page",true,true,Integer.class,ONE_TO_MANY,ICPage.class);

    this.addManyToManyRelationShip(ICObjectInstance.class, _TABLENAME_THIS_ICOBJECTINSTANCE);
    this.addManyToManyRelationShip(ICPage.class, _TABLENAME_THIS_IBPAGE);
    this.addManyToManyRelationShip(GenericGroup.class, _TABLENAME_THIS_IC_GROUP);
  }


  public String getEntityName() {
    return "dpt_page_tri_info";
  }

  public void setICObject(ICObject obj){
    if(obj != null){
      this.setColumn(com.idega.builder.dynamicpagetrigger.data.PageTriggerInfoBMPBean._COLUMNNAME_REFERENCED_IC_OBJECT_ID,obj.getID());
    } else {
      try{
        this.setColumnAsNull(com.idega.builder.dynamicpagetrigger.data.PageTriggerInfoBMPBean._TABLENAME_THIS_ICOBJECTINSTANCE);
      }catch(SQLException e){
        e.printStackTrace();
      }
    }
  }

  
  public void setName(String name) {
  	setColumn(_COLUMNNAME_NAME, name);
  }
  
  public String getName() {
  	return getStringColumnValue(_COLUMNNAME_NAME);
  }


  public void setICObject(int icObjId){
    this.setColumn(com.idega.builder.dynamicpagetrigger.data.PageTriggerInfoBMPBean._COLUMNNAME_REFERENCED_IC_OBJECT_ID,icObjId);
  }

  public void setDefaultTemplateId(int ibPageId){
    this.setColumn(com.idega.builder.dynamicpagetrigger.data.PageTriggerInfoBMPBean._COLUMNNAME_DEFAULT_TEMPLATE_ID,ibPageId);
  }

  public void setRootPageId(int ibPageId){
    this.setColumn(com.idega.builder.dynamicpagetrigger.data.PageTriggerInfoBMPBean._COLUMNNAME_ROOT_PAGE_ID,ibPageId);
  }



  public int getICObjectID(){
    return this.getIntColumnValue(com.idega.builder.dynamicpagetrigger.data.PageTriggerInfoBMPBean._COLUMNNAME_REFERENCED_IC_OBJECT_ID);
  }

  public int getDefaultTemplateId(){
    return this.getIntColumnValue(com.idega.builder.dynamicpagetrigger.data.PageTriggerInfoBMPBean._COLUMNNAME_DEFAULT_TEMPLATE_ID);
  }

  public int getRootPageId(){
    return this.getIntColumnValue(com.idega.builder.dynamicpagetrigger.data.PageTriggerInfoBMPBean._COLUMNNAME_ROOT_PAGE_ID);
  }
  
  
  public Collection ejbFindAllByICObjectID(ICObject obj) throws FinderException {
  	return idoFindPKsByQuery(idoQueryGetSelect().appendWhereEquals(_COLUMNNAME_REFERENCED_IC_OBJECT_ID,obj));
  }

}
