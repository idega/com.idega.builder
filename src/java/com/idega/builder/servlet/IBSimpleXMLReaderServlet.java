package com.idega.builder.servlet;

import com.idega.servlet.IWPresentationServlet;
import com.idega.builder.business.PageCacher;
import com.idega.jmodule.object.Page;
import com.idega.jmodule.object.ModuleInfo;
import java.io.*;
import com.idega.builder.business.BuilderLogic;

/**
 * Title:        idegaclasses
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author
 * @version 1.0
 */

public class IBSimpleXMLReaderServlet extends IWPresentationServlet {

  public IBSimpleXMLReaderServlet(){
  }


  public void initializePage(){
    //String servletName = this.getServletConfig().getServletName();
    //System.out.println("Inside initializePage for "+servletName);
    ModuleInfo modinfo = getModuleInfo();
    boolean builderview=false;
    if(modinfo.isParameterSet("view")){
      //if(modinfo.getParameter("view").equals("builder")){
        builderview=true;
      //}
    }
    String fileName = modinfo.getRequestURI();
    String prefix = modinfo.getApplication().getApplicationRealPath();
    Page page = null;
    try{
      page = PageCacher.getPage(prefix+File.separator+fileName);
    }
    catch(RuntimeException e){
      e.printStackTrace();
    }
    if(builderview){
      setPage(BuilderLogic.getInstance().getBuilderTransformed(page,modinfo));
    }
    else{
      setPage(page);
    }
    //setPage(new Page());
  }




}