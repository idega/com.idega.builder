/*
 * $Id: IBApplication.java,v 1.22 2001/10/17 08:26:04 tryggvil Exp $
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
import com.idega.idegaweb.IWBundle;
import com.idega.builder.presentation.IBPermissionWindow;

/**
 * @author <a href="tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */
public class IBApplication extends IWApplication {
  private final static String IB_FRAMESET1_FRAME = "ib_frameset1";
  private final static String IB_FRAMESET2_FRAME = "ib_frameset2";
  private final static String IB_CONTENT_FRAME = "ib_content";
  private final static String IB_LEFT_MENU_FRAME = "ib_left_menu";

  private final static String ACTION_BUILDER = "builder";
  private final static String ACTION_TEMPLATES = "templates";
  private final static String ACTION_SETTINGS = "settings";
  private final static String ACTION_HELP = "help";

  private static final String IB_BUNDLE_IDENTIFIER = "com.idega.builder";

  private final static String CONTENT_PREVIEW_URL = com.idega.idegaweb.IWMainApplication.BUILDER_SERVLET_URL + "?view=preview";
  private final static String CONTENT_EDIT_URL = com.idega.idegaweb.IWMainApplication.BUILDER_SERVLET_URL + "?view=builder";

  /**
   *
   */
  public IBApplication() {
    super("idegaWeb Builder");
    add(IBBanner.class);
    add(FrameSet1.class);
    setSpanPixels(1,30);
    setScrolling(1,false);
    setSpanAdaptive(2);
    setScrolling(2,false);
    setFrameName(2,IB_FRAMESET1_FRAME);
    super.setResizable(true);
    super.setWidth(900);
    super.setHeight(700);
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
      setScrolling(1,true);
      setFrameName(1,IB_LEFT_MENU_FRAME);
      setFrameName(2,IB_FRAMESET2_FRAME);
      setSpanPixels(1,180);
      setSpanAdaptive(2);
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
      super.setFrameName(2,IB_CONTENT_FRAME);
      add(IBStatusBar.class);
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
      setBackgroundColor("#0E2456");
      Table table = new Table(1,1);
      add(table);
      Image image;
      image = iwc.getApplication().getBundle(IB_BUNDLE_IDENTIFIER).getImage("header.gif");
      //table.add(new Image("/common/pics/arachnea/header.gif","idegaWeb Builder",90,28),1,1);
      table.setWidth("100%");
      table.setHeight("100%");
      table.setCellpadding(0);
      table.setCellpadding(0);

      setAllMargins(0);
    }
  }

  /**
   *
   */
  public static class IBMenu extends Page {
    /**
     *
     */
    public IBMenu() {
    }

    public void main(IWContext iwc){
      setBackgroundColor(com.idega.idegaweb.IWConstants.DEFAULT_LIGHT_INTERFACE_COLOR);
      //setBackgroundImage("/common/pics/arachnea/toolbar_tiler.gif");
      IWBundle iwb = iwc.getApplication().getBundle(IB_BUNDLE_IDENTIFIER);
      Image image = iwb.getImage("toolbar_tiler.gif");
      setBackgroundImage(image);
      setAllMargins(0);

      String builderControlParameter = "builder_controlparameter";

      Table controlTable = new Table(9,1);
      add(controlTable);
      controlTable.setHeight(24);

      controlTable.setCellpadding(0);
      controlTable.setCellspacing(0);
      controlTable.setAlignment("left");

      Image separator = iwb.getImage("toolbar_separator.gif");
      separator.setHorizontalSpacing(5);

      Link text1 = new Link("Builder");
      text1.setTarget("sidebar");
      text1.addParameter(builderControlParameter,ACTION_BUILDER);
      text1.setFontSize(1);
      text1.setFontColor("black");

      Link text2 = new Link("Templates");
      text2.setTarget("sidebar");

      text2.addParameter(builderControlParameter,ACTION_TEMPLATES);
      text2.setFontSize(1);
      text2.setFontColor("black");

      Link text3 = new Link("Settings");
      text3.setTarget("sidebar");
      text3.addParameter(builderControlParameter,ACTION_SETTINGS);
      text3.setFontSize(1);
      text3.setFontColor("black");

      Link text5 = new Link("Help");
      text5.setTarget("sidebar");

      text5.addParameter(builderControlParameter,ACTION_HELP);
      text5.setFontSize(1);
      text5.setFontColor("black");

      controlTable.add(separator,1,1);
      controlTable.add(text1,2,1);
      controlTable.add(separator,3,1);
      controlTable.add(text2,4,1);
      controlTable.add(separator,5,1);
      controlTable.add(text3,6,1);
      controlTable.add(separator,7,1);
      controlTable.add(separator,8,1);
      controlTable.add(text5,9,1);
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
      super.setOnLoad("parent.frames['"+IB_FRAMESET2_FRAME+"'].frames['"+IB_CONTENT_FRAME+"'].location.reload()");
      setAlignment("left");
      setVerticalAlignment("top");
      Text idegawebBuilder = new Text("idegaWeb Builder");
      idegawebBuilder.setFontColor("black");
      idegawebBuilder.setBold();
      idegawebBuilder.setFontSize(2);
      add(idegawebBuilder);
      addBreak();
      Text build = new Text(iwc.getApplication().getBuildNumber());
      build.setFontColor("blue");
      build.setFontSize(1);
      add(build);


      int i_page_id = 1;
      int i_template_id = 2;
      try {
        TreeViewer viewer = TreeViewer.getTreeViewerInstance(new com.idega.builder.data.IBPage(i_page_id),iwc);
        viewer.setTarget(IB_LEFT_MENU_FRAME);
        viewer.setNodeActionParameter(com.idega.builder.business.BuilderLogic.IB_PAGE_PARAMETER);
        Link l = new Link();
        l.maintainParameter(Page.IW_FRAME_CLASS_PARAMETER,iwc);
        viewer.setToMaintainParameter(Page.IW_FRAME_CLASS_PARAMETER,iwc);
        viewer.setTreeStyle("font-face: Verdana, Arial, sans-serif; font-size: 8pt; text-decoration: none;");

        viewer.setLinkProtototype(l);
        add(viewer);

        add(Text.getBreak());

        TreeViewer viewer2 = TreeViewer.getTreeViewerInstance(new com.idega.builder.data.IBPage(i_template_id),iwc);
        viewer2.setTarget(IB_LEFT_MENU_FRAME);
        viewer2.setNodeActionParameter(com.idega.builder.business.BuilderLogic.IB_PAGE_PARAMETER);
        Link l2 = new Link();
        l2.maintainParameter(Page.IW_FRAME_CLASS_PARAMETER,iwc);
        viewer2.setToMaintainParameter(Page.IW_FRAME_CLASS_PARAMETER,iwc);
        viewer2.setTreeStyle("font-face: Verdana, Arial, sans-serif; font-size: 8pt; text-decoration: none;");

        viewer2.setLinkProtototype(l2);
        add(viewer2);

        String page_id = iwc.getParameter(com.idega.builder.business.BuilderLogic.IB_PAGE_PARAMETER);
        if (page_id != null) {
          iwc.setSessionAttribute(com.idega.builder.business.BuilderLogic.SESSION_PAGE_KEY,page_id);
        }
      }
      catch(Exception e) {
        e.printStackTrace();
      }
    }
  }

  /**
   *
   */
  public static class IBToolBar extends Page {
    /**
     *
     */
    public IBToolBar() {
    }

    /**
     *
     */
    public void main(IWContext iwc) {
      super.setOnLoad("parent.parent.frames['"+IB_LEFT_MENU_FRAME+"'].location.reload();parent.frames['"+IB_CONTENT_FRAME+"'].location.reload()");
      IWBundle iwb = iwc.getApplication().getBundle(IB_BUNDLE_IDENTIFIER);
      String controlParameter = "builder_controlparameter";

      setBackgroundColor(com.idega.idegaweb.IWConstants.DEFAULT_LIGHT_INTERFACE_COLOR);
      Image background = iwb.getImage("toolbar_tiler.gif");
      setBackgroundImage(background);
      setAllMargins(0);

      String action = iwc.getParameter(controlParameter);
      if (action == null) {
        action=ACTION_BUILDER;
      }

      if (action.equals(ACTION_BUILDER)) {
        Image separator = iwb.getImage("toolbar_separator.gif");

        Image tool_new = iwb.getImage("toolbar_new_1.gif","New Page");
        Link link_new = new Link(tool_new);
        link_new.setWindowToOpen(IBCreatePageWindow.class);
        add(link_new);

        Image tool_open = iwb.getImage("toolbar_open_1.gif","Open Page");
        Link link_open = new Link(tool_open);
        add(link_open);

        Image tool_save = iwb.getImage("toolbar_save_1.gif","Save Page");
        Link link_save = new Link(tool_save);
        link_save.setWindowToOpen(IBSavePageWindow.class);
        add(link_save);

        Image tool_save_as = iwb.getImage("toolbar_save_1.gif","Save As Page");
        Link link_save_as = new Link(tool_save_as);
        link_save_as.setWindowToOpen(IBSaveAsPageWindow.class);
        add(link_save_as);

        Image tool_delete = iwb.getImage("toolbar_delete_1.gif","Delete Page");
        Link link_delete = new Link(tool_delete);
        link_delete.setWindowToOpen(IBDeletePageWindow.class);
        add(link_delete);

        PresentationObject propertiesIcon = getPropertiesIcon(iwc);
        add(propertiesIcon);

        PresentationObject permissionIcon = getPermissionIcon(iwc);
        add(permissionIcon);

        add(separator);

        Image tool_1 = iwb.getImage("toolbar_back_1.gif","Go back");
        Link link_1 = new Link(tool_1);
        link_1.setURL("javascript:parent.frames['"+IB_CONTENT_FRAME+"'].history.go(-1)");
        add(link_1);

        Image tool_2 = iwb.getImage("toolbar_forward_1.gif","Go forward");
        Link link_2 = new Link(tool_2);
        link_2.setURL("javascript:parent.frames['"+IB_CONTENT_FRAME+"'].history.go(1)");
        add(link_2);

        Image tool_3 = iwb.getImage("toolbar_stop_1.gif","Stop loading");
        Link link_3 = new Link(tool_3);
        link_3.setURL("javascript:parent.frames['"+IB_CONTENT_FRAME+"'].stop()");
        add(link_3);

        Image tool_4 = iwb.getImage("toolbar_reload_1.gif","Reload page");
        Link link_4 = new Link(tool_4);
        link_4.setURL("javascript:parent.frames['"+IB_CONTENT_FRAME+"'].location.reload()");
        add(link_4);

        Image tool_5 = iwb.getImage("toolbar_home_1.gif","Go to startpage");
        Link link_5 = new Link(tool_5);
        link_5.setURL("javascript:parent.frames['"+IB_CONTENT_FRAME+"'].location.href='"+CONTENT_EDIT_URL+"'");

        add(link_5);
      }
    }

    /**
     *
     */
    public PresentationObject getPropertiesIcon(IWContext iwc) {
      IWBundle iwb = iwc.getApplication().getBundle(IB_BUNDLE_IDENTIFIER);
      Image image = iwb.getImage("toolbar_properties_1.gif","Page Properties");
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
      Image image = iwb.getImage("toolbar_permissions_1.gif","Page Permissions");
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
  public static class IBStatusBar extends Page {
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


      setBackgroundColor(com.idega.idegaweb.IWConstants.DEFAULT_LIGHT_INTERFACE_COLOR);
      Image background = iwb.getImage("status_tiler.gif");
      setBackgroundImage(background);
      setAllMargins(0);

      Table toolbarTable = new Table(2,1);
      toolbarTable.setWidth("100%");
      toolbarTable.setHeight("100%");
      toolbarTable.setCellpadding(0);
      toolbarTable.setCellspacing(1);
      toolbarTable.setWidth(2,1,"100%");
      toolbarTable.setAlignment(2,1,"right");
      toolbarTable.setVerticalAlignment(1,1,"top");
      add(toolbarTable);

      String action = iwc.getParameter(controlParameter);
      if (action == null) {
        action = ACTION_BUILDER;
      }

      if (action.equals(ACTION_BUILDER)) {
        Text text1 = new Text("Status normal");
        text1.setFontSize(1);
        text1.setFontColor("Black");

        Table toolTable = new Table(10,1);
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
        //Link sourceLink = new Link("Source");
        sourceLink.setWindowToOpen(IBSourceView.class);
        toolTable.add(sourceLink,3,1);



        toolbarTable.add(toolTable,1,1);
        toolbarTable.add(text1,2,1);
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