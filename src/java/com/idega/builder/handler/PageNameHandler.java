/*
 * $Id: PageNameHandler.java,v 1.5 2004/06/28 11:18:12 thomas Exp $
 *
 * Copyright (C) 2001 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package com.idega.builder.handler;

import com.idega.builder.business.BuilderLogic;
import com.idega.builder.business.PageTreeNode;
import com.idega.builder.business.IBPageUpdater;
import com.idega.core.builder.data.ICPropertyHandler;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.IWContext;
import com.idega.presentation.ui.TextInput;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mail:palli@idega.is">Pall Helgason</a>
 * @version 1.0
 */
public class PageNameHandler implements ICPropertyHandler {
	public PageNameHandler() {
	}

	public List getDefaultHandlerTypes() {
		return (null);
	}

	public PresentationObject getHandlerObject(String name, String stringValue, IWContext iwc) {
		TextInput input = new TextInput(name);
		input.setValue(stringValue);

		return (input);
	}

	public void onUpdate(String values[], IWContext iwc) {
		if (values != null) {
			String value = values[0];

			if (value != null) {
				String currPage = BuilderLogic.getInstance().getCurrentIBPage(iwc);

				if (currPage != null) {
					Map tree = PageTreeNode.getTree(iwc);

					Integer i = new Integer(currPage);

					PageTreeNode node = (PageTreeNode) tree.get(i);
					node.setNodeName(value);
					IBPageUpdater.updatePageName(i.intValue(), value);
				}
			}
		}
	}
}