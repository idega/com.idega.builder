/*
 * Created on 22.6.2004
 *
 * Copyright (C) 2004 Idega hf. All Rights Reserved.
 *
 *  This software is the proprietary information of Idega hf.
 *  Use is subject to license terms.
 */
package com.idega.builder.data;

import java.util.Collection;

import javax.ejb.FinderException;

import com.idega.data.GenericView;

/**
 * @author aron
 *
 * IBPageObjectBMPBean TODO Describe this type
 */
public class IBPageObjectViewBMPBean extends GenericView implements IBPageObjectView{

	/* (non-Javadoc)
	 * @see com.idega.data.GenericEntity#getEntityName()
	 */
	private static final String TEMPLATE_ID = "TEMPLATE_ID";
	private static final String FILE_ID = "FILE_ID";
	private static final String PAGE_NAME = "PAGE_NAME";
	private static final String BUNDLE = "BUNDLE";
	private static final String OBJECT_TYPE = "OBJECT_TYPE";
	private static final String CLASS_NAME = "CLASS_NAME";
	private static final String OBJECT_NAME = "OBJECT_NAME";
	private static final String IB_PAGE_ID = "IB_PAGE_ID";
	private static final String IC_OBJECT_ID = "IC_OBJECT_ID";
	private static final String IC_OBJECT_INSTANCE_ID = "IC_OBJECT_INSTANCE_ID";
	private static final String VIEW_NAME = "V_IBPAGE_OBJECTS";
	private static final String DPT_PARENT_ID = "DPT_PARENT_ID";
	public String getEntityName() {
		// TODO Auto-generated method stub
		return VIEW_NAME;
	}

	/* (non-Javadoc)
	 * @see com.idega.data.GenericEntity#initializeAttributes()
	 */
	public void initializeAttributes() {
		addAttribute(IC_OBJECT_INSTANCE_ID, "Object Instance Id", true, true, java.lang.Integer.class);
		addAttribute(IC_OBJECT_ID, "Ojbect Id", true, true, java.lang.Integer.class);
		addAttribute(IB_PAGE_ID, "Page id", true, true, java.lang.Integer.class);
		addAttribute(OBJECT_NAME, "Object name", true, true, java.lang.String.class);
		addAttribute(CLASS_NAME, "Class name", true, true, java.lang.String.class);
		addAttribute(OBJECT_TYPE, "Object type", true, true, java.lang.String.class);
		addAttribute(BUNDLE, "Class name", true, true, java.lang.String.class);
		addAttribute(PAGE_NAME, "Class name", true, true, java.lang.String.class);
		addAttribute(FILE_ID, "Ojbect Id", true, true, java.lang.Integer.class);
		addAttribute(TEMPLATE_ID, "Page id", true, true, java.lang.Integer.class);
		addAttribute(DPT_PARENT_ID, "Dependant parent instance", true, true, java.lang.Integer.class);
		setAsPrimaryKey(IC_OBJECT_INSTANCE_ID,true);
	}

	/* (non-Javadoc)
	 * @see com.idega.data.IDOView#getCreationSQL()
	 */
	public String getCreationSQL() {
		StringBuffer sql = new StringBuffer();
		/* 
		 select i.ic_object_instance_id,i.ic_object_id,i.ib_page_id ,
o.object_name,o.class_name,o.object_type, o.bundle,
p.name pagename,p.file_id,p.template_id
from ic_object_instance i , ic_object o, ib_page p 
where o.ic_object_id = i.ic_object_id 
and p.ib_page_id = i.ib_page_id

		 */
		sql.append(" CREATE VIEW ").append(VIEW_NAME).append(" ( ");
		sql.append(IC_OBJECT_INSTANCE_ID).append(", ");
		sql.append( IC_OBJECT_ID).append(", ");
		sql.append( IB_PAGE_ID).append(", ");
		sql.append( DPT_PARENT_ID).append(", ");
		sql.append( OBJECT_NAME).append(", ");
		sql.append( CLASS_NAME).append(", ");
		sql.append( OBJECT_TYPE).append(", ");
		sql.append( BUNDLE).append(", ");
		sql.append( PAGE_NAME).append(", ");
		sql.append( FILE_ID).append(", ");
		sql.append( TEMPLATE_ID);
		sql.append("  ) AS ");
		sql.append("  select i.ic_object_instance_id,i.ic_object_id,i.ib_page_id,i.dpt_parent_id ,o.object_name,o.class_name,o.object_type, o.bundle,p.name pagename,p.file_id,p.template_id");
		sql.append("  from ic_object_instance i , ic_object o, ib_page p");
		sql.append("  where o.ic_object_id = i.ic_object_id");
		sql.append("  and p.ib_page_id = i.ib_page_id");
		return sql.toString();
	}
	
