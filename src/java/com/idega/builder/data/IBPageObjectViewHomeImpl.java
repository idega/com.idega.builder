package com.idega.builder.data;


public class IBPageObjectViewHomeImpl extends com.idega.data.IDOFactory implements IBPageObjectViewHome
{
 protected Class getEntityInterfaceClass(){
  return IBPageObjectView.class;
 }


 public IBPageObjectView create() throws javax.ejb.CreateException{
  return (IBPageObjectView) super.createIDO();
 }


public java.util.Collection findByBundle(java.lang.String p0)throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((IBPageObjectViewBMPBean)entity).ejbFindByBundle(p0);
	this.idoCheckInPooledEntity(entity);
	return this.getEntityCollectionForPrimaryKeys(ids);
}

public java.util.Collection findByBundleAndObjectType(java.lang.String p0,java.lang.String p1)throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((IBPageObjectViewBMPBean)entity).ejbFindByBundleAndObjectType(p0,p1);
	this.idoCheckInPooledEntity(entity);
	return this.getEntityCollectionForPrimaryKeys(ids);
}

public java.util.Collection findByClassName(java.lang.String p0)throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((IBPageObjectViewBMPBean)entity).ejbFindByClassName(p0);
	this.idoCheckInPooledEntity(entity);
	return this.getEntityCollectionForPrimaryKeys(ids);
}

public java.util.Collection findByClassNameAndObjectType(java.lang.String p0,java.lang.String p1)throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((IBPageObjectViewBMPBean)entity).ejbFindByClassNameAndObjectType(p0,p1);
	this.idoCheckInPooledEntity(entity);
	return this.getEntityCollectionForPrimaryKeys(ids);
}

public java.util.Collection findByPage(java.lang.Integer p0)throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((IBPageObjectViewBMPBean)entity).ejbFindByPage(p0);
	this.idoCheckInPooledEntity(entity);
	return this.getEntityCollectionForPrimaryKeys(ids);
}

public java.util.Collection findByPageAndObjectType(java.lang.Integer p0,java.lang.String p1)throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((IBPageObjectViewBMPBean)entity).ejbFindByPageAndObjectType(p0,p1);
	this.idoCheckInPooledEntity(entity);
	return this.getEntityCollectionForPrimaryKeys(ids);
}

public java.util.Collection findByPageName(java.lang.String p0)throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((IBPageObjectViewBMPBean)entity).ejbFindByPageName(p0);
	this.idoCheckInPooledEntity(entity);
	return this.getEntityCollectionForPrimaryKeys(ids);
}

public java.util.Collection findByPageNameAndObjectType(java.lang.String p0,java.lang.String p1)throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((IBPageObjectViewBMPBean)entity).ejbFindByPageNameAndObjectType(p0,p1);
	this.idoCheckInPooledEntity(entity);
	return this.getEntityCollectionForPrimaryKeys(ids);
}

 public IBPageObjectView findByPrimaryKey(Object pk) throws javax.ejb.FinderException{
  return (IBPageObjectView) super.findByPrimaryKeyIDO(pk);
 }



}