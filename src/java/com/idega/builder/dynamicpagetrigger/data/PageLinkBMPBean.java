package com.idega.builder.dynamicpagetrigger.data;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collection;

import javax.ejb.FinderException;

import com.idega.core.builder.data.ICPage;
import com.idega.core.file.data.ICFile;
import com.idega.core.user.data.User;
import com.idega.user.data.Group;

/**
 * Title:        idegaWeb
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href="gummi@idega.is">Gudmundur Agust Saemundsson</a>
 * @version 1.0
 */

public class PageLinkBMPBean extends com.idega.data.GenericEntity implements com.idega.builder.dynamicpagetrigger.data.PageLink {

  public static final String _COLUMNNAME_LINK_IMAGE = "link_image";
  public static final String _COLUMNNAME_ONCLICK_IMAGE = "onclick_image";
  public static final String _COLUMNNAME_ONMOUSEOVER_IMAGE = "onmouseover_image";
  public static final String _COLUMNNAME_PAGE_ID = "ib_page_id";
  public static final String _COLUMNNAME_PAGE_TRIGGER_INFO_ID = "dpt_pti_id";
  public static final String _COLUMNNAME_DEFAULT_LINK_TEXT = "link_text";
  //public static final String _COLUMNNAME_IS_VISIBLE = "is_visible";
  public static final String _COLUMNNAME_REFERENCED_DATA_ID = "referenced_data_id";
  public static final String _COLUMNNAME_STANDARD_PRM = "standard_prm";
  private final static String _COLUMN_DELETED = "deleted";
  private final static String _COLUMN_DELETED_BY = "deleted_by";
  private final static String _COLUMN_DELETED_WHEN = "deleted_when";
  private final static String _COLUMNNAME_DPT_PERMISSION_GROUP = "IC_GROUP_ID";

  public PageLinkBMPBean() {
    super();
  }

  public PageLinkBMPBean(int id) throws SQLException{
    super(id);
  }

  public void initializeAttributes() {
    this.addAttribute(this.getIDColumnName());
    this.addAttribute(_COLUMNNAME_LINK_IMAGE,"link image",true,true,Integer.class,"one-to-many",ICFile.class);
    this.addAttribute(_COLUMNNAME_ONCLICK_IMAGE,"onClick image",true,true,Integer.class,"one-to-many",ICFile.class);
    this.addAttribute(_COLUMNNAME_ONMOUSEOVER_IMAGE,"onMouseOver image",true,true,Integer.class,"one-to-many",ICFile.class);
    this.addAttribute(_COLUMNNAME_PAGE_ID,"page id",true,true,Integer.class,"one-to-many",ICPage.class);
    this.addAttribute(_COLUMNNAME_PAGE_TRIGGER_INFO_ID,"trigger upplýsingar",true,true,Integer.class,"one-to-many",PageTriggerInfo.class);
    addAttribute(_COLUMN_DELETED,"Deleted",true,true,Boolean.class);
    addAttribute(_COLUMN_DELETED_BY,"Deleted by",true,true,Integer.class,"many-to-one",User.class);
    addAttribute(_COLUMN_DELETED_WHEN,"Deleted when",true,true,Timestamp.class);
    this.addAttribute(_COLUMNNAME_DEFAULT_LINK_TEXT,"",true,true,String.class,250);
    //this.addAttribute(_COLUMNNAME_IS_VISIBLE,"",true,true,String.class,250);
    this.addAttribute(_COLUMNNAME_REFERENCED_DATA_ID,"",true,true,String.class,250);
    this.addAttribute(_COLUMNNAME_STANDARD_PRM,"",true,true,String.class,250);
    addOneToOneRelationship(_COLUMNNAME_DPT_PERMISSION_GROUP,"Group representing this pagetree for the accesscontrol system",Group.class);
  }

  public String getEntityName() {
    return "dpt_page_link";
  }


  public void setDeleted(boolean value){
      setColumn(_COLUMN_DELETED,value);
    }

    public void setDeletedBy(int userId){
      setColumn(_COLUMN_DELETED_BY,userId);
    }

    public void setDeletedWhen(Timestamp time){
      setColumn(_COLUMN_DELETED_WHEN,time);
    }

    public boolean getDeleted(){
      return getBooleanColumnValue(_COLUMN_DELETED);
    }

    public int getDeletedBy(){
      return getIntColumnValue(_COLUMN_DELETED_BY);
    }

