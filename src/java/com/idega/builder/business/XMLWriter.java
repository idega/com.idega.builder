/*
 * $Id: XMLWriter.java,v 1.25 2002/01/15 14:10:39 tryggvil Exp $
 *
 * Copyright (C) 2001 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package com.idega.builder.business;

import java.util.List;
import java.util.Iterator;
import java.util.Vector;
import java.sql.SQLException;
import com.idega.core.data.ICObject;
import com.idega.core.data.ICObjectInstance;
import com.idega.idegaweb.IWMainApplication;
import com.idega.xml.XMLElement;
import com.idega.xml.XMLAttribute;
import com.idega.xml.XMLException;
import com.idega.core.data.ICObjectInstance;

/**
 * @author <a href="tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */
public class XMLWriter {

  private static final String EMPTY_STRING="";

  /**
   *
   */
  private XMLWriter() {
  }

  /**
   *
   */
  private static XMLElement getPageRootElement(IBXMLAble xml){
    return xml.getPageRootElement();
  }

  /**
   *
   */
  private static XMLElement findRegion(IBXMLAble xml, String id){
    return findXMLElement(xml,id,XMLConstants.REGION_STRING);
  }

  /**
   *
   */
  private static XMLElement findRegion(IBXMLAble xml, String id, XMLElement enclosingModule){
    return findXMLElementInside(xml,id,XMLConstants.REGION_STRING,enclosingModule);
  }

  /**
   *
   */
  private static XMLElement findModule(IBXMLAble xml, int id){
    return findXMLElement(xml,Integer.toString(id),XMLConstants.MODULE_STRING);
  }

  /**
   *
   */
  private static XMLElement findModule(IBXMLAble xml, int id, XMLElement startElement){
    return findXMLElementInside(xml,Integer.toString(id),XMLConstants.MODULE_STRING,startElement);
  }

  /**
   * Returns null if nothing found
   */
  private static XMLElement findXMLElement(IBXMLAble xml, String id, String name){
    return findXMLElementInside(xml,id,name,getPageRootElement(xml));
  }

  /**
   * Returns null if nothing found.
   *
   * If name is null it searches all elements with any name
   */
  private static XMLElement findXMLElementInside(IBXMLAble xml, String id, String name, XMLElement parentElement){
    List list = parentElement.getChildren();
    boolean nameIsNull;
    if (id != null) {
      try {
        int theID = Integer.parseInt(id);
        //Hardcoded -1 for the top Page element
        if (theID == -1) {
          return getPageRootElement(xml);
        }
      }
      catch(NumberFormatException e){

      }
    }

    if(name==null){
      nameIsNull=true;
    }
    else{
      nameIsNull=true;
    }

    if(list !=null){
      Iterator iter = list.iterator();
      while (iter.hasNext()) {
        XMLElement element = (XMLElement)iter.next();
        //if(element.getName().equals(name)||nameIsNull){
          //List attributes = element.getAttributes();
          //if(attributes!=null){
          //Iterator iter2 = attributes.iterator();
          //while (iter2.hasNext()){
            XMLAttribute attr = element.getAttribute(XMLConstants.ID_STRING);
            //XMLAttribute attr = (XMLAttribute)iter2.next();
            //if(item2.getName().equals(ID_STRING)){
              if(attr!=null){
                if(attr.getValue().equals(id)){
                  return element;
                }
              }
            //}
          //}

          //}
        //}
        //else{
          XMLElement el = findXMLElementInside(xml,id,null,element);
          if(el!=null){
            return el;
          }
        //}
      }
    }
    return null;
  }

  /**
   *
   */
  private static XMLElement findProperty(IBXMLAble xml,int ObjectInstanceId,String propertyName){
    XMLElement elem = findModule(xml,ObjectInstanceId);
    return findProperty(elem,propertyName);
  }

  /**
   * Returns null if nothing found
   */
  private static XMLElement findProperty(IWMainApplication iwma,int ICObjectInstanceID,XMLElement parentElement,String propertyName,String[] values){
    List elementList = findProperties(parentElement,propertyName);
    if(elementList!=null){
      Iterator iter = elementList.iterator();
      while (iter.hasNext()) {
        XMLElement item = (XMLElement)iter.next();
        if(hasPropertyElementSpecifiedValues(iwma,ICObjectInstanceID,item,values,true)) return item;
      }
    }
    return null;
  }

