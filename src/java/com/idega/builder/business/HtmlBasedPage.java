/*
 * $Id: HtmlBasedPage.java,v 1.17 2009/01/14 15:07:19 tryggvil Exp $
 * Created on Created on 1.6.2004 by Tryggvi Larusson
 *
 * Copyright (C) 2001-2004 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package com.idega.builder.business;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.ejb.EJBException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import com.idega.core.builder.data.ICPage;
import com.idega.core.view.ViewNodeBase;
import com.idega.exception.PageDoesNotExist;
import com.idega.presentation.HtmlPage;
import com.idega.presentation.IWContext;
import com.idega.presentation.Page;
import com.idega.util.CoreConstants;

/**
 * This class is handles a Builder Page of format HTML.
 * This class is responsible for reading the HTML page stream but the parsing of the
 * HTML code and Region tags is handled by the class com.idega.presentation.HtmlPage.
 *
 *  Last modified: $Date: 2009/01/14 15:07:19 $ by $Author: tryggvil $
 *
 * @author <a href="mailto:tryggvil@idega.com">Tryggvi Larusson</a>
 * @version $Revision: 1.17 $
 */
public class HtmlBasedPage extends CachedBuilderPage implements ComponentBasedPage{

	private static final long serialVersionUID = -4838223049441803865L;

	private Page _populatedPage;

	/**
	 * @param verify
	 * @param key
	 */
	public HtmlBasedPage(String pageKey) {
		super(pageKey);
		setViewNodeBase(ViewNodeBase.COMPONENT);
	}

	@Override
	protected void readPageStream(InputStream stream) throws PageDoesNotExist {
		try {
			InputStreamReader reader = new InputStreamReader(stream, CoreConstants.ENCODING_UTF8);
			int bufferlength=1000;
			char[] buf = new char[bufferlength];
			StringBuffer sbuffer = new StringBuffer();
			int read = reader.read(buf);
			while(read!=-1){
				sbuffer.append(buf,0,read);
				read = reader.read(buf);
			}
			String html = sbuffer.toString();
			this.setSourceFromString(html);

			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Page getPopulatedPage() {
		if (this._populatedPage == null) {
			HtmlPage hPage = new HtmlPage();
			populatePage(hPage);
			setPopulatedPage(hPage);
		}
		return this._populatedPage;
	}

	public void populatePage(HtmlPage hPage) {
		hPage.setHtml(this.getSourceAsString());
		try {
			ICPage icpage = this.getICPage();
			hPage.setPageID(Integer.valueOf(icpage.getId()));
		} catch (EJBException e) {
			e.printStackTrace();
		}
	}

	public void setPopulatedPage(Page page){
		this._populatedPage=page;
	}

	@Override
	public Page getNewPageCloned(){
		return (Page) this.getPopulatedPage().clone();
	}

	/**
	 * Gets a new Page instance without any Builder checks. (not transformed for Builder Edit view)
	 * @param iwc
	 * @return
	 */
	@Override
	public Page getNewPage(IWContext iwc){
		return (Page) this.getPopulatedPage().clonePermissionChecked(iwc);
	}

	@Override
	public Page getPage(IWContext iwc){
		return getNewPage(iwc);
	}

	@Override
	public UIComponent createComponent(FacesContext context){
		IWContext iwc = IWContext.getIWContext(context);
		return getPage(iwc);
	}

	@Override
	public void initializeEmptyPage(){
		try {
			setSourceFromString("<html>\n<head>\n</head>\n<body>\n</body>\n</html>");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}