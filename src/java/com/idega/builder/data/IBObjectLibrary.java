package com.idega.builder.data;

import javax.ejb.*;

public interface IBObjectLibrary extends com.idega.data.TreeableEntity
{
 public void delete()throws java.sql.SQLException;
 public java.lang.String getColumnFile();
 public java.lang.String getColumnOwner();
 public com.idega.core.data.ICFile getFile();
 public int getOwnerId();
 public java.io.InputStream getPageValue();
 public java.io.OutputStream getPageValueForWrite();
 public void setDefaultValues();
 public void setFile(com.idega.core.data.ICFile p0);
 public void setOwnerId(int p0);
 public void setPageValue(java.io.InputStream p0);
}
