/*
 * $Id: TreeNodeFinder.java,v 1.5 2002/04/06 19:07:38 tryggvil Exp $
 *
 * Copyright (C) 2001 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package com.idega.builder.business;

import com.idega.builder.data.IBObjectLibrary;
import com.idega.builder.data.IBPage;
import com.idega.data.EntityFinder;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.List;
import java.util.Vector;

/**
 * @author <a href="mail:palli@idega.is">Pall Helgason</a>
 * @version 1.0
 */
public class TreeNodeFinder {
  public static List listOfAllPages() {
    try {
      IBPage pages = ((com.idega.builder.data.IBPageHome)com.idega.data.IDOLookup.getHomeLegacy(IBPage.class)).createLegacy();
      StringBuffer sql = new StringBuffer("select * from ");
      sql.append(pages.getEntityName());
      sql.append(" where (");
      sql.append(com.idega.builder.data.IBPageBMPBean.getColumnType());
      sql.append(" = '");
      sql.append(com.idega.builder.data.IBPageBMPBean.PAGE);
      sql.append("' or ");
      sql.append(com.idega.builder.data.IBPageBMPBean.getColumnType());
      sql.append(" = '");
      sql.append(com.idega.builder.data.IBPageBMPBean.DPT_PAGE);
      sql.append("') and (");
      sql.append(com.idega.builder.data.IBPageBMPBean.getColumnDeleted());
      sql.append(" = '");
      sql.append(com.idega.builder.data.IBPageBMPBean.NOT_DELETED);
      sql.append("' or ");
      sql.append(com.idega.builder.data.IBPageBMPBean.getColumnDeleted());
      sql.append(" is null)");

      return(EntityFinder.findAll(pages,sql.toString()));
    }
    catch(SQLException e) {
      e.printStackTrace();
      return(null);
    }
  }

  public static List listOfAllTemplates() {
    try {
      IBPage pages = ((com.idega.builder.data.IBPageHome)com.idega.data.IDOLookup.getHomeLegacy(IBPage.class)).createLegacy();
      StringBuffer sql = new StringBuffer("select * from ");
      sql.append(pages.getEntityName());
      sql.append(" where (");
      sql.append(com.idega.builder.data.IBPageBMPBean.getColumnType());
      sql.append(" = '");
      sql.append(com.idega.builder.data.IBPageBMPBean.TEMPLATE);
      sql.append("' or ");
      sql.append(com.idega.builder.data.IBPageBMPBean.getColumnType());
      sql.append(" = '");
      sql.append(com.idega.builder.data.IBPageBMPBean.DPT_TEMPLATE);
      sql.append("') and (");
      sql.append(com.idega.builder.data.IBPageBMPBean.getColumnDeleted());
      sql.append(" = '");
      sql.append(com.idega.builder.data.IBPageBMPBean.NOT_DELETED);
      sql.append("' or ");
      sql.append(com.idega.builder.data.IBPageBMPBean.getColumnDeleted());
      sql.append(" is null)");

      return(EntityFinder.findAll(pages,sql.toString()));
    }
    catch(SQLException e) {
      e.printStackTrace();
      return(null);
    }
  }

  public static List listOfAllDrafts() {
    return(null);
  }

  /**
   *
   * @return
   */
  public static List listOfAllPageRelationships() throws SQLException {
    List ret = null;
    IBPage pages = null;
		Connection conn = null;
		Statement stmt = null;
		try {
      pages = ((com.idega.builder.data.IBPageHome)com.idega.data.IDOLookup.getHomeLegacy(IBPage.class)).createLegacy();
			conn = pages.getConnection();
			stmt = conn.createStatement();

      StringBuffer sql = new StringBuffer();
      /**
       * @todo til að útiloka dpt_síður í famtíðinni þarf að sækja relationship útfrá child en ekki parent annar getur komið plúl í tréð þar sem hann á ekki heima
       * þ.e. að childpage.getColumnType() = pages.PAGE
       */
      sql.append("select * from ");
      sql.append(pages.getEntityName() + "_tree t, ");
      sql.append(pages.getEntityName() + " p ");
      sql.append(" where ");
      sql.append("p." + pages.getIDColumnName());
      sql.append(" = ");
      sql.append("t." + pages.getIDColumnName());
      sql.append(" and (");
      sql.append("p." + com.idega.builder.data.IBPageBMPBean.getColumnType());
      sql.append(" = '");
      sql.append(com.idega.builder.data.IBPageBMPBean.PAGE);
      sql.append("' or p." + com.idega.builder.data.IBPageBMPBean.getColumnType());
      sql.append(" = '");
      sql.append(com.idega.builder.data.IBPageBMPBean.DPT_PAGE);
      sql.append("')");

      ResultSet result = stmt.executeQuery(sql.toString());

      if (result != null) {
        while (result.next()) {
          int parentId = result.getInt(pages.getIDColumnName());
          int childId = result.getInt("child_"+pages.getIDColumnName());

          if (parentId != -1 && childId != -1) {
            if (ret == null)
              ret = new Vector();

            ret.add(new Integer(parentId));
            ret.add(new Integer(childId));
          }
        }
      }
		}
		finally {
			if (stmt != null) {
				stmt.close();
			}
			if (conn != null) {
        if (pages != null)
  				pages.freeConnection(conn);
			}
		}

    return(ret);
  }

  public static List listOfAllTemplateRelationships() throws SQLException {
    List ret = null;
    IBPage pages = null;
		Connection conn = null;
		Statement stmt = null;
		try {
      pages = ((com.idega.builder.data.IBPageHome)com.idega.data.IDOLookup.getHomeLegacy(IBPage.class)).createLegacy();
			conn = pages.getConnection();
			stmt = conn.createStatement();

      StringBuffer sql = new StringBuffer();
      sql.append("select * from ");
      sql.append(pages.getEntityName() + "_tree t, ");
      sql.append(pages.getEntityName() + " p ");
      sql.append(" where ");
      sql.append("p." + pages.getIDColumnName());
      sql.append(" = ");
      sql.append("t." + pages.getIDColumnName());
      sql.append(" and (");
      sql.append("p." + com.idega.builder.data.IBPageBMPBean.getColumnType());
      sql.append(" = '");
      sql.append(com.idega.builder.data.IBPageBMPBean.TEMPLATE);
      sql.append("' or p." + com.idega.builder.data.IBPageBMPBean.getColumnType());
      sql.append(" = '");
      sql.append(com.idega.builder.data.IBPageBMPBean.DPT_TEMPLATE);
      sql.append("')");

      ResultSet result = stmt.executeQuery(sql.toString());

      if (result != null) {
        while (result.next()) {
          int parentId = result.getInt(pages.getIDColumnName());
          int childId = result.getInt("child_"+pages.getIDColumnName());

          if (parentId != -1 && childId != -1) {
            if (ret == null)
              ret = new Vector();

            ret.add(new Integer(parentId));
            ret.add(new Integer(childId));
          }
        }
      }
		}
		finally {
			if (stmt != null) {
				stmt.close();
			}
			if (conn != null) {
        if (pages != null)
  				pages.freeConnection(conn);
			}
		}

    return(ret);
  }

  public static List listOfAllDraftRelationships() {
    return(null);
  }
}
