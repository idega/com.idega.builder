package com.idega.builder.handler;

import java.util.List;

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

public interface PropertyHandler {

  /**
   * Returns a list of Class Objects this Handler will default handle
   * Can return null if none apply
   */
  public List getDefaultHandlerTypes();

  /**
   * Returns an instance of the GUI Widget that handles the setting
   */
  public PresentationObject getHandlerObject(String name,String stringValue,IWContext iwc);

}