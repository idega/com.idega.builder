package com.idega.builder.data;


public class IBObjectLibraryHomeImpl extends com.idega.data.IDOFactory implements IBObjectLibraryHome
{
 protected Class getEntityInterfaceClass(){
  return IBObjectLibrary.class;
 }

 public IBObjectLibrary create() throws javax.ejb.CreateException{
  return (IBObjectLibrary) super.idoCreate();
 }

 public IBObjectLibrary createLegacy(){
	try{
		return create();
	}
	catch(javax.ejb.CreateException ce){
		throw new RuntimeException("CreateException:"+ce.getMessage());
	}

 }

 public IBObjectLibrary findByPrimaryKey(int id) throws javax.ejb.FinderException{
  return (IBObjectLibrary) super.idoFindByPrimaryKey(id);
 }

 public IBObjectLibrary findByPrimaryKey(Object pk) throws javax.ejb.FinderException{
  return (IBObjectLibrary) super.idoFindByPrimaryKey(pk);
 }

 public IBObjectLibrary findByPrimaryKeyLegacy(int id) throws java.sql.SQLException{
	try{
		return findByPrimaryKey(id);
	}
	catch(javax.ejb.FinderException fe){
		throw new java.sql.SQLException(fe.getMessage());
	}

 }


}