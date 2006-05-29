/*
 * $Id: IBXMLPage.java,v 1.62 2006/05/29 18:28:24 tryggvil Exp $
 * Created in 2001 by Tryggvi Larusson
 *
 * Copyright (C) 2001-2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.builder.business;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringReader;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import javax.ejb.FinderException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import com.idega.builder.data.IBPageBMPBean;
import com.idega.business.IBOLookup;
import com.idega.core.component.data.ICObjectInstance;
import com.idega.core.component.data.ICObjectInstanceHome;
import com.idega.data.IDOLookup;
import com.idega.data.IDOLookupException;
import com.idega.exception.PageDoesNotExist;
import com.idega.presentation.IWContext;
import com.idega.presentation.Page;
import com.idega.xml.XMLDocument;
import com.idega.xml.XMLElement;
import com.idega.xml.XMLException;
import com.idega.xml.XMLOutput;
import com.idega.xml.XMLParser;


/**
 * An instance of this class reads pages of format IBXML from the database and returns
 * the elements/modules/applications it contains.
 *
 *  Last modified: $Date: 2006/05/29 18:28:24 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:tryggvil@idega.com">Tryggvi Larusson</a>
 * @version $Revision: 1.62 $
 */
public class IBXMLPage extends CachedBuilderPage implements IBXMLAble,ComponentBasedPage{


	private XMLParser parser = null;
	private XMLDocument xmlDocument = null;
	private XMLElement rootElement = null;
	protected Page _populatedPage = null;
	private boolean verifyXML=false;
	
	/**
	 * @param key
	 */
	public IBXMLPage(String key) {
		this(false,key);
	}

	/**
	 * @param verifyXML
	 * @param key
	 */
	public IBXMLPage(boolean verifyXML, String key) {
		super(key);
		setVerifyXML(verifyXML);
		setComponentBased(true);
	}


