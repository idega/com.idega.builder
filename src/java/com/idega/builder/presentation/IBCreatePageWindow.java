/*
 * $Id: IBCreatePageWindow.java,v 1.20 2002/01/11 12:33:12 palli Exp $
 *
 * Copyright (C) 2001 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package com.idega.builder.presentation;

import com.idega.builder.business.IBPropertyHandler;
import com.idega.builder.business.IBXMLPage;
import com.idega.builder.business.BuilderLogic;
import com.idega.builder.business.PageTreeNode;
import com.idega.builder.business.IBPageHelper;
import com.idega.builder.data.IBPage;
import com.idega.builder.data.IBDomain;
//import com.idega.core.data.ICFile;
import com.idega.presentation.IWContext;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.Table;
import com.idega.presentation.Page;
import com.idega.presentation.text.Text;
import com.idega.presentation.ui.Form;
import com.idega.presentation.ui.TextInput;
import com.idega.presentation.ui.SubmitButton;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.idegaweb.presentation.IWAdminWindow;
import com.idega.presentation.ui.RadioGroup;
import com.idega.presentation.ui.DropdownMenu;
import java.util.List;
import java.util.Iterator;
import java.util.Map;
import com.idega.presentation.ui.Window;

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

  public void main(IWContext iwc) throws Exception {
    IWResourceBundle iwrb = getBundle(iwc).getResourceBundle(iwc);
    super.addTitle(iwrb.getLocalizedString("ib_createpage_window","Create a new Page"));
    Form form = new Form();
    String type = iwc.getParameter(PAGE_TYPE);
    if (type == null) {
      String currPageType = BuilderLogic.getInstance().getCurrentIBXMLPage(iwc).getType();
      if (currPageType.equals(IBXMLPage.TYPE_TEMPLATE))
        type = "2";
      else
        type = "1";
    }

    if (type.equals("2"))
      setTitle(iwrb.getLocalizedString("create_new_template","Create a new template"));
    else
      setTitle(iwrb.getLocalizedString("create_new_page","Create a new page"));
    add(form);
    Table tab = new Table(2,5);
    form.add(tab);
    TextInput inputName = new TextInput(PAGE_NAME_PARAMETER);
    if (type.equals("2"))
      tab.add(iwrb.getLocalizedString("template_name","Template name"),1,1);
    else
      tab.add(iwrb.getLocalizedString("page_name","Page name"),1,1);
    tab.add(inputName,2,1);

    DropdownMenu mnu = new DropdownMenu(PAGE_TYPE);
    mnu.addMenuElement("1","Page");
    mnu.addMenuElement("2","Template");
    mnu.setSelectedElement(type);

    tab.add(new Text("Select type : "),1,2);
    tab.add(mnu,2,2);

    mnu.setToSubmit();

    if (!type.equals("2")) {
      tab.add(iwrb.getLocalizedString("parent_page","Create page under:"),1,3);
      tab.add(getPageChooser(PAGE_CHOOSER_NAME,iwc),2,3);
    }

    tab.add(iwrb.getLocalizedString("using_template","Using template:"),1,4);
    tab.add(getTemplateChooser(TEMPLATE_CHOOSER_NAME,iwc,type),2,4);

    SubmitButton button = new SubmitButton("subbi",iwrb.getLocalizedString("save","Save"));
    tab.add(button,2,5);

    String submit = iwc.getParameter("subbi");

    if (submit != null) {
      String pageId = iwc.getParameter(PAGE_CHOOSER_NAME);
      String name = iwc.getParameter(PAGE_NAME_PARAMETER);
      type = iwc.getParameter(PAGE_TYPE);
      String templateId = iwc.getParameter(TEMPLATE_CHOOSER_NAME);
      if (type.equals("2"))
        pageId = templateId;

      if (pageId != null) {
        int id = IBPageHelper.createNewPage(pageId,name,type,templateId);
        if (id != -1) {
          PageTreeNode parent = new PageTreeNode(Integer.parseInt(pageId),iwc);
          Map tree = PageTreeNode.getTree(iwc);

          if (parent != null) {
            if (tree != null) {
              PageTreeNode child = new PageTreeNode(id,iwc);
              child.setNodeName(name);
              parent.addChild(child);
              tree.put(new Integer(child.getNodeID()),child);
            }
          }

          if ((templateId != null) && (!templateId.equals(""))) {
            IBXMLPage xml = BuilderLogic.getInstance().getIBXMLPage(templateId);
            xml.addUsingTemplate(Integer.toString(id));
            Page templateParent = xml.getPopulatedPage();
            if (!templateParent.isLocked()) {
              BuilderLogic.getInstance().unlockRegion(Integer.toString(id),"-1",null);
            }
          }

          iwc.setSessionAttribute("ib_page_id",Integer.toString(id));
        }

        setParentToReload();
        close();
      }
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
    try {
      IBPage current = BuilderLogic.getInstance().getCurrentIBPageEntity(iwc);
      if (current.getType().equals(IBPage.PAGE))
        chooser.setSelectedPage(current);
      else {
        IBDomain domain = IBDomain.getDomain(1);
        IBPage top = domain.getStartPage();
        if (top != null)
          chooser.setSelectedPage(top);
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
    try {
      String templateId = iwc.getParameter(TEMPLATE_CHOOSER_NAME);
      if (templateId == null || templateId.equals("")) {
        IBPage current = BuilderLogic.getInstance().getCurrentIBPageEntity(iwc);
        if (current.getType().equals(IBPage.TEMPLATE))
          chooser.setSelectedPage(current);
        else {
          if (type.equals("2")) {
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
    return(chooser);
  }
}