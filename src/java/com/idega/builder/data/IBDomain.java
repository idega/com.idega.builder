package com.idega.builder.data;

import com.idega.data.IDORelationshipException;
import java.util.Collection;
import javax.ejb.*;

public interface IBDomain extends com.idega.data.IDOLegacyEntity
{
 public int getStartTemplateID();
 public void setName(java.lang.String p0);
 public int getStartPageID();
 public java.lang.String getURL();
 public java.lang.String getName();
 public void setStartTemplate(com.idega.builder.data.IBPage p0);
 public java.lang.String getDomainName();
 public void setIBPage(com.idega.builder.data.IBPage p0);
 public com.idega.builder.data.IBPage getStartPage();
 public com.idega.builder.data.IBPage getStartTemplate();
 public Collection getTopLevelGroupsUnderDomain() throws IDORelationshipException;
}
