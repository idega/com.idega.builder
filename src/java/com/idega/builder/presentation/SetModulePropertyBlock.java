package com.idega.builder.presentation;

import java.lang.reflect.Method;
import java.util.List;

import com.idega.builder.bean.PropertyHandlerBean;
import com.idega.builder.business.BuilderConstants;
import com.idega.builder.business.BuilderLogic;
import com.idega.builder.business.IBPropertyHandler;
import com.idega.core.builder.business.ICBuilderConstants;
import com.idega.core.builder.presentation.ICPropertyHandler;
import com.idega.core.component.business.ComponentInfo;
import com.idega.core.component.business.ComponentProperty;
import com.idega.core.component.business.ComponentRegistry;
import com.idega.idegaweb.IWMainApplication;
import com.idega.presentation.Block;
import com.idega.presentation.IWContext;
import com.idega.presentation.Layer;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.ui.Label;
import com.idega.util.CoreConstants;
import com.idega.util.reflect.MethodFinder;

public class SetModulePropertyBlock extends Block {
	
	private boolean isMultiValue = false;
	private boolean isMethodIdentifier = false;
	
	@Override
	public void main(IWContext iwc) throws Exception {
		String pageKey = iwc.getApplicationAttribute(BuilderConstants.IB_PAGE_PARAMETER).toString();
		String propertyName = iwc.getApplicationAttribute(BuilderConstants.METHOD_ID_PARAMETER).toString();
		String instanceId = iwc.getApplicationAttribute(ICBuilderConstants.IC_OBJECT_INSTANCE_ID_PARAMETER).toString();
		if (pageKey == null || propertyName == null || instanceId == null) {
			return;
		}
		
		Layer container = new Layer();
		container.setStyleClass("modulePropertyContainer");
		
		IBPropertiesWindowSetter setter = new IBPropertiesWindowSetter();
		
		Class<?> presObjClass = null;
		int icObjectInstanceIDint = BuilderLogic.getInstance().getIBXMLReader().getICObjectInstanceIdFromComponentId(instanceId, null, pageKey);
		if (icObjectInstanceIDint == -1) {
			presObjClass = com.idega.presentation.Page.class;
		}
		else {
			presObjClass = BuilderLogic.getInstance().getObjectClass(icObjectInstanceIDint);
		}
		
		Class<?> parameters[] = getMethodParameters(propertyName, presObjClass, iwc.getIWMainApplication());
		checkIfParametersHasBooleanType(iwc, parameters);
		boolean needsReload = doesPropertyNeedReload(instanceId, propertyName, iwc);
		isMultiValue = parameters.length == 1 ? false : true;
		boolean isChangingProperty = setter.isChangingProperty(iwc);
		String selectedValues[] = setter.parseValues(iwc);
		String paramDescriptions[] = IBPropertyHandler.getInstance().getPropertyDescriptions(iwc, instanceId, propertyName);
		String realValues[] = BuilderLogic.getInstance().getPropertyValues(iwc.getIWMainApplication(), pageKey, instanceId, propertyName, selectedValues, !isChangingProperty);
		String value = BuilderConstants.EMPTY;
		String name = null;
		String paramDescription = null;
		String handlerClass = null;
		Class<?> parameterClass = null;
		String namePrefix = "ib_property_";
		for (int i = 0; i < parameters.length; i++) {
			parameterClass = parameters[i];
			value = BuilderConstants.EMPTY;
			try {
				value = realValues[i];
			}
			catch (ArrayIndexOutOfBoundsException e) {
			}
			catch (NullPointerException npe) {
			}
			
			Layer item = new Layer();
			item.setStyleClass("modulePropertyItem");
			
			name = new StringBuffer(namePrefix).append(i).toString();
			handlerClass = null;
			if (isMethodIdentifier) {
				handlerClass = IBPropertyHandler.getInstance().getMethodParameterProperty(iwc, instanceId, propertyName, i,
						IBPropertyHandler.METHOD_PARAMETER_PROPERTY_HANDLER_CLASS);
			}
			
			Label label = new Label();
			if (isMultiValue) {
				paramDescription = paramDescriptions[i];
				if (paramDescription != null) {
					label.setLabel(paramDescription);
					//description = setter.formatDescription(new StringBuffer(paramDescription).append(":").toString());
					//description.setFontStyle("font-family:Arial,Helvetica,sans-serif;font-size:8pt;");
				}
			}
			else {
				label.setLabel("Value");
			}
			item.add(label);
			
			PresentationObject handlerBox = IBPropertyHandler.getInstance().getPropertySetterComponent(iwc,	new PropertyHandlerBean(instanceId,
					propertyName, name, value, CoreConstants.BUILDER_PORPERTY_SETTER_STYLE_CLASS, parameterClass, i, needsReload, isMultiValue, parameters.length,
					presObjClass.getName()));
			
			handlerBox.setMarkupAttribute("jsfcomponent", IBPropertyHandler.getInstance().isJsfComponent(iwc, presObjClass.getName()));
			item.add(handlerBox);
			container.add(item);
			
			ICPropertyHandler handler = null;
			if (handlerClass != null && !handlerClass.equals(BuilderConstants.EMPTY)) {
				handler = IBPropertyHandler.getInstance().getPropertyHandler(handlerClass);
				if (handler != null) {
					iwc.setSessionAttribute(CoreConstants.HANDLER_PARAMETER, handler);
				}
			}
			if (handler == null) {
				iwc.removeSessionAttribute(CoreConstants.HANDLER_PARAMETER);
			}
		}
		
		this.add(container);
	}
	
