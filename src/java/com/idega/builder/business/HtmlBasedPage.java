/*
 * Created on 1.6.2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.idega.builder.business;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import com.idega.exception.PageDoesNotExist;
import com.idega.presentation.HtmlPage;
import com.idega.presentation.IWContext;
import com.idega.presentation.Page;

/**
 * @author tryggvil
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class HtmlBasedPage extends CachedBuilderPage implements ComponentBasedPage{

	private Page _populatedPage;
	/**
	 * @param verify
	 * @param key
	 */
	public HtmlBasedPage(String pageKey) {
		super(pageKey);
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
	
}
