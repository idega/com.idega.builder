package com.idega.builder.dynamicpagetrigger.data;


public class PageTriggerInfoHomeImpl extends com.idega.data.IDOFactory implements PageTriggerInfoHome
{
 protected Class getEntityInterfaceClass(){
  return PageTriggerInfo.class;
 }


 public PageTriggerInfo create() throws javax.ejb.CreateException{
  return (PageTriggerInfo) super.createIDO();
 }


 public PageTriggerInfo createLegacy(){
	try{
		return create();
	}
	catch(javax.ejb.CreateException ce){
		throw new RuntimeException("CreateException:"+ce.getMessage());
	}

 }


public java.util.Collection findAllByICObjectID(com.idega.core.component.data.ICObject p0)throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((PageTriggerInfoBMPBean)entity).ejbFindAllByICObjectID(p0);
	this.idoCheckInPooledEntity(entity);
	return this.getEntityCollectionForPrimaryKeys(ids);
}

 public PageTriggerInfo findByPrimaryKey(Object pk) throws javax.ejb.FinderException{
  return (PageTriggerInfo) super.findByPrimaryKeyIDO(pk);
 }


 public PageTriggerInfo findByPrimaryKey(int id) throws javax.ejb.FinderException{
  return (PageTriggerInfo) super.findByPrimaryKeyIDO(id);
 }


 public PageTriggerInfo findByPrimaryKeyLegacy(int id) throws java.sql.SQLException{
	try{
		return findByPrimaryKey(id);
	}
	catch(javax.ejb.FinderException fe){
		throw new java.sql.SQLException(fe.getMessage());
	}

 }



}