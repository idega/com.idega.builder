package com.idega.builder.presentation;

import com.idega.presentation.*;
import com.idega.presentation.ui.*;
import com.idega.presentation.text.*;

import com.idega.idegaweb.*;

import com.idega.builder.business.IBPropertyHandler;
import com.idega.builder.business.BuilderLogic;

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

public class IBPropertiesWindowSetter extends Page {

  public static final String IC_OBJECT_ID_PARAMETER = IBPropertiesWindow.IC_OBJECT_ID_PARAMETER;
  public static final String IB_PAGE_PARAMETER = IBPropertiesWindow.IB_PAGE_PARAMETER;
  final static String METHOD_ID_PARAMETER= IBPropertiesWindow.METHOD_ID_PARAMETER;
  final static String VALUE_SAVE_PARAMETER = IBPropertiesWindow.VALUE_SAVE_PARAMETER;
  final static String VALUE_PARAMETER = IBPropertiesWindow.VALUE_PARAMETER;
  final static String REMOVE_PARAMETER = "ib_remove_property";
  final static String CHANGE_PROPERTY_PARAMETER = "ib_change_property";

  final static String CHANGE_PROPERTY_FUNCTION_NAME="setProperty";
  final static String UPDATE_PROPERTY_FUNCTION_NAME="update";


  public IBPropertiesWindowSetter(){
  }

  public String getICObjectID(IWContext iwc){
    return iwc.getParameter(IC_OBJECT_ID_PARAMETER);
  }

  public void main(IWContext iwc)throws Exception{
    boolean propertyChange = false;

    Script script = this.getAssociatedScript();
    script.addFunction(CHANGE_PROPERTY_FUNCTION_NAME,"function "+CHANGE_PROPERTY_FUNCTION_NAME+"(method){var form = document.forms[0];form."+CHANGE_PROPERTY_PARAMETER+".value=method;"+UPDATE_PROPERTY_FUNCTION_NAME+"();}");
    script.addFunction(UPDATE_PROPERTY_FUNCTION_NAME,"function "+UPDATE_PROPERTY_FUNCTION_NAME+"(){var form = document.forms[0];form.submit();}");

    String pageKey = BuilderLogic.getInstance().getCurrentIBPage(iwc);

    Form form = new Form();
    add(form);
    form.maintainParameter(IC_OBJECT_ID_PARAMETER);

    Parameter param = new Parameter(CHANGE_PROPERTY_PARAMETER);
    String newPropertyID = iwc.getParameter(CHANGE_PROPERTY_PARAMETER);
    if(newPropertyID!=null){
      param.setValue(newPropertyID);
    }
    else{
      param.setValue("");
    }

    form.add(param);

    String changePropertyID = iwc.getParameter(CHANGE_PROPERTY_PARAMETER);
    if(changePropertyID!=null){
      Parameter param2 = new Parameter(METHOD_ID_PARAMETER,changePropertyID);
      form.add(param2);
    }
    else{
      String oldPropertyPar = iwc.getParameter(CHANGE_PROPERTY_PARAMETER);
      if(oldPropertyPar!=null){
      Parameter param2 = new Parameter(METHOD_ID_PARAMETER,oldPropertyPar);
        form.add(param2);
      }
    }

    String ic_object_id = getICObjectID(iwc);
    if(ic_object_id!=null){
      String propertyID = iwc.getParameter(METHOD_ID_PARAMETER);
      if(propertyID!=null){
          boolean remove = iwc.isParameterSet(REMOVE_PARAMETER);
          if(remove){
            System.out.println("Trying to remove");
            propertyChange=true;
            removeProperty(propertyID,ic_object_id,pageKey);
          }
          else{
            String[] values = parseValues(iwc);
            if(values!=null){
              System.out.println("Trying to save");
              propertyChange = this.setProperty(propertyID,values,ic_object_id,pageKey,iwc.getApplication());
            }
            else{
              System.out.println("Not Trying to save - values == null");
            }
          }
      }

      if(propertyChange){
        doReload();
      }
      else{
        if(newPropertyID!=null){
          form.add(getPropertySetterBox(newPropertyID,iwc,null,ic_object_id));
          form.add(getRemoveButton());
        }
      }

      //System.out.println("IBPropertiesWindowList: Getting IC_OBJECT_ID");
    }
    else{
      //System.out.println("IBPropertiesWindowList: Not getting IC_OBJECT_ID");
    }
  }

  public void doReload(){
    setOnLoad("doReload()");
    Script script = this.getAssociatedScript();
    script.addFunction("doReload","function doReload(form){;parent.parent.opener.location.reload();document.forms[0].submit();}");
  }

  public PresentationObject getRemoveButton(){
    Table t = new Table(2,1);
    t.add("Remove Property",1,1);
    CheckBox button = new CheckBox(REMOVE_PARAMETER);
    t.add(button,2,1);
    return t;
  }

  public String[] parseValues(IWContext iwc){
            String[] valueParams = iwc.getParameterValues(VALUE_PARAMETER);
            String[] values = null;
            //add("value="+value);
            boolean setProperty=false;
            if(valueParams!=null){
              values = new String[valueParams.length];
              for (int i = 0; i < valueParams.length; i++) {
                values[i]=iwc.getParameter(valueParams[i]);
                if(!values[i].equals("")){setProperty=true;}
              }
            }

            if(setProperty){
              return values;
            }
            else{
              return null;
            }
  }

  public PresentationObject getPropertySetterBox(String methodIdentifier,IWContext iwc,String pageID,String icObjectInstanceID)throws Exception{
      if(pageID==null){pageID = BuilderLogic.getInstance().getCurrentIBPage(iwc);}

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
        ICObjectClass = com.idega.presentation.Page.class;
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
        PresentationObject handlerBox = IBPropertyHandler.getInstance().getPropertySetterComponent(parameterClass,sName,sValue);
        Parameter param = new Parameter(VALUE_PARAMETER,sName);
        table.add(param,2,ypos);
        table.add(handlerBox,2,ypos);
        ypos++;
      }
      //SubmitButton button = new SubmitButton(VALUE_SAVE_PARAMETER,"Save");
      //table.add(button,ypos,2);
      return table;
  }


  public boolean setProperty(String key,String[] values,String icObjectInstanceID,String pageKey,IWMainApplication iwma){
    return BuilderLogic.getInstance().setProperty(pageKey,Integer.parseInt(icObjectInstanceID),key,values,iwma);
  }

  public void removeProperty(String key,String icObjectInstanceID,String pageKey){
    /**
     * @todo Change so that it removes properties of specific values for multivalued properties
     */
    String value = "";
    BuilderLogic.getInstance().removeProperty(pageKey,Integer.parseInt(icObjectInstanceID),key,value);
  }


}