/*
 * $Id: IBApplication.java,v 1.44 2001/12/19 17:37:07 eiki Exp $
 *
 * Copyright (C) 2001 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package com.idega.builder.app;

import com.idega.builder.business.BuilderLogic;
import com.idega.builder.presentation.IBCreatePageWindow;
import com.idega.builder.presentation.IBPropertiesWindow;
import com.idega.builder.presentation.IBDeletePageWindow;
import com.idega.builder.presentation.IBSaveAsPageWindow;
import com.idega.builder.presentation.IBSavePageWindow;
import com.idega.builder.presentation.IBSourceView;
import com.idega.idegaweb.IWConstants;
import com.idega.presentation.app.IWApplication;
import com.idega.presentation.app.IWApplicationComponent;
import com.idega.presentation.FrameSet;
import com.idega.presentation.Page;
import com.idega.presentation.IWContext;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.Image;
import com.idega.presentation.Table;
import com.idega.presentation.text.Link;
import com.idega.presentation.text.Text;
import com.idega.presentation.ui.TreeViewer;
import com.idega.presentation.ui.IFrame;
import com.idega.idegaweb.IWBundle;
import com.idega.builder.presentation.IBPermissionWindow;
import com.idega.builder.business.PageTreeNode;

import java.util.Vector;
import java.util.List;
import java.util.Iterator;

/**
 * @author <a href="tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */
public class IBApplication extends IWApplication {
  private final static String IB_FRAMESET1_FRAME = "ib_frameset1";
  private final static String IB_FRAMESET2_FRAME = "ib_frameset2";
  private final static String IB_TOOLBAR_FRAME = "ib_toolbar";
  private final static String IB_CONTENT_FRAME = "ib_content";
  private final static String IB_STATUS_FRAME = "ib_status";
  private final static String IB_LEFT_MENU_FRAME = "ib_left_menu";

  private final static String ACTION_BUILDER = "builder";
  private final static String ACTION_TEMPLATES = "templates";
  private final static String ACTION_SETTINGS = "settings";
  private final static String ACTION_HELP = "help";

  private static boolean noCurtain = false;
  private static String URL = "";

  private static final String IB_BUNDLE_IDENTIFIER = "com.idega.builder";

  private final static String CONTENT_PREVIEW_URL = com.idega.idegaweb.IWMainApplication.BUILDER_SERVLET_URL + "?view=preview";
  private final static String CONTENT_EDIT_URL = com.idega.idegaweb.IWMainApplication.BUILDER_SERVLET_URL + "?view=builder";

  /**
   *
   */
  public IBApplication() {
    super("idegaWeb Builder");
    super.setResizable(true);
    super.setWidth(900);
    super.setHeight(700);
  }

  static boolean startupInProgress(IWContext iwc){
    List l = (List)iwc.getSessionAttribute("ib_startup_class_list");
    if(l==null){
      return false;
    }
    return true;
  }

  static void endStartup(IWContext iwc,Class c){
    List l = (List)iwc.getSessionAttribute("ib_startup_class_list");
    if(l!=null){
      l.remove(c);
      Iterator iter = l.iterator();
      if(!iter.hasNext()){
        iwc.removeSessionAttribute("ib_startup_class_list");
      }
    }
  }

  //To prevent constant realoding when many frames are loaded at the same time
  static void startIBApplication(IWContext iwc){
    BuilderLogic.getInstance().startBuilderSession(iwc);
    List l = (List)iwc.getSessionAttribute("ib_startup_class_list");
    if(l==null){
      l = new Vector();
      iwc.setSessionAttribute("ib_startup_class_list",l);
    }
    l.add(IBToolBar.class);
  }

  static void startLeftMenu(IWContext iwc){
    List l = (List)iwc.getSessionAttribute("ib_startup_class_list");
    if(l==null){
      l = new Vector();
      iwc.setSessionAttribute("ib_startup_class_list",l);
    }
    l.add(PageTree.class);
    l.add(TemplateTree.class);
//    l.add(LibraryTree.class);
  }



