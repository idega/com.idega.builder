package com.idega.builder.data;

import javax.ejb.*;

public interface IBEntity extends com.idega.data.IDOLegacyEntity
{
 public java.lang.String getClassName();
 public java.lang.String getEntityClassName();
 public java.lang.String getName();
 public void setClassName(java.lang.String p0);
 public void setDefaultValues();
 public void setEntityClassName(java.lang.String p0);
}
