package com.idega.builder.business;


public interface FileBusiness extends com.idega.business.IBOService,com.idega.io.ObjectWriter
{
 public java.lang.String getURLForOfferingDownload(com.idega.io.Storable p0)throws java.io.IOException, java.rmi.RemoteException;
 public java.lang.String getURLForOfferingDownload(java.lang.String p0,java.util.List p1,com.idega.builder.data.IBExportMetadata p2)throws java.io.IOException, java.rmi.RemoteException;
 public java.lang.Object write(com.idega.core.file.data.ICFile p0) throws java.rmi.RemoteException;
 public java.lang.Object write(com.idega.util.xml.XMLData p0) throws java.rmi.RemoteException;
}
