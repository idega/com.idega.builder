package com.idega.builder.presentation;

import com.idega.builder.business.BuilderLogic;

import com.idega.presentation.PresentationObject;
import com.idega.presentation.IWContext;
import com.idega.presentation.ui.Form;
import com.idega.presentation.Table;
import com.idega.presentation.ui.SubmitButton;
import com.idega.presentation.ui.CloseButton;
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
  private static final String ib_page_parameter = BuilderLogic.IB_PAGE_PARAMETER;

  private static final String IB_DELETE_CONFIRM = "ib_del_confirm";

  public IBDeleteModuleWindow() {
    setWidth(300);
    setHeight(200);
  }

  public void main(IWContext iwc){

      setTitle("Confirm delete");

      String ib_parent_id = iwc.getParameter(ib_parent_parameter);
      String ib_page_id = iwc.getParameter(ib_page_parameter);
      this.setParentToReload();
      String ic_object_id = iwc.getParameter(ic_object_id_parameter);

      boolean doConfirm = !iwc.isParameterSet(IB_DELETE_CONFIRM);
      if(doConfirm){
        add(getConfirmBox(iwc));
      }
      else{
        deleteObject(ib_page_id,ib_parent_id,ic_object_id);
        this.close();
      }
  }

  public void deleteObject(String pageKey,String parentID,String objectID){
     BuilderLogic.getInstance().deleteModule(pageKey,parentID,Integer.parseInt(objectID));
  }

  public PresentationObject getConfirmBox(IWContext iwc){
    Table t = new Table(1,2);
    Form f = new Form();

    f.maintainParameter(ic_object_id_parameter);
    f.maintainParameter(ib_parent_parameter);
    f.maintainParameter(ib_page_parameter);

    f.add(t);
    t.setWidth("100%");
    t.setHeight("150");
    t.setAlignment(com.idega.idegaweb.IWConstants.CENTER_ALIGNMENT);

    Text confirmText = new Text("Are you sure you want to delete this element and all its contents?");
    t.add(confirmText,1,1);

    SubmitButton button = new SubmitButton(this.IB_DELETE_CONFIRM,"Yes");
    CloseButton closebutton = new CloseButton("Cancel");

    Table innerTable = new Table(2,1);
    innerTable.setAlignment(com.idega.idegaweb.IWConstants.CENTER_ALIGNMENT);
    innerTable.add(button,1,1);
    innerTable.add(closebutton,2,1);
    t.add(innerTable,1,2);

    return f;
  }

}