package com.idega.builder.dynamicpagetrigger.data;


public interface PageLink extends com.idega.data.IDOLegacyEntity
{
 public java.lang.String getDefaultLinkText();
 public boolean getDeleted();
 public int getDeletedBy();
 public java.sql.Timestamp getDeletedWhen();
 public int getLinkImageId();
 public java.lang.String getName();
 public int getOnClickImageId();
 public int getOnMouseOverImageId();
 public int getPageId();
 public int getPageTriggerInfoId();
 public java.lang.String getReferencedDataId();
 public java.lang.String getStandardParameters();
 public void setDefaultLinkText(java.lang.String p0);
 public void setDeleted(boolean p0);
 public void setDeletedBy(int p0);
 public void setDeletedWhen(java.sql.Timestamp p0);
 public void setLinkImageId(int p0);
 public void setName(java.lang.String p0);
 public void setOnClickImageId(int p0);
 public void setOnMouseOverImageId(int p0);
 public void setPageId(int p0);
 public void setPageTriggerInfoId(int p0);
 public void setReferencedDataId(java.lang.String p0);
 public void setStandardParameters(java.lang.String p0);
}
