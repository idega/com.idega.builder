/*
 *  $Id: IBToolbarButton.java,v 1.1 2002/03/11 22:58:58 palli Exp $
 *
 *  Copyright (C) 2002 Idega hf. All Rights Reserved.
 *
 *  This software is the proprietary information of Idega hf.
 *  Use is subject to license terms.
 *
 */
package com.idega.builder.app;

import com.idega.presentation.Image;
import com.idega.presentation.text.Link;

/**
 * @author     <a href="mail:palli@idega.is">Pall Helgason</a>
 * @created    11. mars 2002
 * @version    1.0
 */
public interface IBToolbarButton {
  /**
   *  Gets the link attribute of the IBToolbarButton object
   *
   * @return    The link value
   */
  public Link getLink();

  /**
   *  Gets the separator attribute of the IBToolbarButton object
   *
   * @return    The separator value
   */
  public boolean getIsSeparator();
}
