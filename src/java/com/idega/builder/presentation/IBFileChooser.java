package com.idega.builder.presentation;

import com.idega.jmodule.object.interfaceobject.*;
import com.idega.jmodule.object.Image;


/**
 * Title:        idegaclasses
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href=teiki@idega.is">Eirikur Hrafnsson</a>
 * @version 1.0
 */

public class IBFileChooser extends AbstractChooser {
  public IBFileChooser(String chooserName) {
    addForm(false);
    setChooseButtonImage(new Image("/common/pics/arachnea/toolbar_open_1.gif","Choose"));
    setChooserParameter(chooserName);
  }

  public Class getChooserWindowClass() {
    return IBFileChooserWindow.class;
  }

}