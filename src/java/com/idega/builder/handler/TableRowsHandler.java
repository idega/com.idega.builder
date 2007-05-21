/*
 * $Id: TableRowsHandler.java,v 1.7 2007/05/21 09:57:01 valdas Exp $
 *
 * Copyright (C) 2001 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package com.idega.builder.handler;

import java.util.List;

import com.idega.builder.presentation.TableRowColumnPropertyPresentation;
import com.idega.core.builder.presentation.ICPropertyHandler;
import com.idega.presentation.IWContext;
import com.idega.presentation.PresentationObject;

/**
 * @author <a href="tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */
public class TableRowsHandler implements ICPropertyHandler {
  /**
   *
   */
  public TableRowsHandler() {
  }

  /**
   *
   */
  public List getDefaultHandlerTypes() {
    return(null);
  }

  public PresentationObject getHandlerObject(String name, String value, IWContext iwc, boolean oldGenerationHandler) {
    TableRowColumnPropertyPresentation menu = new TableRowColumnPropertyPresentation(name,value,iwc);
    return(menu);
  }

  /**
   *
   */
  public void onUpdate(String values[], IWContext iwc) {
  }
}
