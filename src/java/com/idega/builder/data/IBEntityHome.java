package com.idega.builder.data;


public interface IBEntityHome extends com.idega.data.IDOHome
{
 public IBEntity create() throws javax.ejb.CreateException;
 public IBEntity createLegacy();
 public IBEntity findByPrimaryKey(int id) throws javax.ejb.FinderException;
 public IBEntity findByPrimaryKey(Object pk) throws javax.ejb.FinderException;
 public IBEntity findByPrimaryKeyLegacy(int id) throws java.sql.SQLException;

}