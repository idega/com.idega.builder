package com.idega.builder.data;


public interface IBStartPageHome extends com.idega.data.IDOHome
{
 public IBStartPage create() throws javax.ejb.CreateException;
 public IBStartPage createLegacy();
 public IBStartPage findByPrimaryKey(Object pk) throws javax.ejb.FinderException;
 public IBStartPage findByPrimaryKey(int id) throws javax.ejb.FinderException;
 public IBStartPage findByPrimaryKeyLegacy(int id) throws java.sql.SQLException;
 public java.util.Collection findAllTemplatesByDomain(int p0)throws javax.ejb.FinderException;
 public java.util.Collection findAllPagesByDomain(int p0)throws javax.ejb.FinderException;

}