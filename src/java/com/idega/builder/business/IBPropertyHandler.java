/*
 * $Id: IBPropertyHandler.java,v 1.41 2004/06/15 15:22:27 thomas Exp $
 *
 * Copyright (C) 2001 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package com.idega.builder.business;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import com.idega.builder.handler.DropDownMenuSpecifiedChoiceHandler;
import com.idega.builder.handler.PropertyHandler;
import com.idega.builder.handler.SpecifiedChoiceProvider;
import com.idega.builder.handler.TableColumnsHandler;
import com.idega.builder.handler.TableRowsHandler;
import com.idega.builder.presentation.TableRowColumnPropertyPresentation;
import com.idega.core.builder.business.BuilderClassesFactory;
import com.idega.core.builder.business.BuilderFileChooser;
import com.idega.core.builder.business.BuilderImageInserter;
import com.idega.core.builder.data.ICPage;
import com.idega.core.component.business.ICObjectBusiness;
import com.idega.core.component.data.ICObject;
import com.idega.core.component.data.ICObjectInstance;
import com.idega.core.file.data.ICFile;
import com.idega.data.EntityFinder;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWMainApplication;
import com.idega.idegaweb.IWProperty;
import com.idega.idegaweb.IWPropertyList;
import com.idega.presentation.IWContext;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.ui.BooleanInput;
import com.idega.presentation.ui.CheckBox;
import com.idega.presentation.ui.DropdownMenu;
import com.idega.presentation.ui.FloatInput;
import com.idega.presentation.ui.IntegerInput;
import com.idega.presentation.ui.TextInput;
import com.idega.repository.data.ImplementorRepository;
import com.idega.util.caching.Cache;
import com.idega.util.reflect.MethodFinder;
/**
 * @author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0 beta
 */
public class IBPropertyHandler {
	public final static String METHOD_PROPERTY_IDENTIFIER = "iw_method_identifier";
	public final static String METHOD_PROPERTY_DESCRIPTION = "iw_method_description";
	public static final String METHOD_PROPERTY_ALLOW_MULTIVALUED = "iw_method_option_multiv";
	public static final String PAGE_CHOOSER_NAME = "ib_page_chooser";
	public static final String TEMPLATE_CHOOSER_NAME = "ib_template_chooser";
	public static final String FILE_CHOOSER_NAME = "ic_file_chooser";
	public static final String METHODS_KEY = "iw_component_methods";
	public static final String METHOD_PARAMETERS_KEY = "iw_method_params";
	public static final String METHOD_PARAMETER_PROPERTY_DESCRIPTION = "iw_method_param_desc";
	public static final String METHOD_PARAMETER_PROPERTY_HANDLER_CLASS = "iw_method_param_handler";
	public static final String METHOD_PARAMETER_PROPERTY_PRIMARY_KEY = "iw_method_param_prim_key";
	private static final String TABLE_ROWS_PROPERTY = ":method:1:implied:void:setRows:int:";
	private static final String TABLE_COLUMNS_PROPERTY = ":method:1:implied:void:setColumns:int:";
	private static final String PROPERTYWINDOW_VALUE_FIND = "iw_propw_val_find";
	private static IBPropertyHandler instance;
	private Map propertyHandlers;
	private BuilderClassesFactory builderClassesFactory;

	private IBPropertyHandler() {
	}

	public static IBPropertyHandler getInstance() {
		if (instance == null) {
			instance = new IBPropertyHandler();
		}
		return instance;
	}

	public void removeMethod(IWBundle iwb, String componentKey, String methodIdentifier) {
		IWPropertyList methods = getMethods(iwb, componentKey);
		if (methods != null) {
			methods.removeProperty(methodIdentifier);
		}
	}

