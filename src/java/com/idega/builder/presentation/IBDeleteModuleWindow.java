package com.idega.builder.presentation;

import com.idega.idegaweb.IWConstants;
import com.idega.builder.business.BuilderConstants;
import com.idega.core.component.business.ICObjectBusiness;
import com.idega.idegaweb.IWResourceBundle;
import javax.servlet.http.Cookie;
import com.idega.presentation.ui.*;
import com.idega.builder.business.BuilderLogic;

import com.idega.presentation.PresentationObject;
import com.idega.presentation.IWContext;
import com.idega.presentation.Table;
import com.idega.presentation.text.Text;

/**
 * Title:        idegaclasses
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href="tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */

public class IBDeleteModuleWindow extends IBAdminWindow{

  private static final String ic_object_id_parameter = BuilderLogic.IC_OBJECT_INSTANCE_ID_PARAMETER;
  private static final String ib_parent_parameter = BuilderLogic.IB_PARENT_PARAMETER;
  private static final String ib_page_parameter = BuilderConstants.IB_PAGE_PARAMETER;
  private static final String COOKIE_NAME = "not_again";

  private static final String IB_DELETE_CONFIRM = "ib_del_confirm";
  private static final String IB_DELETE_CLOSE = "ib_del_close";
  private static final String IW_BUNDLE_IDENTIFIER  = "com.idega.builder";

  public IBDeleteModuleWindow() {
    setWidth(240);
    setHeight(140);
    setScrollbar(false);
  }

  public void main(IWContext iwc){
      if ( iwc.isParameterSet(IB_DELETE_CLOSE) ) {
	close();
      }

      setTitle("Confirm delete");
      String ib_parent_id = iwc.getParameter(ib_parent_parameter);
      String ib_page_id = iwc.getParameter(ib_page_parameter);
      this.setParentToReload();
      String ic_object_id = iwc.getParameter(ic_object_id_parameter);
      this.addTitle(ICObjectBusiness.getInstance().getNewObjectInstance(Integer.parseInt(ic_object_id)).getBuilderName(iwc),IWConstants.BUILDER_FONT_STYLE_TITLE);

      boolean doConfirm = !iwc.isParameterSet(IB_DELETE_CONFIRM);
      if ( iwc.isCookieSet(COOKIE_NAME) ) {
	doConfirm = false;
      }
      if(doConfirm){
	add(getConfirmBox(iwc));
      }
      else{
	if ( iwc.getParameter("not_again") != null )
	  setCookie(iwc);
	deleteObject(ib_page_id,ib_parent_id,ic_object_id);
	this.close();
      }
  }

  public void deleteObject(String pageKey,String parentID,String objectID){
     BuilderLogic.getInstance().deleteModule(pageKey,parentID,Integer.parseInt(objectID));
  }

  private void setCookie(IWContext iwc) {
    Cookie cookie = new Cookie(COOKIE_NAME,"true");
      cookie.setMaxAge(42*24*60*60);
      cookie.setPath("/");
    iwc.addCookies(cookie);
  }

  public PresentationObject getConfirmBox(IWContext iwc){
    IWResourceBundle iwrb = iwc.getIWMainApplication().getBundle(BuilderLogic.IW_BUNDLE_IDENTIFIER).getResourceBundle(iwc);

    Table t = new Table(1,3);
    t.setCellpadding(6);
    t.setAlignment(1,3,"right");
    Form f = new Form();

    f.maintainParameter(ic_object_id_parameter);
    f.maintainParameter(ib_parent_parameter);
    f.maintainParameter(ib_page_parameter);

    f.add(t);
    t.setWidth("100%");

    Text confirmText = new Text("Are you sure you want to delete this module and all its contents?");
      confirmText.setFontStyle(IWConstants.BUILDER_FONT_STYLE_LARGE);
    t.add(confirmText,1,1);
    Text notAgainText = new Text("Don't ask again for 42 days");
      notAgainText.setFontStyle(IWConstants.BUILDER_FONT_STYLE_SMALL);

    SubmitButton button = new SubmitButton(iwrb.getLocalizedImageButton("yes","Yes"),this.IB_DELETE_CONFIRM);
    SubmitButton closebutton = new SubmitButton(iwrb.getLocalizedImageButton("cancel","Cancel"),this.IB_DELETE_CLOSE);
    CheckBox notAgain = new CheckBox("not_again","true");

    t.add(closebutton,1,3);
    t.add(Text.getNonBrakingSpace(),1,3);
    t.add(button,1,3);
    t.add(notAgain,1,2);
    t.add(notAgainText,1,2);

    return f;
  }

  public String getBundleIdentifier(){
    return IW_BUNDLE_IDENTIFIER;
  }

}
