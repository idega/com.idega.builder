package com.idega.builder.handler;

import java.util.List;

import com.idega.core.builder.presentation.ICPropertyHandler;
import com.idega.presentation.IWContext;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.ui.PasswordInput;

public class PasswordInputHandler implements ICPropertyHandler {

	public List getDefaultHandlerTypes() {
		return null;
	}

	public PresentationObject getHandlerObject(String name, String stringValue, IWContext iwc, boolean oldGenerationHandler,
			String instanceId, String method) {
		PasswordInput input = new PasswordInput(name);
		if (stringValue != null) {
			input.setContent(stringValue);
		}
		return input;
	}

	public void onUpdate(String[] values, IWContext iwc) {
	}

}
