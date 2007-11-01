/*
 * $Id: PageCacher.java,v 1.24 2007/11/01 11:12:12 valdas Exp $
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
 *  Last modified: $Date: 2007/11/01 11:12:12 $ by $Author: valdas $
 * 
 * @author <a href="mailto:tryggvil@idega.com">Tryggvi Larusson</a>
 * @version $Revision: 1.24 $
 */
import java.util.Map;

import javax.ejb.FinderException;

import com.idega.core.builder.data.ICPage;
import com.idega.core.builder.data.ICPageHome;
import com.idega.core.cache.IWCacheManager2;
import com.idega.core.view.ViewNode;
import com.idega.data.IDOLookupException;
import com.idega.idegaweb.IWMainApplication;
public class PageCacher
{
	private static final String CACHE_NAME = "BuilderPages";

	//Instance variables:
	//private Map pageCache = new WeakHashMap();
	//private Map pagesValid = new HashMap();
	PageCacher()
	{}
	
	protected boolean isPageValid(String key)
	{
		boolean theReturn = false;
		//Boolean fetched = (Boolean) pagesValid.get(key);
		//if (fetched != null)
		//{
			if (getPageCacheMap().get(key) != null)
			{
				//theReturn = fetched.booleanValue();
				theReturn=true;
			}
		//}
		return theReturn;
	}
	protected boolean isPageInvalid(String key)
	{
		if (key.equals("-1")) {
			return false;
		}
		return !isPageValid(key);
	}
	public void flagPageInvalid(String key)
	{
		//flagPageValid(key, false);
		getPageCacheMap().remove(key);
	}
	/*public void flagPageValid(String key, boolean trueOrFalse)
	{
		if (trueOrFalse)
		{
			pagesValid.put(key, Boolean.TRUE);
		}
		else
		{
			pagesValid.put(key, Boolean.FALSE);
		}
	}*/
	/*public Page getPage(String key, IWContext iwc)
	{
		IBXMLPage xml = null;
		xml = getXML(key);
		if (xml != null)
		{
			return xml.getNewPage(iwc);
			//return (Page) xml.getPopulatedPage().clonePermissionChecked(iwc);
			//return (Page)xml.getPopulatedPage().clone();
		}
		return null;
	}
	public Page getPage(String key)
	{
		IBXMLPage xml = null;
		xml = getXML(key);
		if (xml != null)
		{
			return (Page) xml.getPopulatedPage().clone();
			//return (Page)xml.getPopulatedPage();
		}
		return null;
	}*/
	/*public static Page getPage(String key, InputStream streamWithXML)throws Exception{
	  Page theReturn = null;
	  IBXMLPage xml = null;
	  xml = getXML(key,streamWithXML);
	  if(xml!=null){
	    return (Page)xml.getPopulatedPage().clone();
	  }
	  return null;
	}*/
	
	public void storePage(String key,String format,String stringRepresentation)throws Exception{
		CachedBuilderPage bPage = getCachedBuilderPage(key);
		//flagPageInvalid(key);
		bPage.setPageFormat(format);
		bPage.setSourceFromString(stringRepresentation);
		bPage.store();
		flagPageInvalid(key);
	}
	
	public ComponentBasedPage getComponentBasedPage(String key)
	{
		return (ComponentBasedPage)getCachedBuilderPage(key);
	}

	public IBXMLPage getIBXML(String key)
	{
		try {
			return (IBXMLPage) getCachedBuilderPage(key);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public CachedBuilderPage getCachedBuilderPage(String key)
	{
		CachedBuilderPage bPage = null;
		if (isPageInvalid(key))
		{
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
				bPage.setICPage(icPage);
				//bPage.setPageKey(key);
				setPage(key, bPage);
				String uri = icPage.getDefaultPageURI();
				if(uri!=null){
					bPage.setPageUri(uri);
				}
				
			} catch (IDOLookupException e) {
				e.printStackTrace();
			} catch (FinderException e) {
				e.printStackTrace();
			}
		}
		else
		{
			bPage = getCachedBuilderPageFromMap(key);
		}
		return bPage;
		/*
		if (isPageInvalid(key)){
		  try{
		    IBPage ibpage = ((com.idega.builder.data.IBPageHome)com.idega.data.IDOLookup.getHomeLegacy(IBPage.class)).findByPrimaryKeyLegacy(Integer.parseInt(key));
		    xml = getXML(key,ibpage.getPageValue());
		  }
		  catch(PageDoesNotExist pe){
		    xml = new IBXMLPage(false);
		    xml.setPageAsEmptyPage();
		  }
		  catch(NumberFormatException ne){
		    try{
		      InputStream stream = new FileInputStream(key);
		      xml = getXML(key,stream);
		    }
		    catch(FileNotFoundException fnfe){
		      fnfe.printStackTrace();
		    }
		    catch(PageDoesNotExist pe){
		      xml = new IBXMLPage(false);
		      xml.setPageAsEmptyPage();
		
		    }
		    //thePage= getPage(key,stream);
		  }
		  catch(Exception e){
		    //InputStream stream = new FileInputStream(key);
		    //thePage= getPage(key,stream);
		    e.printStackTrace();
		  }
		}
		else{
		  xml = getXMLPageCached(key);
		}
		return xml;*/
	}
	/*public static IBXMLPage getXML(String key,InputStream streamWithXML)throws PageDoesNotExist{
	  IBXMLPage xml = null;
	  if (isPageInvalid(key)){
	    xml = XMLReader.parseXML(streamWithXML);
	    setPage(key,xml);
	    return xml;
	  }
	  else{
	    return getXML(key);
	  }
	}*/
	private Object setPage(String key, ViewNode page)
	{
		//flagPageValid(key, true);
		return getPageCacheMap().put(key, page);
	}
	public Map getPageCacheMap()
	{
		//return this.pageCache;
		return getCacheManager().getCache(getCacheName());
	}
	/**
	 * <p>
	 * TODO tryggvil describe method getCacheName
	 * </p>
	 * @return
	 */
	private String getCacheName() {
		return CACHE_NAME;
	}

	private CachedBuilderPage getCachedBuilderPageFromMap(String key)
	{
		return (CachedBuilderPage) getPageCacheMap().get(key);
	}
	/**
	 * A function that gets the CachedBuilderPage if it exists in cache, otherwise it returns null.
	 *
	 * @param key The id of the CachedBuilderPage to get from cache.
	 *
	 * @return The CachedBuilderPage with id = key if it exists in cache, null otherwise.
	 */
	public CachedBuilderPage getCachedBuilderPageIfInCache(String key)
	{
		if (isPageInvalid(key))
		{
			return null;
		}
		else
		{
			CachedBuilderPage xml = getCachedBuilderPageFromMap(key);
			return xml;
		}
	}
	/**
	 * Method flagAllPagesInvalid.
	 */
	public synchronized void flagAllPagesInvalid()
	{
		getPageCacheMap().clear();
	}
	
	protected IWCacheManager2 getCacheManager(){
		return IWCacheManager2.getInstance(IWMainApplication.getDefaultIWMainApplication());
	}
}