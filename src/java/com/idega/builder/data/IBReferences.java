package com.idega.builder.data;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.ejb.EJBLocalObject;
import com.idega.builder.business.IBXMLConstants;
import com.idega.io.serialization.StorableHolder;
import com.idega.presentation.IWContext;
import com.idega.presentation.Page;
import com.idega.presentation.PresentationObject;
import com.idega.repository.data.NonEJBResource;
import com.idega.repository.data.PropertyDescription;
import com.idega.repository.data.PropertyDescriptionHolder;
import com.idega.repository.data.RefactorClassRegistry;
import com.idega.repository.data.Resource;
import com.idega.repository.data.ResourceDescription;
import com.idega.util.reflect.MethodIdentifierCache;
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
	
	private IWContext iwc = null;
	
	private Map moduleReference = null;
	
	private MethodIdentifierCache methodIdendifierCache = null;
	
	public IBReferences(IWContext iwc)  {
		initialize(iwc);
	}
	
	private void initialize(IWContext iwc)	 {
		this.iwc = iwc;
		this.moduleReference = new HashMap();
		this.methodIdendifierCache = new MethodIdentifierCache();
	}
	
	public StorableHolder createSourceFromElement(XMLElement metaDataFileElement) throws IOException {
		String moduleName = metaDataFileElement.getTextTrim(IBXMLConstants.FILE_MODULE);
		IBReference reference = getReferenceOrNull(moduleName);
		if (reference == null) {
			// shouldn't happen
			return null;
		}
		String parameterId = metaDataFileElement.getTextTrim(IBXMLConstants.FILE_PARAMETER_ID);
		String value = metaDataFileElement.getTextTrim(IBXMLConstants.FILE_VALUE);
		String name = checkAndUpdateName(metaDataFileElement);
		IBReferenceEntry entry = reference.getReferenceByName(name, parameterId);
		return entry.createSource(value);
	}
		
	public String checkAndUpdateName(XMLElement metadataFileElement) {
		String name = metadataFileElement.getTextTrim(IBXMLConstants.FILE_NAME);
		return this.methodIdendifierCache.getUpdatedMethodIdentifier(name);
	}
			
	
	public void checkElementForReferencesNoteNecessaryModules(XMLElement element,IBExportImportData metadata) throws IOException {
		if (element == null) {
			// one child in the list is null
			// very strange, ignore it
			return;
		}
		String nameOfElement = element.getName();
		// is it a module or a page?
		if (IBXMLConstants.MODULE_STRING.equalsIgnoreCase(nameOfElement) || 
				IBXMLConstants.PAGE_STRING.equalsIgnoreCase(nameOfElement)) {
			// ask for the class
			String moduleClassName = element.getAttributeValue(IBXMLConstants.CLASS_STRING);
			// special case: pages aren't modules
			if (moduleClassName == null) {
				moduleClassName = Page.class.getName();
			}
			// mark the module as necessary
			metadata.addNecessaryModule(moduleClassName);
			IBReference reference = getReferenceOrNull(moduleClassName);
			if (reference != null) {
				Collection entries = reference.getEntries();
				Iterator iterator = entries.iterator();
				while (iterator.hasNext()) {
					IBReferenceEntry entry = (IBReferenceEntry) iterator.next();
					entry.addSource(element, metadata);
				}
			}
		}
		List children = element.getChildren();
		if (children != null) {
			Iterator childrenIterator = children.iterator();
			while (childrenIterator.hasNext()) {
				XMLElement childElement = (XMLElement) childrenIterator.next();
				checkElementForReferencesNoteNecessaryModules(childElement, metadata);
			}
		}
	}
	
	private IBReference getReferenceOrNull(String moduleClassName) {
		IBReference reference  = null;
		if (this.moduleReference.containsKey(moduleClassName)) {
			reference = (IBReference) this.moduleReference.get(moduleClassName);
		}
		else {
			// create an IBReference
			reference = createIBReference(moduleClassName);
			// put into map even if it is null
			// if the reference is null the module class has no references at all
			// put null into the map to avoid checking the module class again
			this.moduleReference.put(moduleClassName, reference);
		}
		return reference;
	}
	
	// returns a reference or null
	private IBReference createIBReference(String moduleClassName) {
		Class moduleClass = null;
		try {
			moduleClass = RefactorClassRegistry.forName(moduleClassName);
		}
		catch (ClassNotFoundException ex) {
			return null;
		}
		List entries = new ArrayList();
		List alreadyCheckedMethods = new ArrayList();
		if (PresentationObject.class.isAssignableFrom(moduleClass)) {
			addEntriesDefinedByModule(moduleClassName, moduleClass, entries, alreadyCheckedMethods, this.methodIdendifierCache);
		}

		addEntriesByScanningModuleClass(moduleClassName, moduleClass, entries, alreadyCheckedMethods, this.methodIdendifierCache);
		if (entries.isEmpty()) {
			return null;
		}
		IBReference reference = new IBReference(moduleClassName, this.methodIdendifierCache,this.iwc);
		Iterator iterator = entries.iterator();
		while (iterator.hasNext()) {
			IBReferenceEntry entry = (IBReferenceEntry) iterator.next();
			reference.add(entry);
		}
		return reference;
	}

	private void addEntriesByScanningModuleClass(String moduleClassName, Class moduleClass, List entries, List alreadyCheckedMethods, MethodIdentifierCache methodIdentifierCache) {
		Method[] method = moduleClass.getMethods();
		for (int i = 0; i < method.length; i++) {
			Method tempMethod = method[i];
			String methodName = tempMethod.getName();
			if (methodName.startsWith("set")) {
				Class[] parameterType = tempMethod.getParameterTypes();
				int parameterId = 0;
				while (parameterId < parameterType.length) {
					Class parameterClass = parameterType[parameterId++];
					// increase parameter by one before using it further
					if (Resource.class.isAssignableFrom(parameterClass)) {
						IBReferenceEntry entry = new IBReferenceEntry(moduleClassName, methodIdentifierCache, this.iwc);
						String valueName = methodIdentifierCache.getMethodIdentifierWithoutDeclaringClass(tempMethod);
						if (! alreadyCheckedMethods.contains(valueName)) { 
							String sourceClassName = parameterClass.getName();
							String providerClassName = null;
							boolean isEJB = false;
							if (EJBLocalObject.class.isAssignableFrom(parameterClass)) {
								//IWApplicationContext iwac = iwc.getApplicationContext();
								providerClassName = sourceClassName;
								isEJB = true;
								entry.initialize(valueName,Integer.toString(parameterId), sourceClassName, providerClassName, isEJB);
								entries.add(entry);
							}
							else if (NonEJBResource.class.isAssignableFrom(parameterClass)){
								try {
									NonEJBResource nonEJBResource = (NonEJBResource) parameterClass.newInstance();
									ResourceDescription resourceDescription = nonEJBResource.getResourceDescription();
									entry.initialize(valueName, Integer.toString(parameterId),resourceDescription);
									entries.add(entry);
								}
								catch (InstantiationException ex) {
									// do nothing
								}
								catch (IllegalAccessException ex) {
									// do nothing
								}
							}
 						}
					}
				}
			}
		}
	}

	private void addEntriesDefinedByModule(String moduleClassName, Class moduleClass, List entries, List alreadyCheckedMethods, MethodIdentifierCache methodIdentifierCache) {
		if (! PropertyDescriptionHolder.class.isAssignableFrom(moduleClass)) {
			//nothing to do
			return;
		}
		try {
			PropertyDescriptionHolder module = (PropertyDescriptionHolder) moduleClass.newInstance();
			List descriptions = module.getPropertyDescriptions();
			if (descriptions != null) {
				Iterator iterator = descriptions.iterator();
				while (iterator.hasNext()) {
					PropertyDescription description = (PropertyDescription) iterator.next();
					IBReferenceEntry entry = new IBReferenceEntry(moduleClassName, methodIdentifierCache, this.iwc);
					String methodIdentifier = description.getName();
					methodIdentifier = this.methodIdendifierCache.getUpdatedMethodIdentifier(methodIdentifier);
					alreadyCheckedMethods.add(methodIdentifier);
					entry.initialize(methodIdentifier,description.getParameterId(), description.getResourceDescription());
					entries.add(entry);
				}
			}
		}
		catch (InstantiationException ex) {
			// do nothing
		}
		catch (IllegalAccessException ex) {
			// do nothing
		}
	}

}
