package com.idega.builder.data;

import com.idega.data.TreeableEntity;


public interface IBObjectLibrary extends TreeableEntity<IBObjectLibrary>
{
 public void delete()throws java.sql.SQLException;
 public java.lang.String getColumnFile();
 public java.lang.String getColumnOwner();
 public com.idega.core.file.data.ICFile getFile();
 public int getOwnerId();
 public java.io.InputStream getPageValue();
 public java.io.OutputStream getPageValueForWrite();
 public void setDefaultValues();
 public void setFile(com.idega.core.file.data.ICFile p0);
 public void setOwnerId(int p0);
 public void setPageValue(java.io.InputStream p0);
}
