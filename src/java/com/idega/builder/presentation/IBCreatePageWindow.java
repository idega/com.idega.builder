/*
 * $Id: IBCreatePageWindow.java,v 1.30 2002/04/03 12:44:55 palli Exp $
 *
 * Copyright (C) 2001 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package com.idega.builder.presentation;

import com.idega.idegaweb.IWConstants;
import com.idega.builder.business.IBPropertyHandler;
import com.idega.builder.business.IBXMLPage;
import com.idega.builder.business.BuilderLogic;
import com.idega.builder.business.PageTreeNode;
import com.idega.builder.business.IBPageHelper;
import com.idega.builder.data.IBPage;
import com.idega.builder.data.IBDomain;
import com.idega.presentation.IWContext;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.Table;
import com.idega.presentation.Page;
import com.idega.presentation.text.Text;
import com.idega.presentation.ui.Form;
import com.idega.presentation.ui.TextInput;
import com.idega.presentation.ui.SubmitButton;
import com.idega.presentation.ui.RadioGroup;
import com.idega.presentation.ui.DropdownMenu;
import com.idega.presentation.ui.Window;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.idegaweb.presentation.IWAdminWindow;
import java.util.List;
import java.util.Iterator;
import java.util.Map;

/**
 * @author <a href="mailto:palli@idega.is">Pall Helgason</a>
 * @version 1.0
*/
public class IBCreatePageWindow extends IWAdminWindow {
  private static final String PAGE_NAME_PARAMETER   = "ib_page_name";
  private static final String PAGE_CHOOSER_NAME     = IBPropertyHandler.PAGE_CHOOSER_NAME;
  private static final String TEMPLATE_CHOOSER_NAME = IBPropertyHandler.TEMPLATE_CHOOSER_NAME;
  private static final String PAGE_TYPE             = "ib_page_type";
  private static final String IW_BUNDLE_IDENTIFIER  = "com.idega.builder";

  public IBCreatePageWindow() {
    setWidth(280);
    setHeight(160);
    setScrollbar(false);
  }

  public void main(IWContext iwc) throws Exception {
    IWResourceBundle iwrb = iwc.getApplication().getBundle(BuilderLogic.IW_BUNDLE_IDENTIFIER).getResourceBundle(iwc);
    Form form = new Form();
    String type = iwc.getParameter(PAGE_TYPE);
    if (type == null) {
      String currPageType = BuilderLogic.getInstance().getCurrentIBXMLPage(iwc).getType();
      if (currPageType.equals(IBXMLPage.TYPE_TEMPLATE))
      	type = IBPageHelper.TEMPLATE;
      else
      	type = IBPageHelper.PAGE;
    }

    if (type.equals(IBPageHelper.TEMPLATE)) {
      setTitle(iwrb.getLocalizedString("create_new_template","Create a new Template"));
      addTitle(iwrb.getLocalizedString("create_new_template","Create a new Template"),IWConstants.BUILDER_FONT_STYLE_TITLE);
    }
    else {
      setTitle(iwrb.getLocalizedString("create_new_page","Create a new Page"));
      addTitle(iwrb.getLocalizedString("create_new_page","Create a new Page"),IWConstants.BUILDER_FONT_STYLE_TITLE);
    }

    add(form);
    Table tab = new Table(2,5);
    tab.setColumnAlignment(1,"right");
    tab.setWidth(1,"110");
    tab.setCellspacing(3);
    tab.setAlignment(2,5,"right");
    form.add(tab);
    TextInput inputName = new TextInput(PAGE_NAME_PARAMETER);
    inputName.setStyleAttribute(IWConstants.BUILDER_FONT_STYLE_INTERFACE);
    Text inputText = new Text();
    if (type.equals(IBPageHelper.TEMPLATE)) {
      inputText.setText(iwrb.getLocalizedString("template_name","Template name")+":");
    }
    else {
      inputText.setText(iwrb.getLocalizedString("page_name","Page name")+":");
    }
    inputText.setFontStyle(IWConstants.BUILDER_FONT_STYLE_LARGE);
    tab.add(inputText,1,1);
    tab.add(inputName,2,1);

    DropdownMenu mnu = new DropdownMenu(PAGE_TYPE);
    mnu.addMenuElement(IBPageHelper.PAGE,"Page");
    mnu.addMenuElement(IBPageHelper.TEMPLATE,"Template");
    mnu.setSelectedElement(type);
    mnu.setStyleAttribute(IWConstants.BUILDER_FONT_STYLE_INTERFACE);

    Text typeText = new Text(iwrb.getLocalizedString("select_type","Select type")+":");
    typeText.setFontStyle(IWConstants.BUILDER_FONT_STYLE_LARGE);
    tab.add(typeText,1,2);
    tab.add(mnu,2,2);

    mnu.setToSubmit();

    if (!type.equals(IBPageHelper.TEMPLATE)) {
      Text createUnderText = new Text(iwrb.getLocalizedString("parent_page","Create page under")+":");
	    createUnderText.setFontStyle(IWConstants.BUILDER_FONT_STYLE_LARGE);
      tab.add(createUnderText,1,3);
      tab.add(getPageChooser(PAGE_CHOOSER_NAME,iwc),2,3);
    }

    Text usingText = new Text(iwrb.getLocalizedString("using_template","Using template")+":");
    usingText.setFontStyle(IWConstants.BUILDER_FONT_STYLE_LARGE);
    tab.add(usingText,1,4);
    tab.add(getTemplateChooser(TEMPLATE_CHOOSER_NAME,iwc,type),2,4);

    SubmitButton button = new SubmitButton(iwrb.getLocalizedImageButton("save","Save"),"submit");
    SubmitButton close = new SubmitButton(iwrb.getLocalizedImageButton("close","Close"),"close");
    tab.add(close,2,5);
    tab.add(Text.getNonBrakingSpace(),2,5);
    tab.add(button,2,5);

    boolean submit = iwc.isParameterSet("submit");
    boolean quit = iwc.isParameterSet("close");

    if (submit) {
      String parentPageId = iwc.getParameter(PAGE_CHOOSER_NAME);
      String name = iwc.getParameter(PAGE_NAME_PARAMETER);
      type = iwc.getParameter(PAGE_TYPE);
      String templateId = iwc.getParameter(TEMPLATE_CHOOSER_NAME);
      if (type.equals(IBPageHelper.TEMPLATE))
      	parentPageId = templateId;

      if (parentPageId != null) {
	      Map tree = PageTreeNode.getTree(iwc);
      	int id = IBPageHelper.getInstance().createNewPage(parentPageId,name,type,templateId,tree,iwc);
      	iwc.setSessionAttribute("ib_page_id",Integer.toString(id));
        /**@todo is this in the right place? -eiki**/
//        setOnLoad("window.opener.parent.parent.frames['"+com.idega.builder.app.IBApplication.IB_LEFT_MENU_FRAME+"'].location.reload()");
        setOnUnLoad("window.opener.parent.parent.location.reload()");

//        setParentToReload();
	      close();
      }
    }
    else if (quit) {
      close();
    }
    else {
      String name = iwc.getParameter(PAGE_NAME_PARAMETER);
      type = iwc.getParameter(PAGE_TYPE);
      String templateId = iwc.getParameter(TEMPLATE_CHOOSER_NAME);
      String templateName = iwc.getParameter(TEMPLATE_CHOOSER_NAME+"_displaystring");

      if (name != null)
      	inputName.setValue(name);

      if (type != null)
      	mnu.setSelectedElement(type);
    }
  }

