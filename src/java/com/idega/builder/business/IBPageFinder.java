/*
 * $Id: IBPageFinder.java,v 1.2 2001/10/11 16:23:26 palli Exp $
 *
 * Copyright (C) 2001 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package com.idega.builder.business;

import com.idega.builder.data.IBPage;
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
      IBPage page = new IBPage();
      StringBuffer sql = new StringBuffer("select * from ");
      sql.append(page.getEntityName());
      sql.append(" where ");
      sql.append(page.getColumnTemplateID());
      sql.append(" = ");
      sql.append(templateId);
      sql.append(" and (");
      sql.append(page.getColumnDeleted());
      sql.append(" is null or ");
      sql.append(page.getColumnDeleted());
      sql.append(" = 'N')");

      return(EntityFinder.findAll(page,sql.toString()));
    }
    catch(SQLException e) {
      return(null);
    }
  }
}