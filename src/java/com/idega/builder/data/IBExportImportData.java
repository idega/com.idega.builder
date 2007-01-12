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
import com.idega.builder.io.ObjectReaderBuilder;
import com.idega.builder.io.ObjectWriterBuilder;
import com.idega.core.builder.data.ICPage;
import com.idega.core.component.data.ICObject;
import com.idega.core.component.data.ICObjectHome;
import com.idega.data.IDOLookup;
import com.idega.data.IDOLookupException;
import com.idega.io.serialization.ObjectReader;
import com.idega.io.serialization.ObjectWriter;
import com.idega.io.serialization.Storable;
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
	
	private static String PAGE_PRIMARY_KEY = "page_primary_key";
	private static String PAGE_PARAMETER_ID = "1";
	
	private List files = new ArrayList();
	private List fileElements = new ArrayList(); 
	private List necessaryModules = new ArrayList();
	private List missingModules = null;
	
	private XMLElement pagesElement = null; 
	private XMLElement templatesElement = null;
	private XMLData metadataSummary = null;
	
	private Map childParent = null;
	protected List pageIds = null;
	private int pageStartIndex = -1; 
	
	private final String sourceClassForPage = ICPage.class.getName();
	
	public String getName() {
		return EXPORT_NAME;
	}
	
	public List getData() {
		return this.files;
	}
	
	public List getPageData() {
		int index = 0; 
		List list = new ArrayList();
		Iterator iterator = this.fileElements.iterator();
		while (iterator.hasNext()) {
			XMLElement element = (XMLElement) iterator.next();
			String sourceClass = element.getTextTrim(XMLConstants.FILE_SOURCE);
			if (this.sourceClassForPage.equals(sourceClass)) {
				Storable page = (Storable) this.files.get(index);
				list.add(page);
			}
			index++;
		}
		return list;
	}
			
	public boolean isTemplate(String id) {
		if (this.pageIds == null) {
			return false;
		}
		return (this.pageIds.indexOf(id) < this.pageStartIndex);
	}
	
	public String getParentIdForPageId(String id) {
		return (String) ((this.childParent == null) ? null : this.childParent.get(id));
	}
	
	public List getNonPageFileElements() {
		return getPageElementsOrNonPageElements(false);
	}
	
	public List getMissingModules() {
		return this.missingModules;
	}
	
	public boolean isValid() {
		return this.missingModules == null;
	}
	
	/** returns templates first */
	public List getSortedPageElements() {
		List pageElements = getPageElementsOrNonPageElements(true);
		Collections.sort(pageElements, new PageElementComparator());
		return pageElements;
	}
	
		
	
	public XMLElement modifyElementSetNameSetOriginalName(int index, String name, String originalName, String mimeType, boolean fileIsMarkedAsDeleted) {
		return modifyElementSetNameSetOriginalName(index, name, originalName, mimeType, Boolean.toString(fileIsMarkedAsDeleted));
	}
		
		
    public XMLElement modifyElementSetNameSetOriginalName(int index, String name, String originalName, String mimeType, String fileIsMarkedAsDeleted) {
		XMLElement fileElement = (XMLElement) this.fileElements.get(index);
		fileElement.addContent(XMLConstants.FILE_USED_ID,name);
		fileElement.addContent(XMLConstants.FILE_ORIGINAL_NAME, originalName);
		fileElement.addContent(XMLConstants.FILE_MIME_TYPE, mimeType);
		fileElement.addContent(XMLConstants.FILE_IS_MARKED_AS_DELETED, fileIsMarkedAsDeleted);
		return fileElement;
	}
	
	public XMLElement modifyElementSetNameSetOriginalNameLikeElementAt(int index, XMLElement fileElement) {
		String name = fileElement.getTextTrim(XMLConstants.FILE_USED_ID);
		String originalName = fileElement.getTextTrim(XMLConstants.FILE_ORIGINAL_NAME);
		String mimeType = fileElement.getTextTrim(XMLConstants.FILE_MIME_TYPE);
		String fileIsMarkedAsDeleted = fileElement.getTextTrim(XMLConstants.FILE_IS_MARKED_AS_DELETED);
		return modifyElementSetNameSetOriginalName(index, name, originalName, mimeType, fileIsMarkedAsDeleted);
	}
		
	public void addPageTree(IWContext iwc) throws IDOLookupException, FinderException {
		List pageTreeNodes = IBPageHelper.getInstance().getFirstLevelPageTreeNodesDomainFirst(iwc);
		this.pagesElement = new XMLElement(XMLConstants.PAGE_TREE_PAGES);
		addPages(pageTreeNodes.iterator(), this.pagesElement);
	}
	
	public void addTemplateTree(IWContext iwc) throws IDOLookupException, FinderException {
		List pageTreeNodes = IBPageHelper.getInstance().getFirstLevelPageTreeNodesTemplateDomainFirst(iwc);
		this.templatesElement = new XMLElement(XMLConstants.PAGE_TREE_TEMPLATES);
		addPages(pageTreeNodes.iterator(), this.templatesElement);
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
			Iterator iterator = node.getChildrenIterator();
			addPages(iterator, pageElement);
			element.addContent(pageElement);
		}
	}
	
	public void addFileEntry(IBReferenceEntry entry, Storable storable, String value) {
		this.files.add(storable);
		XMLElement fileElement = new XMLElement(XMLConstants.FILE_FILE);
		fileElement.addContent(XMLConstants.FILE_MODULE, entry.getModuleClass());
		fileElement.addContent(XMLConstants.FILE_NAME, entry.getValueName());
		fileElement.addContent(XMLConstants.FILE_PARAMETER_ID, entry.getParameterId());
		fileElement.addContent(XMLConstants.FILE_SOURCE, entry.getSourceClass());
		fileElement.addContent(XMLConstants.FILE_VALUE, value);
		this.fileElements.add(fileElement);
	}
	
	public void addFileEntry(ICPage page) {
		this.files.add(page);
		XMLElement fileElement = new XMLElement(XMLConstants.FILE_FILE);
		fileElement.addContent(XMLConstants.FILE_MODULE, this.sourceClassForPage);
		fileElement.addContent(XMLConstants.FILE_NAME, PAGE_PRIMARY_KEY);
		fileElement.addContent(XMLConstants.FILE_PARAMETER_ID, PAGE_PARAMETER_ID);
		fileElement.addContent(XMLConstants.FILE_SOURCE, this.sourceClassForPage);
		fileElement.addContent(XMLConstants.FILE_VALUE, page.getPrimaryKey().toString());
		this.fileElements.add(fileElement);
	}
	
	public void addNecessaryModule(String moduleClassName) throws IOException {
		// ignore special case page, it is not a module
		if (ICPage.class.getName().equals(moduleClassName)) {
			return;
		}
		// the list isn't really large
		Iterator iterator = this.necessaryModules.iterator();
		while (iterator.hasNext()) {
			XMLElement element = (XMLElement) iterator.next();
			String className = element.getTextTrim(XMLConstants.MODULE_CLASS);
			if (className.equals(moduleClassName)) {
				// do not add the same module twice
				return; 
			}
		}
		try {
			ICObject module = getICObject(moduleClassName);
			String bundle = module.getBundleIdentifier();
			String type = module.getObjectType();
			XMLElement moduleElement = new XMLElement(XMLConstants.MODULE_MODULE);
			moduleElement.addContent(XMLConstants.MODULE_CLASS, moduleClassName);
			moduleElement.addContent(XMLConstants.MODULE_TYPE, type);
			moduleElement.addContent(XMLConstants.MODULE_BUNDLE, bundle);
			this.necessaryModules.add(moduleElement);
		}
		catch (IOException ex) {
			// ignore it, some modules do not have an entry in ic object table
		}
	}	
	
	public Object write(ObjectWriter writer, IWContext iwc) throws RemoteException {
		try {
			// try to use the extended interface assumming that the writer is an ObjectWriterBuilder
			return ((ObjectWriterBuilder) writer).write(this, iwc);
		}
		catch (ClassCastException ex) {
			// this file can not be written by a normal object reader
			// do nothing
			return null;
		}
	}
	
	public Object read(ObjectReader reader, IWContext iwc) throws RemoteException {
		try {
			// try to use the extended interface assumming that the reader is an ObjectReaderBuilder
			return ((ObjectReaderBuilder) reader).read(this, iwc);
		}
		catch (ClassCastException ex) {
			// this file can not be read by a normal object reader
			// do nothing
			return null;
		}
	}
	
	public XMLData createMetadataSummary() {
		XMLData metadata = XMLData.getInstanceWithoutExistingFileSetNameSetRootName(EXPORT_METADATA_FILE_NAME, EXPORT_METADATA_NAME);
		XMLElement metadataElement = metadata.getDocument().getRootElement();
		XMLElement filesElement = new XMLElement(XMLConstants.FILE_FILES);
		metadataElement.addContent(filesElement);
		Iterator iterator = this.fileElements.iterator();
		while (iterator.hasNext()) {
			XMLElement fileElement = (XMLElement) iterator.next();
			filesElement.addContent(fileElement);
		}
		XMLElement modulesElement = new XMLElement(XMLConstants.MODULE_MODULES);
		metadataElement.addContent(modulesElement);
		Iterator moduleIterator = this.necessaryModules.iterator();
		while (moduleIterator.hasNext()) {
			XMLElement moduleElement = (XMLElement) moduleIterator.next();
			modulesElement.addContent(moduleElement);
		}
		if (this.pagesElement != null) {
			metadataElement.addContent(this.pagesElement);
		}
		if (this.templatesElement != null) {
			metadataElement.addContent(this.templatesElement);
		}
		return metadata;
	}
	
	public void setMetadataSummary(XMLData metadataSummary) throws IOException {
		this.metadataSummary = metadataSummary;
		initializeFromSummary();
	}
	
	private void initializeFromSummary() throws IOException {
		XMLElement rootElement = this.metadataSummary.getDocument().getRootElement();
		XMLElement filesElement = rootElement.getChild(XMLConstants.FILE_FILES);
		this.fileElements =  (filesElement == null) ? null : filesElement.getChildren();
		XMLElement modulesElement = rootElement.getChild(XMLConstants.MODULE_MODULES);
		this.necessaryModules = (modulesElement == null) ? null : modulesElement.getChildren();
		this.missingModules = validateAndGetMissingModuleNames();
		this.templatesElement = rootElement.getChild(XMLConstants.PAGE_TREE_TEMPLATES);
		this.pagesElement = rootElement.getChild(XMLConstants.PAGE_TREE_PAGES);
		buildPageAndTemplateHierarchy();
	}
			
	private List getPageElementsOrNonPageElements(boolean getPageElements) {
		List elements = new ArrayList();
		Iterator iterator = this.fileElements.iterator();
		while (iterator.hasNext()) {
			XMLElement fileElement = (XMLElement) iterator.next();
			// only get file elements that represent a page
			boolean isPageElement =	
					this.sourceClassForPage.equals(fileElement.getTextTrim(XMLConstants.FILE_MODULE)) &&
					PAGE_PRIMARY_KEY.equals(fileElement.getTextTrim(XMLConstants.FILE_NAME));
			if ((getPageElements && isPageElement) || (! getPageElements && ! isPageElement)) {
				elements.add(fileElement);
			}
		}
		return elements;
	}
	
	private void buildPageAndTemplateHierarchy() {
		if (this.templatesElement == null && this.pagesElement == null) {
			return;
		}
		this.pageIds = new ArrayList();
		this.childParent = new HashMap();
		// templates first 
		if (this.templatesElement != null) {
			buildPageHierarchy(null, this.templatesElement);
		}
		this.pageStartIndex = this.pageIds.size(); 
		if (this.pagesElement != null) {
			buildPageHierarchy(null, this.pagesElement);
		}
	}

	private void buildPageHierarchy(String parentId, XMLElement pageTreeElement) {
		String currentId = pageTreeElement.getTextTrim(XMLConstants.PAGE_TREE_ID);
		if (currentId != null) {
			this.pageIds.add(currentId);
			this.childParent.put(currentId, parentId);
		}
		List children = pageTreeElement.getChildren();
		Iterator iterator = children.iterator();
		while (iterator.hasNext()) {
			XMLElement childItem = (XMLElement) iterator.next();
			buildPageHierarchy(currentId,  childItem);
		}
	}

	private boolean checkICObject(String className) throws IOException {
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
	
	private List validateAndGetMissingModuleNames() throws IOException {
		if (this.necessaryModules == null) {
			return null;
		}
		List tempMissingModules = null;
		Iterator iterator = this.necessaryModules.iterator();
		while (iterator.hasNext()) {
			XMLElement moduleElement = (XMLElement) iterator.next();
			String className = moduleElement.getTextTrim(XMLConstants.MODULE_CLASS);
			if (! checkICObject(className)) {
				String bundleName = moduleElement.getTextTrim(XMLConstants.MODULE_BUNDLE);
				StringBuffer buffer = new StringBuffer(className);
				buffer.append(" ( ").append(bundleName).append(" ) ");
				if (tempMissingModules == null) {
					tempMissingModules = new ArrayList();
				}
				tempMissingModules.add(buffer.toString());
			}
		}
		return tempMissingModules;
	}

	class PageElementComparator implements Comparator {


	public int compare(Object o1, Object o2) {
		XMLElement element1 = (XMLElement) o1;
		XMLElement element2 = (XMLElement) o2; 
		String id1 = element1.getTextTrim(XMLConstants.VALUE_STRING);
		String id2 = element2.getTextTrim(XMLConstants.VALUE_STRING);
		int index1 = IBExportImportData.this.pageIds.indexOf(id1);
		int index2 = IBExportImportData.this.pageIds.indexOf(id2);
		return (index1 - index2) > 0 ? 1 : -1; 
	}

}


		
		
		
}
		
