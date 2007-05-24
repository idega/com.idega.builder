/*
 * $Id: DropdownMenuHandler.java,v 1.5 2007/05/24 11:31:12 valdas Exp $
 *
 * Copyright (C) 2002 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package com.idega.builder.handler;

import java.util.List;

import com.idega.core.builder.presentation.ICPropertyHandler;
import com.idega.presentation.IWContext;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.ui.TextArea;


/**
 * This class does something very clever.....
 * 
 * @author palli
 * @version 1.0
 */
public class DropdownMenuHandler implements ICPropertyHandler {
	/**
	 * @see com.idega.core.builder.presentation.ICPropertyHandler#getDefaultHandlerTypes()
	 */
	public List getDefaultHandlerTypes() {
		return null;
	}

	/**
	 * @see com.idega.core.builder.presentation.ICPropertyHandler#getHandlerObject(String, String, IWContext)
	 */
	public PresentationObject getHandlerObject(String name, String stringValue, IWContext iwc, boolean oldGenerationHandler, String instanceId, String method) {		
		TextArea area = new TextArea();
		area.setName(name);
		area.setValue(stringValue);
		area.setHeight(7);
		
		return area;
	}

	/**
	 * @see com.idega.core.builder.presentation.ICPropertyHandler#onUpdate(String[], IWContext)
	 */
	public void onUpdate(String[] values, IWContext iwc) {
	}
}
