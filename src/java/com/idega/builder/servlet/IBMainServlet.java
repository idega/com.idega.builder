/*
 * $Id: IBMainServlet.java,v 1.2 2001/04/30 16:40:40 palli Exp $
 *
 * Copyright (C) 2001 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package com.idega.builder.servlet;

import com.idega.jmodule.*;
import com.idega.data.*;
import com.idega.jmodule.news.presentation.*;
import com.idega.util.*;
import com.idega.builder.data.*;
import com.idega.builder.presentation.*;
import com.idega.jmodule.object.*;
import com.idega.jmodule.object.textObject.*;
import com.idega.jmodule.object.interfaceobject.*;
import java.lang.reflect.*;
import java.io.*;
import java.sql.*;
import com.idega.jmodule.login.business.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;

/**
*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
*@version 0.5 alpha
*/

public class IBMainServlet extends JSPModule {

	public void __theService(HttpServletRequest request, HttpServletResponse response) throws ServletException,IOException {
    try {
      main(getModuleInfo());
    }
    catch(SQLException ex) {
      ex.printStackTrace(System.err);
    }
    //_jspService(request,response);
	}

  public void main(ModuleInfo modinfo)throws IOException,SQLException {
    //boolean isAdmin = AccessControl.isAdmin(modinfo);
    boolean isAdmin = true;
    PrintWriter debugger = modinfo.getResponse().getWriter();
    int id;

    String page_id = modinfo.getParameter("idegaweb_page_id");
    if(page_id == null){
      id = 1;
    }
    else {
      id = Integer.parseInt(page_id);
    }

    String language = modinfo.getParameter("language");
    if(language == null){
      language = "IS";
    }

    IBObjectInstance[] instances;
    IBPage ib_page = new IBPage(id);
    IBAdminWindow window = new IBAdminWindow();

    try {
      instances = (IBObjectInstance[]) ib_page.findReverseRelated(new IBObjectInstance());
      for(int i = 0; i < instances.length; i++) {
        Table moduleTable = null;
        Table toolbarTable = null;
        if (isAdmin) {
          moduleTable = new Table(1,2);
          toolbarTable = new Table(3,1);
          toolbarTable.setHeight(10);
          moduleTable.add(toolbarTable,1,1);
          moduleTable.setColor("gray");
          moduleTable.setColor(1,2,"white");
          moduleTable.setHeight(1,"10");
          //moduleTable.setCellpadding(0);
          //moduleTable.setCellspacing(0);
        }
        try {
          ModuleObject obj = getSetObject(instances[i]);
          if (isAdmin) {
            moduleTable.add(obj,1,2);
            //Text text = new Text(obj.getClass().getName());
            //text.setFontSize(1);
            //text.setFontColor("white");
            //moduleTable.add(text,1,1);
          }
          else {
            add(obj);
          }
          //table.add(instances[i].getNewInstance());
        }
        catch(Exception ex) {
          debugger.print("Error");
        }

        if (isAdmin) {
          /*Form form1 = new Form(new Window("Breyta","window2.jsp"));
          form1.add(new SubmitButton(new Image("/pics/arachnea/change.gif")));
          form1.add(new Parameter("ib_object_instance_id",""+instances[i].getID()));*/

          AdminButton form2 = new AdminButton(new Image("/common/pics/arachnea/change.gif"),window);
          form2.addParameter("ib_window_action","window2");
          form2.addParameter(new Parameter("ib_object_instance_id",""+instances[i].getID()));

          toolbarTable.add(form2,1,1);

          /*Form form2 = new Form(new Window("Eyda","window3.jsp"));
          form2.add(new SubmitButton(new Image("/pics/arachnea/delete.gif")));
          form2.add(new Parameter("ib_object_instance_id",""+instances[i].getID()));*/
          AdminButton form3 = new AdminButton(new Image("/common/pics/arachnea/delete.gif"),window);
          form3.addParameter("ib_window_action","window3");
          form3.addParameter(new Parameter("ib_object_instance_id",""+instances[i].getID()));

          toolbarTable.add(form3,2,1);

          /*Form form3 = new Form(new Window("Stilla","window4.jsp"));
          form3.add(new SubmitButton(new Image("/pics/arachnea/edit.gif")));
          form3.add(new Parameter("ib_object_instance_id",""+instances[i].getID()));*/
          AdminButton form4 = new AdminButton(new Image("/common/pics/arachnea/edit.gif"),window);
          form4.addParameter("ib_window_action","window4");
          form4.addParameter(new Parameter("ib_object_instance_id",""+instances[i].getID()));

          toolbarTable.add(form4,3,1);
          super.add(moduleTable);
        }
      }
    }
    catch(Exception ex) {
      add("villa 1");
      System.err.println("ERROR!!!!!!");
      ex.printStackTrace(modinfo.getResponse().getWriter());
    }

    //AdminButton form = new AdminButton("Bæta við",window);
    if (isAdmin) {
      AdminButton form = new AdminButton(new Image("/common/pics/arachnea/add.gif"),window);
      form.addParameter("ib_window_action","window1");
      form.addParameter(new Parameter("page_id",Integer.toString(id)));

      /*Form form = new Form(new Window("Baeta","window1.jsp"));
      form.add(new SubmitButton("Bæta við"));
      form.add(new Parameter("page_id",""+id));*/

      add(form);
    }
    /*if(getSessionAttribute("idega_entity") == null){
            out.print("idega_entity == null");
    }
    else{
            out.print("idega entity != null");
    }*/
  }



  /**
   * Needs reimplementation
   */
  public ModuleObject getSetObject(IBObjectInstance instance) throws Exception {
    return null;
  }

  /*
  public ModuleObject getSetObject(IBObjectInstance instance) throws Exception {

    IBObjectProperty[] properties = instance.getProperties();
    ModuleObject object = null;

    if (properties.length > 0) {
      IBObjectPropertyValue[] values;
      Class[] classType = new Class[1];
      String[] StringArr = new String[1];
      classType[0] = Class.forName("java.lang.String");
      for(int i = 0; i < properties.length; i++) {
        values = properties[i].getPropertyValues();
        if(properties[i].getPropertyType().equals("constructor")){
          //Vector vector;
          Object[] objectArr = new Object[values.length];
          for(int n = 0; n < values.length; n++){
            StringArr[0] = values[n].getStringValue();
            objectArr[n] = values[n].getClassObject().getConstructor(classType).newInstance(StringArr);
          }
          Class myClass = Class.forName(instance.getObject().getClassName());
        }
        else if (properties[i].getPropertyType().equals("function")) {
          if (object == null){
            object = instance.getNewInstance();
          }
        }
        else {
          if (object == null) {
            object=instance.getNewInstance();
          }
        }
      }
    }
    else {
      object=instance.getNewInstance();
    }
    return object;
  }*/
}
