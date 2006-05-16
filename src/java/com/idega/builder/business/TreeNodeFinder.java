/*
 * $Id: TreeNodeFinder.java,v 1.15 2006/05/16 10:16:42 palli Exp $
 *
 * Copyright (C) 2001 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package com.idega.builder.business;

import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.List;
import java.util.Vector;

import javax.ejb.FinderException;

import com.idega.builder.data.IBPageName;
import com.idega.builder.data.IBPageNameHome;
import com.idega.core.builder.data.ICPage;
import com.idega.core.builder.data.ICPageBMPBean;
import com.idega.data.EntityFinder;
import com.idega.data.IDOLookup;

/**
 * @author <a href="mail:palli@idega.is">Pall Helgason</a>
 * @version 1.0
 */
public class TreeNodeFinder {
	public static Collection getAllPageNames() {
		try {
			Collection col = ((IBPageNameHome)IDOLookup.getHome(IBPageName.class)).findAll();
			
			return col;
		}
		catch (RemoteException e) {
			e.printStackTrace();
		}
		catch (FinderException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static List listOfAllPages() {
		try {
			ICPage pages = ((com.idega.core.builder.data.ICPageHome) com.idega.data.IDOLookup.getHomeLegacy(ICPage.class)).createLegacy();
			StringBuffer sql = new StringBuffer("select * from ");
			sql.append(pages.getEntityName());
			sql.append(" where (");
			sql.append(ICPageBMPBean.getColumnType());
			sql.append(" = '");
			sql.append(ICPageBMPBean.PAGE);
			sql.append("' or ");
			sql.append(ICPageBMPBean.getColumnType());
			sql.append(" = '");
			sql.append(ICPageBMPBean.DPT_PAGE);
			sql.append("') and (");
			sql.append(ICPageBMPBean.getColumnDeleted());
			sql.append(" = '");
			sql.append(ICPageBMPBean.NOT_DELETED);
			sql.append("' or ");
			sql.append(ICPageBMPBean.getColumnDeleted());
			sql.append(" is null)");
			
			sql.append(" order by ").append(ICPageBMPBean.getColumnTreeOrder())
			.append(", ").append(pages.getIDColumnName()).append(" desc");

			return (EntityFinder.findAll(pages, sql.toString()));
		}
		catch (SQLException e) {
			e.printStackTrace();
			return (null);
		}
	}

	public static List listOfAllTemplates() {
		try {
			ICPage pages = ((com.idega.core.builder.data.ICPageHome) com.idega.data.IDOLookup.getHomeLegacy(ICPage.class)).createLegacy();
			StringBuffer sql = new StringBuffer("select * from ");
			sql.append(pages.getEntityName());
			sql.append(" where (");
			sql.append(ICPageBMPBean.getColumnType());
			sql.append(" = '");
			sql.append(ICPageBMPBean.TEMPLATE);
			sql.append("' or ");
			sql.append(ICPageBMPBean.getColumnType());
			sql.append(" = '");
			sql.append(ICPageBMPBean.DPT_TEMPLATE);
			sql.append("') and (");
			sql.append(ICPageBMPBean.getColumnDeleted());
			sql.append(" = '");
			sql.append(ICPageBMPBean.NOT_DELETED);
			sql.append("' or ");
			sql.append(ICPageBMPBean.getColumnDeleted());
			sql.append(" is null)");

			sql.append(" order by ").append(ICPageBMPBean.getColumnTreeOrder())
			.append(", ").append(pages.getIDColumnName()).append(" desc");

			return (EntityFinder.findAll(pages, sql.toString()));
		}
		catch (SQLException e) {
			e.printStackTrace();
			return (null);
		}
	}

	public static List listOfAllDrafts() {
		return (null);
	}

	/**
	 *
	 * @return
	 */
	public static List listOfAllPageRelationships() throws SQLException {
		List ret = null;
		ICPage pages = null;
		Connection conn = null;
		Statement stmt = null;
		ResultSet result = null;
		try {
			pages = ((com.idega.core.builder.data.ICPageHome) com.idega.data.IDOLookup.getHomeLegacy(ICPage.class)).createLegacy();
			conn = pages.getConnection();
			stmt = conn.createStatement();

			StringBuffer sql = new StringBuffer();
			/**
			 * @todo til að útiloka dpt_síður í famtíðinni þarf að sækja relationship 
			 * útfrá child en ekki parent annar getur komið plús í tréð þar sem hann 
			 * á ekki heima þ.e. að childpage.getColumnType() = pages.PAGE
			 */
			sql.append("select * from ");
			sql.append(pages.getEntityName() ).append( "_tree t, ");
			sql.append(pages.getEntityName()).append( " p ");
			sql.append(" where ");
			sql.append("p." ).append( pages.getIDColumnName());
			sql.append(" = ");
			sql.append("t.").append( pages.getIDColumnName());
			sql.append(" and (");
			sql.append("p.").append( ICPageBMPBean.getColumnType());
			sql.append(" = '");
			sql.append(ICPageBMPBean.PAGE);
			sql.append("' or p.").append( ICPageBMPBean.getColumnType());
			sql.append(" = '");
			sql.append(ICPageBMPBean.DPT_PAGE);
			sql.append("')");
			
			sql.append(" order by p.").append(ICPageBMPBean.getColumnTreeOrder())
			.append(", p.").append(pages.getIDColumnName());

			result = stmt.executeQuery(sql.toString());

			if (result != null) {
				while (result.next()) {
					int parentId = result.getInt(pages.getIDColumnName());
					int childId = result.getInt("child_" + pages.getIDColumnName());

					if (parentId != -1 && childId != -1) {
						if (ret == null) {
							ret = new Vector();
						}

						ret.add(new Integer(parentId));
						ret.add(new Integer(childId));
					}
				}
			}
		}
	    finally {
	    	// do not hide an existing exception
	    	try { 
	    		if (result != null) {
	    			result.close();
		      	}
	    	}
		    catch (SQLException resultCloseEx) {		    	
		    	System.err.println("[TreeNodeFinder] result set could not be closed");
		     	resultCloseEx.printStackTrace(System.err);
		    }
		    // do not hide an existing exception
		    try {
		    	if (stmt != null)  {
		    		stmt.close();
					if (conn != null) {
						if (pages != null) {
							pages.freeConnection(conn);
						}
					}
		    	}
		    }
	 	    catch (SQLException statementCloseEx) {
		     	System.err.println("[TreeNodeFinder] statement could not be closed");
		     	statementCloseEx.printStackTrace(System.err);
		    }
	    }		
		return (ret);
	}

	public static List listOfAllTemplateRelationships() throws SQLException {
		List ret = null;
		ICPage pages = null;
		Connection conn = null;
		Statement stmt = null;
		ResultSet result = null;
		try {
			pages = ((com.idega.core.builder.data.ICPageHome) com.idega.data.IDOLookup.getHomeLegacy(ICPage.class)).createLegacy();
			conn = pages.getConnection();
			stmt = conn.createStatement();

			StringBuffer sql = new StringBuffer();
			sql.append("select * from ");
			sql.append(pages.getEntityName() ).append( "_tree t, ");
			sql.append(pages.getEntityName() ).append( " p ");
			sql.append(" where ");
			sql.append("p." ).append( pages.getIDColumnName());
			sql.append(" = ");
			sql.append("t.").append( pages.getIDColumnName());
			sql.append(" and (");
			sql.append("p.").append( ICPageBMPBean.getColumnType());
			sql.append(" = '");
			sql.append(ICPageBMPBean.TEMPLATE);
			sql.append("' or p.").append( ICPageBMPBean.getColumnType());
			sql.append(" = '");
			sql.append(ICPageBMPBean.DPT_TEMPLATE);
			sql.append("')");
			
			sql.append(" order by p.").append(ICPageBMPBean.getColumnTreeOrder())
			.append(", p.").append(pages.getIDColumnName());

			result = stmt.executeQuery(sql.toString());

			if (result != null) {
				while (result.next()) {
					int parentId = result.getInt(pages.getIDColumnName());
					int childId = result.getInt("child_" + pages.getIDColumnName());

					if (parentId != -1 && childId != -1) {
						if (ret == null) {
							ret = new Vector();
						}

						ret.add(new Integer(parentId));
						ret.add(new Integer(childId));
					}
				}
			}
		}
	    finally {
	    	// do not hide an existing exception
	    	try { 
	    		if (result != null) {
	    			result.close();
		      	}
	    	}
		    catch (SQLException resultCloseEx) {		    	
		    	System.err.println("[TreeNodeFinder] result set could not be closed");
		     	resultCloseEx.printStackTrace(System.err);
		    }
		    // do not hide an existing exception
		    try {
		    	if (stmt != null)  {
		    		stmt.close();
					if (conn != null) {
						if (pages != null) {
							pages.freeConnection(conn);
						}
					}
		    	}
		    }
	 	    catch (SQLException statementCloseEx) {
		     	System.err.println("[TreeNodeFinder] statement could not be closed");
		     	statementCloseEx.printStackTrace(System.err);
		    }
	    }
	    return (ret);
	}

	public static List listOfAllDraftRelationships() {
		return (null);
	}
}
