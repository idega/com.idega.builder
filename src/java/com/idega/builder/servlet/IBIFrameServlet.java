package com.idega.builder.servlet;


import com.idega.builder.business.BuilderLogic;
import com.idega.core.builder.business.ICBuilderConstants;
import com.idega.presentation.IWContext;
import com.idega.presentation.Page;
import com.idega.presentation.PresentationObject;
import com.idega.servlet.IWJSPPresentationServlet;

/**
*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
*@version 1.0 alpha
*/

public class IBIFrameServlet extends IWJSPPresentationServlet {



  public void initializePage(){
    BuilderLogic blogic = BuilderLogic.getInstance();
    IWContext iwc = getIWContext();
    /**
     * @todo change from hardcoded domain_id
     */
    //int domain_id=1;
/*    int i_page_id=1;
    String page_id = null;
    boolean builderview = false;
    boolean inBuilder = false;
*/    /*
    if (iwc.isParameterSet("view")) {
      if(blogic.isBuilderApplicationRunning(iwc)){
        inBuilder=true;
        String view = iwc.getParameter("view");
        if(view.equals("builder"))
          builderview=true;
      }
    }
*/
/*    if(inBuilder){
      page_id = (String) iwc.getSessionAttribute(BuilderLogic.SESSION_PAGE_KEY);
    }
    else{
      page_id = iwc.getParameter(com.idega.builder.business.BuilderLogic.IB_PAGE_PARAMETER);
    }

    //if(page_id == null){
      if(page_id==null){
        //try{
          //IBDomain domain = com.idega.builder.data.IBDomainBMPBean.getDomain(domain_id);
          IBDomain domain = BuilderLogic.getInstance().getCurrentDomain(iwc);
          i_page_id = domain.getStartPageID();
        //}
        //catch(java.sql.SQLException e){
        //  e.printStackTrace();
        //}
      }
      else{
        i_page_id = Integer.parseInt(page_id);
      }*/
    /*}
    else {
      //iwc.setSessionAttribute(BuilderLogic.SESSION_PAGE_KEY,page_id);
      i_page_id = Integer.parseInt(page_id);
    }*/

//    Page parentPage = blogic.getPage(i_page_id,builderview,iwc);
/*
    //temp
    System.err.println("PageObjects bengin");
    System.err.println("Page id = "+parentPage.getPageID());
    List list = parentPage.getAllContainedObjectsRecursive();
    if(list != null){
      Iterator iter = list.iterator();
      while (iter.hasNext()) {
        Object item = iter.next();
        if(item instanceof PresentationObject){
          System.err.println("object id = "+((PresentationObject)item).getICObjectInstanceID());
        }else{
          System.err.println("item not instance of PresentationObject");
        }
      }
    } else {
      System.err.println("parentPage.getAllContainingObjects() == null");
    }
    System.err.println("PageObjects end");
    //temp
*/
    int instanceId = Integer.parseInt(iwc.getParameter(ICBuilderConstants.IC_OBJECT_INSTANCE_ID_PARAMETER));

    //PresentationObject obj = parentPage.getContainedICObjectInstance(instanceId);
    PresentationObject iframeContent = null;


/*
    if(obj instanceof IFrameContainer && obj != null){
      iframeContent = ((IFrameContainer)obj).getIFrameContent();
    }
    */

    iframeContent = blogic.getIFrameContent(instanceId,iwc);
    Page pageToPrint = null;
    if(iframeContent != null){
      if(iframeContent instanceof Page){
       pageToPrint = ((Page)iframeContent);
      } else {
        pageToPrint = new Page();
        pageToPrint.setAllMargins(0);
        pageToPrint.add(iframeContent);
      }
    } else {
      pageToPrint = new Page();
      pageToPrint.add("Empty");
    }
    /*
    try{
      pageToPrint.setPageID(Integer.parseInt(iwc.getCurrentIBPage()));
    } catch (NumberFormatException e){
      //
    }
    */
    setPage(pageToPrint);

  }



  public void increaseHistoryID(IWContext  iwc){
    // do nothing
  }

  public void handleEvent(IWContext  iwc){
    // do nothing
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

    //IBPage ib_page = ((com.idega.builder.data.IBPageHome)com.idega.data.IDOLookup.getHomeLegacy(IBPage.class)).findByPrimaryKeyLegacy(id);
    //IBAdminWindow window = new IBAdminWindow();

    try {

    }
    catch(Exception ex) {
      add("villa 1");
      System.err.println("ERROR!!!!!!");
      ex.printStackTrace(iwc.getResponse().getWriter());
    }

    //AdminButton form = new AdminButton("B�ta vi�",window);
    if (isAdmin) {
      AdminButton form = new AdminButton(new Image("/common/pics/arachnea/add.gif"),window);
      form.addParameter("ib_window_action","window1");
      form.addParameter(new Parameter("page_id",Integer.toString(id)));

      //Form form = new Form(new Window("Baeta","window1.jsp"));
      //form.add(new SubmitButton("B�ta vi�"));
      //form.add(new Parameter("page_id",""+id));

      add(form);
    }

  }

*/

}
