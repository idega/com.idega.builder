package com.idega.builder.data;


public interface IBPageNameHome extends com.idega.data.IDOHome
{
 public IBPageName create() throws javax.ejb.CreateException;
 public IBPageName findByPrimaryKey(Object pk) throws javax.ejb.FinderException;
 public java.util.Collection findAll()throws javax.ejb.FinderException;
 public java.util.Collection findAllByPageIdAndLocaleId(int p0,int p1)throws javax.ejb.FinderException;

}