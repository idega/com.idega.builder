/*
 * $Id: ColorHandler.java,v 1.8 2003/04/03 09:10:10 laddi Exp $
 *
 * Copyright (C) 2001 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package com.idega.builder.handler;

import java.util.List;

import com.idega.builder.presentation.IBColorChooser;
import com.idega.presentation.IWContext;
import com.idega.presentation.PresentationObject;

/**
 * @author <a href="tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */
public class ColorHandler implements PropertyHandler {
  /**
   *
   */
  public ColorHandler() {
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
    IBColorChooser chooser = new IBColorChooser(name,"font-size: 8pt; border: 1 solid #000000");
    chooser.setSelected(value);
    return(chooser);
  }

  /**
   *
   */
  public void onUpdate(String values[], IWContext iwc) {
  }
}
