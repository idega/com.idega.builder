package com.idega.builder.data;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.idega.builder.business.XMLConstants;
import com.idega.core.builder.data.ICPage;
import com.idega.io.ObjectWriter;
import com.idega.io.Storable;
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
public class IBExportMetadata implements Storable {
	
	public static final String EXPORT_METADATA = "metadata";
	
	private List fileElements = new ArrayList();

	public IBExportMetadata() {
		initialize();
	}
	
	private void initialize() {
		
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
		

	
	
	public void addFileEntry(IBReference.Entry entry, String value) {
		XMLElement fileElement = new XMLElement(XMLConstants.FILE);
		fileElement.addContent(XMLConstants.FILE_SOURCE, entry.getSourceClass());
		fileElement.addContent(XMLConstants.FILE_NAME, entry.getValueName());
		fileElement.addContent(XMLConstants.FILE_VALUE, value);
		fileElements.add(fileElement);
	}
	
	public void addFileEntry(ICPage page) {
		XMLElement fileElement = new XMLElement(XMLConstants.FILE);
		fileElement.addContent(XMLConstants.FILE_SOURCE, ICPage.class.getName());
		fileElement.addContent(XMLConstants.FILE_NAME, page.getIDColumnName());
		fileElement.addContent(XMLConstants.FILE_VALUE, page.getPrimaryKey().toString());
		fileElements.add(fileElement);
	}
	
	public Object write(ObjectWriter writer) throws RemoteException {
		XMLData metadata = createXMLData();
		return writer.write(metadata);
	}

	private XMLData createXMLData() {
		XMLData metadata = XMLData.getInstanceWithoutExistingFileSetNameSetRootName(EXPORT_METADATA, EXPORT_METADATA);
		XMLElement metadataElement = metadata.getDocument().getRootElement();
		Iterator iterator = fileElements.iterator();
		while (iterator.hasNext()) {
			XMLElement fileElement = (XMLElement) iterator.next();
			metadataElement.addContent(fileElement);
		}
		return metadata;
	}

}


