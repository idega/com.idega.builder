/*
 * $Id: IBApplication.java,v 1.29 2001/11/01 16:17:02 tryggvil Exp $
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
    if(l==null)
      return false;
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

  static void startStartup(IWContext iwc){
    List l = new Vector();
    iwc.setSessionAttribute("ib_startup_class_list",l);
    l.add(IBToolBar.class);
    l.add(PageTree.class);
    l.add(PageTree.class);
  }

  public void main(IWContext iwc) {
    startStartup(iwc);
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
        table.setCellspacing(3);
      add(table);
      Image image;
      image = iwc.getApplication().getBundle(IB_BUNDLE_IDENTIFIER).getImage("header.gif");
      table.add(image,1,1);
      Text buildText = new Text("build 220c");
        buildText.setFontColor("#FFFFFF");
        buildText.setFontSize(1);
      table.add(buildText,2,1);
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

      if(!startupInProgress){
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
      int i_page_id = 1;
      try {
        TreeViewer viewer = TreeViewer.getTreeViewerInstance(new com.idega.builder.data.IBPage(i_page_id),iwc);
//        TreeViewer viewer = TreeViewer.getTreeViewerInstance(new PageTreeNode(i_page_id,iwc,PageTreeNode.PAGE_TREE),iwc);
        viewer.setTarget(IB_LEFT_MENU_FRAME);
        viewer.setNodeActionParameter(com.idega.builder.business.BuilderLogic.IB_PAGE_PARAMETER);
        Link l = new Link();
        l.maintainParameter(Page.IW_FRAME_CLASS_PARAMETER,iwc);
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
      endStartup(iwc,PageTree.class);
    }
  }

  public static class TemplateTree extends Page {
    public TemplateTree() {
    }

    public void main(IWContext iwc){
      boolean startupInProgress = startupInProgress(iwc);

      if(!startupInProgress){
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

      int i_template_id = 2;
      try {
        TreeViewer viewer = TreeViewer.getTreeViewerInstance(new com.idega.builder.data.IBPage(i_template_id),iwc);
//        TreeViewer viewer = TreeViewer.getTreeViewerInstance(new PageTreeNode(i_template_id,iwc,PageTreeNode.TEMPLATE_TREE),iwc);
        viewer.setTarget(IB_LEFT_MENU_FRAME);
        viewer.setNodeActionParameter(com.idega.builder.business.BuilderLogic.IB_PAGE_PARAMETER);
        Link l = new Link();
        l.maintainParameter(Page.IW_FRAME_CLASS_PARAMETER,iwc);
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
      endStartup(iwc,TemplateTree.class);
    }
  }

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
      if(!startupInProgress){
        super.setOnLoad("parent.parent.frames['"+IB_LEFT_MENU_FRAME+"'].location.reload();parent.frames['"+IB_CONTENT_FRAME+"'].location.reload()");
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

        Image tool_new = iwb.getImage("toolbar_new.gif","New Page");
        tool_new.setHorizontalSpacing(2);
        Link link_new = new Link(tool_new);
        link_new.setWindowToOpen(IBCreatePageWindow.class);
        toolbarTable.add(link_new,xpos,1);

        Image tool_open = iwb.getImage("toolbar_open.gif","Open Page");
        tool_open.setHorizontalSpacing(2);
        Link link_open = new Link(tool_open);
        toolbarTable.add(link_open,xpos,1);

        Image tool_save = iwb.getImage("toolbar_save.gif","Save Page");
        tool_save.setHorizontalSpacing(2);
        Link link_save = new Link(tool_save);
        link_save.setWindowToOpen(IBSavePageWindow.class);
        toolbarTable.add(link_save,xpos,1);

        Image tool_save_as = iwb.getImage("toolbar_save.gif","Save As Page");
        tool_save_as.setHorizontalSpacing(2);
        Link link_save_as = new Link(tool_save_as);
        link_save_as.setWindowToOpen(IBSaveAsPageWindow.class);
        toolbarTable.add(link_save_as,xpos,1);

        Image tool_delete = iwb.getImage("toolbar_delete.gif","Delete Page");
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
        Image tool_1 = iwb.getImage("toolbar_back.gif","Go back");
        tool_1.setHorizontalSpacing(2);
        Link link_1 = new Link(tool_1);
        link_1.setURL("javascript:parent.frames['"+IB_CONTENT_FRAME+"'].history.go(-1)");
        toolbarTable.add(link_1,xpos,1);

        Image tool_2 = iwb.getImage("toolbar_forward.gif","Go forward");
        tool_2.setHorizontalSpacing(2);
        Link link_2 = new Link(tool_2);
        link_2.setURL("javascript:parent.frames['"+IB_CONTENT_FRAME+"'].history.go(1)");
        toolbarTable.add(link_2,xpos,1);

        Image tool_3 = iwb.getImage("toolbar_stop.gif","Stop loading");
        tool_3.setHorizontalSpacing(2);
        Link link_3 = new Link(tool_3);
        link_3.setURL("javascript:parent.frames['"+IB_CONTENT_FRAME+"'].stop()");
        toolbarTable.add(link_3,xpos,1);

        Image tool_4 = iwb.getImage("toolbar_reload.gif","Reload page");
        tool_4.setHorizontalSpacing(2);
        Link link_4 = new Link(tool_4);
        link_4.setURL("javascript:parent.frames['"+IB_CONTENT_FRAME+"'].location.reload()");
        toolbarTable.add(link_4,xpos,1);

        /*Image tool_5 = iwb.getImage("toolbar_home_1.gif","Go to startpage");
        Link link_5 = new Link(tool_5);
        link_5.setURL("javascript:parent.frames['"+IB_CONTENT_FRAME+"'].location.href='"+CONTENT_EDIT_URL+"'");
        toolbarTable.add(link_5,xpos,1);*/

        xpos++;
        toolbarTable.add(separator,xpos,1);

        Image leftMenuImage = iwb.getImage("toolbar_addtoolbar.gif","Show Curtain");
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
      Image image = iwb.getImage("toolbar_properties.gif","Page Properties");
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
      Image image = iwb.getImage("toolbar_permissions.gif","Page Permissions");
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
    private final static String IW_BUNDLE_IDENTIFIER = "com.idega.core";

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
      toolbarTable.setAlignment(2,1,"top");
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

        Link editLink = new Link(_iwrb.getImage("editorwindow/edit.gif"));
        editLink.setTarget(IBApplication.IB_CONTENT_FRAME);
        editLink.setURL(IBApplication.CONTENT_EDIT_URL);
        toolTable.add(editLink,1,1);

        Link previewLink = new Link(_iwrb.getImage("editorwindow/preview.gif"));
        previewLink.setTarget(IBApplication.IB_CONTENT_FRAME);
        previewLink.setURL(IBApplication.CONTENT_PREVIEW_URL);
        toolTable.add(previewLink,2,1);

        Link sourceLink = new Link(_iwrb.getImage("editorwindow/source.gif"));
        sourceLink.setWindowToOpen(IBSourceView.class);
        toolTable.add(sourceLink,3,1);


        String name = Text.NON_BREAKING_SPACE+"Page name"; //BuilderLogic.getInstance().getCurrentIBXMLPage(iwc).getName();
        toolbarTable.add(new Text(name,true,false,false),1,1);
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