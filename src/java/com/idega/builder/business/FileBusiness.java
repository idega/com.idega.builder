package com.idega.builder.business;


public interface FileBusiness extends com.idega.business.IBOService,com.idega.io.Writer
{
 public java.lang.String getURLForOfferingDownload(com.idega.io.Storable p0)throws java.io.IOException, java.rmi.RemoteException;
 public java.lang.String getURLForOfferingDownload(java.lang.String p0,java.util.List p1)throws java.io.IOException, java.rmi.RemoteException;
}
