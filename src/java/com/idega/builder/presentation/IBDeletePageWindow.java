/*
 * $Id: IBDeletePageWindow.java,v 1.8 2002/02/12 13:18:23 palli Exp $
 *
 * Copyright (C) 2001 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package com.idega.builder.presentation;

import com.idega.presentation.IWContext;
import com.idega.presentation.Table;
import com.idega.presentation.text.Text;
import com.idega.presentation.ui.Form;
import com.idega.presentation.ui.SubmitButton;
import com.idega.presentation.ui.CheckBox;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.idegaweb.presentation.IWAdminWindow;
import com.idega.builder.business.BuilderLogic;
import com.idega.builder.business.IBXMLPage;
import com.idega.builder.business.PageTreeNode;
import com.idega.builder.business.IBPageHelper;
import com.idega.builder.data.IBPage;
import com.idega.presentation.Page;
import java.util.List;
import java.util.Iterator;
import java.util.Map;

/**
 * @author <a href="mailto:palli@idega.is">Pall Helgason</a>
 * @version 1.0
 */
public class IBDeletePageWindow extends IWAdminWindow {
  private static final String PAGE_NAME_PARAMETER   = "ib_page_name";
  private static final String PAGE_TYPE             = "ib_page_type";
  private static final String IW_BUNDLE_IDENTIFIER  = "com.idega.builder";

  public void main(IWContext iwc) throws Exception {
    boolean okToDelete = false;
    boolean okToDeleteChildren = true;
    IWResourceBundle iwrb = getBundle(iwc).getResourceBundle(iwc);
    Form form = new Form();

    setTitle(iwrb.getLocalizedString("delete_page","Delete a page"));
    add(form);

    BuilderLogic instance = BuilderLogic.getInstance();
    String pageId = instance.getCurrentIBPage(iwc);

    String submit = iwc.getParameter("ok");
    String quit = iwc.getParameter("cancel");
    String deleteAll = iwc.getParameter("deletechildren");

    if (submit != null) {
      boolean deleted = false;
      if ((deleteAll != null) && (deleteAll.equals("true")))
        deleted = IBPageHelper.deletePage(pageId,true,PageTreeNode.getTree(iwc),iwc.getUserId());
      else
        deleted = IBPageHelper.deletePage(pageId,false,PageTreeNode.getTree(iwc),iwc.getUserId());

      iwc.setSessionAttribute("ib_page_id",Integer.toString(BuilderLogic.getInstance().getCurrentDomain(iwc).getStartPageID()));
      setParentToReload();
      close();
    }
    else if (quit != null) {
      close();
    }

    okToDelete = IBPageHelper.checkDeletePage(pageId);

    if (okToDelete) {
      okToDeleteChildren = IBPageHelper.checkDeleteChildrenOfPage(pageId);
      SubmitButton ok = new SubmitButton("ok",iwrb.getLocalizedString("yes","Yes"));
      SubmitButton cancel = new SubmitButton("cancel",iwrb.getLocalizedString("no","No"));
      CheckBox deleteChildren = new CheckBox("deletechildren","true");
      deleteChildren.setChecked(false);
      Text deleteChildrenText = new Text(iwrb.getLocalizedString("childrentext","Delete children of page"));
      Text sureText = new Text(iwrb.getLocalizedString("suredelete","Are you sure you want to delete this page"));

      Table table = new Table(2,3);
      table.mergeCells(1,1,2,1);
      table.mergeCells(1,3,2,3);
      table.setAlignment(1,2,"left");

      table.add(sureText,1,1);
      table.add(ok,1,2);
      table.add(cancel,2,2);
      if (!okToDeleteChildren) {
        deleteChildren.setValue("false");
        deleteChildren.setDisabled(true);
      }
      else {
        deleteChildren.setValue("true");
        deleteChildren.setDisabled(false);
      }

      table.add(deleteChildren,1,3);
      table.add(deleteChildrenText,1,3);

      form.add(table);
    }
    else {
      SubmitButton cancel = new SubmitButton("cancel",iwrb.getLocalizedString("cancel","Cancel"));
      Text notAllowed = new Text(iwrb.getLocalizedString("not_allowed","There are pages using this template so you can not delete it"));
      form.add(notAllowed);
      form.add(cancel);
    }
  }
}