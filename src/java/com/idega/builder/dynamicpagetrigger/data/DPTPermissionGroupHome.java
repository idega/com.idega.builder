package com.idega.builder.dynamicpagetrigger.data;


public interface DPTPermissionGroupHome extends com.idega.data.IDOHome
{
 public DPTPermissionGroup create() throws javax.ejb.CreateException;
 public DPTPermissionGroup createLegacy();
 public DPTPermissionGroup findByPrimaryKey(int id) throws javax.ejb.FinderException;
 public DPTPermissionGroup findByPrimaryKey(Object pk) throws javax.ejb.FinderException;
 public DPTPermissionGroup findByPrimaryKeyLegacy(int id) throws java.sql.SQLException;

}