package com.idega.builder.data;


public class IBPageHomeImpl extends com.idega.data.IDOFactory implements IBPageHome
{
 protected Class getEntityInterfaceClass(){
  return IBPage.class;
 }

 public IBPage create() throws javax.ejb.CreateException{
  return (IBPage) super.idoCreate();
 }

 public IBPage createLegacy(){
	try{
		return create();
	}
	catch(javax.ejb.CreateException ce){
		throw new RuntimeException("CreateException:"+ce.getMessage());
	}

 }

 public IBPage findByPrimaryKey(int id) throws javax.ejb.FinderException{
  return (IBPage) super.idoFindByPrimaryKey(id);
 }

 public IBPage findByPrimaryKey(Object pk) throws javax.ejb.FinderException{
  return (IBPage) super.idoFindByPrimaryKey(pk);
 }

 public IBPage findByPrimaryKeyLegacy(int id) throws java.sql.SQLException{
	try{
		return findByPrimaryKey(id);
	}
	catch(javax.ejb.FinderException fe){
		throw new java.sql.SQLException(fe.getMessage());
	}

 }


}