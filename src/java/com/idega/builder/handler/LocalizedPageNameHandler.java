/*
 * $Id:$
 *
 * Copyright (C) 2002 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package com.idega.builder.handler;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;

import com.idega.builder.business.BuilderLogic;
import com.idega.builder.business.IBPageUpdater;
import com.idega.builder.business.PageTreeNode;
import com.idega.core.localisation.business.ICLocaleBusiness;
import com.idega.core.localisation.presentation.LocalePresentationUtil;
import com.idega.presentation.IWContext;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.Table;
import com.idega.presentation.ui.DropdownMenu;
import com.idega.presentation.ui.TextInput;

/**
 * This class does something very clever.....
 * 
 * @author <a href="palli@idega.is">Pall Helgason</a>
 * @version 1.0
 */
public class LocalizedPageNameHandler implements PropertyHandler {
	public LocalizedPageNameHandler() {
		
	}
	
	/**
	 * @see com.idega.builder.handler.PropertyHandler#getDefaultHandlerTypes()
	 */
	public List getDefaultHandlerTypes() {
		return null;
	}

	/**
	 * @see com.idega.builder.handler.PropertyHandler#getHandlerObject(String, String, IWContext)
	 */
	public PresentationObject getHandlerObject(String name, String stringValue, IWContext iwc) {
		System.out.println("Entering LocalizedPageNameHandler.getHandlerObject");
		Table t = new Table(1,2);
		
		StringTokenizer tok = new StringTokenizer(stringValue,";");
		String menuString = null;
		if (tok.hasMoreElements())
			menuString = (String)tok.nextElement();
		String titleString = null;
		if (tok.hasMoreElements())
			titleString = (String)tok.nextElement();
		
    DropdownMenu menu = LocalePresentationUtil.getAvailableLocalesDropdown(iwc.getIWMainApplication(),name);
    if (menuString != null)
	    menu.setSelectedElement(menuString);
    
    TextInput title = new TextInput(name+"a");
    if (titleString != null)
	    title.setValue(titleString);
    
    t.add(menu,1,1);
    t.add(title,1,2);
    return t;
	}

	/**
	 * @see com.idega.builder.handler.PropertyHandler#onUpdate(String[], IWContext)
	 */
	public void onUpdate(String[] values, IWContext iwc) {
		if (values != null) {
			String value = values[0];
			String locale = null;
			String name = null;
			StringTokenizer tok = new StringTokenizer(value,";");
			if (tok.hasMoreElements())
				locale = (String)tok.nextElement();
			if (tok.hasMoreElements())
				name = (String)tok.nextElement();

			Locale loc = ICLocaleBusiness.getLocaleFromLocaleString(locale);
			
			if (locale != null && name != null && !locale.equals("") && !name.equals("")) {
				String currPage = BuilderLogic.getInstance().getCurrentIBPage(iwc);

				if (currPage != null) {
					Map tree = PageTreeNode.getTree(iwc);

					Integer i = new Integer(currPage);

					PageTreeNode node = (PageTreeNode) tree.get(i);
					node.setLocalizedNodeName(locale,name,iwc);
					IBPageUpdater.addLocalizedPageName(i.intValue(),ICLocaleBusiness.getLocaleId(loc),name);
				}
			}
		}		
	}
}