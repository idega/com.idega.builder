package com.idega.builder.dynamicpagetrigger.data;


public class PageLinkHomeImpl extends com.idega.data.IDOFactory implements PageLinkHome
{
 protected Class getEntityInterfaceClass(){
  return PageLink.class;
 }

 public PageLink create() throws javax.ejb.CreateException{
  return (PageLink) super.idoCreate();
 }

 public PageLink createLegacy(){
	try{
		return create();
	}
	catch(javax.ejb.CreateException ce){
		throw new RuntimeException("CreateException:"+ce.getMessage());
	}

 }

 public PageLink findByPrimaryKey(int id) throws javax.ejb.FinderException{
  return (PageLink) super.idoFindByPrimaryKey(id);
 }

 public PageLink findByPrimaryKey(Object pk) throws javax.ejb.FinderException{
  return (PageLink) super.idoFindByPrimaryKey(pk);
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