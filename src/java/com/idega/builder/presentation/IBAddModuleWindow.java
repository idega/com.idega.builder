/*
 * $Id: IBAddModuleWindow.java,v 1.21 2002/03/28 15:59:39 tryggvil Exp $
 *
 * Copyright (C) 2001 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package com.idega.builder.presentation;

import com.idega.idegaweb.IWConstants;
import com.idega.core.localisation.business.ICLocaleBusiness;
import java.util.*;
import com.idega.builder.business.ModuleComparator;
import com.idega.builder.data.IBPage;
import com.idega.core.data.ICObjectInstance;
import com.idega.core.data.ICObject;
import com.idega.presentation.IWContext;
import com.idega.presentation.Table;
import com.idega.presentation.Image;
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

  public static final String ELEMENT_LIST = "element_list";
  public static final String BLOCK_LIST = "block_list";
  final static String STYLE_NAME = "add_module";
  //Image button;


  public IBAddModuleWindow(){
    setWidth(340);
    setHeight(400);
    setResizable(true);
    setScrollbar(true);
  }

  /**
   *
   */
  public void main(IWContext iwc) throws Exception {
    IWResourceBundle iwrb = iwc.getApplication().getBundle(BuilderLogic.IW_BUNDLE_IDENTIFIER).getResourceBundle(iwc);
    super.addTitle(iwrb.getLocalizedString("ib_addmodule_window","Add a new Module"),IWConstants.BUILDER_FONT_STYLE_TITLE);
    setStyles();
    //button = iwc.getApplication().getBundle(BuilderLogic.IW_BUNDLE_IDENTIFIER).getImage("shared/properties/button.gif");

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
    theReturn.setWidth(1,"50%");
    theReturn.setWidth(2,"50%");
    theReturn.setColor(1,1,"#ECECEC");
    theReturn.setColor(2,1,"#ECECEC");
    String listColor = com.idega.idegaweb.IWConstants.DEFAULT_LIGHT_INTERFACE_COLOR;

    ICObject staticICO = (ICObject)ICObject.getStaticInstance(ICObject.class);
    try {
      List elements = null;
      List blocks = null;

      try {
	elements = (List) iwc.getApplicationAttribute(ELEMENT_LIST+"_"+iwc.getCurrentLocaleId());
	blocks = (List) iwc.getApplicationAttribute(BLOCK_LIST+"_"+iwc.getCurrentLocaleId());
      }
      catch (Exception e) {
	elements = null;
	blocks = null;
      }

      if ( elements == null && blocks == null ) {
	elements = EntityFinder.findAllByColumn(staticICO,ICObject.getObjectTypeColumnName(),ICObject.COMPONENT_TYPE_ELEMENT);
	blocks = EntityFinder.findAllByColumn(staticICO,ICObject.getObjectTypeColumnName(),ICObject.COMPONENT_TYPE_BLOCK);

	if ( elements != null ) {
	  java.util.Collections.sort(elements,new ModuleComparator(iwc));
	}
	if ( blocks != null ) {
	  java.util.Collections.sort(blocks,new ModuleComparator(iwc));
	}
	iwc.setApplicationAttribute(ELEMENT_LIST+"_"+iwc.getCurrentLocaleId(),elements);
	iwc.setApplicationAttribute(BLOCK_LIST+"_"+iwc.getCurrentLocaleId(),blocks);
      }

      String sElements = iwrb.getLocalizedString("elements_header","Elements");
      String sBlocks = iwrb.getLocalizedString("blocks_header","Blocks");

      addSubComponentList(sElements,elements,theReturn,1,1,iwc);
      addSubComponentList(sBlocks,blocks,theReturn,1,2,iwc);

      theReturn.setColumnVerticalAlignment(1,"top");
      theReturn.setColumnVerticalAlignment(2,"top");
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
    table.add(subComponentTable,xpos,ypos);

    Text header = new Text(name,true,false,false);
    header.setFontSize(Text.FONT_SIZE_12_HTML_3);
    subComponentTable.add(header,1,ypos);
    subComponentTable.mergeCells(1,ypos,2,ypos);
    if (list != null) {
      Iterator iter = list.iterator();
      ypos++;
      while (iter.hasNext()) {
	ICObject item = (ICObject)iter.next();
        Image icon = this.getIconForObject(item,iwc);
        Link iconLink = new Link(icon);
	Link link = new Link(item.getBundle(iwc.getApplication()).getComponentName(item.getClassName(),iwc.getCurrentLocale()));
	link.setStyle(STYLE_NAME);
	link.addParameter(IB_CONTROL_PARAMETER,ACTION_ADD);
	link.addParameter(INTERNAL_CONTROL_PARAMETER," ");
	link.addParameter(IC_OBJECT_INSTANCE_ID_PARAMETER,item.getID());
	link.maintainParameter(IB_PAGE_PARAMETER,iwc);
	link.maintainParameter(IB_PARENT_PARAMETER,iwc);
	link.maintainParameter(IB_LABEL_PARAMETER,iwc);

	iconLink.addParameter(IB_CONTROL_PARAMETER,ACTION_ADD);
	iconLink.addParameter(INTERNAL_CONTROL_PARAMETER," ");
	iconLink.addParameter(IC_OBJECT_INSTANCE_ID_PARAMETER,item.getID());
	iconLink.maintainParameter(IB_PAGE_PARAMETER,iwc);
	iconLink.maintainParameter(IB_PARENT_PARAMETER,iwc);
	iconLink.maintainParameter(IB_LABEL_PARAMETER,iwc);

	subComponentTable.add(iconLink,1,ypos);
	subComponentTable.add(link,2,ypos);

	ypos++;
      }
    }
    //subComponentTable.setColumnAlignment(1,"center");
  }

  private void setStyles() {
    String _linkStyle = "font-family:Arial,Helvetica,sans-serif;font-size:8pt;color:#000000;text-decoration:none;";
    String _linkHoverStyle = "font-family:Arial,Helvetica,sans-serif;font-size:8pt;color:#FF8008;text-decoration:none;";
    if ( getParentPage() != null ) {
      getParentPage().setStyleDefinition("A."+STYLE_NAME,_linkStyle);
      //getParentPage().setStyleDefinition("A."+STYLE_NAME+":visited",_linkStyle);
      //getParentPage().setStyleDefinition("A."+STYLE_NAME+":active",_linkStyle);
      getParentPage().setStyleDefinition("A."+STYLE_NAME+":hover",_linkHoverStyle);
    }
  }
  /**
   *
   */
  public String getBundleIdentifier() {
    return(IW_BUNDLE_IDENTIFIER);
  }

  public static void removeAttributes(IWContext iwc) {
    Iterator iter = iwc.getApplication().getAvailableLocales().iterator();
    while (iter.hasNext()) {
      Locale locale = (Locale) iter.next();
      int localeID = ICLocaleBusiness.getLocaleId(locale);
      iwc.removeApplicationAttribute(ELEMENT_LIST+"_"+Integer.toString(localeID));
      iwc.removeApplicationAttribute(BLOCK_LIST+"_"+Integer.toString(localeID));
    }
  }

  private Image getIconForObject(ICObject obj,IWContext iwc){
   if(obj.getObjectType().equals(ICObject.COMPONENT_TYPE_ELEMENT)){
     /**
      *@todo: Make support for dynamic icons
      */
     return iwc.getApplication().getCoreBundle().getImage("elementicon16x16.gif");
   }
   else if(obj.getObjectType().equals(ICObject.COMPONENT_TYPE_BLOCK)){
     /**
       *@todo: Make support for dynamic icons
       */
      return iwc.getApplication().getCoreBundle().getImage("blockicon16x16.gif");
    }
    else{
      return iwc.getApplication().getCoreBundle().getImage("elementicon16x16.gif");
    }
  }
}