  public void main(IWContext iwc) {
    startIBApplication(iwc);
    add(IBBanner.class);
    if ( iwc.getParameter("toolbar") != null ) {
      iwc.setSessionAttribute("toolbar",iwc.getParameter("toolbar"));
    }
    if ( iwc.getSessionAttribute("toolbar") != null ) {
      String toolbar = (String) iwc.getSessionAttribute("toolbar");
      if ( toolbar.equalsIgnoreCase("remove") ) {
        noCurtain = true;
      }
      else if ( toolbar.equalsIgnoreCase("add") ) {
        noCurtain = false;
      }
    }

    URL = iwc.getRequestURI();

    if ( noCurtain ) {
      add(FrameSet2.class);
      setScrolling(2,true);
    }
    else {
      add(FrameSet1.class);
      setScrolling(2,false);
    }

    setSpanPixels(1,34);
    setScrolling(1,false);
    setSpanAdaptive(2);
    setFrameName(2,IB_FRAMESET1_FRAME);
  }

  /**
   *
   */
  public static class FrameSet1 extends FrameSet {
    /**
     *
     */
    public FrameSet1() {
      add(IBLeftMenu.class);
      add(FrameSet2.class);
      setScrolling(1,false);
      setFrameName(1,IB_LEFT_MENU_FRAME);
      setFrameName(2,IB_FRAMESET2_FRAME);
      setSpanPixels(1,180);
      setSpanAdaptive(2);
      setNoresize(1,true);
      setNoresize(2,true);
      setHorizontal();
    }
  }

  /**
   *
   */
  public static class FrameSet2 extends FrameSet {
    /**
     *
     */
    public FrameSet2(){
      add(IBToolBar.class);
      add(CONTENT_EDIT_URL);
      add(IBStatusBar.class);
      super.setFrameName(1,IB_TOOLBAR_FRAME);
      super.setFrameName(2,IB_CONTENT_FRAME);
      super.setFrameName(3,IB_STATUS_FRAME);
      setNoresize(1,true);
      setNoresize(3,true);
      setSpanPixels(1,24);
      setScrolling(1,false);
      setScrolling(2,true);
      setSpanAdaptive(2);
      setSpanPixels(3,25);
      setScrolling(3,false);
    }
  }

  /**
   *
   */
  public static class IBBanner extends Page {
    /**
     *
     */
    public IBBanner() {
    }

    public void main(IWContext iwc){
      this.setAllMargins(0);
      setBackgroundColor("#0E2456");
      //setBackgroundColor("#FFFFFF");
      Table table = new Table(2,1);
        table.setCellpadding(0);
        table.setCellspacing(0);
      add(table);
      Image image = iwc.getApplication().getBundle(IB_BUNDLE_IDENTIFIER).getImage("shared/banner/logo.gif");
      table.add(image,1,1);
      Image image2 = iwc.getApplication().getBundle(IB_BUNDLE_IDENTIFIER).getImage("shared/banner/top_image.gif");
      table.add(image2,2,1);
      table.setWidth("100%");
      table.setHeight("100%");
      table.setAlignment(2,1,"right");
      table.setVerticalAlignment(2,1,"bottom");
      table.setCellpadding(0);
      table.setCellpadding(0);
    }
  }

  public static class PageTree extends Page {
    public PageTree() {
    }