  /**
   * Returns true if a propertyElement has the specified values, else false
   */
  static boolean hasPropertyElementSpecifiedValues(IWMainApplication iwma,int ICObjectInstanceID,XMLElement propertyElement,String[] values,boolean withPrimaryKeyCheck){
    boolean check = true;
    int counter = 0;
    List valueList = propertyElement.getChildren(XMLConstants.VALUE_STRING);
    Iterator iter = valueList.iterator();
    while(check && counter < values.length ){
      try{
        String methodIdentifier = getPropertyNameForElement(propertyElement);
        boolean isPrimaryKey = IBPropertyHandler.getInstance().isMethodParameterPrimaryKey(iwma,ICObjectInstanceID,methodIdentifier,counter);
        XMLElement eValue = (XMLElement)iter.next();
        if(withPrimaryKeyCheck){
          if(isPrimaryKey){
            if( ! eValue.getText().equals(values[counter])) check = false;
          }
        }
        else{
          if( ! eValue.getText().equals(values[counter])) check = false;
        }
      }
      catch(Exception e){
        return false;
      }
      counter++;
    }
    return check;
  }


  static String getPropertyNameForElement(XMLElement propertyElement){
    if(propertyElement!=null){
      return propertyElement.getChild(XMLConstants.NAME_STRING).getText();
    }
    return null;

  }


  /**
   *Returns a List of XMLElement objects corresponding to the specified propertyName
   *Returns null if no match
   */
  private static List findProperties(XMLElement parentElement,String propertyName){
    XMLElement elem = parentElement;
    List theReturn = null;
    if(elem != null){
      List properties = elem.getChildren();
      if(properties != null){
        Iterator iter = properties.iterator();
        while (iter.hasNext()) {
          XMLElement pElement = (XMLElement)iter.next();
          if(pElement!=null){
            if(pElement.getName().equals(XMLConstants.PROPERTY_STRING)){
              XMLElement name = pElement.getChild(XMLConstants.NAME_STRING);
              if(name!=null){
                if(name.getText().equals(propertyName)){
                  if(theReturn==null){
                    theReturn = new Vector();
                  }
                  theReturn.add(pElement);
                }
              }
            }
          }
        }
      }
    }
    return theReturn;
  }


  /**
   *
   */
  private static XMLElement findProperty(XMLElement parentElement,String propertyName){
    XMLElement elem = parentElement;
    if(elem != null){
      List properties = elem.getChildren();
      if(properties != null){
        Iterator iter = properties.iterator();
        while (iter.hasNext()) {
          XMLElement pElement = (XMLElement)iter.next();
          if(pElement!=null){
            if(pElement.getName().equals(XMLConstants.PROPERTY_STRING)){
              XMLElement name = pElement.getChild(XMLConstants.NAME_STRING);
              if(name!=null){
                if(name.getText().equals(propertyName)){
                  return pElement;
                }
              }
            }
          }
        }
      }
    }
    return null;
  }

  /**
   * Returns a List of String[]
   */
  static List getPropertyValues(IBXMLAble xml,int ObjectInstanceId,String propertyName){
    XMLElement module = findModule(xml,ObjectInstanceId);
    List theReturn = com.idega.util.ListUtil.getEmptyList();
    List propertyList = findProperties(module,propertyName);
    if(propertyList!=null){
      theReturn = new Vector();
      Iterator iter = propertyList.iterator();
      while (iter.hasNext()) {
        XMLElement property = (XMLElement)iter.next();
        if(property!=null){
          List list = property.getChildren(XMLConstants.VALUE_STRING);
          String[] array = new String[list.size()];
          Iterator iter2 = list.iterator();
          int counter = 0;
          while (iter2.hasNext()) {
            XMLElement el = (XMLElement)iter2.next();
            String theString = el.getText();
            array[counter]=theString;
            counter++;
          }
          theReturn.add(array);
        }
      }
    }
    return theReturn;
  }

