/*

 * $Id: IBDeletePageWindow.java,v 1.16 2003/04/03 19:54:57 laddi Exp $

 *

 * Copyright (C) 2001 Idega hf. All Rights Reserved.

 *

 * This software is the proprietary information of Idega hf.

 * Use is subject to license terms.

 *

 */
package com.idega.builder.presentation;
import com.idega.builder.business.BuilderLogic;
import com.idega.builder.business.IBPageHelper;
import com.idega.builder.business.PageTreeNode;
import com.idega.idegaweb.IWConstants;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.idegaweb.presentation.IWAdminWindow;
import com.idega.presentation.IWContext;
import com.idega.presentation.Table;
import com.idega.presentation.text.Text;
import com.idega.presentation.ui.CheckBox;
import com.idega.presentation.ui.Form;
import com.idega.presentation.ui.SubmitButton;
/**

 * @author <a href="mailto:palli@idega.is">Pall Helgason</a>

 * @version 1.0

 */
public class IBDeletePageWindow extends IWAdminWindow
{
	private static final String PAGE_NAME_PARAMETER = "ib_page_name";
	private static final String PAGE_TYPE = "ib_page_type";
	private static final String IW_BUNDLE_IDENTIFIER = "com.idega.builder";
	public IBDeletePageWindow()
	{
		setWidth(240);
		setHeight(140);
		setScrollbar(false);
	}
	public void main(IWContext iwc) throws Exception
	{
		boolean okToDelete = false;
		boolean okToDeleteChildren = true;
		IWResourceBundle iwrb = iwc.getApplication().getBundle(BuilderLogic.IW_BUNDLE_IDENTIFIER).getResourceBundle(iwc);
		Form form = new Form();
		setTitle(iwrb.getLocalizedString("delete_page", "Delete page"));
		addTitle(iwrb.getLocalizedString("delete_page", "Delete page"), IWConstants.BUILDER_FONT_STYLE_TITLE);
		add(form);
		BuilderLogic instance = BuilderLogic.getInstance();
		String pageId = instance.getCurrentIBPage(iwc);
		boolean submit = iwc.isParameterSet("ok");
		boolean quit = iwc.isParameterSet("cancel");
		String deleteAll = iwc.getParameter("deletechildren");
		if (submit)
		{
			if ((deleteAll != null) && (deleteAll.equals("true")))
				IBPageHelper.getInstance().deletePage(pageId, true, PageTreeNode.getTree(iwc), iwc.getUserId());
			else
				IBPageHelper.getInstance().deletePage(pageId, false, PageTreeNode.getTree(iwc), iwc.getUserId());
			iwc.setSessionAttribute("ib_page_id", Integer.toString(BuilderLogic.getInstance().getCurrentDomain(iwc).getStartPageID()));
			/**@todo is this in the right place? -eiki**/
			//      setOnLoad("window.opener.parent.parent.frames['"+com.idega.builder.app.IBApplication.IB_LEFT_MENU_FRAME+"'].location.reload()");
			setOnUnLoad("window.opener.parent.parent.location.reload()");
			//      setParentToReload();
			close();
		}
		else if (quit)
		{
			close();
		}
		okToDelete = IBPageHelper.getInstance().checkDeletePage(pageId);
		if (okToDelete)
		{
			okToDeleteChildren = IBPageHelper.getInstance().checkDeleteChildrenOfPage(pageId);
			SubmitButton ok = new SubmitButton(iwrb.getLocalizedImageButton("yes", "Yes"), "ok");
			SubmitButton cancel = new SubmitButton(iwrb.getLocalizedImageButton("cancel", "Cancel"), "cancel");
			CheckBox deleteChildren = new CheckBox("deletechildren", "true");
			deleteChildren.setChecked(false);
			Text deleteChildrenText = new Text(iwrb.getLocalizedString("childrentext", "Delete children of page"));
			deleteChildrenText.setFontStyle(IWConstants.BUILDER_FONT_STYLE_SMALL);
			Text sureText = new Text(iwrb.getLocalizedString("suredelete", "Are you sure you want to delete this page"));
			sureText.setFontStyle(IWConstants.BUILDER_FONT_STYLE_LARGE);
			Table table = new Table(1, 3);
			table.setCellpadding(6);
			table.setAlignment(1, 3, "right");
			table.add(sureText, 1, 1);
			table.add(cancel, 1, 3);
			table.add(Text.getNonBrakingSpace(), 1, 3);
			table.add(ok, 1, 3);
			if (!okToDeleteChildren)
			{
				deleteChildren.setValue("false");
				deleteChildren.setDisabled(true);
			}
			else
			{
				deleteChildren.setValue("true");
				deleteChildren.setDisabled(false);
			}
			table.add(deleteChildren, 1, 2);
			table.add(deleteChildrenText, 1, 2);
			form.add(table);
		}
		else
		{
			SubmitButton cancel = new SubmitButton("cancel", iwrb.getLocalizedString("cancel", "Cancel"));
			Text notAllowed = new Text(iwrb.getLocalizedString("not_allowed", "There are pages using this template so you can not delete it"));
			notAllowed.setFontStyle(IWConstants.BUILDER_FONT_STYLE_LARGE);
			Table table = new Table(1, 2);
			table.setCellpadding(6);
			table.add(notAllowed, 1, 1);
			table.add(cancel, 1, 2);
			form.add(table);
		}
	}
	public String getBundleIdentifier()
	{
		return IW_BUNDLE_IDENTIFIER;
	}
}