    public void main(IWContext iwc){

      boolean startupInProgress = startupInProgress(iwc);
      //System.out.println("Startup in progress for PageTree:"+startupInProgress);
      if(!startupInProgress && iwc.getParameter("reload") != null){
        if ( noCurtain ) {
          getParentPage().setOnLoad("parent.frames['"+IB_FRAMESET2_FRAME+"'].frames['"+IB_CONTENT_FRAME+"'].location.reload()");
          getParentPage().setOnLoad("parent.frames['"+IB_FRAMESET2_FRAME+"'].frames['"+IB_STATUS_FRAME+"'].location.reload()");
        }
        else {
          getParentPage().setOnLoad("parent.parent.frames['"+IB_FRAMESET2_FRAME+"'].frames['"+IB_CONTENT_FRAME+"'].location.reload()");
          getParentPage().setOnLoad("parent.parent.frames['"+IB_FRAMESET2_FRAME+"'].frames['"+IB_STATUS_FRAME+"'].location.reload()");
        }
      }
      getParentPage().setAllMargins(2);
      //int i_page_id = 1;
      int i_page_id = BuilderLogic.getInstance().getCurrentDomain(iwc).getStartPageID();

      try {
//        TreeViewer viewer = TreeViewer.getTreeViewerInstance(new com.idega.builder.data.IBPage(i_page_id),iwc);
        TreeViewer viewer = TreeViewer.getTreeViewerInstance(new PageTreeNode(i_page_id,iwc),iwc);
        //viewer.setTarget(IB_LEFT_MENU_FRAME);
        viewer.setNodeActionParameter(com.idega.builder.business.BuilderLogic.IB_PAGE_PARAMETER);
        Link l = new Link();
        l.maintainParameter(Page.IW_FRAME_CLASS_PARAMETER,iwc);
        l.addParameter("reload","t");
//        l.setOnClick("parent.parent.frames['"+IB_FRAMESET2_FRAME+"'].frames['"+IB_TOOLBAR_FRAME+"'].location.reload()");
        viewer.setToMaintainParameter(Page.IW_FRAME_CLASS_PARAMETER,iwc);
        viewer.setTreeStyle("font-face: Verdana, Arial, sans-serif; font-size: 8pt; text-decoration: none;");

        viewer.setLinkPrototype(l);
        add(viewer);

        String page_id = iwc.getParameter(com.idega.builder.business.BuilderLogic.IB_PAGE_PARAMETER);
        if (page_id != null) {
          iwc.setSessionAttribute(com.idega.builder.business.BuilderLogic.SESSION_PAGE_KEY,page_id);
        }
      }
      catch (Exception e) {
        e.printStackTrace(System.err);
      }
      endStartup(iwc,PageTree.class);
    }
  }

  public static class TemplateTree extends Page {
    public TemplateTree() {
    }

    public void main(IWContext iwc){
      boolean startupInProgress = startupInProgress(iwc);
      //System.out.println("Startup in progress for TemplateTree:"+startupInProgress);
      if(!startupInProgress && iwc.getParameter("reload") != null){
        if ( noCurtain ) {
          getParentPage().setOnLoad("parent.frames['"+IB_FRAMESET2_FRAME+"'].frames['"+IB_CONTENT_FRAME+"'].location.reload()");
          getParentPage().setOnLoad("parent.frames['"+IB_FRAMESET2_FRAME+"'].frames['"+IB_STATUS_FRAME+"'].location.reload()");
        }
        else {
          getParentPage().setOnLoad("parent.parent.frames['"+IB_FRAMESET2_FRAME+"'].frames['"+IB_CONTENT_FRAME+"'].location.reload()");
          getParentPage().setOnLoad("parent.parent.frames['"+IB_FRAMESET2_FRAME+"'].frames['"+IB_STATUS_FRAME+"'].location.reload()");
        }
      }
      getParentPage().setAllMargins(2);

      //int i_template_id = 2;
      int i_template_id = BuilderLogic.getInstance().getCurrentDomain(iwc).getStartTemplateID();

      try {
//        TreeViewer viewer = TreeViewer.getTreeViewerInstance(new com.idega.builder.data.IBPage(i_template_id),iwc);
        TreeViewer viewer = TreeViewer.getTreeViewerInstance(new PageTreeNode(i_template_id,iwc),iwc);
        //viewer.setTarget(IB_LEFT_MENU_FRAME);
        viewer.setNodeActionParameter(com.idega.builder.business.BuilderLogic.IB_PAGE_PARAMETER);
        Link l = new Link();
        l.maintainParameter(Page.IW_FRAME_CLASS_PARAMETER,iwc);
        l.addParameter("reload","t");
        viewer.setToMaintainParameter(Page.IW_FRAME_CLASS_PARAMETER,iwc);
        viewer.setTreeStyle("font-face: Verdana, Arial, sans-serif; font-size: 8pt; text-decoration: none;");

        viewer.setLinkPrototype(l);
        add(viewer);

        String page_id = iwc.getParameter(com.idega.builder.business.BuilderLogic.IB_PAGE_PARAMETER);
        if (page_id != null) {
          iwc.setSessionAttribute(com.idega.builder.business.BuilderLogic.SESSION_PAGE_KEY,page_id);
        }
      }
      catch (Exception e) {
        e.printStackTrace(System.err);
      }
      endStartup(iwc,TemplateTree.class);
    }
  }

