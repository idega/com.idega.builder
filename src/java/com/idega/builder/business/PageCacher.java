package com.idega.builder.business;
/**
 * Title:        idegaclasses
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author
 * @version 1.0
 */
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

import com.idega.presentation.IWContext;
import com.idega.presentation.Page;
public class PageCacher
{
	private static Map pageCache = new WeakHashMap();
	private static Map pagesValid = new HashMap();
	private PageCacher()
	{}
	protected static boolean isPageValid(String key)
	{
		boolean theReturn = false;
		Boolean fetched = (Boolean) pagesValid.get(key);
		if (fetched != null)
		{
			if (getPageCacheMap().get(key) != null)
			{
				theReturn = fetched.booleanValue();
			}
		}
		return theReturn;
	}
	protected static boolean isPageInvalid(String key)
	{
		return !isPageValid(key);
	}
	public static void flagPageInvalid(String key)
	{
		flagPageValid(key, false);
	}
	public static void flagPageValid(String key, boolean trueOrFalse)
	{
		if (trueOrFalse)
		{
			pagesValid.put(key, Boolean.TRUE);
		}
		else
		{
			pagesValid.put(key, Boolean.FALSE);
		}
	}
	public static Page getPage(String key, IWContext iwc)
	{
		Page theReturn = null;
		IBXMLPage xml = null;
		xml = getXML(key);
		if (xml != null)
		{
			return (Page) xml.getPopulatedPage().clone(iwc);
			//return (Page)xml.getPopulatedPage().clone();
		}
		return null;
	}
	public static Page getPage(String key)
	{
		Page theReturn = null;
		IBXMLPage xml = null;
		xml = getXML(key);
		if (xml != null)
		{
			return (Page) xml.getPopulatedPage().clone();
			//return (Page)xml.getPopulatedPage();
		}
		return null;
	}
	/*public static Page getPage(String key, InputStream streamWithXML)throws Exception{
	  Page theReturn = null;
	  IBXMLPage xml = null;
	  xml = getXML(key,streamWithXML);
	  if(xml!=null){
	    return (Page)xml.getPopulatedPage().clone();
	  }
	  return null;
	}*/
	public static IBXMLPage getXML(String key)
	{
		IBXMLPage xml = null;
		if (isPageInvalid(key))
		{
			xml = new IBXMLPage(false, key);
			setPage(key, xml);
		}
		else
		{
			xml = getXMLPageCached(key);
		}
		return xml;
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
	private static Object setPage(String key, IBXMLPage page)
	{
		flagPageValid(key, true);
		return getPageCacheMap().put(key, page);
	}
	private static Map getPageCacheMap()
	{
		return pageCache;
	}
	public static IBXMLPage getXMLPageCached(String key)
	{
		return (IBXMLPage) getPageCacheMap().get(key);
	}
	/**
	 * A function that gets the IBXMLPage if it exists in cache, otherwise it returns null.
	 *
	 * @param key The id of the IBXMLPage to get from cache.
	 *
	 * @return The IBXMLPage with id = key if it exists in cache, null otherwise.
	 */
	public static IBXMLPage getXMLIfInCache(String key)
	{
		if (isPageInvalid(key))
		{
			return null;
		}
		else
		{
			IBXMLPage xml = getXMLPageCached(key);
			return xml;
		}
	}
	/**
	 * Method flagAllPagesInvalid.
	 */
	public synchronized static void flagAllPagesInvalid()
	{
		pagesValid.clear();
		pageCache.clear();
	}
}