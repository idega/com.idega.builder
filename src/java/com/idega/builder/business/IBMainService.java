package com.idega.builder.business;


public interface IBMainService extends com.idega.business.IBOService,com.idega.core.builder.business.BuilderService
{
 public com.idega.builder.data.IBDomain getCurrentDomain()throws java.rmi.RemoteException, java.rmi.RemoteException;
 public com.idega.builder.data.IBPage getCurrentPage(com.idega.presentation.IWContext p0)throws java.rmi.RemoteException, java.rmi.RemoteException;
 public int getCurrentPageId(com.idega.presentation.IWContext p0)throws java.rmi.RemoteException, java.rmi.RemoteException;
 public java.lang.String getPageURI(int p0)throws java.rmi.RemoteException, java.rmi.RemoteException;
 public com.idega.builder.data.IBPage getRootPage()throws java.rmi.RemoteException, java.rmi.RemoteException;
 public int getRootPageId()throws java.rmi.RemoteException, java.rmi.RemoteException;
}
