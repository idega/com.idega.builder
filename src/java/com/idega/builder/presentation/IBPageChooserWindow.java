/*
 * $Id: IBPageChooserWindow.java,v 1.6 2001/11/14 17:56:53 eiki Exp $
 *
 * Copyright (C) 2001 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package com.idega.builder.presentation;

import com.idega.presentation.ui.AbstractChooserWindow;
import com.idega.presentation.IWContext;
import com.idega.presentation.ui.TreeViewer;
import com.idega.presentation.text.Link;
import com.idega.builder.data.IBDomain;
import com.idega.builder.business.PageTreeNode;

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
    setTitle("Page chooser");
    setWidth(300);
    setHeight(500);
  }

  /**
   *
   */
  public void displaySelection(IWContext iwc) {
    add("Select a page");

    try{
      /**
       * @todo get a treeviewer with the top page selected by default with better implementation ibdomain...
       */
      // TreeViewer viewer = TreeViewer.getTreeViewerInstance(new com.idega.projects.golf.entity.Union(3),iwc);
      //IBDomain domain = IBDomain.getDomain(1);
      //int i_page_id = domain.getStartPageID();
      int i_page_id = 1;

//      TreeViewer viewer = TreeViewer.getTreeViewerInstance(new com.idega.builder.data.IBPage(i_page_id),iwc);
      TreeViewer viewer = TreeViewer.getTreeViewerInstance(new PageTreeNode(i_page_id,iwc),iwc);

      add(viewer);
      viewer.setToMaintainParameter(SCRIPT_PREFIX_PARAMETER,iwc);
      viewer.setToMaintainParameter(SCRIPT_SUFFIX_PARAMETER,iwc);
      viewer.setToMaintainParameter(DISPLAYSTRING_PARAMETER_NAME,iwc);
      viewer.setToMaintainParameter(VALUE_PARAMETER_NAME,iwc);

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
/*  public PresentationObject getTable(IWContext iwc,IWBundle bundle){
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