package com.idega.builder.presentation;

import com.idega.builder.business.BuilderLogic;
import com.idega.builder.business.IBPropertyHandler;
import com.idega.presentation.IWContext;
import com.idega.presentation.FrameSet;

import com.idega.idegaweb.IWProperty;
import com.idega.idegaweb.IWPropertyList;
import com.idega.idegaweb.IWPropertyListIterator;
import com.idega.idegaweb.IWMainApplication;
import com.idega.idegaweb.IWURL;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.idegaweb.presentation.IWAdminWindow;

import com.idega.presentation.*;
import com.idega.presentation.text.*;
import com.idega.presentation.ui.*;

import com.idega.util.reflect.MethodFinder;

import com.idega.core.data.ICObject;
import com.idega.core.data.ICObjectInstance;

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

   static final String IC_OBJECT_INSTANCE_ID_PARAMETER = BuilderLogic.IC_OBJECT_INSTANCE_ID_PARAMETER;
   static final String IB_PAGE_PARAMETER = BuilderLogic.IB_PAGE_PARAMETER;
   final static String METHOD_ID_PARAMETER="iw_method_identifier";
   final static String VALUE_SAVE_PARAMETER = "ib_method_save";
   final static String VALUE_PARAMETER = "ib_method_value";

   final static String TOP_FRAME = "ib_prop_win_top";
   final static String MIDDLE_FRAME = "ib_prop_win_middle";
   final static String BOTTOM_FRAME = "ib_prop_win_bottom";

   private final static String HEADER_TEXT_PARAMETER = "ib_prop_win_header";

  public void main(IWContext iwc) throws Exception{
    super.setStatus(true);
    String title = "Properties";

    //System.out.println("BuilderLogic.IB_CONTROL_PARAMETER: "+iwc.getParameter(BuilderLogic.IB_CONTROL_PARAMETER)+" and BuilderLogic.IB_PAGE_PARAMETER: "+iwc.getParameter(BuilderLogic.IB_PAGE_PARAMETER));

    try{
      String sICObjectInstanceID = iwc.getParameter(IC_OBJECT_INSTANCE_ID_PARAMETER);
      if(sICObjectInstanceID!=null){
        title += " : ";
        int iInstanceID = Integer.parseInt(sICObjectInstanceID);
        ICObjectInstance instance = com.idega.core.business.ICObjectBusiness.getInstance().getICObjectInstance(iInstanceID);
        ICObject ico = instance.getObject();
        String name = ico.getName();
        title += name;
      }
    }
    catch(Exception e){

    }

    super.setTitle(title);
    super.setWidth(600);
    super.setHeight(600);

    //add(IBPropertiesWindowTop.class);
    IWURL topURL = FrameSet.getFrameURL(IBPropertiesWindowTop.class);
    topURL.addParameter(HEADER_TEXT_PARAMETER,title);
    add(topURL.toString());

    //add(IBPropertiesWindowMiddle.class);
    IWURL mURL = FrameSet.getFrameURL(IBPropertiesWindowMiddle.class);
    mURL.maintainParameter(IC_OBJECT_INSTANCE_ID_PARAMETER,iwc);
    add(mURL.toString());

    add(IBPropertiesWindowBottom.class);
    this.setSpanPixels(1,30);
    this.setSpanAdaptive(2);
    this.setSpanPixels(3,35);

    this.setScrolling(1,false);

    this.setFrameName(1,TOP_FRAME);
    this.setFrameName(2,MIDDLE_FRAME);
    this.setFrameName(3,BOTTOM_FRAME);
  }


  public static class IBPropertiesWindowMiddle extends FrameSet{

    public void main(IWContext iwc){
      super.setHorizontal();
      IWURL url1 = FrameSet.getFrameURL(IBPropertiesWindowList.class);
      url1.maintainParameter(IC_OBJECT_INSTANCE_ID_PARAMETER,iwc);
      add(url1.toString());

      IWURL url2 = FrameSet.getFrameURL(IBPropertiesWindowSetter.class);
      url2.maintainParameter(IC_OBJECT_INSTANCE_ID_PARAMETER,iwc);
      add(url2.toString());

      setFrameName(1,IBPropertiesWindowList.LIST_FRAME);
      setFrameName(2,IBPropertiesWindowList.PROPERTY_FRAME);

      setSpanPixels(1,180);
      setSpanAdaptive(2);
    }

  }

  public static class IBPropertiesWindowBottom extends Page{
    public IBPropertiesWindowBottom(){
      setBackgroundColor(IWAdminWindow.HEADER_COLOR);
      setAllMargins(0);
      Script script = this.getAssociatedScript();
      script.addFunction("doClose","function doClose(){doUpdate();parent.opener.location.reload();parent.close();}");
      script.addFunction("doUpdate","function doUpdate(){parent."+MIDDLE_FRAME+"."+IBPropertiesWindowList.PROPERTY_FRAME+"."+IBPropertiesWindowSetter.UPDATE_PROPERTY_FUNCTION_NAME+"();}");

    }

    public void main(IWContext iwc){
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

  public static class IBPropertiesWindowTop extends IWAdminWindow{
    public IBPropertiesWindowTop(){
      setAllMargins(0);
      //setBackgroundColor("gray");
    }

    public void main(IWContext iwc){
      //Text t = new Text("Properties");
      //t.setBold();
      //add(t);
      String title = iwc.getParameter(HEADER_TEXT_PARAMETER);
      if(title!=null){
        super.addTitle(title);
      }
      else{
        IWResourceBundle iwrb = getBundle(iwc).getResourceBundle(iwc);
        super.addTitle(iwrb.getLocalizedString("ib_properties_window_title","Properties"));
      }

    }
  }





}