  /**
   * Returns the first property if there is an array of properties set
   */
  static String getProperty(IBXMLAble xml,int ObjectInstanceId,String propertyName){
    XMLElement module = findModule(xml,ObjectInstanceId);
    XMLElement property = findProperty(module,propertyName);
    if(property!=null){
      XMLElement value = property.getChild(XMLConstants.VALUE_STRING);
      return value.getText();
    }
    return null;
  }

  /**
   *
   */
  static boolean removeProperty(IWMainApplication iwma,IBXMLAble xml,int ICObjectInstanceId,String propertyName,String[] values){
      XMLElement module = findModule(xml,ICObjectInstanceId);
      if(module!=null){
        XMLElement property = findProperty(iwma,ICObjectInstanceId,module,propertyName,values);
        if(property!=null){
          return module.removeContent(property);
        }
        else{
          return false;
        }
      }
      else{
        return false;
      }
  }

  /**
   *
   */
  static boolean setProperty(IWMainApplication iwma,IBXMLAble xml,int ObjectInstanceId,String propertyName,String propertyValue){
      String[] values = {propertyValue};
      return setProperty(iwma,xml,ObjectInstanceId,propertyName,values,false);
  }


  /**
   * Checks if the propertyValue array is correctly formcatted
   * (Not with empty strings or null values)
   */
  private static boolean isPropertyValueArrayValid(String[] propertyValues){
    for (int i = 0; i < propertyValues.length; i++) {
      String s = propertyValues[i];
      if(s==null){
        return false;
      }
      else{
        if(s.equals(EMPTY_STRING))
          return false;
      }
    }
    return true;
  }


  /**
   * Returns true if properties changed, else false
   */
  static boolean setProperty(IWMainApplication iwma,IBXMLAble xml,int ICObjectInstanceId,String propertyName,String[] propertyValues,boolean allowMultiValued){

    //Checks if the propertyValues array is correctly formatted
    if(!isPropertyValueArrayValid(propertyValues))
      return false;

    boolean changed = false;
    XMLElement module = findModule(xml,ICObjectInstanceId);
    XMLElement property = null;
    if(allowMultiValued){
      property = findProperty(iwma,ICObjectInstanceId,module,propertyName,propertyValues);
    }
    else{
      property = findProperty(module,propertyName);
    }

    if(property==null){
      property = getNewProperty(propertyName,propertyValues);
      module.addContent(property);
      changed=true;
    }
    else{
      List values = property.getChildren(XMLConstants.VALUE_STRING);
      if(values!=null){
        Iterator iter = values.iterator();
        int index = 0;
        while (iter.hasNext()) {

          String propertyValue = propertyValues[index];
          XMLElement value = (XMLElement)iter.next();
          String currentValue = value.getText();
          if(!currentValue.equals(propertyValue)){
            value.setText(propertyValue);
            changed=true;
          }
          index++;
        }
      }
      else{
        for (int index = 0; index < propertyValues.length; index++) {
            String propertyValue = propertyValues[index];
            XMLElement value = new XMLElement(XMLConstants.VALUE_STRING);
            value.addContent(propertyValue);
            property.addContent(value);
            changed=true;
        }

      }
    }
    return changed;
  }

  /**
   *
   */
  private static XMLElement getNewProperty(String propertyName,Object[] propertyValues){

    XMLElement element = new XMLElement(XMLConstants.PROPERTY_STRING);
    XMLElement name = new XMLElement(XMLConstants.NAME_STRING);
    for (int i = 0; i < propertyValues.length; i++) {
      XMLElement value = new XMLElement(XMLConstants.VALUE_STRING);
      XMLElement type = new XMLElement(XMLConstants.TYPE_STRING);
      Object propertyValue = propertyValues[i];
      if(i==0){
        element.addContent(name);
        name.addContent(propertyName);
      }
      element.addContent(value);
      element.addContent(type);
      value.addContent(propertyValue.toString());
      type.addContent(propertyValue.getClass().getName());
    }
    return element;
  }

