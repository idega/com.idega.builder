package com.idega.builder.presentation;

import com.idega.builder.business.BuilderLogic;
import com.idega.core.component.business.ICObjectBusiness;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.idegaweb.IWURL;
import com.idega.idegaweb.presentation.IWAdminWindow;
import com.idega.presentation.FrameSet;
import com.idega.presentation.IWContext;
import com.idega.presentation.Image;
import com.idega.presentation.Page;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.Script;
import com.idega.presentation.Table;

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

public IBPropertiesWindow() {
  super.setWidth(420);
  super.setHeight(350);
}

  public void main(IWContext iwc) throws Exception{
    String title = "Properties";

    //System.out.println("BuilderLogic.IB_CONTROL_PARAMETER: "+iwc.getParameter(BuilderLogic.IB_CONTROL_PARAMETER)+" and BuilderLogic.IB_PAGE_PARAMETER: "+iwc.getParameter(BuilderLogic.IB_PAGE_PARAMETER));

    try{
      String sICObjectInstanceID = iwc.getParameter(IC_OBJECT_INSTANCE_ID_PARAMETER);
      if(sICObjectInstanceID!=null){
	title += " : ";
	int iInstanceID = Integer.parseInt(sICObjectInstanceID);
	/*ICObjectInstance instance = com.idega.core.business.ICObjectBusiness.getInstance().getICObjectInstance(iInstanceID);
	ICObject ico = instance.getObject();*/
        PresentationObject obj = ICObjectBusiness.getInstance().getNewObjectInstance(iInstanceID);
	String name = obj.getBuilderName(iwc);
	title += name;
      }
    }
    catch(Exception e){

    }

    super.setTitle(title);
    //add(IBPropertiesWindowTop.class);
    IWURL topURL = FrameSet.getFrameURL(IBPropertiesWindowTop.class,iwc);
    topURL.addParameter(HEADER_TEXT_PARAMETER,title);
    add(topURL.toString());

    //add(IBPropertiesWindowMiddle.class);
    IWURL mURL = FrameSet.getFrameURL(IBPropertiesWindowMiddle.class,iwc);
    mURL.maintainParameter(IC_OBJECT_INSTANCE_ID_PARAMETER,iwc);
    add(mURL.toString());

    add(IBPropertiesWindowBottom.class);
    this.setSpanPixels(1,25);
    this.setSpanAdaptive(2);
    this.setSpanPixels(3,21);

    this.setScrolling(1,false);
    this.setScrolling(3,false);

    this.setFrameName(1,TOP_FRAME);
    this.setFrameName(2,MIDDLE_FRAME);
    this.setFrameName(3,BOTTOM_FRAME);
  }


  public static class IBPropertiesWindowMiddle extends FrameSet{

    public void main(IWContext iwc){
      super.setHorizontal();
      IWURL url1 = FrameSet.getFrameURL(IBPropertiesWindowList.class,iwc);
      url1.maintainParameter(IC_OBJECT_INSTANCE_ID_PARAMETER,iwc);
      add(url1.toString());

      IWURL url2 = FrameSet.getFrameURL(IBPropertiesWindowSetter.class,iwc);
      url2.maintainParameter(IC_OBJECT_INSTANCE_ID_PARAMETER,iwc);
      add(url2.toString());

      setFrameName(1,IBPropertiesWindowList.LIST_FRAME);
      setFrameName(2,IBPropertiesWindowList.PROPERTY_FRAME);

      setSpanPixels(1,190);
      setScrolling(1,true);
      setSpanAdaptive(2);
    }

  }

  public static class IBPropertiesWindowBottom extends Page{
    public IBPropertiesWindowBottom(){
      setBackgroundColor(IWAdminWindow.HEADER_COLOR);
      setAllMargins(0);
      Script script = this.getAssociatedScript();
      script.addFunction("doClose","function doClose(){parent.close();}");
      script.addFunction("doApply","function doApply(){doSet();parent.opener.location.reload();}");
      script.addFunction("doSet","function doSet(){parent."+MIDDLE_FRAME+"."+IBPropertiesWindowList.PROPERTY_FRAME+"."+IBPropertiesWindowSetter.UPDATE_PROPERTY_FUNCTION_NAME+"();top.ib_prop_win_middle.ib_prop_list_frame.location.reload();}");

    }

    public void main(IWContext iwc){
      Table table = new Table(1, 1);
      table.setCellpaddingAndCellspacing(0);
      table.setWidth(Table.HUNDRED_PERCENT);
      add(table);
    	IWResourceBundle iwrb = iwc.getApplication().getBundle(BuilderLogic.IW_BUNDLE_IDENTIFIER).getResourceBundle(iwc);

      Image b1 = iwrb.getLocalizedImageButton("close","CLOSE");
      b1.setOnClick("javascript:doClose()");
      Image b2 = iwrb.getLocalizedImageButton("set","SET");
      b2.setOnClick("javascript:doSet()");
      Image b3 = iwrb.getLocalizedImageButton("apply","Apply");
      b3.setOnClick("javascript:doApply()");
      Table t = new Table(3,1);
      t.setHeight("100%");
      table.setAlignment(1, 1, "right");
      table.add(t, 1, 1);
      t.add(b1,1,1);
      t.add(b2,2,1);
      t.add(b3,3,1);
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
	super.addTitle(title,"font-family:Verdana,Arial,Helvetica,sans-serif;font-size:9pt;font-weight:bold;color:#FFFFFF;");
      }
      else{
	IWResourceBundle iwrb = getBundle(iwc).getResourceBundle(iwc);
	super.addTitle(iwrb.getLocalizedString("ib_properties_window_title","Properties"),"font-family:Verdana,Arial,Helvetica,sans-serif;font-size:11pt;font-weight:bold;color:#FFFFFF;");
      }

    }
  }





}