	public Integer getObjectInstanceId(){
		return getIntegerColumnValue(IC_OBJECT_INSTANCE_ID);
	}
	
	public Integer getObjectId(){
		return getIntegerColumnValue(IC_OBJECT_ID);
	}
	
	public Integer getPageId(){
		return getIntegerColumnValue(IB_PAGE_ID);
	}
	
	public Integer getFileId(){
		return getIntegerColumnValue(FILE_ID);
	}
	
	public Integer getTemplateId(){
		return getIntegerColumnValue(TEMPLATE_ID);
	}
	
	public Integer getDependantInstanceId(){
		return getIntegerColumnValue(DPT_PARENT_ID);
	}
	
	public String getPageName(){
		return getStringColumnValue(PAGE_NAME);
	}
	
	public String getObjectName(){
		return getStringColumnValue(OBJECT_NAME);
	}
	
	public String getClassName(){
		return getStringColumnValue(CLASS_NAME);
	}
	
	public String getObjectType(){
		return getStringColumnValue(OBJECT_TYPE);
	}
	
	public String getBundleName(){
		return getStringColumnValue(BUNDLE);
	}
	
	public Collection ejbFindByPage(Integer pageId) throws FinderException{
		return super.idoFindPKsByQuery(super.idoQueryGetSelect().appendWhereEquals(IB_PAGE_ID,pageId).appendOrderBy(IB_PAGE_ID));
	}
	
	public Collection ejbFindByPageAndObjectType(Integer pageId,String objectType) throws FinderException{
		return super.idoFindPKsByQuery(super.idoQueryGetSelect().appendWhereEquals(IB_PAGE_ID,pageId).appendAndEqualsQuoted(OBJECT_TYPE,objectType).appendOrderBy(IB_PAGE_ID));
	}
	
	public Collection ejbFindByPageName(String name)throws FinderException{
		return super.idoFindPKsByQuery(super.idoQueryGetSelect().appendWhereEqualsQuoted(PAGE_NAME,name).appendOrderBy(IB_PAGE_ID));
	}
	
	public Collection ejbFindByPageNameAndObjectType(String name,String objectType) throws FinderException{
		return super.idoFindPKsByQuery(super.idoQueryGetSelect().appendWhereEqualsQuoted(PAGE_NAME,name).appendAndEqualsQuoted(OBJECT_TYPE,objectType).appendOrderBy(IB_PAGE_ID));
	}
	
	public Collection ejbFindByBundle(String bundleName )throws FinderException{
		return super.idoFindPKsByQuery(super.idoQueryGetSelect().appendWhereEqualsQuoted(BUNDLE,bundleName).appendOrderBy(BUNDLE));
	}
	
	public Collection ejbFindByBundleAndObjectType(String name,String objectType) throws FinderException{
		return super.idoFindPKsByQuery(super.idoQueryGetSelect().appendWhereEqualsQuoted(BUNDLE,name).appendAndEqualsQuoted(OBJECT_TYPE,objectType).appendOrderBy(IB_PAGE_ID));
	}
	
	public Collection ejbFindByClassName(String className )throws FinderException{
		return super.idoFindPKsByQuery(super.idoQueryGetSelect().appendWhereEqualsQuoted(CLASS_NAME,className).appendOrderBy(BUNDLE));
	}
	
	public Collection ejbFindByClassNameAndObjectType(String name,String objectType) throws FinderException{
		return super.idoFindPKsByQuery(super.idoQueryGetSelect().appendWhereEqualsQuoted(CLASS_NAME,name).appendAndEqualsQuoted(OBJECT_TYPE,objectType).appendOrderBy(IB_PAGE_ID));
	}
	

}
