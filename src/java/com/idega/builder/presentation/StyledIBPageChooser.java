/*
 * Created on Nov 27, 2003
 */
package com.idega.builder.presentation;

import com.idega.builder.business.BuilderLogic;
import com.idega.core.builder.data.ICPage;
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

public class StyledIBPageChooser extends AbstractChooser {
	private String style;

	public StyledIBPageChooser(String chooserName) {
		addForm(false);
		//setChooseButtonImage(new Image("/common/pics/arachnea/open.gif","Choose"));
		setChooserParameter(chooserName);
	}

	public StyledIBPageChooser(String chooserName,String style) {
		this(chooserName);
		setInputStyle(style);
	}

	public void main(IWContext iwc) throws Exception {
		this.empty();
		IWBundle iwb = iwc.getApplication().getBundle(BuilderLogic.IW_BUNDLE_IDENTIFIER);
		setChooseButtonImage(iwb.getImage("open.gif","Choose"));
	}

	public Class getChooserWindowClass() {
		return StyledIBPageChooserWindow.class;
	}

	/**
	 * Sets the page designated by pageId to be the page that is selected
	 * in the page tree.
	 *
	 * @param pageId The id of the page that is to be selected
	 * @param pageName The name of the page that is to be selected
	 */
	public void setSelectedPage(int pageId, String pageName) {
		super.setChooserValue(pageName,pageId);
	}

	/**
	 * @deprecated Replaced by {@link #setSelectedPage(int,String)}
	 */
	public void setSelectedPage(ICPage page){
		super.setChooserValue(page.getName(),page.getID());
	}

	/**
	 * @deprecated Replaced by {@link #setSelectedPage(int,String)}
	 */
	public void setValue(Object page){
		setSelectedPage((ICPage)page);
	}
}

