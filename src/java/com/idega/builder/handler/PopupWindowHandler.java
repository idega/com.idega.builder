/*
 * $Id: PopupWindowHandler.java,v 1.2 2002/04/06 19:07:39 tryggvil Exp $
 *
 * Copyright (C) 2001 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package com.idega.builder.handler;

import com.idega.builder.presentation.PopupWindowChooserWindow;
import com.idega.util.text.TextSoap;
import java.util.List;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.IWContext;
import com.idega.presentation.ui.DropdownMenu;
import com.idega.builder.presentation.PopupWindowChooser;

/**
 * @author <a href="tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */
public class PopupWindowHandler implements PropertyHandler {
  /**
   *
   */
  public PopupWindowHandler() {
  }

  /**
   *
   */
  public List getDefaultHandlerTypes() {
    return(null);
  }

  /**
   *
   */
  public PresentationObject getHandlerObject(String name, String value, IWContext iwc) {
    PopupWindowChooser chooser = new PopupWindowChooser(name,"font-size: 8pt; border: 1 solid #000000");
    value = TextSoap.findAndReplace(value,PopupWindowChooserWindow.replaceMent,"'");
    chooser.setSelected(value);
    return(chooser);
  }

  /**
   *
   */
  public void onUpdate(String values[], IWContext iwc) {

  }
}
