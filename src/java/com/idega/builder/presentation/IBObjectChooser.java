/*
 * Created on Jun 21, 2003
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code Template
 */
package com.idega.builder.presentation;

import com.idega.builder.business.BuilderLogic;
import com.idega.core.component.data.ICObject;
import com.idega.idegaweb.IWBundle;
import com.idega.presentation.IWContext;
import com.idega.presentation.ui.AbstractChooser;

/**
 * <p>Title: idegaWeb</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: idega Software</p>
 * @author aron 
 * @version 1.0
 */
public class IBObjectChooser extends AbstractChooser {
	
	public static String USE_CLASS_VALUE = "ibobjbchooser_classval";
	boolean useClassValue = false;
	
	public IBObjectChooser(String chooserName){
		addForm(false);
		setChooserParameter(chooserName);
	}
	
	public IBObjectChooser(String chooserName,String style) {
		this(chooserName);
		setInputStyle(style);
	 }
	
	/* (non-Javadoc)
	 * @see com.idega.presentation.ui.AbstractChooser#getChooserWindowClass()
	 */
	public Class getChooserWindowClass() {
		return IBObjectChooserWindow.class;
	}
	
	public void main(IWContext iwc){
		this.empty();
		IWBundle iwb = iwc.getIWMainApplication().getBundle(BuilderLogic.IW_BUNDLE_IDENTIFIER);
		setChooseButtonImage(iwb.getImage("open.gif","Choose"));
	  }
	  
	public void addTypeFilter(String typeFilter){
		super.addParameterToChooserLink(IBObjectChooserWindow.PRM_FILTER,typeFilter);
	}
	
	public void setTypeFilter(String typeFilter){
		addTypeFilter(typeFilter);
	}
	  
	public void setSelectedObject(ICObject object){
		if(this.useClassValue) {
			super.setChooserValue(object.getName(),object.getClassName());
		}
		else {
			super.setChooserValue(object.getName(),object.getID());
		}
	 }
	 

	 public void setValue(Object file){
		setSelectedObject((ICObject)file);
	 }
	 
	 public void setToUseClassValue(boolean use){
	 	this.useClassValue = use;
	 	if(use) {
			super.addParameterToChooserLink(USE_CLASS_VALUE,"true");
		}
	 }
	 
	 @Override
	 public String getChooserHelperVarName() {
		 return "Object_chooser_helper";
	 }
}