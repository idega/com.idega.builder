package com.idega.builder.business;


public interface FileBusiness extends com.idega.business.IBOService,com.idega.io.ObjectWriter
{
 public com.idega.builder.data.IBExportImportData getIBExportImportData(com.idega.io.UploadFile p0)throws java.io.IOException, java.rmi.RemoteException;
 public java.lang.String getURLForOfferingDownload(com.idega.io.Storable p0)throws java.io.IOException, java.rmi.RemoteException;
 public java.lang.Object write(com.idega.core.file.data.ICFile p0) throws java.rmi.RemoteException;
 public java.lang.Object write(com.idega.builder.data.IBExportImportData p0) throws java.rmi.RemoteException;
 public java.lang.Object write(com.idega.util.xml.XMLData p0) throws java.rmi.RemoteException;
}
