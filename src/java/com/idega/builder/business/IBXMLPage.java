/*
 * $Id: IBXMLPage.java,v 1.35 2002/03/26 20:31:49 tryggvil Exp $
 *
 * Copyright (C) 2001 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package com.idega.builder.business;

import java.util.List;
import java.util.Iterator;
import java.util.Vector;
import java.io.InputStream;
import java.io.FileNotFoundException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.io.StringReader;
import com.idega.builder.data.IBPage;
import com.idega.exception.PageDoesNotExist;
import com.idega.presentation.Page;
import com.idega.xml.XMLParser;
import com.idega.xml.XMLDocument;
import com.idega.xml.XMLElement;
import com.idega.xml.XMLAttribute;
import com.idega.xml.XMLException;
import com.idega.xml.XMLOutput;

/**
 * A class that reads XML page descriptions from the database and returns
 * the elements/modules/applications it contains.
 *
 * @author <a href="mailto:palli@idega.is">Pall Helgason</a>, <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */
public class IBXMLPage implements IBXMLAble {
  public final static String TYPE_PAGE = XMLConstants.PAGE_TYPE_PAGE;
  public final static String TYPE_TEMPLATE = XMLConstants.PAGE_TYPE_TEMPLATE;
  public final static String TYPE_DRAFT = XMLConstants.PAGE_TYPE_DRAFT;
  public final static String TYPE_DPT_TEMPLATE = XMLConstants.PAGE_TYPE_DPT_TEMPLATE;
  public final static String TYPE_DPT_PAGE = XMLConstants.PAGE_TYPE_DPT_PAGE;
//  private final static String EMPTY = "";
  private XMLParser _parser = null;
  private XMLDocument _xmlDocument = null;
  private XMLElement _rootElement = null;
  private Page _populatedPage = null;
  private String _key;
  private IBPage _ibPage;

  private String _type = TYPE_PAGE;

  private List _usingTemplate = null;

  /*
   *
   */
  private IBXMLPage(boolean verify) {
    _parser = new XMLParser(verify);
  }

  /**
   *
   */
  public IBXMLPage(boolean verify, String key) {
    this(verify);
    _key = key;

    IBPage ibpage = null;
    try {
      ibpage = new IBPage(Integer.parseInt(key));
      setXMLPageDescriptionFile(ibpage.getPageValue());
      if (ibpage.getType().equals(ibpage.PAGE))
        setType(TYPE_PAGE);
      else if (ibpage.getType().equals(ibpage.DRAFT))
        setType(TYPE_DRAFT);
      else if (ibpage.getType().equals(ibpage.TEMPLATE))
        setType(TYPE_TEMPLATE);
      else if (ibpage.getType().equals(ibpage.DPT_TEMPLATE))
        setType(TYPE_DPT_TEMPLATE);
      else if (ibpage.getType().equals(ibpage.DPT_PAGE))
        setType(TYPE_DPT_PAGE);
      else
        setType(TYPE_PAGE);
    }
    catch(PageDoesNotExist pe) {
      int template = ibpage.getTemplateId();
      String templateString = null;
      if (template != -1)
        templateString = Integer.toString(template);
      if (ibpage.getType().equals(IBPage.PAGE))
        setPageAsEmptyPage(TYPE_PAGE,templateString);
      else if (ibpage.getType().equals(IBPage.DRAFT))
        setPageAsEmptyPage(TYPE_DRAFT,templateString);
      else if (ibpage.getType().equals(IBPage.TEMPLATE))
        setPageAsEmptyPage(TYPE_TEMPLATE,templateString);
      else if (ibpage.getType().equals(IBPage.DPT_TEMPLATE))
        setPageAsEmptyPage(TYPE_DPT_TEMPLATE,templateString);
      else if (ibpage.getType().equals(IBPage.DPT_PAGE))
        setPageAsEmptyPage(TYPE_DPT_PAGE,templateString);
      else
        setPageAsEmptyPage(TYPE_PAGE,templateString);
    }
    catch(NumberFormatException ne) {
      try {
        InputStream stream = new FileInputStream(key);
        setXMLPageDescriptionFile(stream);
      }
      catch(FileNotFoundException fnfe) {
        fnfe.printStackTrace();
      }
      catch(PageDoesNotExist pe) {
        setPageAsEmptyPage(null,null);
      }
    }
    catch(Exception e) {
      e.printStackTrace();
    }
    setPopulatedPage(XMLReader.getPopulatedPage(this));
  }

