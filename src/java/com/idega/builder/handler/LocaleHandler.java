/*
 * $Id: LocaleHandler.java,v 1.3 2002/01/09 13:25:55 tryggvil Exp $
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
import com.idega.presentation.text.Text;
import com.idega.development.presentation.Localizer;
import com.idega.builder.business.IBPropertyHandler;

/**
 * @author <a href="tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 *
 * This handler is to display a selection with the available Locales.
 * In the selection the keys (values) are the locale-stringrepresentations e.g. "en_US" for English/US
 */
public class LocaleHandler implements PropertyHandler {
  /**
   *
   */
  public LocaleHandler() {
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
    DropdownMenu menu = Localizer.getAvailableLocalesDropdown(iwc.getApplication(),name);
    menu.setSelectedElement(value);
    IBPropertyHandler.getInstance().setDropdownToChangeValue(menu);
    return(menu);
  }

  /**
   *
   */
  public void onUpdate(String values[], IWContext iwc) {
  }
}