  /**
   *
   */
/*  public static class LibraryTree extends Page {
    public LibraryTree() {
    }

    public void main(IWContext iwc){
      boolean startupInProgress = startupInProgress(iwc);
      if (!startupInProgress && iwc.getParameter("reload") != null) {
        if (noCurtain) {
          getParentPage().setOnLoad("parent.frames['"+IB_FRAMESET2_FRAME+"'].frames['"+IB_CONTENT_FRAME+"'].location.reload()");
          getParentPage().setOnLoad("parent.frames['"+IB_FRAMESET2_FRAME+"'].frames['"+IB_STATUS_FRAME+"'].location.reload()");
        }
        else {
          getParentPage().setOnLoad("parent.parent.frames['"+IB_FRAMESET2_FRAME+"'].frames['"+IB_CONTENT_FRAME+"'].location.reload()");
          getParentPage().setOnLoad("parent.parent.frames['"+IB_FRAMESET2_FRAME+"'].frames['"+IB_STATUS_FRAME+"'].location.reload()");
        }
      }
      getParentPage().setAllMargins(2);

      int i_template_id = BuilderLogic.getInstance().getCurrentDomain(iwc).getStartTemplateID();

      try {
        TreeViewer viewer = TreeViewer.getTreeViewerInstance(new PageTreeNode(i_template_id,iwc),iwc);
//        vi
        viewer.setNodeActionParameter(com.idega.builder.business.BuilderLogic.IB_PAGE_PARAMETER);
        Link l = new Link();
        l.maintainParameter(Page.IW_FRAME_CLASS_PARAMETER,iwc);
        l.addParameter("reload","t");
        viewer.setToMaintainParameter(Page.IW_FRAME_CLASS_PARAMETER,iwc);
        viewer.setTreeStyle("font-face: Verdana, Arial, sans-serif; font-size: 8pt; text-decoration: none;");

        viewer.setLinkProtototype(l);
        add(viewer);

        String page_id = iwc.getParameter(com.idega.builder.business.BuilderLogic.IB_PAGE_PARAMETER);
        if (page_id != null) {
          iwc.setSessionAttribute(com.idega.builder.business.BuilderLogic.SESSION_PAGE_KEY,page_id);
        }
      }
      catch (Exception e) {
        e.printStackTrace(System.err);
      }
      endStartup(iwc,LibraryTree.class);
    }
  }*/

  /**
   *
   */
  public static class IBLeftMenu extends IWApplicationComponent {
    /**
     *
     */
    public IBLeftMenu() {
    }

