package com.idega.builder.business;

/**
 * Title:        idegaclasses
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author
 * @version 1.0
 */

import com.idega.presentation.PresentationObject;
import com.idega.presentation.IWContext;
import com.idega.presentation.Page;

import java.util.Hashtable;
import java.util.Map;
import java.util.HashMap;

public class ObjectInstanceCacher{

  private static Map _objectInstanceCache = new HashMap();
  private static Map _objectInstanceCacheForPage = new HashMap();

  private ObjectInstanceCacher(){

  }


  public static PresentationObject getObjectInstanceCached(String key){
    return (PresentationObject)getObjectInstanceCacheMap().get(key);
  }

  public static PresentationObject getObjectInstanceClone(String key, IWContext iwc){
    PresentationObject obj = getObjectInstanceCached(key);
    if(obj != null){
      return (PresentationObject)obj.clone(iwc);
    }else {
      return null;
    }
  }

  public static Map getObjectInstancesCachedForPage(String pageKey){
    Map map = getObjectInstanceCacheMapForPage();
    if(map != null){
      return (Map)map.get(pageKey);
    }else{
      return null;
    }
  }

  public static Map getObjectInstancesCachedForPage(int pageKey ){
    return getObjectInstancesCachedForPage(Integer.toString(pageKey));
  }

  public static PresentationObject getObjectInstanceCached(int key ){
    return getObjectInstanceCached(Integer.toString(key));
  }


  public static PresentationObject getObjectInstanceClone(int key, IWContext iwc){
    return getObjectInstanceClone(Integer.toString(key),iwc);
  }

  public static void setTemplateObjectsForPage(IBXMLPage ibxml){
    setObjectInstance(ibxml, null, null);
  }

  public static void setObjectInstance(IBXMLPage ibxml, String instanceKey, PresentationObject objectInstance){
    if(instanceKey != null){
      getObjectInstanceCacheMap().put(instanceKey,objectInstance);
    }
    //System.err.println("Cashing objectInstance: "+instanceKey);
    Map map = getObjectInstancesCachedForPage(ibxml.getKey());
    if(map == null){
      String templateKey = Integer.toString(ibxml.getTemplateId());
      Map templateMap = getObjectInstancesCachedForPage(templateKey);
      if(templateMap != null){
        //System.err.println("geting template Map");
        map = (Map)((Hashtable)templateMap).clone();
      } else {
        //System.err.println("creating new Map");
        map = new Hashtable();
      }
      getObjectInstanceCacheMapForPage().put(ibxml.getKey(),map);
    }

    //System.err.println("Cashing objectInstance: "+instanceKey+" on page "+ ibxml.getKey()+" extending: "+ibxml.getTemplateId());
    if(instanceKey != null){
      getObjectInstancesCachedForPage(ibxml.getKey()).put(instanceKey,objectInstance);
    }
  }

  public static void changeObjectInstanceID(Page page, String oldInstanceKey, String newInstanceKey, PresentationObject newObjectInstance){
    if(newInstanceKey != null){
      getObjectInstanceCacheMap().put(newInstanceKey,newObjectInstance);
    }
    //System.err.println("Cashing objectInstance: "+instanceKey);
    Map map = getObjectInstancesCachedForPage(page.getPageID());
    if(map == null){
      String templateKey = page.getTemplateId();
      Map templateMap = getObjectInstancesCachedForPage(templateKey);
      if(templateMap != null){
        //System.err.println("geting template Map");
        map = (Map)((Hashtable)templateMap).clone();
      } else {
        //System.err.println("creating new Map");
        map = new Hashtable();
      }
      getObjectInstanceCacheMapForPage().put(Integer.toString(page.getPageID()),map);
    }

    //System.err.println("Cashing objectInstance: "+instanceKey+" on page "+ ibxml.getKey()+" extending: "+ibxml.getTemplateId());
    if(oldInstanceKey != null){
      getObjectInstancesCachedForPage(page.getPageID()).remove(oldInstanceKey);
    }

    if(newInstanceKey != null){
      getObjectInstancesCachedForPage(page.getPageID()).put(newInstanceKey,newObjectInstance);
    }
  }




  private static Map getObjectInstanceCacheMap(){
    return _objectInstanceCache;
  }

  private static Map getObjectInstanceCacheMapForPage(){
    return _objectInstanceCacheForPage;
  }




}