/*

 * $Id: IBDeletePageWindow.java,v 1.13 2002/04/06 19:07:39 tryggvil Exp $

 *

 * Copyright (C) 2001 Idega hf. All Rights Reserved.

 *

 * This software is the proprietary information of Idega hf.

 * Use is subject to license terms.

 *

 */
package com.idega.builder.presentation;
import com.idega.idegaweb.IWConstants;
import com.idega.presentation.IWContext;
import com.idega.presentation.Table;
import com.idega.presentation.text.Text;
import com.idega.presentation.ui.Form;
import com.idega.presentation.ui.SubmitButton;
import com.idega.presentation.ui.CheckBox;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.idegaweb.presentation.IWAdminWindow;
import com.idega.builder.business.BuilderLogic;
import com.idega.builder.business.IBXMLPage;
import com.idega.builder.business.PageTreeNode;
import com.idega.builder.business.IBPageHelper;
import com.idega.builder.data.IBPage;
import com.idega.presentation.Page;
import java.util.List;
import java.util.Iterator;
import java.util.Map;
/**

 * @author <a href="mailto:palli@idega.is">Pall Helgason</a>

 * @version 1.0

 */
public class IBMovePageWindow extends IBPageWindow
{
	private static final String PARAM_NEW_PARENT_PAGE_ID = PAGE_CHOOSER_NAME;
	private static final String PAGE_TYPE = "ib_page_type";
	private static final String IW_BUNDLE_IDENTIFIER = "com.idega.builder";
	public IBMovePageWindow()
	{
		setWidth(240);
		setHeight(140);
		setScrollbar(false);
	}
	public void main(IWContext iwc) throws Exception
	{
		boolean okToMove = false;
		boolean submit = false;
		IWResourceBundle iwrb = getResourceBundle(iwc);
		int userId = iwc.getUserId();
		Form form = new Form();
		setTitle(iwrb.getLocalizedString("move_page", "Move page"));
		addTitle(iwrb.getLocalizedString("move_page", "Move page"), IWConstants.BUILDER_FONT_STYLE_TITLE);
		add(form);
		BuilderLogic instance = BuilderLogic.getInstance();
		String sPageId = instance.getCurrentIBPage(iwc);
		int iPageId = Integer.parseInt(sPageId);
		String sNewParentPageId = iwc.getParameter(PARAM_NEW_PARENT_PAGE_ID);
		int iNewParentPageId = -1;
		if (iwc.isParameterSet("ok"))
		{
			try
			{
				iNewParentPageId = Integer.parseInt(sNewParentPageId);
				submit = true;
			}
			catch (NumberFormatException nf)
			{
			}
		}
		boolean quit = iwc.isParameterSet("cancel");
		if (submit)
		{
			boolean updated = false;
			//if ((deleteAll != null) && (deleteAll.equals("true")))
			//	deleted = IBPageHelper.getInstance().deletePage(pageId, true, PageTreeNode.getTree(iwc), iwc.getUserId());
			//else
			updated = IBPageHelper.getInstance().movePage(iPageId, iNewParentPageId, PageTreeNode.getTree(iwc), userId);
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
		else
		{
			okToMove = IBPageHelper.getInstance().checkIfMayMovePage(iPageId, userId);
			if (okToMove)
			{
				SubmitButton ok = new SubmitButton(iwrb.getLocalizedImageButton("ok", "OK"), "ok");
				SubmitButton cancel = new SubmitButton(iwrb.getLocalizedImageButton("cancel", "Cancel"), "cancel");
				CheckBox deleteChildren = new CheckBox("deletechildren", "true");
				Text moveText = new Text(iwrb.getLocalizedString("move_page_text", "Select page to move this page to under") + ":");
				moveText.setFontStyle(IWConstants.BUILDER_FONT_STYLE_LARGE);
				Table table = new Table(1, 3);
				table.setCellpadding(6);
				table.setAlignment(1, 3, "right");
				table.add(moveText, 1, 1);
				table.add(cancel, 1, 3);
				table.add(Text.getNonBrakingSpace(), 1, 3);
				table.add(ok, 1, 3);
				IBPageChooser pageChooser = getPageChooser(PAGE_CHOOSER_NAME, iwc);
				table.add(pageChooser, 1, 1);
				form.add(table);
			}
			else
			{
				SubmitButton cancel = new SubmitButton("cancel", iwrb.getLocalizedString("cancel", "Cancel"));
				Text notAllowed =
					new Text(iwrb.getLocalizedString("move_not_allowed", "Move of a page of this type is not supported"));
				notAllowed.setFontStyle(IWConstants.BUILDER_FONT_STYLE_LARGE);
				Table table = new Table(1, 2);
				table.setCellpadding(6);
				table.add(notAllowed, 1, 1);
				table.add(cancel, 1, 2);
				form.add(table);
			}
		}
	}
}