    public void main(IWContext iwc){
      startLeftMenu(iwc);
      setAlignment("left");
      setVerticalAlignment("top");
      setBackgroundColor(IWConstants.DEFAULT_INTERFACE_COLOR);
      setLightShadowColor(IWConstants.DEFAULT_LIGHT_INTERFACE_COLOR);
      setDarkShadowColor(IWConstants.DEFAULT_DARK_INTERFACE_COLOR);

      try {
        Table menuTable = new Table();
          menuTable.setAlignment(1,1,"right");

        Image closeImage = iwc.getApplication().getBundle(IB_BUNDLE_IDENTIFIER).getImage("toolbar_remove.gif","toolbar_remove_1.gif","Hide Curtain",16,16);
          closeImage.setAlignment("right");

        Link closeLink = new Link(closeImage);
          closeLink.setTarget(Link.TARGET_TOP_WINDOW);
          closeLink.addParameter("toolbar","remove");
          closeLink.addParameter(Page.IW_FRAME_CLASS_PARAMETER,IBApplication.class);

        menuTable.add(closeLink,1,1);

        Text pageText = new Text("Page Tree:");
          pageText.setFontSize(1);
        Text templateText = new Text("Template Tree:");
          templateText.setFontSize(1);
        Text libraryText = new Text("Library Tree:");
        libraryText.setFontSize(1);

        IFrame frame = new IFrame("PageTree",PageTree.class);
          frame.setWidth(170);
          frame.setHeight(200);
          frame.setScrolling(IFrame.SCROLLING_YES);
        menuTable.add(pageText,1,2);
        menuTable.add(Text.getBreak(),1,2);
        menuTable.add(frame,1,2);

        IFrame frame2 = new IFrame("TemplateTree",TemplateTree.class);
          frame2.setWidth(170);
          frame2.setHeight(200);
          frame2.setScrolling(IFrame.SCROLLING_YES);
        menuTable.add(templateText,1,3);
        menuTable.add(Text.getBreak(),1,3);
        menuTable.add(frame2,1,3);

/*        IFrame frame3 = new IFrame("LibraryTree",LibraryTree.class);
          frame3.setWidth(170);
          frame3.setHeight(200);
          frame3.setScrolling(IFrame.SCROLLING_YES);
        menuTable.add(libraryText,1,4);
        menuTable.add(Text.getBreak(),1,4);
        menuTable.add(frame3,1,4);*/

        add(menuTable);
      }
      catch(Exception e) {
        e.printStackTrace();
      }
    }
  }

  /**
   *
   */
  public static class IBToolBar extends IWApplicationComponent {
    /**
     *
     */
    public IBToolBar() {
    }

