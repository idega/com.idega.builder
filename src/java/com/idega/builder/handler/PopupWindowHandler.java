/*
 * $Id: PopupWindowHandler.java,v 1.7 2007/05/24 11:31:12 valdas Exp $
 *
 * Copyright (C) 2001 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package com.idega.builder.handler;

import java.util.List;

import com.idega.builder.presentation.PopupWindowChooser;
import com.idega.builder.presentation.PopupWindowChooserWindow;
import com.idega.core.builder.presentation.ICPropertyHandler;
import com.idega.presentation.IWContext;
import com.idega.presentation.PresentationObject;
import com.idega.util.text.TextSoap;

/**
 * @author <a href="tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */
public class PopupWindowHandler implements ICPropertyHandler {
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
  public PresentationObject getHandlerObject(String name, String value, IWContext iwc, boolean oldGenerationHandler, String instanceId, String method) {
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
