/*
 * $Id: PageNameHandler.java,v 1.1 2001/12/12 21:06:32 palli Exp $
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
import com.idega.presentation.ui.TextInput;

/**
 * @author <a href="mail:palli@idega.is">Pall Helgason</a>
 * @version 1.0
 */
public class PageNameHandler implements PropertyHandler {
  /**
   *
   */
  public PageNameHandler() {
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
  public PresentationObject getHandlerObject(String name, String stringValue, IWContext iwc) {
    TextInput input = new TextInput(name);
    input.setValue(stringValue);

    return(input);
  }

  /**
   *
   */
  public void onUpdate(String values[], IWContext iwc) {
    System.out.println("Getting to onUpdate in PageNameHandler");
    System.out.println("values = " + values);
  }
}