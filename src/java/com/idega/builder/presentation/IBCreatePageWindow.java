/*
 * $Id: IBCreatePageWindow.java,v 1.4 2001/09/13 17:38:17 palli Exp $
 *
 * Copyright (C) 2001 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package com.idega.builder.presentation;

import com.idega.builder.data.IBPage;
import com.idega.core.data.ICFile;
import com.idega.jmodule.object.ModuleInfo;
import com.idega.jmodule.object.ModuleObject;
import com.idega.jmodule.object.Table;
import com.idega.jmodule.object.textObject.Text;
import com.idega.jmodule.object.interfaceobject.Form;
import com.idega.jmodule.object.interfaceobject.TextInput;
import com.idega.jmodule.object.interfaceobject.SubmitButton;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.idegaweb.presentation.IWAdminWindow;
import com.idega.jmodule.object.interfaceobject.RadioGroup;
import java.util.List;
import java.util.Iterator;

/**
 * @author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
 * @modified by <a href=teiki@idega.is">Eirikur Hrafnsson</a>
 * @version 1.0 alpha
*/

//public class IBCreatePageWindow extends IBAdminWindow{
public class IBCreatePageWindow extends IWAdminWindow {
  private static final String PAGE_NAME_PARAMETER   = "ib_page_name";
  private static final String PAGE_CHOOSER_NAME     = "ib_page_chooser";
  private static final String TEMPLATE_CHOOSER_NAME = "ib_template_chooser";
  private static final String PAGE_TYPE             = "ib_page_type";
  private static final String IW_BUNDLE_IDENTIFIER  = "com.idega.builder";

  public void main(ModuleInfo modinfo) throws Exception {
    IWResourceBundle iwrb = getBundle(modinfo).getResourceBundle(modinfo);
    Form form = new Form();

    setTitle(iwrb.getLocalizedString("create_new_page","Create a new page"));
    add(form);
    Table tab = new Table(2,5);
    form.add(tab);
    TextInput inputName = new TextInput(PAGE_NAME_PARAMETER);
    tab.add(iwrb.getLocalizedString("page_name","Page name"),1,1);
    tab.add(inputName,2,1);


    tab.add(iwrb.getLocalizedString("parent_page","Create page under:"),1,2);
    tab.add(getPageChooser(PAGE_CHOOSER_NAME),2,2);

    tab.add(iwrb.getLocalizedString("using_template","Using template:"),1,3);
    tab.add(getTemplateChooser(TEMPLATE_CHOOSER_NAME),2,3);

    RadioGroup rg = new RadioGroup(PAGE_TYPE);
    rg.addRadioButton(1,new Text("Page"),true);
    rg.addRadioButton(2,new Text("Draft"),false);
    rg.addRadioButton(3,new Text("Template"),false);

    tab.add(new Text("Select page type : "),1,4);
    tab.add(rg,2,4);

    SubmitButton button = new SubmitButton("submit",iwrb.getLocalizedString("save","Save"));
    tab.add(button,2,5);

    String submit = modinfo.getParameter("submit");

    if (submit != null) {
      String pageId = modinfo.getParameter("chooser_value");
      String name = modinfo.getParameter(PAGE_NAME_PARAMETER);
      String type = modinfo.getParameter(PAGE_TYPE);
      if (pageId != null) {
        IBPage ibPage = new IBPage();
        if (name == null)
          name = "Untitled";
        ibPage.setName(name);
        ICFile file = new ICFile();
        //file.insert();
        ibPage.setFile(file);

        if (type.equalsIgnoreCase("1"))
          ibPage.setType(IBPage.page);
        else if (type.equalsIgnoreCase("2"))
          ibPage.setType(IBPage.draft);
        else if (type.equalsIgnoreCase("3"))
          ibPage.setType(IBPage.template);
        else
          ibPage.setType(IBPage.page);

        ibPage.insert();
        IBPage ibPageParent = new IBPage(Integer.parseInt(pageId));
        ibPageParent.addChild(ibPage);

        modinfo.setSessionAttribute("ib_page_id",Integer.toString(ibPage.getID()));
        setParentToReload();
        close();
      }
    }
  }

/**
 * @todo use the name variable to identify the parameter
 */
  private ModuleObject getPageChooser(String name){
    return new IBPageChooser(name);
  }

  private ModuleObject getTemplateChooser(String name){
    return new IBTemplateChooser(name);
  }

}

