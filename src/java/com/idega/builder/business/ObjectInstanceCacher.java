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

import java.util.Hashtable;
import java.util.Map;
import java.util.HashMap;

public class ObjectInstanceCacher{

  private static Map objectInstanceCache = new HashMap();

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

  public static PresentationObject getObjectInstanceCached(int key ){
    return getObjectInstanceCached(Integer.toString(key));
  }


  public static PresentationObject getObjectInstanceClone(int key, IWContext iwc){
    return getObjectInstanceClone(Integer.toString(key),iwc);
  }


  public static Object setObjectInstance(String key, PresentationObject objectInstance){
    return getObjectInstanceCacheMap().put(key,objectInstance);
  }

  private static Map getObjectInstanceCacheMap(){
    return objectInstanceCache;
  }




}