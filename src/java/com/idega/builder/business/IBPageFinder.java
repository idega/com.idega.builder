/*
 * $Id: IBPageFinder.java,v 1.6 2004/09/27 13:51:39 aron Exp $
 *
 * Copyright (C) 2001 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package com.idega.builder.business;

import java.util.List;
import java.util.Vector;

import javax.ejb.FinderException;

import com.idega.core.builder.data.ICPage;
import com.idega.core.builder.data.ICPageHome;
import com.idega.data.IDOLookup;
import com.idega.data.IDOLookupException;

/**
 * @author <a href="mail:palli@idega.is">Pall Helgason</a>
 * @version 1.0
 */
public class IBPageFinder {

  public static List getAllPagesExtendingTemplate(int templateId) {
    try {
        /*try {
            
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
        */
          return new Vector(((ICPageHome)IDOLookup.getHome(ICPage.class)).findByTemplate(new Integer(templateId)));
    } catch (IDOLookupException e) {
        e.printStackTrace();
    } catch (FinderException e) {
        e.printStackTrace();
    }
    return null;
  }
}
