/*
 * $Id: XMLReader.java,v 1.52 2004/05/11 14:35:04 gummi Exp $
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

import com.idega.builder.dynamicpagetrigger.util.DPTCrawlable;
import com.idega.core.component.data.ICObjectInstance;
import com.idega.event.ObjectInstanceCacher;
import com.idega.presentation.Page;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.PresentationObjectContainer;
import com.idega.presentation.Table;
import com.idega.xml.XMLAttribute;
import com.idega.xml.XMLElement;
import com.idega.xml.XMLException;

/**
 * @author <a href="tryggvi@idega.is">Tryggvi Larusson</a>,<a href="palli@idega.is">Pall Helgason</a>
 * @version 1.0
 */
public class XMLReader {
	/**
	 *
	 */
	private XMLReader() {
	}

	/**
	 *
	 */
	private static void setAllBuilderControls(PresentationObjectContainer parent, boolean setTo) {
		List list = parent.getChildren();
		if (list != null) {
			Iterator it = list.iterator();
			while (it.hasNext()) {
				PresentationObject obj = (PresentationObject) it.next();
				obj.setUseBuilderObjectControl(setTo);
				obj.setBelongsToParent(true);
				if (obj instanceof PresentationObjectContainer) {
					setAllBuilderControls((PresentationObjectContainer) obj, setTo);
				}
			}
		}
	}

	/**
	 *
	 */
	static Page getPopulatedPage(IBXMLPage ibxml) {
		Page parentContainer = null;
		String pageKey = null;
		XMLElement root = ibxml.getRootElement();
		XMLElement pageXML = root.getChild(XMLConstants.PAGE_STRING);
		List pageAttr = pageXML.getAttributes();
		Iterator attr = pageAttr.iterator();

		boolean hasTemplate = false;
		boolean isTemplate = false;
		boolean isLocked = true;
		String dptRootPage = null;

		// Parse the page attributes
		while (attr.hasNext()) {
			XMLAttribute at = (XMLAttribute) attr.next();
			if (at.getName().equalsIgnoreCase(XMLConstants.TEMPLATE_STRING)) {
				hasTemplate = true;
				parentContainer = PageCacher.getPage(at.getValue());
				parentContainer.setIsExtendingTemplate();
				parentContainer.setTemplateId(at.getValue());
				setAllBuilderControls(parentContainer, false);
			}
			else if (at.getName().equalsIgnoreCase(XMLConstants.PAGE_TYPE)) {
				String value = at.getValue();
				if (value.equals(XMLConstants.PAGE_TYPE_TEMPLATE) || value.equals(XMLConstants.PAGE_TYPE_DPT_TEMPLATE)) {
					isTemplate = true;
				}
			}
			else if (at.getName().equalsIgnoreCase(XMLConstants.ID_STRING)) {
				pageKey = (String) at.getValue();
			}
			else if (at.getName().equalsIgnoreCase(XMLConstants.REGION_LOCKED)) {
				if (at.getValue().equals("false"))
					isLocked = false;
				else
					isLocked = true;
			} 
			else if (at.getName().equalsIgnoreCase(XMLConstants.DPT_ROOTPAGE_STRING)) {
				dptRootPage = (String)at.getValue();
			}
		}

		//If the page does not extend a template it has no parent container
		if (!hasTemplate) {
			parentContainer = new Page();
		}
		else {
			setTemplateObjectsForPage(ibxml);
		}

		if (isLocked)
			parentContainer.lock();
		else
			parentContainer.unlock();

		//Set the type of the page
		if (isTemplate) {
			parentContainer.setIsTemplate();
			ibxml.setType(XMLConstants.PAGE_TYPE_TEMPLATE);
		}
		else {
			parentContainer.setIsPage();
			ibxml.setType(XMLConstants.PAGE_TYPE_PAGE);
		}

		//sets the id of the page
		try {
			int id = Integer.parseInt(pageKey);
			parentContainer.setPageID(id);
		}
		catch (NumberFormatException e) {
			try {
				parentContainer.setPageID(Integer.parseInt(ibxml.getKey()));
			}
			catch (NumberFormatException ex) {
				//      System.err.println("NumberFormatException - ibxml.getKey():"+ibxml.getKey()+" not Integer");
			}
		}
		
		//sets dptRootpageID
		try {
			if(dptRootPage!=null) {
				parentContainer.setDPTRootPage(dptRootPage);
			}
		}
		catch (NumberFormatException e) {
			e.printStackTrace();
		}
		
		parentContainer.setTitle(ibxml.getName());

		if (pageXML.hasChildren()) {
			List children = pageXML.getChildren();
			Iterator it = children.iterator();

			while (it.hasNext()) {
				XMLElement child = (XMLElement) it.next();
				
				if (child.getName().equalsIgnoreCase(XMLConstants.PROPERTY_STRING)) {
					setProperties(child, parentContainer);
				}
				else if (child.getName().equalsIgnoreCase(XMLConstants.ELEMENT_STRING) || child.getName().equalsIgnoreCase(XMLConstants.MODULE_STRING)) {
					if (!parentContainer.getIsExtendingTemplate())
						parseElement(child, parentContainer, ibxml);
					else if (!parentContainer.isLocked())
						parseElement(child, parentContainer, ibxml);
				}
				else if (child.getName().equalsIgnoreCase(XMLConstants.REGION_STRING)) {
					parseRegion(child, parentContainer, ibxml);
				}
				else if (child.getName().equalsIgnoreCase(XMLConstants.CHANGE_PAGE_LINK)) {
					changeDPTCrawlableLinkedPageProperty(child, parentContainer);
				}
				else if (child.getName().equals(XMLConstants.CHANGE_IC_INSTANCE_ID)) {
					changeInstanceId(child, parentContainer);
				}
				else {
					System.err.println("Unknown tag in xml description file : " + child.getName());
				}
			}
		}

		return (parentContainer);
	}

