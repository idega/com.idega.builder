package com.idega.builder.presentation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.ejb.FinderException;

import com.idega.builder.business.ModuleComparator;
import com.idega.core.accesscontrol.business.StandardRoles;
import com.idega.core.component.data.ICObject;
import com.idega.core.component.data.ICObjectBMPBean;
import com.idega.core.component.data.ICObjectHome;
import com.idega.data.IDOLookup;
import com.idega.data.IDOLookupException;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.IWContext;
import com.idega.presentation.Layer;
import com.idega.presentation.Script;
import com.idega.presentation.text.Break;
import com.idega.presentation.text.Heading3;
import com.idega.presentation.text.ListItem;
import com.idega.presentation.text.Lists;
import com.idega.presentation.text.Paragraph;
import com.idega.presentation.ui.GenericButton;

public class AddModuleWindow extends IBAdminWindow {
	
	public void main(IWContext iwc) throws Exception {
		IWResourceBundle iwrb = getBuilderLogic().getBuilderBundle().getResourceBundle(iwc);
		boolean isBuilderUser = iwc.getAccessController().hasRole(StandardRoles.ROLE_KEY_BUILDER, iwc);
		
		Collection allComoponents = getAllComponents();
		
		Layer componentsContainer = new Layer();
		Lists items = new Lists();
		items.setID("modules_lists");
		
		ListItem widgetsList = new ListItem();
		widgetsList.setId("one");
		List<ICObject> widgets = getConcreteComponents(iwc, allComoponents, true, false, false);
		addListToWindow(widgets, iwrb.getLocalizedString("widget_modules", "Widgets"), "widgets_list", widgetsList);
		items.add(widgetsList);
		
		ListItem blocksList = new ListItem();
		blocksList.setId("two");
		List<ICObject> blocks = getConcreteComponents(iwc, allComoponents, false, true, false);
		addListToWindow(blocks, iwrb.getLocalizedString("blocks_header", "Blocks"), "blocks_list", blocksList);
		items.add(blocksList);
		
		ListItem builderList = new ListItem();
		builderList.setId("three");
		List<ICObject> builder = null;
		if (isBuilderUser) {
			builder = getConcreteComponents(iwc, allComoponents, false, false, true);
		}
		addListToWindow(builder, iwrb.getLocalizedString("builder_modules", "Builder"), "builder_list", builderList);
		
		items.add(builderList);
		componentsContainer.add(items);
		this.add(componentsContainer);
		
		this.add(new Break());
		
		Layer closeContainer = new Layer();
		closeContainer.setId("closeButtonContainer");
		closeContainer.setStyleClass("closeButtonContainerStyle");
		GenericButton close = new GenericButton("cancel", iwrb.getLocalizedString("cancel", "Cancel"));
		close.setOnClick("closeAddModuleWindow();");
		closeContainer.add(close);
		this.add(closeContainer);
		
		// Be sure 'niftycube.js' and 'BuilderHelper.js' files are added to page
		Script init = new Script();
		init.addScriptLine("roundModulesListCorners();");
		this.add(init);
	}
	
	@SuppressWarnings("unchecked")
	private void addListToWindow(List<ICObject> objects, String name, String id, ListItem container) {
		Heading3 header = new Heading3(name);
		container.add(header);
		
		Layer content = new Layer();
//		Lists items = new Lists();
//		String itemStyleClass = "modulesListItemStyle";
//		ListItem item = new ListItem();
//		Link l = null;
		Paragraph p = null;
		String actionDefinition = "onclick";
		ICObject object = null;
		for (int i = 0; i < objects.size(); i++) {
			object = objects.get(i);
//			item = new ListItem();
//			item.addText(object.getName());
			//item.setStyleClass(itemStyleClass);
//			item.attributes.put(actionDefinition, new StringBuffer("addSelectedModule(").append(object.getID()).append(");").toString());
//			items.add(item);
			p = new Paragraph();
			p.add(object.getName());
			if (p.attributes == null) {
				p.attributes = new HashMap();
			}
			p.attributes.put(actionDefinition, new StringBuffer("addSelectedModule(").append(object.getID()).append(");").toString());
			content.add(p);
		}
//		content.add(items);
		container.add(content);
	}
	
	/*private void addListToWindow(List<ICObject> objects, String name, String id) {
		Layer container = new Layer();
		container.setStyleClass("modulesListContainerStyle");
		container.setId(id);
		this.add(container);
		
		if (objects == null) {
			return;
		}
		container.add(name);
		ICObject object = null;
		Lists items = new Lists();
		items.setStyleClass("modulesListStyle");
		String itemStyleClass = "modulesListItemStyle";
		ListItem item = new ListItem();
		String actionDefinition = "onclick";
		for (int i = 0; i < objects.size(); i++) {
			object = objects.get(i);
			item = new ListItem();
			item.addText(object.getName());
			item.setStyleClass(itemStyleClass);
			item.attributes.put(actionDefinition, new StringBuffer("addSelectedModule(").append(object.getID()).append(");").toString());
			items.add(item);
		}
		container.add(items);
	}*/
	
	@SuppressWarnings("unchecked")
	private List<ICObject> getConcreteComponents(IWContext iwc, Collection allComponents, boolean findWidgets, boolean findBlocks, boolean findBuilder) {
		List<ICObject> components = new ArrayList<ICObject>();

		if (allComponents == null) {
			return components;
		}
		
		Object o = null;
		ICObject object = null;
		//	Find "widget" type modules
		if (findWidgets) {
			for (Iterator it = allComponents.iterator(); it.hasNext(); ) {
				o = it.next();
				if (o instanceof ICObject) {
					object = (ICObject) o;
					if (object.isWidget()) {
						components.add(object);
					}
				}
			}
			return components;
		}
		
		//	Find "block" type modules
		if (findBlocks) {
			for (Iterator it = allComponents.iterator(); it.hasNext(); ) {
				o = it.next();
				if (o instanceof ICObject) {
					object = (ICObject) o;
					if (object.isBlock()) {
						components.add(object);
					}
				}
			}
			return components;
		}
		
		//	Find all other Builder/Development modules
		for (Iterator it = allComponents.iterator(); it.hasNext(); ) {
			o = it.next();
			if (o instanceof ICObject) {
				object = (ICObject) o;
				if (!object.isBlock() && !object.isWidget()) {
					if (ICObjectBMPBean.COMPONENT_TYPE_ELEMENT.equals(object.getObjectType())) {
						components.add(object);
					}
					if (ICObjectBMPBean.COMPONENT_TYPE_BLOCK.equals(object.getObjectType())) {
						components.add(object);
					}
					if (ICObjectBMPBean.COMPONENT_TYPE_JSFUICOMPONENT.equals(object.getObjectType())) {
						components.add(object);
					}
				}
			}
		}
		if (components != null) {
			ModuleComparator comparator = new ModuleComparator(iwc);
			java.util.Collections.sort(components, comparator);
		}
		return components;
	}
	
	private Collection getAllComponents() {
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
