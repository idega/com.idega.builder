/*
 * $Id: DropdownMenuHandler.java,v 1.1 2002/07/22 10:10:41 palli Exp $
 *
 * Copyright (C) 2002 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package com.idega.builder.handler;

import java.util.List;

import com.idega.presentation.IWContext;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.ui.TextArea;


/**
 * This class does something very clever.....
 * 
 * @author palli
 * @version 1.0
 */
public class DropdownMenuHandler implements PropertyHandler {
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
		TextArea area = new TextArea();
		area.setName(name);
		area.setValue(stringValue);
		area.setHeight(7);
		
		return area;
	}

	/**
	 * @see com.idega.builder.handler.PropertyHandler#onUpdate(String[], IWContext)
	 */
	public void onUpdate(String[] values, IWContext iwc) {
	}
}
