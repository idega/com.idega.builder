/*
 * $Id: XMLWriter.java,v 1.37 2004/08/05 22:10:39 tryggvil Exp $
 *
 * Copyright (C) 2001 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package com.idega.builder.business;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import com.idega.core.component.data.ICObject;
import com.idega.core.component.data.ICObjectInstance;
import com.idega.idegaweb.IWMainApplication;
import com.idega.xml.XMLAttribute;
import com.idega.xml.XMLElement;
import com.idega.xml.XMLException;

/**
 * @author <a href="tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */
public class XMLWriter {
    
    private static Logger log = Logger.getLogger(XMLWriter.class.getName());

	private static final String EMPTY_STRING = "";

	/**
	 *
	 */
	private XMLWriter() {
	}

	/**
	 *
	 */
	private static XMLElement getPageRootElement(IBXMLAble xml) {
		return xml.getPageRootElement();
	}

	/**
	 * Find the XMLElement for the region with label label or id id.
	 * Label has precedence so regionId does not necessarily have to be the same.
	 */
	private static XMLElement findRegion(IBXMLAble xml,String label,String regionId) {
	    XMLElement region;
	    if(label!=null){
	        //first try to search by the label identifier
	        region = findXMLElement(xml, XMLConstants.LABEL_STRING, label, XMLConstants.REGION_STRING);
	        if(region!=null){
	            return region;
	        }
		}
	    else if(regionId!=null){
	        //if nothing is found with the label label then try the id
	        region = findXMLElementWithId(xml, regionId, XMLConstants.REGION_STRING);
	    }
	    else{
	        throw new RuntimeException("Can not find any region. Both label and regionId are null");
	    }
	    return region;
	}

	/**
	 *
	 */
	private static XMLElement findRegion(IBXMLAble xml, String id, XMLElement enclosingModule) {
		return findXMLElementInsideWithId(xml, id, XMLConstants.REGION_STRING, enclosingModule);
	}

	/**
	 *
	 */
	private static XMLElement findModule(IBXMLAble xml, int id) {
		return findXMLElementWithId(xml, Integer.toString(id), XMLConstants.MODULE_STRING);
	}

	/**
	 *
	 */
	private static XMLElement findModule(IBXMLAble xml, int id, XMLElement startElement) {
		return findXMLElementInsideWithId(xml, Integer.toString(id), XMLConstants.MODULE_STRING, startElement);
	}

	/**
	 * Returns null if nothing found
	 */
	private static XMLElement findXMLElementWithId(IBXMLAble xml, String id, String name) {
	    String idAttributeKey = XMLConstants.ID_STRING;
		return findXMLElement(xml, idAttributeKey, id, name);
	}