	public void setMethod(IWBundle iwb, String componentKey, String methodIdentifier, String methodDescription, Map options) {
		IWPropertyList methods = getMethods(iwb, componentKey);
		if (methods != null) {
			IWProperty method = methods.getIWProperty(methodIdentifier);
			if (method != null) {
				methods.removeProperty(methodIdentifier);
			}
			IWPropertyList methodprop = methods.getNewPropertyList(methodIdentifier);
			methodprop.setProperty(METHOD_PROPERTY_IDENTIFIER, methodIdentifier);
			methodprop.setProperty(METHOD_PROPERTY_DESCRIPTION, methodDescription);
			methodprop.setProperties(options);
		}
	}

	/**	
	 * Returns the IWProperty standing for the Method in the list of
	 * registered properties for the component.
	 *
	 * @return
	 */
	public IWProperty getMethodProperty(int ic_object_instance_id, String methodPropertyKey, IWMainApplication iwma) throws Exception {
		IWPropertyList list = getMethods(ic_object_instance_id, iwma);
		if (list != null) {
			return list.getIWProperty(methodPropertyKey);
		}
		return null;
	}

	public IWPropertyList getMethods(int ic_object_instance_id, IWMainApplication iwma) throws Exception {
		String componentKey = null;
		IWBundle iwb = null;
		//Hardcoded -1 for the top page
		if (ic_object_instance_id == -1) {
			componentKey = "com.idega.presentation.Page";
			iwb = iwma.getBundle(com.idega.presentation.Page.IW_BUNDLE_IDENTIFIER);
		}
		else {
			ICObjectInstance icoi = ((com.idega.core.component.data.ICObjectInstanceHome) com.idega.data.IDOLookup.getHomeLegacy(ICObjectInstance.class)).findByPrimaryKeyLegacy(ic_object_instance_id);
			ICObject obj = icoi.getObject();
			iwb = obj.getBundle(iwma);
			componentKey = obj.getClassName();
		}
		return getMethods(iwb, componentKey);
	}

	public IWPropertyList getMethods(IWBundle iwb, String componentKey) {
		//IWPropertyList compList = iwb.getComponentList();
		//IWPropertyList componentProperties = compList.getPropertyList(componentKey);
		IWPropertyList componentProperties = iwb.getComponentPropertyList(componentKey);
		if (componentProperties != null) {
			IWPropertyList methodList = componentProperties.getPropertyList(METHODS_KEY);
			if (methodList == null) {
				methodList = componentProperties.getNewPropertyList(METHODS_KEY);
			}
			return (methodList);
		}
		return null;
	}

	public int getColumnCountForTable(IWContext iwc, String ICObjectInstanceID) {
		String pageKey = BuilderLogic.getInstance().getCurrentIBPage(iwc);
		String theReturn = BuilderLogic.getInstance().getProperty(pageKey, Integer.parseInt(ICObjectInstanceID), TABLE_COLUMNS_PROPERTY);
		if (theReturn != null) {
			try {
				return Integer.parseInt(theReturn);
			}
			catch (Exception e) {
			}
		}
		return 1;
	}

	/**	
	 * @return true if the Method Parameter property is a Primary Key
	 */
	boolean isMethodParameterPrimaryKey(IWMainApplication iwma, int ICObjectInstanceId, String methodIdentifier, int parameterIndex) {
		try {
			IWProperty methodProperty = this.getMethodProperty(ICObjectInstanceId, methodIdentifier, iwma);
			String sValue = getMethodParameterProperty(methodProperty, parameterIndex, METHOD_PARAMETER_PROPERTY_PRIMARY_KEY);
			if (sValue != null) {
				if (sValue.equalsIgnoreCase("true")) {
					return true;
				}
				else if (sValue.equalsIgnoreCase("false")) {
					return false;
				}
				else if (sValue.equalsIgnoreCase("y")) {
					return true;
				}
				else if (sValue.equalsIgnoreCase("n")) {
					return false;
				}
			}
		}
		catch (Exception e) {
		}

		return false;
	}

