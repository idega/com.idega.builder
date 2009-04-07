/*
 * $Id: CachedBuilderPage.java,v 1.21 2009/04/07 23:32:51 eiki Exp $
 *
 * Copyright (C) 2001-2004 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package com.idega.builder.business;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.io.StringReader;
import java.rmi.RemoteException;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import com.idega.business.IBOLookup;
import com.idega.core.builder.data.ICPage;
import com.idega.core.builder.data.ICPageHome;
import com.idega.core.view.DefaultViewNode;
import com.idega.core.view.ViewNode;
import com.idega.data.IDOLookup;
import com.idega.exception.PageDoesNotExist;
import com.idega.idegaweb.IWMainApplication;
import com.idega.idegaweb.IWService;
import com.idega.presentation.IWContext;
import com.idega.slide.business.IWSlideService;
import com.idega.slide.business.IWSlideSession;
import com.idega.util.CoreConstants;

/**
 * An abstract class that represents a cached instance of a Builder page.
 * Subclasses of this class handle pages of different formats such as IBXML,HTML and JSP.
 * 
 * @author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>, 
 * <a href="mailto:palli@idega.is">Pall Helgason</a>
 * 
 * @version 1.0
 */
public abstract class CachedBuilderPage extends DefaultViewNode implements ViewNode,Serializable {
	
	public final static String TYPE_PAGE = IBXMLConstants.PAGE_TYPE_PAGE;
	public final static String TYPE_TEMPLATE = IBXMLConstants.PAGE_TYPE_TEMPLATE;
	protected final static String TYPE_DRAFT = IBXMLConstants.PAGE_TYPE_DRAFT;
	protected final static String TYPE_DPT_TEMPLATE = IBXMLConstants.PAGE_TYPE_DPT_TEMPLATE;
	protected final static String TYPE_DPT_PAGE = IBXMLConstants.PAGE_TYPE_DPT_PAGE;
	
	private String type=TYPE_PAGE;
	private String _key;
	private String pageFormat;
	private String sourceAsString;
	private List pageKeysUsingThisTemplate;
	private String pageUri;
	private String templateKey;
	
	/**
	public IBXMLPage(){
		//Default constructor
	}
	private IBXMLPage(boolean verify) {
		setVerifyXML(verify);
		//_parser = new XMLParser(verify);
	}*/

	public CachedBuilderPage(String key) {
		super(key,BuilderLogic.getInstance().getBuilderPageRootViewNode());
		//this(verify);
		//setVerifyXML(verifyXML);
		setPageKey(key);
	}

	protected abstract void readPageStream(InputStream stream) throws PageDoesNotExist;
	/**
	 * Gets the key for the page that this instance represents.
	 * This is typically an id to a ICPage or ib_page.
	 */
	
	public void setSourceFromString(String xmlRepresentation) throws Exception {
		//htmlSource = htmlRepresentation;
		//super.setSourceFromString(htmlRepresentation);
		this.sourceAsString=xmlRepresentation;
	}
	
	public String getSourceAsString(){
		return this.sourceAsString;
	}
	
	public String getPageKey() {
		return this._key;
	}
	
	public void setPageKey(String key){
		this._key=key;
	}
	
	public String getType(){
		return this.type;
	}
	
	public void setType(String type){
		this.type=type;
	}
	
	/**
	 * @return Returns the pageFormat.
	 */
	public String getPageFormat() {
		return this.pageFormat;
	}
	/**
	 * @param pageFormat The pageFormat to set.
	 */
	public void setPageFormat(String pageFormat) {
		this.pageFormat = pageFormat;
	}
	
	
	public void addPageUsingThisTemplate(String id) {
		if (this.pageKeysUsingThisTemplate == null) {
			this.pageKeysUsingThisTemplate = new Vector();
		}

		if (!this.pageKeysUsingThisTemplate.contains(id)) {
			this.pageKeysUsingThisTemplate.add(id);
		}
	}

	public void removePageAsUsingThisTemplate(String id) {
		if (this.pageKeysUsingThisTemplate == null) {
			return;
		}

		if (this.pageKeysUsingThisTemplate.contains(id)) {
			this.pageKeysUsingThisTemplate.remove(id);
		}
	}

	public List getUsingTemplate() {
		if (this.pageKeysUsingThisTemplate == null) {
			findAllPagesUsingThisTemplate();
		}
		return this.pageKeysUsingThisTemplate;
	}

	private void findAllPagesUsingThisTemplate() {
		this.pageKeysUsingThisTemplate = new Vector();
		List l = IBPageFinder.getAllPagesExtendingTemplate(Integer.parseInt(getPageKey()));
		if (l == null) {
			return;
		}
		Iterator i = l.iterator();
		while (i.hasNext()) {
			ICPage p = (ICPage) i.next();
			addPageUsingThisTemplate(p.getPrimaryKey().toString());
		}
	}

