/*
 * $Id: IBPropertiesWindowSetter.java,v 1.18 2002/04/10 01:57:38 tryggvil Exp $
 *
 * Copyright (C) 2001 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package com.idega.builder.presentation;

import com.idega.core.business.ICObjectBusiness;
import com.idega.presentation.Page;
import com.idega.presentation.IWContext;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.Script;
import com.idega.presentation.Table;
import com.idega.presentation.ui.Parameter;
import com.idega.presentation.ui.CheckBox;
import com.idega.presentation.ui.Form;
import com.idega.presentation.ui.HiddenInput;
import com.idega.presentation.text.Text;
import com.idega.idegaweb.IWMainApplication;
import com.idega.builder.business.IBPropertyHandler;
import com.idega.builder.business.BuilderLogic;
import com.idega.builder.handler.PropertyHandler;
import com.idega.util.reflect.MethodFinder;
import java.lang.reflect.Method;

/**
 * @author <a href="tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */
public class IBPropertiesWindowSetter extends Page {
  //Parameters used in the window
  public static final String IC_OBJECT_INSTANCE_ID_PARAMETER = IBPropertiesWindow.IC_OBJECT_INSTANCE_ID_PARAMETER;
  public static final String IB_PAGE_PARAMETER = IBPropertiesWindow.IB_PAGE_PARAMETER;
  final static String METHOD_ID_PARAMETER = IBPropertiesWindow.METHOD_ID_PARAMETER;
  final static String VALUE_SAVE_PARAMETER = IBPropertiesWindow.VALUE_SAVE_PARAMETER;
  final static String VALUE_PARAMETER = IBPropertiesWindow.VALUE_PARAMETER;
  final static String REMOVE_PARAMETER = "ib_remove_property";
  final static String CHANGE_PROPERTY_PARAMETER = "ib_change_property";
  final static String IS_CHANGING_PROPERTY_BOOLEAN_PARAMETER = "ib_is_changing";
  final static String SAVE_PROPERTY_PARAMETER = "ib_save_prop";

  //Javascript Functions names used in the window
  final static String CHANGE_PROPERTY_FUNCTION_NAME = "setProperty";
  final static String UPDATE_PROPERTY_FUNCTION_NAME = "update";
  public final static String MULTIVALUE_PROPERTY_CHANGE_FUNCTION_NAME = "multivalueChange";

//  private static final String BACKGROUND_COLOUR = "#E4E0D8";
  private static final String BACKGROUND_COLOUR = "#FFFFFF";
  private static final String HANDLER_PARAMETER = "handler_parameter";

  /**
   *
   */
  public IBPropertiesWindowSetter() {
    setBackgroundColor(BACKGROUND_COLOUR);
  }

  /**
   *
   */
  public String getUsedICObjectInstanceID(IWContext iwc) {
    return iwc.getParameter(IC_OBJECT_INSTANCE_ID_PARAMETER);
  }

  /**
   *
   */
  public int getUsedICObjectInstanceIDInt(IWContext iwc) {
    String s = getUsedICObjectInstanceID(iwc);
    return(Integer.parseInt(s));
  }

  private boolean isChangingProperty(IWContext iwc) {
    String sValue = iwc.getParameter(IS_CHANGING_PROPERTY_BOOLEAN_PARAMETER);
    if (sValue != null) {
      if (sValue.equals("Y"))
	return(true);
    }

    return(false);
  }

