package com.idega.builder.dynamicpagetrigger.data;


public class DPTDomainHomeImpl extends com.idega.data.IDOFactory implements DPTDomainHome
{
 protected Class getEntityInterfaceClass(){
  return DPTDomain.class;
 }

 public DPTDomain create() throws javax.ejb.CreateException{
  return (DPTDomain) super.idoCreate();
 }

 public DPTDomain createLegacy(){
	try{
		return create();
	}
	catch(javax.ejb.CreateException ce){
		throw new RuntimeException("CreateException:"+ce.getMessage());
	}

 }

 public DPTDomain findByPrimaryKey(int id) throws javax.ejb.FinderException{
  return (DPTDomain) super.idoFindByPrimaryKey(id);
 }

 public DPTDomain findByPrimaryKey(Object pk) throws javax.ejb.FinderException{
  return (DPTDomain) super.idoFindByPrimaryKey(pk);
 }

 public DPTDomain findByPrimaryKeyLegacy(int id) throws java.sql.SQLException{
	try{
		return findByPrimaryKey(id);
	}
	catch(javax.ejb.FinderException fe){
		throw new java.sql.SQLException(fe.getMessage());
	}

 }


}