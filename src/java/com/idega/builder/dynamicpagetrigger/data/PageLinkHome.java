package com.idega.builder.dynamicpagetrigger.data;


public interface PageLinkHome extends com.idega.data.IDOHome
{
 public PageLink create() throws javax.ejb.CreateException;
 public PageLink createLegacy();
 public PageLink findByPrimaryKey(Object pk) throws javax.ejb.FinderException;
 public PageLink findByPrimaryKey(int id) throws javax.ejb.FinderException;
 public PageLink findByPrimaryKeyLegacy(int id) throws java.sql.SQLException;
 public java.util.Collection findAllByPageTriggerInfo(com.idega.builder.dynamicpagetrigger.data.PageTriggerInfo p0)throws javax.ejb.FinderException;
 public java.util.Collection findAllByPageTriggerInfo(java.util.Collection p0)throws javax.ejb.FinderException;
 public PageLink findByGroup(com.idega.user.data.Group p0)throws javax.ejb.FinderException;
 public PageLink findByRootPage(com.idega.core.builder.data.ICPage p0)throws javax.ejb.FinderException;
 public PageLink findByRootPageID(int p0)throws javax.ejb.FinderException;

}