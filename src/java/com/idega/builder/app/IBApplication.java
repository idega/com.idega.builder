package com.idega.builder.app;

import com.idega.builder.presentation.*;

import com.idega.jmodule.object.app.IWApplication;
import com.idega.jmodule.object.app.IWApplicationComponent;
import com.idega.jmodule.object.*;
import com.idega.jmodule.object.textObject.*;
import com.idega.jmodule.object.interfaceobject.*;


/**
 * Title:        idegaclasses
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href="tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */

public class IBApplication extends IWApplication {


  private final static String IB_CONTENT_FRAME = "ib_content";
  private final static String CONTENT_URL = com.idega.idegaweb.IWMainApplication.BUILDER_SERVLET_URL+"?view=builder";

  public IBApplication() {
    super("idegaWeb Builder");
    add(IBBanner.class);
    //add(IBMenu.class);
    add(FrameSet1.class);
    this.setSpanPixels(1,24);
    setScrolling(1,false);
    this.setSpanAdaptive(2);
    setScrolling(2,false);
    //this.setSpanPixels(2,24);
    //setScrolling(2,false);
    //this.setSpanAdaptive(3);
    //setScrolling(3,false);
    super.setResizable(true);
    super.setWidth(900);
    super.setHeight(700);
  }

  public static class FrameSet1 extends FrameSet{
      public FrameSet1(){
        add(IBLeftMenu.class);
        add(FrameSet2.class);
        setScrolling(1,false);
        setSpanPixels(1,180);
        setSpanAdaptive(2);
        setHorizontal();
      }
  }

  public static class FrameSet2 extends FrameSet{
      public FrameSet2(){
          add(IBToolBar.class);
          add(CONTENT_URL);
          super.setFrameName(2,IB_CONTENT_FRAME);
          //add(IBMainView.class);
          add(IBStatusBar.class);
          this.setSpanPixels(1,24);
          this.setScrolling(1,false);
          this.setSpanAdaptive(2);
          this.setSpanPixels(3,25);
          this.setScrolling(3,false);

      }
  }

  public static class IBBanner extends Page{
      public IBBanner(){

        setBackgroundColor("#0E2456");
        Table table = new Table(1,1);
        add(table);
        //table.add(new Image("/common/pics/arachnea/Builderheader1.gif"),1,1);
        Text text = new Text("idegaWeb Builder");
        text.setFontColor("white");
        table.add(text);
        table.setWidth("100%");
        table.setHeight("100%");
        //table.setHeight(1,"57");
        table.setCellpadding(0);
        table.setCellpadding(0);

        setAllMargins(0);
      }
  }

  public static class IBMenu extends Page{
      public IBMenu(){
        setBackgroundColor(com.idega.idegaweb.IWConstants.DEFAULT_LIGHT_INTERFACE_COLOR);
        setBackgroundImage("/common/pics/arachnea/toolbar_tiler.gif");
        setAllMargins(0);
      }

      public void main(ModuleInfo modinfo){

        String builderControlParameter = "builder_controlparameter";

        Table controlTable = new Table(9,1);
        add(controlTable);
        controlTable.setHeight(24);

        controlTable.setCellpadding(0);
        controlTable.setCellspacing(0);
        controlTable.setAlignment("left");

        Image separator = new Image("/common/pics/arachnea/toolbar_separator.gif");
        separator.setHorizontalSpacing(5);

        Link text1 = new Link("Builder");
        text1.setTarget("sidebar");
        text1.setURL("sidebar.jsp");
        text1.addParameter(builderControlParameter,"builder");
        text1.setFontSize(1);
        text1.setFontColor("black");

        Link text2 = new Link("Templates");
        text2.setTarget("sidebar");
        text2.setURL("sidebar.jsp");
        text2.addParameter(builderControlParameter,"templates");
        text2.setFontSize(1);
        text2.setFontColor("black");

        Link text3 = new Link("Settings");
        text3.setTarget("sidebar");
        text3.setURL("sidebar.jsp");
        text3.addParameter(builderControlParameter,"settings");
        text3.setFontSize(1);
        text3.setFontColor("black");

        Link text5 = new Link("Help");
        text5.setTarget("sidebar");
        text5.setURL("sidebar.jsp");
        text5.addParameter(builderControlParameter,"help");
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

        /*
        Table logoTable = new Table();
        logoTable.setCellpadding(0);
        logoTable.setCellspacing(0);
        Image idegaLogo = new Image("/common/pics/arachnea/idega_logo_small_1.gif");
        logoTable.add(idegaLogo);
        logoTable.setAlignment("right");
        */

      }
  }


