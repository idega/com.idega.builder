
//idega 2001 - Tryggvi Larusson
/*
*Copyright 2001 idega.is All Rights Reserved.
*/
package com.idega.builder.presentation;


import java.util.List;
import java.util.Iterator;

import com.idega.jmodule.*;
import com.idega.data.*;
import com.idega.jmodule.news.presentation.*;
import com.idega.util.*;
import com.idega.builder.data.*;
import com.idega.core.data.*;
import com.idega.jmodule.object.*;
import com.idega.jmodule.object.textObject.*;
import com.idega.jmodule.object.interfaceobject.*;
import com.idega.builder.business.*;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWResourceBundle;

import com.idega.data.EntityFinder;

/**
*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
*@version 1.0 alpha
*/

public class IBCreatePageWindow extends IBAdminWindow{

  private static final String PAGE_NAME_PARAMETER = "ib_page_name";
  private static final String PAGE_CHOOSER_NAME = "ib_page_chooser";
  private static final String TEMPLATE_CHOOSER_NAME = "ib_template_chooser";

  public void main(ModuleInfo modinfo)throws Exception{
    IWResourceBundle iwrb = getBundle(modinfo).getResourceBundle(modinfo);
    Form form = new Form();
    add(form);
    FramePane pane = new FramePane(iwrb.getLocalizedString("create_new_page","Create New page"));
    form.add(pane);
    Table tab = new Table(2,4);
    pane.add(tab);

    TextInput inputName = new TextInput(PAGE_NAME_PARAMETER);
    tab.add(iwrb.getLocalizedString("page_name","Page name"),1,1);
    tab.add(inputName,2,1);


    tab.add(iwrb.getLocalizedString("parent_page","Create under:"),1,2);
    tab.add(getPageChooser(PAGE_CHOOSER_NAME),2,2);

    tab.add(iwrb.getLocalizedString("using_template","Using template:"),1,3);
    tab.add(getPageChooser(TEMPLATE_CHOOSER_NAME),2,3);


    SubmitButton button = new SubmitButton(iwrb.getLocalizedString("save","Save"));
    tab.add(button,2,4);
  }

  private ModuleObject getPageChooser(String name){
    return new TextInput(name,"none");
  }

  private ModuleObject getTemplateChooser(String name){
    return new TextInput(name,"none");
  }

}

