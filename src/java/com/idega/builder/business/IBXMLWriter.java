/*
 * $Id: IBXMLWriter.java,v 1.17 2008/02/04 11:30:36 valdas Exp $
 * 
 * Copyright (C) 2001 Idega hf. All Rights Reserved.
 * 
 * This software is the proprietary information of Idega hf. Use is subject to
 * license terms.
 *  
 */
package com.idega.builder.business;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.rmi.RemoteException;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import com.idega.business.IBOLookup;
import com.idega.business.IBOLookupException;
import com.idega.core.component.data.ICObject;
import com.idega.core.component.data.ICObjectHome;
import com.idega.core.component.data.ICObjectInstance;
import com.idega.core.component.data.ICObjectInstanceHome;
import com.idega.core.idgenerator.business.UUIDGenerator;
import com.idega.data.IDOLookup;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWMainApplication;
import com.idega.slide.business.IWSlideService;
import com.idega.util.CoreConstants;
import com.idega.util.bundles.BundleResourceResolver;
import com.idega.util.reflect.MethodFinder;
import com.idega.xml.XMLAttribute;
import com.idega.xml.XMLElement;
import com.idega.xml.XMLException;

/**
 * <p>
 * This is the main class for writing or manipulating the 'IBXML' document format in the Builder.
 * <p>
 * @author <a href="tryggvi@idega.is">Tryggvi Larusson </a>
 * @version 1.0
 */
public class IBXMLWriter {

	private Logger log = Logger.getLogger(IBXMLWriter.class.getName());
	
	/**
	 * <p>
	 * Constructor only used by BuilderLogic
	 * </p>
	 */
	IBXMLWriter() {
	}

	/**
	 *  
	 */
	private XMLElement getPageRootElement(IBXMLAble xml) {
		return xml.getPageRootElement();
	}

	/**
	 * Find the XMLElement for the region with label label or id id. Label has
	 * precedence so regionId does not necessarily have to be the same.
	 */
	public XMLElement findRegion(IBXMLAble xml, String label, String regionId) {
		XMLElement region;
		if (label != null) {
			//first try to search by the label identifier
			region = findXMLElement(xml, IBXMLConstants.LABEL_STRING, label);
			if (region != null) {
				return region;
			}
		}
		else if (regionId != null) {
			//if nothing is found with the label label then try the id
			region = findXMLElementWithId(xml, regionId);
		}
		else {
			throw new RuntimeException("Can not find any region. Both label and regionId are null");
		}
		return region;
	}

	/**
	 *  
	 */
	public XMLElement findModule(IBXMLAble xml, String instanceId) {
		return findXMLElementWithId(xml, instanceId);
	}

	/**
	 *  
	 */
	private XMLElement findModule(IBXMLAble xml, String instanceId, XMLElement startElement) {
		return findXMLElementInsideWithId(xml, instanceId, startElement);
	}

	/**
	 * Returns null if nothing found
	 */
	private XMLElement findXMLElementWithId(IBXMLAble xml, String id) {
		String idAttributeKey = IBXMLConstants.ID_STRING;
		return findXMLElement(xml, idAttributeKey, id);
	}

	/**
	 * Returns null if nothing found
	 */
	private XMLElement findXMLElement(IBXMLAble xml, String attributeKey, String attributeValue) {
		return findXMLElementInside(xml, attributeKey, attributeValue, getPageRootElement(xml));
	}

	/**
	 * Finds recursively all elements with the id attribute set to 'id' Returns
	 * null if nothing found.
	 * 
	 * If name is null it searches all elements with any name
	 */
	private XMLElement findXMLElementInsideWithId(IBXMLAble xml, String id, XMLElement parentElement) {
		String idAttributeKey = IBXMLConstants.ID_STRING;
		return findXMLElementInside(xml, idAttributeKey, id, parentElement);
	}

	/**
	 * Recursively finds XMLElements down the tree where the attributeKey is of
	 * value attributeKey and attributeValue is of value attributeKey.
	 * 
	 * @param attributeKey
	 *            value is e.g. 'id'
	 * @param attributeValue
	 *            value is e.g. '645'
	 * 
	 * @return Returns null if nothing found.
	 * 
	 * If name is null it searches all elements with any name
	 */
	private XMLElement findXMLElementInside(IBXMLAble xml, String attributeKey, String attributeValue, XMLElement parentElement) {
		List list = null;
		if (parentElement != null) {
			list = parentElement.getChildren();
		}
		
		//Hardcoded -1 for the top Page element
		if ("-1".equals(attributeValue)) {
			return getPageRootElement(xml);
		}
		
		if (list != null) {
			Iterator iter = list.iterator();
			while (iter.hasNext()) {
				XMLElement element = (XMLElement) iter.next();
				XMLAttribute attr = element.getAttribute(attributeKey);
				if (attr != null) {
					if (attr.getValue().equals(attributeValue)) {
						return element;
					}
				}
				XMLElement el = findXMLElementInside(xml, attributeKey, attributeValue, element);
				if (el != null) {
					return el;
				}
			}
		}
		return null;
	}

	/**
	 * Returns null if nothing found
	 */
	private XMLElement findProperty(IWMainApplication iwma, String instanceId, XMLElement parentElement,
			String propertyName, String[] values) {
		List elementList = findProperties(parentElement, propertyName);
		if (elementList != null) {
			Iterator iter = elementList.iterator();
			while (iter.hasNext()) {
				XMLElement item = (XMLElement) iter.next();
				if (hasPropertyElementSpecifiedValues(iwma, instanceId, item, propertyName,values, true)) {
					return item;
				}
			}
		}
		return null;
	}

