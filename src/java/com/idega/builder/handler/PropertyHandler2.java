package com.idega.builder.handler;

import java.util.Map;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.IWContext;

/**
 * Title:        idegaclasses
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href="tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */

interface PropertyHandler2 extends PropertyHandler {

  /**
   * Returns an instance of the GUI Widget that handles the setting
   */
  public PresentationObject getHandlerObject(Map contextMap,IWContext iwc);



}