  /*
   *
   */
  private PresentationObject getPageChooser(String name, IWContext iwc) {
    IBPageChooser chooser = new IBPageChooser(name);
    chooser.setInputStyle(IWConstants.BUILDER_FONT_STYLE_INTERFACE);
    try {
      IBPage current = BuilderLogic.getInstance().getCurrentIBPageEntity(iwc);
      if (current.getType().equals(IBPage.PAGE))
      	chooser.setSelectedPage(current.getID(),current.getName());
      else {
      	IBDomain domain = IBDomain.getDomain(1);
      	IBPage top = domain.getStartPage();
      	if (top != null)
      	  chooser.setSelectedPage(top.getID(),top.getName());
      }
    }
    catch(Exception e) {
      //does nothing
    }
    return(chooser);
  }

  /*
   *
   */
  private PresentationObject getTemplateChooser(String name, IWContext iwc, String type){
    IBTemplateChooser chooser = new IBTemplateChooser(name);
    chooser.setInputStyle(IWConstants.BUILDER_FONT_STYLE_INTERFACE);
    try {
      String templateId = iwc.getParameter(TEMPLATE_CHOOSER_NAME);
      if (templateId == null || templateId.equals("")) {
      	IBPage current = BuilderLogic.getInstance().getCurrentIBPageEntity(iwc);
      	if (current.getType().equals(IBPage.TEMPLATE))
      	  chooser.setSelectedPage(current);
      	else {
      	  if (type.equals(IBPageHelper.TEMPLATE)) {
	          IBDomain domain = IBDomain.getDomain(1);
    	      IBPage top = domain.getStartTemplate();
    	      if (top != null)
	            chooser.setSelectedPage(top);
      	  }
      	}
      }
      else {
      	IBPage top = new IBPage(Integer.parseInt(templateId));
      	if (top != null)
      	  chooser.setSelectedPage(top);
      }
    }
    catch(Exception e) {
      //does nothing
    }
    return chooser;
  }

  /**
   *
   */
  public String getBundleIdentifier() {
    return IW_BUNDLE_IDENTIFIER;
  }
}