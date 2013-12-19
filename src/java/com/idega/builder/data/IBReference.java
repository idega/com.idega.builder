package com.idega.builder.data;

import java.util.Iterator;
import java.util.List;

import com.idega.builder.business.IBXMLConstants;
import com.idega.presentation.IWContext;
import com.idega.util.datastructures.HashMatrix;
import com.idega.util.reflect.MethodIdentifierCache;
import com.idega.xml.XMLElement;

/**
 * <p>Title: idegaWeb</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: idega Software</p>
 * @author <a href="thomas@idega.is">Thomas Hilbig</a>
 * @version 1.0
 * Created on Mar 22, 2004
 */
public class IBReference {

	private IWContext iwc = null;
	private MethodIdentifierCache methodIdentiferCache = null;
	private HashMatrix<String, String, IBReferenceEntry> nameParameterEntries = null;

	protected String moduleClass = null;


	public IBReference(String moduleClassName, MethodIdentifierCache methodIdentfierCache, IWContext iwc) {
		this.iwc = iwc;
		this.methodIdentiferCache = methodIdentfierCache;
		this.moduleClass = moduleClassName;
	}

	public IBReference(XMLElement moduleElement, IWContext iwc) {
		this.iwc = iwc;
		initialize(moduleElement);
	}

	public List<IBReferenceEntry> getEntries() {
		return (this.nameParameterEntries == null) ? null : this.nameParameterEntries.getCopiedListOfValues();
	}

	public IBReferenceEntry getReferenceByName(String name, String parameterId) {
		return ((this.nameParameterEntries == null) ? null : this.nameParameterEntries.get(name, parameterId));
	}

	public String getModuleClass() {
		return this.moduleClass;
	}


	private void initialize(XMLElement moduleElement) {
		this.nameParameterEntries = null;
		this.moduleClass = moduleElement.getAttributeValue(IBXMLConstants.EXPORT_MODULE_CLASS);
		List properties = moduleElement.getChildren(IBXMLConstants.EXPORT_PROPERTY);
		Iterator propertiesIterator = properties.iterator();
		while (propertiesIterator.hasNext()) {
			XMLElement propertyElement = (XMLElement) propertiesIterator.next();
			String propertyName =  propertyElement.getTextTrim(IBXMLConstants.EXPORT_PROPERTY_NAME);
			List parameters = propertyElement.getChildren(IBXMLConstants.EXPORT_PROPERTY_PARAMETER);
			Iterator parameterIterator = parameters.iterator();
			while (parameterIterator.hasNext()) {
				XMLElement parameterElement = (XMLElement) parameterIterator.next();
				IBReferenceEntry entry = new IBReferenceEntry(this.moduleClass, this.methodIdentiferCache, this.iwc);
				entry.initialize(propertyName, parameterElement);
				add(entry);
			}
		}
	}

	public void add(IBReferenceEntry entry) {
		if (this.nameParameterEntries == null) {
			this.nameParameterEntries = new HashMatrix<String, String, IBReferenceEntry>();
		}
		this.nameParameterEntries.put(entry.getValueName(), entry.getParameterId(),entry);
	}




}
