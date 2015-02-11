/*
 * $Id: IBPageUpdater.java,v 1.9 2008/01/12 13:27:40 valdas Exp $
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
import java.util.logging.Logger;

import javax.ejb.CreateException;

import com.idega.builder.data.IBPageName;
import com.idega.core.builder.data.ICPage;
import com.idega.core.builder.data.ICPageHome;
import com.idega.data.IDOContainer;
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
		IBPageName name = null;
		try {
			name = ((com.idega.builder.data.IBPageNameHome) com.idega.data.IDOLookup.getHome(IBPageName.class)).findByPageIdAndLocaleId(pageId, localeId);
		} catch(Exception e) {
			Logger.getLogger(IBPageUpdater.class.getName()).warning("Did not find localized name for page ID: " + pageId + ", locale ID: " + localeId);
		}

		try {
			if (name == null) {
				name = ((com.idega.builder.data.IBPageNameHome) com.idega.data.IDOLookup.getHome(IBPageName.class)).create();
			}

			name.setPageId(pageId);
			name.setLocaleId(localeId);
			name.setPageName(pageName);
			name.store();

			IDOContainer.getInstance().flushAllBeanCache();
			IDOContainer.getInstance().flushAllQueryCache();

			return true;
		}
		catch (RemoteException e) {
			e.printStackTrace();
		}
		catch (CreateException e) {
			e.printStackTrace();
		}

		return false;
	}
}