package com.idega.builder.data;

import com.idega.core.builder.data.ICPage;
import com.idega.core.builder.data.ICPageHome;


public class IBPageHomeImpl extends com.idega.data.IDOFactory implements ICPageHome
{
 protected Class getEntityInterfaceClass(){
  return ICPage.class;
 }

 public ICPage create() throws javax.ejb.CreateException{
  return (ICPage) super.idoCreate();
 }

 public ICPage createLegacy(){
	try{
		return create();
	}
	catch(javax.ejb.CreateException ce){
		throw new RuntimeException("CreateException:"+ce.getMessage());
	}

 }

 public ICPage findByPrimaryKey(int id) throws javax.ejb.FinderException{
  return (ICPage) super.idoFindByPrimaryKey(id);
 }

 public ICPage findByPrimaryKey(Object pk) throws javax.ejb.FinderException{
  return (ICPage) super.idoFindByPrimaryKey(pk);
 }

 public ICPage findByPrimaryKeyLegacy(int id) throws java.sql.SQLException{
	try{
		return findByPrimaryKey(id);
	}
	catch(javax.ejb.FinderException fe){
		throw new java.sql.SQLException(fe.getMessage());
	}

 }


}