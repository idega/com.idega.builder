/*
 * $Id: IBReferenceEntry.java,v 1.1 2005/10/03 18:31:59 thomas Exp $
 * Created on Sep 27, 2005
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.builder.data;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import javax.ejb.CreateException;
import javax.ejb.FinderException;
import com.idega.builder.business.XMLConstants;
import com.idega.data.IDOEntity;
import com.idega.data.IDOHome;
import com.idega.data.IDOLookup;
import com.idega.data.IDOLookupException;
import com.idega.io.serialization.Storable;
import com.idega.io.serialization.StorableHolder;
import com.idega.io.serialization.StorableProvider;
import com.idega.presentation.IWContext;
import com.idega.repository.data.RefactorClassRegistry;
import com.idega.repository.data.ResourceDescription;
import com.idega.util.reflect.MethodIdentifierCache;
import com.idega.xml.XMLElement;


public class IBReferenceEntry {
	
	private IWContext iwc = null;
	private MethodIdentifierCache methodIdenfierCache = null; 
	
	private String moduleClass = null;
	
	private String parameterId = null;
	private String valueName = null;
	private String sourceClassName = null;
	private String providerClassName = null;
	private boolean isEjb = false;
	
	public IBReferenceEntry(String moduleClass, MethodIdentifierCache methodIdentifierCache, IWContext iwc) {
		this.iwc = iwc;
		this.moduleClass = moduleClass;
		this.methodIdenfierCache = methodIdentifierCache;
	}
	
	public void initialize(String propertyName, XMLElement parameterElement) {
		String tempParameterId = parameterElement.getAttributeValue(XMLConstants.EXPORT_PROPERTY_PARAMETER_ID);
		String tempSourceClassName = parameterElement.getTextTrim(XMLConstants.EXPORT_SOURCE);
		
		XMLElement providerElement = parameterElement.getChild(XMLConstants.EXPORT_PROVIDER);
		boolean tempIsEjb = (new Boolean(providerElement.getTextTrim(XMLConstants.EXPORT_PROVIDER_EJB))).booleanValue();
		String tempProviderClassName = providerElement.getTextTrim(XMLConstants.EXPORT_PROVIDER_CLASS);
		
		initialize(propertyName, tempParameterId, tempSourceClassName, tempProviderClassName, tempIsEjb);
	}
	
	public void initialize(String valueName, String parameterId, ResourceDescription resourceDescription) {
		initialize(valueName, parameterId, resourceDescription.getSource(), resourceDescription.getProvider(), resourceDescription.isEjb());
	}
	
	
	public void initialize(String valueName, String parameterId, String sourceClassName, String providerClassName, boolean isEjb) {
		this.parameterId = parameterId;
		this.valueName = valueName;
		this.sourceClassName = sourceClassName;
		this.providerClassName = providerClassName;
		this.isEjb = isEjb;
	}
	
	public String getModuleClass() {
		return moduleClass;
	}
	
	public String getParameterId() {
		return parameterId;
	}
	
	public void addSource(XMLElement moduleElement, IBExportImportData metadata) throws IOException {
		List properties = moduleElement.getChildren(XMLConstants.PROPERTY_STRING);
		Iterator iterator = properties.iterator();
		while (iterator.hasNext()) {
			XMLElement propertyElement = (XMLElement) iterator.next();
			String tempValueName = propertyElement.getTextTrim(XMLConstants.NAME_STRING);
			tempValueName = methodIdenfierCache.getUpdatedMethodIdentifier(tempValueName);
			if (tempValueName.equals(valueName)) {
				// right propertyElement has been found, now get the right value (properties can have more than one value)
				List valueElements = propertyElement.getChildren(XMLConstants.VALUE_STRING);
				// index starts at zero 
				int index = Integer.parseInt(parameterId);
				XMLElement valueElement = (XMLElement) valueElements.get(--index);
				String value = valueElement.getTextTrim();
				Storable storable = null;
				if (isEjb) {
					storable = getSourceFromPropertyElementUsingEjb(value);
				}
				else {
					storable = getSourceFromPropertyElementUsingProvider(value);
				}
				metadata.addFileEntry(this, storable, value);
				return;
			}
		}
	}
	
	private Storable getSourceFromPropertyElementUsingEjb(String value) throws IOException {
		try {
			Class providerClass = RefactorClassRegistry.forName(providerClassName);
			IDOHome home = IDOLookup.getHome(providerClass);
			return (Storable) home.findByPrimaryKeyIDO(new Integer(value));
		}
		catch (ClassNotFoundException ex) {
			throw new IOException("[IBReference] Provider class ("+providerClassName+") doesn't exist");
		}
		catch (IDOLookupException ex) {
			throw new IOException("[IBReference] Provider class ("+providerClassName+") could not be found (Look up problem)");
		}
		catch (NumberFormatException ex) {
			throw new IOException("[IBReference] Identifier is not a number:" + value + "Provider is: " + providerClassName); 
		}
		catch (FinderException ex) {
			throw new IOException("[IBReference] Instance with identifier" + value + "could not be found. Provider is: " + providerClassName);
		}
	}
	
	private Storable getSourceFromPropertyElementUsingProvider(String value) throws IOException {
		try {
			Class providerClass = RefactorClassRegistry.forName(providerClassName);
			// get an instance
			StorableProvider provider = (StorableProvider) providerClass.newInstance();
			return provider.getSource(value, sourceClassName, iwc);
		}
		catch (ClassCastException e) {
			throw new IOException("[IBReference] Provider class ("+providerClassName+") doesn't implement StorableProvider");
		}
		catch (ClassNotFoundException ex) {
			throw new IOException("[IBReference] Provider class ("+providerClassName+") doesn't exist");
		}
		catch (InstantiationException e) {
			throw new IOException("[IBReference] Provider class ("+providerClassName+") could not be instanciated");
		} 
		catch (IllegalAccessException e) {
			throw new IOException("[IBReference] Provider class ("+providerClassName+") could not be instanciated (illegal access)");
		} 
	}
	
//	use the code below if the simple solution above is not sufficient.
//at the moment the code below seems to be an overkill.
//	private IBStorable getSourceFromPropertyElementUsingEjb(XMLElement propertyElement) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, IDOLookupException, ClassNotFoundException {
//		String value = propertyElement.getTextTrim(XMLConstants.VALUE_STRING);
//		Class providerClass = Class.forName(providerClassName);
//		IDOHome home = IDOLookup.getHome(providerClass);
//		Method method = MethodFinder.getInstance().getMethod(providerMethodName, providerClass);
//		Class[] parameterTypes = method.getParameterTypes();
//		if (parameterTypes[0].equals(Object.class)) {
//			Object[] parameters = { new Integer(value)};
//			return (IBStorable) method.invoke(method, parameters);
//		}
//		Object[] parameters = { value};
//		return (IBStorable) method.invoke(home, parameters);
//	}
		
	public StorableHolder createSource(String value) throws IOException {
		return (isEjb) ? createSourceUsingEjb() : createSourceUsingProvider(value);
	}
	
	private StorableHolder createSourceUsingEjb() throws IOException {
		try {
			Class providerClass = RefactorClassRegistry.forName(providerClassName);
			IDOHome home = IDOLookup.getHome(providerClass);
			IDOEntity entity = home.createIDO();
			entity.store();
			StorableHolder holder = new StorableHolder();
			holder.setStorable((Storable)entity);
			holder.setValue(entity.getPrimaryKey().toString());
			return holder;
		}
		catch (ClassNotFoundException ex) {
			throw new IOException("[IBReference] Provider class doesn't exist");
		}
		catch (IDOLookupException ex) {
			throw new IOException("[IBReference] Provider class could not be found (Look up problem)");
		}
		catch (CreateException ex) {
			throw new IOException("[IBReference] Identifier is not a number");
		}
	}
		
	private StorableHolder createSourceUsingProvider(String value) throws IOException {
		try {
			Class providerClass = RefactorClassRegistry.forName(providerClassName);
			// get an instance
			StorableProvider provider = (StorableProvider) providerClass.newInstance();
			return provider.createSource(value, sourceClassName ,iwc);
		}
		catch (ClassCastException e) {
			throw new IOException("[IBReference] Provider class ("+providerClassName+") doesn't implement StorableProvider");
		}
		catch (ClassNotFoundException ex) {
			throw new IOException("[IBReference] Provider class ("+providerClassName+") doesn't exist");
		}
		catch (InstantiationException e) {
			throw new IOException("[IBReference] Provider class ("+providerClassName+") could not be instanciated");
		} 
		catch (IllegalAccessException e) {
			throw new IOException("[IBReference] Provider class ("+providerClassName+") could not be instanciated (illegal access)");
		} 
	}


		
	
	/**
	 * @return Returns the isEjb.
	 */
	public boolean isEjb() {
		return isEjb;
	}
	/**

	 * @return Returns the providerClass.
	 */
	public String getProviderClass() {
		return providerClassName;
	}
//	/**
//	 * @return Returns the providerMethod.
//	 */
//	public String getProviderMethod() {
//		return providerMethodName;
//	}
	/**
	 * @return Returns the sourceClass.
	 */
	public String getSourceClass() {
		return sourceClassName;
	}
	/**
	 * @return Returns the valueName.
	 */
	public String getValueName() {
		return valueName;
	}

}