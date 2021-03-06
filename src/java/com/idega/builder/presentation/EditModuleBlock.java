package com.idega.builder.presentation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.faces.component.UIComponent;

import com.idega.builder.bean.AdvancedProperty;
import com.idega.builder.business.BuilderConstants;
import com.idega.builder.business.BuilderLogic;
import com.idega.builder.business.ComponentPropertyComparator;
import com.idega.builder.business.IBPropertyHandler;
import com.idega.core.accesscontrol.business.AccessController;
import com.idega.core.accesscontrol.business.StandardRoles;
import com.idega.core.builder.business.ICBuilderConstants;
import com.idega.core.component.business.ComponentProperty;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.Block;
import com.idega.presentation.IWContext;
import com.idega.presentation.Image;
import com.idega.presentation.Layer;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.Span;
import com.idega.presentation.text.Heading1;
import com.idega.presentation.text.Heading3;
import com.idega.presentation.text.ListItem;
import com.idega.presentation.text.Lists;
import com.idega.presentation.text.Text;
import com.idega.presentation.ui.IFrame;
import com.idega.util.CoreConstants;
import com.idega.util.PresentationUtil;
import com.idega.util.StringUtil;

public class EditModuleBlock extends Block {
	
	private String groupPermissionsText = "group_permissions";
	//private String rolePermissionsText = "role_permissions";
	
	private String localizedText = "Sorry, there are no properties for this module.";
	private Map<String, List<ComponentProperty>> addedProperties = new HashMap<String, List<ComponentProperty>>();
	
	@Override
	public void main(IWContext iwc) throws Exception {
		String instanceId = iwc.getParameter(ICBuilderConstants.IC_OBJECT_INSTANCE_ID_PARAMETER);
		String pageKey = iwc.getParameter(BuilderConstants.IB_PAGE_PARAMETER_FOR_EDIT_MODULE_BLOCK);
		if (instanceId == null) {
			return;
		}
		
		BuilderLogic builder = BuilderLogic.getInstance();
		IWResourceBundle iwrb = getResourceBundle(iwc);
		
		String nullInString = "null";
		if (pageKey == null || CoreConstants.EMPTY.equals(pageKey) || nullInString.equals(pageKey)) {
			pageKey = builder.getCurrentIBPage(iwc);
		}
		if (pageKey == null || CoreConstants.MINUS.equals(pageKey) || nullInString.equals(pageKey)) {
			logError("Page id is not defined for EditModuleBlock!");
			add(new Heading1(iwrb.getLocalizedString("error_page_was_not_found", "Sorry, page was not found.")));
			add(PresentationUtil.getJavaScriptAction("MOOdalBox.addEventToCloseAction(function(){reloadPage();});"));
			return;
		}
		
		String name = null;
		iwc.getRequest().setAttribute(BuilderConstants.TRANSFORM_PAGE_TO_BUILDER_PAGE_ATTRIBUTE, Boolean.FALSE);
		UIComponent component = builder.findComponentInPage(iwc, pageKey, instanceId);
		iwc.getRequest().removeAttribute(BuilderConstants.TRANSFORM_PAGE_TO_BUILDER_PAGE_ATTRIBUTE);
		if (component instanceof PresentationObject) {
			name = ((PresentationObject) component).getBuilderName(iwc);
		}
		else {
			name = component.getClass().getSimpleName();
		}
		
		localizedText = iwrb.getLocalizedString("no_properties_for_this_module", localizedText);
		
		List<ComponentProperty> properties = getPropertyListOrdered(iwc, instanceId);
		if (properties == null) {
			return;
		}
		
		Layer container = new Layer();
		add(container);
		
		// Header
		Layer header = new Layer();
		header.add(new Heading1(StringUtil.isEmpty(name) ? iwrb.getLocalizedString("unkown_module", "Unkown module") : name));
		header.setId("editModuleHeader");
		container.add(header);
		
		//	Properties
		Layer propertiesContainer = new Layer();
		addProperties(properties, propertiesContainer, iwrb, iwc, instanceId, pageKey);
		container.add(propertiesContainer);
		
		Layer script = new Layer();
		script.add(new StringBuffer("<script type=\"text/javascript\">createTabsWithMootabs('").append(propertiesContainer.getId()).append("');</script>").toString());
		container.add(script);
	}
	