    /**
     *
     */
    public void main(IWContext iwc) {

      boolean startupInProgress = startupInProgress(iwc);
      //System.out.println("Startup in progress for IBToolBar:"+startupInProgress);
      if(!startupInProgress){
        super.setOnLoad("parent.parent.frames['"+IB_LEFT_MENU_FRAME+"'].location.reload();parent.frames['"+IB_CONTENT_FRAME+"'].location.reload()");
        //super.setOnLoad("parent.parent.frames['"+IB_LEFT_MENU_FRAME+"'].location.reload()");
      }
      IWBundle iwb = iwc.getApplication().getBundle(IB_BUNDLE_IDENTIFIER);
      String controlParameter = "builder_controlparameter";
      setBackgroundColor(IWConstants.DEFAULT_INTERFACE_COLOR);
      setLightShadowColor(IWConstants.DEFAULT_LIGHT_INTERFACE_COLOR);
      setDarkShadowColor(IWConstants.DEFAULT_DARK_INTERFACE_COLOR);

      //setBackgroundColor(com.idega.idegaweb.IWConstants.DEFAULT_LIGHT_INTERFACE_COLOR);
      //Image background = iwb.getImage("toolbar_tiler.gif");
      //setBackgroundImage(background);
      setAllMargins(0);

      String action = iwc.getParameter(controlParameter);
      if (action == null) {
        action=ACTION_BUILDER;
      }

      if (action.equals(ACTION_BUILDER)) {
        int xpos = 1;
        Table toolbarTable = new Table();
          toolbarTable.setCellpadding(0);
          toolbarTable.setCellspacing(0);

        Image separator = iwb.getImage("toolbar_separator.gif");

        Image tool_new = iwb.getImage("shared/toolbar/new.gif","shared/toolbar/new1.gif","New Page",20,20);
        tool_new.setHorizontalSpacing(2);
        Link link_new = new Link(tool_new);
        link_new.setWindowToOpen(IBCreatePageWindow.class);
        toolbarTable.add(link_new,xpos,1);

        Image tool_open = iwb.getImage("shared/toolbar/open.gif","shared/toolbar/open1.gif","Open Page",20,20);
        tool_open.setHorizontalSpacing(2);
        Link link_open = new Link(tool_open);
        toolbarTable.add(link_open,xpos,1);

        Image tool_save = iwb.getImage("shared/toolbar/save.gif","shared/toolbar/save1.gif","Save Page",20,20);
        tool_save.setHorizontalSpacing(2);
        Link link_save = new Link(tool_save);
        link_save.setWindowToOpen(IBSavePageWindow.class);
        toolbarTable.add(link_save,xpos,1);

        Image tool_save_as = iwb.getImage("shared/toolbar/saveas.gif","shared/toolbar/saveas1.gif","Save As Page",20,20);
        tool_save_as.setHorizontalSpacing(2);
        Link link_save_as = new Link(tool_save_as);
        link_save_as.setWindowToOpen(IBSaveAsPageWindow.class);
        toolbarTable.add(link_save_as,xpos,1);

        Image tool_delete = iwb.getImage("shared/toolbar/delete.gif","shared/toolbar/delete1.gif","Delete Page",20,20);
        tool_delete.setHorizontalSpacing(2);
        Link link_delete = new Link(tool_delete);
        link_delete.setWindowToOpen(IBDeletePageWindow.class);
        toolbarTable.add(link_delete,xpos,1);

        PresentationObject propertiesIcon = getPropertiesIcon(iwc);
        toolbarTable.add(propertiesIcon,xpos,1);

        PresentationObject permissionIcon = getPermissionIcon(iwc);
        toolbarTable.add(permissionIcon,xpos,1);

        xpos++;
        toolbarTable.add(separator,xpos,1);

        xpos++;
        Image tool_1 = iwb.getImage("shared/toolbar/back.gif","shared/toolbar/back1.gif","Go back",20,20);
        tool_1.setHorizontalSpacing(2);
        Link link_1 = new Link(tool_1);
        link_1.setURL("javascript:parent.frames['"+IB_CONTENT_FRAME+"'].history.go(-1)");
        toolbarTable.add(link_1,xpos,1);

        Image tool_2 = iwb.getImage("shared/toolbar/forward.gif","shared/toolbar/forward1.gif","Go forward",20,20);
        tool_2.setHorizontalSpacing(2);
        Link link_2 = new Link(tool_2);
        link_2.setURL("javascript:parent.frames['"+IB_CONTENT_FRAME+"'].history.go(1)");
        toolbarTable.add(link_2,xpos,1);

        Image tool_3 = iwb.getImage("shared/toolbar/stop.gif","shared/toolbar/stop1.gif","Stop loading",20,20);
        tool_3.setHorizontalSpacing(2);
        Link link_3 = new Link(tool_3);
        link_3.setURL("javascript:parent.frames['"+IB_CONTENT_FRAME+"'].stop()");
        toolbarTable.add(link_3,xpos,1);

        Image tool_4 = iwb.getImage("shared/toolbar/refresh.gif","shared/toolbar/refresh1.gif","Reload page",20,20);
        tool_4.setHorizontalSpacing(2);
        Link link_4 = new Link(tool_4);
        link_4.setURL("javascript:parent.frames['"+IB_CONTENT_FRAME+"'].location.reload()");
        toolbarTable.add(link_4,xpos,1);

        /*Image tool_5 = iwb.getImage("shared/toolbar/home.gif","shared/toolbar/home1.gif","Go to startpage",20,20);
        Link link_5 = new Link(tool_5);
        link_5.setURL("javascript:parent.frames['"+IB_CONTENT_FRAME+"'].location.href='"+CONTENT_EDIT_URL+"'");
        toolbarTable.add(link_5,xpos,1);*/
        /*Image tool_5 = iwb.getImage("shared/toolbar/home.gif","",20,20);
        toolbarTable.add(tool_5,xpos,1);*/

        xpos++;
        toolbarTable.add(separator,xpos,1);

        Image leftMenuImage = null;
        if ( noCurtain )
          leftMenuImage = iwb.getImage("shared/toolbar/show_curtain.gif","shared/toolbar/show_curtain1.gif","Show Curtain",20,20);
        else
          leftMenuImage = iwb.getImage("shared/toolbar/no_curtain.gif","shared/toolbar/no_curtain1.gif","Show Curtain",20,20);
        leftMenuImage.setHorizontalSpacing(2);

        Link leftMenuLink = new Link(leftMenuImage);
          leftMenuLink.setTarget(Link.TARGET_TOP_WINDOW);
          if ( noCurtain )
            leftMenuLink.addParameter("toolbar","add");
          else
            leftMenuLink.addParameter("toolbar","remove");
          leftMenuLink.addParameter(Page.IW_FRAME_CLASS_PARAMETER,IBApplication.class);

        xpos++;
        toolbarTable.add(leftMenuLink,xpos,1);

        /*xpos++;
        toolbarTable.add(separator,xpos,1);

        Text pageName = new Text(BuilderLogic.getInstance().getCurrentIBXMLPage(iwc).getName());
          pageName.setBold();

        xpos++;
        toolbarTable.add(pageName,xpos,1);*/

        add(toolbarTable);
      }

      endStartup(iwc,IBToolBar.class);
    }