	private void checkIfParametersHasBooleanType(IWContext iwc, Class<?>[] parameters) {
		if (parameters == null || parameters.length == 0) {
			iwc.removeSessionAttribute(BuilderConstants.BUILDER_MODULE_PROPERTY_HAS_BOOLEAN_TYPE_ATTRIBUTE);
			return;
		}
		
		boolean hasAnyBoolean = false;
		for (int i = 0; (i < parameters.length && !hasAnyBoolean); i++) {
			hasAnyBoolean = parameters[i].getName().toLowerCase().indexOf(boolean.class.getName()) != -1;
		}
		
		iwc.setSessionAttribute(BuilderConstants.BUILDER_MODULE_PROPERTY_HAS_BOOLEAN_TYPE_ATTRIBUTE, Boolean.valueOf(hasAnyBoolean));
	}
	
	private Class<?>[] getMethodParameters(String propertyName, Class<?> presObjClass, IWMainApplication iwma) {
		MethodFinder methodFinder = MethodFinder.getInstance();
		Class<?>[] parameters = null;
		if (methodFinder.isMethodIdentifier(propertyName)) {
			Method method = MethodFinder.getInstance().getMethod(propertyName, presObjClass);
			parameters = method.getParameterTypes();
			isMethodIdentifier = true;
			return parameters;
		}

		//	Fix for JSF type components
		try {
			ComponentRegistry registry = ComponentRegistry.getInstance(iwma);
			ComponentInfo info = registry.getComponentByClassName(presObjClass.getName());
			
			List<ComponentProperty> properties = info.getProperties();
			ComponentProperty property = null;
			for (int i = 0; (i < properties.size() && property == null); i++) {
				property = properties.get(i);
				if (!propertyName.equals(property.getName())) {
					property = null;
				}
			}
			
			Class<?> parameter = null;
			String className = property.getClassName();
			if (boolean.class.getName().equals(className)) {
				parameter = Boolean.class;
			}
			if (parameter == null) {
				parameter = Class.forName(className);
			}
			parameters = new Class[]{parameter};
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		if (parameters == null || parameters.length == 0) {
			parameters = new Class[]{String.class};
		}
		
		return parameters;
	}
	
	public boolean isMultiValue() {
		return isMultiValue;
	}
	
	@Override
	public String getBundleIdentifier() {
		return BuilderConstants.IW_BUNDLE_IDENTIFIER;
	}
	
	private boolean doesPropertyNeedReload(String instanceId, String propertyName, IWContext iwc) {
		if (instanceId == null || propertyName == null || iwc == null) {
			return false;
		}
		
		List<ComponentProperty> properties = IBPropertyHandler.getInstance().getComponentProperties(instanceId, iwc.getIWMainApplication(), iwc.getCurrentLocale());
		if (properties == null) {
			return false;
		}
		
		ComponentProperty property = null;
		for (int i = 0; i < properties.size(); i++) {
			property = properties.get(i);
			if (propertyName.equals(property.getName())) {
				return property.isNeedsReload();
			}
		}
		
		return false;
	}

}
