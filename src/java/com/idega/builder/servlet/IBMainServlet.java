/*
 * $Id: IBMainServlet.java,v 1.19 2002/02/27 00:04:30 aron Exp $
 *
 * Copyright (C) 2001 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package com.idega.builder.servlet;


import com.idega.servlet.IWJSPPresentationServlet;
import com.idega.presentation.IWContext;

import com.idega.builder.business.BuilderLogic;
import com.idega.builder.data.*;

/**
*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
*@version 1.0
*/

public class IBMainServlet extends IWJSPPresentationServlet {

  public void initializePage(){
    BuilderLogic blogic = BuilderLogic.getInstance();
    IWContext iwc = getIWContext();

    String page_id = null;
    boolean builderview = false;
    if (iwc.isParameterSet("view")) {
      if(blogic.isBuilderApplicationRunning(iwc)){
        String view = iwc.getParameter("view");
        if(view.equals("builder"))
          builderview=true;
      }
    }

    int i_page_id = BuilderLogic.getInstance().getCurrentIBPageID(iwc);
    setPage(blogic.getPage(i_page_id,builderview,iwc));
  }
}
