package com.idega.builder.data;


public class IBEntityHomeImpl extends com.idega.data.IDOFactory implements IBEntityHome
{
 protected Class getEntityInterfaceClass(){
  return IBEntity.class;
 }

 public IBEntity create() throws javax.ejb.CreateException{
  return (IBEntity) super.idoCreate();
 }

 public IBEntity createLegacy(){
	try{
		return create();
	}
	catch(javax.ejb.CreateException ce){
		throw new RuntimeException("CreateException:"+ce.getMessage());
	}

 }

 public IBEntity findByPrimaryKey(int id) throws javax.ejb.FinderException{
  return (IBEntity) super.idoFindByPrimaryKey(id);
 }

 public IBEntity findByPrimaryKey(Object pk) throws javax.ejb.FinderException{
  return (IBEntity) super.idoFindByPrimaryKey(pk);
 }

 public IBEntity findByPrimaryKeyLegacy(int id) throws java.sql.SQLException{
	try{
		return findByPrimaryKey(id);
	}
	catch(javax.ejb.FinderException fe){
		throw new java.sql.SQLException(fe.getMessage());
	}

 }


}