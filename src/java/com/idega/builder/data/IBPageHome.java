package com.idega.builder.data;


public interface IBPageHome extends com.idega.data.IDOHome
{
 public IBPage create() throws javax.ejb.CreateException;
 public IBPage createLegacy();
 public IBPage findByPrimaryKey(int id) throws javax.ejb.FinderException;
 public IBPage findByPrimaryKey(Object pk) throws javax.ejb.FinderException;
 public IBPage findByPrimaryKeyLegacy(int id) throws java.sql.SQLException;

}