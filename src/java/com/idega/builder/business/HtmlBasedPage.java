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
import java.io.OutputStream;

import com.idega.exception.PageDoesNotExist;
import com.idega.presentation.HtmlPage;
import com.idega.presentation.Page;

/**
 * @author tryggvil
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class HtmlBasedPage extends IBXMLPage {

	//private String htmlSource;
	
	/**
	 * @param verify
	 * @param key
	 */
	public HtmlBasedPage() {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see com.idega.builder.business.IBXMLPage#readPageStream(java.io.InputStream)
	 */
	protected void readPageStream(InputStream stream) throws PageDoesNotExist {
		//HtmlPage hPage = new HtmlPage();
		//hPage.setResource(stream);
		InputStreamReader reader = new InputStreamReader(stream);
		int bufferlength=1000;
		char[] buf = new char[bufferlength];
		StringBuffer sbuffer = new StringBuffer();
		try {
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
	public void setSourceFromString(String htmlRepresentation) throws Exception {
		//htmlSource = htmlRepresentation;
		//super.setSourceFromString(htmlRepresentation);
		stringSourceXML=htmlRepresentation;
	}
	/* (non-Javadoc)
	 * @see com.idega.builder.business.IBXMLPage#getPopulatedPage()
	 */
	public Page getPopulatedPage() {
		if(this._populatedPage==null){
			HtmlPage hPage = new HtmlPage();
			hPage.setHtml(this.getSourceAsString());
			this.setPopulatedPage(hPage);
		}
		return _populatedPage;
	}
}
