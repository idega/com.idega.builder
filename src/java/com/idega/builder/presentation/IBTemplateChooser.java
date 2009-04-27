/*
 * $Id: IBTemplateChooser.java,v 1.20 2009/04/27 14:52:25 valdas Exp $
 *
 * Copyright (C) 2001 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package com.idega.builder.presentation;

import com.idega.builder.business.BuilderConstants;
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
  @Override
public void main(IWContext iwc) {
    IWBundle iwb = iwc.getIWMainApplication().getBundle(BuilderConstants.IW_BUNDLE_IDENTIFIER);
    setChooseButtonImage(iwb.getImage("choose.png", "Choose"));
  }

  /**
   *
   */
  @Override
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
  public String getChooserHelperVarName() {
	  return "template_chooser_helper";
  }
}