package com.idega.builder.dynamicpagetrigger.data;


public interface PageTriggerInfoHome extends com.idega.data.IDOHome
{
 public PageTriggerInfo create() throws javax.ejb.CreateException;
 public PageTriggerInfo createLegacy();
 public PageTriggerInfo findByPrimaryKey(Object pk) throws javax.ejb.FinderException;
 public PageTriggerInfo findByPrimaryKey(int id) throws javax.ejb.FinderException;
 public PageTriggerInfo findByPrimaryKeyLegacy(int id) throws java.sql.SQLException;
 public java.util.Collection findAllByICObjectID(com.idega.core.component.data.ICObject p0)throws javax.ejb.FinderException;

}