  /**
   *
   */
  public void main(IWContext iwc) throws Exception {
    boolean propertyChange = false;

    Script script = this.getAssociatedScript();
    script.addFunction(CHANGE_PROPERTY_FUNCTION_NAME,"function "+CHANGE_PROPERTY_FUNCTION_NAME+"(method){var form = document.forms[0];form."+CHANGE_PROPERTY_PARAMETER+".value=method;form."+IS_CHANGING_PROPERTY_BOOLEAN_PARAMETER+".value='Y';"+UPDATE_PROPERTY_FUNCTION_NAME+"();}");
    script.addFunction(UPDATE_PROPERTY_FUNCTION_NAME,"function "+UPDATE_PROPERTY_FUNCTION_NAME+"(){var form = document.forms[0];form.submit();}");
    script.addFunction(MULTIVALUE_PROPERTY_CHANGE_FUNCTION_NAME,"function "+MULTIVALUE_PROPERTY_CHANGE_FUNCTION_NAME+"(){var form = document.forms[0];form."+SAVE_PROPERTY_PARAMETER+".value='false';"+UPDATE_PROPERTY_FUNCTION_NAME+"();}");

    String pageKey = BuilderLogic.getInstance().getCurrentIBPage(iwc);

    Form form = new Form();
    add(form);
    form.maintainParameter(IC_OBJECT_INSTANCE_ID_PARAMETER);

    Parameter param1 = new Parameter(SAVE_PROPERTY_PARAMETER,"true");
    form.add(param1);
    Parameter param = new Parameter(CHANGE_PROPERTY_PARAMETER);
    Parameter param3 = new Parameter(IS_CHANGING_PROPERTY_BOOLEAN_PARAMETER,"N");
    form.add(param3);
    String newPropertyID = iwc.getParameter(CHANGE_PROPERTY_PARAMETER);
    if (newPropertyID != null) {
      param.setValue(newPropertyID);
    }
    else {
      param.setValue("");
    }

    form.add(param);

    String changePropertyID = iwc.getParameter(CHANGE_PROPERTY_PARAMETER);
    if (changePropertyID != null) {
      Parameter param2 = new Parameter(METHOD_ID_PARAMETER,changePropertyID);
      form.add(param2);
    }
    else {
      String oldPropertyPar = iwc.getParameter(CHANGE_PROPERTY_PARAMETER);
      if (oldPropertyPar != null) {
	Parameter param2 = new Parameter(METHOD_ID_PARAMETER,oldPropertyPar);
	form.add(param2);
      }
    }

    boolean doSave = true;
    String sDoSave = iwc.getParameter(SAVE_PROPERTY_PARAMETER);
    if (sDoSave != null) {
      if (sDoSave.equalsIgnoreCase("false")) {
	doSave = false;
      }
      else {
	doSave = true;
      }
    }

    String ic_object_id = getUsedICObjectInstanceID(iwc);
    if (ic_object_id != null) {
      String propertyID = iwc.getParameter(METHOD_ID_PARAMETER);
      if (propertyID != null) {
	  boolean remove = iwc.isParameterSet(REMOVE_PARAMETER);
	  String values[] = parseValues(iwc);
	  if (values == null)
	  doSave = false;
	  if (remove) {
	    if (values != null) {
	      propertyChange = true;
		  removeProperty(iwc.getApplication(),propertyID,values,ic_object_id,pageKey);
	    }
	      }
	  else {
	    if (doSave) {
	      propertyChange = setProperty(propertyID,values,ic_object_id,pageKey,iwc.getApplication());
	    PropertyHandler handler = (PropertyHandler)iwc.getSessionAttribute(HANDLER_PARAMETER);

	      if (handler != null) {
			handler.onUpdate(values,iwc);
	      iwc.removeSessionAttribute(HANDLER_PARAMETER);
	      }
	    }
	      }
      }

      if (propertyChange) {
	doReload();
      }
      else {
	if (newPropertyID != null) {
	  int iICObjectInstanceID = this.getUsedICObjectInstanceIDInt(iwc);
	  Text description = new Text(IBPropertyHandler.getInstance().getMethodDescription(iICObjectInstanceID,newPropertyID,iwc));
	  description.setFontStyle("font-family:Arial,Helvetica,sans-serif;font-size:11pt;font-weight:bold;");
	  form.add(description);
	  form.add(getPropertySetterBox(newPropertyID,iwc,null,ic_object_id));
	  form.add(getRemoveButton());
	}
      }
    }
  }

  /**
   *
   */
  public void doReload() {
    setOnLoad("doReload()");
    Script script = this.getAssociatedScript();
    script.addFunction("doReload","function doReload(form){document.forms[0].submit();}");
  }

  /**
   *
   */
  public PresentationObject getRemoveButton() {
    Table t = new Table(2,1);
    Text removeProperty = new Text("Remove Property");
    removeProperty.setFontStyle("font-family:Arial,Helvetica,sans-serif;font-size:8pt;");
    t.add(removeProperty,1,1);
    CheckBox button = new CheckBox(REMOVE_PARAMETER);
    t.add(button,2,1);
    return(t);
  }

  /**
   *
   */
  public String[] parseValues(IWContext iwc) {
    String valueParams[] = iwc.getParameterValues(VALUE_PARAMETER);
    String values[] = null;
    boolean setProperty = false;
    if (valueParams != null) {
      values = new String[valueParams.length];
      for (int i = 0; i < valueParams.length; i++) {
		values[i] = iwc.getParameter(valueParams[i]);
		if (!values[i].equals("")) {
	  setProperty = true;
	}
      }
    }

    if (setProperty) {
      return(values);
    }
    else {
      return(null);
    }
  }

