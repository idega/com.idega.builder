package com.idega.builder.dynamicpagetrigger.data;

import com.idega.data.*;
import com.idega.builder.data.IBPage;
import com.idega.core.data.ICObjectInstance;
import com.idega.core.data.ICObject;
import com.idega.core.data.GenericGroup;

import java.sql.SQLException;


/**
 * Title:        idegaWeb
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href="gummi@idega.is">Guðmundur Ágúst Sæmundsson</a>
 * @version 1.0
 */

public class PageTriggerInfo extends GenericEntity {


  public static final String _COLUMNNAME_REFERENCED_IC_OBJECT_ID = "referenced_ic_object_id";
  public static final String _COLUMNNAME_DEFAULT_TEMPLATE_ID = "default_template_id";

  public static final String _TABLENAME_THIS_IBPAGE = "dpt_pti_ib_page";
  public static final String _TABLENAME_THIS_ICOBJECTINSTANCE = "dpt_pti_ic_object_instance";
  public static final String _TABLENAME_THIS_IC_GROUP = "dpt_pti_ic_group";


  public PageTriggerInfo() {
    super();
  }


  public PageTriggerInfo(int id) throws SQLException {
    super(id);
  }

  public void initializeAttributes() {
    this.addAttribute(this.getIDColumnName());
    this.addAttribute(_COLUMNNAME_REFERENCED_IC_OBJECT_ID,"ICObject sem tengst er við",true,true,Integer.class,ONE_TO_MANY,ICObject.class);
    this.addAttribute(_COLUMNNAME_DEFAULT_TEMPLATE_ID,"default tempalte",true,true,Integer.class,ONE_TO_MANY,IBPage.class);
    this.addManyToManyRelationShip(ICObjectInstance.class, _TABLENAME_THIS_ICOBJECTINSTANCE);
    this.addManyToManyRelationShip(IBPage.class, _TABLENAME_THIS_IBPAGE);
    this.addManyToManyRelationShip(GenericGroup.class, _TABLENAME_THIS_IC_GROUP);
  }


  public String getEntityName() {
    return "dpt_page_tri_info";
  }

  public void setICObject(ICObject obj){
    if(obj != null){
      this.setColumn(PageTriggerInfo._COLUMNNAME_REFERENCED_IC_OBJECT_ID,obj.getID());
    } else {
      try{
        this.setColumnAsNull(PageTriggerInfo._TABLENAME_THIS_ICOBJECTINSTANCE);
      }catch(SQLException e){
        e.printStackTrace();
      }
    }
  }

  public void setICObject(int icObjId){
    this.setColumn(PageTriggerInfo._COLUMNNAME_REFERENCED_IC_OBJECT_ID,icObjId);
  }

  public void setDefaultTemplateId(int ibPageId){
    this.setColumn(PageTriggerInfo._COLUMNNAME_DEFAULT_TEMPLATE_ID,ibPageId);
  }


  public int getICObjectID(){
    return this.getIntColumnValue(PageTriggerInfo._COLUMNNAME_REFERENCED_IC_OBJECT_ID);
  }

  public int getDefaultTemplateId(){
    return this.getIntColumnValue(PageTriggerInfo._COLUMNNAME_DEFAULT_TEMPLATE_ID);
  }
}