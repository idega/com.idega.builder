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

import javax.ejb.FinderException;

import com.idega.core.builder.data.ICPage;
import com.idega.core.builder.data.ICPageHome;
import com.idega.data.IDOLookupException;
import com.idega.presentation.IWContext;
import com.idega.presentation.Page;
public class PageCacher
{
	//Static variables:
	private static PageCacher instance;
	
	//Instance variables:
	private Map pageCache = new WeakHashMap();
	private Map pagesValid = new HashMap();
	private PageCacher()
	{}
	protected static PageCacher getInstance(){
		if(instance==null){
			instance=new PageCacher();
		}
		return instance;
	}
	
	public void unload(){
		instance=null;
	}
	
	
	protected static boolean isPageValid(String key)
	{
		boolean theReturn = false;
		Boolean fetched = (Boolean) getInstance().pagesValid.get(key);
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
			getInstance().pagesValid.put(key, Boolean.TRUE);
		}
		else
		{
			getInstance().pagesValid.put(key, Boolean.FALSE);
		}
	}
	public static Page getPage(String key, IWContext iwc)
	{
		IBXMLPage xml = null;
		xml = getXML(key);
		if (xml != null)
		{
			return (Page) xml.getPopulatedPage().clonePermissionChecked(iwc);
			//return (Page)xml.getPopulatedPage().clone();
		}
		return null;
	}
	public static Page getPage(String key)
	{
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
	
	public static void storePage(String key,String format,String stringRepresentation)throws Exception{
		IBXMLPage bPage = getXML(key);
		//flagPageInvalid(key);
		bPage.setPageFormat(format);
		bPage.setSourceFromString(stringRepresentation);
		bPage.store();
		flagPageInvalid(key);
	}
	
	
	public static IBXMLPage getXML(String key)
	{
		IBXMLPage bPage = null;
		if (isPageInvalid(key))
		{
			ICPageHome pHome;
			try {
				pHome = (ICPageHome) com.idega.data.IDOLookup.getHome(ICPage.class);
				int pageId = Integer.parseInt(key);
				ICPage ibpage = pHome.findByPrimaryKey(pageId);
				
				if(ibpage.getIsFormattedInIBXML()){
					bPage = new IBXMLPage();
				}
				else if (ibpage.getIsFormattedInHTML()){
					bPage= new HtmlBasedPage();
				}
				bPage.setPageKey(key);
				setPage(key, bPage);
				
			} catch (IDOLookupException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (FinderException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			
		}
		else
		{
			bPage = getXMLPageCached(key);
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
	private static Object setPage(String key, IBXMLPage page)
	{
		flagPageValid(key, true);
		return getPageCacheMap().put(key, page);
	}
	private static Map getPageCacheMap()
	{
		return getInstance().pageCache;
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
		getInstance().pagesValid.clear();
		getInstance().pageCache.clear();
	}
}