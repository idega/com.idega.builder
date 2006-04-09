/*
 * $Id: HtmlTemplateGrabber.java,v 1.2 2006/04/09 11:43:34 laddi Exp $ Created on
 * 24.2.2005
 * 
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 * 
 * This software is the proprietary information of Idega hf. Use is subject to
 * license terms.
 */
package com.idega.builder.business;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.URL;
import java.net.URLConnection;
import javax.ejb.FinderException;
import com.idega.core.builder.data.ICPage;
import com.idega.util.HtmlReferenceRewriter;

/**
 * <p>
 * Class that "grabs" a temlpate from a URL, parses it (re-writes all relative hrefs) and updates a BuilderPage
 * to include the parsed html code.
 * </p>
 * Last modified: $Date: 2006/04/09 11:43:34 $ by $Author: laddi $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil </a>
 * @version $Revision: 1.2 $
 */
public class HtmlTemplateGrabber {

	String sUrl;
	String pageKey;

	public HtmlTemplateGrabber(String url, String pageKey) throws NumberFormatException, IOException, FinderException {
		this.sUrl = url;
		this.pageKey = pageKey;
		process();
	}

	/**
	 * Executes the grab
	 * @throws IOException
	 * @throws NumberFormatException
	 * @throws FinderException
	 */
	protected void process() throws IOException, NumberFormatException, FinderException {
		// String sUrl = "http://nobel.idega.is/rvk/template.html";
		// String sUrl = "http://www.rvk.is/default.asp?cat_id=1197";
		URL url = new URL(this.sUrl);
		// InputStream iStream = url.openStream();
		URLConnection conn = url.openConnection();
		String encoding = conn.getContentEncoding();
		InputStream iStream = conn.getInputStream();
		if (encoding == null) {
			encoding = "ISO-8859-1";
		}
		InputStreamReader iReader = new InputStreamReader(iStream, encoding);
		System.out.println("Reading from url:+" + this.sUrl + " with content-encoding:" + iReader.getEncoding());
		HtmlReferenceRewriter instance = new HtmlReferenceRewriter();
		// String urlPrefix = "http://www.rvk.is/";
		String urlPrefix = url.getProtocol() + "://" + url.getHost() + "/";
		// String pageKey = "101";
		// ServletContext application = null;
		// IWApplicationContext iwac =
		// IWMainApplication.getIWMainApplication(application).getIWApplicationContext();
		// BuilderLogic.getInstance().getIBXMLPage(pageKey).
		ICPage ibpage = ((com.idega.core.builder.data.ICPageHome) com.idega.data.IDOLookup.getHome(ICPage.class)).findByPrimaryKey(new Integer(
				this.pageKey));
		ibpage.setFormat(BuilderLogic.getInstance().PAGE_FORMAT_HTML);
		OutputStream outStream = ibpage.getPageValueForWrite();
		Reader input = new BufferedReader(iReader);
		Writer output = new OutputStreamWriter(outStream, "UTF-8");
		instance.setInput(input);
		instance.setOutput(output);
		instance.setUrlPrefix(urlPrefix);
		instance.process();
		ibpage.store();
		BuilderLogic.getInstance().clearAllCachedPages();
	}
}
