/*
 * $Id: IBPageChooserWindow.java,v 1.2 2001/09/25 13:33:16 palli Exp $
 *
 * Copyright (C) 2001 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package com.idega.builder.presentation;

import com.idega.jmodule.object.interfaceobject.AbstractChooserWindow;
import com.idega.jmodule.object.ModuleInfo;
import com.idega.jmodule.object.interfaceobject.TreeViewer;
import com.idega.jmodule.object.textObject.Link;
import com.idega.builder.data.IBDomain;


/**
 * @author <a href="tryggvi@idega.is">Tryggvi Larusson</a>
 * @modified by <a href=teiki@idega.is">Eirikur Hrafnsson</a>
 * @version 1.0
 */
public class IBPageChooserWindow extends AbstractChooserWindow {
  /**
   *
   */
  public IBPageChooserWindow() {
    setName("Page Chooser");
    setWidth(300);
    setHeight(500);
    add("Select a page");
  }

  /**
   *
   */
  public void displaySelection(ModuleInfo modinfo) {
    try{
      /**
       * @todo get a treeviewer with the top page selected by default with better implementation ibdomain...
       */
      // TreeViewer viewer = TreeViewer.getTreeViewerInstance(new com.idega.projects.golf.entity.Union(3),modinfo);
      //IBDomain domain = IBDomain.getDomain(1);
      //int i_page_id = domain.getStartPageID();
      int i_page_id = 1;

      TreeViewer viewer = TreeViewer.getTreeViewerInstance(new com.idega.builder.data.IBPage(i_page_id),modinfo);

      add(viewer);
      viewer.setToMaintainParameter(SCRIPT_PREFIX_PARAMETER,modinfo);
      viewer.setToMaintainParameter(SCRIPT_SUFFIX_PARAMETER,modinfo);
      viewer.setToMaintainParameter(DISPLAYSTRING_PARAMETER_NAME,modinfo);
      viewer.setToMaintainParameter(VALUE_PARAMETER_NAME,modinfo);

      Link prototype = new Link();
      viewer.setToUseOnClick();
      //sets the hidden input and textinput of the choosing page
      viewer.setOnClick(SELECT_FUNCTION_NAME+"("+viewer.ONCLICK_DEFAULT_NODE_NAME_PARAMETER_NAME+","+viewer.ONCLICK_DEFAULT_NODE_ID_PARAMETER_NAME+")");
    }
    catch(Exception e){
      e.printStackTrace();
    }
  }

  /**
   *
   */
/*  public ModuleObject getTable(ModuleInfo modinfo,IWBundle bundle){
    Table table = new Table(2,1);
    TextInput input = new TextInput(displayInputName);
    Parameter value = new Parameter(getChooserParameter(),"");
    table.add(value);
    table.add(new Parameter(VALUE_PARAMETER_NAME,value.getName()));
    if(addForm){
      SubmitButton button = new SubmitButton("Choose");
      table.add(button,2,1);
      form.addParameter(CHOOSER_SELECTION_PARAMETER,getChooserParameter());
      form.addParameter(SCRIPT_PREFIX_PARAMETER,"window.opener.document."+form.getID()+".");
      form.addParameter(SCRIPT_SUFFIX_PARAMETER,"value");
    }
    else{
      Link link;
      if( buttonImage == null ) link = new Link("Choose");
      else link = new Link(buttonImage);

      link.setWindowToOpen(getChooserWindowClass());
      link.addParameter(CHOOSER_SELECTION_PARAMETER,getChooserParameter());
      //debug skiiiiiiiiiiiiiiiiiiiitamix getParentForm ekki að virka??
      link.addParameter(SCRIPT_PREFIX_PARAMETER,"window.opener.document."+getParentObject().getParentObject().getID()+".");
      link.addParameter(SCRIPT_SUFFIX_PARAMETER,"value");
      link.addParameter(DISPLAYSTRING_PARAMETER_NAME,input.getName());
      link.addParameter(VALUE_PARAMETER_NAME,value.getName());
      table.add(link,2,1);
    }

    table.add(input,1,1);
    table.add(new Parameter(DISPLAYSTRING_PARAMETER_NAME,input.getName()));
    return table;
  }*/
}