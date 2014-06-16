/*
 * $Id: CachedBuilderPage.java,v 1.22 2009/04/07 23:54:02 eiki Exp $
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
import java.io.Serializable;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.idega.core.builder.data.ICPage;
import com.idega.core.builder.data.ICPageHome;
import com.idega.core.view.DefaultViewNode;
import com.idega.core.view.ViewNode;
import com.idega.data.IDOLookup;
import com.idega.exception.PageDoesNotExist;
import com.idega.repository.bean.RepositoryItem;
import com.idega.util.CoreConstants;
import com.idega.util.StringHandler;
import com.idega.util.StringUtil;

/**
 * An abstract class that represents a cached instance of a Builder page.
 * Subclasses of this class handle pages of different formats such as IBXML, HTML and JSP.
 *
 * @author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>,
 * <a href="mailto:palli@idega.is">Pall Helgason</a>
 *
 * @version 1.0
 */
public abstract class CachedBuilderPage extends DefaultViewNode implements ViewNode, Serializable {

	private static final long serialVersionUID = -294824313982410948L;

	private static final Logger LOGGER = Logger.getLogger(CachedBuilderPage.class.getName());

	public final static String	TYPE_PAGE = IBXMLConstants.PAGE_TYPE_PAGE,
								TYPE_TEMPLATE = IBXMLConstants.PAGE_TYPE_TEMPLATE;
	protected final static String	TYPE_DRAFT = IBXMLConstants.PAGE_TYPE_DRAFT,
									TYPE_DPT_TEMPLATE = IBXMLConstants.PAGE_TYPE_DPT_TEMPLATE,
									TYPE_DPT_PAGE = IBXMLConstants.PAGE_TYPE_DPT_PAGE;

