/*
 * $Id: IBPageUpdater.java,v 1.7 2007/09/11 13:10:13 justinas Exp $
 *
 * Copyright (C) 2001 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package com.idega.builder.business;

import java.rmi.RemoteException;
import java.sql.SQLException;

import javax.ejb.CreateException;
import javax.ejb.FinderException;

import com.idega.builder.data.IBPageName;
import com.idega.core.builder.data.ICPage;
import com.idega.core.builder.data.ICPageHome;
import com.idega.data.IDOLookup;

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
//			ICPage page = ((com.idega.core.builder.data.ICPageHome) com.idega.data.IDOLookup.getHomeLegacy(ICPage.class)).findByPrimaryKeyLegacy(pageId);
			ICPage page = ((ICPageHome) IDOLookup.getHomeLegacy(ICPage.class)).findByPrimaryKeyLegacy(pageId);

			page.setName(pageName);
			page.update();
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void setAsCategory(int pageID, boolean isCategory) {
		try {
			ICPage page = ((ICPageHome) com.idega.data.IDOLookup.getHomeLegacy(ICPage.class)).findByPrimaryKeyLegacy(pageID);

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
			ICPage page = ((com.idega.core.builder.data.ICPageHome) com.idega.data.IDOLookup.getHomeLegacy(ICPage.class)).findByPrimaryKeyLegacy(pageId);

			page.setTemplateId(templateId);
			page.update();
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static boolean addLocalizedPageName(int pageId, int localeId, String pageName) {
		try {
			IBPageName name = ((com.idega.builder.data.IBPageNameHome) com.idega.data.IDOLookup.getHome(IBPageName.class)).findByPageIdAndLocaleId(pageId, localeId);
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