	/**
	 * Returns null if nothing found
	 */
	private static XMLElement findXMLElement(IBXMLAble xml, String attributeKey, String attributeValue, String name) {
		return findXMLElementInside(xml, attributeKey, attributeValue , name, getPageRootElement(xml));
	}
	
	
	/**
	 * Finds recursively all elements with the id attribute set to 'id'
	 * Returns null if nothing found.
	 *
	 * If name is null it searches all elements with any name
	 */
	private static XMLElement findXMLElementInsideWithId(IBXMLAble xml, String id, String name, XMLElement parentElement) {
	    String idAttributeKey = XMLConstants.ID_STRING;
	    return findXMLElementInside(xml,idAttributeKey,id,name,parentElement);
	}
	
	
	/**
	 * Recursively finds XMLElements down the tree where the attributeKey 
	 * is of value attributeKey and attributeValue is of value attributeKey.
	 * 
	 * @param attributeKey value is e.g. 'id'
	 * @param attributeValue value is e.g. '645'
	 * 
	 * @return Returns null if nothing found.
	 *
	 * If name is null it searches all elements with any name
	 */
	private static XMLElement findXMLElementInside(IBXMLAble xml, String attributeKey, String attributeValue, String name2, XMLElement parentElement) {
		List list = parentElement.getChildren();
		if (attributeValue != null) {
			try {
				int theID = Integer.parseInt(attributeValue);
				//Hardcoded -1 for the top Page element
				if (theID == -1) {
					return getPageRootElement(xml);
				}
			}
			catch (NumberFormatException e) {

			}
		}

		if (list != null) {
			Iterator iter = list.iterator();
			while (iter.hasNext()) {
				XMLElement element = (XMLElement) iter.next();
				//if(element.getName().equals(name)||nameIsNull){
				//List attributes = element.getAttributes();
				//if(attributes!=null){
				//Iterator iter2 = attributes.iterator();
				//while (iter2.hasNext()){
				XMLAttribute attr = element.getAttribute(attributeKey);
				//XMLAttribute attr = (XMLAttribute)iter2.next();
				//if(item2.getName().equals(ID_STRING)){
				if (attr != null) {
					if (attr.getValue().equals(attributeValue)) {
						return element;
					}
				}
				//}
				//}

				//}
				//}
				//else{
				XMLElement el = findXMLElementInside(xml, attributeKey, attributeValue, null, element);
				if (el != null) {
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
	private static XMLElement findProperty(IBXMLAble xml, int ObjectInstanceId, String propertyName) {
		XMLElement elem = findModule(xml, ObjectInstanceId);
		return findProperty(elem, propertyName);
	}

	/**
	 * Returns null if nothing found
	 */
	private static XMLElement findProperty(IWMainApplication iwma, int ICObjectInstanceID, XMLElement parentElement, String propertyName, String[] values) {
		List elementList = findProperties(parentElement, propertyName);
		if (elementList != null) {
			Iterator iter = elementList.iterator();
			while (iter.hasNext()) {
				XMLElement item = (XMLElement) iter.next();
				if (hasPropertyElementSpecifiedValues(iwma, ICObjectInstanceID, item, values, true))
					return item;
			}
		}
		return null;
	}

	/**
	 * Returns true if properties changed, else false
	 */
	static boolean isPropertySet(IWMainApplication iwma, IBXMLAble xml, int ICObjectInstanceId, String propertyName) {
		XMLElement module = findModule(xml, ICObjectInstanceId);
		return isPropertySet(module, propertyName);
	}

	public static boolean isPropertySet(XMLElement parentElement, String propertyName) {
		XMLElement element = findProperty(parentElement, propertyName);
		if (element != null)
			return true;
		return false;
	}

	/**
	 * Returns true if a propertyElement has the specified values, else false
	 */
	public static boolean hasPropertyElementSpecifiedValues(IWMainApplication iwma, int ICObjectInstanceID, XMLElement propertyElement, String[] values, boolean withPrimaryKeyCheck) {
		boolean check = true;
		int counter = 0;
		List valueList = propertyElement.getChildren(XMLConstants.VALUE_STRING);
		Iterator iter = valueList.iterator();
		while (check && counter < values.length) {
			try {
				String methodIdentifier = getPropertyNameForElement(propertyElement);
				boolean isPrimaryKey = IBPropertyHandler.getInstance().isMethodParameterPrimaryKey(iwma, ICObjectInstanceID, methodIdentifier, counter);
				XMLElement eValue = (XMLElement) iter.next();
				if (withPrimaryKeyCheck) {
					if (isPrimaryKey) {
						if (!eValue.getText().equals(values[counter]))
							check = false;
					}
				}
				else {
					if (!eValue.getText().equals(values[counter]))
						check = false;
				}
			}
			catch (Exception e) {
				return false;
			}
			counter++;
		}
		return check;
	}

	public static String getPropertyNameForElement(XMLElement propertyElement) {
		if (propertyElement != null) {
			return propertyElement.getChild(XMLConstants.NAME_STRING).getText();
		}
		return null;

	}

	/**
	 *Returns a List of XMLElement objects corresponding to the specified propertyName
	 *Returns null if no match
	 */
	private static List findProperties(XMLElement parentElement, String propertyName) {
		XMLElement elem = parentElement;
		List theReturn = null;
		if (elem != null) {
			List properties = elem.getChildren();
			if (properties != null) {
				Iterator iter = properties.iterator();
				while (iter.hasNext()) {
					XMLElement pElement = (XMLElement) iter.next();
					if (pElement != null) {
						if (pElement.getName().equals(XMLConstants.PROPERTY_STRING)) {
							XMLElement name = pElement.getChild(XMLConstants.NAME_STRING);
							if (name != null) {
								if (name.getText().equals(propertyName)) {
									if (theReturn == null) {
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
	private static XMLElement findProperty(XMLElement parentElement, String propertyName) {
		XMLElement elem = parentElement;
		if (elem != null) {
			List properties = elem.getChildren();
			if (properties != null) {
				Iterator iter = properties.iterator();
				while (iter.hasNext()) {
					XMLElement pElement = (XMLElement) iter.next();
					if (pElement != null) {
						if (pElement.getName().equals(XMLConstants.PROPERTY_STRING)) {
							XMLElement name = pElement.getChild(XMLConstants.NAME_STRING);
							if (name != null) {
								if (name.getText().equals(propertyName)) {
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
	public static List getPropertyValues(IBXMLAble xml, int ObjectInstanceId, String propertyName) {
		XMLElement module = findModule(xml, ObjectInstanceId);
		List theReturn = com.idega.util.ListUtil.getEmptyList();
		List propertyList = findProperties(module, propertyName);
		if (propertyList != null) {
			theReturn = new Vector();
			Iterator iter = propertyList.iterator();
			while (iter.hasNext()) {
				XMLElement property = (XMLElement) iter.next();
				if (property != null) {
					List list = property.getChildren(XMLConstants.VALUE_STRING);
					String[] array = new String[list.size()];
					Iterator iter2 = list.iterator();
					int counter = 0;
					while (iter2.hasNext()) {
						XMLElement el = (XMLElement) iter2.next();
						String theString = el.getText();
						array[counter] = theString;
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
	public static String getProperty(IBXMLAble xml, int ObjectInstanceId, String propertyName) {
		XMLElement module = findModule(xml, ObjectInstanceId);
		XMLElement property = findProperty(module, propertyName);
		if (property != null) {
			XMLElement value = property.getChild(XMLConstants.VALUE_STRING);
			return value.getText();
		}
		return null;
	}

	/**
	 *
	 */
	public static boolean removeProperty(IWMainApplication iwma, IBXMLAble xml, int ICObjectInstanceId, String propertyName, String[] values) {
		XMLElement module = findModule(xml, ICObjectInstanceId);
		if (module != null) {
			XMLElement property = findProperty(iwma, ICObjectInstanceId, module, propertyName, values);
			if (property != null) {
				return module.removeContent(property);
			}
			else {
				return false;
			}
		}
		else {
			return false;
		}
	}

	/**
	 *
	 */
	public static boolean setProperty(IWMainApplication iwma, IBXMLAble xml, int ObjectInstanceId, String propertyName, String propertyValue) {
		String[] values = { propertyValue };
		return setProperty(iwma, xml, ObjectInstanceId, propertyName, values, false);
	}

	/**
	 * Checks if the propertyValue array is correctly formcatted
	 * (Not with empty strings or null values)
	 */
	private static boolean isPropertyValueArrayValid(String[] propertyValues) {
		for (int i = 0; i < propertyValues.length; i++) {
			String s = propertyValues[i];
			if (s == null) {
				return false;
			}
			else {
				if (s.equals(EMPTY_STRING))
					return false;
			}
		}
		return true;
	}

	/**
	 * Returns true if properties changed, else false
	 */
	static boolean setProperty(IWMainApplication iwma, IBXMLAble xml, int ICObjectInstanceId, String propertyName, String[] propertyValues, boolean allowMultiValued) {

		//Checks if the propertyValues array is correctly formatted
		if (!isPropertyValueArrayValid(propertyValues))
			return false;

		boolean changed = false;
		XMLElement module = findModule(xml, ICObjectInstanceId);
		XMLElement property = null;
		if (allowMultiValued) {
			property = findProperty(iwma, ICObjectInstanceId, module, propertyName, propertyValues);
		}
		else {
			property = findProperty(module, propertyName);
		}

		if (property == null) {
			property = getNewProperty(propertyName, propertyValues);
			module.addContent(property);
			changed = true;
		}
		else {
			List values = property.getChildren(XMLConstants.VALUE_STRING);
			if (values != null) {
				Iterator iter = values.iterator();
				int index = 0;
				while (iter.hasNext()) {

					String propertyValue = propertyValues[index];
					XMLElement value = (XMLElement) iter.next();
					String currentValue = value.getText();
					if (!currentValue.equals(propertyValue)) {
						value.setText(propertyValue);
						changed = true;
					}
					index++;
				}
			}
			else {
				for (int index = 0; index < propertyValues.length; index++) {
					String propertyValue = propertyValues[index];
					XMLElement value = new XMLElement(XMLConstants.VALUE_STRING);
					value.addContent(propertyValue);
					property.addContent(value);
					changed = true;
				}

			}
		}
		return changed;
	}

	/**
	 *
	 */
	private static XMLElement getNewProperty(String propertyName, Object[] propertyValues) {

		XMLElement element = new XMLElement(XMLConstants.PROPERTY_STRING);
		XMLElement name = new XMLElement(XMLConstants.NAME_STRING);
		for (int i = 0; i < propertyValues.length; i++) {
			XMLElement value = new XMLElement(XMLConstants.VALUE_STRING);
			XMLElement type = new XMLElement(XMLConstants.TYPE_STRING);
			Object propertyValue = propertyValues[i];
			if (i == 0) {
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
	private static boolean addNewModule(XMLElement parent, String pageKey, int newICObjectTypeID) {
		//XMLElement parent = findModule(parentObjectInstanceID);
		if (parent != null) {
			try {
				ICObjectInstance instance = ((com.idega.core.component.data.ICObjectInstanceHome) com.idega.data.IDOLookup.getHomeLegacy(ICObjectInstance.class)).createLegacy();
				instance.setICObjectID(newICObjectTypeID);
				instance.setIBPageByKey(pageKey);
				instance.store();

				ICObject obj = ((com.idega.core.component.data.ICObjectHome) com.idega.data.IDOLookup.getHome(ICObject.class)).findByPrimaryKey(newICObjectTypeID);
				Class theClass = obj.getObjectClass();

				XMLElement newElement = new XMLElement(XMLConstants.MODULE_STRING);
				XMLAttribute aId = new XMLAttribute(XMLConstants.ID_STRING, instance.getPrimaryKey().toString());
				XMLAttribute aIcObjectId = new XMLAttribute(XMLConstants.IC_OBJECT_ID_STRING, Integer.toString(newICObjectTypeID));
				XMLAttribute aClass = new XMLAttribute(XMLConstants.CLASS_STRING, theClass.getName());

				//        newElement.addAttribute(aId);
				//        newElement.addAttribute(aIcObjectId);
				//        newElement.addAttribute(aClass);
				newElement.setAttribute(aId);
				newElement.setAttribute(aIcObjectId);
				newElement.setAttribute(aClass);

				parent.addContent(newElement);

			}
			catch (Exception e) {
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
	public static boolean addLabel(IBXMLAble xml, int parentObjectInstanceId, int xpos, int ypos, String label) {
		return (true);
	}

	/**
	 *
	 */
	public static boolean addNewModule(IBXMLAble xml, String pageKey, int parentObjectInstanceID, int newICObjectID, int xpos, int ypos, String label) {
		String regionId = parentObjectInstanceID + "." + xpos + "." + ypos;
		return addNewModule(xml,pageKey,parentObjectInstanceID,newICObjectID,regionId,label);
	}
	
	public static boolean addNewModule(IBXMLAble xml, String pageKey, int parentObjectInstanceID, int newICObjectID, String regionId, String label) {
		
		XMLElement region = findRegion(xml, label,regionId);
	    
		if (region == null) {
			region = createRegion(regionId,label);
			addNewModule(region, pageKey, newICObjectID);
			XMLElement parent = findModule(xml, parentObjectInstanceID);		
			if (parent != null){
				//This is in a page that is NOT extending a template (is a template itself)
				parent.addContent(region);
			}
			else { 
				//This is in a page that is extending a template
				xml.getPageRootElement().addContent(region);
			}
		}
		else {
			addNewModule(region, pageKey, newICObjectID);
		}
		return true;

	}
	
	protected static XMLElement createRegion(String regionId,String label){
		XMLElement region = new XMLElement(XMLConstants.REGION_STRING);
		XMLAttribute id = new XMLAttribute(XMLConstants.ID_STRING, regionId);
		region.setAttribute(id);
		if (label != null) {
			XMLAttribute labelAttribute = new XMLAttribute(XMLConstants.LABEL_STRING, label);
			region.setAttribute(labelAttribute);
		}
		return region;
	}

	/**
	 *
	 */
	public static boolean addNewModule(IBXMLAble xml, String pageKey, int parentObjectInstanceID, int newICObjectID, String label) {
		return addNewModule(findModule(xml, parentObjectInstanceID), pageKey, newICObjectID);
	}

	/**
	 *
	 */
	public static boolean addNewModule(IBXMLAble xml, String pageKey, String parentObjectInstanceID, int newICObjectID, String label) {
		if(label==null){
			try {
				return addNewModule(findModule(xml, Integer.parseInt(parentObjectInstanceID)), pageKey, newICObjectID);
			}
			catch (NumberFormatException nfe) {
	
				int parentID = Integer.parseInt(parentObjectInstanceID.substring(0, parentObjectInstanceID.indexOf(".")));
				String theRest = parentObjectInstanceID.substring(parentObjectInstanceID.indexOf(".") + 1, parentObjectInstanceID.length());
	
				int xpos = Integer.parseInt(theRest.substring(0, theRest.indexOf(".")));
				int ypos = Integer.parseInt(theRest.substring(theRest.indexOf(".") + 1, theRest.length()));
	
				return addNewModule(xml, pageKey, parentID, newICObjectID, xpos, ypos, label);
			}
		}
		else{
			int parentID=-1;
			try{
				parentID = Integer.parseInt(parentObjectInstanceID);
			}
			catch(NumberFormatException nfe){}
			return addNewModule(xml, pageKey, parentID, newICObjectID, parentObjectInstanceID, label);
		}
	}

	/**
	 *
	 */
	public static boolean addNewModule(IBXMLAble xml, String pageKey, String parentObjectInstanceID, ICObject newObjectType, String label) {
		int icObjectId = ((Number)newObjectType.getPrimaryKey()).intValue();
		return addNewModule(xml, pageKey, parentObjectInstanceID, icObjectId, label);
	}

	/**
	 * Checks if the given element is empty, i.e. if it contains no child elements.
	 * @param element
	 * @return
	 */
	public static boolean isElementEmpty(XMLElement element){
	    List children = element.getChildren();
	    for (Iterator iter = children.iterator(); iter.hasNext();) {
            XMLElement child = (XMLElement) iter.next();
            if(child!=null){
                return false;
            }
        }
	    
	    return true;
	}
	
	/**
	 *Deletes the module
	 */
	public static boolean deleteModule(IBXMLAble xml, String parentObjectInstanceID, int ICObjectInstanceID) {
		XMLElement parent = findXMLElementWithId(xml, parentObjectInstanceID, null);
		if (parent != null) {
			try {
				XMLElement module = findModule(xml, ICObjectInstanceID, parent);
				if(module==null){
				    //This is to handle the case when a duplicate empty region (with the same id)
				    //prevents the find operation above to find the correct module.
				    //This only seems to happen in table regions with e.g. parentObjectInstanceID=1.5.3
				    log.info("Found likely corrupt duplicate region with id:"+parentObjectInstanceID);
				    //Check if the module is empty for safetys sake
				    if(isElementEmpty(parent)){
					    //First Delete the corrupt region
					    deleteModule(parent.getParent(), parent);
					    log.info("Deleted corrupt region with id:"+parentObjectInstanceID);
					    //Find the parent (region) again:
					    parent = findXMLElementWithId(xml, parentObjectInstanceID, null);
					    //Find the module again:
					    module = findModule(xml, ICObjectInstanceID, parent);
				    }
				}
				return deleteModule(parent, module);
			}
			catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
		return false;
	}

	/**
	 *
	 */
	public static boolean lockRegion(IBXMLAble xml, String parentObjectInstanceID) {
		XMLElement parent = findXMLElementWithId(xml, parentObjectInstanceID, null);
		if (parent != null) {
			XMLAttribute lock = new XMLAttribute(XMLConstants.REGION_LOCKED, "true");
			//      if (parent.getAttribute(XMLConstants.REGION_LOCKED) != null)
			//        parent.removeAttribute(XMLConstants.REGION_LOCKED);
			//      parent.addAttribute(lock);
			parent.setAttribute(lock);
			return (true);
		}
		else {
			int index = parentObjectInstanceID.indexOf(".");
			if (index != -1) {
				XMLElement region = new XMLElement(XMLConstants.REGION_STRING);
				XMLAttribute id = new XMLAttribute(XMLConstants.ID_STRING, parentObjectInstanceID);
				//        region.addAttribute(id);
				region.setAttribute(id);

				int parentID = Integer.parseInt(parentObjectInstanceID.substring(0, index));
				XMLElement regionParent = findModule(xml, parentID);
				if (regionParent != null)
					regionParent.addContent(region);

				XMLAttribute lock = new XMLAttribute(XMLConstants.REGION_LOCKED, "true");
				//        region.addAttribute(lock);
				region.setAttribute(lock);

				return (true);
			}
		}

		return (false);
	}

	/**
	 *
	 */
	public static boolean setAttribute(IBXMLAble xml, String parentObjectInstanceID, String attributeName, String attributeValue) {
		XMLElement parent = findXMLElementWithId(xml, parentObjectInstanceID, null);
		if (parent != null) {
			XMLAttribute attribute = new XMLAttribute(attributeName, attributeValue);
			//      if (parent.getAttribute(attributeName) != null)
			//        parent.removeAttribute(attributeName);
			//      parent.addAttribute(attribute);
			parent.setAttribute(attribute);
			return (true);
		}

		return (false);
	}

	/**
	 *
	 */
	public static boolean unlockRegion(IBXMLAble xml, String parentObjectInstanceID) {
		XMLElement parent = findXMLElementWithId(xml, parentObjectInstanceID, null);
		if (parent != null) {
			XMLAttribute lock = new XMLAttribute(XMLConstants.REGION_LOCKED, "false");
			//      if (parent.getAttribute(XMLConstants.REGION_LOCKED) != null)
			//        parent.removeAttribute(XMLConstants.REGION_LOCKED);
			//      parent.addAttribute(lock);
			parent.setAttribute(lock);
			return (true);
		}
		else {
			int index = parentObjectInstanceID.indexOf(".");
			if (index != -1) {
				XMLElement region = new XMLElement(XMLConstants.REGION_STRING);
				XMLAttribute id = new XMLAttribute(XMLConstants.ID_STRING, parentObjectInstanceID);
				//        region.addAttribute(id);
				region.setAttribute(id);

				int parentID = Integer.parseInt(parentObjectInstanceID.substring(0, index));
				XMLElement regionParent = findModule(xml, parentID);
				if (regionParent != null)
					regionParent.addContent(region);

				XMLAttribute lock = new XMLAttribute(XMLConstants.REGION_LOCKED, "false");
				//        region.addAttribute(lock);
				region.setAttribute(lock);

				return (true);
			}
		}

		return (false);
	}

	/**
	 *
	 */
	private static boolean deleteModule(XMLElement parent, XMLElement child) throws Exception {
		List children = getChildElements(child);
		if (children != null) {
			Iterator iter = children.iterator();
			while (iter.hasNext()) {
				XMLElement childchild = (XMLElement) iter.next();
				deleteModule(child, childchild);
			}
			XMLAttribute attribute = child.getAttribute(XMLConstants.ID_STRING);
			if (attribute != null) {
				String ICObjectInstanceID = attribute.getValue();
				try {
					ICObjectInstance instance = ((com.idega.core.component.data.ICObjectInstanceHome) com.idega.data.IDOLookup.getHome(ICObjectInstance.class)).findByPrimaryKey(Integer.parseInt(ICObjectInstanceID));
					instance.remove();
				}
				catch (NumberFormatException e) {
				}
			}
		}
		parent.removeContent(child);
		return true;
	}

	/**
	 *
	 */
	private static List getChildElements(XMLElement parent) {
		return parent.getChildren();
	}

	/**
	 *
	 */
	private static List getChildModules(XMLElement parent) {
		List children = parent.getChildren();
		Iterator iter = children.iterator();
		while (iter.hasNext()) {
			XMLElement item = (XMLElement) iter.next();
			if (item.getName().equals(XMLConstants.REGION_STRING)) {
				children.addAll(getChildModules((XMLElement) item));
			}
			else if (!item.getName().equals(XMLConstants.MODULE_STRING)) {
				iter.remove();
			}
		}
		return children;
	}

	/**
	 *
	 */
	public static boolean labelRegion(IBXMLAble xml, String parentObjectInstanceID, String label) {
		XMLElement parent = findXMLElementWithId(xml, parentObjectInstanceID, null);
		if (parent != null) {
			if (label != null && !label.equals("")) {
				XMLAttribute labelAttribute = new XMLAttribute(XMLConstants.LABEL_STRING, label);
				//        if (parent.getAttribute(XMLConstants.LABEL_STRING) != null)
				//          parent.removeAttribute(XMLConstants.LABEL_STRING);
				//        parent.addAttribute(labelAttribute);
				parent.setAttribute(labelAttribute);
			}
			else {
				if (parent.getAttribute(XMLConstants.LABEL_STRING) != null)
					parent.removeAttribute(XMLConstants.LABEL_STRING);
			}

			return (true);
		}
		else {
			int index = parentObjectInstanceID.indexOf(".");
			if (index != -1) {
				if (label != null && !label.equals("")) {
					XMLElement region = new XMLElement(XMLConstants.REGION_STRING);
					XMLAttribute id = new XMLAttribute(XMLConstants.ID_STRING, parentObjectInstanceID);
					//          region.addAttribute(id);
					region.setAttribute(id);

					int parentID = Integer.parseInt(parentObjectInstanceID.substring(0, index));
					XMLElement regionParent = findModule(xml, parentID);
					if (regionParent != null)
						regionParent.addContent(region);

					XMLAttribute labelAttribute = new XMLAttribute(XMLConstants.LABEL_STRING, label);
					//          region.addAttribute(labelAttribute);
					region.setAttribute(labelAttribute);

					return (true);
				}
			}
		}

		return (false);
	}

	/**
	 *
	 */
	public static boolean copyModule(IBXMLAble xml, String parentObjectInstanceID, int ICObjectInstanceID) {
		XMLElement parent = findXMLElementWithId(xml, parentObjectInstanceID, null);
		if (parent != null) {
			try {
				XMLElement module = findModule(xml, ICObjectInstanceID, parent);
				return (copyModule(parent, module));
			}
			catch (Exception e) {
				e.printStackTrace();
				return (false);
			}
		}

		return (false);
	}

	/**
	 *
	 */
	private static boolean copyModule(XMLElement parent, XMLElement child) throws Exception {
		List children = getChildElements(child);
		if (children != null) {
			Iterator iter = children.iterator();
			while (iter.hasNext()) {
				XMLElement childchild = (XMLElement) iter.next();
				copyModule(child, childchild);
			}
			XMLAttribute attribute = child.getAttribute(XMLConstants.ID_STRING);
			if (attribute != null) {
				String ICObjectInstanceID = attribute.getValue();
				try {
					ICObjectInstance instance = ((com.idega.core.component.data.ICObjectInstanceHome) com.idega.data.IDOLookup.getHomeLegacy(ICObjectInstance.class)).findByPrimaryKeyLegacy(Integer.parseInt(ICObjectInstanceID));
					instance.remove();
				}
				catch (NumberFormatException e) {
				}
			}
		}

		return (true);
	}

	/**
	 *
	 */
	public static boolean addNewElement(IBXMLAble xml, int parentObjectInstanceID, XMLElement element) {
		XMLElement parent = findModule(xml, parentObjectInstanceID);
		if (parent != null)
			parent.addContent(element);

		return true;
	}

	/**
	 *
	 */
	public static boolean pasteElement(IBXMLAble xml, String pageKey, String parentObjectInstanceID, XMLElement element) {
		changeModuleIds(element, pageKey);
		XMLElement parent = findXMLElementWithId(xml, parentObjectInstanceID, null);
		if (parent != null) {
			parent.addContent(element);

			return (true);
		}
		else {
			int index = parentObjectInstanceID.indexOf(".");
			if (index != -1) {
				XMLElement region = new XMLElement(XMLConstants.REGION_STRING);
				XMLAttribute id = new XMLAttribute(XMLConstants.ID_STRING, parentObjectInstanceID);
				region.setAttribute(id);

				int parentID = Integer.parseInt(parentObjectInstanceID.substring(0, index));
				XMLElement regionParent = findModule(xml, parentID);
				if (regionParent != null)
					regionParent.addContent(region);
				else
					xml.getPageRootElement().addContent(region);

				region.addContent(element);

				return (true);
			}
		}

		return (false);
	}

	/**
	 *
	 */
	public static boolean pasteElementAbove(IBXMLAble xml, String pageKey, String parentObjectInstanceID, String objectId, XMLElement element) {
		changeModuleIds(element, pageKey);
		XMLElement parent = findXMLElementWithId(xml, parentObjectInstanceID, null);
		if (parent != null) {
			//      parent.addContent(element);
			List li = parent.getChildren();
			int index = -1;
			if (li != null) {
				Iterator it = li.iterator();
				while (it.hasNext()) {
					XMLElement el = (XMLElement) it.next();
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
						XMLElement el = (XMLElement) it.next();
						parent.addContent(el);
					}
				}
			}
			else
				parent.addContent(element); //hmmmm

			return (true);
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

		return (false);
	}

	/**
	 *
	 */
	private static boolean changeModuleIds(XMLElement element, String pageKey) {
		try {
			XMLAttribute attribute = element.getAttribute(XMLConstants.ID_STRING);
			XMLAttribute object_id = element.getAttribute(XMLConstants.IC_OBJECT_ID_STRING);

			ICObjectInstance instance = ((com.idega.core.component.data.ICObjectInstanceHome) com.idega.data.IDOLookup.getHome(ICObjectInstance.class)).create();
			instance.setICObjectID(object_id.getIntValue());
			instance.setIBPageByKey(pageKey);
			instance.store();

			String moduleId = instance.getPrimaryKey().toString();
			attribute = new XMLAttribute(XMLConstants.ID_STRING, moduleId);
			element.setAttribute(attribute);

			List childs = element.getChildren(XMLConstants.MODULE_STRING);
			if (childs != null) {
				Iterator it = childs.iterator();
				while (it.hasNext()) {
					XMLElement child = (XMLElement) it.next();
					if (!changeModuleIds(child, pageKey))
						return (false);
				}
			}

			childs = element.getChildren(XMLConstants.REGION_STRING);
			if (childs != null) {
				Iterator it = childs.iterator();
				while (it.hasNext()) {
					XMLElement el = (XMLElement) it.next();
					XMLAttribute regionId = el.getAttribute(XMLConstants.ID_STRING);
					if (regionId != null) {
						String regId = regionId.getValue();
						int index = regId.indexOf(".");
						if (index > -1) {
							String sub = regId.substring(index);
							sub = moduleId + sub;
							regionId = new XMLAttribute(XMLConstants.ID_STRING, sub);
							el.setAttribute(regionId);
						}
					}
					List childs2 = el.getChildren(XMLConstants.MODULE_STRING);
					if (childs2 != null) {
						Iterator it2 = childs2.iterator();
						while (it2.hasNext()) {
							XMLElement child = (XMLElement) it2.next();
							if (!changeModuleIds(child, pageKey))
								return (false);
						}
					}
				}
			}

			return (true);
		}
		catch (XMLException e) {
			return (false);
		}
		catch (Exception e) {
			e.printStackTrace();
			return (false);
		}		
	}

	/**
	 *
	 */
	public static XMLElement copyModule(IBXMLAble xml, int id) {
		return (findXMLElementWithId(xml, Integer.toString(id), XMLConstants.MODULE_STRING));
	}

}
