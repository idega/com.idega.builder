/*
 * $Id: IBLockRegionWindow.java,v 1.10 2004/06/24 20:12:24 tryggvil Exp $
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
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWConstants;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.IWContext;
import com.idega.presentation.Image;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.Table;
import com.idega.presentation.text.Link;
import com.idega.presentation.text.Text;
import com.idega.presentation.ui.Form;
import com.idega.presentation.ui.SubmitButton;
import com.idega.presentation.ui.TextInput;
/**

 * @author <a href="mailto:palli@idega.is">Pall Helgason</a>

 * @version 1.0

 */
public class IBLockRegionWindow extends IBAdminWindow {
	private static final String LABEL_PARAMETER = "label_name";
	private static final String SUBMIT_PARAMETER = "submit";

	private static final String IB_PARENT_PARAMETER = BuilderLogic.IB_PARENT_PARAMETER;
	private static final String IB_PAGE_PARAMETER = BuilderConstants.IB_PAGE_PARAMETER;
	private static final String IB_CONTROL_PARAMETER = BuilderLogic.IB_CONTROL_PARAMETER;
	private static final String ACTION_LOCK = BuilderLogic.ACTION_LOCK_REGION;
	private static final String ACTION_UNLOCK = BuilderLogic.ACTION_UNLOCK_REGION;
	private static final String IW_BUNDLE_IDENTIFIER = BuilderLogic.IW_BUNDLE_IDENTIFIER;
	private static final String IB_LABEL_PARAMETER = BuilderLogic.IB_LABEL_PARAMETER;

	public IBLockRegionWindow() {
		setWidth(250);
		setHeight(100);
		setScrollbar(false);
		setResizable(false);
	}

	public void main(IWContext iwc) throws Exception {
		IWResourceBundle iwrb = getBundle(iwc).getResourceBundle(iwc);
		setTitle(iwrb.getLocalizedString("lock_region_window", "Lock region window"));
		addTitle(iwrb.getLocalizedString("lock_region","Lock region"));
		
		String ib_parent_id = iwc.getParameter(IB_PARENT_PARAMETER);
		String ib_page_id = iwc.getParameter(IB_PAGE_PARAMETER);
		String action = iwc.getParameter(IB_CONTROL_PARAMETER);
		String label = iwc.getParameter(IB_LABEL_PARAMETER);
		
		if (action.equalsIgnoreCase(ACTION_LOCK)) {
			lockRegion(ib_page_id, ib_parent_id);
			setParentToReload();
			close();
		}
		
		else if (action.equalsIgnoreCase(ACTION_UNLOCK)) {
			if (!ib_parent_id.equals("-1")) {
				
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

				Text text = new Text(iwrb.getLocalizedString("label", "Label")+":");
				text.setFontStyle(IWConstants.BUILDER_FONT_STYLE_LARGE);
				tab.add(text, 1, 1);
				
				TextInput inputName = new TextInput(LABEL_PARAMETER);
				inputName.setStyleAttribute(IWConstants.BUILDER_FONT_STYLE_INTERFACE_SMALL);
				inputName.setAsNotEmpty(iwrb.getLocalizedString("can_not_be_empty", "Must supply a label name"));
				inputName.setLength(24);
				tab.add(inputName, 2, 1);

				if (label != null)
					inputName.setValue(label);

				SubmitButton button = new SubmitButton(iwrb.getLocalizedString("save", "Save"));
				button.setAsImageButton(true);
				tab.add(button, 2, 2);
				
				if (iwc.isParameterSet(SUBMIT_PARAMETER)) {
					label = iwc.getParameter(LABEL_PARAMETER);
					unlockRegion(ib_page_id, ib_parent_id, label);
					setParentToReload();
					close();
				}
			}
			else {
				unlockRegion(ib_page_id, ib_parent_id, null);
				setParentToReload();
				close();
			}
		}
	}

	private void lockRegion(String pageKey, String parentID) throws Exception {
		BuilderLogic.getInstance().lockRegion(pageKey, parentID);
	}

	private void unlockRegion(String pageKey, String parentID, String label) throws Exception {
		BuilderLogic.getInstance().unlockRegion(pageKey, parentID, label);
	}

	public String getBundleIdentifier() {
		return (IW_BUNDLE_IDENTIFIER);
	}

	/**
	 *
	 */
	public static PresentationObject getLockedIcon(String parentKey, IWContext iwc, String label)
	{
		IWBundle bundle = iwc.getIWMainApplication().getBundle(BuilderLogic.IW_BUNDLE_IDENTIFIER);
		Image lockImage = bundle.getImage("las_close.gif", "Unlock region");
		Link link = new Link(lockImage);
		link.setWindowToOpen(IBLockRegionWindow.class);
		link.addParameter(BuilderConstants.IB_PAGE_PARAMETER, BuilderLogic.getCurrentIBPage(iwc));
		link.addParameter(BuilderLogic.IB_CONTROL_PARAMETER, BuilderLogic.ACTION_UNLOCK_REGION);
		link.addParameter(BuilderLogic.IB_PARENT_PARAMETER, parentKey);
		link.addParameter(BuilderLogic.IB_LABEL_PARAMETER, label);
		return (link);
	}

	/**
	 *
	 */
	public static PresentationObject getUnlockedIcon(String parentKey, IWContext iwc)
	{
		IWBundle bundle = iwc.getIWMainApplication().getBundle(BuilderLogic.IW_BUNDLE_IDENTIFIER);
		Image lockImage = bundle.getImage("las_open.gif", "Lock region");
		Link link = new Link(lockImage);
		link.setWindowToOpen(IBLockRegionWindow.class);
		link.addParameter(BuilderConstants.IB_PAGE_PARAMETER, BuilderLogic.getCurrentIBPage(iwc));
		link.addParameter(BuilderLogic.IB_CONTROL_PARAMETER, BuilderLogic.ACTION_LOCK_REGION);
		link.addParameter(BuilderLogic.IB_PARENT_PARAMETER, parentKey);
		return (link);
	}
}
