package com.idega.builder.dynamicpagetrigger.data;


public interface DPTDomainHome extends com.idega.data.IDOHome
{
 public DPTDomain create() throws javax.ejb.CreateException;
 public DPTDomain createLegacy();
 public DPTDomain findByPrimaryKey(int id) throws javax.ejb.FinderException;
 public DPTDomain findByPrimaryKey(Object pk) throws javax.ejb.FinderException;
 public DPTDomain findByPrimaryKeyLegacy(int id) throws java.sql.SQLException;

}