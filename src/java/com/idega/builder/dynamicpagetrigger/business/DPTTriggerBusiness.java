package com.idega.builder.dynamicpagetrigger.business;


public interface DPTTriggerBusiness extends com.idega.business.IBOService
{
 public boolean addObjectInstancToSubPages(int p0,com.idega.idegaweb.IWUserContext p1)throws java.sql.SQLException, java.rmi.RemoteException;
 public boolean addObjectInstancToSubPages(com.idega.core.component.data.ICObjectInstance p0,com.idega.idegaweb.IWUserContext p1) throws java.rmi.RemoteException;
 public void addRuleToInstance(com.idega.core.component.data.ICObjectInstance p0,int p1)throws java.sql.SQLException, java.rmi.RemoteException;
 public void addRuleToInstance(com.idega.builder.dynamicpagetrigger.data.PageTriggerInfo p0,int p1)throws java.sql.SQLException, java.rmi.RemoteException;
 public void addTemplateToRule(com.idega.builder.dynamicpagetrigger.data.PageTriggerInfo p0,int p1)throws java.sql.SQLException, java.rmi.RemoteException;
 public void addTemplateToRule(com.idega.core.builder.data.ICPage p0,int p1)throws java.sql.SQLException, java.rmi.RemoteException;
 public void copyInstancePermissions(java.lang.String p0,java.lang.String p1)throws java.sql.SQLException, java.rmi.RemoteException;
 public void copyPagePermissions(java.lang.String p0,java.lang.String p1)throws java.sql.SQLException, java.rmi.RemoteException;
 public com.idega.builder.dynamicpagetrigger.data.PageLink createPageLink(com.idega.presentation.IWContext p0,com.idega.builder.dynamicpagetrigger.data.PageTriggerInfo p1,java.lang.String p2,java.lang.String p3,java.lang.String p4,java.lang.Integer p5,java.lang.Integer p6,java.lang.Integer p7)throws java.lang.Exception, java.rmi.RemoteException;
 public com.idega.builder.dynamicpagetrigger.data.PageLink createPageLink(com.idega.presentation.IWContext p0,com.idega.builder.dynamicpagetrigger.data.PageTriggerInfo p1,java.lang.String p2,java.lang.String p3,com.idega.user.data.Group p4,java.lang.String p5,java.lang.Integer p6,java.lang.Integer p7,java.lang.Integer p8)throws java.lang.Exception, java.rmi.RemoteException;
 public int createTriggerRule(com.idega.core.component.data.ICObject p0,int p1,int p2,int[] p3,com.idega.core.builder.data.ICPage[] p4)throws java.sql.SQLException, java.rmi.RemoteException;
 public java.util.List getDPTPermissionGroups(com.idega.builder.dynamicpagetrigger.data.PageTriggerInfo p0)throws java.sql.SQLException, java.rmi.RemoteException;
 public com.idega.user.data.Group getOwnerGroupFromRootPageID(int p0) throws java.rmi.RemoteException;
 public java.util.List getPageLinkRecords(com.idega.core.component.data.ICObjectInstance p0)throws java.sql.SQLException, java.rmi.RemoteException;
 public boolean invalidatePageLink(com.idega.presentation.IWContext p0,com.idega.builder.dynamicpagetrigger.data.PageLink p1,int p2) throws java.rmi.RemoteException;
 public void removeRuleFromInstance(com.idega.core.component.data.ICObjectInstance p0,int p1)throws java.sql.SQLException, java.rmi.RemoteException;
 public void removeRuleFromInstance(com.idega.builder.dynamicpagetrigger.data.PageTriggerInfo p0,int p1)throws java.sql.SQLException, java.rmi.RemoteException;
 public void removeTemplateFromRule(com.idega.builder.dynamicpagetrigger.data.PageTriggerInfo p0,int p1)throws java.sql.SQLException, java.rmi.RemoteException;
 public void removeTemplateFromRule(com.idega.core.builder.data.ICPage p0,int p1)throws java.sql.SQLException, java.rmi.RemoteException;
}
