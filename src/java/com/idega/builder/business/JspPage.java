/*
 * $Id: JspPage.java,v 1.5.2.1 2007/01/12 19:31:48 idegaweb Exp $
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


/**
 * <p>
 * This is an implementation for a a "JSP" based Builder page that is rendered through JSF.<br/>
 * This means that the page is based on a JSP page and the rendering is dispatched to the 
 * Servlet/JSP container (e.g. Tomcat) for processing the rendering.
 * </p>
 *  Last modified: $Date: 2007/01/12 19:31:48 $ by $Author: idegaweb $
 * 
 * @author <a href="mailto:tryggvil@idega.com">Tryggvi Larusson</a>
 * @version $Revision: 1.5.2.1 $
 */
public class JspPage extends CachedBuilderPage {
	
	public String getURI() {
		/*String parentUri = getParent().getURI();
		String pageUri = getPageUri();
		
		String newUri = parentUri+pageUri;
		return newUri;*/
		return super.getURI();
		
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
			return "builderpage_"+getPageKey()+".jsp";
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
				
				log.finer("Streaming builder page with uri: "+getURI()+" to disk in file: "+jspFile.toURL().toString());
				
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
			return this.isLoadedToDisk;
		}
		/**
		 * @param isLoadedToDisk The isLoadedToDisk to set.
		 */
		public void setLoadedToDisk(boolean isLoadedToDisk) {
			this.isLoadedToDisk = isLoadedToDisk;
		}
		
		public String getResourceURI(){
			if(this.resourceURI==null){
				this.resourceURI="/"+getJspFilesFolderName()+"/"+getJSPFileName();
			}
			return this.resourceURI;
		}
		
		public void initializeEmptyPage(){
			
			String templateKey = this.getTemplateKey();
			String templateReference = "";
			if(templateKey!=null){
				if (!templateKey.equals("-1")){
					templateReference="template=\""+templateKey+"\"";
				}
			}
			String source = "<?xml version=\"1.0\"?>\n<jsp:root xmlns:jsp=\"http://java.sun.com/JSP/Page\"\nxmlns:h=\"http://java.sun.com/jsf/html\"\nxmlns:jsf=\"http://java.sun.com/jsf/core\"\nxmlns:builder=\"http://xmlns.idega.com/com.idega.builder\"\n version=\"1.2\">\n<jsp:directive.page contentType=\"text/html;charset=UTF-8\" pageEncoding=\"UTF-8\"/>\n<jsf:view>\n<builder:page id=\"builderpage_"+getPageKey()+"\" "+templateReference+">\n</builder:page>\n</jsf:view>\n</jsp:root>";
			try {
				setSourceFromString(source);
			}
			catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
}
