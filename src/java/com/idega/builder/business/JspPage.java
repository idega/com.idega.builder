/*
 * $Id: JspPage.java,v 1.3 2005/03/04 18:17:26 tryggvil Exp $
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
 *  The instance of this class wrapps a Builder page of format JSP.<br>
 * 
 *  Last modified: $Date: 2005/03/04 18:17:26 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:tryggvil@idega.com">Tryggvi Larusson</a>
 * @version $Revision: 1.3 $
 */
public class JspPage extends CachedBuilderPage {
	
	public String getURI() {
		String parentUri = getParent().getURI();
		String pageUri = getPageUri();
		
		String newUri = parentUri+pageUri;
		return newUri;
		
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
		
}
