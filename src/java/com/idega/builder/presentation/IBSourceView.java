package com.idega.builder.presentation;

import com.idega.presentation.text.PreformattedText;
import com.idega.presentation.ui.*;
import com.idega.presentation.*;

import com.idega.builder.business.BuilderLogic;
import com.idega.builder.business.IBXMLPage;
import com.idega.core.builder.data.ICPage;

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
  private static final String IB_PAGE_FORMAT="ib_page_fomat";
  
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
				String format = iwc.getParameter(IB_PAGE_FORMAT);  
				doUpdate(stringRep,format,iwc);
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
      Table table = new Table(1,4);
      form.add(table);
      form.addParameter(IB_SOURCE_ACTION,"update");
      add(form);
      TextArea area = new TextArea(SOURCE_PARAMETER);
      area.setWidth("600");
      area.setHeight("500");
      area.setWrap(false);
      setSource(area,iwc);
      table.add(area,1,1);
      DropdownMenu menu = getFormatDropdown(iwc);
      table.add(menu,1,2);
      SubmitButton button = new SubmitButton("Save");
      table.add(button,1,3);
      String templateString = "Note: Template regions in HTML templates are defined like this:\n<code>&lt;!-- TemplateBeginEditable name=\"MyUniqueRegionId1\" --&gt;MyUniqueRegionId1&lt;!-- TemplateEndEditable --&gt;</code>";
      PreformattedText helpText = new PreformattedText(templateString);
      table.add(helpText,1,4);
 

  }


  private void doUpdate(String sourceString,String pageFormat,IWContext iwc)throws Exception{
    //IBXMLPage page = BuilderLogic.getInstance().getCurrentIBXMLPage(iwc);
    //page.setSourceFromString(sourceString);
  	BuilderLogic.getInstance().setPageSource(iwc,pageFormat,sourceString);
  }

  public void setSource(TextArea area,IWContext iwc){
    //IBXMLPage page = BuilderLogic.getInstance().getCurrentIBXMLPage(iwc);
  	try{
	    String source = BuilderLogic.getInstance().getPageSource(iwc);
	  	area.setContent(source);
  	}
  	catch(Exception e){
  		add(e);
  	}
  }

  public DropdownMenu getFormatDropdown(IWContext iwc){
    ICPage page;
    String pageFormat = "IBXML";
	try {
		page = BuilderLogic.getInstance().getCurrentIBPageEntity(iwc);
		pageFormat = page.getFormat();
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
    DropdownMenu menu = new DropdownMenu(IB_PAGE_FORMAT);
    menu.addMenuElement("IBXML");
    menu.addMenuElement("HTML");
    menu.setSelectedElement(pageFormat);
    return menu;
  }
  
}