	private List<ComponentProperty> getPropertyListOrdered(IWContext iwc, String instanceId) throws Exception {
		List<ComponentProperty> properties = IBPropertyHandler.getInstance().getComponentProperties(instanceId, iwc.getIWMainApplication(), iwc.getCurrentLocale());
		if (properties == null) {
			return null;
		}
		
		try {
			Collections.sort(properties, ComponentPropertyComparator.getInstance());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return properties;
	}
	
	private void addProperties(List<ComponentProperty> properties, Layer container, IWResourceBundle iwrb, IWContext iwc, String instanceId, String pageKey) {
		if (properties == null) {
			return;
		}
		
		ComponentProperty property = null;
		List<ComponentProperty> simpleProperties = new ArrayList<ComponentProperty>();
		List<ComponentProperty> advancedProperties = new ArrayList<ComponentProperty>();
		List<String> addedPropertiesNames = new ArrayList<String>();
		for (int i = 0; i < properties.size(); i++) {
			property = properties.get(i);
			
			if (!addedPropertiesNames.contains(property.getName())) {
				addedPropertiesNames.add(property.getName());
				
				if (property.isSimpleProperty()) {
					simpleProperties.add(property);
				}
				else {
					advancedProperties.add(property);
				}
			}
		}
	
		Lists tabs = new Lists();
		tabs.setStyleClass("mootabs_title");
		container.add(tabs);
		if (simpleProperties.size() == 0 || advancedProperties.size() == 0) {
			List<ComponentProperty> jointList = new ArrayList<ComponentProperty>(simpleProperties);
			jointList.addAll(advancedProperties);
			String propertiesText = "module_properties";
			addTab(tabs, propertiesText, iwrb.getLocalizedString(propertiesText, "Properties"), jointList);
		}
		else {
			String simplePropertiesTabKey = "simple_properties";
			addTab(tabs, simplePropertiesTabKey, iwrb.getLocalizedString(simplePropertiesTabKey, "Simple Properties"), simpleProperties);
			
			String advancedPropertiesTabKey = "advanced_properties";
			addTab(tabs, advancedPropertiesTabKey, iwrb.getLocalizedString(advancedPropertiesTabKey, "Advanced Properties"), advancedProperties);
		}
		
		boolean isBuilderUser = iwc.getAccessController().hasRole(StandardRoles.ROLE_KEY_BUILDER, iwc);
		if (isBuilderUser) {
			addTab(tabs, groupPermissionsText, iwrb.getLocalizedString(groupPermissionsText, "Group permissions"), null);
//			addTab(tabs, rolePermissionsText, iwrb.getLocalizedString(rolePermissionsText, "Role permissions"), null);		//	TODO:	implement tab for roles
		}
		
		String key = null;
		Layer tabContentContainer = null;
		for (Iterator<String> keys = addedProperties.keySet().iterator(); keys.hasNext();) {
			key = keys.next();
			
			tabContentContainer = new Layer();
			container.add(tabContentContainer);
			tabContentContainer.setId(key);
			tabContentContainer.setStyleClass("mootabs_panel");
			if (key.equals(groupPermissionsText)) {
				addGroupPermissionsWindow(iwc, tabContentContainer, instanceId);
			}
			/*else if (key.equals(rolePermissionsText)) {
				//	TODO:	implement tab for roles
			}*/
			else {
				addPropertiesToContainer(addedProperties.get(key), tabContentContainer, instanceId, iwc, pageKey);
			}
		}
	}
	
	private void addGroupPermissionsWindow(IWContext iwc, Layer main, String instanceId) {
		List<AdvancedProperty> parameters = new ArrayList<AdvancedProperty>();
		parameters.add(new AdvancedProperty(IBPermissionWindow._PARAMETERSTRING_IDENTIFIER, instanceId));
		parameters.add(new AdvancedProperty(IBPermissionWindow._PARAMETERSTRING_PERMISSION_CATEGORY, String.valueOf(AccessController.CATEGORY_OBJECT_INSTANCE)));
		parameters.add(new AdvancedProperty(BuilderConstants.CURRENT_COMPONENT_IS_IN_FRAME, Boolean.TRUE.toString()));
		IFrame frame = new IFrame(groupPermissionsText, BuilderLogic.getInstance().getUriToObject(IBPermissionWindow.class, parameters));
		frame.setStyleClass("groupPermissionFrameInNewBuilderStyle");
		main.add(frame);
	}
	
	private void addTab(Lists tabs, String key, String text, List<ComponentProperty> properties) {
		ListItem tab = new ListItem();
		tab.addText(text);
		tab.setMarkupAttribute("title", key);
		tabs.add(tab);
		
		addedProperties.put(key, properties);
	}
	
	private void addPropertiesToContainer(List<ComponentProperty> properties, Layer main, String instanceId, IWContext iwc, String pageKey) {		
		// Main container
		Layer container = new Layer();
		main.add(container);
		
		if (properties == null || properties.size() == 0) {
			container.add(new Heading3(localizedText));
			return;
		}
		
		// Header
		Span header = new Span();
		container.add(header);
		
		//	Properties wrapper
		Layer propertiesWrapper = new Layer();
		
		// Properties container
		Layer propertiesContainer = new Layer();
		Random generator = new Random();
		int random = generator.nextInt(Integer.MAX_VALUE);
		String propertiesContainerId = new StringBuffer("propertiesContainerId").append(random).toString();
		propertiesContainer.setId(propertiesContainerId);
		header.setStyleClass("componentPropertiesListHeader");
		header.setMarkupAttributeMultivalued("onclick", new StringBuffer("manageComponentPropertiesList('").append(propertiesContainerId).append("');").toString());
		
		// Properties
		ComponentProperty property = null;
		Lists list = new Lists();
		list.setListOrdered(true);
		ListItem item = null;
		String itemStyle = "moduleProperty";
		String itemStyleSetProperty = "modulePropertyIsSet";
		String removeStyleClass = "removeBuilderModulePropertyStyle";
		String propertyId = null;
		Span propertyName = null;
		IWBundle bundle = getBundle(iwc);
		String imageUri = bundle.getVirtualPathWithFileNameString("remove.png");
		String imageName = bundle.getResourceBundle(iwc).getLocalizedString("remove", "Remove");
		Image remove = null;
		boolean isPropertySet = false;
		for (int i = 0; i < properties.size(); i++) {
			propertyId = new StringBuffer("property").append(generator.nextInt(Integer.MAX_VALUE)).toString();
			property = properties.get(i);
			item = new ListItem();
			item.setId(propertyId);
			isPropertySet = BuilderLogic.getInstance().isPropertySet(pageKey, instanceId, property.getName(), iwc.getIWMainApplication());
			if (isPropertySet) {
				item.setStyleClass(itemStyleSetProperty);
			}
			else {
				item.setStyleClass(itemStyle);
			}
			propertyName = new Span(new Text(property.getDisplayName(iwc.getCurrentLocale())));
			
			String methodName = property.getName();
			propertyName.setMarkupAttributeMultivalued("onclick", new StringBuffer("getPropertyBox('").append(propertyId).append("', '").append(methodName).append("', '").append(instanceId).append("', ").append(needReloadPropertyBox(methodName, property.getClassName())).append(");").toString());
			item.add(propertyName);
			
			if (isPropertySet) {
				remove = new Image(imageUri, imageName, 16, 16);
				remove.setStyleClass(removeStyleClass);
				remove.setOnClick(new StringBuffer("removeBuilderModuleProperty('").append(remove.getId()).append("', '").append(propertyId).append("', '").append(instanceId).append("', '").append(property.getName()).append("');").toString());
				item.add(remove);
			}
			
			list.add(item);
		}
		
		propertiesContainer.add(list);
		propertiesWrapper.add(propertiesContainer);
		propertiesWrapper.setId(new StringBuffer(propertiesContainer.getId()).append("Wrapper").toString());
		container.add(propertiesWrapper);
	}
	
	private boolean needReloadPropertyBox(String methodName, String propertyClassName) {
		String booleanClassName = boolean.class.getName();
		boolean needReload = methodName.toLowerCase().indexOf(booleanClassName) != -1;
		
		if (!needReload && propertyClassName != null) {
			needReload = propertyClassName.indexOf(booleanClassName) != -1;
		}
		
		return needReload;
	}
	
	@Override
	public String getBundleIdentifier() {
		return BuilderConstants.IW_BUNDLE_IDENTIFIER;
	}

}