  /**
   *
   */
  private static boolean addNewModule(XMLElement parent,int newICObjectTypeID){
    //XMLElement parent = findModule(parentObjectInstanceID);
    if(parent!=null){
      try{
        ICObjectInstance instance = new ICObjectInstance();
        instance.setICObjectID(newICObjectTypeID);
        instance.insert();

        ICObject obj = new ICObject(newICObjectTypeID);
        Class theClass = obj.getObjectClass();

        XMLElement newElement = new XMLElement(XMLConstants.MODULE_STRING);
        XMLAttribute aId = new XMLAttribute(XMLConstants.ID_STRING,Integer.toString(instance.getID()));
        XMLAttribute aIcObjectId = new XMLAttribute(XMLConstants.IC_OBJECT_ID_STRING,Integer.toString(newICObjectTypeID));
        XMLAttribute aClass = new XMLAttribute(XMLConstants.CLASS_STRING,theClass.getName());

//        newElement.addAttribute(aId);
//        newElement.addAttribute(aIcObjectId);
//        newElement.addAttribute(aClass);
        newElement.setAttribute(aId);
        newElement.setAttribute(aIcObjectId);
        newElement.setAttribute(aClass);

        parent.addContent(newElement);

      }
      catch(Exception e){
        e.printStackTrace();
        return false;
      }

      return true;

    }
    return false;
  }

  /**
   *
   */
  static boolean addLabel(IBXMLAble xml, int parentObjectInstanceId, int xpos, int ypos, String label) {
    return(true);
  }

  /**
   *
   */
  static boolean addNewModule(IBXMLAble xml,int parentObjectInstanceID,int newICObjectID,int xpos,int ypos, String label) {
    String regionId = parentObjectInstanceID + "." + xpos + "." + ypos;
    XMLElement region = findRegion(xml,regionId);

    if(region==null){
      region = new XMLElement(XMLConstants.REGION_STRING);
      XMLAttribute id = new XMLAttribute(XMLConstants.ID_STRING,regionId);
//      region.addAttribute(id);
      region.setAttribute(id);
      addNewModule(region,newICObjectID);
      XMLElement parent = findModule(xml,parentObjectInstanceID);
      if (parent != null)
        parent.addContent(region);
      else { //Þetta er í síðu sem extendar template
        if (label != null) {
          XMLAttribute labelAttribute = new XMLAttribute(XMLConstants.LABEL_STRING,label);
//          region.addAttribute(labelAttribute);
          region.setAttribute(labelAttribute);
        }
        xml.getPageRootElement().addContent(region);
      }
    }
    else{
      addNewModule(region,newICObjectID);
    }
    return true;

  }

  /**
   *
   */
  static boolean addNewModule(IBXMLAble xml,int parentObjectInstanceID,int newICObjectID, String label){
    return addNewModule(findModule(xml,parentObjectInstanceID),newICObjectID);
  }

  /**
   *
   */
  static boolean addNewModule(IBXMLAble xml,String parentObjectInstanceID,int newICObjectID,String label){
    try{
      return addNewModule(findModule(xml,Integer.parseInt(parentObjectInstanceID)),newICObjectID);
    }
    catch(NumberFormatException nfe){

      int parentID = Integer.parseInt(parentObjectInstanceID.substring(0,parentObjectInstanceID.indexOf(".")));
      String theRest = parentObjectInstanceID.substring(parentObjectInstanceID.indexOf(".")+1,parentObjectInstanceID.length());

      int xpos = Integer.parseInt(theRest.substring(0,theRest.indexOf(".")));
      int ypos = Integer.parseInt(theRest.substring(theRest.indexOf(".")+1,theRest.length()));

      return addNewModule(xml,parentID,newICObjectID,xpos,ypos,label);
    }
  }

  /**
   *
   */
  static boolean addNewModule(IBXMLAble xml,String parentObjectInstanceID,ICObject newObjectType, String label){
    return addNewModule(xml,parentObjectInstanceID,newObjectType.getID(),label);
  }

  /**
   *
   */
  static boolean deleteModule(IBXMLAble xml,String parentObjectInstanceID,int ICObjectInstanceID){
    XMLElement parent = findXMLElement(xml,parentObjectInstanceID,null);
    if(parent!=null){
      try{
        XMLElement module = findModule(xml,ICObjectInstanceID,parent);
        return deleteModule(parent,module);
      }
      catch(Exception e){
        e.printStackTrace();
        return false;
      }
    }
    return false;
  }

