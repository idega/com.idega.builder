package com.idega.builder.data;


public interface IBPageName extends com.idega.data.IDOEntity
{
 public void setPageId(java.lang.Integer p0);
 public void setLocaleId(java.lang.Integer p0);
 public void setPageId(int p0);
 public void setPageName(java.lang.String p0);
 public void setPage(com.idega.core.builder.data.ICPage p0);
 public int getLocaleId();
 public java.lang.String getPageName();
 public com.idega.core.localisation.data.ICLocale getLocale();
 public void setLocaleId(int p0);
 public void initializeAttributes();
 public void setLocale(com.idega.core.localisation.data.ICLocale p0);
 public com.idega.core.builder.data.ICPage getPage();
 public int getPageId();
}
