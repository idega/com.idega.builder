/*
 * $Id: IBPageUpdater.java,v 1.2 2002/04/06 19:07:38 tryggvil Exp $
 *
 * Copyright (C) 2001 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package com.idega.builder.business;

import com.idega.builder.data.IBPage;
import java.sql.SQLException;

/**
 * @author <a href="mail:palli@idega.is">Pall Helgason</a>
 * @version 1.0
 */
public class IBPageUpdater {
  /**
   *
   */
  public static void updatePageName(int pageId, String pageName) {
    try {
      IBPage page = ((com.idega.builder.data.IBPageHome)com.idega.data.IDOLookup.getHomeLegacy(IBPage.class)).findByPrimaryKeyLegacy(pageId);

      page.setName(pageName);
      page.update();
    }
    catch(SQLException e) {
      e.printStackTrace();
    }
  }

  /**
   *
   */
  public static void updateTemplateId(int pageId, int templateId) {
    try {
      IBPage page = ((com.idega.builder.data.IBPageHome)com.idega.data.IDOLookup.getHomeLegacy(IBPage.class)).findByPrimaryKeyLegacy(pageId);

      page.setTemplateId(templateId);
      page.update();
    }
    catch(SQLException e) {
      e.printStackTrace();
    }
  }
}
