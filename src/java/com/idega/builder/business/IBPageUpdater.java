/*
 * $Id: IBPageUpdater.java,v 1.4 2003/09/18 11:41:57 laddi Exp $
 *
 * Copyright (C) 2001 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package com.idega.builder.business;

import com.idega.builder.data.IBPage;
import com.idega.builder.data.IBPageName;

import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;

import javax.ejb.CreateException;
import javax.ejb.FinderException;

/**
 * @author <a href="mail:palli@idega.is">Pall Helgason</a>
 * @version 1.0
 */
public class IBPageUpdater {
	/**
	 *
	 */
	public static void updatePageName(int pageId, String pageName) {
		try {
			IBPage page = ((com.idega.builder.data.IBPageHome) com.idega.data.IDOLookup.getHomeLegacy(IBPage.class)).findByPrimaryKeyLegacy(pageId);

			page.setName(pageName);
			page.update();
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void setAsCategory(int pageID, boolean isCategory) {
		try {
			IBPage page = ((com.idega.builder.data.IBPageHome) com.idega.data.IDOLookup.getHomeLegacy(IBPage.class)).findByPrimaryKeyLegacy(pageID);

			page.setIsCategory(isCategory);
			page.update();
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 *
	 */
	public static void updateTemplateId(int pageId, int templateId) {
		try {
			IBPage page = ((com.idega.builder.data.IBPageHome) com.idega.data.IDOLookup.getHomeLegacy(IBPage.class)).findByPrimaryKeyLegacy(pageId);

			page.setTemplateId(templateId);
			page.update();
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static boolean addLocalizedPageName(int pageId, int localeId, String pageName) {
		try {
			IBPageName name = null;
			Collection col = ((com.idega.builder.data.IBPageNameHome) com.idega.data.IDOLookup.getHome(IBPageName.class)).findAllByPageIdAndLocaleId(pageId, localeId);
			if (col != null && !col.isEmpty()) {
				Iterator it = col.iterator();
				if (it.hasNext()) {
					name = (IBPageName)it.next();	
				}
			}
			
			if (name == null) {
				name = ((com.idega.builder.data.IBPageNameHome) com.idega.data.IDOLookup.getHome(IBPageName.class)).create();
			}
			
			name.setPageId(pageId);
			name.setLocaleId(localeId);
			name.setPageName(pageName);
			name.store();
			
			return true;
		}
		catch (RemoteException e) {
			e.printStackTrace();
		}
		catch (FinderException e) {
			e.printStackTrace();
		}
		catch (CreateException e) {
			e.printStackTrace();
		}
		
		return false;
	}
}