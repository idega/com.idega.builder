package com.idega.builder.data;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.ejb.FinderException;

import com.idega.builder.business.IBPageHelper;
import com.idega.builder.business.PageTreeNode;
import com.idega.builder.business.XMLConstants;
import com.idega.core.builder.data.ICPage;
import com.idega.data.IDOLookupException;
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
	
	public static final String EXPORT_METADATA_NAME = "metadata";
	public static final String EXPORT_METADATA_FILE_NAME = EXPORT_METADATA_NAME + ".xml";
	
	private List files = new ArrayList();
	private List fileElements = new ArrayList();
	private XMLElement pagesElement = null; 
	private XMLElement templatesElement = null;

	public String getName() {
		return EXPORT_METADATA_NAME;
	}
	
	public List getData() {
		return files;
	}
	
	public String getSourceClassNameForElement(int index) {
		XMLElement fileElement = (XMLElement) fileElements.get(index);
		return fileElement.getTextTrim(XMLConstants.FILE_SOURCE);
	}
	
	public void modifyElementSetNameSetOriginalName(int index, String name, String originalName) {
		XMLElement fileElement = (XMLElement) fileElements.get(index);
		fileElement.addContent(XMLConstants.FILE_USED_ID,name);
		fileElement.addContent(XMLConstants.FILE_ORIGINAL_NAME, originalName);
	}
	
	public void modifyElementSetNameSetOriginalNameLikeElementAt(int index, int existingDataIndex) {
		XMLElement fileElement = (XMLElement) fileElements.get(existingDataIndex);
		String name = fileElement.getTextTrim(XMLConstants.FILE_USED_ID);
		String originalName = fileElement.getTextTrim(XMLConstants.FILE_ORIGINAL_NAME);
		modifyElementSetNameSetOriginalName(index, name, originalName);
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
		XMLElement fileElement = new XMLElement(XMLConstants.FILE);
		fileElement.addContent(XMLConstants.FILE_SOURCE, entry.getSourceClass());
		fileElement.addContent(XMLConstants.FILE_NAME, entry.getValueName());
		fileElement.addContent(XMLConstants.FILE_VALUE, value);
		fileElements.add(fileElement);
	}
	
	public void addFileEntry(ICPage page) {
		files.add(page);
		XMLElement fileElement = new XMLElement(XMLConstants.FILE);
		fileElement.addContent(XMLConstants.FILE_SOURCE, ICPage.class.getName());
		fileElement.addContent(XMLConstants.FILE_NAME, page.getIDColumnName());
		fileElement.addContent(XMLConstants.FILE_VALUE, page.getPrimaryKey().toString());
		fileElements.add(fileElement);
	}
	
	public Object write(ObjectWriter writer) throws RemoteException {
		return writer.write(this);
	}

	public XMLData createMetadataSummary() {
		XMLData metadata = XMLData.getInstanceWithoutExistingFileSetNameSetRootName(EXPORT_METADATA_FILE_NAME, EXPORT_METADATA_NAME);
		XMLElement metadataElement = metadata.getDocument().getRootElement();
		Iterator iterator = fileElements.iterator();
		while (iterator.hasNext()) {
			XMLElement fileElement = (XMLElement) iterator.next();
			metadataElement.addContent(fileElement);
		}
		metadataElement.addContent(pagesElement);
		metadataElement.addContent(templatesElement);
		return metadata;
	}

}


