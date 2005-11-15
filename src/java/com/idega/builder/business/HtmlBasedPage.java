/*
 * $Id: HtmlBasedPage.java,v 1.12 2005/11/15 16:56:33 eiki Exp $
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
import com.idega.exception.PageDoesNotExist;
import com.idega.presentation.HtmlPage;
import com.idega.presentation.IWContext;
import com.idega.presentation.Page;

/**
 * This class is handles a Builder Page of format HTML.
 * This class is responsible for reading the HTML page stream but the parsing of the 
 * Html code and Region tags is handled by the class com.idega.presentation.HtmlPage.
 * 
 *  Last modified: $Date: 2005/11/15 16:56:33 $ by $Author: eiki $
 * 
 * @author <a href="mailto:tryggvil@idega.com">Tryggvi Larusson</a>
 * @version $Revision: 1.12 $
 */
public class HtmlBasedPage extends CachedBuilderPage implements ComponentBasedPage{

	private Page _populatedPage;
	/**
	 * @param verify
	 * @param key
	 */
	public HtmlBasedPage(String pageKey) {
		super(pageKey);
		setComponentBased(true);
	}

	/* (non-Javadoc)
	 * @see com.idega.builder.business.IBXMLPage#readPageStream(java.io.InputStream)
	 */
	protected void readPageStream(InputStream stream) throws PageDoesNotExist {
		//HtmlPage hPage = new HtmlPage();
		//hPage.setResource(stream);

		try {
			
			InputStreamReader reader = new InputStreamReader(stream,"UTF-8");//,encoding);
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/* (non-Javadoc)
	 * @see com.idega.builder.business.IBXMLAble#setSourceFromString(java.lang.String)
	 */

	/* (non-Javadoc)
	 * @see com.idega.builder.business.IBXMLPage#getPopulatedPage()
	 */
	public Page getPopulatedPage() {
		if(this._populatedPage==null){
			HtmlPage hPage = new HtmlPage();
			hPage.setHtml(this.getSourceAsString());
			try {
				ICPage icpage = this.getICPage();
				_populatedPage.setPageID(((Integer)icpage.getPrimaryKey()).intValue());
			}
			catch (EJBException e) {
				e.printStackTrace();
			}
			setPopulatedPage(hPage);
		}
		return _populatedPage;
	}
	
	public void setPopulatedPage(Page page){
		this._populatedPage=page;
	}

	public Page getNewPageCloned(){
		return (Page) this.getPopulatedPage().clone();
	}
	
	/**
	 * Gets a new Page instanfce without any Builder checks. (not transformed for Builder Edit view)
	 * @param iwc
	 * @return
	 */
	public Page getNewPage(IWContext iwc){
		return (Page) this.getPopulatedPage().clonePermissionChecked(iwc);
	}
	
	public Page getPage(IWContext iwc){
		return getNewPage(iwc);
	}
	
	
	public UIComponent createComponent(FacesContext context){
		IWContext iwc = IWContext.getIWContext(context);
		return getPage(iwc);
	}
	
	public void initializeEmptyPage(){
		try {
			setSourceFromString("<html>\n<head>\n</head>\n<body>\n</body>\n</html>");
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