	/**
	 * Sets the key for the page for this instance to represent.
	 * This is typically an id to a ICPage or ib_page.
	 */
	public void setPageKey(String key){
		super.setPageKey(key);
/*		ICPage ibpage = null;
		try {
			ICPageHome pHome = (ICPageHome) com.idega.data.IDOLookup.getHome(ICPage.class);
			int pageId = Integer.parseInt(key);
			ibpage = pHome.findByPrimaryKey(pageId);
			setICPage(ibpage);
		}
//		catch (PageDoesNotExist pe) {
//			int template = ibpage.getTemplateId();
//			String templateString = null;
//			if (template != -1)
//				templateString = Integer.toString(template);
//			if (ibpage.isPage())
//				setPageAsEmptyPage(TYPE_PAGE, templateString);
//			else if (ibpage.isDraft())
//				setPageAsEmptyPage(TYPE_DRAFT, templateString);
//			else if (ibpage.isTemplate())
//				setPageAsEmptyPage(TYPE_TEMPLATE, templateString);
//			else if (ibpage.isDynamicTriggeredTemplate())
//				setPageAsEmptyPage(TYPE_DPT_TEMPLATE, templateString);
//			else if (ibpage.isDynamicTriggeredPage())
//				setPageAsEmptyPage(TYPE_DPT_PAGE, templateString);
//			else
//				setPageAsEmptyPage(TYPE_PAGE, templateString);
//		}
		catch (NumberFormatException ne) {
			try {
				InputStream stream = new FileInputStream(key);
				readPageStream(stream);
			}
			catch (FileNotFoundException fnfe) {
				fnfe.printStackTrace();
			}
			catch (PageDoesNotExist pe) {
				setPageAsEmptyPage(null, null);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
//		String tmp = ibpage.getType();
//		String tmp2 = IBPageBMPBean.PAGE;
//		if (ibpage.getType().equals(IBPageBMPBean.PAGE)) {
//			Page p = getPopulatedPage();
//			if (p.getTitle() == null || p.getTitle().trim().equals("")) {
//				p.setTitle(ibpage.getName());
//			}
//		}
 	*/
		preloadIcObjectInstance();
	}
	
	
	/**
	 * <p>
	 * TODO tryggvil describe method preloadIcObject
	 * </p>
	 */
	private void preloadIcObjectInstance() {
		
		ICObjectInstanceHome icoHome = getICObjectInstanceHome();
		try {
			Collection icos = icoHome.findByPageKey(getPageKey());
			for (Iterator iter = icos.iterator(); iter.hasNext();) {
				ICObjectInstance instance = (ICObjectInstance) iter.next();
				//just caching the instance in beancache
			}
		}
		catch (FinderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * <p>
	 * TODO tryggvil describe method getICObjectInstanceHome
	 * </p>
	 * @return
	 */
	private ICObjectInstanceHome getICObjectInstanceHome() {
		try {
			return (ICObjectInstanceHome) IDOLookup.getHome(ICObjectInstance.class);
		}
		catch (IDOLookupException e) {
			throw new RuntimeException(e);
		}
	}

	/** 
	 * This method is called from setICPage to read into this page 
	 * from the page stream (from the database).
	 * @return
	 * @throws PageDoesNotExist
	 */
	protected void readPageStream(InputStream stream) throws PageDoesNotExist{
		readIBXMLDocument(stream);
	}
	

	public synchronized boolean store() {
		/*try {
			ICPage ibpage = ((com.idega.core.builder.data.ICPageHome) com.idega.data.IDOLookup.getHome(ICPage.class)).findByPrimaryKey(new Integer(getPageKey()));
			ibpage.setFormat(this.getPageFormat());
			OutputStream stream = ibpage.getPageValueForWrite();
			storeStream(stream);
			ibpage.store();
		}
		catch (NumberFormatException ne) {
		}
		catch (Exception e) {
			e.printStackTrace(System.err);
		}
		//setPopulatedPage(XMLReader.getPopulatedPage(this));
		setPopulatedPage(null);
		if (getType().equals(TYPE_TEMPLATE))
			invalidateAllPagesUsingThisTemplate();
		
		return true;*/
		boolean theReturn = super.store();
		setPopulatedPage(null);
		return theReturn;
	}

	/**
	 * Writes this page to the given OutputStream stream.
	 * Called from the update method
	 * @param stream
	 */
	protected synchronized void storeStream(OutputStream stream) {
		try {
			//Double check for the case when changing type from IBXML to HTML
			if(this.getPageFormat().equals(IBPageBMPBean.FORMAT_IBXML)){
					
					XMLOutput output = new XMLOutput("  ", true);
					output.setLineSeparator(System.getProperty("line.separator"));
					output.setTextNormalize(true);
					output.setEncoding("UTF-8");
					output.output(getXMLDocument(), stream);
					stream.close();
			}
			else{
				super.storeStream(stream);
			}
		}
		catch (IOException e) {
			e.printStackTrace(System.err);
		}
	}

	public void setPopulatedPage(Page page) {
		this._populatedPage = page;
	}

	/**
	 * Gets the com.idega.presentation.Page document instanciated for this page.
	 * @return
	 */
	public Page getPopulatedPage() {
		//Lazily load
		if(this._populatedPage==null){
			synchronized(BuilderLogic.getInstance()){
				setPopulatedPage(getBuilderLogic().getIBXMLReader().getPopulatedPage(this));
			}
		}
		return this._populatedPage;
	}

	/**
	 * Sets the ...
	 *
	 * @param URI The path to the file containing the XML description of the page.
	 *
	 * @throws com.idega.exception.PageDescriptionDoesNotExists The given XML file does not exists.
	 */
	public void setXMLPageDescriptionFile(String URI) throws PageDoesNotExist {
		try {
			//_xmlDocument = _parser.parse(URI);
			//_rootElement = _xmlDocument.getRootElement();
			this.setXMLDocument(getParser().parse(URI));
		}
		catch (XMLException e) {
			throw new PageDoesNotExist();
		}
	}

	private XMLDocument getXMLDocument(){
		if(this.xmlDocument==null){
			throw new RuntimeException(this.getClass()+": xmlDocument is not initialized");
		}
		return this.xmlDocument;
	}
	
	private void setXMLDocument(XMLDocument document) {
		this.xmlDocument = document;
		this.rootElement = document.getRootElement();
	}



	/**
	 * Sets the InputStream to read the 
	 *
	 * @param stream Stream to the file containing the XML description of the page.
	 *
	 * @throws com.idega.exception.PageDescriptionDoesNotExists The given XML file does not exists.
	 */
	protected void readIBXMLDocument(InputStream stream) throws PageDoesNotExist {
	
		boolean streamopen = true;
		try {
			if(stream==null){
				throw new PageDoesNotExist("Page contains no data");
			}
			new InputStreamReader(stream,"UTF-8");
			this.setXMLDocument(getParser().parse(stream));
			//_xmlDocument = _parser.parse(stream);
			stream.close();
			//_rootElement = _xmlDocument.getRootElement();
			streamopen = false;
		}
		catch (XMLException e) {
			throw new PageDoesNotExist();
		}
		catch (java.io.IOException ioe) {
			ioe.printStackTrace();
		}
		finally {
			if (streamopen) {
				try {
					if (stream != null) {
						stream.close();
						streamopen = false;
					}
				}
				catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void setPageAsEmptyPage(String type, String template) {
		XMLElement _rootElement = new XMLElement(IBXMLConstants.ROOT_STRING);
		setRootElement(_rootElement);
		XMLElement pageElement = new XMLElement(IBXMLConstants.PAGE_STRING);

		if (type == null) {
			type = IBXMLConstants.PAGE_TYPE_PAGE;
		}

		if ((type.equals(TYPE_DRAFT)) || (type.equals(TYPE_PAGE)) || (type.equals(TYPE_TEMPLATE)) || (type.equals(TYPE_DPT_TEMPLATE)) || (type.equals(TYPE_DPT_PAGE))) {
			pageElement.setAttribute(IBXMLConstants.PAGE_TYPE, type);
			setType(type);
		}
		else {
			pageElement.setAttribute(IBXMLConstants.PAGE_TYPE, TYPE_PAGE);
			setType(type);
		}

		if (template != null) {
			pageElement.setAttribute(IBXMLConstants.TEMPLATE_STRING, template);
		}

		this.setXMLDocument(new XMLDocument(_rootElement));
		_rootElement.addContent(pageElement);
		setPopulatedPage(getBuilderLogic().getIBXMLReader().getPopulatedPage(this));
	}

	/**
	 * A function that sets the root element for the given page xml document.
	 */
	public void setRootElement(XMLElement rootElement){
		this.rootElement=rootElement;
	}
	
	/**
	 * A function that returns the root element for the given page description file.
	 *
	 * @return The root element. Null if the page description file is not set.
	 * @todo Wrap the Element class to hide all implementation of the XML parser.
	 */
	public XMLElement getRootElement() {
		if(this.rootElement==null){
			this.rootElement=getXMLDocument().getRootElement();
		}
		return this.rootElement;
	}

	public XMLElement getPageRootElement() {
		if (getRootElement() != null) {
			return getRootElement().getChild(IBXMLConstants.PAGE_STRING);
		}
		return null;
	}

	/**
	 * A function that returns a list of child elements for a given element.
	 *
	 * @param element
	 * @return A List of elements. Null if the element has no children or is null.
	 * @todo Wrap the Element class to hide all implementation of the XML parser.
	 */
	List getChildren(XMLElement element) {
		if (element == null) {
			return null;
		}

		if (!element.hasChildren()) {
			return null;
		}

		List li = element.getChildren();

		return li;
	}

	public List getAttributes(XMLElement element) {
		if (element == null) {
			return null;
		}

		List li = element.getAttributes();

		return li;
	}

	public void setType(String type) {
		if ((type.equals(TYPE_PAGE)) || (type.equals(TYPE_TEMPLATE)) || (type.equals(TYPE_DRAFT)) || (type.equals(TYPE_DPT_TEMPLATE)) || (type.equals(TYPE_DPT_PAGE))) {
			super.setType(type);
		}
		else {
			super.setType(TYPE_PAGE);
		}
	}
	
	public void setSourceFromString(String xmlRepresentation) throws Exception {
		super.setSourceFromString(xmlRepresentation);
		try{
			StringReader reader = new StringReader(xmlRepresentation);
			XMLParser parser = new XMLParser();
			XMLDocument doc = parser.parse(reader);
			setXMLDocument(doc);
		}
		catch(Exception e){
			e.printStackTrace();
		}
		//update();
	}

	public String toString() {
		if(getSourceAsString()!=null){
			return getSourceAsString();
		}
		else if (getRootElement() != null) {
			XMLElement root = getRootElement();
			try {
				XMLOutput output = new XMLOutput();
				return output.outputString(root);
			}
			catch (Exception e) {
				e.printStackTrace();
				return super.toString();
			}
		}
		return super.toString();
	}

	public XMLElement copyModule(String instanceId) {
		return getBuilderLogic().getIBXMLWriter().copyModule(this, instanceId);
	}
	
	/**
	 * Gets if the XML parser should verify the XML source.
	 * Default is false.
	 * @return Returns the verifyXML.
	 */
	public boolean getIfVerifyXML() {
		return this.verifyXML;
	}
	/**
	 * Sets if the XML parser should verify the XML source.
	 * Default is false.
	 * 	 * @param verifyXML The verifyXML to set.
	 */
	public void setVerifyXML(boolean verifyXML) {
		this.verifyXML = verifyXML;
	}
	
	
	protected XMLParser getParser(){
		if(this.parser==null){
			this.parser=new XMLParser(this.getIfVerifyXML());
		}
		return this.parser;
	}

	
	//Moved from BuilderLogic:
	
	/**
	 * Gets a new Page instance with all Builder and Access control checks:
	 */
	public Page getPage(IWContext iwc) {
	    boolean builderView = false;
	    if (iwc.isParameterSet("view")) {
	      if(getBuilderLogic().isBuilderApplicationRunning(iwc)){
	        String view = iwc.getParameter("view");
	        if(view.equals("builder")) {
						builderView=true;
					}
	      }
	    }
	    return getPage(builderView,iwc);
	}
	
	/**
	 *
	 */
	public Page getPage(boolean builderEditView,IWContext iwc) {
		try {
			boolean permissionview = false;
			if (iwc.isParameterSet("ic_pm") && iwc.isSuperAdmin()) {
				permissionview = true;
			}
			Page page = getNewPage(iwc);
			
			if (builderEditView && iwc.hasEditPermission(page)) {
				return (getBuilderLogic().getBuilderTransformed(getPageKey(), page, iwc));
			}
			else if (permissionview) {
				int groupId = -1906;
				String bla = iwc.getParameter("ic_pm");
				if (bla != null) {
					try {
						groupId = Integer.parseInt(bla);
					}
					catch (NumberFormatException ex) {
					}
				}
				//page = getPageCacher().getPage(Integer.toString(id));
				page = getNewPageCloned();
				return (getBuilderLogic().getPermissionTransformed(groupId, page, iwc));
			}
			else {
				return (page);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			Page theReturn = new Page();
			theReturn.add("Page invalid");
			return (theReturn);
		}
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
	
	
	public UIComponent createComponent(FacesContext context){
		IWContext iwc = IWContext.getIWContext(context);
		return getPage(iwc);
	}
	

}