	/**	
	 * Returns the real properties set for the property if the property is set with the specified keys
	 * Returns the selectedValues[] if nothing found	
	 */
	public String[] getPropertyValues(IWMainApplication iwma, IBXMLPage xml, int ICObjectInstanceId, String methodIdentifier, String[] selectedValues, boolean returnSelectedValueIfNothingFound) {
		//if(selectedValues!=null){
		List availableValues = XMLWriter.getPropertyValues(xml, ICObjectInstanceId, methodIdentifier);
		if (selectedValues != null) {
			for (int i = 0; i < selectedValues.length; i++) {
				String selectedValue = selectedValues[i];
				boolean isPrimaryKey = isMethodParameterPrimaryKey(iwma, ICObjectInstanceId, methodIdentifier, i);
				if (isPrimaryKey) {
					Iterator iter = availableValues.iterator();
					while (iter.hasNext()) {
						String[] item = (String[]) iter.next();
						if (item[i].equals(selectedValue)) {
							//keep in list
						}
						else {
							//throw out of list
							iter.remove();
						}
					}
				}
			}
		}
		if (availableValues.size() > 0)
			return (String[]) availableValues.get(0);
		if (returnSelectedValueIfNothingFound) {
			return selectedValues;
		}
		else {
			return null;
		}
	}
	
	public int getRowCountForTable(IWContext iwc, String ICObjectInstanceID) {
		String pageKey = BuilderLogic.getInstance().getCurrentIBPage(iwc);
		String theReturn = BuilderLogic.getInstance().getProperty(pageKey, Integer.parseInt(ICObjectInstanceID), TABLE_ROWS_PROPERTY);
		if (theReturn != null) {
			try {
				return Integer.parseInt(theReturn);
			}
			catch (Exception e) {
			}
		}
		return 1;
	}
  
  
  /** Returns a proberty value.
   * This method is used (or can be used) by presentation objects that
   * implements the SpecifiedChoiceProvider interface. The presentation object
   * asks for its proberty value in order to decide which values it should
   * deliver for the drop down menue of the DropDownMenuSpecifiedChoiceHandler.
  */
 public String getPropertyValue(IWContext iwc, String ICObjectInstanceID, String methodIdentifier) {
    String pageKey = BuilderLogic.getInstance().getCurrentIBPage(iwc);
    String theReturn = BuilderLogic.getInstance().getProperty(pageKey, Integer.parseInt(ICObjectInstanceID), methodIdentifier);
    return theReturn;
  }
  
