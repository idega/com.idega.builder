/*
 * $Id: IBPageFinder.java,v 1.5 2003/10/03 01:41:54 tryggvil Exp $
 *
 * Copyright (C) 2001 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package com.idega.builder.business;

import com.idega.core.builder.data.ICPage;
import com.idega.data.EntityFinder;
import java.util.List;
import java.sql.SQLException;

/**
 * @author <a href="mail:palli@idega.is">Pall Helgason</a>
 * @version 1.0
 */
public class IBPageFinder {

  public static List getAllPagesExtendingTemplate(int templateId) {
    try {
      ICPage page = ((com.idega.core.builder.data.ICPageHome)com.idega.data.IDOLookup.getHomeLegacy(ICPage.class)).createLegacy();
      StringBuffer sql = new StringBuffer("select * from ");
      sql.append(page.getEntityName());
      sql.append(" where ");
      sql.append(com.idega.builder.data.IBPageBMPBean.getColumnTemplateID());
      sql.append(" = ");
      sql.append(templateId);
      sql.append(" and (");
      sql.append(com.idega.builder.data.IBPageBMPBean.getColumnDeleted());
      sql.append(" is null or ");
      sql.append(com.idega.builder.data.IBPageBMPBean.getColumnDeleted());
      sql.append(" = 'N')");

      return(EntityFinder.findAll(page,sql.toString()));
    }
    catch(SQLException e) {
      return(null);
    }
  }
}
