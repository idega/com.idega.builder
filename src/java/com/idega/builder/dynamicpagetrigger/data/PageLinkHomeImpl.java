package com.idega.builder.dynamicpagetrigger.data;


public class PageLinkHomeImpl extends com.idega.data.IDOFactory implements PageLinkHome
{
 protected Class getEntityInterfaceClass(){
  return PageLink.class;
 }


 public PageLink create() throws javax.ejb.CreateException{
  return (PageLink) super.createIDO();
 }


 public PageLink createLegacy(){
	try{
		return create();
	}
	catch(javax.ejb.CreateException ce){
		throw new RuntimeException("CreateException:"+ce.getMessage());
	}

 }


public java.util.Collection findAllByPageTriggerInfo(com.idega.builder.dynamicpagetrigger.data.PageTriggerInfo p0)throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((PageLinkBMPBean)entity).ejbFindAllByPageTriggerInfo(p0);
	this.idoCheckInPooledEntity(entity);
	return this.getEntityCollectionForPrimaryKeys(ids);
}

public java.util.Collection findAllByPageTriggerInfo(java.util.Collection p0)throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((PageLinkBMPBean)entity).ejbFindAllByPageTriggerInfo(p0);
	this.idoCheckInPooledEntity(entity);
	return this.getEntityCollectionForPrimaryKeys(ids);
}

public PageLink findByGroup(com.idega.user.data.Group p0)throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	Object pk = ((PageLinkBMPBean)entity).ejbFindByGroup(p0);
	this.idoCheckInPooledEntity(entity);
	return this.findByPrimaryKey(pk);
}

public PageLink findByRootPage(com.idega.core.builder.data.ICPage p0)throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	Object pk = ((PageLinkBMPBean)entity).ejbFindByRootPage(p0);
	this.idoCheckInPooledEntity(entity);
	return this.findByPrimaryKey(pk);
}

public PageLink findByRootPageID(int p0)throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	Object pk = ((PageLinkBMPBean)entity).ejbFindByRootPageID(p0);
	this.idoCheckInPooledEntity(entity);
	return this.findByPrimaryKey(pk);
}

 public PageLink findByPrimaryKey(Object pk) throws javax.ejb.FinderException{
  return (PageLink) super.findByPrimaryKeyIDO(pk);
 }


 public PageLink findByPrimaryKey(int id) throws javax.ejb.FinderException{
  return (PageLink) super.findByPrimaryKeyIDO(id);
 }


 public PageLink findByPrimaryKeyLegacy(int id) throws java.sql.SQLException{
	try{
		return findByPrimaryKey(id);
	}
	catch(javax.ejb.FinderException fe){
		throw new java.sql.SQLException(fe.getMessage());
	}

 }



}