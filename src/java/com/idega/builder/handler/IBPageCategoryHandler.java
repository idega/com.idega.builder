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
import com.idega.util.CoreConstants;

/**
 * @author laddi
 */
public class IBPageCategoryHandler implements ICPropertyHandler {

	/* (non-Javadoc)
	 * @see com.idega.builder.handler.ICPropertyHandler#getDefaultHandlerTypes()
	 */
	@Override
	public List getDefaultHandlerTypes() {
		return null;
	}

	/* (non-Javadoc)
	 * @see com.idega.builder.handler.ICPropertyHandler#getHandlerObject(java.lang.String, java.lang.String, com.idega.presentation.IWContext)
	 */
	@Override
	public PresentationObject getHandlerObject(String name, String stringValue, IWContext iwc, boolean oldGenerationHandler, String instanceId, String method) {
		BooleanInput input = new BooleanInput(name);
		input.setSelectedElement(stringValue);

		return input;
	}

	@Override
	public void onUpdate(String[] values, IWContext iwc) {
		if (values != null) {
			String value = values[0];
			boolean isCategory = false;
			if (value.equalsIgnoreCase(CoreConstants.Y)) {
				isCategory = true;
			}

			int currentPage = BuilderLogic.getInstance().getCurrentIBPageID(iwc);
			if (currentPage != -1) {
				IBPageUpdater.setAsCategory(currentPage, isCategory);
			}
		}
	}
}