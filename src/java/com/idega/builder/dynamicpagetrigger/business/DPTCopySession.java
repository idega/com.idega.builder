package com.idega.builder.dynamicpagetrigger.business;


public interface DPTCopySession extends com.idega.business.IBOSession
{
 public void endCopySession(java.lang.String p0) throws java.rmi.RemoteException;
 public java.lang.String getNewValue(java.lang.String p0,java.lang.Class p1,java.lang.String p2) throws java.rmi.RemoteException;
 public void setNewValue(java.lang.String p0,java.lang.Class p1,java.lang.String p2,java.lang.String p3) throws java.rmi.RemoteException;
 public void startCopySession(java.lang.String p0) throws java.rmi.RemoteException;
}
