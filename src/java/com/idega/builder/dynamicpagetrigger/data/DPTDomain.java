package com.idega.builder.dynamicpagetrigger.data;

import javax.ejb.*;

public interface DPTDomain extends com.idega.data.IDOLegacyEntity
{
 public java.lang.String getDPTPageType();
 public int getIBDomainID();
 public int getIBPageID();
 public void setDPTPageType(java.lang.String p0);
 public void setIBDomainID(int p0);
 public void setIBPageID(int p0);
}
