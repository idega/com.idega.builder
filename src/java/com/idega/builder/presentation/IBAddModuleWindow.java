/*
 * $Id: IBAddModuleWindow.java,v 1.13 2002/01/09 15:33:11 tryggvil Exp $
 *
 * Copyright (C) 2001 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package com.idega.builder.presentation;

import java.util.List;
import java.util.Iterator;
import com.idega.builder.business.ModuleComparator;
import com.idega.builder.data.IBPage;
import com.idega.core.data.ICObjectInstance;
import com.idega.core.data.ICObject;
import com.idega.presentation.IWContext;
import com.idega.presentation.Table;
import com.idega.presentation.text.Text;
import com.idega.presentation.text.Link;
import com.idega.presentation.ui.Form;
import com.idega.presentation.ui.Window;
import com.idega.presentation.ui.Parameter;
import com.idega.presentation.ui.EntityUpdater;
import com.idega.builder.business.BuilderLogic;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.data.EntityFinder;

/**
 * @author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */

public class IBAddModuleWindow extends IBAdminWindow {
  private static final String IC_OBJECT_INSTANCE_ID_PARAMETER = BuilderLogic.IC_OBJECT_INSTANCE_ID_PARAMETER;
  private static final String IB_PARENT_PARAMETER = BuilderLogic.IB_PARENT_PARAMETER;
  private static final String IB_PAGE_PARAMETER = BuilderLogic.IB_PAGE_PARAMETER;
  private static final String IB_LABEL_PARAMETER = BuilderLogic.IB_LABEL_PARAMETER;
  private static final String IB_CONTROL_PARAMETER = BuilderLogic.IB_CONTROL_PARAMETER;
  private static final String ACTION_EDIT = BuilderLogic.ACTION_EDIT;
  private static final String ACTION_ADD = BuilderLogic.ACTION_ADD;
  private static final String IW_BUNDLE_IDENTIFIER = BuilderLogic.IW_BUNDLE_IDENTIFIER;
  private static final String INTERNAL_CONTROL_PARAMETER = "ib_adminwindow_par";

  /**
   *
   */
  public void main(IWContext iwc) throws Exception {
    IWResourceBundle iwrb = getBundle(iwc).getResourceBundle(iwc);
    super.addTitle(iwrb.getLocalizedString("ib_addmodule_window","Add a new Module"));

    String action = iwc.getParameter(IB_CONTROL_PARAMETER);
    if (action.equals(ACTION_ADD)) {
      addNewObject(iwc);
    }
  }

  /**
   *
   */
  public void addNewObject(IWContext iwc) throws Exception {
    Window window = this;
    String insert = "object_has_inserted";
    Form form = getForm();
    add(form);
    Table table = new Table(1,2);
    table.setBorder(0);
    form.add(getComponentList(iwc));

    String ib_parent_id = iwc.getParameter(IB_PARENT_PARAMETER);
    if (ib_parent_id == null) {
      System.out.println("ib_parent_id==null");
    }
    else {
      form.add(new Parameter(IB_PARENT_PARAMETER,ib_parent_id));
    }

    String ib_page_id = iwc.getParameter(IB_PAGE_PARAMETER);
    if (ib_page_id == null) {
      System.out.println("ib_page_id==null");
    }
    else {
      form.add(new Parameter(IB_PAGE_PARAMETER,ib_page_id));
    }

    String control = iwc.getParameter(IB_CONTROL_PARAMETER);
    if (control == null) {
      System.out.println("control==null");
    }
    else {
      form.add(new Parameter(IB_CONTROL_PARAMETER,control));
    }

    String label = iwc.getParameter(IB_LABEL_PARAMETER);
    if (label != null) {
      form.add(new Parameter(IB_LABEL_PARAMETER,label));
    }

    if (hasSubmitted(iwc)) {
      window.setParentToReload();
      String ic_object_id = iwc.getParameter(IC_OBJECT_INSTANCE_ID_PARAMETER);
      BuilderLogic.getInstance().addNewModule(ib_page_id,ib_parent_id,Integer.parseInt(ic_object_id),label);
      window.close();
    }
  }

  /**
   *
   */
  private Form getForm() {
    Form form = new Form();
    form.add(new Parameter(INTERNAL_CONTROL_PARAMETER,"submit"));
    return(form);
  }