	/**
	 *
	 */
	static void parseRegion(XMLElement reg, PresentationObjectContainer regionParent, IBXMLPage ibxml) {
		List regionAttrList = reg.getAttributes();
		PresentationObjectContainer newRegionParent = regionParent;
		if ((regionAttrList == null) || (regionAttrList.isEmpty())) {
			System.err.println("Table region has no attributes");
			return;
		}

		int x = 1;
		int y = 1;
		boolean isLocked = true;

		XMLAttribute locked = reg.getAttribute(XMLConstants.REGION_LOCKED);
		if (locked != null) {
			if (locked.getValue().equalsIgnoreCase("true"))
				isLocked = true;
			else
				isLocked = false;
		}

		XMLAttribute label = reg.getAttribute(XMLConstants.LABEL_STRING);

		XMLAttribute regionIDattr = reg.getAttribute(XMLConstants.ID_STRING);
		String regionID = null;
		if (regionIDattr != null) {
			regionID = regionIDattr.getValue();
			try {
				Integer.parseInt(regionID);
				XMLAttribute regionAttrX = reg.getAttribute(XMLConstants.X_REGION_STRING);
				if (regionAttrX != null) {
					try {
						x = regionAttrX.getIntValue();
					}
					catch (XMLException e) {
						System.err.println("Unable to convert x region attribute to integer");
						x = 1;
					}
				}
				XMLAttribute regionAttrY = reg.getAttribute(XMLConstants.Y_REGION_STRING);
				if (regionAttrY != null) {
					try {
						y = regionAttrY.getIntValue();
					}
					catch (XMLException e) {
						System.err.println("Unable to convert y region attribute to integer");
						y = 1;
					}
				}
			}
			catch (NumberFormatException e) {
				//Integer.parseInt(regionID.substring(0, regionID.indexOf(".")));
				String theRest = regionID.substring(regionID.indexOf(".") + 1, regionID.length());
				x = Integer.parseInt(theRest.substring(0, theRest.indexOf(".")));
				y = Integer.parseInt(theRest.substring(theRest.indexOf(".") + 1, theRest.length()));
			}
		}

		boolean parseChildren = true;
		boolean emptyParent = false;

		if (regionParent instanceof com.idega.presentation.Page) {
			if ((regionID == null) || (regionID.equals(""))) {
				System.err.println("Missing id attribute for region tag");
				return;
			}
			if (((Page) regionParent).getIsExtendingTemplate()) {
				newRegionParent = (PresentationObjectContainer) regionParent.getContainedObject(regionID);

				if (newRegionParent == null) {
					if (label != null) {
						newRegionParent = (PresentationObjectContainer) regionParent.getContainedLabeledObject(label.getValue());
						if (newRegionParent == null) {
							parseChildren = false;
						}
					}
					else
						parseChildren = false;
				}
				else {
					if ((newRegionParent.getBelongsToParent()) && (newRegionParent.isLocked()))
						parseChildren = false;
					else
						emptyParent = true;
				}
			}
		}
		else if (regionParent instanceof com.idega.presentation.Table) {
			if (isLocked)
				 ((Table) regionParent).lock(x, y);
			else
				 ((Table) regionParent).unlock(x, y);

			if (label != null) {
				((Table) regionParent).setLabel(label.getValue(), x, y);
			}

			newRegionParent = ((Table) regionParent).containerAt(x, y);
		}

		if (parseChildren) {
			if (reg.hasChildren()) {
				if (emptyParent)
					newRegionParent.empty();
				List children = reg.getChildren();
				Iterator childrenIt = children.iterator();

				while (childrenIt.hasNext())
					parseElement((XMLElement) childrenIt.next(), newRegionParent, ibxml);
			}
		}
	}

