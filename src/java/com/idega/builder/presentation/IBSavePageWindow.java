/*

 * $Id: IBSavePageWindow.java,v 1.13 2007/05/24 11:31:12 valdas Exp $

 *

 * Copyright (C) 2001 Idega hf. All Rights Reserved.

 *

 * This software is the proprietary information of Idega hf.

 * Use is subject to license terms.

 *

 */

package com.idega.builder.presentation;



import com.idega.builder.business.BuilderLogic;
import com.idega.builder.business.IBPropertyHandler;
import com.idega.core.builder.data.ICPage;
import com.idega.core.builder.data.ICPageBMPBean;
import com.idega.core.file.data.ICFile;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.idegaweb.presentation.IWAdminWindow;
import com.idega.presentation.IWContext;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.Table;
import com.idega.presentation.text.Text;
import com.idega.presentation.ui.DropdownMenu;
import com.idega.presentation.ui.Form;
import com.idega.presentation.ui.SubmitButton;
import com.idega.presentation.ui.TextInput;



/**

 * @author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>

 * @modified by <a href=teiki@idega.is">Eirikur Hrafnsson</a>

 * @version 1.0 alpha

*/



public class IBSavePageWindow extends IWAdminWindow {

  private static final String PAGE_NAME_PARAMETER   = "ib_page_name";

  private static final String PAGE_CHOOSER_NAME     = IBPropertyHandler.PAGE_CHOOSER_NAME;

  private static final String TEMPLATE_CHOOSER_NAME = IBPropertyHandler.TEMPLATE_CHOOSER_NAME;

  private static final String PAGE_TYPE             = "ib_page_type";

  public void main(IWContext iwc) throws Exception {

    IWResourceBundle iwrb = getBundle(iwc).getResourceBundle(iwc);

    Form form = new Form();



    setTitle(iwrb.getLocalizedString("create_new_page","Create a new page"));

    add(form);

    Table tab = new Table(2,5);

    form.add(tab);

    TextInput inputName = new TextInput(PAGE_NAME_PARAMETER);

    tab.add(iwrb.getLocalizedString("page_name","Page name"),1,1);

    tab.add(inputName,2,1);



    DropdownMenu mnu = new DropdownMenu(PAGE_TYPE);

    mnu.addMenuElement("1","Page");

    mnu.addMenuElement("2","Template");



    tab.add(new Text("Select page type : "),1,2);

    tab.add(mnu,2,2);



    mnu.setToSubmit();



    String type = iwc.getParameter(PAGE_TYPE);



    tab.add(iwrb.getLocalizedString("parent_page","Create page under:"),1,3);

    if (type != null) {

      if (type.equals("2")) {
				tab.add(getTemplateChooser(PAGE_CHOOSER_NAME),2,3);
			}
			else {
				tab.add(getPageChooser(PAGE_CHOOSER_NAME),2,3);
			}

    }
		else {
			tab.add(getPageChooser(PAGE_CHOOSER_NAME),2,3);
		}



    tab.add(iwrb.getLocalizedString("using_template","Using template:"),1,4);

    tab.add(getTemplateChooser(TEMPLATE_CHOOSER_NAME),2,4);



    SubmitButton button = new SubmitButton("subbi",iwrb.getLocalizedString("save","Save"));

    tab.add(button,2,5);



    String submit = iwc.getParameter("subbi");



    if (submit != null) {

      String pageId = iwc.getParameter(PAGE_CHOOSER_NAME);

      String name = iwc.getParameter(PAGE_NAME_PARAMETER);

      type = iwc.getParameter(PAGE_TYPE);

      String templateId = iwc.getParameter(TEMPLATE_CHOOSER_NAME);



      if (pageId != null) {

        ICPage ibPage = ((com.idega.core.builder.data.ICPageHome)com.idega.data.IDOLookup.getHomeLegacy(ICPage.class)).createLegacy();

        if (name == null) {
					name = "Untitled";
				}

        ibPage.setName(name);

        ICFile file = ((com.idega.core.file.data.ICFileHome)com.idega.data.IDOLookup.getHome(ICFile.class)).create();

        ibPage.setFile(file);



        if (type.equals("1")) {
					ibPage.setType(ICPageBMPBean.PAGE);
				}
				else if (type.equals("2")) {
					ibPage.setType(ICPageBMPBean.TEMPLATE);
				}
				else {
					ibPage.setType(ICPageBMPBean.PAGE);
				}



        int tid = -1;

        try {

          tid = Integer.parseInt(templateId);

          ibPage.setTemplateId(tid);

        }

        catch(java.lang.NumberFormatException e) {

        }



        ibPage.store();

        ICPage ibPageParent = ((com.idega.core.builder.data.ICPageHome)com.idega.data.IDOLookup.getHomeLegacy(ICPage.class)).findByPrimaryKeyLegacy(Integer.parseInt(pageId));

        ibPageParent.addChild(ibPage);



        BuilderLogic.getInstance().setCurrentIBPage(iwc,ibPage.getPrimaryKey().toString());

        setParentToReload();

        close();

      }

    }

    else {

      String name = iwc.getParameter(PAGE_NAME_PARAMETER);

      type = iwc.getParameter(PAGE_TYPE);

      if (name != null) {
				inputName.setValue(name);
			}



      if (type != null) {
				mnu.setSelectedElement(type);
			}





/*      System.out.println("id = " + templateId);

      System.out.println("name = " + templateName);*/



      java.util.Enumeration e = iwc.getParameterNames();



      while (e.hasMoreElements()) {

        String param = (String)e.nextElement();

        String value = iwc.getParameter(param);

        System.out.println(param + " = " + value);

      }



    }

  }



  /*

   *

   */

  private PresentationObject getPageChooser(String name){

    return new IBPageChooser(name, true, null, null);

  }



  /*

   *

   */

  private PresentationObject getTemplateChooser(String name){

    return new IBTemplateChooser(name, true, null, null);

  }

}



