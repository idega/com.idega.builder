/*
 * $Id: IBStartPageBMPBean.java,v 1.1 2004/03/25 15:37:39 thomas Exp $
 *
 * Copyright (C) 2001 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package com.idega.builder.data;

import com.idega.core.builder.data.*;
import com.idega.data.GenericEntity;
import com.idega.data.IDOLookup;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;
import javax.ejb.FinderException;

/**
 * @author <a href="mail:palli@idega.is">Pall Helgason</a>
 * @version 1.0
 */
public class IBStartPageBMPBean extends GenericEntity implements IBStartPage {
  private static final String TABLE_NAME = "ib_start_pages";
  private static final String DOMAIN_ID = "ib_domain_id";
  private static final String PAGE_ID = "ib_page_id";
  private static final String PAGE_TYPE = "ib_page_type";

  private static final String TEMPLATE = "T";
  private static final String PAGE = "P";

  /**
   *
   */
  public IBStartPageBMPBean() {
    super();
  }

  /**
   *
   */
  public IBStartPageBMPBean(int id) throws SQLException {
    super(id);
  }

  /**
   *
   */
  public void initializeAttributes() {
    addAttribute(getIDColumnName());
    addAttribute(getColumnDomainId(),"Domain id",true,true,Integer.class,GenericEntity.MANY_TO_ONE,ICDomain.class);
    addAttribute(getColumnPageId(),"Page id",true,true,Integer.class,GenericEntity.MANY_TO_ONE,ICPage.class);
    addAttribute(getColumnPageType(),"Page type",true,true,String.class);
    setMaxLength(getColumnPageType(),1);
  }

  /**
   *
   */
  public String getEntityName() {
    return(TABLE_NAME);
  }

  /**
   *
   */
  public static String getColumnDomainId() {
    return(DOMAIN_ID);
  }

  /**
   *
   */
  public static String getColumnPageId() {
    return(PAGE_ID);
  }

  /**
   *
   */
  public static String getColumnPageType() {
    return(PAGE_TYPE);
  }

  /**
   *
   */
  public void insertStartData() throws Exception {
  try {
    ICDomainHome dhome = (ICDomainHome)IDOLookup.getHome(ICDomain.class);

    Collection domains = dhome.findAllDomains();
    if (domains != null && !domains.isEmpty()) {
      Iterator it = domains.iterator();
      while (it.hasNext()) {
        ICDomain domain = (ICDomain)it.next();
        IBStartPage start = null;
        int id = domain.getStartPageID();
        if (id > 0) {
          start = ((IBStartPageHome)IDOLookup.getHome(this.getClass())).create();
          start.setDomainId(domain.getID());
          start.setPageId(id);
          start.setPageTypePage();
          start.store();
        }

        id = domain.getStartTemplateID();
        if (id > 0) {
          start = ((IBStartPageHome)IDOLookup.getHome(this.getClass())).create();
          start.setDomainId(domain.getID());
          start.setPageId(id);
          start.setPageTypeTemplate();
          start.store();
        }
      }
    }
    }
    catch(Exception e) {
      e.printStackTrace();

      throw e;
    }
  }

  /**
   *
   */
  public void setDomainId(int id) {
    setColumn(getColumnDomainId(),id);
  }

  /**
   *
   */
  public void setDomainId(Integer id) {
    setColumn(getColumnDomainId(),id);
  }

  /**
   *
   */
  public int getDomainId() {
    return(getIntColumnValue(getColumnDomainId()));
  }

  /**
   *
   */
  public void setPageId(int id) {
    setColumn(getColumnPageId(),id);
  }

  /**
   *
   */
  public void setPageId(Integer id) {
    setColumn(getColumnPageId(),id);
  }

  /**
   *
   */
  public int getPageId() {
    return(getIntColumnValue(getColumnPageId()));
  }

  private void setPageType(String type) {
    setColumn(getColumnPageType(),type);
  }

  public void setPageTypeTemplate() {
    setPageType(TEMPLATE);
  }

  public void setPageTypePage() {
    setPageType(PAGE);
  }

  public boolean getIsPageTypePage() {
    return getStringColumnValue(getColumnPageType()).equals(PAGE)?true:false;
  }

  public boolean getIsPageTypeTemplate() {
    return getStringColumnValue(getColumnPageType()).equals(TEMPLATE)?true:false;
  }

  public Collection ejbFindAllPagesByDomain(int domain_id) throws FinderException {
    return ejbFindAllStartTemplatesByDomainAndType(domain_id,PAGE);
  }

  public Collection ejbFindAllTemplatesByDomain(int domain_id) throws FinderException {
    return ejbFindAllStartTemplatesByDomainAndType(domain_id,TEMPLATE);
  }

  private Collection ejbFindAllStartTemplatesByDomainAndType(int domain_id, String type) throws FinderException {
    StringBuffer sql = new StringBuffer("select * from ");
    sql.append(getTableName());
    sql.append(" where ");
    sql.append(getColumnDomainId());
    sql.append(" = ");
    sql.append(domain_id);
    sql.append(" and ");
    sql.append(getColumnPageType());
    sql.append(" = '");
    sql.append(type);
    sql.append("'");

    return super.idoFindIDsBySQL(sql.toString());
  }
}