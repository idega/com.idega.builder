/*
 * $Id: IBTemplateChooser.java,v 1.6 2001/11/06 18:18:03 palli Exp $
 *
 * Copyright (C) 2001 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package com.idega.builder.presentation;

import com.idega.presentation.ui.AbstractChooser;
import com.idega.presentation.IWContext;
import com.idega.idegaweb.IWBundle;
import com.idega.builder.business.BuilderLogic;
import com.idega.builder.data.IBPage;

/**
 * @author <a href="mailto:palli@idega.is">Pall Helgason</a>
 * @version 1.3
 */
public class IBTemplateChooser extends AbstractChooser {
  private String _name = null;
  /**
   *
   */
  public IBTemplateChooser(String name) {
    addForm(false);
    //setChooseButtonImage(new Image("/common/pics/arachnea/open.gif","Choose"));
    setChooserParameter(name);
    _name = name;
  }


  public void main(IWContext iwc) {
    IWBundle iwb = iwc.getApplication().getBundle(BuilderLogic.IW_BUNDLE_IDENTIFIER);
    setChooseButtonImage(iwb.getImage("open.gif","Choose"));

/*    System.out.println("Name = " + _name);
    String newValue = iwc.getParameter(_name);
    System.out.println("New value = " + newValue);
    if (newValue != null && !newValue.equals("")) {
      BuilderLogic instance = BuilderLogic.getInstance();
      instance.changeTemplateId(newValue,iwc);
    }*/
  }

  /**
   *
   */
  public Class getChooserWindowClass() {
    return(IBTemplateChooserWindow.class);
  }


  public void setSelectedPage(IBPage page) {
    super.setChooserValue(page.getName(),page.getID());
  }

  public void setSelectedPage(int id, String name) {
  System.out.println("Setting selected page to " + id + ", name = " + name);
    super.setChooserValue(name,id);
  }

/*  public void setSelectedPage(String id) {
    super.setChooserValue(page.getName(),page.getID());
  }*/
}
