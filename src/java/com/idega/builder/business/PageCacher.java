/*
 * $Id: PageCacher.java,v 1.26 2009/01/14 15:07:19 tryggvil Exp $
 * Created in 2001 by Tryggvi Larusson
 *
 * Copyright (C) 2001-2004 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package com.idega.builder.business;
/**
 *  The instance of this class holds an manages a cache of Builder pages that are instances
 * of CachedBuilderPage.<br>
 *
 *  Last modified: $Date: 2009/01/14 15:07:19 $ by $Author: tryggvil $
 *
 * @author <a href="mailto:tryggvil@idega.com">Tryggvi Larusson</a>
 * @version $Revision: 1.26 $
 */
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import javax.ejb.FinderException;

import com.idega.core.builder.data.ICPage;
import com.idega.core.builder.data.ICPageHome;
import com.idega.core.cache.IWCacheManager2;
import com.idega.core.view.ViewNode;
import com.idega.data.IDOLookupException;
import com.idega.idegaweb.IWMainApplication;
import com.idega.util.StringUtil;

public class PageCacher {
	private static final String CACHE_NAME = "BuilderPages";

	PageCacher() {}

	protected boolean isPageValid(String key) {
		return StringUtil.isEmpty(key) ? false : getPageCacheMap().get(key) != null;
	}

	protected boolean isPageInvalid(String key) {
		if (StringUtil.isEmpty(key) || key.equals("-1")) {
			return false;
		}
		return !isPageValid(key);
	}

	public void flagPageInvalid(String key)	{
		getPageCacheMap().remove(key);
	}

	public void storePage(String key, String format, String stringRepresentation) throws Exception {
		CachedBuilderPage bPage = getCachedBuilderPage(key);
		bPage.setPageFormat(format);
		bPage.setSourceFromString(stringRepresentation);
		bPage.store();
		flagPageInvalid(key);
	}

	public ComponentBasedPage getComponentBasedPage(String key)	{
		return (ComponentBasedPage)getCachedBuilderPage(key);
	}

	public IBXMLPage getIBXML(String key) {
		return (IBXMLPage) getCachedBuilderPage(key);
	}

	public CachedBuilderPage getCachedBuilderPage(String key) {
		CachedBuilderPage bPage = null;
		if (isPageInvalid(key)) {
			ICPageHome pHome;
			try {
				pHome = (ICPageHome) com.idega.data.IDOLookup.getHome(ICPage.class);
				int pageId = Integer.parseInt(key);
				ICPage icPage = pHome.findByPrimaryKey(pageId);

				if(icPage.getIsFormattedInIBXML()){
					bPage = new IBXMLPage(key);
				}
				else if (icPage.getIsFormattedInHTML()){
					bPage= new HtmlBasedPage(key);
				}
				else if (icPage.getIsFormattedInJSP()){
					bPage= new JspPage(key);
				}
				else if (icPage.getIsFormattedInFacelet()){
					bPage= new FaceletPage(key);
				}
				else if (icPage.getIsFormattedInIBXML2()){
					bPage= new IBXML2FaceletPage(key);
				}

				bPage.setICPage(icPage);
				setPage(key, bPage);
				String uri = icPage.getDefaultPageURI();
				if (uri != null) {
					bPage.setPageUri(uri);
				}
			} catch (IDOLookupException e) {
				e.printStackTrace();
			} catch (FinderException e) {
				e.printStackTrace();
			} catch (NullPointerException e) {
				e.printStackTrace();
			}
		} else {
			bPage = getCachedBuilderPageFromMap(key);
		}

		return bPage;
	}

	private Object setPage(String key, ViewNode page) {
		return getPageCacheMap().put(key, page);
	}

	public Map<String, ViewNode> getPageCacheMap() {
		return getCacheManager().getCache(getCacheName(), 10000, Boolean.FALSE, Boolean.TRUE);
	}

	private String getCacheName() {
		return CACHE_NAME;
	}

	private CachedBuilderPage getCachedBuilderPageFromMap(String key) {
		return (CachedBuilderPage) getPageCacheMap().get(key);
	}

	/**
	 * A function that gets the CachedBuilderPage if it exists in cache, otherwise it returns null.
	 *
	 * @param key The id of the CachedBuilderPage to get from cache.
	 *
	 * @return The CachedBuilderPage with id = key if it exists in cache, null otherwise.
	 */
	public CachedBuilderPage getCachedBuilderPageIfInCache(String key) {
		if (isPageInvalid(key))	{
			return null;
		} else {
			CachedBuilderPage xml = getCachedBuilderPageFromMap(key);
			return xml;
		}
	}

	/**
	 * Method flagAllPagesInvalid.
	 */
	public synchronized void flagAllPagesInvalid() {
		getPageCacheMap().clear();
	}

	protected IWCacheManager2 getCacheManager() {
		return IWCacheManager2.getInstance(IWMainApplication.getDefaultIWMainApplication());
	}

	public Iterator<CachedBuilderPage> getAllPages() {
		ICPageHome pHome;
		Collection<CachedBuilderPage> allPages = new ArrayList<CachedBuilderPage>();
		try {
			pHome = (ICPageHome) com.idega.data.IDOLookup.getHome(ICPage.class);
			Collection<ICPage> pages = pHome.findAllPagesAndTemplates();

			for (Iterator<ICPage> iterator = pages.iterator(); iterator.hasNext();) {
				ICPage page = iterator.next();
				String key = page.getPageKey();
				allPages.add(getCachedBuilderPage(key));
			}

		} catch (IDOLookupException e) {
			e.printStackTrace();
		} catch (FinderException e) {
			e.printStackTrace();
		}
		return allPages.iterator();
	}
}