/*
 * $Id: IBMainServlet.java,v 1.22 2003/04/03 19:54:57 laddi Exp $
 *
 * Copyright (C) 2001 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package com.idega.builder.servlet;


import com.idega.builder.business.BuilderLogic;
import com.idega.presentation.IWContext;
import com.idega.servlet.IWJSPPresentationServlet;

/**
*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
*@version 1.0
*/

public class IBMainServlet extends IWJSPPresentationServlet {

  public void initializePage(){
  	setHandleJSPTags(false);
    BuilderLogic blogic = BuilderLogic.getInstance();
    IWContext iwc = getIWContext();

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
