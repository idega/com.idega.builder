/*
 * $Id: IBMainServlet.java,v 1.23 2003/08/05 19:45:36 tryggvil Exp $
 *
 * Copyright (C) 2001 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package com.idega.builder.servlet;


import com.idega.builder.business.BuilderLogic;
import com.idega.business.IBOLookup;
import com.idega.core.builder.business.BuilderService;
import com.idega.core.builder.business.BuilderServiceFactory;
import com.idega.idegaweb.IWApplicationContext;
import com.idega.presentation.IWContext;
import com.idega.servlet.IWJSPPresentationServlet;

/**
*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
*@version 1.0
*/

public class IBMainServlet extends IWJSPPresentationServlet {

  public void initializePage()throws Exception{
  	setHandleJSPTags(false);
  	IWApplicationContext iwac = this.getApplication().getIWApplicationContext();
  	BuilderLogic blogic = BuilderLogic.getInstance();
  	BuilderService bs = BuilderServiceFactory.getBuilderService(iwac);
    IWContext iwc = getIWContext();

    boolean builderview = false;
    if (iwc.isParameterSet("view")) {
      if(blogic.isBuilderApplicationRunning(iwc)){
        String view = iwc.getParameter("view");
        if(view.equals("builder"))
          builderview=true;
      }
    }

    int i_page_id = bs.getCurrentPageId(iwc);
    setPage(blogic.getPage(i_page_id,builderview,iwc));
  }
  

}