	/**
	 *
	 */
	static void setProperties(XMLElement properties, PresentationObject object) {
		String key = null;
		Vector values = new Vector(1);
		String vals[] = null;

		List li = properties.getChildren();
		Iterator it = li.iterator();

		while (it.hasNext()) {
			XMLElement e = (XMLElement) it.next();

			if (e.getName().equalsIgnoreCase(XMLConstants.NAME_STRING)) {
				if (key != null) {
					vals = new String[values.size()];
					for (int i = 0; i < values.size(); i++)
						vals[i] = (String) values.elementAt(i);
					object.setProperty(key, vals);
					values.clear();
				}
				key = e.getTextTrim();
			}
			else if (e.getName().equalsIgnoreCase(XMLConstants.VALUE_STRING)) {
				values.addElement(e.getTextTrim());
			}
		}

		if (key != null) {
			//key is MethodIdentifier
			if (key.startsWith(XMLConstants.METHOD_STRING)) {
				try {
					setReflectionProperty(object, key, values);
				}
				catch(Exception e) {
					e.printStackTrace();	
				}
			}
			else {
				vals = new String[values.size()];
				for (int i = 0; i < values.size(); i++)
					vals[i] = (String) values.elementAt(i);
				object.setProperty(key, vals);
			}
		}
	}

	/**
	 *
	 */
	static void setReflectionProperty(PresentationObject instance, String methodIdentifier, Vector stringValues) {
		ComponentPropertyHandler.getInstance().setReflectionProperty(instance, methodIdentifier, stringValues);
	}

