package com.idega.builder.presentation;

import com.idega.presentation.ui.*;
import com.idega.presentation.Image;
import com.idega.presentation.IWContext;

import com.idega.core.data.ICFile;

import com.idega.builder.business.BuilderLogic;
import com.idega.idegaweb.IWBundle;


/**
 * Title:        idegaclasses
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href=teiki@idega.is">Eirikur Hrafnsson</a>
 * @version 1.0
 */

public class IBFileChooser extends AbstractChooser {
  private String style;

  public IBFileChooser(String chooserName) {
    addForm(false);
    //setChooseButtonImage(new Image("/common/pics/arachnea/open.gif","Choose"));
    setChooserParameter(chooserName);
  }

  public IBFileChooser(String chooserName,String style) {
    this(chooserName);
    setInputStyle(style);
  }

  public Class getChooserWindowClass() {
    return IBFileChooserWindow.class;
  }

  public void main(IWContext iwc){
    IWBundle iwb = iwc.getApplication().getBundle(BuilderLogic.IW_BUNDLE_IDENTIFIER);
    setChooseButtonImage(iwb.getImage("open.gif","Choose File"));
  }

  public void setSelectedFile(ICFile file){
    super.setChooserValue(file.getName(),file.getID());
  }

}