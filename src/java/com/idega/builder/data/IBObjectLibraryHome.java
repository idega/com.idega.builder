package com.idega.builder.data;


public interface IBObjectLibraryHome extends com.idega.data.IDOHome
{
 public IBObjectLibrary create() throws javax.ejb.CreateException;
 public IBObjectLibrary createLegacy();
 public IBObjectLibrary findByPrimaryKey(int id) throws javax.ejb.FinderException;
 public IBObjectLibrary findByPrimaryKey(Object pk) throws javax.ejb.FinderException;
 public IBObjectLibrary findByPrimaryKeyLegacy(int id) throws java.sql.SQLException;

}