/*
 * $Id: CachedBuilderPage.java,v 1.7 2006/04/09 11:43:34 laddi Exp $
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
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import com.idega.core.builder.data.ICPage;
import com.idega.core.builder.data.ICPageHome;
import com.idega.core.view.DefaultViewNode;
import com.idega.core.view.ViewNode;
import com.idega.data.IDOLookup;
import com.idega.exception.PageDoesNotExist;

/**
 * An abstract class that represents a cached instance of a Builder page.
 * Subclasses of this class handle pages of different formats such as IBXML,HTML and JSP.
 * 
 * @author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>, 
 * <a href="mailto:palli@idega.is">Pall Helgason</a>
 * 
 * @version 1.0
 */
public abstract class CachedBuilderPage extends DefaultViewNode implements ViewNode {
	
	public final static String TYPE_PAGE = XMLConstants.PAGE_TYPE_PAGE;
	public final static String TYPE_TEMPLATE = XMLConstants.PAGE_TYPE_TEMPLATE;
	protected final static String TYPE_DRAFT = XMLConstants.PAGE_TYPE_DRAFT;
	protected final static String TYPE_DPT_TEMPLATE = XMLConstants.PAGE_TYPE_DPT_TEMPLATE;
	protected final static String TYPE_DPT_PAGE = XMLConstants.PAGE_TYPE_DPT_PAGE;
	
	private String type=TYPE_PAGE;
	private String _key;
	private String pageFormat;
	private String sourceAsString;
	private List pageKeysUsingThisTemplate;
	private String pageUri;
	
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
			readPageStream(ibpage.getPageValue());
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

	public synchronized boolean store() {
		try {
			ICPage icPage = getICPage();
			icPage.setFormat(this.getPageFormat());
			OutputStream stream = icPage.getPageValueForWrite();
			storeStream(stream);
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
				//String theString = new String(this.toString().getBytes(),"UTF-8");
				String theString = this.toString();
				StringReader sr = new StringReader(theString);
				
				OutputStreamWriter out = new OutputStreamWriter(stream,"UTF-8");
				
				
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
		try {
			return getICPage().getName();
		}
		catch (Exception e) {
			return "";
		}
	}

	public String getTemplateKey() {
		try {
			return Integer.toString(getICPage().getTemplateId());
		}
		catch (Exception e) {
			return "-1";
		}
	}

	public void setTemplateKey(String templateKey) {
		try {
			int id = Integer.parseInt(templateKey);
			ICPage page = getICPage();
			page.setTemplateId(id);
			page.store();
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