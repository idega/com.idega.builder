package com.idega.builder.data;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.ejb.FinderException;

import com.idega.builder.business.IBPageHelper;
import com.idega.builder.business.PageTreeNode;
import com.idega.builder.business.XMLConstants;
import com.idega.core.builder.data.ICPage;
import com.idega.core.component.data.ICObject;
import com.idega.core.component.data.ICObjectHome;
import com.idega.data.IDOLookup;
import com.idega.data.IDOLookupException;
import com.idega.io.ObjectReader;
import com.idega.io.ObjectWriter;
import com.idega.io.Storable;
import com.idega.presentation.IWContext;
import com.idega.util.xml.XMLData;
import com.idega.xml.XMLElement;

/**
 * <p>Title: idegaWeb</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: idega Software</p>
 * @author <a href="thomas@idega.is">Thomas Hilbig</a>
 * @version 1.0
 * Created on Mar 24, 2004
 */
public class IBExportImportData implements Storable {
	
	public static final String EXPORT_NAME = "builderpages";
	public static final String EXPORT_METADATA_NAME = "metadata";
	public static final String EXPORT_METADATA_FILE_NAME = EXPORT_METADATA_NAME + ".xml";
	
	private List files = new ArrayList();
	private List fileElements = new ArrayList(); 
	private List necessaryModules = new ArrayList();
	
	private XMLElement pagesElement = null; 
	private XMLElement templatesElement = null;
	private XMLData metadataSummary = null;

	
	private Map childParent = null;
	protected List pageIds = null;

	
	private static String getSourceClassForPage() {
		 return ICPage.class.getName();
	}
		
	
	public String getName() {
		return EXPORT_NAME;
	}
	
	public List getData() {
		return files;
	}
	
	public String getParentIdForPageId(String id) {
		return (String) ((childParent == null) ? null : childParent.get(id));
	}
	
	public List getNonPageFileElements() {
		return getPageElementsOrNonPageElements(false);
	}
	
	/** returns templates first */
	public List getSortedPageElements() {
		List pageElements = getPageElementsOrNonPageElements(true);
		Collections.sort(pageElements, new PageElementComparator());
		return pageElements;
	}
	
	public List validateAndGetMissingModuleNames() throws IOException {
		if (necessaryModules == null) {
			return null;
		}
		List missingModules = null;
		Iterator iterator = necessaryModules.iterator();
		while (iterator.hasNext()) {
			XMLElement moduleElement = (XMLElement) iterator.next();
			String className = moduleElement.getTextTrim(XMLConstants.MODULE_CLASS);
			if (checkCObject(className)) {
				if (missingModules == null) {
					missingModules = new ArrayList();
				}
				missingModules.add(className);
			}
		}
		return missingModules;
	}
		
	
	public void modifyElementSetNameSetOriginalName(int index, String name, String originalName, String mimeType) {
		XMLElement fileElement = (XMLElement) fileElements.get(index);
		fileElement.addContent(XMLConstants.FILE_USED_ID,name);
		fileElement.addContent(XMLConstants.FILE_ORIGINAL_NAME, originalName);
		fileElement.addContent(XMLConstants.FILE_MIME_TYPE, mimeType);
	}
	
	public void modifyElementSetNameSetOriginalNameLikeElementAt(int index, int existingDataIndex) {
		XMLElement fileElement = (XMLElement) fileElements.get(existingDataIndex);
		String name = fileElement.getTextTrim(XMLConstants.FILE_USED_ID);
		String originalName = fileElement.getTextTrim(XMLConstants.FILE_ORIGINAL_NAME);
		String mimeType = fileElement.getTextTrim(XMLConstants.FILE_MIME_TYPE);
		modifyElementSetNameSetOriginalName(index, name, originalName, mimeType);
	}
		
	public void addPageTree(IWContext iwc) throws IDOLookupException, FinderException {
		List pageTreeNodes = IBPageHelper.getInstance().getFirstLevelPageTreeNodesDomainFirst(iwc);
		pagesElement = new XMLElement(XMLConstants.PAGE_TREE_PAGES);
		addPages(pageTreeNodes.iterator(), pagesElement);
	}
	
