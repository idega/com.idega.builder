package com.idega.builder.presentation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.ejb.FinderException;

import com.idega.builder.business.BuilderConstants;
import com.idega.builder.business.BuilderLogic;
import com.idega.builder.business.ModuleComparator;
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
import com.idega.presentation.text.Heading3;
import com.idega.presentation.text.ListItem;
import com.idega.presentation.text.Lists;

public class AddModuleBlock extends Block {
	
	public AddModuleBlock() {
		setCacheable(getCacheKey());
	}
	
	public String getCacheKey() {
		return BuilderConstants.ADD_NEW_MODULE_WINDOW_CACHE_KEY;
	}
	
	protected String getCacheState(IWContext iwc, String cacheStatePrefix) {
		return cacheStatePrefix;
	}
	
	public void main(IWContext iwc) throws Exception {
		IWResourceBundle iwrb = BuilderLogic.getInstance().getBuilderBundle().getResourceBundle(iwc);
		boolean isBuilderUser = iwc.getAccessController().hasRole(StandardRoles.ROLE_KEY_BUILDER, iwc);
		
		Collection<ICObject> allComoponents = getAllComponents();
		
		Layer componentsContainer = new Layer();
		Lists items = new Lists();
		items.setID("modules_lists");
		
		ListItem widgetsList = new ListItem();
		widgetsList.setId("widget_modules");
		List<ICObject> widgets = getConcreteComponents(iwc, allComoponents, true, false, false);
		addListToWindow(widgets, iwrb.getLocalizedString("widget_modules", "Widgets"), "widgets_list", widgetsList);
		items.add(widgetsList);
		
		ListItem blocksList = new ListItem();
		blocksList.setId("block_modules");
		List<ICObject> blocks = getConcreteComponents(iwc, allComoponents, false, true, false);
		addListToWindow(blocks, iwrb.getLocalizedString("blocks_header", "Blocks"), "blocks_list", blocksList);
		items.add(blocksList);
		
		ListItem builderList = new ListItem();
		builderList.setId("builder_modules");
		List<ICObject> builder = null;
		if (isBuilderUser) {
			builder = getConcreteComponents(iwc, allComoponents, false, false, true);
		}
		addListToWindow(builder, iwrb.getLocalizedString("builder_modules", "Builder"), "builder_list", builderList);
		
		items.add(builderList);
		componentsContainer.add(items);
		this.add(componentsContainer);
	}
	
	@SuppressWarnings("unchecked")
	private void addListToWindow(List<ICObject> objects, String name, String id, ListItem container) {
		Heading3 header = new Heading3(name);
		container.add(header);
		
		Layer content = new Layer();
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
		container.add(content);
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
			return components;
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
			return components;
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
		if (components != null) {
			ModuleComparator comparator = new ModuleComparator(iwc);
			java.util.Collections.sort(components, comparator);
		}
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
