package com.idega.builder.data;

import javax.ejb.*;

public interface IBObject extends com.idega.data.IDOLegacyEntity
{
 public java.lang.String getClassName();
 public java.lang.String getName();
 public java.lang.Class getObjectClass()throws java.lang.ClassNotFoundException;
 public void setClassName(java.lang.String p0);
 public void setDefaultValues();
 public void setName(java.lang.String p0);
 public void setObjectClass(java.lang.Class p0);
}