  public static class IBLeftMenu extends IWApplicationComponent{
      public IBLeftMenu(){
        Text idegawebBuilder = new Text("idegaWeb Builder");
        idegawebBuilder.setFontColor("black");
        idegawebBuilder.setBold();
        idegawebBuilder.setFontSize(2);
        add(idegawebBuilder);
        addBreak();
        Text build = new Text("Build 782");
        build.setFontColor("blue");
        build.setFontSize(1);
        add(build);
      }
  }

  public static class IBToolBar extends Page{
      public IBToolBar(){
      }

      public void main(ModuleInfo modinfo){

        String controlParameter = "builder_controlparameter";

        setBackgroundColor(com.idega.idegaweb.IWConstants.DEFAULT_LIGHT_INTERFACE_COLOR);
        setBackgroundImage("/common/pics/arachnea/toolbar_tiler.gif");
        setAllMargins(0);

        String action = modinfo.getParameter(controlParameter);
        if(action==null){
          action="builder";
        }

        if(action.equals("builder")){

            Image separator = new Image("/common/pics/arachnea/toolbar_separator.gif");

            Image tool_new = new Image("/common/pics/arachnea/toolbar_new_1.gif","New Page");
            Link link_new = new Link(tool_new);
            link_new.setWindowToOpen(IBCreatePageWindow.class);
            add(link_new);

            Image tool_open = new Image("/common/pics/arachnea/toolbar_open_1.gif","Open Page");
            Link link_open = new Link(tool_open);
            add(link_open);

            Image tool_save = new Image("/common/pics/arachnea/toolbar_save_1.gif","Save Page");
            Link link_save = new Link(tool_save);
            add(link_save);

            Image tool_properties = new Image("/common/pics/arachnea/toolbar_properties_1.gif","Page Properties");
            Link link_properties = new Link(tool_properties);
            add(link_properties);

            add(separator);

            Image tool_1 = new Image("/common/pics/arachnea/toolbar_back_1.gif","Go back");
            Link link_1 = new Link(tool_1);
            //link_1.setURL("#");
            //link_1.setOnClick("top.frames['"+IB_CONTENT_FRAME+"'].history.go(-1)");
            link_1.setURL("javascript:parent.frames['"+IB_CONTENT_FRAME+"'].history.go(-1)");
            add(link_1);

            Image tool_2 = new Image("/common/pics/arachnea/toolbar_forward_1.gif","Go forward");
            Link link_2 = new Link(tool_2);
            //link_2.setURL("#");
            //link_2.setOnClick("top.frames['"+IB_CONTENT_FRAME+"'].history.go(1)");
            link_2.setURL("javascript:parent.frames['"+IB_CONTENT_FRAME+"'].history.go(1)");
            add(link_2);

            Image tool_3 = new Image("/common/pics/arachnea/toolbar_stop_1.gif","Stop loading");
            Link link_3 = new Link(tool_3);
            //link_3.setURL("#");
            //link_3.setOnClick("");
            link_3.setURL("javascript:parent.frames['"+IB_CONTENT_FRAME+"'].stop()");
            add(link_3);

            Image tool_4 = new Image("/common/pics/arachnea/toolbar_reload_1.gif","Reload page");
            Link link_4 = new Link(tool_4);
            //link_4.setURL("#");
            //link_4.setOnClick("top.frames['"+IB_CONTENT_FRAME+"'].location.reload()");
            link_4.setURL("javascript:parent.frames['"+IB_CONTENT_FRAME+"'].location.reload()");
            add(link_4);

            Image tool_5 = new Image("/common/pics/arachnea/toolbar_home_1.gif","Go to startpage");
            Link link_5 = new Link(tool_5);
            //link_5.setURL("#");
            //link_5.setOnClick("");
            link_4.setURL("javascript:parent.frames['"+IB_CONTENT_FRAME+"'].location.href='"+CONTENT_URL+"'");

            add(link_5);

            }

      }
  }

  /*public static class IBMainView extends Page{
      public IBMainView(){
        add("MainView");
      }
  }*/

  public static class IBStatusBar extends Page{
      public IBStatusBar(){
      }

      public void main(ModuleInfo modinfo){

        String controlParameter = "builder_controlparameter";


        setBackgroundColor(com.idega.idegaweb.IWConstants.DEFAULT_LIGHT_INTERFACE_COLOR);
        setBackgroundImage("/common/pics/arachnea/status_tiler.gif");
        setAllMargins(0);

        Table toolbarTable = new Table(2,1);
        add(toolbarTable);

        String action = modinfo.getParameter(controlParameter);
        if(action==null){
          action="builder";
        }

        if(action.equals("builder")){

        Text text1 = new Text("Status normal");
        text1.setFontSize(1);
        text1.setFontColor("Black");
        toolbarTable.add(text1);


        //Image tool_1 = new Image("/common/pics/arachnea/back_1.gif");
        //Link link_1 = new Link(tool_1);
        //add(link_1);

        }
        else if(action.equals("templates")){


        }
        else if(action.equals("settings")){



        }


      }
  }


}