/*
 * $Id: IBAddRegionLabelWindow.java,v 1.8 2009/04/27 14:52:25 valdas Exp $
 *
 * Copyright (C) 2001 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package com.idega.builder.presentation;

import com.idega.builder.business.BuilderLogic;
import com.idega.builder.business.BuilderConstants;
import com.idega.core.builder.business.ICBuilderConstants;
import com.idega.idegaweb.IWConstants;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.IWContext;
import com.idega.presentation.Table;
import com.idega.presentation.text.Text;
import com.idega.presentation.ui.Form;
import com.idega.presentation.ui.SubmitButton;
import com.idega.presentation.ui.TextInput;

/**
 * @author <a href="mailto:palli@idega.is">Pall Helgason</a>
 * @version 1.0
 */

public class IBAddRegionLabelWindow extends IBAdminWindow {
	private static final String LABEL_PARAMETER = "label_name";
	private static final String SUBMIT_PARAMETER = "submit";

	private static final String IB_PARENT_PARAMETER = BuilderLogic.IB_PARENT_PARAMETER;
	private static final String IB_PAGE_PARAMETER = BuilderConstants.IB_PAGE_PARAMETER;
	private static final String IB_CONTROL_PARAMETER = ICBuilderConstants.IB_CONTROL_PARAMETER;
	private static final String ACTION_LABEL = BuilderLogic.ACTION_LABEL;
	private static final String IB_LABEL_PARAMETER = BuilderLogic.IB_LABEL_PARAMETER;

	public IBAddRegionLabelWindow() {
		setWidth(250);
		setHeight(100);
		setScrollbar(false);
		setResizable(false);
	}

	/**
	 * @see com.idega.presentation.PresentationObject#main(IWContext)
	 */
	@Override
	public void main(IWContext iwc) throws Exception {
		IWResourceBundle iwrb = getBundle(iwc).getResourceBundle(iwc);
		addTitle(iwrb.getLocalizedString("add_region_label", "Add region label"));
		setTitle(iwrb.getLocalizedString("region_label", "Put label on region"));

		String ib_parent_id = iwc.getParameter(IB_PARENT_PARAMETER);
		String ib_page_id = iwc.getParameter(IB_PAGE_PARAMETER);
		String action = iwc.getParameter(IB_CONTROL_PARAMETER);
		String label = iwc.getParameter(IB_LABEL_PARAMETER);

		if (action.equalsIgnoreCase(ACTION_LABEL)) {
			if (iwc.isParameterSet(SUBMIT_PARAMETER)) {
				label = iwc.getParameter(LABEL_PARAMETER);
				BuilderLogic.getInstance().labelRegion(ib_page_id, ib_parent_id, label);

				setParentToReload();
				close();
			}
			else {
				Form form = new Form();
				form.addParameter(IB_PARENT_PARAMETER, ib_parent_id);
				form.addParameter(IB_PAGE_PARAMETER, ib_page_id);
				form.addParameter(IB_CONTROL_PARAMETER, action);
				form.addParameter(SUBMIT_PARAMETER, "true");
				add(form);

				Table outer = new Table();
				outer.setWidth(Table.HUNDRED_PERCENT);
				outer.setHeight(Table.HUNDRED_PERCENT);
				outer.setAlignment(1, 1, Table.HORIZONTAL_ALIGN_CENTER);
				form.add(outer);
				
				Table tab = new Table(2, 2);
				tab.setCellpadding(5);
				tab.setAlignment(1, 1, Table.HORIZONTAL_ALIGN_RIGHT);
				tab.setAlignment(2, 2, Table.HORIZONTAL_ALIGN_RIGHT);
				outer.add(tab);

				Text text = new Text(iwrb.getLocalizedString("label", "Label"));
				text.setFontStyle(IWConstants.BUILDER_FONT_STYLE_LARGE);
				tab.add(text, 1, 1);
				
				TextInput inputName = new TextInput(LABEL_PARAMETER);
				inputName.setStyleAttribute(IWConstants.BUILDER_FONT_STYLE_INTERFACE_SMALL);
				inputName.setAsNotEmpty(iwrb.getLocalizedString("can_not_be_empty", "Must supply a label name"));
				inputName.setLength(24);
				tab.add(inputName, 2, 1);

				if (label != null) {
					inputName.setValue(label);
				}

				SubmitButton button = new SubmitButton(iwrb.getLocalizedString("save", "Save"));
				button.setAsImageButton(true);
				tab.add(button, 2, 2);
			}
		}
	}

	/**
	 * @see com.idega.presentation.PresentationObject#getBundleIdentifier()
	 */
	@Override
	public String getBundleIdentifier() {
		return BuilderConstants.IW_BUNDLE_IDENTIFIER;
	}
}