package com.idega.builder.presentation;

import com.idega.presentation.ui.*;
import com.idega.presentation.Image;


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
  private String style;

  public IBPageChooser(String chooserName) {
    addForm(false);
    setChooseButtonImage(new Image("/common/pics/arachnea/open.gif","Choose"));
    setChooserParameter(chooserName);
  }

  public IBPageChooser(String chooserName,String style) {
    this(chooserName);
    setInputStyle(style);
  }

  public Class getChooserWindowClass() {
    return IBPageChooserWindow.class;
  }

}