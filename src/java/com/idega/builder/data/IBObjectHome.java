package com.idega.builder.data;


public interface IBObjectHome extends com.idega.data.IDOHome
{
 public IBObject create() throws javax.ejb.CreateException;
 public IBObject createLegacy();
 public IBObject findByPrimaryKey(int id) throws javax.ejb.FinderException;
 public IBObject findByPrimaryKey(Object pk) throws javax.ejb.FinderException;
 public IBObject findByPrimaryKeyLegacy(int id) throws java.sql.SQLException;

}