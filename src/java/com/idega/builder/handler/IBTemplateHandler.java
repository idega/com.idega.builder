/*
 * $Id: IBTemplateHandler.java,v 1.1 2001/11/03 13:05:50 palli Exp $
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
import com.idega.builder.presentation.IBTemplateChooser;

/**
 * @author <a href="palli@idega.is">Pall Helgason</a>
 * @version 1.0
 */
public class IBTemplateHandler implements PropertyHandler {

  public IBTemplateHandler() {
  }

  public List getDefaultHandlerTypes() {
    return(null);
  }

  public PresentationObject getHandlerObject(String name, String value, IWContext iwc) {
    IBTemplateChooser chooser = new IBTemplateChooser(name);
    return(chooser);
  }
}