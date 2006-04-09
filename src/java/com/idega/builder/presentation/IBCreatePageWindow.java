/*
 * $Id: IBCreatePageWindow.java,v 1.46 2006/04/09 11:43:34 laddi Exp $
 *
 * Copyright (C) 2001-2004 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package com.idega.builder.presentation;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import com.idega.builder.business.BuilderLogic;
import com.idega.builder.business.IBPageHelper;
import com.idega.builder.business.CachedBuilderPage;
import com.idega.builder.business.PageTreeNode;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWConstants;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.IWContext;
import com.idega.presentation.Table;
import com.idega.presentation.text.Text;
import com.idega.presentation.ui.CheckBox;
import com.idega.presentation.ui.CloseButton;
import com.idega.presentation.ui.DropdownMenu;
import com.idega.presentation.ui.Form;
import com.idega.presentation.ui.SubmitButton;
import com.idega.presentation.ui.TextInput;

/**
 * The widow for creating a page.
 * 
 * @author <a href="mailto:palli@idega.is">Pall Helgason</a>,
 * <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
*/
public class IBCreatePageWindow extends IBPageWindow {

	private static final String TOP_LEVEL = "top_level";

	public IBCreatePageWindow() {
		super();
	}

	public void main(IWContext iwc) throws Exception {
		IWBundle iwb = getBundle(iwc);
		IWResourceBundle iwrb = getResourceBundle(iwc);
		boolean allowMultiplePageCreation = Boolean.valueOf(iwb.getProperty("allow_multiple_page_creation",Boolean.toString(false))).booleanValue();
		Form form = new Form();
		String type = iwc.getParameter(PAGE_TYPE);
		String topLevelString = iwc.getParameter(TOP_LEVEL);

		if (type == null) {
			String currPageType = BuilderLogic.getInstance().getCurrentIBPageEntity(iwc).getType();
			if (currPageType.equals(CachedBuilderPage.TYPE_TEMPLATE)) {
				type = IBPageHelper.TEMPLATE;
			}
			else {
				type = IBPageHelper.PAGE;
			}
		}

		if (type.equals(IBPageHelper.TEMPLATE)) {
			setTitle(iwrb.getLocalizedString("create_new_template", "Create a new Template"));
			addTitle(iwrb.getLocalizedString("create_new_template", "Create a new Template"), IWConstants.BUILDER_FONT_STYLE_TITLE);
		}
		else {
			setTitle(iwrb.getLocalizedString("create_new_page", "Create a new Page"));
			addTitle(iwrb.getLocalizedString("create_new_page", "Create a new Page"), IWConstants.BUILDER_FONT_STYLE_TITLE);
		}

		add(form);
		Table tab = new Table(2,7);
		tab.setColumnAlignment(1, "right");
		tab.setWidth(1, "110");
		tab.setCellspacing(3);
		tab.setAlignment(2, 7, "right");
		form.add(tab);
		TextInput inputName = new TextInput(PAGE_NAME_PARAMETER);
		inputName.setStyleAttribute(IWConstants.BUILDER_FONT_STYLE_INTERFACE);
		inputName.setAsNotEmpty(iwrb.getLocalizedString("must_supply_name", "Must supply a name"));
		Text inputText = new Text();
		if (type.equals(IBPageHelper.TEMPLATE)) {
			inputText.setText(iwrb.getLocalizedString("template_name", "Template name") + ":");
		}
		else {
			inputText.setText(iwrb.getLocalizedString("page_name", "Page name") + ":");
		}
		inputText.setFontStyle(IWConstants.BUILDER_FONT_STYLE_LARGE);
		int row =1;
		tab.add(inputText, 1, row);
		tab.add(inputName, 2, row++);
		if(allowMultiplePageCreation){
			String delimit = iwrb.getLocalizedString("delimit_with", "Delimit with");
			String tocreate = iwrb.getLocalizedString("to_create_multiple_pages","to create multiple pages");
			Text multiText = new Text(delimit+" ; "+tocreate);
			multiText.setFontStyle(IWConstants.BUILDER_FONT_STYLE_SMALL);
			tab.add(multiText,2,row++);
		}
		DropdownMenu mnu = new DropdownMenu(PAGE_TYPE);
		mnu.addMenuElement(IBPageHelper.PAGE, "Page");
		mnu.addMenuElement(IBPageHelper.TEMPLATE, "Template");
		mnu.setSelectedElement(type);
		mnu.setStyleAttribute(IWConstants.BUILDER_FONT_STYLE_INTERFACE);

		Text typeText = new Text(iwrb.getLocalizedString("select_type", "Select type") + ":");
		typeText.setFontStyle(IWConstants.BUILDER_FONT_STYLE_LARGE);
		tab.add(typeText, 1, row);
		tab.add(mnu, 2, row++);

		mnu.setToSubmit();

		CheckBox topLevel = new CheckBox(TOP_LEVEL);
		Text topLevelText = new Text(iwrb.getLocalizedString(TOP_LEVEL, "Top level") + ":");
		topLevelText.setFontStyle(IWConstants.BUILDER_FONT_STYLE_LARGE);
		tab.add(topLevelText, 1, row);
		tab.add(topLevel, 2, row++);
		topLevel.setOnClick("this.form.submit()");

		IBPageChooser pageChooser = getPageChooser(PAGE_CHOOSER_NAME, iwc);

		if (!type.equals(IBPageHelper.TEMPLATE)) {
			if (topLevelString == null) {
				Text createUnderText = new Text(iwrb.getLocalizedString("parent_page", "Create page under") + ":");
				createUnderText.setFontStyle(IWConstants.BUILDER_FONT_STYLE_LARGE);
				tab.add(createUnderText, 1, row);
				tab.add(pageChooser, 2, row++);
			}
		}

		if ((topLevelString == null) || (topLevelString != null && type.equals(IBPageHelper.PAGE))) {
			Text usingText = new Text(iwrb.getLocalizedString("using_template", "Using template") + ":");
			usingText.setFontStyle(IWConstants.BUILDER_FONT_STYLE_LARGE);
			tab.add(usingText, 1, row);
			IBTemplateChooser templateChooser = getTemplateChooser(TEMPLATE_CHOOSER_NAME, iwc, type);
			tab.add(templateChooser, 2, row++);
		}
		
		Text usingText = new Text(iwrb.getLocalizedString("with_format", "With format") + ":");
		usingText.setFontStyle(IWConstants.BUILDER_FONT_STYLE_LARGE);
		tab.add(usingText, 1, row);
		DropdownMenu formatsMenu = new DropdownMenu(PAGE_FORMAT);
		Map formats = BuilderLogic.getInstance().getPageFormatsSupportedAndDescription();
		Set keySet = formats.keySet();
		for (Iterator iter = keySet.iterator(); iter.hasNext();) {
			String format = (String) iter.next();
			String description = (String) formats.get(format);
			formatsMenu.addMenuElement(format,description);
		}
		formatsMenu.setSelectedElement(BuilderLogic.getInstance().PAGE_FORMAT_IBXML);
		tab.add(formatsMenu, 2, row++);

		SubmitButton button = new SubmitButton(iwrb.getLocalizedImageButton("save", "Save"), "submit");
		CloseButton close = new CloseButton(iwrb.getLocalizedImageButton("close", "Close"));
		tab.add(close, 2, row);
		tab.add(Text.getNonBrakingSpace(), 2, row);
		tab.add(button, 2, row++);

		boolean submit = iwc.isParameterSet("submit");

		if (submit) {
			String parentPageId = iwc.getParameter(PAGE_CHOOSER_NAME);
			String name = iwc.getParameter(PAGE_NAME_PARAMETER);
			type = iwc.getParameter(PAGE_TYPE);
			String templateId = iwc.getParameter(TEMPLATE_CHOOSER_NAME);
			String format = iwc.getParameter(PAGE_FORMAT);
			if (type.equals(IBPageHelper.TEMPLATE)) {
				parentPageId = templateId;
			//      String topLevelString = iwc.getParameter(TOP_LEVEL);
			}

			Map tree = PageTreeNode.getTree(iwc);
			int id = -1;
			// create multiple pages
			if(!allowMultiplePageCreation){
				id = createPage(iwc, type, topLevelString, parentPageId, name, templateId, tree, id,format);
			}
			else{
				StringTokenizer tokener = new StringTokenizer(name,";");
				while(tokener.hasMoreTokens()){
					name = tokener.nextToken();
					id = createPage(iwc, type, topLevelString, parentPageId, name, templateId, tree, id,format);
				} // loop ends
			}

			BuilderLogic.getInstance().setCurrentIBPage(iwc,Integer.toString(id));
			setOnUnLoad("window.opener.parent.parent.location.reload()");
		}
		else {
			String name = iwc.getParameter(PAGE_NAME_PARAMETER);
			type = iwc.getParameter(PAGE_TYPE);

			if (topLevelString != null) {
				topLevel.setChecked(true);
			}

			if (name != null) {
				inputName.setValue(name);
			}

			if (type != null) {
				mnu.setSelectedElement(type);
			}
		}
	}

	private int createPage(IWContext iwc, String type, String topLevelString, String parentPageId, String name, String templateId, Map tree, int id,String format) {
		int domainId = -1;
		if (topLevelString == null) {
			if (parentPageId != null) {
				domainId =-1;
				//id = IBPageHelper.getInstance().createNewPage(parentPageId, name, type, templateId, tree, iwc,null,);
			}
		}
		else {
			domainId = BuilderLogic.getInstance().getCurrentDomain(iwc).getID();
			parentPageId=null;
			tree=null;
			if (type.equals(IBPageHelper.TEMPLATE)) {
				//id = IBPageHelper.getInstance().createNewPage(null, name, type, null, tree, iwc, null, domainId);
				templateId=null;
			}
			else {
				//id = IBPageHelper.getInstance().createNewPage(null, name, type, templateId, tree, iwc, null, domainId);
			}
		}
		String sourceMarkup=null;
		String pageUri = null;
		String subType=null;
		id = IBPageHelper.getInstance().createNewPage(parentPageId, name, type, templateId, pageUri, tree, iwc, subType, domainId,format,sourceMarkup);
		return id;
		
	}

}