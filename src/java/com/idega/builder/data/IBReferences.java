package com.idega.builder.data;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.idega.builder.app.IBApplication;
import com.idega.builder.business.XMLConstants;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWMainApplication;
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
public class IBReferences {
	
	public static final String EXPORT_DEFINITION = "exportdefinition.xml";
	
	private Map moduleReference = null;
	
	public IBReferences(IWMainApplication iwac) throws IOException  {
		initialize(iwac);
	}
	
	private void initialize(IWMainApplication iwac) throws IOException {
		moduleReference = new HashMap();
		IWBundle bundle = IWBundle.getBundle(IBApplication.IB_BUNDLE_IDENTIFIER, iwac);
		String exportDefinitionPath = bundle.getRealPathWithFileNameString(EXPORT_DEFINITION);
		XMLData exportDefinition = XMLData.getInstanceForFile(exportDefinitionPath);
		XMLElement root = exportDefinition.getDocument().getRootElement();
		List children = root.getChildren(XMLConstants.EXPORT_MODULE);
		Iterator iterator = children.iterator();
		while (iterator.hasNext()) {
			XMLElement module = (XMLElement) iterator.next();
			IBReference reference = new IBReference(module);
			moduleReference.put(reference.getModuleClass(), reference);
		}
	}

	
	public void checkElementForReferences(XMLElement element,IBExportImportData metadata) throws IOException {
		String nameOfElement = element.getName();
		// is it a module?
		if (XMLConstants.MODULE_STRING.equalsIgnoreCase(nameOfElement)) {
			// ask for the class
			String moduleClass = element.getAttributeValue(XMLConstants.CLASS_STRING);
			if (moduleReference.containsKey(moduleClass)) {
				IBReference reference = (IBReference) moduleReference.get(moduleClass);
				List entries = reference.getEntries();
				Iterator iterator = entries.iterator();
				while (iterator.hasNext()) {
					IBReference.Entry entry = (IBReference.Entry) iterator.next();
					entry.addSource(element, metadata);
				}
			}
		}
		List children = element.getChildren();
		Iterator childrenIterator = children.iterator();
		while (childrenIterator.hasNext()) {
			XMLElement childElement = (XMLElement) childrenIterator.next();
			checkElementForReferences(childElement, metadata);
		}
	}
	

}