	public void addTemplateTree(IWContext iwc) throws IDOLookupException, FinderException {
		List pageTreeNodes = IBPageHelper.getInstance().getFirstLevelPageTreeNodesTemplateDomainFirst(iwc);
		templatesElement = new XMLElement(XMLConstants.PAGE_TREE_TEMPLATES);
		addPages(pageTreeNodes.iterator(), templatesElement);
	}


	private void addPages(Iterator pageTreeNodeIterator, XMLElement element) {
		if (pageTreeNodeIterator == null) {
			return;
		}
		while (pageTreeNodeIterator.hasNext()) {
			PageTreeNode node = (PageTreeNode) pageTreeNodeIterator.next();
			String name = node.getNodeName();
			String id = Integer.toString(node.getNodeID());
			XMLElement pageElement = new XMLElement(XMLConstants.PAGE_TREE_PAGE);
			pageElement.addContent(XMLConstants.PAGE_TREE_NAME, name);
			pageElement.addContent(XMLConstants.PAGE_TREE_ID, id);
			Iterator iterator = node.getChildren();
			addPages(iterator, pageElement);
			element.addContent(pageElement);
		}
	}
	
	public void addFileEntry(IBReference.Entry entry, Storable storable, String value) {
		files.add(storable);
		XMLElement fileElement = new XMLElement(XMLConstants.FILE_FILE);
		fileElement.addContent(XMLConstants.FILE_MODULE, entry.getModuleClass());
		fileElement.addContent(XMLConstants.FILE_NAME, entry.getValueName());
		fileElement.addContent(XMLConstants.FILE_SOURCE, entry.getSourceClass());
		fileElement.addContent(XMLConstants.FILE_VALUE, value);
		fileElements.add(fileElement);
	}
	
	public void addFileEntry(ICPage page) {
		files.add(page);
		XMLElement fileElement = new XMLElement(XMLConstants.FILE_FILE);
		fileElement.addContent(XMLConstants.FILE_SOURCE, IBExportImportData.getSourceClassForPage());
		fileElement.addContent(XMLConstants.FILE_NAME, page.getIDColumnName());
		fileElement.addContent(XMLConstants.FILE_VALUE, page.getPrimaryKey().toString());
		fileElements.add(fileElement);
	}
	
	public void addNecessaryModule(String moduleClassName) throws IOException {
		ICObject module = getICObject(moduleClassName);
		String bundle = module.getBundleIdentifier();
		String type = module.getObjectType();
		XMLElement moduleElement = new XMLElement(XMLConstants.MODULE_MODULE);
		moduleElement.addContent(XMLConstants.MODULE_CLASS, moduleClassName);
		moduleElement.addContent(XMLConstants.MODULE_TYPE, type);
		moduleElement.addContent(XMLConstants.MODULE_BUNDLE, bundle);
		necessaryModules.add(moduleElement);
	}
		
	
	public Object write(ObjectWriter writer) throws RemoteException {
		return writer.write(this);
	}
	
	public Object read(ObjectReader reader) throws RemoteException {
		return reader.read(this);
	}

	public XMLData createMetadataSummary() {
		XMLData metadata = XMLData.getInstanceWithoutExistingFileSetNameSetRootName(EXPORT_METADATA_FILE_NAME, EXPORT_METADATA_NAME);
		XMLElement metadataElement = metadata.getDocument().getRootElement();
		XMLElement filesElement = new XMLElement(XMLConstants.FILE_FILES);
		metadataElement.addContent(filesElement);
		Iterator iterator = fileElements.iterator();
		while (iterator.hasNext()) {
			XMLElement fileElement = (XMLElement) iterator.next();
			filesElement.addContent(fileElement);
		}
		XMLElement modulesElement = new XMLElement(XMLConstants.MODULE_MODULES);
		metadataElement.addContent(modulesElement);
		Iterator moduleIterator = necessaryModules.iterator();
		while (moduleIterator.hasNext()) {
			XMLElement moduleElement = (XMLElement) moduleIterator.next();
			modulesElement.addContent(moduleElement);
		}
		if (pagesElement != null) {
			metadataElement.addContent(pagesElement);
		}
		if (templatesElement != null) {
			metadataElement.addContent(templatesElement);
		}
		return metadata;
	}
	
