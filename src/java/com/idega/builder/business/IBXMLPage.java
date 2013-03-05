/*
 * $Id: IBXMLPage.java,v 1.74 2009/01/14 15:07:18 tryggvil Exp $
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
import java.util.List;
import java.util.logging.Logger;

import javax.ejb.FinderException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import com.idega.builder.data.IBPageBMPBean;
import com.idega.core.accesscontrol.business.PagePermissionObject;
import com.idega.core.accesscontrol.business.StandardRoles;
import com.idega.core.builder.data.ICPage;
import com.idega.core.component.data.ICObjectInstance;
import com.idega.core.component.data.ICObjectInstanceHome;
import com.idega.data.IDOLookup;
import com.idega.data.IDOLookupException;
import com.idega.exception.PageDoesNotExist;
import com.idega.presentation.IWContext;
import com.idega.presentation.Page;
import com.idega.util.CoreConstants;
import com.idega.util.IOUtil;
import com.idega.xml.XMLAttribute;
import com.idega.xml.XMLDocument;
import com.idega.xml.XMLElement;
import com.idega.xml.XMLException;
import com.idega.xml.XMLOutput;
import com.idega.xml.XMLParser;


/**
 * An instance of this class reads pages of format IBXML from the database and returns
 * the elements/modules/applications it contains.
 *
 *  Last modified: $Date: 2009/01/14 15:07:18 $ by $Author: tryggvil $
 *
 * @author <a href="mailto:tryggvil@idega.com">Tryggvi Larusson</a>
 * @version $Revision: 1.74 $
 */
public class IBXMLPage extends CachedBuilderPage implements IBXMLAble,ComponentBasedPage{

	private static final long serialVersionUID = -2693227585756124885L;
	private static final Logger LOGGER = Logger.getLogger(IBXMLPage.class.getName());

	private XMLParser parser = null;
	private XMLDocument xmlDocument = null;
	private XMLElement rootElement = null;
	protected Page _populatedPage = null;
	private boolean verifyXML=false;