	/**
	 *
	 */
	static void parseElement(XMLElement el, PresentationObjectContainer parent, IBXMLPage ibxml) {
		PresentationObject inst = null;
		List at = el.getAttributes();
		boolean isLocked = true;

		if ((at == null) || (at.isEmpty())) {
			System.err.println("No attributes specified");
			return;
		}
		String className = null;
		String id = null;
		String ic_object_id = null;
		Iterator it = at.iterator();
		while (it.hasNext()) {
			XMLAttribute attr = (XMLAttribute) it.next();
			if (attr.getName().equalsIgnoreCase(XMLConstants.CLASS_STRING)) {
				className = attr.getValue();
			}
			else if (attr.getName().equalsIgnoreCase(XMLConstants.ID_STRING)) {
				id = attr.getValue();
			}
			else if (attr.getName().equalsIgnoreCase(XMLConstants.IC_OBJECT_ID_STRING)) {
				ic_object_id = attr.getValue();
			}
			else if (attr.getName().equalsIgnoreCase(XMLConstants.REGION_LOCKED)) {
				if (attr.getValue().equals("false"))
					isLocked = false;
				else
					isLocked = true;
			}
		}

		try {
			if (id == null) {
				try {
					inst = (PresentationObject) Class.forName(className).newInstance();
				}
				catch (Exception e) {
					e.printStackTrace(System.err);
					throw new Exception("Invalid Class tag for module");
				}
			}
			else {
				ICObjectInstance ico = ((com.idega.core.component.data.ICObjectInstanceHome) com.idega.data.IDOLookup.getHomeLegacy(ICObjectInstance.class)).findByPrimaryKeyLegacy(Integer.parseInt(id));
				inst = ico.getNewInstance();
				inst.setICObjectInstance(ico);
				if (ic_object_id == null) {
					inst.setICObject(ico.getObject());
				}
				else {
					inst.setICObjectID(Integer.parseInt(ic_object_id));
				}
				// added by gummi@idega.is // - cache ObjectInstance
				if (!"0".equals(id)) {
					setObjectInstance(ibxml, id, inst);
				}
			}

			if (inst instanceof PresentationObjectContainer) {
				if (isLocked)
					 ((PresentationObjectContainer) inst).lock();
				else
					 ((PresentationObjectContainer) inst).unlock();
			}

			if (inst instanceof com.idega.presentation.Table) {
				com.idega.presentation.Table table = (com.idega.presentation.Table) inst;
				parent.add(table);

				if (el.hasChildren()) {
					List children = el.getChildren();
					Iterator itr = children.iterator();

					while (itr.hasNext()) {
						XMLElement child = (XMLElement) itr.next();
						if (child.getName().equalsIgnoreCase(XMLConstants.PROPERTY_STRING)) {
							setProperties(child, table);
						}
						else if (child.getName().equalsIgnoreCase(XMLConstants.ELEMENT_STRING) || child.getName().equalsIgnoreCase(XMLConstants.MODULE_STRING)) {
							parseElement(child, table, ibxml);
						}
						else if (child.getName().equalsIgnoreCase(XMLConstants.REGION_STRING)) {
							parseRegion(child, table, ibxml);
						}
						else
							System.err.println("Unknown tag in xml description file : " + child.getName());
					}
				}
			}
			else {
				try {
					parent.add(inst);
				}
				catch (Exception e) {
					e.printStackTrace(System.err);
					if (parent != null)
						System.err.println("ParentID: " + parent.getID());
					if (inst != null)
						System.err.println("InstanceID: " + inst.getICObjectInstanceID());
				}
				if (el.hasChildren()) {
					List children = el.getChildren();
					Iterator itr = children.iterator();

					while (itr.hasNext()) {
						XMLElement child = (XMLElement) itr.next();
						if (child.getName().equalsIgnoreCase(XMLConstants.PROPERTY_STRING)) {
							setProperties(child, inst);
						}
						else if (child.getName().equalsIgnoreCase(XMLConstants.ELEMENT_STRING) || child.getName().equalsIgnoreCase(XMLConstants.MODULE_STRING)) {
							parseElement(child, (PresentationObjectContainer) inst, ibxml);
						}
						else if (child.getName().equalsIgnoreCase(XMLConstants.REGION_STRING)) {
							parseRegion(child, (PresentationObjectContainer) inst, ibxml);
						}
						else {
							System.err.println("Unknown tag in xml description file : " + child.getName());
						}
					}
				}
			}
		}
		catch (ClassNotFoundException e) {
			System.err.println("The specified class can not be found: " + className);
			e.printStackTrace();
		}
		catch (java.lang.IllegalAccessException e2) {
			System.err.println("Illegal access");
			e2.printStackTrace();
		}
		catch (java.lang.InstantiationException e3) {
			System.err.println("Unable to instanciate class: " + className);
			e3.printStackTrace();
		}
		catch (Exception e4) {
			System.err.println("Exception");
			e4.printStackTrace();
		}
	}

