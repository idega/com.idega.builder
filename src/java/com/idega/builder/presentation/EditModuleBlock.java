package com.idega.builder.presentation;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import com.idega.builder.business.BuilderConstants;
import com.idega.builder.business.BuilderLogic;
import com.idega.builder.business.ComponentPropertyComparator;
import com.idega.builder.business.IBPropertyHandler;
import com.idega.core.component.business.ComponentProperty;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.Block;
import com.idega.presentation.IWContext;
import com.idega.presentation.Layer;
import com.idega.presentation.Script;
import com.idega.presentation.Span;
import com.idega.presentation.text.Break;
import com.idega.presentation.text.Heading1;
import com.idega.presentation.text.Heading3;
import com.idega.presentation.text.Link;
import com.idega.presentation.text.ListItem;
import com.idega.presentation.text.Lists;
import com.idega.presentation.text.Text;
import com.idega.presentation.ui.GenericButton;

public class EditModuleBlock extends Block {
	
	public EditModuleBlock() {
		setCacheable(getCacheKey(), 0);
	}
	
	public String getCacheKey() {
		return BuilderConstants.EDIT_MODULE_WINDOW_CACHE_KEY;
	}
	
	protected String getCacheState(IWContext iwc, String cacheStatePrefix) {
		String name = iwc.getParameter(BuilderConstants.MODULE_NAME);
		String instanceId = iwc.getParameter(BuilderConstants.IC_OBJECT_INSTANCE_ID_PARAMETER);

		return new StringBuffer(cacheStatePrefix).append(name).append(instanceId).toString();
	}
	
	public void main(IWContext iwc) throws Exception {
		String name = iwc.getParameter(BuilderConstants.MODULE_NAME);
		String instanceId = iwc.getParameter(BuilderConstants.IC_OBJECT_INSTANCE_ID_PARAMETER);
		if (name == null || instanceId == null) {
			return;
		}
		
		IWResourceBundle iwrb = BuilderLogic.getInstance().getBuilderBundle().getResourceBundle(iwc);
		
		List properties = getPropertyListOrdered(iwc, instanceId);
		
		// Header
		Layer header = new Layer();
		header.add(new Heading1(name));
		header.setId("editModuleHeader");
		this.add(header);
		
		// Menu
		Layer menu = new Layer();
		menu.setId("editModuleMenu");
		Lists navigation = new Lists();
		navigation.setId("editModuleMenuNavigation");
		
		ListItem settings = new ListItem();
		Link settingsLink = new Link(iwrb.getLocalizedString("settings", "Settings"), "#");
		//settingsLink.setOnClick("alert('settings')");
		settings.add(settingsLink);
		navigation.add(settings);
		
		/*ListItem settings2 = new ListItem();
		Link settings2Link = new Link(iwrb.getLocalizedString("settings2", "Settings2"), "#");
		settings2.add(settings2Link);
		navigation.add(settings2);*/
		menu.add(navigation);
		this.add(menu);
		
		this.add(new Break());
		
		Layer propertiesContainer = new Layer();
		addProperties(properties, propertiesContainer, iwrb, iwc, instanceId);
		this.add(propertiesContainer);
		
		// Cancel button
		Layer closeContainer = new Layer();
		closeContainer.setId("closeButtonContainer");
		closeContainer.setStyleClass("closeButtonContainerStyle");
		GenericButton close = new GenericButton("cancel", iwrb.getLocalizedString("cancel", "Cancel"));
		close.setOnClick("exitFromPropertiesWindow();");
		closeContainer.add(close);
		this.add(closeContainer);
		
		// Be sure 'niftycube.js' and 'BuilderHelper.js' files are added to page
		Script init = new Script();
		init.addScriptLine("initializeEditModuleWindow();");
		this.add(init);
	}
	
	@SuppressWarnings("unchecked")
	private List getPropertyListOrdered(IWContext iwc, String instanceId) throws Exception {
		List properties = IBPropertyHandler.getInstance().getComponentProperties(instanceId, iwc.getIWMainApplication(), iwc.getCurrentLocale(), true);
		java.util.Collections.sort(properties, ComponentPropertyComparator.getInstance());
		return properties;
	}
	
	private void addProperties(List properties, Layer container, IWResourceBundle iwrb, IWContext iwc, String instanceId) {
		if (properties == null) {
			return;
		}
		Object o = null;
		ComponentProperty property = null;
		List<ComponentProperty> simpleProperties = new ArrayList<ComponentProperty>();
		List<ComponentProperty> advancedProperties = new ArrayList<ComponentProperty>();
		for (int i = 0; i < properties.size(); i++) {
			o = properties.get(i);
			if (o instanceof ComponentProperty) {
				property = (ComponentProperty) o;
				if (property.isSimpleProperty()) {
					simpleProperties.add(property);
				}
				else {
					advancedProperties.add(property);
				}
			}
		}
		addPropertiesToContainer(simpleProperties, container, iwrb.getLocalizedString("simple_properties", "Simple Properties"), "simple_properties_box", null, false, iwc.getCurrentLocale(), instanceId);		
		addPropertiesToContainer(advancedProperties, container, iwrb.getLocalizedString("advanced_properties", "Advanced Properties"), "advanced_properties_box", null, true, iwc.getCurrentLocale(), instanceId);	
	}
	
	private void addPropertiesToContainer(List<ComponentProperty> properties, Layer parent, String name, String id, String className, boolean hidePropertiesList, Locale locale, String instanceId) {
		if (properties == null || parent == null || name == null) {
			return;
		}
		// Main container
		Layer container = new Layer();
		
		// Header
		Span header = new Span();
		header.add(new Heading3(name));
		container.add(header);
		container.setToolTip(name);
		
		if (id != null) {
			container.setId(id);
		}
		if (className != null) {
			container.setStyleClass(className);
		}
		
		// Properties container
		Layer propertiesContainer = new Layer();
		Random generator = new Random();
		int random = generator.nextInt(Integer.MAX_VALUE);
		String propertiesContainerId = new StringBuffer("propertiesContainerId").append(random).toString();
		propertiesContainer.setId(propertiesContainerId);
		header.setStyleClass("componentPropertiesListHeader");
		header.setOnClick(new StringBuffer("manageComponentPropertiesList('").append(propertiesContainerId).append("');").toString());
		if (hidePropertiesList) {
			Script hide = new Script();
			hide.addScriptLine(new StringBuffer("addPropertyIdAndClose('").append(propertiesContainerId).append("');").toString());
			header.add(hide);
		}
		
		// Properties
		ComponentProperty property = null;
		Lists list = new Lists();
		list.setListOrdered(true);
		ListItem item = null;
		String itemStyle = "moduleProperty";
		String propertyId = null;
		Span propertyName = null;
		for (int i = 0; i < properties.size(); i++) {
			propertyId = new StringBuffer("property").append(generator.nextInt(Integer.MAX_VALUE)).toString();
			property = properties.get(i);
			item = new ListItem();
			item.setId(propertyId);
			item.setStyleClass(itemStyle);
			propertyName = new Span(new Text(property.getDisplayName(locale)));
			propertyName.setOnClick(new StringBuffer("getPropertyBox('").append(propertyId).append("', '").append(property.getName()).append("', '").append(instanceId).append("');").toString());
			item.add(propertyName);
			list.add(item);
		}
		
		propertiesContainer.add(list);
		container.add(propertiesContainer);
		parent.add(container);
	}

}