  /**
   *
   */
  private boolean hasSubmitted(IWContext iwc) {
    return(iwc.isParameterSet(INTERNAL_CONTROL_PARAMETER));
  }

  /**
   *
   */
  private Table getComponentList(IWContext iwc) {
    IWResourceBundle iwrb = getBundle(iwc).getResourceBundle(iwc);
    Table theReturn = new Table();
    theReturn.setWidth("100%");
    theReturn.setHeight("100%");
    theReturn.setCellpadding(0);
    theReturn.setCellspacing(1);
    theReturn.setWidth(1,"33%");
    theReturn.setWidth(2,"33%");
    theReturn.setWidth(3,"33%");
    theReturn.setColor(1,1,"#ECECEC");
    theReturn.setColor(2,1,"#ECECEC");
    theReturn.setColor(3,1,"#ECECEC");
    theReturn.setCellspacing(1);
    String listColor = com.idega.idegaweb.IWConstants.DEFAULT_LIGHT_INTERFACE_COLOR;

    ICObject staticICO = (ICObject)ICObject.getStaticInstance(ICObject.class);
    try {
      List elements = EntityFinder.findAllByColumn(staticICO,staticICO.getObjectTypeColumnName(),ICObject.COMPONENT_TYPE_ELEMENT);
      List blocks = EntityFinder.findAllByColumn(staticICO,staticICO.getObjectTypeColumnName(),ICObject.COMPONENT_TYPE_BLOCK);
      //List applications = EntityFinder.findAllByColumn(staticICO,staticICO.getObjectTypeColumnName(),ICObject.COMPONENT_TYPE_APPLICATION);

      if ( elements != null ) {
        java.util.Collections.sort(elements,new ModuleComparator());
      }
      if ( blocks != null ) {
        java.util.Collections.sort(blocks,new ModuleComparator());
      }
      //if ( applications != null ) {
      //  java.util.Collections.sort(applications,new ModuleComparator());
      //}

      String sElements = iwrb.getLocalizedString("elements_header","Elements");
      String sBlocks = iwrb.getLocalizedString("blocks_header","Blocks");
      //String sApplications = iwrb.getLocalizedString("applicaitions_header","Applications");

      addSubComponentList(sElements,elements,theReturn,1,1,iwc);
      addSubComponentList(sBlocks,blocks,theReturn,1,2,iwc);
      //addSubComponentList(sApplications,applications,theReturn,1,3,iwc);

      theReturn.setColumnVerticalAlignment(1,"top");
      theReturn.setColumnVerticalAlignment(2,"top");
      //theReturn.setColumnVerticalAlignment(3,"top");
    }
    catch(Exception e) {
      e.printStackTrace();
    }
    return(theReturn);
  }

  /**
   *
   */
  private void addSubComponentList(String name, List list, Table table, int ypos, int xpos, IWContext iwc) {
    Table subComponentTable = new Table();
    subComponentTable.setWidth("100%");
    table.add(subComponentTable,xpos,ypos);

    Text header = new Text(name,true,false,false);
    header.setFontSize(Text.FONT_SIZE_12_HTML_3);
    subComponentTable.add(header,1,ypos);
    if (list != null) {
      Iterator iter = list.iterator();
      String space = " ";
      ypos++;
      while (iter.hasNext()) {
        ICObject item = (ICObject)iter.next();
        Link link = new Link(space+item.getName());
        link.addParameter(IB_CONTROL_PARAMETER,ACTION_ADD);
        link.addParameter(INTERNAL_CONTROL_PARAMETER," ");
        link.addParameter(IC_OBJECT_INSTANCE_ID_PARAMETER,item.getID());
        link.maintainParameter(IB_PAGE_PARAMETER,iwc);
        link.maintainParameter(IB_PARENT_PARAMETER,iwc);
        link.maintainParameter(IB_LABEL_PARAMETER,iwc);
        subComponentTable.add(link,1,ypos);
        ypos++;
      }
    }
    subComponentTable.setColumnAlignment(1,"center");
  }

  /**
   *
   */
  public String getBundleIdentifier() {
    return(IW_BUNDLE_IDENTIFIER);
  }
}

