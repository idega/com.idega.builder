/*
 * $Id: IBMainServlet.java,v 1.25 2004/03/31 17:22:28 eiki Exp $
 *
 * Copyright (C) 2001 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package com.idega.builder.servlet;


import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import com.idega.builder.business.BuilderLogic;
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

    //needs to be static because there are at least 2 instances of IBMainServlet , /servlet/IBMainServlet and index.jsp
  private static List initializedPageIDs;
      
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
  

    /* (non-Javadoc)
     * @see com.idega.servlet.IWCoreServlet#getIfSyncronizeAccess(javax.servlet.ServletRequest, javax.servlet.ServletResponse)
     */
    protected boolean getIfSyncronizeAccess(ServletRequest _req,ServletResponse _res) {
        //if using a PageIncluder that calls a page on the same server and the pageincluder page is the first page loaded
        //we will get a deadlock in the super impl of this method. 
        //However one will never create a pageincluder pointing to the page it is contained in so all we need to do is to
        //keep track of what ibpages have been initilized and only synchronize a page when it is first being loaded and not lock out other
        //pages at the same time.
        //eiki@idega.is
        if(initializedPageIDs==null) {
            initializedPageIDs = new ArrayList();
        }
        
        try {
            IWApplicationContext iwac = this.getApplication().getIWApplicationContext();
            BuilderService bs = BuilderServiceFactory.getBuilderService(iwac);
	        IWContext iwc = getIWContext();

	        Integer pageID = new Integer(bs.getCurrentPageId(iwc));
	        if(initializedPageIDs.contains(pageID)) {
	            //no need to synchronize
	            return false;
	        }
	        else {
	            //synchronize but add the id to memory so we don't do it again
	            initializedPageIDs.add(pageID);
	            return true;
	        }
	        
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        
        //if all else failes, use the default
        return super.getIfSyncronizeAccess(_req, _res);
    }
}
