package com.idega.builder.data;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.ejb.CreateException;
import javax.ejb.FinderException;

import com.idega.builder.business.XMLConstants;
import com.idega.data.IDOEntity;
import com.idega.data.IDOHome;
import com.idega.data.IDOLookup;
import com.idega.data.IDOLookupException;
import com.idega.io.Storable;
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
	
	private Map nameEntries = null;
	protected String moduleClass = null;

	public IBReference(XMLElement moduleElement) {
		initialize(moduleElement);
	}
	
	public Collection getEntries() {
		return (nameEntries == null) ? null : nameEntries.values();
	}
	
	public IBReference.Entry getReferenceByName(String name) {
		return (IBReference.Entry) ((nameEntries == null) ? null : nameEntries.get(name));
	}
	
	public String getModuleClass() {
		return moduleClass;
	}

	
	private void initialize(XMLElement moduleElement) {
		nameEntries = new HashMap();
		moduleClass = moduleElement.getAttributeValue(XMLConstants.EXPORT_MODULE_CLASS);
		List properties = moduleElement.getChildren(XMLConstants.EXPORT_PROPERTY);
		Iterator propertiesIterator = properties.iterator(); 
		while (propertiesIterator.hasNext()) {
			XMLElement propertyElement = (XMLElement) propertiesIterator.next();
			IBReference.Entry entry = new IBReference.Entry();
			entry.initialize(propertyElement);
			nameEntries.put(entry.getValueName(), entry);
		}
	}
	
	
		
	public class Entry {	
		
		private String valueName = null;
		private String sourceClassName = null;
		private String providerClassName = null;
		//private String providerMethodName = null;
		private boolean isEjb = false;
		
		public void initialize(XMLElement propertyElement) {
			valueName = propertyElement.getTextTrim(XMLConstants.EXPORT_PROPERTY_NAME);
			sourceClassName = propertyElement.getTextTrim(XMLConstants.EXPORT_SOURCE);
			XMLElement providerElement = propertyElement.getChild(XMLConstants.EXPORT_PROVIDER);
			isEjb = (new Boolean(providerElement.getTextTrim(XMLConstants.EXPORT_PROVIDER_EJB))).booleanValue();
			providerClassName = providerElement.getTextTrim(XMLConstants.EXPORT_PROVIDER_CLASS);
			//providerMethodName = providerElement.getTextTrim(XMLConstants.EXPORT_PROVIDER_METHOD);
		}
		
		public String getModuleClass() {
			return moduleClass;
		}
		
		public void addSource(XMLElement moduleElement, IBExportImportData metadata) throws IOException {
			List properties = moduleElement.getChildren(XMLConstants.PROPERTY_STRING);
			Iterator iterator = properties.iterator();
			while (iterator.hasNext()) {
				XMLElement propertyElement = (XMLElement) iterator.next();
				String valueName = propertyElement.getTextTrim(XMLConstants.NAME_STRING);
				if (this.valueName.equalsIgnoreCase(valueName)) {
					// right propertyElement has been found
					if (isEjb) {
						String value = propertyElement.getTextTrim(XMLConstants.VALUE_STRING);
						Storable storable = getSourceFromPropertyElementUsingEjb(value);
						metadata.addFileEntry(this, storable, value);
						return;
					}
					else {
						// not yet implemented
					}
				}
			}
			throw new IOException("[IBReference] Source could not be found");
		}

		private Storable getSourceFromPropertyElementUsingEjb(String value) throws IOException {
			try {
				Class providerClass = Class.forName(providerClassName);
				IDOHome home = IDOLookup.getHome(providerClass);
				return (Storable) home.findByPrimaryKeyIDO(new Integer(value));
			}
			catch (ClassNotFoundException ex) {
				throw new IOException("[IBReference] Provider class doesn't exist");
			}
			catch (IDOLookupException ex) {
				throw new IOException("[IBReference] Provider class could not be found (Look up problem)");
			}
			catch (NumberFormatException ex) {
				throw new IOException("[IBReference] Identifier is not a number");
			}
			catch (FinderException ex) {
				throw new IOException("[IBReference] Instance with identifier" + value + "could not be found");
			}
		}
		
					
//		use the code below if the simple solution above is not sufficient.
//    at the moment the code below seems to be an overkill.
//		private Storable getSourceFromPropertyElementUsingEjb(XMLElement propertyElement) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, IDOLookupException, ClassNotFoundException {
//			String value = propertyElement.getTextTrim(XMLConstants.VALUE_STRING);
//			Class providerClass = Class.forName(providerClassName);
//			IDOHome home = IDOLookup.getHome(providerClass);
//			Method method = MethodFinder.getInstance().getMethod(providerMethodName, providerClass);
//			Class[] parameterTypes = method.getParameterTypes();
//			if (parameterTypes[0].equals(Object.class)) {
//				Object[] parameters = { new Integer(value)};
//				return (Storable) method.invoke(method, parameters);
//			}
//			Object[] parameters = { value};
//			return (Storable) method.invoke(home, parameters);
//		}
			
		public StorableHolder createSource() throws IOException {
			if (isEjb) {
				return createSourceUsingEjb();
			}
			else {
				// not yet implemented
			}
			throw new IOException("[IBReference] Source could not be found");
		}
		
		private StorableHolder createSourceUsingEjb() throws IOException {
			try {
				Class providerClass = Class.forName(providerClassName);
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
//		/**
//		 * @return Returns the providerMethod.
//		 */
//		public String getProviderMethod() {
//			return providerMethodName;
//		}
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
		
}
