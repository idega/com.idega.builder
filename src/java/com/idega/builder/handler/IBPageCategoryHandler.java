/*
 * Created on 17.9.2003
 */
package com.idega.builder.handler;

import java.util.List;

import com.idega.builder.business.BuilderLogic;
import com.idega.builder.business.IBPageUpdater;
import com.idega.core.builder.presentation.ICPropertyHandler;
import com.idega.presentation.IWContext;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.ui.BooleanInput;

/**
 * @author laddi
 */
public class IBPageCategoryHandler implements ICPropertyHandler {

	/* (non-Javadoc)
	 * @see com.idega.builder.handler.ICPropertyHandler#getDefaultHandlerTypes()
	 */
	public List getDefaultHandlerTypes() {
		return null;
	}

	/* (non-Javadoc)
	 * @see com.idega.builder.handler.ICPropertyHandler#getHandlerObject(java.lang.String, java.lang.String, com.idega.presentation.IWContext)
	 */
	public PresentationObject getHandlerObject(String name, String stringValue, IWContext iwc) {
		BooleanInput input = new BooleanInput(name);
		input.setSelectedElement(stringValue);
		
		return input;
	}

	public void onUpdate(String[] values, IWContext iwc) {
		if (values != null) {
			String value = values[0];
			boolean isCategory = false;
			if (value.equalsIgnoreCase("Y"))
				isCategory = true;
			
			int currentPage = BuilderLogic.getInstance().getCurrentIBPageID(iwc);
			if (currentPage != -1) {
				IBPageUpdater.setAsCategory(currentPage, isCategory);
			}
		}		
	}
}