	public void setMetadataSummary(XMLData metadataSummary) {
		this.metadataSummary = metadataSummary;
		initializeFromSummary();
	}
	
	private void initializeFromSummary() {
		XMLElement rootElement = metadataSummary.getDocument().getRootElement();
		XMLElement filesElement = rootElement.getChild(XMLConstants.FILE_FILES);
		fileElements =  (filesElement == null) ? null : filesElement.getChildren();
		XMLElement modulesElement = rootElement.getChild(XMLConstants.MODULE_MODULES);
		necessaryModules = (modulesElement == null) ? null : modulesElement.getChildren();
		templatesElement = rootElement.getChild(XMLConstants.PAGE_TREE_TEMPLATES);
		pagesElement = rootElement.getChild(XMLConstants.PAGE_TREE_PAGES);
		buildPageAndTemplateHierarchy();
	}
			
	private List getPageElementsOrNonPageElements(boolean getPageElements) {
		List elements = new ArrayList();
		Iterator iterator = fileElements.iterator();
		while (iterator.hasNext()) {
			XMLElement fileElement = (XMLElement) iterator.next();
			if (getPageElements == IBExportImportData.getSourceClassForPage().equals(fileElement.getTextTrim(XMLConstants.FILE_SOURCE)))		{
				elements.add(fileElement);
			}
		}
		return elements;
	}
	
	private void buildPageAndTemplateHierarchy() {
		if (templatesElement == null && pagesElement == null) {
			return;
		}
		pageIds = new ArrayList();
		childParent = new HashMap();
		// templates first 
		if (templatesElement != null) {
			buildPageHierarchy(null, templatesElement);
		}
		if (pagesElement != null) {
			buildPageHierarchy(null, pagesElement);
		}
	}

	private void buildPageHierarchy(String parentId, XMLElement pageTreeElement) {
		String currentId = pageTreeElement.getTextTrim(XMLConstants.PAGE_TREE_ID);
		if (currentId != null) {
			pageIds.add(currentId);
			childParent.put(currentId, parentId);
		}
		List children = pageTreeElement.getChildren();
		Iterator iterator = children.iterator();
		while (iterator.hasNext()) {
			XMLElement childItem = (XMLElement) iterator.next();
			buildPageHierarchy(currentId,  childItem);
		}
	}

	private boolean checkCObject(String className) throws IOException {
		try {
			findICObject(className);
			return true;
		}
		catch (FinderException findEx) { 
			return false;
		}
  }
	
	private ICObject getICObject(String className) throws IOException {
		try {
			return findICObject(className);
		}
		catch (FinderException findEx) { 
			throw new IOException("[IBExportImportData] Could not retrieve ICObject " + className);
		}
  }
	
	private ICObject findICObject(String className) throws IOException, FinderException {
		try {
			ICObjectHome home = (ICObjectHome) IDOLookup.getHome(ICObject.class);
			return home.findByClassName(className);
		}
		catch (IDOLookupException lookUp) {
			throw new IOException("[IBExportImportData] Could not look up ICObject home");
		}
	}
	
	
	class PageElementComparator implements Comparator {


	public int compare(Object o1, Object o2) {
		XMLElement element1 = (XMLElement) o1;
		XMLElement element2 = (XMLElement) o2; 
		String id1 = element1.getTextTrim(XMLConstants.VALUE_STRING);
		String id2 = element2.getTextTrim(XMLConstants.VALUE_STRING);
		int index1 = pageIds.indexOf(id1);
		int index2 = pageIds.indexOf(id2);
		return (index1 - index2) > 0 ? 1 : -1; 
	}

}
		
		
		
}
		