  /**
   *
   */
  static boolean lockRegion(IBXMLAble xml, String parentObjectInstanceID) {
    XMLElement parent = findXMLElement(xml,parentObjectInstanceID,null);
    if (parent != null) {
      XMLAttribute lock = new XMLAttribute(XMLConstants.REGION_LOCKED,"true");
//      if (parent.getAttribute(XMLConstants.REGION_LOCKED) != null)
//        parent.removeAttribute(XMLConstants.REGION_LOCKED);
//      parent.addAttribute(lock);
      parent.setAttribute(lock);
      return(true);
    }
    else {
      int index = parentObjectInstanceID.indexOf(".");
      if (index != -1) {
        XMLElement region = new XMLElement(XMLConstants.REGION_STRING);
        XMLAttribute id = new XMLAttribute(XMLConstants.ID_STRING,parentObjectInstanceID);
//        region.addAttribute(id);
        region.setAttribute(id);

        int parentID = Integer.parseInt(parentObjectInstanceID.substring(0,index));
        XMLElement regionParent = findModule(xml,parentID);
        if (regionParent != null)
          regionParent.addContent(region);

        XMLAttribute lock = new XMLAttribute(XMLConstants.REGION_LOCKED,"true");
//        region.addAttribute(lock);
        region.setAttribute(lock);

        return(true);
      }
    }

    return(false);
  }

  /**
   *
   */
  static boolean setAttribute(IBXMLAble xml, String parentObjectInstanceID, String attributeName, String attributeValue) {
    XMLElement parent = findXMLElement(xml,parentObjectInstanceID,null);
    if (parent != null) {
      XMLAttribute attribute = new XMLAttribute(attributeName,attributeValue);
//      if (parent.getAttribute(attributeName) != null)
//        parent.removeAttribute(attributeName);
//      parent.addAttribute(attribute);
      parent.setAttribute(attribute);
      return(true);
    }

    return(false);
  }

  /**
   *
   */
  static boolean unlockRegion(IBXMLAble xml, String parentObjectInstanceID) {
    XMLElement parent = findXMLElement(xml,parentObjectInstanceID,null);
    if (parent != null) {
      XMLAttribute lock = new XMLAttribute(XMLConstants.REGION_LOCKED,"false");
//      if (parent.getAttribute(XMLConstants.REGION_LOCKED) != null)
//        parent.removeAttribute(XMLConstants.REGION_LOCKED);
//      parent.addAttribute(lock);
      parent.setAttribute(lock);
      return(true);
    }
    else {
      int index = parentObjectInstanceID.indexOf(".");
      if (index != -1) {
        XMLElement region = new XMLElement(XMLConstants.REGION_STRING);
        XMLAttribute id = new XMLAttribute(XMLConstants.ID_STRING,parentObjectInstanceID);
//        region.addAttribute(id);
        region.setAttribute(id);

        int parentID = Integer.parseInt(parentObjectInstanceID.substring(0,index));
        XMLElement regionParent = findModule(xml,parentID);
        if (regionParent != null)
          regionParent.addContent(region);

        XMLAttribute lock = new XMLAttribute(XMLConstants.REGION_LOCKED,"false");
//        region.addAttribute(lock);
        region.setAttribute(lock);

        return(true);
      }
    }


    return(false);
  }

  /**
   *
   */
  private static boolean deleteModule(XMLElement parent,XMLElement child)throws Exception{
        List children = getChildElements(child);
        if(children!=null){
          Iterator iter = children.iterator();
          while (iter.hasNext()) {
            XMLElement childchild = (XMLElement)iter.next();
            deleteModule(child,childchild);
          }
          XMLAttribute attribute = child.getAttribute(XMLConstants.ID_STRING);
          if(attribute!=null){
            String ICObjectInstanceID = attribute.getValue();
            try{
              ICObjectInstance instance = new ICObjectInstance(Integer.parseInt(ICObjectInstanceID));
              instance.delete();
            }
            catch(NumberFormatException e){
            }
          }
        }
        parent.removeContent(child);
        return true;
  }

