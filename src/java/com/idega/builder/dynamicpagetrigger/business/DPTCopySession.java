package com.idega.builder.dynamicpagetrigger.business;


public interface DPTCopySession extends com.idega.business.IBOSession
{
 public void collectDPTCrawlable(java.lang.Object p0,com.idega.builder.dynamicpagetrigger.util.DPTCrawlable p1) throws java.rmi.RemoteException;
 public boolean doCopyInstancePermissions() throws java.rmi.RemoteException;
 public boolean doCopyPagePermissions() throws java.rmi.RemoteException;
 public void endCopySession() throws java.rmi.RemoteException;
 public java.lang.Object getNewValue(java.lang.Class p0,java.lang.Object p1) throws java.rmi.RemoteException;
 public java.lang.Object getRootPagePrimaryKey() throws java.rmi.RemoteException;
 public boolean hasNextCollectedDPTCrawlable() throws java.rmi.RemoteException;
 public boolean isRunningSession() throws java.rmi.RemoteException;
 public com.idega.builder.dynamicpagetrigger.util.KeyAndValue nextCollectedDPTCrawlable() throws java.rmi.RemoteException;
 public void setNewValue(java.lang.Class p0,java.lang.Object p1,java.lang.Object p2) throws java.rmi.RemoteException;
 public void setRootPagePrimaryKey(java.lang.Object p0) throws java.rmi.RemoteException;
 public void setToCopyInstancePermissions(boolean p0) throws java.rmi.RemoteException;
 public void setToCopyPagePermissions(boolean p0) throws java.rmi.RemoteException;
 public void startCopySession()throws java.lang.Exception, java.rmi.RemoteException;
}
