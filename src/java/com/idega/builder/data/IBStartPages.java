package com.idega.builder.data;

import javax.ejb.*;

public interface IBStartPages extends com.idega.data.IDOLegacyEntity
{
 public void setPageId(int p0);
 public void setPageId(java.lang.Integer p0);
 public void setPageTypeTemplate();
 public int getPageId();
 public void setPageTypePage();
 public boolean getIsPageTypePage();
 public int getDomainId();
 public boolean getIsPageTypeTemplate();
 public void setDomainId(java.lang.Integer p0);
 public void setDomainId(int p0);
}
