package com.idega.builder.data;


public interface IBDomainHome extends com.idega.data.IDOHome
{
 public IBDomain create() throws javax.ejb.CreateException;
 public IBDomain createLegacy();
 public IBDomain findByPrimaryKey(Object pk) throws javax.ejb.FinderException;
 public IBDomain findByPrimaryKey(int id) throws javax.ejb.FinderException;
 public IBDomain findByPrimaryKeyLegacy(int id) throws java.sql.SQLException;
 public java.util.Collection findAllDomains()throws javax.ejb.FinderException;

}