/*
 * $Id: IBXMLPage.java,v 1.46 2004/06/14 13:52:48 palli Exp $
 *
 * Copyright (C) 2001 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package com.idega.builder.business;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.ejb.FinderException;

import com.idega.core.builder.data.ICPage;
import com.idega.core.builder.data.ICPageHome;
import com.idega.data.IDOLookupException;
import com.idega.exception.PageDoesNotExist;
import com.idega.presentation.Page;
import com.idega.xml.XMLDocument;
import com.idega.xml.XMLElement;
import com.idega.xml.XMLException;
import com.idega.xml.XMLOutput;
import com.idega.xml.XMLParser;

/**
 * A class that reads XML page descriptions from the database and returns
 * the elements/modules/applications it contains.
 *
 * @author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>, 
 * <a href="mailto:palli@idega.is">Pall Helgason</a>
 * 
 * @version 1.0
 */
public class IBXMLPage implements IBXMLAble {
	public final static String TYPE_PAGE = XMLConstants.PAGE_TYPE_PAGE;
	public final static String TYPE_TEMPLATE = XMLConstants.PAGE_TYPE_TEMPLATE;
	public final static String TYPE_DRAFT = XMLConstants.PAGE_TYPE_DRAFT;
	public final static String TYPE_DPT_TEMPLATE = XMLConstants.PAGE_TYPE_DPT_TEMPLATE;
	public final static String TYPE_DPT_PAGE = XMLConstants.PAGE_TYPE_DPT_PAGE;
	private XMLParser parser = null;
	private XMLDocument xmlDocument = null;
	private XMLElement rootElement = null;
	protected Page _populatedPage = null;
	private String _key;
	private ICPage _ibPage;
	private String _type = TYPE_PAGE;
	private List _usingTemplate = null;
	private String pageFormat;
	private boolean verifyXML=false;
	protected String stringSourceXML;
	
	/**
	 * Default constructor.
	 * Does nothing but set default values.
	 */
	public IBXMLPage(){
		//Default constructor
	}

	
	private IBXMLPage(boolean verify) {
		setVerifyXML(verify);
		//_parser = new XMLParser(verify);
	}

	public IBXMLPage(boolean verify, String key) {
		this(verify);
		setPageKey(key);
	}

