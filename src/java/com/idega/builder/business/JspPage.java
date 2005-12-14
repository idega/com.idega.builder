/*
 * $Id: JspPage.java,v 1.9 2005/12/14 00:45:22 tryggvil Exp $
 * Created on 17.12.2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.builder.business;

import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.logging.Logger;
import com.idega.idegaweb.IWMainApplication;
import com.idega.util.StringHandler;


/**
 * <p>
 * This is an implementation for a a "JSP" based Builder page that is rendered through JSF.<br/>
 * This means that the page is based on a JSP page and the rendering is dispatched to the 
 * Servlet/JSP container (e.g. Tomcat) for processing the rendering.
 * </p>
 *  Last modified: $Date: 2005/12/14 00:45:22 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:tryggvil@idega.com">Tryggvi Larusson</a>
 * @version $Revision: 1.9 $
 */
public class JspPage extends CachedBuilderPage {
	
	private static final String BUILDERPAGE_PREFIX = "builderpage_";
	private static final int BUILDERPAGE_PREFIX_LENGTH = BUILDERPAGE_PREFIX.length();
	private static final String JSP_PAGE_EXTENSION_WITH_DOT = ".jsp";
	private static final int JSP_PAGE_EXTENSION_WITH_DOT_LENGTH = JSP_PAGE_EXTENSION_WITH_DOT.length();
	
	/**
	 * Returns page key if the view id represents a JSPPage else null. 
	 * 
	 * @param viewId
	 * @return
	 */
	public static String getPageKey(String viewId) {
		// we are looking for something like "/jsps/builderpage_12.jsp"
		// quick check at the beginning
		if (! viewId.endsWith(JSP_PAGE_EXTENSION_WITH_DOT)) {
			// no jsp page at all
			return null;
		}
		int startIndex = viewId.lastIndexOf(BUILDERPAGE_PREFIX);
		if (startIndex < 0) {
			// jsp page but not a builder page
			return null;
		}
		startIndex += BUILDERPAGE_PREFIX_LENGTH;
		int endIndex = viewId.length() - JSP_PAGE_EXTENSION_WITH_DOT_LENGTH;
		String key = viewId.substring(startIndex, endIndex);
		if (StringHandler.isNaturalNumber(key)) {
			return key;
		}
		return null;
	}
	
	public String getURIWithContextPath() {
		/*String parentUri = getParent().getURI();
		String pageUri = getPageUri();
		
		String newUri = parentUri+pageUri;
		return newUri;*/
		return super.getURIWithContextPath();
		
	}
	private static Logger log = Logger.getLogger(JspPage.class.getName());
	
		private boolean isLoadedToDisk=false;
		private static File jspTmpDir;
		private String resourceURI;
	
		public JspPage(String pageId){
			super(pageId);
			super.setResourceBased(true);
		}
		
		protected void readPageStream(InputStream pageStream){
			File jspTmpDirectory = getJSPTmpDirectory();
			File jspFile = new File(jspTmpDirectory,getJSPFileName());
			streamToFile(pageStream,jspFile);
			setLoadedToDisk(true);
		}
		
		private String getJSPFileName(){
			StringBuffer buffer = new StringBuffer(BUILDERPAGE_PREFIX);
			buffer.append(getPageKey()).append(JSP_PAGE_EXTENSION_WITH_DOT);
			return buffer.toString();
		}
		
		private String getJspFilesFolderName(){
			return "jsps";
		}

		private File getJSPTmpDirectory(){
			if(jspTmpDir==null){
				IWMainApplication iwma = this.getIWMainApplication();
				String appRealPath = iwma.getApplicationRealPath();
				File appRealDir = new File(appRealPath);
				jspTmpDir = new File(appRealDir,getJspFilesFolderName());
				if(!jspTmpDir.exists()){
					jspTmpDir.mkdir();
				}
			}
			return jspTmpDir;
		}
		
		private void streamToFile(InputStream pageStream,File jspFile){
			try {
				if(!jspFile.exists()){
					jspFile.createNewFile();
				}
				//OutputStream jspStream = new FileOutputStream(jspFile);
				/*
				log.info("Writing to JSP page: "+jspFile.toString());
				
				//ICPage icPage = getICPage();
				//InputStream pageStream = icPage.getPageValue();
				
				int bufferLen=1000;
				byte[] buffer = new byte[bufferLen]; 
				int read = pageStream.read(buffer);
				while(read!=-1){
					jspStream.write(buffer);
					read = pageStream.read(buffer);
				}
				pageStream.close();
				jspStream.close();*/
				
				log.finer("Streaming builder page with uri: "+getURIWithContextPath()+" to disk in file: "+jspFile.toURL().toString());
				
				InputStreamReader reader = new InputStreamReader(pageStream,"UTF-8");//,encoding);
				int bufferlength=1000;
				char[] buf = new char[bufferlength];
				StringBuffer sbuffer = new StringBuffer();			
				int read = reader.read(buf);
				while(read!=-1){
					sbuffer.append(buf,0,read);
					read = reader.read(buf);
				}
				String xml = sbuffer.toString();
				this.setSourceFromString(xml);
				
				reader.close();
				FileWriter fw = new FileWriter(jspFile);
				PrintWriter out = new PrintWriter(fw);
				out.write(xml);
				out.close();				
				fw.close();
				//jspStream.write();
				
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		/**
		 * @return Returns the isLoadedToDisk.
		 */
		public boolean isLoadedToDisk() {
			return isLoadedToDisk;
		}
		/**
		 * @param isLoadedToDisk The isLoadedToDisk to set.
		 */
		public void setLoadedToDisk(boolean isLoadedToDisk) {
			this.isLoadedToDisk = isLoadedToDisk;
		}
		
		public String getResourceURI(){
			if(resourceURI==null){
				resourceURI="/"+getJspFilesFolderName()+"/"+getJSPFileName();
			}
			return resourceURI;
		}
		
		public void initializeEmptyPage(){
			
			String templateKey = this.getTemplateKey();
			String templateReference = "";
			if(templateKey!=null){
				if (!templateKey.equals("-1")){
					templateReference="template=\""+templateKey+"\"";
				}
			}
			String source = "<?xml version=\"1.0\"?>\n<jsp:root xmlns:jsp=\"http://java.sun.com/JSP/Page\"\nxmlns:h=\"http://java.sun.com/jsf/html\"\nxmlns:jsf=\"http://java.sun.com/jsf/core\"\nxmlns:builder=\"http://xmlns.idega.com/com.idega.builder\"\n version=\"1.2\">\n<jsp:directive.page contentType=\"text/html\" pageEncoding=\"UTF-8\"/>\n<jsf:view>\n<builder:page id=\"builderpage_"+getPageKey()+"\" "+templateReference+">\n</builder:page>\n</jsf:view>\n</jsp:root>";
			try {
				setSourceFromString(source);
			}
			catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
}
