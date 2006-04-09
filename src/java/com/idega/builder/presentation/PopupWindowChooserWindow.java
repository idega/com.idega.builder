package com.idega.builder.presentation;

import java.net.URLEncoder;

import com.idega.builder.business.BuilderLogic;
import com.idega.presentation.IWContext;
import com.idega.presentation.Table;
import com.idega.presentation.ui.AbstractChooserWindow;
import com.idega.presentation.ui.BooleanInput;
import com.idega.presentation.ui.Form;
import com.idega.presentation.ui.SubmitButton;
import com.idega.presentation.ui.TextInput;
import com.idega.presentation.ui.Window;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author
 * @version 1.0
 */

public class PopupWindowChooserWindow extends AbstractChooserWindow {

private String _windowString;
public static final String replaceMent = "æ";
public static final String replaceMentCommon = "+";

  public PopupWindowChooserWindow() {
    this.setHeight(500);
    this.setStatus(true);
  }

  public void displaySelection(IWContext iwc) {

    if ( iwc.getParameter("mode") != null && iwc.getParameter("mode").equals("submit")) {
      try {
        collectStyles(iwc);
        debug("collected");
        debug("String = "+this._windowString);
//        _windowString = TextSoap.findAndReplace(_windowString, "'",replaceMent);
        /*_windowString = TextSoap.findAndReplace(_windowString, ",",replaceMentCommon);
        _windowString = TextSoap.findAndReplace(_windowString, "iwOpenWindow(", "");
        _windowString = TextSoap.findAndReplace(_windowString, ")", "");*/
        this._windowString = URLEncoder.encode(this._windowString);
        getParentPage().setOnLoad(SELECT_FUNCTION_NAME+"('"+this._windowString+"','"+this._windowString+"')");

      }catch (Exception e) {
        drawForm();
      }
    }
    else {
      drawForm();
    }
  }

  private void drawForm() {
    Form form = new Form();
    Table formTable = new Table();

    form.maintainParameter(FORM_ID_PARAMETER);
    form.maintainParameter(SCRIPT_SUFFIX_PARAMETER);
    form.maintainParameter(DISPLAYSTRING_PARAMETER_NAME);
    form.maintainParameter(VALUE_PARAMETER_NAME);


    int row = 1;

/*    IBPageChooser pageHandler = new IBPageChooser("page_ranus");
    formTable.add("Page:",1,row);
    formTable.add(pageHandler,2,row);
    ++row;
*/    TextInput pageHandler = new TextInput("page");
    formTable.add("PageID:",1,row);
    formTable.add(pageHandler,2,row);
    ++row;

    TextInput name = new TextInput("name");
    formTable.add("Name:",1,row);
    formTable.add(name,2,row);
    ++row;

    BooleanInput location = new BooleanInput("location");
    formTable.add("Location:", 1 ,row);
    formTable.add(location, 2 ,row);
    ++row;

    BooleanInput toolbar = new BooleanInput("toolbar");
    formTable.add("Toolbar:", 1 ,row);
    formTable.add(toolbar, 2 ,row);
    ++row;

    BooleanInput directories = new BooleanInput("directories");
    formTable.add("Directories:", 1 ,row);
    formTable.add(directories, 2 ,row);
    ++row;

    BooleanInput statusbar = new BooleanInput("statusbar");
    formTable.add("Statusbar:", 1 ,row);
    formTable.add(statusbar, 2 ,row);
    ++row;

    BooleanInput menubar = new BooleanInput("menubar");
    formTable.add("Menubar:", 1 ,row);
    formTable.add(menubar, 2 ,row);
    ++row;

    BooleanInput titlebar = new BooleanInput("titlebar");
    formTable.add("Titlebar:", 1 ,row);
    formTable.add(titlebar, 2 ,row);
    ++row;

    BooleanInput scrollbars = new BooleanInput("scrollbars");
    formTable.add("Scrollbars:", 1 ,row);
    formTable.add(scrollbars, 2 ,row);
    ++row;

    BooleanInput resizable = new BooleanInput("resizable");
    formTable.add("Resizable:", 1 ,row);
    formTable.add(resizable, 2 ,row);
    ++row;

    BooleanInput fullscreen = new BooleanInput("fullscreen");
    formTable.add("Fullscreen:", 1 ,row);
    formTable.add(fullscreen, 2 ,row);
    ++row;

    TextInput heigth = new TextInput("heigth");
    formTable.add("Heigth:",1,row);
    formTable.add(heigth,2,row);
    ++row;

    TextInput width = new TextInput("width");
    formTable.add("Width:",1,row);
    formTable.add(width,2,row);
    ++row;

    SubmitButton submit = new SubmitButton("Submit", "mode","submit");
    formTable.setAlignment(2,row,Table.HORIZONTAL_ALIGN_RIGHT);
    formTable.add(submit, 2, row);

    form.add(formTable);
    add(form);
  }

  private void collectStyles(IWContext iwc) {
    String page = iwc.getParameter("page");
    String name = iwc.getParameter("name");
    String toolbar = iwc.getParameter("toolbar");
    String location = iwc.getParameter("location");
    String directories = iwc.getParameter("directories");
    String statusbar = iwc.getParameter("statusbar");
    String menubar = iwc.getParameter("menubar");
    String titlebar = iwc.getParameter("titlebar");
    String scrollbars = iwc.getParameter("scrollbars");
    String resizable = iwc.getParameter("resizable");
    String fullscreen = iwc.getParameter("fullscreen");
    String heigth = iwc.getParameter("heigth");
    String width = iwc.getParameter("width");

    boolean bLocation = false;
    boolean bDirectories = false;
    boolean bToolbar = false;
    boolean bStatusbar = false;
    boolean bMenubar = false;
    boolean bTitlebar = false;
    boolean bScrollbars = false;
    boolean bResizable = false;
    boolean bFullscreen = false;
    int iHeight = 500;
    int iWidth = 500;
    int iPage = 1;

    if (toolbar.equals("Y")) {
			bToolbar = true;
		}
    if (location.equals("Y")) {
			bLocation = true;
		}
    if (directories.equals("Y")) {
			bDirectories = true;
		}
    if (statusbar.equals("Y")) {
			bStatusbar = true;
		}
    if (menubar.equals("Y")) {
			bMenubar = true;
		}
    if (titlebar.equals("Y")) {
			bTitlebar = true;
		}
    if (scrollbars.equals("Y")) {
			bScrollbars = true;
		}
    if (resizable.equals("Y")) {
			bResizable = true;
		}
    if (fullscreen.equals("Y")) {
			bFullscreen = true;
		}

    try {
      iHeight = Integer.parseInt(heigth);
    }catch (NumberFormatException n) {}
    try {
      iWidth = Integer.parseInt(width);
    }catch (NumberFormatException n) {}
    try {
      iPage = Integer.parseInt(page);
    }catch (NumberFormatException n){}


    this._windowString = Window.getWindowCallingScript( BuilderLogic.getInstance().getIBPageURL(iwc,iPage), name, bToolbar, bLocation, bDirectories, bStatusbar, bMenubar, bTitlebar, bScrollbars, bResizable, bFullscreen, iWidth, iHeight);
  }


}