	private String type=TYPE_PAGE;
	private String _key;
	private String pageFormat;
	private String sourceAsString;
	private List<String> pageKeysUsingThisTemplate;
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
		super(key, BuilderLogic.getInstance().getBuilderPageRootViewNode());
		setPageKey(key);
	}

	protected abstract void readPageStream(InputStream stream) throws PageDoesNotExist;

	public void setSourceFromString(String xmlRepresentation) throws Exception {
		this.sourceAsString = xmlRepresentation;
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
			this.pageKeysUsingThisTemplate = new ArrayList<String>();
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

	public List<String> getUsingTemplate() {
		if (this.pageKeysUsingThisTemplate == null) {
			findAllPagesUsingThisTemplate();
		}
		return this.pageKeysUsingThisTemplate;
	}

	private void findAllPagesUsingThisTemplate() {
		this.pageKeysUsingThisTemplate = new ArrayList<String>();
		List<ICPage> l = IBPageFinder.getAllPagesExtendingTemplate(Integer.parseInt(getPageKey()));
		if (l == null) {
			return;
		}
		for (Iterator<ICPage> i = l.iterator(); i.hasNext();) {
			ICPage p = i.next();
			addPageUsingThisTemplate(p.getPrimaryKey().toString());
		}
	}

	protected void invalidateAllPagesUsingThisTemplate() {
		List<String> l = getUsingTemplate();
		if (l != null) {
			for (Iterator<String> i = l.iterator(); i.hasNext();) {
				String invalid = i.next();
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
		try {
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
			} else if (ibpage.isDraft()) {
				setType(TYPE_DRAFT);
			} else if (ibpage.isTemplate()) {
				setType(TYPE_TEMPLATE);
			} else if (ibpage.isDynamicTriggeredTemplate()) {
				setType(TYPE_DPT_TEMPLATE);
			} else if (ibpage.isDynamicTriggeredPage()) {
				setType(TYPE_DPT_PAGE);
			} else {
				setType(TYPE_PAGE);
			}

			super.setName(ibpage.getName());
			this.templateKey = ibpage.getTemplateKey();
		} catch (PageDoesNotExist pe) {
			int template = ibpage.getTemplateId();
			String templateString = null;
			if (template != -1) {
				templateString = Integer.toString(template);
			}
			if (ibpage.isPage()) {
				setPageAsEmptyPage(TYPE_PAGE, templateString);
			} else if (ibpage.isDraft()) {
				setPageAsEmptyPage(TYPE_DRAFT, templateString);
			} else if (ibpage.isTemplate()) {
				setPageAsEmptyPage(TYPE_TEMPLATE, templateString);
			} else if (ibpage.isDynamicTriggeredTemplate()) {
				setPageAsEmptyPage(TYPE_DPT_TEMPLATE, templateString);
			} else if (ibpage.isDynamicTriggeredPage()) {
				setPageAsEmptyPage(TYPE_DPT_PAGE, templateString);
			} else {
				setPageAsEmptyPage(TYPE_PAGE, templateString);
			}
		}
	}

	protected void ensureReferencedPagesLoaded(ICPage ibpage) {
		//Ensuring that referenced templates are loaded before us
		String templateKey = ibpage.getTemplateKey();
		if (templateKey != null && !templateKey.equals(String.valueOf(-1))) {
			getPageCacher().getCachedBuilderPage(templateKey);
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
		if (StringUtil.isEmpty(webdavUri)) {
			//	Older method, read it from ICFile
			return icPage.getPageValue();
		}

		try {
			return getRepositoryService().getInputStreamAsRoot(webdavUri);
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Error getting input stream for page: " + webdavUri, e);
		}

		return null;
	}

	/**
	 * <p>
	 * Method for getting a reference to the outputStream for storing the page.
	 * </p>
	 * @param pageType
	 * @param templateString
	 */
	protected OutputStream getPageOutputStream(ICPage icPage) {
		String webdavUri = icPage.getWebDavUri();
		if (webdavUri == null) {
			//	older method, read it from ICFile
			return icPage.getPageValueForWrite();
		} else {
			try {
				String basePath = CoreConstants.PAGES_PATH;
				if (webdavUri.startsWith(basePath)) {
					getRepositoryService().createFolder(basePath);
				}
				RepositoryItem file = getRepositoryService().getRepositoryItemAsRootUser(webdavUri);
				if (!file.exists()) {
					file.createNewFile();
				}

				return getRepositoryService().getOutputStreamAsRoot(webdavUri);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		throw new RuntimeException("OutputStream cannot be read for page " + icPage.getPageKey() );
	}

	public void setPageAsEmptyPage(String pageType,String templateString){
		//does nothing by default
	}

	protected ICPage getICPage(){
		try {
			ICPageHome icPageHome = (ICPageHome) IDOLookup.getHome(ICPage.class);
			return icPageHome.findByPrimaryKey(getPageKey());
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	private boolean store(String webDavUri) {
		if (webDavUri == null)
			return false;

		String source = getSourceAsString();
		if (source == null) {
			LOGGER.warning("There is no source to store for page with ID: " + getPageKey() + ", URI: " + getPageUri() + ", URI in repository: " +
					webDavUri);
			return false;
		}

		String fileName = webDavUri.substring(webDavUri.lastIndexOf(CoreConstants.SLASH) + 1 );
		String base = webDavUri.substring(0, webDavUri.indexOf(fileName));
		if (!base.endsWith(CoreConstants.SLASH))
			base = base.concat(CoreConstants.SLASH);
		boolean result = false;
		try {
			result = getRepositoryService().updateFileContentsAsRoot(base + fileName, StringHandler.getStreamFromString(source), true) != null;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return result;
	}

	public boolean store() {
		try {
			ICPage icPage = getICPage();
			icPage.setFormat(this.getPageFormat());
			if (icPage.getWebDavUri() == null) {
				OutputStream stream = getPageOutputStream(icPage);
				storeStream(stream);
			} else {
				store(icPage.getWebDavUri());
			}
			icPage.store();
		} catch (NumberFormatException ne) {
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}

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
	protected void storeStream(OutputStream stream) {
		try {
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
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String toString() {
		String s = getSourceAsString();
		if (s != null) {
			return s;
		} else{
			return super.toString();
		}
	}

	@Override
	public String getName() {
		String sName = super.getName();
		if (sName == null) {
			try {
				sName= getICPage().getName();
				super.setName(sName);
			} catch (Exception e) {
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

	@Override
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


	@Override
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

	@Override
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