	/**
	 * Sets the key for the page for this instance to represent.
	 * This is typically an id to a ICPage or ib_page.
	 */
	public void setPageKey(String key){
		_key = key;
		ICPage ibpage = null;
		try {
			ICPageHome pHome = (ICPageHome) com.idega.data.IDOLookup.getHome(ICPage.class);
			int pageId = Integer.parseInt(key);
			ibpage = pHome.findByPrimaryKey(pageId);
			setICPage(ibpage);
		}
		/*catch (PageDoesNotExist pe) {
			int template = ibpage.getTemplateId();
			String templateString = null;
			if (template != -1)
				templateString = Integer.toString(template);
			if (ibpage.isPage())
				setPageAsEmptyPage(TYPE_PAGE, templateString);
			else if (ibpage.isDraft())
				setPageAsEmptyPage(TYPE_DRAFT, templateString);
			else if (ibpage.isTemplate())
				setPageAsEmptyPage(TYPE_TEMPLATE, templateString);
			else if (ibpage.isDynamicTriggeredTemplate())
				setPageAsEmptyPage(TYPE_DPT_TEMPLATE, templateString);
			else if (ibpage.isDynamicTriggeredPage())
				setPageAsEmptyPage(TYPE_DPT_PAGE, templateString);
			else
				setPageAsEmptyPage(TYPE_PAGE, templateString);
		}*/
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
/*		String tmp = ibpage.getType();
		String tmp2 = IBPageBMPBean.PAGE;
		if (ibpage.getType().equals(IBPageBMPBean.PAGE)) {
			Page p = getPopulatedPage();
			if (p.getTitle() == null || p.getTitle().trim().equals("")) {
				p.setTitle(ibpage.getName());
			}
		}*/
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
			if (ibpage.isPage())
				setType(TYPE_PAGE);
			else if (ibpage.isDraft())
				setType(TYPE_DRAFT);
			else if (ibpage.isTemplate())
				setType(TYPE_TEMPLATE);
			else if (ibpage.isDynamicTriggeredTemplate())
				setType(TYPE_DPT_TEMPLATE);
			else if (ibpage.isDynamicTriggeredPage())
				setType(TYPE_DPT_PAGE);
			else
				setType(TYPE_PAGE);
		}
		catch (PageDoesNotExist pe) {
			int template = ibpage.getTemplateId();
			String templateString = null;
			if (template != -1)
				templateString = Integer.toString(template);
			if (ibpage.isPage())
				setPageAsEmptyPage(TYPE_PAGE, templateString);
			else if (ibpage.isDraft())
				setPageAsEmptyPage(TYPE_DRAFT, templateString);
			else if (ibpage.isTemplate())
				setPageAsEmptyPage(TYPE_TEMPLATE, templateString);
			else if (ibpage.isDynamicTriggeredTemplate())
				setPageAsEmptyPage(TYPE_DPT_TEMPLATE, templateString);
			else if (ibpage.isDynamicTriggeredPage())
				setPageAsEmptyPage(TYPE_DPT_PAGE, templateString);
			else
				setPageAsEmptyPage(TYPE_PAGE, templateString);
		}
	}
	
	/** 
	 * This method is called from setICPage to read into this page 
	 * from the page stream (from the database).
	 * @return
	 * @throws PageDoesNotExist
	 */
	protected void readPageStream(InputStream stream) throws PageDoesNotExist{
		readXMLDocument(stream);
	}
	
	/**
	 * Gets the key for the page that this instance represents.
	 * This is typically an id to a ICPage or ib_page.
	 */
	public String getPageKey() {
		return _key;
	}

	public void addUsingTemplate(String id) {
		if (_usingTemplate == null)
			_usingTemplate = new Vector();

		if (!_usingTemplate.contains(id)) {
			_usingTemplate.add(id);
		}
	}

	public void removeUsingTemplate(String id) {
		if (_usingTemplate == null)
			return;

		if (_usingTemplate.contains(id))
			_usingTemplate.remove(id);
	}

	public List getUsingTemplate() {
		if (_usingTemplate == null)
			findAllUsingTemplate();
		return _usingTemplate;
	}

	private void findAllUsingTemplate() {
		_usingTemplate = new Vector();
		List l = IBPageFinder.getAllPagesExtendingTemplate(Integer.parseInt(_key));
		if (l == null)
			return;
		Iterator i = l.iterator();
		while (i.hasNext()) {
			ICPage p = (ICPage) i.next();
			addUsingTemplate(p.getPrimaryKey().toString());
		}
	}

	private void invalidateUsingTemplate() {
		List l = getUsingTemplate();
		if (l != null) {
			Iterator i = l.iterator();
			while (i.hasNext()) {
				String invalid = (String) i.next();
				IBXMLPage child = PageCacher.getXMLIfInCache(invalid);
				if (child != null) {
					if (child.getType().equals(TYPE_TEMPLATE))
						child.invalidateUsingTemplate();
					PageCacher.flagPageInvalid(invalid);
				}
			}
		}
	}

	public synchronized boolean store() {
		try {
			ICPage ibpage = ((com.idega.core.builder.data.ICPageHome) com.idega.data.IDOLookup.getHome(ICPage.class)).findByPrimaryKey(new Integer(_key));
			ibpage.setFormat(this.getPageFormat());
			OutputStream stream = ibpage.getPageValueForWrite();
			storeStream(stream);
			ibpage.store();
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
		setPopulatedPage(null);
		if (_type.equals(TYPE_TEMPLATE))
			invalidateUsingTemplate();
		
		return true;
	}

	/**
	 * Writes this page to the given OutputStream stream.
	 * Called from the update method
	 * @param stream
	 */
	protected synchronized void storeStream(OutputStream stream) {
	try {
		
		if(this.getPageFormat().equals("IBXML")){
				
				XMLOutput output = new XMLOutput("  ", true);
				output.setLineSeparator(System.getProperty("line.separator"));
				output.setTextNormalize(true);
				//output.setEncoding("UTF-16");
				output.output(getXMLDocument(), stream);
				stream.close();
			}
			else if(this.getPageFormat().equals("HTML")){
				
				String theString = this.toString();
				StringReader sr = new StringReader(theString);
				OutputStreamWriter out = new OutputStreamWriter(stream);
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
		}
		catch (IOException e) {
			e.printStackTrace(System.err);
		}
	}

	public void setPopulatedPage(Page page) {
		_populatedPage = page;
	}

	/**
	 * Gets the com.idega.presentation.Page document instanciated for this page.
	 * @return
	 */
	public Page getPopulatedPage() {
		//Lazily load
		if(_populatedPage==null){
			synchronized(BuilderLogic.getInstance()){
				setPopulatedPage(XMLReader.getPopulatedPage(this));
			}
		}
		return _populatedPage;
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
		if(xmlDocument==null){
			throw new RuntimeException(this.getClass()+": xmlDocument is not initialized");
		}
		return xmlDocument;
	}
	
	private void setXMLDocument(XMLDocument document) {
		this.xmlDocument = document;
		this.rootElement = document.getRootElement();
	}

	public String getName() {
		try {
			return getIBPage().getName();
		}
		catch (Exception e) {
			return "";
		}
	}

	public int getTemplateId() {
		try {
			return getIBPage().getTemplateId();
		}
		catch (Exception e) {
			return -1;
		}
	}

	public void setTemplateId(int id) {
		try {
			ICPage page = getIBPage();
			page.setTemplateId(id);
			page.store();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setName(String name) {
		try {
			ICPage page = getIBPage();
			page.setName(name);
			page.store();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	ICPage getIBPage() throws IDOLookupException, NumberFormatException, FinderException{
		return ((com.idega.core.builder.data.ICPageHome) com.idega.data.IDOLookup.getHome(ICPage.class)).findByPrimaryKey(Integer.parseInt(_key));
	}

	/**
	 * Sets the InputStream to read the 
	 *
	 * @param stream Stream to the file containing the XML description of the page.
	 *
	 * @throws com.idega.exception.PageDescriptionDoesNotExists The given XML file does not exists.
	 */
	public void readXMLDocument(InputStream stream) throws PageDoesNotExist {
		boolean streamopen = true;
		try {
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
		XMLElement _rootElement = new XMLElement(XMLConstants.ROOT_STRING);
		setRootElement(_rootElement);
		XMLElement pageElement = new XMLElement(XMLConstants.PAGE_STRING);

		if (type == null)
			type = XMLConstants.PAGE_TYPE_PAGE;

		if ((type.equals(TYPE_DRAFT)) || (type.equals(TYPE_PAGE)) || (type.equals(TYPE_TEMPLATE)) || (type.equals(TYPE_DPT_TEMPLATE)) || (type.equals(TYPE_DPT_PAGE))) {
			pageElement.setAttribute(XMLConstants.PAGE_TYPE, type);
			setType(type);
		}
		else {
			pageElement.setAttribute(XMLConstants.PAGE_TYPE, TYPE_PAGE);
			setType(type);
		}

		if (template != null)
			pageElement.setAttribute(XMLConstants.TEMPLATE_STRING, template);

		this.setXMLDocument(new XMLDocument(_rootElement));
		_rootElement.addContent(pageElement);
		setPopulatedPage(XMLReader.getPopulatedPage(this));
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
		if(rootElement==null){
			rootElement=getXMLDocument().getRootElement();
		}
		return rootElement;
	}

	public XMLElement getPageRootElement() {
		if (getRootElement() != null) {
			return getRootElement().getChild(XMLConstants.PAGE_STRING);
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
		if (element == null)
			return null;

		if (!element.hasChildren())
			return null;

		List li = element.getChildren();

		return li;
	}

	public List getAttributes(XMLElement element) {
		if (element == null)
			return null;

		List li = element.getAttributes();

		return li;
	}

	public void setType(String type) {
		if ((type.equals(TYPE_PAGE)) || (type.equals(TYPE_TEMPLATE)) || (type.equals(TYPE_DRAFT)) || (type.equals(TYPE_DPT_TEMPLATE)) || (type.equals(TYPE_DPT_PAGE)))
			_type = type;
		else
			_type = TYPE_PAGE;
	}

	public String getType() {
		return _type;
	}

	public String getSourceAsString(){
		return stringSourceXML;
	}
	
	public void setSourceFromString(String xmlRepresentation) throws Exception {
		stringSourceXML=xmlRepresentation;
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

	public XMLElement copyModule(String pageKey, int ICObjectInstanceID) {
		return XMLWriter.copyModule(this, ICObjectInstanceID);
	}
	/**
	 * @return Returns the pageFormat.
	 */
	public String getPageFormat() {
		return pageFormat;
	}
	/**
	 * @param pageFormat The pageFormat to set.
	 */
	public void setPageFormat(String pageFormat) {
		this.pageFormat = pageFormat;
	}
	/**
	 * Gets if the XML parser should verify the XML source.
	 * Default is false.
	 * @return Returns the verifyXML.
	 */
	public boolean getIfVerifyXML() {
		return verifyXML;
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
		if(parser==null){
			parser=new XMLParser(this.getIfVerifyXML());
		}
		return parser;
	}
}