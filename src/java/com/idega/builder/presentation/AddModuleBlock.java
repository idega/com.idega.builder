package com.idega.builder.presentation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.ejb.FinderException;

import com.idega.builder.business.BuilderConstants;
import com.idega.builder.business.BuilderLogic;
import com.idega.builder.business.ICObjectComparator;
import com.idega.core.accesscontrol.business.StandardRoles;
import com.idega.core.component.data.ICObject;
import com.idega.core.component.data.ICObjectBMPBean;
import com.idega.core.component.data.ICObjectHome;
import com.idega.data.IDOLookup;
import com.idega.data.IDOLookupException;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.Block;
import com.idega.presentation.IWContext;
import com.idega.presentation.Layer;
import com.idega.presentation.text.Heading1;
import com.idega.presentation.text.Heading3;
import com.idega.presentation.text.ListItem;
import com.idega.presentation.text.Lists;
import com.idega.util.CoreConstants;

public class AddModuleBlock extends Block {
	
	private Map<String, List<ICObject>> addedTabs = new HashMap<String, List<ICObject>>();
	private String localizedText = "Sorry, there are no components available.";
	
	public void main(IWContext iwc) throws Exception {
		IWResourceBundle iwrb = BuilderLogic.getInstance().getBuilderBundle().getResourceBundle(iwc);
		boolean isBuilderUser = iwc.getAccessController().hasRole(StandardRoles.ROLE_KEY_BUILDER, iwc);
		
		localizedText = iwrb.getLocalizedString("no_components_available", localizedText);
		
		Collection<ICObject> allComoponents = getAllComponents();
		
		Layer container = new Layer();
		add(container);
		
		// Header
		Layer header = new Layer();
		String regionName = iwc.getParameter(BuilderConstants.REGION_NAME);
		StringBuffer label = new StringBuffer(iwrb.getLocalizedString(BuilderConstants.ADD_MODULE_TO_REGION_LOCALIZATION_KEY,
				BuilderConstants.ADD_MODULE_TO_REGION_LOCALIZATION_VALUE));
		if (regionName != null && !(CoreConstants.EMPTY.equals(regionName))) {
			label.append(CoreConstants.SPACE).append(iwrb.getLocalizedString("to", "to")).append(CoreConstants.SPACE).append(iwrb.getLocalizedString("region", "region"));
			label.append(CoreConstants.SPACE).append(regionName);
		}
		header.add(new Heading1(label.toString()));
		header.setStyleClass("addModuleToBuilderPage");
		container.add(header);
		
		Lists titles = new Lists();
		titles.setStyleClass("mootabs_title");
		
		List<ICObject> widgets = getConcreteComponents(iwc, allComoponents, true, false, false);
		if (widgets != null && widgets.size() > 0) {
			addComponentsTitles(titles, widgets, "widgetsTab", iwrb.getLocalizedString("widget_modules", "Widgets"));
		}

		List<ICObject> blocks = getConcreteComponents(iwc, allComoponents, false, true, false);
		if (blocks != null && blocks.size() > 0) {
			addComponentsTitles(titles, blocks, "blocksTab", iwrb.getLocalizedString("blocks_header", "Blocks"));
		}

		List<ICObject> builderComponents = null;
		if (isBuilderUser) {
			builderComponents = getConcreteComponents(iwc, allComoponents, false, false, true);
			if (builderComponents != null && builderComponents.size() > 0) {
				addComponentsTitles(titles, builderComponents, "builderTab", iwrb.getLocalizedString("builder_modules", "Builder"));
			}
		}
		
		if (addedTabs.size() == 0) {
			container.add(new Heading3(localizedText));
			return;
		}
		
		container.add(titles);
		String key = null;
		for (Iterator<String> keys = addedTabs.keySet().iterator(); keys.hasNext();) {
			key = keys.next();
			
			Layer componentsListContainer = new Layer();
			componentsListContainer.setStyleClass("mootabs_panel");
			componentsListContainer.setId(key);
			addListToWindow(addedTabs.get(key), componentsListContainer);
			container.add(componentsListContainer);
		}
		
		Layer script = new Layer();
		script.add(new StringBuffer("<script type=\"text/javascript\">createTabsWithMootabs('").append(container.getId()).append("');</script>").toString());
		container.add(script);
	}
	
