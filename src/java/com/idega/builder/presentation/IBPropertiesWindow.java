package com.idega.builder.presentation;

import com.idega.builder.business.BuilderLogic;
import com.idega.builder.business.IBPropertyHandler;
import com.idega.jmodule.object.ModuleInfo;
import com.idega.jmodule.object.FrameSet;

import com.idega.idegaweb.IWProperty;
import com.idega.idegaweb.IWPropertyList;
import com.idega.idegaweb.IWPropertyListIterator;
import com.idega.idegaweb.IWMainApplication;
import com.idega.idegaweb.IWURL;

import com.idega.jmodule.object.*;
import com.idega.jmodule.object.textObject.*;
import com.idega.jmodule.object.interfaceobject.*;

import com.idega.util.reflect.MethodFinder;

import com.idega.core.data.ICObject;

import java.util.List;
import java.util.Iterator;

/**
 * Title:        idegaclasses
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href="tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */

public class IBPropertiesWindow extends FrameSet{

   static final String IC_OBJECT_ID_PARAMETER = BuilderLogic.IC_OBJECT_ID_PARAMETER;
   static final String IB_PAGE_PARAMETER = BuilderLogic.IB_PAGE_PARAMETER;
   final static String METHOD_ID_PARAMETER="iw_method_identifier";
   final static String VALUE_SAVE_PARAMETER = "ib_method_save";
   final static String VALUE_PARAMETER = "ib_method_value";

   final static String TOP_FRAME = "ib_prop_win_top";
   final static String MIDDLE_FRAME = "ib_prop_win_middle";
   final static String BOTTOM_FRAME = "ib_prop_win_bottom";


  public void main(ModuleInfo modinfo) throws Exception{
    super.setTitle("Properties");
    super.setWidth(600);
    super.setHeight(600);
    add(IBPropertiesWindowTop.class);

    IWURL mURL = FrameSet.getFrameURL(IBPropertiesWindowMiddle.class);
    mURL.maintainParameter(IC_OBJECT_ID_PARAMETER,modinfo);
    add(mURL.toString());
    //add(IBPropertiesWindowMiddle.class);

    add(IBPropertiesWindowBottom.class);
    this.setSpanPixels(1,40);
    this.setSpanAdaptive(2);
    this.setSpanPixels(3,50);

    this.setFrameName(1,TOP_FRAME);
    this.setFrameName(2,MIDDLE_FRAME);
    this.setFrameName(3,BOTTOM_FRAME);
  }


  public void main2(ModuleInfo modinfo)throws Exception{
      //super.addTitle("IBPropertiesWindow");
      //setParentToReload();
      String ib_page_id = modinfo.getParameter(IB_PAGE_PARAMETER);
      String ic_objectinstance_id = modinfo.getParameter(IC_OBJECT_ID_PARAMETER);
      if(ic_objectinstance_id!=null){
        String methodIdentifier = modinfo.getParameter(METHOD_ID_PARAMETER);
        if(methodIdentifier==null){
          add(getPropertiesList(ic_objectinstance_id,modinfo));
        }
        else{
          if(modinfo.isParameterSet(VALUE_SAVE_PARAMETER)){
            String[] valueParams = modinfo.getParameterValues(VALUE_PARAMETER);
            //add("value="+value);
            if(valueParams!=null){
              boolean deleteProperty=true;
              String[] values = new String[valueParams.length];
              for (int i = 0; i < valueParams.length; i++) {
                values[i]=modinfo.getParameter(valueParams[i]);
                if(!values[i].equals("")){deleteProperty=false;}
              }
              //System.out.println("setting property 1");
              if(deleteProperty){
                removeProperty(methodIdentifier,ic_objectinstance_id,ib_page_id);
              }
              else{
                setProperty(methodIdentifier,values,ic_objectinstance_id,ib_page_id,modinfo.getApplication());
                setParentToReload();
                close();
              }
            }
          }
          else{
            Form form = new Form();
            add(form);
            form.maintainAllParameters();
            form.add(getPropertySetterBox(methodIdentifier,modinfo,ib_page_id,ic_objectinstance_id));
          }
        }
      }
      else {
        add("IWPropertiesWindow: ICObjectInstanceID is null");
      }
  }

  public ModuleObject getPropertiesList(String ic_object_id,ModuleInfo modinfo)throws Exception{
    Table table = new Table();
    int icObjectInstanceID = Integer.parseInt(ic_object_id);
    IWPropertyList methodList = IBPropertyHandler.getInstance().getMethods(icObjectInstanceID,modinfo.getApplication());
    IWPropertyListIterator iter = methodList.getIWPropertyListIterator();
    int counter=1;
    while (iter.hasNext()) {
      IWProperty methodProp = iter.nextProperty();
      String methodIdentifier = IBPropertyHandler.getInstance().getMethodIdentifier(methodProp);
      String methodDescr = IBPropertyHandler.getInstance().getMethodDescription(methodProp);
      Link link = new Link(methodDescr);
      link.maintainParameter(IC_OBJECT_ID_PARAMETER,modinfo);
      link.maintainParameter(IB_PAGE_PARAMETER,modinfo);
      link.addParameter(METHOD_ID_PARAMETER,methodIdentifier);
      table.add(link,1,counter);
      counter++;
    }
    return table;
  }

