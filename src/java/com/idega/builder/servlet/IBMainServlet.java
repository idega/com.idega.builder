/*
 * $Id: IBMainServlet.java,v 1.18 2002/01/11 17:49:56 eiki Exp $
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
*@version 1.0 alpha
*/

public class IBMainServlet extends IWJSPPresentationServlet {



  public void initializePage(){
    BuilderLogic blogic = BuilderLogic.getInstance();
    IWContext iwc = getIWContext();
    /**
     * @todo change from hardcoded domain_id
     */
    //int domain_id=1;
    int i_page_id=BuilderLogic.getInstance().getCurrentDomain(iwc).getStartPageID();

    String page_id = null;
    boolean builderview = false;
    boolean inBuilder = false;
    if (iwc.isParameterSet("view")) {
      if(blogic.isBuilderApplicationRunning(iwc)){
        inBuilder=true;
        String view = iwc.getParameter("view");
        if(view.equals("builder"))
          builderview=true;
      }
    }

    if(inBuilder){
      page_id = (String) iwc.getSessionAttribute(BuilderLogic.SESSION_PAGE_KEY);
    }
    else{
      page_id = iwc.getParameter(com.idega.builder.business.BuilderLogic.IB_PAGE_PARAMETER);
    }

    //if(page_id == null){
      if(page_id==null){
        //try{
          //IBDomain domain = IBDomain.getDomain(domain_id);
          i_page_id = BuilderLogic.getStartPageId(iwc);
        //}
        //catch(java.sql.SQLException e){
        //  e.printStackTrace();
        //}
      }
      else{
        i_page_id = Integer.parseInt(page_id);
      }
    /*}
    else {
      //iwc.setSessionAttribute(BuilderLogic.SESSION_PAGE_KEY,page_id);
      i_page_id = Integer.parseInt(page_id);
    }*/

    setPage(blogic.getPage(i_page_id,builderview,iwc));

  }







/*


    public void __theService(HttpServletRequest request, HttpServletResponse response) throws ServletException,IOException {
      try {
        main(getIWContext());
      }
      catch(SQLException ex) {
        ex.printStackTrace(System.err);
      }
      //_jspService(request,response);
    }

  public void main(IWContext iwc)throws IOException,SQLException {
    //boolean isAdmin = AccessControl.isAdmin(iwc);
    boolean isAdmin = true;
    PrintWriter debugger = iwc.getResponse().getWriter();
    int id;

    String page_id = iwc.getParameter("idegaweb_page_id");
    if(page_id == null){
      id = 1;
    }
    else {
      id = Integer.parseInt(page_id);
    }

    String language = iwc.getParameter("language");
    if(language == null){
      language = "IS";
    }

    //IBPage ib_page = new IBPage(id);
    //IBAdminWindow window = new IBAdminWindow();

    try {

    }
    catch(Exception ex) {
      add("villa 1");
      System.err.println("ERROR!!!!!!");
      ex.printStackTrace(iwc.getResponse().getWriter());
    }

    //AdminButton form = new AdminButton("Bæta við",window);
    if (isAdmin) {
      AdminButton form = new AdminButton(new Image("/common/pics/arachnea/add.gif"),window);
      form.addParameter("ib_window_action","window1");
      form.addParameter(new Parameter("page_id",Integer.toString(id)));

      //Form form = new Form(new Window("Baeta","window1.jsp"));
      //form.add(new SubmitButton("Bæta við"));
      //form.add(new Parameter("page_id",""+id));

      add(form);
    }

  }

*/

}
