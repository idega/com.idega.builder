package com.idega.builder.business;


public interface IBPageImportBusiness extends com.idega.business.IBOService
{
 public com.idega.util.datastructures.MessageContainer importPages(com.idega.io.UploadFile p0,boolean p1,int p2,int p3,com.idega.idegaweb.IWUserContext p4)throws java.rmi.RemoteException,java.io.IOException, java.rmi.RemoteException;
}
