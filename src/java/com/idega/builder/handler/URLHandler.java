/*
 * $Id: URLHandler.java,v 1.2 2001/12/12 21:06:32 palli Exp $
 *
 * Copyright (C) 2001 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package com.idega.builder.handler;

import java.util.List;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.IWContext;
import com.idega.presentation.ui.DropdownMenu;
import com.idega.builder.presentation.IBPageChooser;

/**
 * @author <a href="tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */
public class URLHandler implements PropertyHandler {
  /**
   *
   */
  public URLHandler() {
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
    IBPageChooser chooser = new IBPageChooser(name);
    return(chooser);
  }

  /**
   *
   */
  public void onUpdate(String values[], IWContext iwc) {
  }
}