  /**
   *
   */
  private static List getChildElements(XMLElement parent){
    return parent.getChildren();
  }

  /**
   *
   */
  private static List getChildModules(XMLElement parent){
    List children = parent.getChildren();
    Iterator iter = children.iterator();
    while (iter.hasNext()) {
      XMLElement item = (XMLElement)iter.next();
      if(item.getName().equals(XMLConstants.REGION_STRING)){
        children.addAll(getChildModules((XMLElement)item));
      }
      else if(!item.getName().equals(XMLConstants.MODULE_STRING)){
        iter.remove();
      }
    }
    return children;
  }

  /**
   *
   */
  static boolean labelRegion(IBXMLAble xml, String parentObjectInstanceID, String label) {
    XMLElement parent = findXMLElement(xml,parentObjectInstanceID,null);
    if (parent != null) {
      if (label != null && !label.equals("")) {
        XMLAttribute labelAttribute = new XMLAttribute(XMLConstants.LABEL_STRING,label);
//        if (parent.getAttribute(XMLConstants.LABEL_STRING) != null)
//          parent.removeAttribute(XMLConstants.LABEL_STRING);
//        parent.addAttribute(labelAttribute);
        parent.setAttribute(labelAttribute);
      }
      else {
        if (parent.getAttribute(XMLConstants.LABEL_STRING) != null)
          parent.removeAttribute(XMLConstants.LABEL_STRING);
      }

      return(true);
    }
    else {
      int index = parentObjectInstanceID.indexOf(".");
      if (index != -1) {
        if (label != null && !label.equals("")) {
          XMLElement region = new XMLElement(XMLConstants.REGION_STRING);
          XMLAttribute id = new XMLAttribute(XMLConstants.ID_STRING,parentObjectInstanceID);
//          region.addAttribute(id);
          region.setAttribute(id);

          int parentID = Integer.parseInt(parentObjectInstanceID.substring(0,index));
          XMLElement regionParent = findModule(xml,parentID);
          if (regionParent != null)
            regionParent.addContent(region);

          XMLAttribute labelAttribute = new XMLAttribute(XMLConstants.LABEL_STRING,label);
//          region.addAttribute(labelAttribute);
          region.setAttribute(labelAttribute);

          return(true);
        }
      }
    }

    return(false);
  }

  /**
   *
   */
  static boolean copyModule(IBXMLAble xml, String parentObjectInstanceID, int ICObjectInstanceID) {
    XMLElement parent = findXMLElement(xml,parentObjectInstanceID,null);
    if (parent != null) {
      try {
        XMLElement module = findModule(xml,ICObjectInstanceID,parent);
        return(copyModule(parent,module));
      }
      catch(Exception e) {
        e.printStackTrace();
        return(false);
      }
    }

    return(false);
  }

  /**
   *
   */
  private static boolean copyModule(XMLElement parent, XMLElement child) throws Exception {
    List children = getChildElements(child);
    if (children != null) {
      Iterator iter = children.iterator();
      while (iter.hasNext()) {
        XMLElement childchild = (XMLElement)iter.next();
        copyModule(child,childchild);
      }
      XMLAttribute attribute = child.getAttribute(XMLConstants.ID_STRING);
      if (attribute != null) {
        String ICObjectInstanceID = attribute.getValue();
        try {
          ICObjectInstance instance = new ICObjectInstance(Integer.parseInt(ICObjectInstanceID));
          instance.delete();
        }
        catch(NumberFormatException e){
        }
      }
    }

    return(true);
  }

  /**
   *
   */
  static boolean addNewElement(IBXMLAble xml, int parentObjectInstanceID, XMLElement element) {
    XMLElement parent = findModule(xml,parentObjectInstanceID);
    if (parent != null)
      parent.addContent(element);

    return true;
  }

