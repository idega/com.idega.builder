package com.idega.builder.business;

/**
 * Title:        idegaclasses
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author
 * @version 1.0
 */

import com.idega.jmodule.object.Page;

import com.idega.builder.data.IBPage;

import com.idega.exception.PageDoesNotExist;

import java.util.Hashtable;
import java.util.Map;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class PageCacher{

  private static Map pageCache = new Hashtable();
  private static Map pagesValid = new Hashtable();

  private PageCacher(){

  }

  protected static boolean isPageValid(String key){
    boolean theReturn = false;
    Boolean fetched = (Boolean)pagesValid.get(key);
    if(fetched!=null){
      theReturn = fetched.booleanValue();
    }
    return theReturn;
  }

  protected static boolean isPageInvalid(String key){
    return !isPageValid(key);
  }

  public static void flagPageInvalid(String key){
    flagPageValid(key,false);
  }

  public static void flagPageValid(String key,boolean trueOrFalse){
    if(trueOrFalse){
      pagesValid.put(key,Boolean.TRUE);
    }
    else{
      pagesValid.put(key,Boolean.FALSE);
    }
  }


  public static Page getPage(String key ){
    Page theReturn = null;
    IBXMLPage xml = null;
    xml = getXML(key);
    if(xml!=null){
      return (Page)xml.getPopulatedPage().clone();
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


  public static IBXMLPage getXML(String key){
    IBXMLPage xml = null;
    if (isPageInvalid(key)){
      xml = new IBXMLPage(false,key);
    }
    else{
      xml = getXMLPageCached(key);
    }
    return xml;

    /*
    if (isPageInvalid(key)){
      try{
        IBPage ibpage = new IBPage(Integer.parseInt(key));
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

  private static Object setPage(String key, IBXMLPage page){
    flagPageValid(key,true);
    return getPageCacheMap().put(key,page);
  }

  private static Map getPageCacheMap(){
    return pageCache;
  }


  public static IBXMLPage getXMLPageCached(String key){
    return (IBXMLPage)getPageCacheMap().get(key);
  }

}