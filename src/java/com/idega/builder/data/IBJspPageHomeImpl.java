package com.idega.builder.data;


public class IBJspPageHomeImpl extends com.idega.data.IDOFactory implements IBJspPageHome
{
 protected Class getEntityInterfaceClass(){
  return IBJspPage.class;
 }

 public IBJspPage create() throws javax.ejb.CreateException{
  return (IBJspPage) super.idoCreate();
 }

 public IBJspPage createLegacy(){
	try{
		return create();
	}
	catch(javax.ejb.CreateException ce){
		throw new RuntimeException("CreateException:"+ce.getMessage());
	}

 }

 public IBJspPage findByPrimaryKey(int id) throws javax.ejb.FinderException{
  return (IBJspPage) super.idoFindByPrimaryKey(id);
 }

 public IBJspPage findByPrimaryKey(Object pk) throws javax.ejb.FinderException{
  return (IBJspPage) super.idoFindByPrimaryKey(pk);
 }

 public IBJspPage findByPrimaryKeyLegacy(int id) throws java.sql.SQLException{
	try{
		return findByPrimaryKey(id);
	}
	catch(javax.ejb.FinderException fe){
		throw new java.sql.SQLException(fe.getMessage());
	}

 }


}