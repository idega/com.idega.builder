package com.idega.builder.data;


public interface IBPageObjectViewHome extends com.idega.data.IDOHome
{
 public IBPageObjectView create() throws javax.ejb.CreateException;
 public IBPageObjectView findByPrimaryKey(Object pk) throws javax.ejb.FinderException;
 public java.util.Collection findByBundle(java.lang.String p0)throws javax.ejb.FinderException;
 public java.util.Collection findByBundleAndObjectType(java.lang.String p0,java.lang.String p1)throws javax.ejb.FinderException;
 public java.util.Collection findByClassName(java.lang.String p0)throws javax.ejb.FinderException;
 public java.util.Collection findByClassNameAndObjectType(java.lang.String p0,java.lang.String p1)throws javax.ejb.FinderException;
 public java.util.Collection findByPage(java.lang.Integer p0)throws javax.ejb.FinderException;
 public java.util.Collection findByPageAndObjectType(java.lang.Integer p0,java.lang.String p1)throws javax.ejb.FinderException;
 public java.util.Collection findByPageName(java.lang.String p0)throws javax.ejb.FinderException;
 public java.util.Collection findByPageNameAndObjectType(java.lang.String p0,java.lang.String p1)throws javax.ejb.FinderException;

}