	protected void invalidateAllPagesUsingThisTemplate() {
		List l = getUsingTemplate();
		if (l != null) {
			Iterator i = l.iterator();
			while (i.hasNext()) {
				String invalid = (String) i.next();
				CachedBuilderPage child = getPageCacher().getCachedBuilderPageIfInCache(invalid);
				if (child != null) {
					if (child.getType().equals(TYPE_TEMPLATE)) {
						child.invalidateAllPagesUsingThisTemplate();
					}
					getPageCacher().flagPageInvalid(invalid);
				}
			}
		}
	}
	
	
	protected BuilderLogic getBuilderLogic(){
		return BuilderLogic.getInstance();
	}
	
	protected PageCacher getPageCacher(){
		return getBuilderLogic().getPageCacher();
	}
	
	
	/** 
	 * This method is called from setPageKey to initialize the page document.
	 * @return
	 * @throws PageDoesNotExist
	 */
	protected void setICPage(ICPage ibpage){
		try{
			setPageFormat(ibpage.getFormat());
			InputStream stream = null;
			try {
				stream = getPageInputStream(ibpage);
			} catch (RuntimeException re) {
				re.printStackTrace();
			}
			if (stream == null) {
				return;
			}
			ensureReferencedPagesLoaded(ibpage);
			readPageStream(stream);

			if (ibpage.isPage()) {
				setType(TYPE_PAGE);
			}
			else if (ibpage.isDraft()) {
				setType(TYPE_DRAFT);
			}
			else if (ibpage.isTemplate()) {
				setType(TYPE_TEMPLATE);
			}
			else if (ibpage.isDynamicTriggeredTemplate()) {
				setType(TYPE_DPT_TEMPLATE);
			}
			else if (ibpage.isDynamicTriggeredPage()) {
				setType(TYPE_DPT_PAGE);
			}
			else {
				setType(TYPE_PAGE);
			}
			super.setName(ibpage.getName());
			this.templateKey=ibpage.getTemplateKey();
		}
		catch (PageDoesNotExist pe) {
			int template = ibpage.getTemplateId();
			String templateString = null;
			if (template != -1) {
				templateString = Integer.toString(template);
			}
			if (ibpage.isPage()) {
				setPageAsEmptyPage(TYPE_PAGE, templateString);
			}
			else if (ibpage.isDraft()) {
				setPageAsEmptyPage(TYPE_DRAFT, templateString);
			}
			else if (ibpage.isTemplate()) {
				setPageAsEmptyPage(TYPE_TEMPLATE, templateString);
			}
			else if (ibpage.isDynamicTriggeredTemplate()) {
				setPageAsEmptyPage(TYPE_DPT_TEMPLATE, templateString);
			}
			else if (ibpage.isDynamicTriggeredPage()) {
				setPageAsEmptyPage(TYPE_DPT_PAGE, templateString);
			}
			else {
				setPageAsEmptyPage(TYPE_PAGE, templateString);
			}
		}
	}
	
	protected void ensureReferencedPagesLoaded(ICPage ibpage) {
		//Ensuring that referenced templates are loaded before us
		String templateKey = ibpage.getTemplateKey();
		if(templateKey!=null){
			if(!templateKey.equals("-1")){
				CachedBuilderPage page = getPageCacher().getCachedBuilderPage(templateKey);
			}
		}
	}

	/**
	 * <p>
	 * Method for getting a reference to the inputstream for reading the page.
	 * </p>
	 * @param pageType
	 * @param templateString
	 */
	protected InputStream getPageInputStream(ICPage icPage){
		String webdavUri = icPage.getWebDavUri();
		if(webdavUri!=null){
			try {
				IWSlideService service = (IWSlideService) IBOLookup.getServiceInstance(IWMainApplication.getDefaultIWApplicationContext(),IWSlideService.class);
				InputStream stream = service.getInputStream(webdavUri);
				return stream;
			}
			catch (Exception e) {
				System.err.println("Error getting file: " + webdavUri);
				throw new RuntimeException(e);
			}
		}
		else{
			//older method, read it from ICFile
			return icPage.getPageValue();
		}
		//throw new RuntimeException("Page InputStream cannot be read");
	}
	
