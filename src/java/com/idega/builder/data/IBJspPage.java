package com.idega.builder.data;

import javax.ejb.*;

public interface IBJspPage extends com.idega.data.IDOLegacyEntity
{
 public java.lang.String getAttributeName();
 public java.lang.String getAttributeNameColumnName();
 public java.lang.String getAttributeValue();
 public java.lang.String getAttributeValueColumnName();
 public java.lang.String getUrl();
 public java.lang.String getUrlColumnName();
 public void setAttributeName(java.lang.String p0);
 public void setAttributeValue(java.lang.String p0);
 public void setUrl(java.lang.String p0);
}