	/**
	
	 * Returns a property of a Method Parameter, Returns null if nothing set
	
	 */
	public String getMethodParameterProperty(IWContext iwc, String ICObjectInstanceID, String methodIdentifier, int parameterIndex, String paramKey) {
		try {
			IWBundle iwb = ICObjectBusiness.getInstance().getBundleForInstance(ICObjectInstanceID, iwc.getIWMainApplication());
			Class objectClass = ICObjectBusiness.getInstance().getClassForInstance(ICObjectInstanceID);
			//IWPropertyList component = iwb.getComponentList().getIWPropertyList(objectClass.getName());
			IWPropertyList component = iwb.getComponentPropertyList(objectClass.getName());
			IWPropertyList methodList = component.getIWPropertyList(METHODS_KEY);
			IWPropertyList method = methodList.getIWPropertyList(methodIdentifier);
			IWPropertyList parameterOptions = method.getPropertyList(METHOD_PARAMETERS_KEY);
			IWPropertyList parameter = parameterOptions.getIWPropertyList(Integer.toString(parameterIndex));
			return (parameter.getProperty(paramKey));
		}
		catch (Exception e) {
			return (null);
		}
	}
	/**
	
	 *
	
	 */
	public PresentationObject getHandlerInstance(IWContext iwc, String ICObjectInstanceID, String methodIdentifier, int parameterIndex, String name, String stringValue) throws Exception {
		String handlerClass = getMethodParameterProperty(iwc, ICObjectInstanceID, methodIdentifier, parameterIndex, METHOD_PARAMETER_PROPERTY_HANDLER_CLASS);
		if (handlerClass.equals("")) {
			return (null);
		}
		PropertyHandler handler = getPropertyHandler(handlerClass);
		PresentationObject handlerPresentation = handler.getHandlerObject(name, stringValue, iwc);

    /* 
     * special treatment for a drop down menu that gets the choice 
     * (that is the elements of the menu)
     * directly from the presentation object 
     * (this presentation object must implement SpecifiedChoiceProvider).
     * see also method getPropertyValue(IWContext, ICObjectInstanceID,
     * String) of this class.
     *  
     */ 
    if (handler instanceof DropDownMenuSpecifiedChoiceHandler)  {
      // get the presentation object
      Class aClass = BuilderLogic.getInstance().getObjectClass(Integer.parseInt(ICObjectInstanceID));
      Collection menuElements;
      if ((SpecifiedChoiceProvider.class).isAssignableFrom(aClass))  {
        try {
          SpecifiedChoiceProvider provider = (SpecifiedChoiceProvider) aClass.newInstance();
          // ask the presentation object for the menu elements
          menuElements = provider.getSpecifiedChoice(iwc,ICObjectInstanceID, methodIdentifier, this);
        }
        catch (Exception e) {
          System.err.println("[IBPropertyHandler] Presentation class can not be created. Message: "+
            e.getMessage());
          e.printStackTrace(System.err);
          menuElements = new ArrayList();
        }
      Iterator iterator = menuElements.iterator();
      while (iterator.hasNext())
        ((DropdownMenu) handlerPresentation).addMenuElement((String) iterator.next());
      }
    }
    
		/*
		
		 * Special treatment for tables
		
		 */
		if (handler instanceof TableRowsHandler) {
			int numberOfRows = getRowCountForTable(iwc, ICObjectInstanceID);
			((TableRowColumnPropertyPresentation) handlerPresentation).setRowOrColumnCount(numberOfRows, iwc);
		}
		else if (handler instanceof TableColumnsHandler) {
			int numberOfColumns = getColumnCountForTable(iwc, ICObjectInstanceID);
			((TableRowColumnPropertyPresentation) handlerPresentation).setRowOrColumnCount(numberOfColumns, iwc);
		}
		return (handlerPresentation);
	}
	public String[] getPropertyDescriptions(IWContext iwc, String icObjectInstanceID, String methodIdentifier) {
		try {
			int numberOfParametersForMethod = MethodFinder.getInstance().getArgumentClasses(methodIdentifier).length;
			String[] theReturn = new String[numberOfParametersForMethod];
			for (int i = 0; i < theReturn.length; i++) {
				theReturn[i] = getMethodParameterProperty(iwc, icObjectInstanceID, methodIdentifier, i, this.METHOD_PARAMETER_PROPERTY_DESCRIPTION);
			}
			return theReturn;
		}
		catch (Exception e) {
			String[] theReturn = { this.getMethodDescription(Integer.parseInt(icObjectInstanceID), methodIdentifier, iwc)};
			return theReturn;
		}
	}
	/**
	 *
	 */
	public PresentationObject getPropertySetterComponent(IWContext iwc, String ICObjectInstanceID, String methodIdentifier, int parameterIndex, Class parameterClass, String name, String stringValue) {
		PresentationObject obj = null;
		try {
			obj = getHandlerInstance(iwc, ICObjectInstanceID, methodIdentifier, parameterIndex, name, stringValue);
		}
		catch (Exception e) {
		}
		if (obj != null) {
			return (obj);
		}
		if (parameterClass.equals(java.lang.Integer.class) || parameterClass.equals(Integer.TYPE)) {
			obj = new IntegerInput(name);
			((IntegerInput) obj).setMaxlength(9);
			((IntegerInput) obj).setLength(9);
			if (stringValue != null) {
				((IntegerInput) obj).setContent(stringValue);
			}
		}
		else if (parameterClass.equals(java.lang.String.class)) {
			obj = new TextInput(name);
			if (stringValue != null) {
				((TextInput) obj).setContent(stringValue);
			}
		}
		else if (parameterClass.equals(java.lang.Boolean.class) || parameterClass.equals(Boolean.TYPE)) {
			obj = new BooleanInput(name);
			((BooleanInput) obj).displaySelectOption();
			if (stringValue != null) {
				if (stringValue.equalsIgnoreCase("Y")) {
					((BooleanInput) obj).setSelected(true);
				}
				else if (stringValue.equalsIgnoreCase("T")) {
					((BooleanInput) obj).setSelected(true);
				}
				else if (stringValue.equalsIgnoreCase("N")) {
					((BooleanInput) obj).setSelected(false);
				}
				else if (stringValue.equalsIgnoreCase("F")) {
					((BooleanInput) obj).setSelected(false);
				}
			}
		}
		else if (parameterClass.equals(java.lang.Float.class) || parameterClass.equals(Float.TYPE)) {
			obj = new FloatInput(name);
			if (stringValue != null) {
				((FloatInput) obj).setContent(stringValue);
			}
		}
		else if (parameterClass.equals(java.lang.Void.class) || parameterClass.equals(Void.TYPE)) {
			obj = new CheckBox(name);
			if (stringValue != null) {
				((CheckBox) obj).setChecked(true);
			}
		}
		else if (parameterClass.equals(java.lang.Double.class) || parameterClass.equals(Double.TYPE)) {
			obj = new FloatInput(name);
			if (stringValue != null) {
				((FloatInput) obj).setContent(stringValue);
			}
		}
		else if (parameterClass.equals(com.idega.presentation.Image.class)) {
			BuilderImageInserter inserter = null;
			try {
				BuilderClassesFactory builderClassesFactoryTemp = getBuilderClassesFactory();
				inserter = builderClassesFactoryTemp.createImageInserterImpl();
			}
			catch (ClassNotFoundException ex) {
				// do nothing
			}
			inserter.setImSessionImageName(name);
			inserter.setHasUseBox(false);
			inserter.setNullImageIDDefault();
			try {
				inserter.setImageId(Integer.parseInt(stringValue));
			}
			catch (NumberFormatException e) {
				// do nothing
			}
			// BuilderImageInserter extends PresentationObjectType
			obj = (PresentationObject) inserter;
		}
		/**
		
		 * @todo handle page, template, file if the inputs already hava a value
		
		 */
		else if (parameterClass.equals(com.idega.core.file.data.ICFile.class)) {
			BuilderFileChooser fileChooser = null;
			try {
				BuilderClassesFactory builderClassesFactoryTemp = getBuilderClassesFactory();
				fileChooser = builderClassesFactoryTemp.createFileChooserImpl();
			}
			catch (ClassNotFoundException ex) {
				// do nothing
			}
			fileChooser.setChooserParameter(name);
			try {
				//extends block.media.presentation.FileChooser
				int id = Integer.parseInt(stringValue);
				IWMainApplication iwma = iwc.getIWMainApplication();
				Cache cache = IWMainApplication.getIWCacheManager().getCachedBlobObject(ICFile.class.getName(), id, iwma);
				fileChooser.setValue(cache.getEntity());
			}
			catch (Exception e) {
				//throw new RuntimeException(e.getMessage());
			}
			// BuilderImageInserter extends PresentationObjectType
			obj = (PresentationObject) fileChooser;
		}
		else if (parameterClass.equals(com.idega.core.builder.data.ICPage.class)) {
			com.idega.builder.presentation.IBPageChooser chooser = new com.idega.builder.presentation.IBPageChooser(name);
			try {
				ICPage page = ((com.idega.core.builder.data.ICPageHome) com.idega.data.IDOLookup.getHomeLegacy(ICPage.class)).findByPrimaryKeyLegacy(Integer.parseInt(stringValue));
				chooser.setValue(page);
			}
			catch (Exception e) {
				//throw new RuntimeException(e.getMessage());
			}
			obj = chooser;
		}
		else {
			obj = new TextInput(name);
			if (stringValue != null) {
				((TextInput) obj).setContent(stringValue);
			}
		}
		return (obj);
	}
	/**
	
	 *
	
	 */
	public String getMethodIdentifier(IWProperty methodProperty) {
		if (methodProperty.getType().equals(IWProperty.MAP_TYPE)) {
			return (methodProperty.getPropertyList().getProperty(METHOD_PROPERTY_IDENTIFIER));
		}
		else {
			return (methodProperty.getKey());
		}
	}
	/**
	
	 * @todo Change so that this returns the Localized description
	
	 */
	public String getMethodDescription(IWProperty methodProperty, IWContext iwc) {
		if (methodProperty.getType().equals(IWProperty.MAP_TYPE)) {
			return (methodProperty.getPropertyList().getProperty(METHOD_PROPERTY_DESCRIPTION));
		}
		else {
			return (methodProperty.getValue());
		}
	}
	/**
	
	 * @todo Change so that this returns the Localized description
	
	 */
	public String getMethodDescription(int icObjectInstanceID, String methodPropertyKey, IWContext iwc) {
		try {
			IWProperty methodProperty = getMethodProperty(icObjectInstanceID, methodPropertyKey, iwc.getIWMainApplication());
			if (methodProperty != null) {
				return (getMethodDescription(methodProperty, iwc));
			}
		}
		catch (Exception e) {
			e.printStackTrace(System.err);
		}
		return (null);
	}
	/**
	
	 * @todo Change so that this returns the Localized description
	
	 */
	public String getMethodParameterDescription(IWProperty methodProperty, int parameterIndex, IWContext iwc) {
		IWPropertyList parameter = getMethodParameterPropertyList(methodProperty, parameterIndex);
		if (parameter != null) {
			return (parameter.getProperty(METHOD_PARAMETER_PROPERTY_DESCRIPTION));
		}
		else {
			return ("Property " + parameterIndex);
		}
	}
	/**
	
	 * Returns a property of a Method Parameter, Returns null if nothing set
	
	 */
	public String getMethodParameterProperty(IWProperty methodProperty, int parameterIndex, String propertyKey) {
		IWPropertyList parameter = getMethodParameterPropertyList(methodProperty, parameterIndex);
		if (parameter != null) {
			return (parameter.getProperty(propertyKey));
		}
		else {
			return (null);
		}
	}
	/**
	
	 *
	
	 */
	public String getMethodParameterHandlerClassName(IWProperty methodProperty, int parameterIndex) {
		String theReturn = getMethodParameterProperty(methodProperty, parameterIndex, METHOD_PARAMETER_PROPERTY_HANDLER_CLASS);
		if (theReturn == null) {
			return (getClass().getName());
		}
		else {
			return (theReturn);
		}
	}
	/**
	
	 *
	
	 */
	public IWPropertyList getMethodParameterPropertyList(IWProperty methodProperty) {
		if (methodProperty.getType().equals(IWProperty.MAP_TYPE)) {
			return (methodProperty.getPropertyList().getPropertyList(METHOD_PARAMETERS_KEY));
		}
		else {
			return (null);
		}
	}
	/**
	
	 *
	
	 */
	public IWPropertyList getMethodParameterPropertyList(IWProperty methodProperty, int parameter) {
		IWPropertyList parameters = getMethodParameterPropertyList(methodProperty);
		if (parameters != null) {
			IWPropertyList list = parameters.getIWPropertyList(Integer.toString(parameter));
			return (list);
		}
		else {
			return (null);
		}
	}
	/**
	
	 *
	
	 */
	public List getAvailablePropertyHandlers() {
		try {
			return (EntityFinder.findAllByColumn(((com.idega.core.component.data.ICObjectHome) com.idega.data.IDOLookup.getHomeLegacy(ICObject.class)).createLegacy(), com.idega.core.component.data.ICObjectBMPBean.getObjectTypeColumnName(), com.idega.core.component.data.ICObjectBMPBean.COMPONENT_TYPE_PROPERTYHANDLER));
		}
		catch (Exception e) {
			e.printStackTrace();
			return (null);
		}
	}
	/**
	
	 *
	
	 */
	void preLoadPropertyHandlers() {
		List l = getAvailablePropertyHandlers();
		Iterator iter = l.iterator();
		while (iter.hasNext()) {
			ICObject item = (ICObject) iter.next();
			try {
				Class objectClass = item.getObjectClass();
				Object instance = objectClass.newInstance();
				putPropertyHandler(objectClass.getName(), instance);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	/**
	
	 *
	
	 */
	void putPropertyHandler(String key, Object handler) {
		getPropertyHandlersMap().put(key, handler);
	}
	/**
	
	 *
	
	 */
	private Map getPropertyHandlersMap() {
		if (propertyHandlers == null) {
			propertyHandlers = new HashMap();
		}
		return (propertyHandlers);
	}

	public PropertyHandler getPropertyHandler(String handlerClassName) {
		PropertyHandler theReturn = (PropertyHandler) getPropertyHandlersMap().get(handlerClassName);
		if (theReturn == null) {
			try {
				Class theClass = Class.forName(handlerClassName);
				theReturn = (PropertyHandler) theClass.newInstance();
				putPropertyHandler(handlerClassName, theReturn);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		return theReturn;
	}

	/**	
	 * @return false if property already set
	 */
	public boolean saveNewProperty(IWBundle iwb, String componentIdentifier, String methodIdentifier, String description, boolean isMultivalued, String[] handlers, String[] descriptions, boolean[] primaryKeys) throws Exception {
		//IWPropertyList complist = iwb.getComponentList();
		//IWPropertyList component = complist.getIWPropertyList(componentIdentifier);
		IWPropertyList component = iwb.getComponentPropertyList(componentIdentifier);
		IWPropertyList methodList = component.getIWPropertyList(this.METHODS_KEY);
		IWPropertyList method = methodList.getIWPropertyList(methodIdentifier);
		if (method != null) {
			return false;
		}
		method = methodList.getNewPropertyList(methodIdentifier);
		Map options = new Hashtable();
		options.put(METHOD_PROPERTY_DESCRIPTION, description);
		options.put(METHOD_PROPERTY_ALLOW_MULTIVALUED, new Boolean(isMultivalued));
		options.put(METHOD_PROPERTY_IDENTIFIER, methodIdentifier);
		Map parameters = new Hashtable();
		options.put(METHOD_PARAMETERS_KEY, parameters);
		for (int i = 0; i < handlers.length; i++) {
			String handler = handlers[i];
			String desc = descriptions[i];
			Map paramMap = new Hashtable();
			parameters.put(new Integer(i), paramMap);
			paramMap.put(METHOD_PARAMETER_PROPERTY_DESCRIPTION, desc);
			paramMap.put(METHOD_PARAMETER_PROPERTY_HANDLER_CLASS, handler);
			boolean primaryKey = primaryKeys[i];
			if (primaryKey) {
				paramMap.put(METHOD_PARAMETER_PROPERTY_PRIMARY_KEY, Boolean.TRUE);
			}
		}
		method.setProperties(options);
		return true;
	}

	public void setDropdownToChangeValue(DropdownMenu drop) {
		drop.setOnChange(com.idega.builder.presentation.IBPropertiesWindowSetter.MULTIVALUE_PROPERTY_CHANGE_FUNCTION_NAME + "()");
	}
	
	private BuilderClassesFactory getBuilderClassesFactory() throws ClassNotFoundException {
		if (builderClassesFactory == null) {
			builderClassesFactory = (BuilderClassesFactory) ImplementorRepository.getInstance().getImplementor(BuilderClassesFactory.class, this.getClass());
		}
		return builderClassesFactory;
	}
	
}