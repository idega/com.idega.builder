package com.idega.builder.data;

import javax.ejb.*;

public interface IBDomain extends com.idega.data.IDOLegacyEntity
{
 public java.lang.String getDomainName();
 public java.lang.String getName();
 public com.idega.builder.data.IBPage getStartPage();
 public int getStartPageID();
 public com.idega.builder.data.IBPage getStartTemplate();
 public int getStartTemplateID();
 public java.lang.String getURL();
 public void setIBPage(com.idega.builder.data.IBPage p0);
 public void setName(java.lang.String p0);
 public void setStartTemplate(com.idega.builder.data.IBPage p0);
}
