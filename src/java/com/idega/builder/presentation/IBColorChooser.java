package com.idega.builder.presentation;

import com.idega.presentation.*;
import com.idega.util.text.TextSoap;
import com.idega.presentation.ui.*;

import com.idega.idegaweb.IWBundle;
import com.idega.builder.business.BuilderLogic;

import com.idega.builder.data.IBPage;

/**
 * Title:        idegaclasses
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href="tryggvi@idega.is">Tryggvi Larusson</a>
 * @modified by <a href=teiki@idega.is">Eirikur Hrafnsson</a>
 * @version 1.0
 */

public class IBColorChooser extends AbstractChooser {
  private String style;

  public IBColorChooser(String chooserName) {
    addForm(false);
    //setChooseButtonImage(new Image("/common/pics/arachnea/open.gif","Choose"));
    setChooserParameter(chooserName);
  }

  public IBColorChooser(String chooserName,String style) {
    this(chooserName);
    setInputStyle(style);
  }

  public void main(IWContext iwc){
    IWBundle iwb = iwc.getApplication().getBundle(BuilderLogic.IW_BUNDLE_IDENTIFIER);
    setChooseButtonImage(iwb.getImage("open.gif","Choose"));
  }

  public Class getChooserWindowClass() {
    return IBColorChooserWindow.class;
  }

  public void setSelected(String color){
    super.setChooserValue(color,color);
    super.setParameterValue("color",TextSoap.findAndCut(color,"#"));
  }
}
