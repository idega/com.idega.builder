package com.idega.builder.dynamicpagetrigger.data;


public interface PageTriggerInfo extends com.idega.data.IDOLegacyEntity
{
 public int getDefaultTemplateId();
 public int getICObjectID();
 public int getRootPageId();
 public void setDefaultTemplateId(int p0);
 public void setICObject(int p0);
 public void setICObject(com.idega.core.data.ICObject p0);
 public void setRootPageId(int p0);
}
