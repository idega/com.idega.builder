package com.idega.builder.data;


public class IBDomainHomeImpl extends com.idega.data.IDOFactory implements IBDomainHome
{
 protected Class getEntityInterfaceClass(){
  return IBDomain.class;
 }

 public IBDomain create() throws javax.ejb.CreateException{
  return (IBDomain) super.idoCreate();
 }

 public IBDomain createLegacy(){
	try{
		return create();
	}
	catch(javax.ejb.CreateException ce){
		throw new RuntimeException("CreateException:"+ce.getMessage());
	}

 }

 public IBDomain findByPrimaryKey(int id) throws javax.ejb.FinderException{
  return (IBDomain) super.idoFindByPrimaryKey(id);
 }

 public IBDomain findByPrimaryKey(Object pk) throws javax.ejb.FinderException{
  return (IBDomain) super.idoFindByPrimaryKey(pk);
 }

 public IBDomain findByPrimaryKeyLegacy(int id) throws java.sql.SQLException{
	try{
		return findByPrimaryKey(id);
	}
	catch(javax.ejb.FinderException fe){
		throw new java.sql.SQLException(fe.getMessage());
	}

 }


}