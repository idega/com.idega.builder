package com.idega.builder.data;


public class IBObjectHomeImpl extends com.idega.data.IDOFactory implements IBObjectHome
{
 protected Class getEntityInterfaceClass(){
  return IBObject.class;
 }

 public IBObject create() throws javax.ejb.CreateException{
  return (IBObject) super.idoCreate();
 }

 public IBObject createLegacy(){
	try{
		return create();
	}
	catch(javax.ejb.CreateException ce){
		throw new RuntimeException("CreateException:"+ce.getMessage());
	}

 }

 public IBObject findByPrimaryKey(int id) throws javax.ejb.FinderException{
  return (IBObject) super.idoFindByPrimaryKey(id);
 }

 public IBObject findByPrimaryKey(Object pk) throws javax.ejb.FinderException{
  return (IBObject) super.idoFindByPrimaryKey(pk);
 }

 public IBObject findByPrimaryKeyLegacy(int id) throws java.sql.SQLException{
	try{
		return findByPrimaryKey(id);
	}
	catch(javax.ejb.FinderException fe){
		throw new java.sql.SQLException(fe.getMessage());
	}

 }


}