  public String getKey(){
    return(_key);
  }

  /**
   *
   */
  public void addUsingTemplate(String id) {
    if (_usingTemplate == null)
      _usingTemplate = new Vector();

    if (!_usingTemplate.contains(id)) {
      _usingTemplate.add(id);
    }
  }

  /**
   *
   */
  public void removeUsingTemplate(String id) {
    if (_usingTemplate == null)
      return;

    if (_usingTemplate.contains(id))
      _usingTemplate.remove(id);
  }

  /**
   *
   */
  public List getUsingTemplate() {
    if (_usingTemplate == null)
      findAllUsingTemplate();
    return(_usingTemplate);
  }

  /**
   *
   */
  private void findAllUsingTemplate() {
    _usingTemplate = new Vector();
    List l = IBPageFinder.getAllPagesExtendingTemplate(Integer.parseInt(_key));
    if (l == null)
      return;
    Iterator i = l.iterator();
    while (i.hasNext()) {
      IBPage p = (IBPage)i.next();
      addUsingTemplate(Integer.toString(p.getID()));
    }
  }

  /**
   *
   */
  private void invalidateUsingTemplate() {
    List l = getUsingTemplate();
    if (l != null) {
      Iterator i = l.iterator();
      while (i.hasNext()) {
        String invalid = (String)i.next();
        PageCacher.flagPageInvalid(invalid);
        IBXMLPage child = PageCacher.getXML(invalid);
        if (child.getType().equals(TYPE_TEMPLATE))
          child.invalidateUsingTemplate();
      }
    }
  }

  /**
   *
   */
  public synchronized boolean update() {
    try {
      IBPage ibpage = new IBPage(Integer.parseInt(_key));
      OutputStream stream = ibpage.getPageValueForWrite();
      store(stream);
      ibpage.update();
//      System.out.println("[iwBuilder] : Updating page "+ibpage.getName()+" with id="+ibpage.getID());
    }
    catch(NumberFormatException ne) {
      try {
        OutputStream stream = new FileOutputStream(_key);
        store(stream);
      }
      catch(FileNotFoundException fnfe) {
        fnfe.printStackTrace();
      }
    }
    catch(Exception e) {
      e.printStackTrace(System.err);
    }
    setPopulatedPage(XMLReader.getPopulatedPage(this));
    if (_type.equals(TYPE_TEMPLATE))
      invalidateUsingTemplate();

    return true;
  }

  /**
   *
   */
  private synchronized void store(OutputStream stream) {
    try {
      XMLOutput output = new XMLOutput("  ",true);
      output.setLineSeparator(System.getProperty("line.separator"));
      output.setTextNormalize(true);
      output.output(_xmlDocument,stream);
      stream.close();
    }
    catch(IOException e) {
      e.printStackTrace(System.err);
    }
  }

  /**
   *
   */
  public void setPopulatedPage(Page page) {
    _populatedPage = page;
  }