    public Timestamp getDeletedWhen(){
      return (Timestamp)this.getColumnValue(_COLUMN_DELETED_WHEN);
    }


  public String getName(){
    return this.getDefaultLinkText();
  }

  public void setName(String name){
    this.setDefaultLinkText(name);
  }
  public String getDefaultLinkText(){
    return this.getStringColumnValue(_COLUMNNAME_DEFAULT_LINK_TEXT);
  }

  public int getPageId(){
    return this.getIntColumnValue(PageLinkBMPBean._COLUMNNAME_PAGE_ID);
  }

  public int getLinkImageId(){
    return this.getIntColumnValue(PageLinkBMPBean._COLUMNNAME_LINK_IMAGE);
  }

  public int getOnClickImageId(){
    return this.getIntColumnValue(PageLinkBMPBean._COLUMNNAME_ONCLICK_IMAGE);
  }

  public int getOnMouseOverImageId(){
    return this.getIntColumnValue(PageLinkBMPBean._COLUMNNAME_ONMOUSEOVER_IMAGE);
  }

  public int getPageTriggerInfoId(){
    return this.getIntColumnValue(PageLinkBMPBean._COLUMNNAME_PAGE_TRIGGER_INFO_ID);
  }

  public String getReferencedDataId(){
    return this.getStringColumnValue(PageLinkBMPBean._COLUMNNAME_REFERENCED_DATA_ID);
  }

  public String getStandardParameters(){
    return this.getStringColumnValue(PageLinkBMPBean._COLUMNNAME_STANDARD_PRM);
  }

  public Group getGroup() {
  	return (Group)getColumnValue(_COLUMNNAME_DPT_PERMISSION_GROUP);
  }



  public void setDefaultLinkText(String value){
    this.setColumn(_COLUMNNAME_DEFAULT_LINK_TEXT, value);
  }

  public void setPageId(int value){
    this.setColumn(PageLinkBMPBean._COLUMNNAME_PAGE_ID, value);
  }

  public void setLinkImageId(int value){
    this.setColumn(PageLinkBMPBean._COLUMNNAME_LINK_IMAGE, value);
  }

  public void setOnClickImageId(int value){
    this.setColumn(PageLinkBMPBean._COLUMNNAME_ONCLICK_IMAGE, value);
  }

  public void setOnMouseOverImageId(int value){
    this.setColumn(PageLinkBMPBean._COLUMNNAME_ONMOUSEOVER_IMAGE, value);
  }

  public void setPageTriggerInfoId(int value){
    this.setColumn(PageLinkBMPBean._COLUMNNAME_PAGE_TRIGGER_INFO_ID, value);
  }

  public void setReferencedDataId(String value){
    this.setColumn(PageLinkBMPBean._COLUMNNAME_REFERENCED_DATA_ID, value);
  }

  public void setStandardParameters(String value){
    this.setColumn(PageLinkBMPBean._COLUMNNAME_STANDARD_PRM, value);
  }
  
  public void setGroup(Group group) {
  	setColumn(_COLUMNNAME_DPT_PERMISSION_GROUP,group);
  }
  
  
  public Collection ejbFindAllByPageTriggerInfo(PageTriggerInfo info) throws FinderException {
  	return idoFindPKsByQuery(idoQueryGetSelect().appendWhereEquals(_COLUMNNAME_PAGE_TRIGGER_INFO_ID,info));
  }
  
  public Collection ejbFindAllByPageTriggerInfo(Collection info) throws FinderException {
  	return idoFindPKsByQuery(idoQueryGetSelect().appendWhere(_COLUMNNAME_PAGE_TRIGGER_INFO_ID).appendInCollection(info));
  }

  public Object ejbFindByGroup(Group group) throws FinderException {
  	return idoFindOnePKByQuery(idoQueryGetSelect().appendWhereEquals(_COLUMNNAME_DPT_PERMISSION_GROUP,group));
  }

  public Object ejbFindByRootPageID(int rootpageID) throws FinderException {
  	return idoFindOnePKByQuery(idoQueryGetSelect().appendWhereEquals(_COLUMNNAME_PAGE_ID,rootpageID));
  }
  
  public Object ejbFindByRootPage(ICPage rootpage) throws FinderException {
  	return idoFindOnePKByQuery(idoQueryGetSelect().appendWhereEquals(_COLUMNNAME_PAGE_ID,rootpage));
  }

}