    /**
     *
     */
    public PresentationObject getPropertiesIcon(IWContext iwc) {
      IWBundle iwb = iwc.getApplication().getBundle(IB_BUNDLE_IDENTIFIER);
      Image image = iwb.getImage("shared/toolbar/page_properties.gif","shared/toolbar/page_properties1.gif","Page Properties",20,20);
        image.setHorizontalSpacing(2);
      Link link = new Link(image);
      link.setWindowToOpen(IBPropertiesWindow.class);
      link.addParameter(BuilderLogic.IB_PAGE_PARAMETER,BuilderLogic.getInstance().getCurrentIBPage(iwc));
      link.addParameter(BuilderLogic.IB_CONTROL_PARAMETER,BuilderLogic.ACTION_EDIT);
      //Hardcoded -1 for the top page
      String pageICObjectInstanceID = "-1";
      link.addParameter(BuilderLogic.IC_OBJECT_INSTANCE_ID_PARAMETER,pageICObjectInstanceID);
      return(link);
    }

    public PresentationObject getPermissionIcon(IWContext iwc){
      IWBundle iwb = iwc.getApplication().getBundle(IB_BUNDLE_IDENTIFIER);
      Image image = iwb.getImage("shared/toolbar/permissions.gif","shared/toolbar/permissions1.gif","Page Permissions",20,20);
        image.setHorizontalSpacing(2);
      Link link = new Link(image);
      link.setWindowToOpen(IBPermissionWindow.class);
      link.addParameter(IBPermissionWindow._PARAMETERSTRING_IDENTIFIER,BuilderLogic.getInstance().getCurrentIBPage(iwc));
      link.addParameter(IBPermissionWindow._PARAMETERSTRING_PERMISSION_CATEGORY,com.idega.core.accesscontrol.business.AccessControl._CATEGORY_PAGE_INSTANCE);

      return link;
    }
  }

  /**
   *
   */
  public static class IBStatusBar extends IWApplicationComponent {
    private final static String IW_BUNDLE_IDENTIFIER = "com.idega.builder";

    /**
     *
     */
    public IBStatusBar() {
    }