	/**
	 * Returns true if properties changed, else false
	 */
	boolean isPropertySet(IWMainApplication iwma, IBXMLAble xml, String instanceId, String propertyName) {
		XMLElement module = findModule(xml, instanceId);
		return isPropertySet(module, propertyName);
	}

	public boolean isPropertySet(XMLElement parentElement, String propertyName) {
		XMLElement element = findProperty(parentElement, propertyName);
		if (element != null) {
			return true;
		}
		return false;
	}

	/**
	 * Returns true if a propertyElement has the specified values, else false
	 */
	public boolean hasPropertyElementSpecifiedValues(IWMainApplication iwma, String instanceId,
			XMLElement propertyElement, String propertyName, String[] values, boolean withPrimaryKeyCheck) {
		boolean check = true;
		boolean isMethodIdentifier = MethodFinder.getInstance().isMethodIdentifier(propertyName);
		if(isMethodIdentifier){
			int counter = 0;
			List valueList = propertyElement.getChildren(IBXMLConstants.VALUE_STRING);
			Iterator iter = valueList.iterator();
			while (check && counter < values.length) {
				try {
					String methodIdentifier = getPropertyNameForElement(propertyElement);
					boolean isPrimaryKey = IBPropertyHandler.getInstance().isMethodParameterPrimaryKey(iwma,instanceId, methodIdentifier, counter);
					XMLElement eValue = (XMLElement) iter.next();
					if (withPrimaryKeyCheck) {
						if (isPrimaryKey) {
							if (!eValue.getText().equals(values[counter])) {
								check = false;
							}
						}
					}
					else {
						if (!eValue.getText().equals(values[counter])) {
							check = false;
						}
					}
				}
				catch (Exception e) {
					return false;
				}
				counter++;
			}
		}
		else{
			//Only handle a single property Value for now:
			String propValue = propertyElement.getAttributeValue(IBXMLConstants.VALUE_STRING);
			if(values.length==1){
				String value = values[0];
				if(value!=null){
					if(value.equals(propValue)){
						check=true;
						return check;
					}
				}
			}
		}
		return check;
	}

	public String getPropertyNameForElement(XMLElement propertyElement) {
		if (propertyElement != null) {
			return propertyElement.getChild(IBXMLConstants.NAME_STRING).getText();
		}
		return null;
	}