  /**
   *
   */
  public Page getPopulatedPage() {
    return(_populatedPage);
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
      this.setXMLDocument(_parser.parse(URI));
    }
    catch(XMLException e) {
      throw new PageDoesNotExist();
    }
  }

  private void setXMLDocument(XMLDocument document){
    this._xmlDocument=document;
    this._rootElement=document.getRootElement();
  }

  /**
   *
   */
  public String getName() {
    try {
      return(getIBPage().getName());
    }
    catch(Exception e) {
      return("");
    }
  }

  /**
   *
   */
  public int getTemplateId() {
    try {
      return(getIBPage().getTemplateId());
    }
    catch(Exception e){
      return(-1);
    }
  }

  /**
   *
   */
  public void setTemplateId(int id) {
    try {
      IBPage page = getIBPage();
      page.setTemplateId(id);
      page.update();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }

  /**
   *
   */
  public void setName(String name) {
    try {
      IBPage page = getIBPage();
      page.setName(name);
      page.update();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }


  IBPage getIBPage()throws Exception{
    /*if(_ibPage==null){
      _ibPage=new IBPage(Integer.parseInt(_key));
    }
    return _ibPage;*/
    return new IBPage(Integer.parseInt(_key));
  }


  /**
   * Sets the ...
   *
   * @param stream Stream to the file containing the XML description of the page.
   *
   * @throws com.idega.exception.PageDescriptionDoesNotExists The given XML file does not exists.
   */
  public void setXMLPageDescriptionFile(InputStream stream) throws PageDoesNotExist {
    boolean streamopen = true;
    try {
      this.setXMLDocument(_parser.parse(stream));
      //_xmlDocument = _parser.parse(stream);
      stream.close();
      //_rootElement = _xmlDocument.getRootElement();
      streamopen=false;
    }
    catch(XMLException e) {
      throw new PageDoesNotExist();
    }
    catch(java.io.IOException ioe) {
      ioe.printStackTrace();
    }
    finally{
      if(streamopen){
        try{
          if(stream!=null){
            stream.close();
          }
        }
        catch(IOException e){
          e.printStackTrace();
        }
      }
    }
  }

  public void setPageAsEmptyPage(String type, String template) {
    _rootElement = new XMLElement(XMLConstants.ROOT_STRING);
    XMLElement pageElement = new XMLElement(XMLConstants.PAGE_STRING);

    if (type == null)
      type = XMLConstants.PAGE_TYPE_PAGE;

    if ((type.equals(TYPE_DRAFT)) ||
        (type.equals(TYPE_PAGE)) ||
        (type.equals(TYPE_TEMPLATE)) ||
        (type.equals(TYPE_DPT_TEMPLATE)) ||
        (type.equals(TYPE_DPT_PAGE))
        ) {
//      pageElement.addAttribute(XMLConstants.PAGE_TYPE,type);
      pageElement.setAttribute(XMLConstants.PAGE_TYPE,type);
      setType(type);
    }
    else {
//      pageElement.addAttribute(XMLConstants.PAGE_TYPE,TYPE_PAGE);
      pageElement.setAttribute(XMLConstants.PAGE_TYPE,TYPE_PAGE);
      setType(type);
    }

    if (template != null)
//      pageElement.addAttribute(XMLConstants.TEMPLATE_STRING,template);
      pageElement.setAttribute(XMLConstants.TEMPLATE_STRING,template);

    this.setXMLDocument( new XMLDocument(_rootElement));
    _rootElement.addContent(pageElement);
    //_xmlDocument = new XMLDocument(_rootElement);
    setPopulatedPage(XMLReader.getPopulatedPage(this));
  }

  /**
   * A function that returns the root element for the given page description file.
   *
   * @return The root element. Null if the page description file is not set.
   * @todo Wrap the Element class to hide all implementation of the XML parser.
   */
  public XMLElement getRootElement() {
    return(_rootElement);
  }

  public XMLElement getPageRootElement(){
    if(_rootElement!=null){
      return _rootElement.getChild(XMLConstants.PAGE_STRING);
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
      return(null);

    if (!element.hasChildren())
      return(null);

    List li = element.getChildren();

    return(li);
  }

  public List getAttributes(XMLElement element) {
    if (element == null)
      return(null);

    List li = element.getAttributes();

    return(li);
  }

  /**
   *
   */
  public void setType(String type) {
    if ((type.equals(TYPE_PAGE)) ||
        (type.equals(TYPE_TEMPLATE)) ||
        (type.equals(TYPE_DRAFT)) ||
        (type.equals(TYPE_DPT_TEMPLATE)) ||
        (type.equals(TYPE_DPT_PAGE)))
      _type = type;
    else
      _type = TYPE_PAGE;
  }

  /**
   *
   */
  public String getType() {
    return(_type);
  }

  /**
   *
   */
  public void setSourceFromString(String xmlRepresentation) throws Exception {
    StringReader reader = new StringReader(xmlRepresentation);
    XMLParser parser = new XMLParser();
    XMLDocument doc = parser.parse(reader);
    //_rootElement = doc.getRootElement();
    //_xmlDocument.setRootElement(_rootElement);
    this.setXMLDocument(doc);
    update();
  }

  /**
   *
   */
  public String toString() {
    XMLElement root = getRootElement();
    if (root != null) {
      try {
        XMLOutput output = new XMLOutput();
        return(output.outputString(root));
      }
      catch(Exception e) {
        e.printStackTrace();
        return(super.toString());
      }
    }
    return(super.toString());
  }

  /**
   *
   */
  public XMLElement copyModule(String pageKey, int ICObjectInstanceID) {
    return(XMLWriter.copyModule(this,ICObjectInstanceID));
  }
}