  /**
   *
   */
  static boolean pasteElement(IBXMLAble xml, String parentObjectInstanceID, XMLElement element) {
    changeModuleIds(element);
    XMLElement parent = findXMLElement(xml,parentObjectInstanceID,null);
    if (parent != null) {
      parent.addContent(element);

      return(true);
    }
    else {
      int index = parentObjectInstanceID.indexOf(".");
      if (index != -1) {
        XMLElement region = new XMLElement(XMLConstants.REGION_STRING);
        XMLAttribute id = new XMLAttribute(XMLConstants.ID_STRING,parentObjectInstanceID);
        region.setAttribute(id);

        int parentID = Integer.parseInt(parentObjectInstanceID.substring(0,index));
        XMLElement regionParent = findModule(xml,parentID);
        if (regionParent != null)
          regionParent.addContent(region);
        else
          xml.getPageRootElement().addContent(region);

        region.addContent(element);

        return(true);
      }
    }

    return(false);
  }

  /**
   *
   */
  static boolean pasteElementAbove(IBXMLAble xml, String parentObjectInstanceID, String objectId, XMLElement element) {
    changeModuleIds(element);
    XMLElement parent = findXMLElement(xml,parentObjectInstanceID,null);
    if (parent != null) {
//      parent.addContent(element);
      List li = parent.getChildren();
      int index = -1;
      if (li != null) {
        Iterator it = li.iterator();
        while (it.hasNext()) {
          XMLElement el = (XMLElement)it.next();
          index++;
          if (el.getName().equals(XMLConstants.MODULE_STRING)) {
            XMLAttribute id = el.getAttribute(XMLConstants.ID_STRING);
            if (id != null) {
              if (id.getValue().equals(objectId))
                break;
            }
          }
        }

        if (index != -1) {
          parent.removeChildren();
          it = li.iterator();
          int counter = -1;
          while (it.hasNext()) {
            counter++;
            if (counter == index)
              parent.addContent(element);
            XMLElement el = (XMLElement)it.next();
            parent.addContent(el);
          }
        }
      }
      else
        parent.addContent(element); //hmmmm

      return(true);
    }
/*    else {
      int index = parentObjectInstanceID.indexOf(".");
      if (index != -1) {
        XMLElement region = new XMLElement(XMLConstants.REGION_STRING);
        XMLAttribute id = new XMLAttribute(XMLConstants.ID_STRING,parentObjectInstanceID);
        region.setAttribute(id);

        int parentID = Integer.parseInt(parentObjectInstanceID.substring(0,index));
        XMLElement regionParent = findModule(xml,parentID);
        if (regionParent != null)
          regionParent.addContent(region);

        region.addContent(element);

        return(true);
      }
    }*/

    return(false);
  }

  /**
   *
   */
  private static boolean changeModuleIds(XMLElement element) {
    try {
      XMLAttribute attribute = element.getAttribute(XMLConstants.ID_STRING);
      XMLAttribute object_id = element.getAttribute(XMLConstants.IC_OBJECT_ID_STRING);

      ICObjectInstance instance = new ICObjectInstance();
      instance.setICObjectID(object_id.getIntValue());
      instance.insert();

      attribute = new XMLAttribute(XMLConstants.ID_STRING,Integer.toString(instance.getID()));
      element.setAttribute(attribute);

      List childs = element.getChildren(XMLConstants.MODULE_STRING);
      if (childs != null) {
        Iterator it = childs.iterator();
        while (it.hasNext()) {
          XMLElement child = (XMLElement)it.next();
          if (!changeModuleIds(child))
            return(false);
        }
      }

      childs = element.getChildren(XMLConstants.REGION_STRING);
      if (childs != null) {
        Iterator it = childs.iterator();
        while (it.hasNext()) {
          XMLElement el = (XMLElement)it.next();
          List childs2 = el.getChildren(XMLConstants.MODULE_STRING);
          if (childs2 != null) {
            Iterator it2 = childs2.iterator();
            while (it2.hasNext()) {
              XMLElement child = (XMLElement)it2.next();
              if (!changeModuleIds(child))
                return(false);
            }
          }
        }
      }

      return(true);
    }
    catch(SQLException e) {
      return(false);
    }
    catch(XMLException e) {
      return(false);
    }
  }

  /**
   *
   */
  static XMLElement copyModule(IBXMLAble xml, int id) {
    return(findXMLElement(xml,Integer.toString(id),XMLConstants.MODULE_STRING));
  }

}