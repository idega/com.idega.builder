/*
 * $Id: IBTemplateChooser.java,v 1.11 2004/02/20 16:37:42 tryggvil Exp $
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
import com.idega.core.builder.data.ICPage;

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
    setChooserParameter(name);
    _name = name;
  }

  /**
   *
   */
  public void main(IWContext iwc) {
    IWBundle iwb = iwc.getIWMainApplication().getBundle(BuilderLogic.IW_BUNDLE_IDENTIFIER);
    setChooseButtonImage(iwb.getImage("open.gif","Choose"));
  }

  /**
   *
   */
  public Class getChooserWindowClass() {
    return(IBTemplateChooserWindow.class);
  }

  /**
   *
   */
  public void setSelectedPage(ICPage page) {
    super.setChooserValue(page.getName(),page.getID());
  }

  /**
   *
   */
  public void setSelectedPage(int id, String name) {
    super.setChooserValue(name,id);
  }
}
