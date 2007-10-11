package com.idega.builder.presentation;

import com.idega.builder.business.BuilderLogic;
import com.idega.idegaweb.IWBundle;
import com.idega.presentation.IWContext;
import com.idega.presentation.ui.AbstractChooser;

/**
 * Title:        idegaclasses
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href="tryggvi@idega.is">Tryggvi Larusson</a>
 * @modified by <a href=teiki@idega.is">Eirikur Hrafnsson</a>
 * @version 1.0
 */

public class IBPageChooser extends AbstractChooser {
	
	public IBPageChooser() {
		this(false);
	}
	
	public IBPageChooser(boolean useOldLogic) {
		super(useOldLogic);
		addForm(false);
	}

	public IBPageChooser(String chooserName, boolean useOldLogic, String instanceId, String method) {
		this(useOldLogic);
		setInstanceId(instanceId);
		setMethod(method);
		setChooserParameter(chooserName);
	}

	public IBPageChooser(String chooserName, String style, boolean useOldLogic) {
		this(chooserName, useOldLogic, null, null);
		setInputStyle(style);
	}
	
	public IBPageChooser(String chooserName) {
	    addForm(false);
	    //setChooseButtonImage(new Image("/common/pics/arachnea/open.gif","Choose"));
	    setChooserParameter(chooserName);
	  }

	  public IBPageChooser(String chooserName,String style) {
	    this(chooserName);
	    setInputStyle(style);
	  }

	public void main(IWContext iwc){
		this.empty();
	    IWBundle iwb = iwc.getIWMainApplication().getBundle(BuilderLogic.IW_BUNDLE_IDENTIFIER);
	    setChooseButtonImage(iwb.getImage("choose.png", "Choose"));
	}

	public Class getChooserWindowClass() {
		if (isUseOldLogic()) {
			return IBPageChooserWindow.class;
		}
		return IBPageChooserBlock.class;
	}
	
	/**
	 * Sets the page designated by pageId to be the page that is selected
	 * in the page tree
	 *
	 * @param pageId The id of the page that is to be selected
	 * @param pageName The name of the page that is to be selected
	 */
	public void setSelectedPage(int pageId, String pageName) {
		setChooserValue(pageName, pageId);
	}
	
	public void setSelectedPage(String pageId, String pageName) {
		setChooserValue(pageName, pageId);
	}
	
	@Override
	public String getChooserHelperVarName() {
		 return "page_chooser_helper";
	}
}
