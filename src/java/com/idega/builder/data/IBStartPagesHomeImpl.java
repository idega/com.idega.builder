package com.idega.builder.data;


public class IBStartPagesHomeImpl extends com.idega.data.IDOFactory implements IBStartPagesHome
{
 protected Class getEntityInterfaceClass(){
  return IBStartPages.class;
 }


 public IBStartPages create() throws javax.ejb.CreateException{
  return (IBStartPages) super.createIDO();
 }


 public IBStartPages createLegacy(){
	try{
		return create();
	}
	catch(javax.ejb.CreateException ce){
		throw new RuntimeException("CreateException:"+ce.getMessage());
	}

 }


public java.util.Collection findAllTemplatesByDomain(int p0)throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((IBStartPagesBMPBean)entity).ejbFindAllTemplatesByDomain(p0);
	this.idoCheckInPooledEntity(entity);
	return this.getEntityCollectionForPrimaryKeys(ids);
}

public java.util.Collection findAllPagesByDomain(int p0)throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((IBStartPagesBMPBean)entity).ejbFindAllPagesByDomain(p0);
	this.idoCheckInPooledEntity(entity);
	return this.getEntityCollectionForPrimaryKeys(ids);
}

 public IBStartPages findByPrimaryKey(Object pk) throws javax.ejb.FinderException{
  return (IBStartPages) super.findByPrimaryKeyIDO(pk);
 }


 public IBStartPages findByPrimaryKey(int id) throws javax.ejb.FinderException{
  return (IBStartPages) super.findByPrimaryKeyIDO(id);
 }


 public IBStartPages findByPrimaryKeyLegacy(int id) throws java.sql.SQLException{
	try{
		return findByPrimaryKey(id);
	}
	catch(javax.ejb.FinderException fe){
		throw new java.sql.SQLException(fe.getMessage());
	}

 }



}