package com.idega.builder.presentation;

import com.idega.presentation.ui.*;
import com.idega.presentation.*;

import com.idega.builder.business.BuilderLogic;
import com.idega.builder.business.IBXMLPage;

/**
 * Title:        idegaclasses
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href="tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */

public class IBSourceView extends Window {

  private static final String SOURCE_PARAMETER = "ib_page_source";
  private static final String IB_SOURCE_ACTION = "ib_page_source_action";

  public IBSourceView() {
    setWidth(700);
    setHeight(600);
  }


  public void main(IWContext iwc){

      String action = iwc.getParameter(IB_SOURCE_ACTION);
      if(action!=null){
        if(action.equals("update")){
          try{
            String stringRep = iwc.getParameter(SOURCE_PARAMETER);
            if(stringRep!=null){
              doUpdate(stringRep,iwc);
              this.setParentToReload();
            }
          }
          catch(Exception e){
            add("Error: "+e.getMessage());
            e.printStackTrace();
          }
        }
      }

      Form form = new Form();
      Table table = new Table(1,2);
      form.add(table);
      form.addParameter(IB_SOURCE_ACTION,"update");
      add(form);
      TextArea area = new TextArea(SOURCE_PARAMETER);
      area.setWidth(70);
      area.setHeight(28);
      area.setWrap(false);
      setSource(area,iwc);
      table.add(area,1,1);
      SubmitButton button = new SubmitButton("Save");
      table.add(button,1,2);


  }


  private void doUpdate(String sourceString,IWContext iwc)throws Exception{
    IBXMLPage page = BuilderLogic.getInstance().getCurrentIBXMLPage(iwc);
    page.setSourceFromString(sourceString);
  }

  public void setSource(TextArea area,IWContext iwc){
    IBXMLPage page = BuilderLogic.getInstance().getCurrentIBXMLPage(iwc);
    String source = page.toString();
    area.setContent(source);
  }


}
