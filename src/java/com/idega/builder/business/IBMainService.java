package com.idega.builder.business;


public interface IBMainService extends com.idega.business.IBOService,com.idega.core.builder.business.BuilderService
{
 public com.idega.core.builder.data.ICDomain getCurrentDomain()throws java.rmi.RemoteException, java.rmi.RemoteException;
 public com.idega.core.builder.data.ICPage getCurrentPage(com.idega.presentation.IWContext p0)throws java.rmi.RemoteException, java.rmi.RemoteException;
 public int getCurrentPageId(com.idega.presentation.IWContext p0)throws java.rmi.RemoteException, java.rmi.RemoteException;
 public java.lang.String getPageURI(int p0)throws java.rmi.RemoteException, java.rmi.RemoteException;
 public com.idega.core.builder.data.ICPage getRootPage()throws java.rmi.RemoteException, java.rmi.RemoteException;
 public int getRootPageId()throws java.rmi.RemoteException, java.rmi.RemoteException;
}
