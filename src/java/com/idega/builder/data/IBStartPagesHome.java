package com.idega.builder.data;


public interface IBStartPagesHome extends com.idega.data.IDOHome
{
 public IBStartPages create() throws javax.ejb.CreateException;
 public IBStartPages createLegacy();
 public IBStartPages findByPrimaryKey(Object pk) throws javax.ejb.FinderException;
 public IBStartPages findByPrimaryKey(int id) throws javax.ejb.FinderException;
 public IBStartPages findByPrimaryKeyLegacy(int id) throws java.sql.SQLException;
 public java.util.Collection findAllTemplatesByDomain(int p0)throws javax.ejb.FinderException;
 public java.util.Collection findAllPagesByDomain(int p0)throws javax.ejb.FinderException;

}