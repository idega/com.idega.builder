package com.idega.builder.data;


public class IBStartPageHomeImpl extends com.idega.data.IDOFactory implements IBStartPageHome
{
 protected Class getEntityInterfaceClass(){
  return IBStartPage.class;
 }


 public IBStartPage create() throws javax.ejb.CreateException{
  return (IBStartPage) super.createIDO();
 }


 public IBStartPage createLegacy(){
	try{
		return create();
	}
	catch(javax.ejb.CreateException ce){
		throw new RuntimeException("CreateException:"+ce.getMessage());
	}

 }


public java.util.Collection findAllTemplatesByDomain(int p0)throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((IBStartPageBMPBean)entity).ejbFindAllTemplatesByDomain(p0);
	this.idoCheckInPooledEntity(entity);
	return this.getEntityCollectionForPrimaryKeys(ids);
}

public java.util.Collection findAllPagesByDomain(int p0)throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((IBStartPageBMPBean)entity).ejbFindAllPagesByDomain(p0);
	this.idoCheckInPooledEntity(entity);
	return this.getEntityCollectionForPrimaryKeys(ids);
}

 public IBStartPage findByPrimaryKey(Object pk) throws javax.ejb.FinderException{
  return (IBStartPage) super.findByPrimaryKeyIDO(pk);
 }


 public IBStartPage findByPrimaryKey(int id) throws javax.ejb.FinderException{
  return (IBStartPage) super.findByPrimaryKeyIDO(id);
 }


 public IBStartPage findByPrimaryKeyLegacy(int id) throws java.sql.SQLException{
	try{
		return findByPrimaryKey(id);
	}
	catch(javax.ejb.FinderException fe){
		throw new java.sql.SQLException(fe.getMessage());
	}

 }



}