package com.idega.builder.data;


public interface IBJspPageHome extends com.idega.data.IDOHome
{
 public IBJspPage create() throws javax.ejb.CreateException;
 public IBJspPage createLegacy();
 public IBJspPage findByPrimaryKey(int id) throws javax.ejb.FinderException;
 public IBJspPage findByPrimaryKey(Object pk) throws javax.ejb.FinderException;
 public IBJspPage findByPrimaryKeyLegacy(int id) throws java.sql.SQLException;

}