package com.idega.builder.dynamicpagetrigger.business;


public interface DPTCopySession extends com.idega.business.IBOSession
{
 public boolean doCopyInstancePermissions() throws java.rmi.RemoteException;
 public boolean doCopyPagePermissions() throws java.rmi.RemoteException;
 public void endCopySession() throws java.rmi.RemoteException;
 public java.lang.Object getNewValue(java.lang.Class p0,java.lang.Object p1) throws java.rmi.RemoteException;
 public boolean isRunningSession() throws java.rmi.RemoteException;
 public void setNewValue(java.lang.Class p0,java.lang.Object p1,java.lang.Object p2) throws java.rmi.RemoteException;
 public void setToCopyInstancePermissions(boolean p0) throws java.rmi.RemoteException;
 public void setToCopyPagePermissions(boolean p0) throws java.rmi.RemoteException;
 public void startCopySession()throws java.lang.Exception, java.rmi.RemoteException;
}