  public ModuleObject getPropertySetterBox(String methodIdentifier,ModuleInfo modinfo,String pageID,String icObjectInstanceID)throws Exception{
      Table table = new Table();
      int ypos = 1;
      /*TextInput input = new TextInput(VALUE_PARAMETER);
      String value = BuilderLogic.getInstance().getProperty(pageID,Integer.parseInt(icObjectInstanceID),methodIdentifier);
      if(value!=null){
        input.setContent(value);
      }
      table.add(input,1,1);*/
      Class ICObjectClass = null;
      int icObjectInstanceIDint = Integer.parseInt(icObjectInstanceID);
      if(icObjectInstanceIDint == -1){
        ICObjectClass = com.idega.jmodule.object.Page.class;
      }
      else{
        ICObjectClass = BuilderLogic.getInstance().getObjectClass(icObjectInstanceIDint);
      }
      String namePrefix = "ib_property_";
      java.lang.reflect.Method method = MethodFinder.getInstance().getMethod(methodIdentifier,ICObjectClass);
      Class[] parameters = method.getParameterTypes();
      //System.out.println("parameters.length="+parameters.length);
      //System.out.println("method.toString()="+method.toString());
      List list = BuilderLogic.getInstance().getPropertyValues(pageID,Integer.parseInt(icObjectInstanceID),methodIdentifier);
      Iterator iter = null;
      if(list!=null){
        iter = list.iterator();
      }
      for (int i = 0; i < parameters.length; i++) {
        Class parameterClass = parameters[i];
        String sValue=null;
        try{
          if(iter!=null){sValue = (String)iter.next();}
        }
        catch(java.util.NoSuchElementException e){
        }
        String sName=namePrefix+i;
        ModuleObject handlerBox = IBPropertyHandler.getInstance().getPropertySetterComponent(parameterClass,sName,sValue);
        Parameter param = new Parameter(VALUE_PARAMETER,sName);
        table.add(param,2,ypos);
        table.add(handlerBox,2,ypos);
        ypos++;
      }
      SubmitButton button = new SubmitButton(VALUE_SAVE_PARAMETER,"Save");
      table.add(button,ypos,2);
      return table;
  }

  public void setProperty(String key,String[] values,String icObjectInstanceID,String pageKey,IWMainApplication iwma){
    BuilderLogic.getInstance().setProperty(pageKey,Integer.parseInt(icObjectInstanceID),key,values,iwma);
  }

  public void removeProperty(String key,String icObjectInstanceID,String pageKey){
    /**
     * @todo Change so that it removes properties of specific values for multivalued properties
     */
    String value = "";
    BuilderLogic.getInstance().removeProperty(pageKey,Integer.parseInt(icObjectInstanceID),key,value);
  }


  public static class IBPropertiesWindowMiddle extends FrameSet{

    public void main(ModuleInfo modinfo){
      super.setHorizontal();
      IWURL url1 = FrameSet.getFrameURL(IBPropertiesWindowList.class);
      url1.maintainParameter(IC_OBJECT_ID_PARAMETER,modinfo);
      add(url1.toString());

      IWURL url2 = FrameSet.getFrameURL(IBPropertiesWindowSetter.class);
      url2.maintainParameter(IC_OBJECT_ID_PARAMETER,modinfo);
      add(url2.toString());

      setFrameName(1,IBPropertiesWindowList.LIST_FRAME);
      setFrameName(2,IBPropertiesWindowList.PROPERTY_FRAME);

      setSpanPixels(1,180);
      setSpanAdaptive(2);
    }

  }

  public static class IBPropertiesWindowBottom extends Page{
    public IBPropertiesWindowBottom(){
      setBackgroundColor("gray");
      setAllMargins(0);
      Script script = this.getAssociatedScript();
      script.addFunction("doClose","function doClose(){doUpdate();parent.opener.location.reload();parent.close();}");
      script.addFunction("doUpdate","function doUpdate(){parent."+MIDDLE_FRAME+"."+IBPropertiesWindowList.PROPERTY_FRAME+"."+IBPropertiesWindowSetter.UPDATE_PROPERTY_FUNCTION_NAME+"();}");

    }

    public void main(ModuleInfo modinfo){
      SubmitButton b1 = new SubmitButton("OK");
      b1.setOnClick("doClose()");
      SubmitButton b2 = new SubmitButton("Apply");
      b2.setOnClick("doUpdate()");
      Form form = new Form();
      add(form);
      Table t = new Table(2,1);
      form.add(t);
      t.add(b1,1,1);
      t.add(b2,2,1);
    }
  }

  public static class IBPropertiesWindowTop extends Page{
    public IBPropertiesWindowTop(){
      setBackgroundColor("gray");
    }

    public void main(ModuleInfo modinfo){
      add("Properties");
    }
  }


}