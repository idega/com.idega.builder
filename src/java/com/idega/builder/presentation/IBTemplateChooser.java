/*
 * $Id: IBTemplateChooser.java,v 1.18 2007/06/15 13:24:06 civilis Exp $
 *
 * Copyright (C) 2001 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package com.idega.builder.presentation;

import com.idega.builder.business.BuilderLogic;
import com.idega.core.builder.data.ICPage;
import com.idega.idegaweb.IWBundle;
import com.idega.presentation.IWContext;
import com.idega.presentation.ui.AbstractChooser;

/**
 * @author <a href="mailto:palli@idega.is">Pall Helgason</a>
 * @version 1.3
 */
public class IBTemplateChooser extends AbstractChooser {
  
	public IBTemplateChooser(){
		this(false);
	}
	
	public IBTemplateChooser(boolean useOldLogic) {
		super(useOldLogic);
		addForm(false);
	}
  
  /**
   *
   */
  public IBTemplateChooser(String name, boolean useOldLogic, String instanceId, String method) {
    this(useOldLogic);
    setInstanceId(instanceId);
    setMethod(method);
    setChooserParameter(name);
  }

  /**
   *
   */
  public void main(IWContext iwc) {
    IWBundle iwb = iwc.getIWMainApplication().getBundle(BuilderLogic.IW_BUNDLE_IDENTIFIER);
    setChooseButtonImage(iwb.getImage("choose.png", "Choose"));
  }

  /**
   *
   */
  public Class getChooserWindowClass() {
	  if (isUseOldLogic()) {
		  return IBTemplateChooserWindow.class;
	  }
	  return IBTemplateChooserBlock.class;
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
  
  @Override
  protected String getChooserHelperVarName() {
	  return "template_chooser_helper";
  }
}