  /**
   *
   */
  public PresentationObject getPropertySetterBox(String methodIdentifier, IWContext iwc, String pageID, String icObjectInstanceID) throws Exception {
    if (pageID == null) {
      pageID = BuilderLogic.getInstance().getCurrentIBPage(iwc);
    }

    Table table = new Table();
    int ypos = 1;

    Class ICObjectClass = null;
    int icObjectInstanceIDint = Integer.parseInt(icObjectInstanceID);
    if (icObjectInstanceIDint == -1) {
      ICObjectClass = com.idega.presentation.Page.class;
    }
    else {
      ICObjectClass = BuilderLogic.getInstance().getObjectClass(icObjectInstanceIDint);
    }
    String namePrefix = "ib_property_";
    Method method = MethodFinder.getInstance().getMethod(methodIdentifier,ICObjectClass);
    Class parameters[] = method.getParameterTypes();
    String selectedValues[] = parseValues(iwc);
    String paramDescriptions[] = IBPropertyHandler.getInstance().getPropertyDescriptions(iwc,icObjectInstanceID,methodIdentifier);
    boolean isChangingProperty = isChangingProperty(iwc);
    String realValues[] = BuilderLogic.getInstance().getPropertyValues(iwc.getApplication(),pageID,Integer.parseInt(icObjectInstanceID),methodIdentifier,selectedValues,!isChangingProperty);

    for (int i = 0; i < parameters.length; i++) {
      Class parameterClass = parameters[i];
      String sValue = "";

      try {
	sValue = realValues[i];
      }
      catch(ArrayIndexOutOfBoundsException e) {
      }
      catch(NullPointerException npe){
      }

      String sName = namePrefix + i;
      String sParamDescription = paramDescriptions[i];
      PresentationObject handlerBox = IBPropertyHandler.getInstance().getPropertySetterComponent(iwc,icObjectInstanceID,methodIdentifier,i,parameterClass,sName,sValue);

      String handlerClass = IBPropertyHandler.getInstance().getMethodParameterProperty(iwc,icObjectInstanceID,methodIdentifier,i,IBPropertyHandler.METHOD_PARAMETER_PROPERTY_HANDLER_CLASS);

      PropertyHandler handler = null;
      if (handlerClass != null && !handlerClass.equals("")) {
	handler = IBPropertyHandler.getInstance().getPropertyHandler(handlerClass);

	if (handler != null)
	  iwc.setSessionAttribute(HANDLER_PARAMETER,handler);
      }

      if (handler == null) {
	iwc.removeSessionAttribute(HANDLER_PARAMETER);
      }

      Parameter param = new Parameter(VALUE_PARAMETER,sName);
      if (sParamDescription != null) {
	Text tDescription = formatDescription(sParamDescription+":");
	tDescription.setFontStyle("font-family:Arial,Helvetica,sans-serif;font-size:8pt;");
	table.add(tDescription,1,ypos);
      }
      table.add(param,2,ypos);
      table.add(handlerBox,2,ypos);
      ypos++;
    }
    table.setColumnVerticalAlignment(1,"top");

    return(table);
  }

  public Text formatDescription(String text) {
    return(new Text(text));
  }

  /**
   *
   */
  public boolean setProperty(String key, String values[], String icObjectInstanceID, String pageKey, IWMainApplication iwma) {
  //invalidate cache for blocks
    PresentationObject obj = ICObjectBusiness.getInstance().getNewObjectInstance(icObjectInstanceID);
    if( obj instanceof com.idega.presentation.Block ){
      iwma.getIWCacheManager().invalidateCache( ((com.idega.presentation.Block)obj).getCacheKey());
    }
  //

    return(BuilderLogic.getInstance().setProperty(pageKey,Integer.parseInt(icObjectInstanceID),key,values,iwma));


  }

  /**
   *
   */
  public void removeProperty(IWMainApplication iwma, String key, String values[], String icObjectInstanceID, String pageKey) {
    /**
     * @todo Change so that it removes properties of specific values for multivalued properties
     */
  //invalidate cache for blocks
    PresentationObject obj = ICObjectBusiness.getInstance().getNewObjectInstance(icObjectInstanceID);
    if( obj instanceof com.idega.presentation.Block ){
       iwma.getIWCacheManager().invalidateCache( ((com.idega.presentation.Block)obj).getCacheKey());
    }
  //

    BuilderLogic.getInstance().removeProperty(iwma,pageKey,Integer.parseInt(icObjectInstanceID),key,values);
  }
}
