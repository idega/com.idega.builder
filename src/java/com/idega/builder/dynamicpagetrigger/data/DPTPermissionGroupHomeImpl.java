package com.idega.builder.dynamicpagetrigger.data;


public class DPTPermissionGroupHomeImpl extends com.idega.data.IDOFactory implements DPTPermissionGroupHome
{
 protected Class getEntityInterfaceClass(){
  return DPTPermissionGroup.class;
 }

 public DPTPermissionGroup create() throws javax.ejb.CreateException{
  return (DPTPermissionGroup) super.idoCreate();
 }

 public DPTPermissionGroup createLegacy(){
	try{
		return create();
	}
	catch(javax.ejb.CreateException ce){
		throw new RuntimeException("CreateException:"+ce.getMessage());
	}

 }

 public DPTPermissionGroup findByPrimaryKey(int id) throws javax.ejb.FinderException{
  return (DPTPermissionGroup) super.idoFindByPrimaryKey(id);
 }

 public DPTPermissionGroup findByPrimaryKey(Object pk) throws javax.ejb.FinderException{
  return (DPTPermissionGroup) super.idoFindByPrimaryKey(pk);
 }

 public DPTPermissionGroup findByPrimaryKeyLegacy(int id) throws java.sql.SQLException{
	try{
		return findByPrimaryKey(id);
	}
	catch(javax.ejb.FinderException fe){
		throw new java.sql.SQLException(fe.getMessage());
	}

 }


}