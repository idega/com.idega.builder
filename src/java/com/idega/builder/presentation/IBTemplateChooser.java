/*
 * $Id: IBTemplateChooser.java,v 1.5 2001/10/31 13:12:46 tryggvil Exp $
 *
 * Copyright (C) 2001 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package com.idega.builder.presentation;

import com.idega.presentation.ui.*;
import com.idega.presentation.Image;
import com.idega.presentation.*;
import com.idega.idegaweb.IWBundle;
import com.idega.builder.business.BuilderLogic;

import com.idega.builder.data.IBPage;

/**
 * @author <a href="mailto:palli@idega.is">Pall Helgason</a>
 * @version 1.3
 */
public class IBTemplateChooser extends AbstractChooser {
  /**
   *
   */
  public IBTemplateChooser(String name) {
    addForm(false);
    //setChooseButtonImage(new Image("/common/pics/arachnea/open.gif","Choose"));
    setChooserParameter(name);
  }


  public void main(IWContext iwc){
    IWBundle iwb = iwc.getApplication().getBundle(BuilderLogic.IW_BUNDLE_IDENTIFIER);
    setChooseButtonImage(iwb.getImage("open.gif","Choose"));
  }

  /**
   *
   */
  public Class getChooserWindowClass() {
    return(IBTemplateChooserWindow.class);
  }


  public void setSelectedPage(IBPage page){
    super.setChooserValue(page.getName(),page.getID());
  }

}