	/**
	 * @param key
	 */
	public IBXMLPage(String key) {
		this(false, key);
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
	@Override
	public void setPageKey(String key){
		super.setPageKey(key);
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
			/*Collection icos = */icoHome.findByPageKey(getPageKey());
			/*for (Iterator iter = icos.iterator(); iter.hasNext();) {
				ICObjectInstance instance = (ICObjectInstance) iter.next();
				//just caching the instance in beancache
			}*/
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
		} catch (IDOLookupException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * This method is called from setICPage to read into this page
	 * from the page stream (from the database).
	 * @return
	 * @throws PageDoesNotExist
	 */
	@Override
	protected void readPageStream(InputStream stream) throws PageDoesNotExist{
		readIBXMLDocument(stream);
	}


	@Override
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

		try {
			setSourceFromString(toString());
		} catch (Exception e) {}
		boolean theReturn = super.store();
		setPopulatedPage(null);
		return theReturn;
	}

	/**
	 * Writes this page to the given OutputStream stream.
	 * Called from the update method
	 * @param stream
	 */
	@Override
	protected synchronized void storeStream(OutputStream stream) {
		XMLDocument doc = getXMLDocument();
		if (doc == null) {
			LOGGER.warning("XMLDocument is not initialized for page " + getPageUri() + " (ID: " + getPageKey() + ")");
			return;
		}
		try {
			//Double check for the case when changing type from IBXML to HTML
			if (this.getPageFormat().equals(IBPageBMPBean.FORMAT_IBXML)) {
				XMLOutput output = new XMLOutput("  ", true);
				output.setLineSeparator(System.getProperty("line.separator"));
				output.setTextNormalize(true);
				output.setEncoding(CoreConstants.ENCODING_UTF8);
				output.output(doc, stream);
				stream.close();
			} else{
				super.storeStream(stream);
			}
		} catch (IOException e) {
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
		if (this._populatedPage==null) {
			synchronized(BuilderLogic.getInstance()) {
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
			this.setXMLDocument(getParser().parse(URI));
		} catch (XMLException e) {
			throw new PageDoesNotExist();
		}
	}

	protected XMLDocument getXMLDocument(){
		if(this.xmlDocument==null) {
			if (loadXMLDocument()) {
				LOGGER.info("Page " + this.getPageUri() + " was successfully initialized.");
			} else {
				LOGGER.warning("Page " + this.getPageUri() + " can not be found, XMLDocument is not intialized!");
			}
		}
		return this.xmlDocument;
	}

	private boolean loadXMLDocument() {
		try {
			readIBXMLDocument(getPageInputStream(getICPage()));
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	protected void setXMLDocument(XMLDocument document) {
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
		InputStreamReader reader = null;
		try {
			if (stream == null)
				throw new PageDoesNotExist("Page (URI: " + getPageUri() + ", ID: " + getPageKey() + ") contains no data");

			reader = new InputStreamReader(stream, CoreConstants.ENCODING_UTF8);
			XMLDocument page = getParser().parse(stream);
			setXMLDocument(page);

			XMLOutput output = new XMLOutput();
			String content = output.outputString(page);
			setSourceFromString(content);
		} catch (XMLException e) {
			throw new PageDoesNotExist();
		} catch (java.io.IOException ioe) {
			ioe.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			IOUtil.close(stream);
			IOUtil.close(reader);
		}
	}

	@Override
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
		} else {
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
	@Override
	public XMLElement getRootElement() {
		if (this.rootElement == null) {
			XMLDocument doc = getXMLDocument();
			if (doc != null) {
				this.rootElement = doc.getRootElement();
			}
		}
		return this.rootElement;
	}

	@Override
	public XMLElement getPageRootElement() {
		if (getRootElement() != null) {
			return getRootElement().getChild(IBXMLConstants.PAGE_STRING);
		}
		return null;
	}

	protected XMLElement getPageElement(XMLElement root) {
		XMLElement pageXML = root.getChild(IBXMLConstants.PAGE_STRING);
		return pageXML;
	}

	/**
	 * A function that returns a list of child elements for a given element.
	 *
	 * @param element
	 * @return A List of elements. Null if the element has no children or is null.
	 * @todo Wrap the Element class to hide all implementation of the XML parser.
	 */
	List<XMLElement> getChildren(XMLElement element) {
		if (element == null) {
			return null;
		}

		if (!element.hasChildren()) {
			return null;
		}

		return  element.getChildren();
	}

	@Override
	public List<XMLAttribute> getAttributes(XMLElement element) {
		if (element == null) {
			return null;
		}

		return element.getAttributes();
	}

	@Override
	public void setType(String type) {
		if ((type.equals(TYPE_PAGE)) || (type.equals(TYPE_TEMPLATE)) || (type.equals(TYPE_DRAFT)) || (type.equals(TYPE_DPT_TEMPLATE)) || (type.equals(TYPE_DPT_PAGE))) {
			super.setType(type);
		} else {
			super.setType(TYPE_PAGE);
		}
	}

	@Override
	public void setSourceFromString(String xmlRepresentation) throws Exception {
		super.setSourceFromString(xmlRepresentation);
		try{
			StringReader reader = new StringReader(xmlRepresentation);
			XMLParser parser = new XMLParser();
			XMLDocument doc = parser.parse(reader);
			setXMLDocument(doc);
		} catch(Exception e){
			e.printStackTrace();
		}
	}

	@Override
	public String toString() {
		if (getRootElement() != null) {
			XMLElement root = getRootElement();
			try {
				XMLOutput output = new XMLOutput();
				String content = output.outputString(root);
				return content;
			} catch (Exception e) {
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

	/**
	 * Gets a new Page instance with all Builder and Access control checks:
	 */
	@Override
	public Page getPage(IWContext iwc) {
	    return getPageBuilderChecked(iwc);
	}

	public boolean hasEditPermissions(IWContext iwc) {
		PagePermissionObject page = new PagePermissionObject(getPageKey());
		if (iwc.hasEditPermission(page)) {
			return true;
		}

		ICPage icPage = getBuilderLogic().getICPage(getPageKey());
		if (icPage == null) {
			return false;
		}
		if (icPage.isPublished() && !iwc.hasRole(StandardRoles.ROLE_KEY_EDITOR)) {
			return false;
		}

		return iwc.hasRole(StandardRoles.ROLE_KEY_EDITOR) || iwc.hasRole(StandardRoles.ROLE_KEY_AUTHOR);
	}

	/**
	 *
	 */
	public Page getPageBuilderChecked(IWContext iwc) {
		try {
			boolean permissionview = false;
			if (iwc.isParameterSet("ic_pm") && iwc.isSuperAdmin()) {
				permissionview = true;
			}
			Page page = getNewPage(iwc);

			boolean transformPage = Boolean.TRUE;
			Object o = iwc.getRequest().getAttribute(BuilderConstants.TRANSFORM_PAGE_TO_BUILDER_PAGE_ATTRIBUTE);
			if (o instanceof Boolean) {
				transformPage = (Boolean) o;
			}
			if (!page.isHideBuilder() && isBuilderEditMode(iwc) && transformPage) {
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

	public boolean isBuilderEditMode(IWContext iwc) {
	    boolean builderEditView = false;
	    if (getBuilderLogic().isBuilderApplicationRunning(iwc)) {
	    	builderEditView = true;
	    }
		return(builderEditView && hasEditPermissions(iwc));
	}

	@Override
	public Page getNewPageCloned(){
		return (Page) this.getPopulatedPage().clone();
	}

	/**
	 * Gets a new Page instanfce without any Builder checks. (not transformed for Builder Edit view)
	 * @param iwc
	 * @return
	 */
	@Override
	public Page getNewPage(IWContext iwc){
		return (Page) this.getPopulatedPage().clonePermissionChecked(iwc);
	}


	@Override
	public UIComponent createComponent(FacesContext context){
		IWContext iwc = IWContext.getIWContext(context);
		return getPage(iwc);
	}

	/**
	 * Changes the template id for the current page.
	 *
	 * @param newTemplateId
	 *          The new template id for the current page.
	 * @param iwc
	 *          The IdegeWeb Context object
	 */
	public void changeTemplateId(String newTemplateId) {
		if (getType().equals(CachedBuilderPage.TYPE_PAGE)) {
			String oldId = getTemplateKey();
			if (!newTemplateId.equals(oldId)) {
				setTemplateKey(newTemplateId);
				synchronized(BuilderLogic.getInstance()) {
					String currentPageId = getPageKey();
					setTemplateId(newTemplateId);
					getBuilderLogic().getCachedBuilderPage(newTemplateId).addPageUsingThisTemplate(currentPageId);
					getBuilderLogic().getCachedBuilderPage(oldId).removePageAsUsingThisTemplate(currentPageId);
				}
			}
		}
	}

	public boolean setTemplateId(String id) {
		synchronized(BuilderLogic.getInstance()) {
			if (getBuilderLogic().getIBXMLWriter().setAttribute(this, "-1", IBXMLConstants.TEMPLATE_STRING, id)) {
				return this.store();
			}
		}
		return false;
	}

}