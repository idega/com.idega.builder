
//idega 2001 - Tryggvi Larusson
/*
*Copyright 2001 idega.is All Rights Reserved.
*/
package com.idega.builder.presentation;

//import java.util.*;
import java.lang.reflect.Method;
import java.beans.BeanInfo;
import java.beans.PropertyDescriptor;
import java.beans.Introspector;

import java.util.List;
import java.util.Iterator;

import com.idega.jmodule.*;
import com.idega.data.*;
import com.idega.jmodule.news.presentation.*;
import com.idega.util.*;
import com.idega.builder.data.*;
import com.idega.core.data.*;
import com.idega.jmodule.object.*;
import com.idega.jmodule.object.textObject.*;
import com.idega.jmodule.object.interfaceobject.*;
import com.idega.builder.business.*;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWResourceBundle;

import com.idega.data.EntityFinder;

/**
*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
*@version 1.0 alpha
*/

public class IBAddModuleWindow extends IBAdminWindow{


  private static final String ic_object_id_parameter = BuilderLogic.ic_object_id_parameter;
  private static final String ib_parent_parameter = BuilderLogic.ib_parent_parameter;
  private static final String ib_page_parameter = BuilderLogic.ib_page_parameter;

  private static final String ib_control_parameter = BuilderLogic.ib_control_parameter;
  //private static final String ACTION_DELETE = BuilderLogic.ACTION_DELETE;
  private static final String ACTION_EDIT = BuilderLogic.ACTION_EDIT;
  private static final String ACTION_ADD = BuilderLogic.ACTION_ADD;
  //private static final String ACTION_MOVE = BuilderLogic.ACTION_MOVE;

  private static final String IW_BUNDLE_IDENTIFIER=BuilderLogic.IW_BUNDLE_IDENTIFIER;

  private static final String internal_control_parameter ="ib_adminwindow_par";

  public void main(ModuleInfo modinfo)throws Exception{
    //empty();
      super.addTitle("IBAddModuleWindow");

    String action = modinfo.getParameter(ib_control_parameter);
    if(action.equals(ACTION_ADD)){
      addNewObject(modinfo);
    }
    else if(action.equals(ACTION_EDIT)){
      //edit(modinfo);
    }
  }

  public void addNewObject(ModuleInfo modinfo)throws Exception{

        //IWBundle bundle = this.getBundle(modinfo);
        //IWResourceBundle iwrb = bundle.getResourceBundle(modinfo);

        Window window = this;
        String insert = "object_has_inserted";
        Form form = getForm();
        add(form);
        Table table = new Table(1,2);
        table.setBorder(0);
        //form.add(table);
        //DropdownMenu menu = new DropdownMenu(ICObject.getStaticInstance(ICObject.class).findAll(),ic_object_id_parameter);
        //menu.setToSubmit();
        //table.add(menu,1,1);
        //table.add(new SubmitButton("Choose"),1,2);
        form.add(getComponentList(modinfo));
        String ib_parent_id = modinfo.getParameter(ib_parent_parameter);
        if(ib_parent_id==null){
          System.out.println("ib_parent_id==null");
        }
        else{
          form.add(new Parameter(ib_parent_parameter,ib_parent_id));
        }
        String ib_page_id = modinfo.getParameter(ib_page_parameter);
        if(ib_page_id==null){
          System.out.println("ib_page_id==null");
        }
        else{
          form.add(new Parameter(ib_page_parameter,ib_page_id));
        }
        String control = modinfo.getParameter(ib_control_parameter);
        if(control==null){
          System.out.println("control==null");
        }
        else{
          form.add(new Parameter(ib_control_parameter,control));
        }
        //form.maintainParameter(ib_parent_id);
        //form.maintainParameter(ib_page_id);
        //form.maintainParameter(ib_control_parameter);

        if(hasSubmitted(modinfo)){
          window.setParentToReload();
          String ic_object_id = modinfo.getParameter(ic_object_id_parameter);
          BuilderLogic.getInstance().addNewModule(ib_page_id,ib_parent_id,Integer.parseInt(ic_object_id));
          window.close();
        }

        }





        private Form getForm(){
          Form form = new Form();
          form.add(new Parameter(internal_control_parameter,"submit"));
          return form;
        }

        private boolean hasSubmitted(ModuleInfo modinfo){
          return modinfo.isParameterSet(internal_control_parameter);
        }