	/**
	 *
	 */
	static void changeDPTCrawlableLinkedPageProperty(XMLElement change, PresentationObjectContainer parent) {
		List regionAttrList = change.getAttributes();
		if ((regionAttrList == null) || (regionAttrList.isEmpty())) {
			System.err.println("Table region has no attributes");
			return;
		}

		XMLAttribute id = change.getAttribute(XMLConstants.LINK_ID_STRING);
		XMLAttribute newPageLink = change.getAttribute(XMLConstants.LINK_TO);

		int intId = -1;
		int intNewPage = -1;
		try {
			intId = id.getIntValue();
			intNewPage = newPageLink.getIntValue();
		}
		catch (com.idega.xml.XMLException e) {
			e.printStackTrace();
		}
		List li = parent.getChildrenRecursive();
		if (li != null) {
			Iterator it = li.iterator();
			while (it.hasNext()) {
				PresentationObject obj = (PresentationObject) it.next();
				if (obj instanceof DPTCrawlable) {
					DPTCrawlable l = (DPTCrawlable) obj;
					if (intId == l.getICObjectInstanceID()) {
						l.setLinkedDPTPageID(intNewPage);
					}
				}
			}
		}
	}

	/**
	 *
	 */
	static void changeInstanceId(XMLElement change, Page page) {
		int from = -1, to = -1;
		try {
			from = change.getAttribute(XMLConstants.IC_INSTANCE_ID_FROM).getIntValue();
			to = change.getAttribute(XMLConstants.IC_INSTANCE_ID_TO).getIntValue();
		}
		catch (XMLException e) {
			e.printStackTrace();
			return;
		}
		
		if (from != -1 && to != -1) {
			List children = page.getChildrenRecursive();
			if (children != null) {
				Iterator it = children.iterator();
				while (it.hasNext()) {
					PresentationObject obj = (PresentationObject) it.next();
					if (obj.getICObjectInstanceID() == from) {
						obj.changeInstanceIdForInheritedObject(to);
						ObjectInstanceCacher.changeObjectInstanceID(page, Integer.toString(from), Integer.toString(to), obj);
						return;
					}
				}
			}
		}
	}
	

	public static void setTemplateObjectsForPage(IBXMLPage ibxml){
	  setObjectInstance(ibxml, null, null);
	}

	public static void setObjectInstance(IBXMLPage ibxml, String instanceKey, PresentationObject objectInstance){
	  if(instanceKey != null){
		ObjectInstanceCacher.putObjectIntanceInCache(instanceKey,objectInstance);
	  }
	  //System.err.println("Cashing objectInstance: "+instanceKey);
	  String pageKey = ibxml.getKey();
	  String templatePageKey = Integer.toString(ibxml.getTemplateId());
	  
	  ObjectInstanceCacher.copyInstancesFromPageToPage(templatePageKey,pageKey);

	  //System.err.println("Cashing objectInstance: "+instanceKey+" on page "+ ibxml.getKey()+" extending: "+ibxml.getTemplateId());
	  if(instanceKey != null){
		ObjectInstanceCacher.getObjectInstancesCachedForPage(ibxml.getKey()).put(instanceKey,objectInstance);
	  }
	}
	
}
