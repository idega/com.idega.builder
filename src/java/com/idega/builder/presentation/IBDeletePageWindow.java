/*
 * $Id: IBDeletePageWindow.java,v 1.5 2001/10/10 12:41:20 palli Exp $
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
import com.idega.builder.data.IBPage;
import com.idega.presentation.Page;
import java.util.List;
import java.util.Iterator;

/**
 * @author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
 * @modified by <a href=teiki@idega.is">Eirikur Hrafnsson</a>
 * @version 1.0 alpha
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
      IBPage ibpage = new IBPage(Integer.parseInt(pageId));
      IBPage parent = (IBPage)ibpage.getParentNode();

      parent.removeChild(ibpage);
      ibpage.setDeleted(true,iwc);
      ibpage.update();

      if ((deleteAll != null) && (deleteAll.equals("true"))) {
        deleteAllChildren(ibpage,iwc);
      }
      else {
        parent.moveChildrenFrom(ibpage);
      }

      setParentToReload();
      close();
    }
    else if (quit != null) {
      close();
    }

    IBXMLPage xml = instance.getIBXMLPage(pageId);

    if (xml.getType().equals(IBXMLPage.TYPE_TEMPLATE)) {
      List map = xml.getUsingTemplate();

      if ((map == null) || (map.isEmpty())) {
        IBPage ibpage = new IBPage(Integer.parseInt(pageId));
        okToDelete = true;
        okToDeleteChildren = checkDeleteOfChildren(ibpage);
      }
      else
        okToDelete = false;
    }
    else {
      okToDelete = true;
    }


    if (okToDelete) {
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

  private void deleteAllChildren(IBPage page, IWContext iwc) throws java.sql.SQLException {
    Iterator it = page.getChildren();

    if (it != null) {
      while (it.hasNext()) {
        IBPage child = (IBPage)it.next();

        if (child.getChildCount() != 0)
          deleteAllChildren(child,iwc);

        child.setDeleted(true,iwc);
        child.update();
        page.removeChild(child);
      }
    }
  }

  private boolean checkDeleteOfChildren(IBPage page) throws java.sql.SQLException {
    Iterator it = page.getChildren();

    if (it != null) {
      while (it.hasNext()) {
        IBPage child = (IBPage)it.next();

        IBXMLPage xml = BuilderLogic.getInstance().getIBXMLPage(child.getID());
        List map = xml.getUsingTemplate();

        if ((map != null) || (!map.isEmpty())) {
          return(false);
        }

        boolean check = true;
        if (child.getChildCount() != 0)
          check = checkDeleteOfChildren(child);

        if (!check)
          return(false);
      }
    }
    return(true);
  }
}