  public void changeObject(ModuleInfo modinfo)throws Exception{


        Window window = this;
        window.setParentToReload();

        IBPage ib_page ;

        ICObjectInstance instance = new ICObjectInstance();
        //add(new EntityInsert(instance));



        if ( modinfo.getParameter("ib_object_instance_id") != null){
                instance = new ICObjectInstance(Integer.parseInt(modinfo.getParameter("ib_object_instance_id")));
                add(new EntityUpdater(instance));
        }
        else{
                add("Aðgerð tókst");
                modinfo.removeSessionAttribute("idega_entity");
                window.close();
        }

  }


  private Table getComponentList(ModuleInfo modinfo){
    IWResourceBundle iwrb = getBundle(modinfo).getResourceBundle(modinfo);
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
    //theReturn.setColor(listColor);

    ICObject staticICO = (ICObject)ICObject.getStaticInstance(ICObject.class);
    try{
      List elements = EntityFinder.findAllByColumn(staticICO,staticICO.getObjectTypeColumnName(),ICObject.COMPONENT_TYPE_ELEMENT);
      List blocks = EntityFinder.findAllByColumn(staticICO,staticICO.getObjectTypeColumnName(),ICObject.COMPONENT_TYPE_BLOCK);
      List applications = EntityFinder.findAllByColumn(staticICO,staticICO.getObjectTypeColumnName(),ICObject.COMPONENT_TYPE_APPLICATION);

      String sElements = iwrb.getLocalizedString("elements_header","Elements");
      String sBlocks = iwrb.getLocalizedString("blocks_header","Blocks");
      String sApplications = iwrb.getLocalizedString("applicaitions_header","Applications");

      addSubComponentList(sElements,elements,theReturn,1,1,modinfo);
      addSubComponentList(sBlocks,blocks,theReturn,1,2,modinfo);
      addSubComponentList(sApplications,applications,theReturn,1,3,modinfo);

      theReturn.setColumnVerticalAlignment(1,"top");
      theReturn.setColumnVerticalAlignment(2,"top");
      theReturn.setColumnVerticalAlignment(3,"top");
    }
    catch(Exception e){
      e.printStackTrace();
    }
    return theReturn;

  }

  private void addSubComponentList(String name,List list,Table table,int ypos,int xpos,ModuleInfo modinfo){
      Table subComponentTable = new Table();
        subComponentTable.setWidth("100%");
      table.add(subComponentTable,xpos,ypos);

      Text header = new Text(name,true,false,false);
        header.setFontSize(Text.FONT_SIZE_12_HTML_3);
      subComponentTable.add(header,1,ypos);
      if(list!=null){
        Iterator iter = list.iterator();
        String space = " ";
        ypos++;
        while (iter.hasNext()) {
          ICObject item = (ICObject)iter.next();
          Link link = new Link(space+item.getName());
          link.addParameter(ib_control_parameter,ACTION_ADD);
          link.addParameter(internal_control_parameter," ");
          link.addParameter(ic_object_id_parameter,item.getID());
          //link.maintainParameter(internal_control_parameter,modinfo);
          link.maintainParameter(ib_page_parameter,modinfo);
          link.maintainParameter(ib_parent_parameter,modinfo);
          subComponentTable.add(link,1,ypos);
          ypos++;
        }
      }
      subComponentTable.setColumnAlignment(1,"center");
  }

  private Class[] getClasses(ModuleInfo modinfo,String parameterName){
      try{
        String[] classNames = modinfo.getParameterValues(parameterName);
        Class[] classes = new Class[classNames.length];
        for (int i = 0; i < classNames.length; i++) {
          if(classNames[i].equals("int")){
            classes[i]=Integer.TYPE;
          }
          else if(classNames[i].equals("boolean")){
            classes[i]=Boolean.TYPE;
          }
          else if(classNames[i].equals("float")){
            classes[i]=Float.TYPE;
          }
          else if(classNames[i].equals("double")){
            classes[i]=Double.TYPE;
          }
          else if(classNames[i].equals("char")){
            classes[i]=Character.TYPE;
          }
          else{
            classes[i]=Class.forName(classNames[i]);
          }
        }
        return classes;
      }
      catch(ClassNotFoundException ex){
        return null;
      }
  }

  private Class getClass(int ib_object_instance_id)throws Exception{
      ICObjectInstance instance = new ICObjectInstance(ib_object_instance_id);
      Table table = new Table();
      Class theClass = instance.getObject().getNewInstance().getClass();
      return theClass;
  }


  public String getBundleIdentifier(){
    return IW_BUNDLE_IDENTIFIER;
  }
}

