package com.idega.builder.data;


public class IBPageNameHomeImpl extends com.idega.data.IDOFactory implements IBPageNameHome
{
 protected Class getEntityInterfaceClass(){
  return IBPageName.class;
 }


 public IBPageName create() throws javax.ejb.CreateException{
  return (IBPageName) super.createIDO();
 }


public java.util.Collection findAll()throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((IBPageNameBMPBean)entity).ejbFindAll();
	this.idoCheckInPooledEntity(entity);
	return this.getEntityCollectionForPrimaryKeys(ids);
}

public java.util.Collection findAllByPageIdAndLocaleId(int p0,int p1)throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((IBPageNameBMPBean)entity).ejbFindAllByPageIdAndLocaleId(p0,p1);
	this.idoCheckInPooledEntity(entity);
	return this.getEntityCollectionForPrimaryKeys(ids);
}

 public IBPageName findByPrimaryKey(Object pk) throws javax.ejb.FinderException{
  return (IBPageName) super.findByPrimaryKeyIDO(pk);
 }



}