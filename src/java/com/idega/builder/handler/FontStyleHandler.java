package com.idega.builder.handler;

import java.util.List;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.IWContext;
import com.idega.presentation.ui.DropdownMenu;

import com.idega.builder.presentation.IBStyleChooser;

/**
 * Title:        idegaclasses
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href="tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */

public class FontStyleHandler implements PropertyHandler {

  public FontStyleHandler() {
  }
  public List getDefaultHandlerTypes() {
    return null;
  }
  public PresentationObject getHandlerObject(String name,String value,IWContext iwc){
    IBStyleChooser chooser = new IBStyleChooser(name,"font-size: 8pt; border: 1 solid #000000");
    chooser.setSelected(value);
    return chooser;
  }

}