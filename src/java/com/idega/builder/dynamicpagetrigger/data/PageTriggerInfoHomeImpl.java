package com.idega.builder.dynamicpagetrigger.data;


public class PageTriggerInfoHomeImpl extends com.idega.data.IDOFactory implements PageTriggerInfoHome
{
 protected Class getEntityInterfaceClass(){
  return PageTriggerInfo.class;
 }

 public PageTriggerInfo create() throws javax.ejb.CreateException{
  return (PageTriggerInfo) super.idoCreate();
 }

 public PageTriggerInfo createLegacy(){
	try{
		return create();
	}
	catch(javax.ejb.CreateException ce){
		throw new RuntimeException("CreateException:"+ce.getMessage());
	}

 }

 public PageTriggerInfo findByPrimaryKey(int id) throws javax.ejb.FinderException{
  return (PageTriggerInfo) super.idoFindByPrimaryKey(id);
 }

 public PageTriggerInfo findByPrimaryKey(Object pk) throws javax.ejb.FinderException{
  return (PageTriggerInfo) super.idoFindByPrimaryKey(pk);
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