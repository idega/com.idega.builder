package com.idega.builder.business;


public interface FileBusiness extends com.idega.business.IBOService,com.idega.io.serialization.ObjectWriter
{
 public com.idega.core.file.data.ICFile createFileFromInputStream(java.io.InputStream p0,java.lang.String p1,java.lang.String p2)throws java.io.IOException, java.rmi.RemoteException;
 public com.idega.builder.data.IBExportImportData getIBExportImportData(com.idega.io.UploadFile p0,boolean p1,int p2,int p3,com.idega.presentation.IWContext p4)throws java.io.IOException, java.rmi.RemoteException;
 public java.lang.String getURLForOfferingDownload(com.idega.io.serialization.Storable p0,com.idega.presentation.IWContext p1)throws java.io.IOException, java.rmi.RemoteException;
 public java.lang.Object write(java.io.File p0,com.idega.presentation.IWContext p1) throws java.rmi.RemoteException;
 public java.lang.Object write(com.idega.builder.data.IBExportImportData p0,com.idega.presentation.IWContext p1) throws java.rmi.RemoteException;
 public java.lang.Object write(com.idega.core.file.data.ICFile p0,com.idega.presentation.IWContext p1) throws java.rmi.RemoteException;
 public java.lang.Object write(com.idega.util.xml.XMLData p0,com.idega.presentation.IWContext p1) throws java.rmi.RemoteException;
}