	/**
	 * <p>
	 * Method for getting a reference to the outputStream for storing the page.
	 * </p>
	 * @param pageType
	 * @param templateString
	 */
	protected OutputStream getPageOutputStream(ICPage icPage, IWSlideSession session){
		String webdavUri = icPage.getWebDavUri();
		if (webdavUri == null) {
			//	older method, read it from ICFile
			return icPage.getPageValueForWrite();
		}
		else{
			try {
				if (session == null) {
					session = (IWSlideSession) IBOLookup.getSessionInstance(IWContext.getInstance(), IWSlideSession.class);
				}
						
				String basePath = "/files/cms/pages";
				if(webdavUri.startsWith(basePath)){
					File baseDir = session.getFile(basePath);
					baseDir.mkdirs();
				}
				File file = session.getFile(webdavUri);
				if(!file.exists()){
					file.createNewFile();
				}
				
				OutputStream out = session.getOutputStream(webdavUri);
				return out;
			
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		throw new RuntimeException("Page OutputStream cannot be read");
	}
	
	public void setPageAsEmptyPage(String pageType,String templateString){
		//does nothing by default
	}
	
	protected ICPage getICPage(){
		try {
			ICPageHome icPageHome = (ICPageHome) IDOLookup.getHome(ICPage.class);
			return icPageHome.findByPrimaryKey(getPageKey());
		}
		catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	private boolean store(String webDavUri, IWSlideSession session) {
		if (webDavUri == null || session == null) {
			return false;
		}
		IWSlideService service = null;
		try {
			service = session.getIWSlideService();
		} catch (RemoteException e) {
			e.printStackTrace();
			return false;
		}
		String source = getSourceAsString();
		if (source == null) {
			return false;
		}

		String fileName = webDavUri;
		String[] pathParts = webDavUri.split(BuilderConstants.BASE_PAGE_PATH);
		if (pathParts != null) {
			if (pathParts.length == 2) {
				fileName = pathParts[1];
			}
		}
		boolean result = false;
		try {
			result = service.uploadFileAndCreateFoldersFromStringAsRoot(BuilderConstants.BASE_PAGE_PATH, fileName, source, "text/xml", true);
		} catch (RemoteException e) {
			e.printStackTrace();
			return false;
		}
		return result;
	}
	
	public synchronized boolean store() {
		return store(null);
	}
	
	public synchronized boolean store(IWSlideSession session) {
		try {
			ICPage icPage = getICPage();
			icPage.setFormat(this.getPageFormat());
			if (icPage.getWebDavUri() == null || session == null) {
				OutputStream stream = getPageOutputStream(icPage, session);
				storeStream(stream);
			}
			else {
				store(icPage.getWebDavUri(), session);
			}
			icPage.store();
		}
		catch (NumberFormatException ne) {
			/*try {
				OutputStream stream = new FileOutputStream(_key);
				store(stream);
			}
			catch (FileNotFoundException fnfe) {
				fnfe.printStackTrace();
			}*/
		}
		catch (Exception e) {
			e.printStackTrace(System.err);
		}
		//setPopulatedPage(XMLReader.getPopulatedPage(this));
		//setPopulatedPage(null);
		// invalidate the page
		getPageCacher().flagPageInvalid(getPageKey());
		if (getType().equals(TYPE_TEMPLATE)) {
			invalidateAllPagesUsingThisTemplate();
		}
		
		return true;
	}
	
	
	/**
	 * Writes this page to the given OutputStream stream.
	 * Called from the update method
	 * @param stream
	 */
	protected synchronized void storeStream(OutputStream stream) {
		try {
				//convert the string to utf-8
				//String theString = new String(this.toString().getBytes(),"ISO-8859-1");
				//String theString = new String(this.toString().getBytes(),CoreConstants.ENCODING_UTF8);
				String theString = this.toString();
				StringReader sr = new StringReader(theString);
				
				OutputStreamWriter out = new OutputStreamWriter(stream,CoreConstants.ENCODING_UTF8);
				
				
				int bufferlength=1000;
				char[] buf = new char[bufferlength];
				int read = sr.read(buf);
				while (read!=-1){
					out.write(buf,0,read);
					read = sr.read(buf);
				}
				sr.close();
				out.close();
				stream.close();
		}
		catch (IOException e) {
			e.printStackTrace(System.err);
		}
	}
	
	
	public String toString() {
		String s = getSourceAsString();
		if(s!=null){
			return s;
		}
		else{
			return super.toString();
		}
	}
	public String getName() {
		String sName = super.getName();
		if(sName==null){
			try {
				sName= getICPage().getName();
				super.setName(sName);
			}
			catch (Exception e) {
				return "";
			}
		}
		return sName;
	}

	public String getTemplateKey() {
		if(this.templateKey==null){
			try {
				this.templateKey= Integer.toString(getICPage().getTemplateId());
			}
			catch (Exception e) {
				return "-1";
			}
		}
		return this.templateKey;
	}

	public void setTemplateKey(String templateKey) {
		try {
			int id = Integer.parseInt(templateKey);
			ICPage page = getICPage();
			page.setTemplateId(id);
			page.store();
			this.templateKey=templateKey;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setName(String name) {
		try {
			ICPage page = getICPage();
			page.setName(name);
			page.store();
			super.setName(name);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public String getPageUri(){
		return this.pageUri;
	}
	
	public void setPageUri(String pageUri){
		this.pageUri=pageUri;
	}
	
	
	public String getURI(){
		
		ViewNode parent = getParent();
		String parentUri = parent.getURI();
		if(parentUri.endsWith(SLASH)){
			//strip the last slash off:
			parentUri = parentUri.substring(0,parentUri.length()-1);
		}
		String theReturn = parentUri+this.getPageUri();
		return theReturn;
	}
	
	public String getURIWithContextPath(){
		
		ViewNode parent = getParent();
		
		String parentUri = parent.getURIWithContextPath();
		if(parentUri.endsWith(SLASH)){
			//strip the last slash off:
			parentUri = parentUri.substring(0,parentUri.length()-1);
		}
		String theReturn = parentUri+this.getPageUri();
		return theReturn;
	}
	
	
	public void initializeEmptyPage(){
		//meant to be overrided in subclasses.
	}
	
}