	/**
	 * Returns a List of XMLElement objects corresponding to the specified
	 * propertyName Returns null if no match
	 */
	private List findProperties(XMLElement parentElement, String propertyName) {
		XMLElement elem = parentElement;
		List theReturn = null;
		if (elem != null) {
			List properties = elem.getChildren();
			if (properties != null) {
				Iterator iter = properties.iterator();
				while (iter.hasNext()) {
					XMLElement pElement = (XMLElement) iter.next();
					if (pElement != null) {
						if (pElement.getName().equals(IBXMLConstants.PROPERTY_STRING)) {
							//boolean isMethodIdentifier = MethodFinder.getInstance().isMethodIdentifier(propertyName);
							//if(isMethodIdentifier){
								XMLElement name = pElement.getChild(IBXMLConstants.NAME_STRING);
								if (name != null) {
									if (name.getText().equals(propertyName)) {
										if (theReturn == null) {
											theReturn = new Vector();
										}
										theReturn.add(pElement);
									}
								}
								else if(name==null){
									String nameAttr = pElement.getAttributeValue(IBXMLConstants.NAME_STRING);
									if(nameAttr!=null){
										if(nameAttr.equals(propertyName)){
											if (theReturn == null) {
												theReturn = new Vector();
											}
											theReturn.add(pElement);
										}
									}
								}
							//}
							//else{
							//	XMLAttribute value = pElement.getAttribute(XMLConstants.VALUE_STRING);
							//	theReturn = new Vector();
							//	theReturn.add(value);
							//}
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
	protected XMLElement findProperty(XMLElement parentElement, String propertyName) {
		XMLElement elem = parentElement;
		if (elem != null) {
			List properties = elem.getChildren();
			if (properties != null) {
				Iterator iter = properties.iterator();
				while (iter.hasNext()) {
					XMLElement pElement = (XMLElement) iter.next();
					if (pElement != null) {
						if (pElement.getName().equals(IBXMLConstants.PROPERTY_STRING)) {
							boolean isMethodIdentifier = MethodFinder.getInstance().isMethodIdentifier(propertyName);
							if(isMethodIdentifier){
								//Older way of handling methodIdentifier Properties:
								XMLElement name = pElement.getChild(IBXMLConstants.NAME_STRING);
								if (name != null) {
									if (name.getText().equals(propertyName)) {
										return pElement;
									}
								}
							}
							else{
								String name = pElement.getAttributeValue("name");
								if(name!=null){
									if(name.equals(propertyName)){
										return pElement;
									}
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
	public List getPropertyValues(IBXMLAble xml, String instanceId, String propertyName) {
		XMLElement module = findModule(xml, instanceId);
		List theReturn = com.idega.util.ListUtil.getEmptyList();

		List propertyList = findProperties(module, propertyName);
		if (propertyList != null) {
			boolean isMethodIdentifier = MethodFinder.getInstance().isMethodIdentifier(propertyName);
			if(isMethodIdentifier){
				theReturn = new Vector();
				Iterator iter = propertyList.iterator();
				while (iter.hasNext()) {
					XMLElement property = (XMLElement) iter.next();
					if (property != null) {
						List list = property.getChildren(IBXMLConstants.VALUE_STRING);
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
			else{
				Iterator iter = propertyList.iterator();
				theReturn = new Vector();
				while (iter.hasNext()) {
					XMLElement property = (XMLElement) iter.next();
					String propValue = property.getAttributeValue(IBXMLConstants.VALUE_STRING);
					String[] array = new String[]{propValue};
					theReturn.add(array);
				}
			}
		}

		return theReturn;
	}

	/**
	 * Returns the first property if there is an array of properties set
	 */
	public String getProperty(IBXMLAble xml, String instanceId, String propertyName) {
		XMLElement module = findModule(xml, instanceId);
		XMLElement property = findProperty(module, propertyName);
		if (property != null) {
			XMLElement value = property.getChild(IBXMLConstants.VALUE_STRING);
			if (value != null) {
				return value.getText();
			}
			else {
				XMLAttribute attrValue = property.getAttribute(IBXMLConstants.VALUE_STRING);
				if (attrValue != null) {
					return attrValue.getValue();
				}
			}
		}
		return null;
	}

	/**
	 *  
	 */
	public boolean removeProperty(IWMainApplication iwma, IBXMLAble xml, String instanceId,
			String propertyName, String[] values) {
		XMLElement module = findModule(xml, instanceId);
		if (module != null) {
			XMLElement property = findProperty(iwma, instanceId, module, propertyName, values);
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
	public boolean setProperty(IWMainApplication iwma, IBXMLAble xml, String instanceId, String propertyName,
			String propertyValue) {
		String[] values = { propertyValue };
		return setProperty(iwma, xml, instanceId, propertyName, values, false);
	}

	/**
	 * Checks if the propertyValue array is correctly formatted (Not with empty
	 * strings or null values)
	 */
	private boolean isPropertyValueArrayValid(String[] propertyValues) {
		for (int i = 0; i < propertyValues.length; i++) {
			String s = propertyValues[i];
			if (s == null) {
				return false;
			}
			else {
				if (s.equals(IBXMLConstants.EMPTY_STRING)) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Returns true if properties changed, else false
	 */
	boolean setProperty(IWMainApplication iwma, IBXMLAble xml, String instanceId, String propertyName,
			String[] propertyValues, boolean allowMultiValued) {
		//Checks if the propertyValues array is correctly formatted
		if (!isPropertyValueArrayValid(propertyValues)) {
			return false;
		}
		boolean isMethodIdentifier = MethodFinder.getInstance().isMethodIdentifier(propertyName);
		boolean changed = false;
		XMLElement module = findModule(xml, instanceId);
		XMLElement property = null;
		if (allowMultiValued) {
			property = findProperty(iwma, instanceId, module, propertyName, propertyValues);
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
			if(isMethodIdentifier){
				List values = property.getChildren(IBXMLConstants.VALUE_STRING);
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
						XMLElement value = new XMLElement(IBXMLConstants.VALUE_STRING);
						value.addContent(propertyValue);
						property.addContent(value);
						changed = true;
					}
				}
			}
			else{
				//only support one value for now:
				String value = propertyValues[0];
				property.setAttribute(IBXMLConstants.VALUE_STRING, value);
				changed = true;
			}
		}
		return changed;
	}

	/**
	 *  
	 */
	private XMLElement getNewProperty(String propertyName, Object[] propertyValues) {
		
		
		boolean isMethodIdentifier = MethodFinder.getInstance().isMethodIdentifier(propertyName);
		
		XMLElement element = new XMLElement(IBXMLConstants.PROPERTY_STRING);
		
		if(isMethodIdentifier){
			XMLElement name = new XMLElement(IBXMLConstants.NAME_STRING);
			for (int i = 0; i < propertyValues.length; i++) {
				XMLElement value = new XMLElement(IBXMLConstants.VALUE_STRING);
				XMLElement type = new XMLElement(IBXMLConstants.TYPE_STRING);
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
		}
		else{
			element.setAttribute(new XMLAttribute(IBXMLConstants.NAME_STRING,propertyName));
			String strValue = null ;
			for (int i = 0; i < propertyValues.length; i++) {
				if(strValue==null){
					strValue=propertyValues[i].toString();
				}
				else{
					strValue=","+propertyValues[i].toString();
				}
			}
			
			element.setAttribute(new XMLAttribute(IBXMLConstants.VALUE_STRING,strValue));
		}
		return element;
	}

	/**
	 *  
	 */
	/*private boolean addNewModule(XMLElement parent, String pageKey, int newICObjectTypeID) {
		//XMLElement parent = findModule(parentObjectInstanceID);
		if (parent != null) {
			try {
				ICObjectInstanceHome icoiHome = (ICObjectInstanceHome) IDOLookup.getHome(ICObjectInstance.class);
				ICObjectHome icoHome = (ICObjectHome)IDOLookup.getHome(ICObject.class);
				
				ICObjectInstance instance = icoiHome.create();
				instance.setICObjectID(newICObjectTypeID);
				instance.setIBPageByKey(pageKey);
				instance.store();
				ICObject obj = icoHome.findByPrimaryKey(new Integer(newICObjectTypeID));
				Class theClass = obj.getObjectClass();
				
				String uuid = instance.getUniqueId();
				String xmlId = IBXMLReader.UUID_PREFIX+uuid;
				
				XMLElement newElement = new XMLElement(IBXMLConstants.MODULE_STRING);
				XMLAttribute aId = new XMLAttribute(IBXMLConstants.ID_STRING, xmlId);
				//XMLAttribute aIcObjectId = new XMLAttribute(XMLConstants.IC_OBJECT_ID_STRING,
				//		Integer.toString(newICObjectTypeID));
				XMLAttribute aClass = new XMLAttribute(IBXMLConstants.CLASS_STRING, theClass.getName());
				//        newElement.addAttribute(aId);
				//        newElement.addAttribute(aIcObjectId);
				//        newElement.addAttribute(aClass);
				newElement.setAttribute(aId);
				//newElement.setAttribute(aIcObjectId);
				newElement.setAttribute(aClass);
				parent.addContent(newElement);
				onObjectAdd(parent,newElement,pageKey,xmlId,obj);
			}
			catch (Exception e) {
				e.printStackTrace();
				return false;
			}
			return true;
		}
		return false;
	}*/
	
	/**
	 * Replaced private boolean addNewModule(XMLElement parent, String pageKey, int newICObjectTypeID)
	 * returns UUID of inserted object or null if error occurred
	 */
	private String addNewModule(XMLElement parent, String pageKey, int newICObjectTypeID) {
		String uuid = null;
		boolean result = false;
		try {
			ICObjectInstanceHome icoiHome = (ICObjectInstanceHome) IDOLookup.getHome(ICObjectInstance.class);
			ICObjectHome icoHome = (ICObjectHome)IDOLookup.getHome(ICObject.class);
			
			ICObjectInstance instance = icoiHome.create();
			instance.setICObjectID(newICObjectTypeID);
			instance.setIBPageByKey(pageKey);
			instance.store();
			
			ICObject obj = icoHome.findByPrimaryKey(new Integer(newICObjectTypeID));
			Class theClass = obj.getObjectClass();
			
			uuid = instance.getUniqueId();
			String xmlId = new StringBuffer(IBXMLReader.UUID_PREFIX).append(uuid).toString();
			
			result = addNewModule(xmlId, theClass, parent, pageKey, obj);
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		if (result) {
			return uuid;
		}
		return null;
	}
	
	private boolean addNewModule(String xmlId, Class theClass, XMLElement parent, String pageKey, ICObject obj) {
		try {
			XMLElement newElement = new XMLElement(IBXMLConstants.MODULE_STRING);
			XMLAttribute aId = new XMLAttribute(IBXMLConstants.ID_STRING, xmlId);
			XMLAttribute aClass = new XMLAttribute(IBXMLConstants.CLASS_STRING, theClass.getName());
			newElement.setAttribute(aId);
			newElement.setAttribute(aClass);
			parent.addContent(newElement);
			onObjectAdd(parent,newElement,pageKey,xmlId,obj);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * <p>
	 * TODO tryggvil describe method onObjectAdd
	 * </p>
	 * @param parent
	 * @param newElement
	 * @param pageKey
	 * @param obj
	 */
	private void onObjectAdd(XMLElement parent, XMLElement newElement, String pageKey, String newInstanceId, ICObject obj) {
		if (obj.getClassName().equals(CoreConstants.getArticleItemViewerClass().getName())) {
			
			IBXMLPage xml = null;
			try {
				xml = BuilderLogic.getInstance().getIBXMLPage(pageKey);
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}
			IWMainApplication iwma = IWMainApplication.getDefaultIWMainApplication();
			
			String base = new StringBuffer(CoreConstants.CONTENT_PATH).append(CoreConstants.ARTICLE_CONTENT_PATH).toString();
			String propertyValue = getBuilderLogic().generateResourcePath(base, CoreConstants.ARTICLE_FILENAME_SCOPE, CoreConstants.ARTICLE_FILENAME_SCOPE);
			
			setProperty(iwma, xml, newInstanceId, CoreConstants.ARTICLE_RESOURCE_PATH_PROPERTY_NAME, propertyValue);
			setProperty(iwma, xml, newInstanceId, "showAuthor", "false");
			setProperty(iwma, xml, newInstanceId, "showCreationDate", "false");
		}
		if(obj.getClassName().indexOf("VideoViewer")!=-1){
//			IWMainApplication iwma = IWMainApplication.getDefaultIWMainApplication();
//			try {
//				VideoServices videoServices = (VideoServices) IBOLookup.getServiceInstance(iwma.getIWApplicationContext(), VideoServices.class);
//				IWBundle bundle = obj.getBundle(iwma);
//				videoServices.uploadConfigFile(bundle.getBundleIdentifier(), "/properties/services.xml");
//			} catch (IBOLookupException ile) {
//				throw new IBORuntimeException(ile);
//			} catch (RemoteException re) {
//				//TODO
//			}
			//TODO remove this hardcoded stuff
			IWMainApplication iwma = IWMainApplication.getDefaultIWMainApplication();
			IWBundle bundle = obj.getBundle(iwma);
			try {
				IWSlideService slide = (IWSlideService) IBOLookup.getServiceInstance(iwma.getIWApplicationContext(), IWSlideService.class);
				String config_uri_string = 
					new StringBuffer("bundle://")
					.append(bundle.getBundleIdentifier())
					.append("/properties/services.xml")
					.toString();
				
				BundleResourceResolver resolver = new BundleResourceResolver(iwma);
				URI config_uri = URI.create(config_uri_string);
				
				InputStream resource = resolver.resolve(config_uri).getInputStream();
				slide.uploadFileAndCreateFoldersFromStringAsRoot("/files/cms/settings/", "video-services.xml", resource, null, true);
				resource.close();
				
			} catch (IBOLookupException ile) {
				//TODO
//				throw new IBORuntimeException(ile);
			} catch(RemoteException re) {
				//TODO handling
			} catch(IOException ioe) {
				//TODO handling
			}
		}
	}

	/**
	 *  
	 */
	public boolean addLabel(IBXMLAble xml, int parentObjectInstanceId, int xpos, int ypos, String label) {
		return (true);
	}

	/**
	 *  
	 */
	public String addNewModule(IBXMLAble xml, String pageKey, String parentObjectInstanceID, int newICObjectID,
			int xpos, int ypos, String label) {
		String regionId = parentObjectInstanceID + "." + xpos + "." + ypos;
		return addNewModule(xml, pageKey, parentObjectInstanceID, newICObjectID, regionId, label);
	}

	public String addNewModule(IBXMLAble xml, String pageKey, String parentObjectInstanceID, int newICObjectID,	String regionId,
			String label) {
		
		if(label==null || "null".equals(label)){
			label = regionId; 
		}
		
		XMLElement region = findRegion(xml, label, regionId);
		if (region == null) {
			XMLElement parent = findModule(xml, parentObjectInstanceID);
			if(regionId.equals("-1")){
				//parentElement = xml.getPageRootElement();
			}
			else{
				region = createRegion(regionId, label);
			}
			
			if (parent != null&&region!=null) {
				//This is in a page that is NOT extending a template (is a
				// template itself)
				parent.addContent(region);
			}
			else if(region!=null){
				//This is in a page that is extending a template
				parent = region;
				xml.getPageRootElement().addContent(region);
			}
		}
		return addNewModule(region, pageKey, newICObjectID);
	}

	protected XMLElement createRegion(String regionId, String label) {
		XMLElement region = new XMLElement(IBXMLConstants.REGION_STRING);
		XMLAttribute id = new XMLAttribute(IBXMLConstants.ID_STRING, regionId);
		region.setAttribute(id);
		if (label != null) {
			XMLAttribute labelAttribute = new XMLAttribute(IBXMLConstants.LABEL_STRING, label);
			region.setAttribute(labelAttribute);
		}
		return region;
	}

	/**
	 *  
	 */
	public String addNewModule(IBXMLAble xml, String pageKey, String parentObjectInstanceID, int newICObjectID,	String label) {
		if (label == null) {
			if(parentObjectInstanceID.indexOf(".")>=0){
				String parentID = parentObjectInstanceID.substring(0, parentObjectInstanceID.indexOf("."));
				String theRest = parentObjectInstanceID.substring(parentObjectInstanceID.indexOf(".") + 1,parentObjectInstanceID.length());
				int xpos = Integer.parseInt(theRest.substring(0, theRest.indexOf(".")));
				int ypos = Integer.parseInt(theRest.substring(theRest.indexOf(".") + 1, theRest.length()));
				return addNewModule(xml, pageKey, parentID, newICObjectID, xpos, ypos, label);
			}
			else{
				return addNewModule(findModule(xml, parentObjectInstanceID), pageKey, newICObjectID);
			}
		}
		else {
			String regionId = null;
			if (parentObjectInstanceID.indexOf(CoreConstants.DOT) >= 0) {
				regionId = "-1";
			}
			else {
				regionId = new StringBuilder(IBXMLConstants.REGION_OF_MODULE_STRING).append(parentObjectInstanceID).toString();
			}
			return addNewModule(xml, pageKey, parentObjectInstanceID, newICObjectID, regionId, label);
		}
	}
	
	public String addNewModule(IBXMLAble xml, String pageKey, String parentObjectInstanceID, String regionId, int newICObjectID,	String label) {
		return addNewModule(xml, pageKey, parentObjectInstanceID, newICObjectID, regionId, label);
	}

	/**
	 *  
	 */
	public String addNewModule(IBXMLAble xml, String pageKey, String parentObjectInstanceID,
			ICObject newObjectType, String label) {
		int icObjectId = ((Number) newObjectType.getPrimaryKey()).intValue();
		return addNewModule(xml, pageKey, parentObjectInstanceID, icObjectId, label);
	}

	/**
	 * Checks if the given element is empty, i.e. if it contains no child
	 * elements.
	 * 
	 * @param element
	 * @return
	 */
	public boolean isElementEmpty(XMLElement element) {
		List children = element.getChildren();
		for (Iterator iter = children.iterator(); iter.hasNext();) {
			XMLElement child = (XMLElement) iter.next();
			if (child != null) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Deletes the module
	 */
	public boolean deleteModule(IBXMLAble xml, String parentObjectInstanceID, String instanceId) {
		XMLElement parent = findXMLElementWithId(xml, parentObjectInstanceID);
		if (parent == null) {
			return false;
		}
		try {
			XMLElement module = findModule(xml, instanceId, parent);
			if (module == null) {
				//This is to handle the case when a duplicate empty region
				// (with the same id)
				//prevents the find operation above to find the correct
				// module.
				//This only seems to happen in table regions with e.g.
				// parentObjectInstanceID=1.5.3
				this.log.info("Found likely corrupt duplicate region with id:" + parentObjectInstanceID);
				//Check if the module is empty for safetys sake
				if (isElementEmpty(parent)) {
					//First Delete the corrupt region
					deleteModule(parent.getParent(), parent);
					this.log.info("Deleted corrupt region with id:" + parentObjectInstanceID);
					//Find the parent (region) again:
					parent = findXMLElementWithId(xml, parentObjectInstanceID);
					//Find the module again:
					module = findModule(xml, instanceId, parent);
				}
			}
			boolean result = deleteModule(parent, module);
			if (!result) {
				String newId = new StringBuilder(IBXMLConstants.REGION_OF_MODULE_STRING).append(parentObjectInstanceID).toString();
				result = deleteModule(findXMLElementWithId(xml, newId), module);
			}
			return result;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 *  
	 */
	public boolean lockRegion(IBXMLAble xml, String parentObjectInstanceID) {
		XMLElement parent = findXMLElementWithId(xml, parentObjectInstanceID);
		if (parent != null) {
			XMLAttribute lock = new XMLAttribute(IBXMLConstants.REGION_LOCKED, "true");
			//      if (parent.getAttribute(XMLConstants.REGION_LOCKED) != null)
			//        parent.removeAttribute(XMLConstants.REGION_LOCKED);
			//      parent.addAttribute(lock);
			parent.setAttribute(lock);
			return (true);
		}
		else {
			int index = parentObjectInstanceID.indexOf(".");
			if (index != -1) {
				XMLElement region = new XMLElement(IBXMLConstants.REGION_STRING);
				XMLAttribute id = new XMLAttribute(IBXMLConstants.ID_STRING, parentObjectInstanceID);
				//        region.addAttribute(id);
				region.setAttribute(id);
				String parentID = parentObjectInstanceID.substring(0, index);
				XMLElement regionParent = findModule(xml, parentID);
				if (regionParent != null) {
					regionParent.addContent(region);
				}
				XMLAttribute lock = new XMLAttribute(IBXMLConstants.REGION_LOCKED, "true");
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
	public boolean setAttribute(IBXMLAble xml, String parentObjectInstanceID, String attributeName,
			String attributeValue) {
		XMLElement parent = findXMLElementWithId(xml, parentObjectInstanceID);
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
	public boolean unlockRegion(IBXMLAble xml, String parentObjectInstanceID) {
		XMLElement parent = findXMLElementWithId(xml, parentObjectInstanceID);
		if (parent != null) {
			XMLAttribute lock = new XMLAttribute(IBXMLConstants.REGION_LOCKED, "false");
			//      if (parent.getAttribute(XMLConstants.REGION_LOCKED) != null)
			//        parent.removeAttribute(XMLConstants.REGION_LOCKED);
			//      parent.addAttribute(lock);
			parent.setAttribute(lock);
			return (true);
		}
		else {
			int index = parentObjectInstanceID.indexOf(".");
			if (index != -1) {
				XMLElement region = new XMLElement(IBXMLConstants.REGION_STRING);
				XMLAttribute id = new XMLAttribute(IBXMLConstants.ID_STRING, parentObjectInstanceID);
				//        region.addAttribute(id);
				region.setAttribute(id);
				String parentID = parentObjectInstanceID.substring(0, index);
				XMLElement regionParent = findModule(xml, parentID);
				if (regionParent != null) {
					regionParent.addContent(region);
				}
				XMLAttribute lock = new XMLAttribute(IBXMLConstants.REGION_LOCKED, "false");
				//        region.addAttribute(lock);
				region.setAttribute(lock);
				return (true);
			}
		}
		return (false);
	}

	private boolean deleteModule(XMLElement parent, XMLElement child) throws Exception{
		return removeElement(parent,child,true);
	}
	/**
	 *  
	 */
	public boolean removeElement(XMLElement parent, XMLElement child, boolean removeICObjectInstance) throws Exception {
		List children = getChildElements(child);
		if (children != null) {
			Iterator iter = children.iterator();
			while (iter.hasNext()) {
				XMLElement childchild = (XMLElement) iter.next();
				removeElement(child, childchild,removeICObjectInstance);
			}
			if(removeICObjectInstance){
				XMLAttribute attribute = child.getAttribute(IBXMLConstants.ID_STRING);
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
		}
		boolean removeSuccess = parent.removeContent(child);
		return removeSuccess;
	}

	/**
	 *  
	 */
	private List getChildElements(XMLElement parent) {
		return parent.getChildren();
	}

	/**
	 *  
	 */
	public boolean labelRegion(IBXMLAble xml, String parentObjectInstanceID, String label) {
		XMLElement parent = findXMLElementWithId(xml, parentObjectInstanceID);
		if (parent != null) {
			if (label != null && !label.equals("")) {
				XMLAttribute labelAttribute = new XMLAttribute(IBXMLConstants.LABEL_STRING, label);
				//        if (parent.getAttribute(XMLConstants.LABEL_STRING) != null)
				//          parent.removeAttribute(XMLConstants.LABEL_STRING);
				//        parent.addAttribute(labelAttribute);
				parent.setAttribute(labelAttribute);
			}
			else {
				if (parent.getAttribute(IBXMLConstants.LABEL_STRING) != null) {
					parent.removeAttribute(IBXMLConstants.LABEL_STRING);
				}
			}
			return (true);
		}
		else {
			int index = parentObjectInstanceID.indexOf(".");
			if (index != -1) {
				if (label != null && !label.equals("")) {
					XMLElement region = new XMLElement(IBXMLConstants.REGION_STRING);
					XMLAttribute id = new XMLAttribute(IBXMLConstants.ID_STRING, parentObjectInstanceID);
					//          region.addAttribute(id);
					region.setAttribute(id);
					String parentID = parentObjectInstanceID.substring(0, index);
					XMLElement regionParent = findModule(xml, parentID);
					if (regionParent != null) {
						regionParent.addContent(region);
					}
					XMLAttribute labelAttribute = new XMLAttribute(IBXMLConstants.LABEL_STRING, label);
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
	public boolean copyModule(IBXMLAble xml, String parentObjectInstanceID, String instanceId) {
		XMLElement parent = findXMLElementWithId(xml, parentObjectInstanceID);
		if (parent != null) {
			try {
				XMLElement module = findModule(xml, instanceId, parent);
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
	private boolean copyModule(XMLElement parent, XMLElement child) throws Exception {
		List children = getChildElements(child);
		if (children != null) {
			Iterator iter = children.iterator();
			while (iter.hasNext()) {
				XMLElement childchild = (XMLElement) iter.next();
				copyModule(child, childchild);
			}
			XMLAttribute attribute = child.getAttribute(IBXMLConstants.ID_STRING);
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
	public boolean addNewElement(IBXMLAble xml, String parentObjectInstanceID, XMLElement element) {
		XMLElement parent = findModule(xml, parentObjectInstanceID);
		if (parent != null) {
			parent.addContent(element);
		}
		return true;
	}
	
	/**
	 *  
	 */
	public boolean insertElementAbove(IBXMLAble xml, String parentInstanceId, XMLElement elementToInsert, String instanceIdToInsertAbove) {
		return insertElement(xml,null,parentInstanceId,instanceIdToInsertAbove,elementToInsert,true,false);
	}
	
	public boolean insertElementBelow(IBXMLAble xml, String parentInstanceId, XMLElement elementToInsert, String instanceIdToInsertBelow) {
		return insertElement(xml,null,parentInstanceId,instanceIdToInsertBelow,elementToInsert,false,false);
	}
	
	public boolean pasteElementLastIntoParentOrRegion(IBXMLAble xml, String pageKey, String parentInstanceId, String label, XMLElement element) {
		return insertElementLastIntoParentOrRegion(xml, pageKey, parentInstanceId, label, element, true);
	}
	
	public boolean insertElementLastIntoParentOrRegion(IBXMLAble xml, String pageKey, String parentInstanceId, String label, XMLElement element) {
		return insertElementLastIntoParentOrRegion(xml, pageKey, parentInstanceId, label, element, false);
	}
	
	public boolean insertElementLastIntoParentOrRegion(IBXMLAble xml, String pageKey, String parentInstanceId, String label, XMLElement element, boolean changeInstanceId) {
		String instanceId = insertElementLast(xml, pageKey, parentInstanceId, label, element, changeInstanceId);
		return instanceId != null;
	}
	
	public String insertElementLast(IBXMLAble xml, String pageKey, String parentInstanceId, String label, XMLElement element, boolean changeInstanceId) {
		if (changeInstanceId) {
			changeModuleIds(element, pageKey);
		}
		
		XMLElement parent = findXMLElementWithId(xml, parentInstanceId);
		if (parent != null) {
			parent.addContent(element);
			return getElementId(element);
		}
		else {
			int index = parentInstanceId.indexOf(CoreConstants.DOT);
			if (index != -1) {
				XMLElement region = new XMLElement(IBXMLConstants.REGION_STRING);
				XMLAttribute id = new XMLAttribute(IBXMLConstants.ID_STRING, parentInstanceId);
				region.setAttribute(id);
				
				String parentID = parentInstanceId.substring(0, index);
				XMLElement regionParent = findModule(xml, parentID);
				
				if (label != null) {
					XMLAttribute labelAttr = new XMLAttribute(IBXMLConstants.LABEL_STRING, label);
					region.setAttribute(labelAttr);
				}
				
				if (regionParent != null) {
					regionParent.addContent(region);
				}
				else {
					xml.getPageRootElement().addContent(region);
				}
				
				region.addContent(element);
				return getElementId(element);
			}
			else {
				XMLElement region = findRegion(xml, label, parentInstanceId);
				if (region == null) {
					//add the region 
					region = createRegion(parentInstanceId, label);
					//This is in a page that is extending a template
					xml.getPageRootElement().addContent(region);
				}
				region.addContent(element);
				return getElementId(element);
			}
		}
	}
	
	private String getElementId(XMLElement element) {
		if (element == null) {
			return null;
		}
		XMLAttribute attribute = element.getAttribute(IBXMLConstants.ID_STRING);
		if (attribute == null) {
			return null;
		}
		return attribute.getValue();
	}
	
	protected boolean addRegionToRootElement(IBXMLAble xml, String label, String parentId) {
		XMLElement region = findRegion(xml, label, parentId);
		if (region == null) {
			region = createRegion(parentId, label);
			xml.getPageRootElement().addContent(region);
			return true;
		}
		return false;
	}

	public boolean pasteElementBelow(IBXMLAble xml, String pageKey, String parentObjectInstanceID,String objectId, XMLElement element) {
		return pasteElement(xml, pageKey, parentObjectInstanceID,objectId, element, false);
	}
	
	public boolean pasteElementAbove(IBXMLAble xml, String pageKey, String parentObjectInstanceID,String objectId, XMLElement element) {
		return pasteElement(xml, pageKey, parentObjectInstanceID,objectId, element, true);
	}
	
	public boolean pasteElement(IBXMLAble xml, String pageKey, String parentObjectInstanceID,String objectId, XMLElement elementToPaste, boolean pasteAbove){
		return insertElement(xml, pageKey, parentObjectInstanceID, objectId, elementToPaste, pasteAbove,true);
	}
	
	/**
	 *  
	 */
	public boolean insertElement(IBXMLAble xml, String pageKey, String parentObjectInstanceID,
			String objectId, XMLElement element, boolean pasteAbove, boolean changeInstanceId) {
		if(changeInstanceId){
			changeModuleIds(element, pageKey);
		}
		XMLElement parent = findXMLElementWithId(xml, parentObjectInstanceID);
		if (parent != null) {
			//      parent.addContent(element);
			List li = parent.getChildren();
			int index = -1;
			if (li != null) {
				Iterator it = li.iterator();
				while (it.hasNext()) {
					XMLElement el = (XMLElement) it.next();
					index++;
					if (el.getName().equals(IBXMLConstants.MODULE_STRING)) {
						XMLAttribute id = el.getAttribute(IBXMLConstants.ID_STRING);
						if (id != null) {
							if (id.getValue().equals(objectId)) {
								break;
							}
						}
					}
				}
				if (index != -1) {
					parent.removeChildren();
					it = li.iterator();
					int counter = -1;
					while (it.hasNext()) {
						counter++;
						if (counter == index && pasteAbove){
							parent.addContent(element);
						}
						XMLElement el = (XMLElement) it.next();
						parent.addContent(el);
						if(counter==index && !pasteAbove){
							parent.addContent(element);
						}
						
						
					}
				}
			}
			else {
				parent.addContent(element); //hmmmm
			}
			return (true);
		}
		return (false);
	}

	/**
	 *  
	 */
	private boolean changeModuleIds(XMLElement element, String pageKey) {
		try {
			XMLAttribute instanceIDAttribute = element.getAttribute(IBXMLConstants.ID_STRING);
			
			//only for backward compatability, we now only use the ID attribute with a UUID
			XMLAttribute object_id = element.getAttribute(IBXMLConstants.IC_OBJECT_ID_STRING);
			
			if(object_id!=null){
				//OLD WAY
				//If a ic_object_id is found then try to generate a new ic_object_instance and take its uniqueId:
				ICObjectInstanceHome home = (ICObjectInstanceHome) IDOLookup.getHome(ICObjectInstance.class);
				ICObjectInstance instance = home.create();
				instance.setICObjectID(object_id.getIntValue());
				instance.setIBPageByKey(pageKey);
				instance.store();
				//String moduleId = instance.getPrimaryKey().toString();
				String uuid = instance.getUniqueId();
				String moduleId = IBXMLReader.UUID_PREFIX+uuid;
				instanceIDAttribute = new XMLAttribute(IBXMLConstants.ID_STRING, moduleId);
			}
			else{
				//NEW WAY
				//If no ic_object_id is found and the instanceid is the new UUID type of id (or a made up on) then try and create a new icobjectinstance and generate a new UUID
				UUIDGenerator generator = UUIDGenerator.getInstance();
				XMLAttribute classNameXML = element.getAttribute(IBXMLConstants.CLASS_STRING);
				String className = null;
				if(classNameXML!=null){
					className = classNameXML.getValue();
				}
				
				String newUuid = generator.generateId();
				String newValue = IBXMLReader.UUID_PREFIX+newUuid;
				instanceIDAttribute = new XMLAttribute(IBXMLConstants.ID_STRING, newValue);
				
				if(className!=null){
					//making sure there creates a new ICObjectInstance record
					getBuilderLogic().getIBXMLReader().getICObjectInstanceFromComponentId(newValue, className,pageKey);
				}
			}
			
			element.setAttribute(instanceIDAttribute);
			List childs = element.getChildren(IBXMLConstants.MODULE_STRING);
			if (childs != null) {
				Iterator it = childs.iterator();
				while (it.hasNext()) {
					XMLElement child = (XMLElement) it.next();
					if (!changeModuleIds(child, pageKey)) {
						return (false);
					}
				}
			}
			childs = element.getChildren(IBXMLConstants.REGION_STRING);
			if (childs != null) {
				Iterator it = childs.iterator();
				while (it.hasNext()) {
					XMLElement el = (XMLElement) it.next();
					XMLAttribute regionId = el.getAttribute(IBXMLConstants.ID_STRING);
					if (regionId != null) {
						String regId = regionId.getValue();
						int index = regId.indexOf(".");
						if (index > -1) {
							String sub = regId.substring(index);
							sub = instanceIDAttribute.getValue() + sub;
							regionId = new XMLAttribute(IBXMLConstants.ID_STRING, sub);
							el.setAttribute(regionId);
						}
					}
					List childs2 = el.getChildren(IBXMLConstants.MODULE_STRING);
					if (childs2 != null) {
						Iterator it2 = childs2.iterator();
						while (it2.hasNext()) {
							XMLElement child = (XMLElement) it2.next();
							if (!changeModuleIds(child, pageKey)) {
								return (false);
							}
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
	public XMLElement copyModule(IBXMLAble xml, String id) {
		return (findXMLElementWithId(xml, id));
	}
	
	protected BuilderLogic getBuilderLogic(){
		return BuilderLogic.getInstance();
	}
}