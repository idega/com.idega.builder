/*
 * $Id: IBCreatePageWindow.java,v 1.12 2001/10/31 13:12:46 tryggvil Exp $
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
import com.idega.builder.data.IBPage;
import com.idega.core.data.ICFile;
import com.idega.presentation.IWContext;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.Table;
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
 * @author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
 * @modified by <a href=teiki@idega.is">Eirikur Hrafnsson</a>
 * @version 1.0 alpha
*/

public class IBCreatePageWindow extends IWAdminWindow {
  private static final String PAGE_NAME_PARAMETER   = "ib_page_name";
  private static final String PAGE_CHOOSER_NAME     = IBPropertyHandler.PAGE_CHOOSER_NAME;
  private static final String TEMPLATE_CHOOSER_NAME = IBPropertyHandler.TEMPLATE_CHOOSER_NAME;
  private static final String PAGE_TYPE             = "ib_page_type";
  private static final String IW_BUNDLE_IDENTIFIER  = "com.idega.builder";

  public void main(IWContext iwc) throws Exception {
    IWResourceBundle iwrb = getBundle(iwc).getResourceBundle(iwc);
    Form form = new Form();

    setTitle(iwrb.getLocalizedString("create_new_page","Create a new page"));
    add(form);
    Table tab = new Table(2,5);
    form.add(tab);
    TextInput inputName = new TextInput(PAGE_NAME_PARAMETER);
    tab.add(iwrb.getLocalizedString("page_name","Page name"),1,1);
    tab.add(inputName,2,1);

    DropdownMenu mnu = new DropdownMenu(PAGE_TYPE);
    mnu.addMenuElement("1","Page");
    mnu.addMenuElement("2","Template");

    tab.add(new Text("Select page type : "),1,2);
    tab.add(mnu,2,2);

    mnu.setToSubmit();

    String type = iwc.getParameter(PAGE_TYPE);

    tab.add(iwrb.getLocalizedString("parent_page","Create page under:"),1,3);
    if (type != null) {
      if (type.equals("2"))
        tab.add(getTemplateChooser(PAGE_CHOOSER_NAME,iwc),2,3);
      else
        tab.add(getPageChooser(PAGE_CHOOSER_NAME,iwc),2,3);
    }
    else
      tab.add(getPageChooser(PAGE_CHOOSER_NAME,iwc),2,3);

    tab.add(iwrb.getLocalizedString("using_template","Using template:"),1,4);
    tab.add(getTemplateChooser(TEMPLATE_CHOOSER_NAME,iwc),2,4);

    SubmitButton button = new SubmitButton("subbi",iwrb.getLocalizedString("save","Save"));
    tab.add(button,2,5);

    String submit = iwc.getParameter("subbi");

    if (submit != null) {
      String pageId = iwc.getParameter(PAGE_CHOOSER_NAME);
      String name = iwc.getParameter(PAGE_NAME_PARAMETER);
      type = iwc.getParameter(PAGE_TYPE);
      String templateId = iwc.getParameter(TEMPLATE_CHOOSER_NAME);

      if (pageId != null) {
        IBPage ibPage = new IBPage();
        if (name == null)
          name = "Untitled";
        ibPage.setName(name);
        ICFile file = new ICFile();
        ibPage.setFile(file);

        String treeType = null;
        if (type.equals("1")) {
          ibPage.setType(IBPage.PAGE);
          treeType =PageTreeNode.PAGE_TREE;
        }
        else if (type.equals("2")) {
          ibPage.setType(IBPage.TEMPLATE);
          treeType = PageTreeNode.TEMPLATE_TREE;
        }
        else {
          ibPage.setType(IBPage.PAGE);
          treeType =PageTreeNode.PAGE_TREE;
        }

        int tid = -1;
        try {
          tid = Integer.parseInt(templateId);
          ibPage.setTemplateId(tid);
        }
        catch(java.lang.NumberFormatException e) {
        }

        ibPage.insert();
        IBPage ibPageParent = new IBPage(Integer.parseInt(pageId));
        ibPageParent.addChild(ibPage);

        PageTreeNode parent = new PageTreeNode(Integer.parseInt(pageId),iwc,treeType);
        Map tree = (Map)iwc.getApplicationAttribute(treeType);

        if (parent != null) {
          if (tree != null) {
            PageTreeNode child = new PageTreeNode(ibPage.getID(),iwc,treeType);
            child.setNodeId(ibPage.getID());
            child.setNodeName(ibPage.getName());
            parent.addChild(child);
            tree.put(new Integer(child.getNodeID()),child);
          }
        }

        if ((templateId != null) && (!templateId.equals(""))) {
          IBXMLPage xml = BuilderLogic.getInstance().getIBXMLPage(templateId);
          xml.addUsingTemplate(Integer.toString(ibPage.getID()));
        }

        iwc.setSessionAttribute("ib_page_id",Integer.toString(ibPage.getID()));
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
  private PresentationObject getPageChooser(String name,IWContext iwc){
    IBPageChooser chooser = new IBPageChooser(name);
    try{
      chooser.setSelectedPage(BuilderLogic.getInstance().getCurrentIBPageEntity(iwc));
    }
    catch(Exception e){
      //does nothing
    }
    return chooser;
  }

  /*
   *
   */
  private PresentationObject getTemplateChooser(String name,IWContext iwc){
    IBTemplateChooser chooser = new IBTemplateChooser(name);
    //chooser.setSelectedPage(BuilderLogic.getInstance().getCurrentIBPage(iwc));
    return chooser;
  }
}

