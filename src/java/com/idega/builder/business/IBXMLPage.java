/*
 * $Id: IBXMLPage.java,v 1.18 2001/10/18 11:32:14 palli Exp $
 *
 * Copyright (C) 2001 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package com.idega.builder.business;

import org.jdom.JDOMException;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Attribute;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;
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

/**
 * A class that reads XML page descriptions from the database and returns
 * the elements/modules/applications it contains.
 *
 * @author <a href="mailto:palli@idega.is">Pall Helgason</a>, <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0alpha
 */
public class IBXMLPage {
  public final static String TYPE_PAGE = XMLConstants.PAGE_TYPE_PAGE;
  public final static String TYPE_TEMPLATE = XMLConstants.PAGE_TYPE_TEMPLATE;
  public final static String TYPE_DRAFT = XMLConstants.PAGE_TYPE_DRAFT;
  private final static String EMPTY = "";
  private SAXBuilder _builder = null;
  private Document _xmlDocument = null;
  private Element _rootElement = null;
  private Page _populatedPage = null;
  private String _key;

  private String _type = TYPE_PAGE;

  private static List _usingTemplate = null;

  /*
   *
   */
  private IBXMLPage(boolean verify) {
    _builder = new SAXBuilder(verify);
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
      if (ibpage.getType().equals(ibpage.DRAFT))
        setType(TYPE_DRAFT);
      if (ibpage.getType().equals(ibpage.TEMPLATE))
        setType(TYPE_TEMPLATE);
      else
        setType(TYPE_PAGE);
    }
    catch(PageDoesNotExist pe) {
      int template = ibpage.getTemplateId();
      String templateString = null;
      if (template != -1)
        templateString = Integer.toString(template);
      if (ibpage.getType().equals(ibpage.PAGE))
        setPageAsEmptyPage(TYPE_PAGE,templateString);
      if (ibpage.getType().equals(ibpage.DRAFT))
        setPageAsEmptyPage(TYPE_DRAFT,templateString);
      if (ibpage.getType().equals(ibpage.TEMPLATE))
        setPageAsEmptyPage(TYPE_TEMPLATE,templateString);
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

  private void invalidateUsingTemplate() {
    List l = getUsingTemplate();
    if (l != null) {
      Iterator i = l.iterator();
      while (i.hasNext()) {
        String invalid = (String)i.next();
        PageCacher.flagPageInvalid(invalid);
        IBXMLPage child = PageCacher.getXML(invalid);
        if (child.getType() == TYPE_TEMPLATE)
          child.invalidateUsingTemplate();
      }
    }
  }

  public boolean update() {
    try {
      IBPage ibpage = new IBPage(Integer.parseInt(_key));
      OutputStream stream = ibpage.getPageValueForWrite();
      store(stream);
      ibpage.update();
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
      e.printStackTrace();
    }
    setPopulatedPage(XMLReader.getPopulatedPage(this));
    if (_type == TYPE_TEMPLATE)
      invalidateUsingTemplate();

    return true;
  }

  private void store(OutputStream stream) {
    try {
      XMLOutputter outputter = new XMLOutputter("  ",true);
      outputter.setLineSeparator(System.getProperty("line.separator"));
      outputter.setTrimText(true);
      outputter.output(_xmlDocument,stream);
      stream.close();
    }
    catch(IOException e) {
      e.printStackTrace();
    }
  }

  public void setPopulatedPage(Page page){
    _populatedPage=page;
  }

  public Page getPopulatedPage(){
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
      _xmlDocument = _builder.build(URI);
      _rootElement = _xmlDocument.getRootElement();
    }
    catch(org.jdom.JDOMException e) {
      //System.err.println("JDOM Exception: " + e.getMessage());
      throw new PageDoesNotExist();
    }
  }



  /**
   * Sets the ...
   *
   * @param stream Stream to the file containing the XML description of the page.
   *
   * @throws com.idega.exception.PageDescriptionDoesNotExists The given XML file does not exists.
   */
  public void setXMLPageDescriptionFile(InputStream stream) throws PageDoesNotExist {
    boolean streamopen=true;
    try {
      _xmlDocument = _builder.build(stream);
      stream.close();
      _rootElement = _xmlDocument.getRootElement();
      streamopen=false;
    }
    catch(org.jdom.JDOMException e) {
      //System.err.println("JDOM Exception: " + e.getMessage());
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
    _rootElement = new Element(XMLConstants.ROOT_STRING);
    Element pageElement = new Element(XMLConstants.PAGE_STRING);

    if (type == null)
      type = XMLConstants.PAGE_TYPE_PAGE;

    if ((type.equals(TYPE_DRAFT)) ||
        (type.equals(TYPE_PAGE)) ||
        (type.equals(TYPE_TEMPLATE))) {
      pageElement.addAttribute(XMLConstants.PAGE_TYPE,type);
      setType(type);
    }
    else {
      pageElement.addAttribute(XMLConstants.PAGE_TYPE,TYPE_PAGE);
      setType(type);
    }

    if (template != null)
      pageElement.addAttribute(XMLConstants.TEMPLATE_STRING,template);

    _rootElement.addContent(pageElement);
    _xmlDocument = new Document(_rootElement);
    setPopulatedPage(XMLReader.getPopulatedPage(this));
  }

  /**
   * A function that returns the root element for the given page description file.
   *
   * @return The root element. Null if the page description file is not set.
   * @todo Wrap the Element class to hide all implementation of the XML parser.
   */
  Element getRootElement() {
    return(_rootElement);
  }

  Element getPageRootElement(){
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
  List getChildren(Element element) {
    if (element == null)
      return(null);

    if (!element.hasChildren())
      return(null);

    List li = element.getChildren();

    return(li);
  }

  List getAttributes(Element element) {
    if (element == null)
      return(null);

    List li = element.getAttributes();

    return(li);
  }

  /**
   *
   */
  public void setType(String type) {
    if ((type == TYPE_PAGE) ||
        (type == TYPE_TEMPLATE) ||
        (type == TYPE_DRAFT))
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


  public void setSourceFromString(String xmlRepresentation)throws Exception{
    StringReader reader = new StringReader(xmlRepresentation);
    SAXBuilder builder = new SAXBuilder();
    Document doc = builder.build(reader);
    this._rootElement = doc.getRootElement();
    this._xmlDocument.setRootElement(_rootElement);
    update();
  }

  public String toString(){
    Element root = getRootElement();
    if(root!=null){
      try{
        XMLOutputter outputter = new XMLOutputter();
        return outputter.outputString(root);
      }
      catch(Exception e){
        e.printStackTrace();
        return super.toString();
      }
    }
    return super.toString();
  }
}