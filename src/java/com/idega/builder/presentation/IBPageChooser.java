package com.idega.builder.presentation;

import com.idega.jmodule.object.interfaceobject.*;
import com.idega.jmodule.object.Image;


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
    addForm(false);
    setChooseButtonImage(new Image("/common/pics/arachnea/toolbar_open_1.gif","Choose"));
  }

  public Class getChooserWindowClass() {
    return IBPageChooserWindow.class;
  }

}