    /**
     *
     */
    public void main(IWContext iwc) {
      IWBundle iwb = iwc.getApplication().getBundle(IB_BUNDLE_IDENTIFIER);
      IWBundle _iwrb = getBundle(iwc);
      String controlParameter = "builder_controlparameter";
      setBackgroundColor(IWConstants.DEFAULT_INTERFACE_COLOR);
      setLightShadowColor(IWConstants.DEFAULT_LIGHT_INTERFACE_COLOR);
      setDarkShadowColor(IWConstants.DEFAULT_DARK_INTERFACE_COLOR);
      Image tilerCell = Table.getTransparentCell(iwc);
        tilerCell.setHeight("100%");

      //setBackgroundColor(com.idega.idegaweb.IWConstants.DEFAULT_LIGHT_INTERFACE_COLOR);
      //Image background = iwb.getImage("status_tiler.gif");
      //setBackgroundImage(background);
      setAllMargins(0);

      Table toolbarTable = new Table(2,1);
      toolbarTable.setWidth("100%");
      toolbarTable.setHeight("100%");
      toolbarTable.setCellpadding(0);
      toolbarTable.setCellspacing(0);
      toolbarTable.setWidth(1,1,"100%");
      toolbarTable.setAlignment(2,1,"right");
      toolbarTable.setVerticalAlignment(2,1,"top");
      toolbarTable.setVerticalAlignment(1,1,"middle");
      add(toolbarTable);

      String action = iwc.getParameter(controlParameter);
      if (action == null) {
        action = ACTION_BUILDER;
      }

      if (action.equals(ACTION_BUILDER)) {
/*        Text text1 = new Text("Status normal"+Text.NON_BREAKING_SPACE+Text.NON_BREAKING_SPACE);
        text1.setFontSize(1);
        text1.setFontColor("Black");*/

        Table toolTable = new Table(3,1);
        toolTable.setWidth("100%");
        toolTable.setCellpadding(0);
        toolTable.setCellspacing(0);

        Image editImage = _iwrb.getImage("shared/status/edit1.gif","Edit",64,17);
          editImage.setOnClickImage(_iwrb.getImage("shared/status/edit.gif"));
        Link editLink = new Link(editImage);
        editLink.setTarget(IBApplication.IB_CONTENT_FRAME);
        editLink.setURL(IBApplication.CONTENT_EDIT_URL);
        toolTable.add(editLink,1,1);

        getParentPage().setOnLoad("javascript: swapImage('"+editImage.getName()+"','','"+_iwrb.getImage("shared/status/edit.gif").getURL()+"',1)");

        Image previewImage = _iwrb.getImage("shared/status/preview1.gif","Preview",64,17);
          previewImage.setOnClickImage(_iwrb.getImage("shared/status/preview.gif"));
        Link previewLink = new Link(previewImage);
        previewLink.setTarget(IBApplication.IB_CONTENT_FRAME);
        previewLink.setURL(IBApplication.CONTENT_PREVIEW_URL);
        toolTable.add(previewLink,2,1);

        Image sourceImage = _iwrb.getImage("shared/status/source1.gif","Source",64,17);
          sourceImage.setOnClickImage(_iwrb.getImage("shared/status/source.gif"));
        Link sourceLink = new Link(sourceImage,IBSourceView.class);
        sourceLink.setTarget(IBApplication.IB_CONTENT_FRAME);
        toolTable.add(sourceLink,3,1);

        String id = (String)iwc.getSessionAttribute("ib_page_id");
        if (id == null) {
          int i_page_id = BuilderLogic.getInstance().getCurrentDomain(iwc).getStartPageID();
          id = Integer.toString(i_page_id);
        }
        String name = null;
        if (id != null && !id.equals("")) {
          java.util.Map tree = PageTreeNode.getTree(iwc);

          Integer pageId = new Integer(id);

          if (tree != null) {
            PageTreeNode node = (PageTreeNode)tree.get(pageId);
            if (node != null)
              name = Text.NON_BREAKING_SPACE + node.getNodeName();
          }

          if (name == null) {
            tree = PageTreeNode.getTree(iwc);
            if (tree != null) {
              PageTreeNode node = (PageTreeNode)tree.get(pageId);
              if (node != null)
                name = Text.NON_BREAKING_SPACE + node.getNodeName();
            }
          }

          if (name == null)
            name = "Page name";
        }
        else
          name = "Page name";

//        String name = Text.NON_BREAKING_SPACE + BuilderLogic.getInstance().getCurrentIBXMLPage(iwc).getName();
        Text pageName = new Text(name);
          pageName.setFontStyle("font-face: Geneva, Helvetica, sans-serif; font-weight: bold; font-size: 8pt;");
        toolbarTable.add(tilerCell,1,1);
        toolbarTable.add(pageName,1,1);
        toolbarTable.add(toolTable,2,1);
      }
      else if (action.equals(ACTION_TEMPLATES)) {
      }
      else if (action.equals(ACTION_SETTINGS)) {
      }
    }

    /**
     *
     */
    public String getBundleIdentifier() {
      return(IW_BUNDLE_IDENTIFIER);
    }
  }

}