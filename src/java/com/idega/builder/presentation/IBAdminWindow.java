
//idega 2001 - Tryggvi Larusson
/*
*Copyright 2001 idega.is All Rights Reserved.
*/
package com.idega.builder.presentation;

//import java.util.*;
import com.idega.jmodule.*;
import com.idega.data.*;
import com.idega.jmodule.news.presentation.*;
import com.idega.util.*;
import com.idega.builder.data.*;
import com.idega.jmodule.object.*;
import com.idega.jmodule.object.textObject.*;
import com.idega.jmodule.object.interfaceobject.*;
import java.lang.reflect.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.sql.*;
import java.lang.reflect.*;
import java.beans.*;
import com.idega.builder.business.*;
/**
*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
*@version 1.0 alpha
*/

public class IBAdminWindow extends JModuleAdminWindow{


private static String controlParameter = "ib_window_action";
private static String idega_parameter_classname = "idega_parameter_classname";

  public void main(ModuleInfo modinfo)throws Exception{
    empty();
    String action = modinfo.getParameter(controlParameter);
    if(action.equals("window1")){
      addNewObject(modinfo);
    }
    else if(action.equals("window2")){
      changeObject(modinfo);
    }
    else if(action.equals("window3")){
      deleteObjectInstance(modinfo);
    }
    else if(action.equals("window4")){
      getPropertyListForObject(modinfo);
    }
    else if(action.equals("window5")){
      setPropertyForObject(modinfo);
    }
  }

  public void addNewObject(ModuleInfo modinfo)throws Exception{

        Window window = this;
        window.setParentToReload();

        IBPage ib_page ;

        IBObjectInstance instance = new IBObjectInstance();
        EntityInsert inserter = new EntityInsert(instance);
        //EntityInsert inserter = new EntityInsert(instance,modinfo.getRequest().getRequestURI()+"?ib_object_instance_insert=true&"+controlParameter+"="+modinfo.getParameter(controlParameter));
        add(inserter);
        inserter.main(modinfo);

        //if ( inserter.hasInserted(getModuleInfo()) || (getParameter("ib_object_instance_insert") != null) ){
        if(inserter.hasInserted(modinfo)){
        //if ( modinfo.getParameter("ib_object_instance_insert") != null ){
                instance = (IBObjectInstance) inserter.getInsertedObject(modinfo);
                ib_page= (IBPage)modinfo.getSessionAttribute("ib_page");
                //modinfo.removeSessionAttribute("ib_page");
                //out.print(ib_page.getName());
                //if (instance != null){
                        if(instance==null){
                          System.err.println("instance==null");
                        }
                        else{
                          System.err.println("instance!=null");
                          instance.addTo(ib_page);
                        }

                        //out.print(instance.getName());
                        window.close();
                //}
                //else{
                //      out.println("instance null");
                //}

                //out.print("er i if");
                //modinfo.getWriter().println("i 1<br>");
        }
        else{
                int id = Integer.parseInt(modinfo.getParameter("page_id"));
                ib_page= new IBPage(id);
                modinfo.setSessionAttribute("ib_page",ib_page);
                //out.print("er i else");
                //modinfo.getWriter().println("i 2<br>");
        }


  }


  public void changeObject(ModuleInfo modinfo)throws Exception{


        Window window = this;
        window.setParentToReload();

        IBPage ib_page ;

        IBObjectInstance instance = new IBObjectInstance();
        //add(new EntityInsert(instance));



        if ( modinfo.getParameter("ib_object_instance_id") != null){
                instance = new IBObjectInstance(Integer.parseInt(modinfo.getParameter("ib_object_instance_id")));
                add(new EntityUpdater(instance));
        }
        else{
                add("Aðgerð tókst");
                modinfo.removeSessionAttribute("idega_entity");
                window.close();
        }

  }

  public void deleteObjectInstance(ModuleInfo modinfo)throws Exception{

            Window window = this;
            window.setParentToReload();

            IBPage ib_page ;

            IBObjectInstance instance = new IBObjectInstance();
            //add(new EntityInsert(instance));

            if ( modinfo.getParameter("ib_object_instance_id") != null){
                    instance = new IBObjectInstance(Integer.parseInt(modinfo.getParameter("ib_object_instance_id")));
                    instance.removeFrom(new IBPage());
                    instance.delete();
                    window.close();
            }
            else{
                    add("Aðgerð tókst");
                    window.close();
            }
  }

  public void getPropertyListForObject(ModuleInfo modinfo)throws Exception{

        Window window = this;
        window.setParentToReload();

        IBPage ib_page;

        IBObjectInstance instance;// = new IBObjectInstance();
        //add(new EntityInsert(instance));
        String ib_object_instance_id = modinfo.getParameter("ib_object_instance_id");
        String idega_ib_method_name = modinfo.getParameter("idega_ib_method_name");


        if ( ib_object_instance_id  != null){

                int ib_object_instance_id_int=Integer.parseInt(ib_object_instance_id);
                Class theClass = getClass(ib_object_instance_id_int);


                BeanInfo info = Introspector.getBeanInfo(theClass,theClass.getSuperclass());
                PropertyDescriptor[] desc = info.getPropertyDescriptors();
                Table table=new Table();

                int counter=1;
                if(desc!=null){

                  add("Stillingarmöguleikar:");
                  addBreak();

                  for (int i = 0; i < desc.length; i++) {

                      Method method = desc[i].getWriteMethod();
                      //Class[] classes = method.getParameterTypes();


                      if(method!=null){
                        String name = method.getName();

                        Link link = new Link(name.substring(3));
                        link.addParameter("ib_object_instance_id",modinfo.getParameter("ib_object_instance_id"));
                        link.addParameter(controlParameter,"window5");
                        link.addParameter("idega_ib_method_name",name);

                        Class[] classes = method.getParameterTypes();
                        for (int n = 0; n < classes.length; n++) {
                          link.addParameter(idega_parameter_classname,classes[n].getName());
                        }


                        table.add(link,1,counter);
                        //TextInput input = new TextInput(name);;
                        //input.setLength(20);
                        //table.add(input,2,counter);
                        counter++;
                      }
                  }
                  add(table);
                }

                //instance.removeFrom(new IBPage());
                //instance.delete();
                //window.close();

        }
        else{
                add("Aðgerð tókst");
                window.close();
        }

  }


  public void setPropertyForObject(ModuleInfo modinfo)throws Exception{
    String methodName = modinfo.getParameter("idega_ib_method_name");
    int ib_object_instance_id=Integer.parseInt(modinfo.getParameter("ib_object_instance_id"));
    Class theClass = getClass(ib_object_instance_id);
    add("Stilling "+methodName+":");
    addBreak();
    Method method = theClass.getMethod(methodName,getClasses(modinfo,idega_parameter_classname));
    Class[] parameters = method.getParameterTypes();
    ModuleObject[] objects = IBPropertyHandler.getInterfaceComponent(parameters,null);
    Table table = new Table(2,objects.length+1);
    for (int i = 0; i < objects.length; i++) {
      table.add("Parameter"+(i+1),1,i+1);
      table.add(objects[i],2,i+1);
    }
    table.add(new SubmitButton("Uppfæra"),2,objects.length+1);
    add(table);
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
      IBObjectInstance instance = new IBObjectInstance(ib_object_instance_id);
      Table table = new Table();
      Class theClass = instance.getObject().getNewInstance().getClass();
      return theClass;
  }

}