	private void addComponentsTitles(Lists titles, List<ICObject> components, String tabText, String text) {
		ListItem tab = new ListItem();
		tab.setMarkupAttribute("title", tabText);
		tab.addText(text);
		titles.add(tab);
		
		addedTabs.put(tabText, components);
	}
	
	@SuppressWarnings("unchecked")
	private void addListToWindow(List<ICObject> objects, Layer container) {
		Layer content = new Layer();
		container.add(content);
		if (objects == null || objects.size() == 0) {
			content.add(localizedText);
			return;
		}
		
		Lists items = new Lists();
		String itemStyleClass = "modulesListItemStyle";
		ListItem item = new ListItem();
		String actionDefinition = "onclick";
		ICObject object = null;
		for (int i = 0; i < objects.size(); i++) {
			object = objects.get(i);
			item = new ListItem();
			item.addText(object.getName());
			item.setStyleClass(itemStyleClass);
			item.attributes.put(actionDefinition, new StringBuffer("addSelectedModule(").append(object.getID()).append(", '").append(object.getClassName()).append("');").toString());
			items.add(item);
		}
		content.add(items);
	}
	
	@SuppressWarnings("unchecked")
	private List<ICObject> getConcreteComponents(IWContext iwc, Collection<ICObject> allComponents, boolean findWidgets, boolean findBlocks, boolean findBuilder) {
		List<ICObject> components = new ArrayList<ICObject>();
		List<String> namesList = new ArrayList<String>();

		if (allComponents == null) {
			return components;
		}
		
		ICObject object = null;
		//	Find "widget" type modules
		if (findWidgets) {
			for (Iterator<ICObject> it = allComponents.iterator(); it.hasNext(); ) {
				object = it.next();
				if (object.isWidget()) {
					addComponent(components, object, namesList);
				}
			}
			return getSortedComponents(components);
		}
		
		//	Find "block" type modules
		if (findBlocks) {
			for (Iterator<ICObject> it = allComponents.iterator(); it.hasNext(); ) {
				object = it.next();
				if (object.isBlock()) {
					if (!components.contains(object)) {
						addComponent(components, object, namesList);
					}
				}
			}
			return getSortedComponents(components);
		}
		
		//	Find all other Builder/Development modules
		for (Iterator<ICObject> it = allComponents.iterator(); it.hasNext(); ) {
			object = it.next();
			if (!object.isBlock() && !object.isWidget()) {
				if (ICObjectBMPBean.COMPONENT_TYPE_ELEMENT.equals(object.getObjectType())) {
					addComponent(components, object, namesList);
				}
				if (ICObjectBMPBean.COMPONENT_TYPE_BLOCK.equals(object.getObjectType())) {
					addComponent(components, object, namesList);
				}
				if (ICObjectBMPBean.COMPONENT_TYPE_JSFUICOMPONENT.equals(object.getObjectType())) {
					addComponent(components, object, namesList);
				}
			}
		}
		
		return getSortedComponents(components);
	}
	
	private List<ICObject> getSortedComponents(List<ICObject> components) {
		if (components == null) {
			return null;
		}
		
		ICObjectComparator comparator = new ICObjectComparator();
		Collections.sort(components, comparator);
		
		return components;
	}
	
	private void addComponent(List<ICObject> components, ICObject component, List<String> namesList) {
		if (component == null || components == null || namesList == null) {
			return;
		}
		
		if (namesList.contains(component.getClassName())) {
			return;
		}
		
		namesList.add(component.getClassName());
		components.add(component);
	}
	
	@SuppressWarnings("unchecked")
	private Collection<ICObject> getAllComponents() {
		ICObjectHome icoHome = null;
		try {
			icoHome = (ICObjectHome) IDOLookup.getHome(ICObject.class);
		} catch (IDOLookupException e) {
			e.printStackTrace();
		}
		if (icoHome == null) {
			return null;
		}
		
		try {
			return icoHome.findAll();
		} catch (FinderException e) {
			e.printStackTrace();
		}
		return null;
	}

}
