package com.idega.builder.dynamicpagetrigger.data;


public interface PageLinkHome extends com.idega.data.IDOHome
{
 public PageLink create() throws javax.ejb.CreateException;
 public PageLink createLegacy();
 public PageLink findByPrimaryKey(int id) throws javax.ejb.FinderException;
 public PageLink findByPrimaryKey(Object pk) throws javax.ejb.FinderException;
 public PageLink findByPrimaryKeyLegacy(int id) throws java.sql.SQLException;

}