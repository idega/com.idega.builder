/*
 * $Id: IBXMLPage.java,v 1.9 2001/10/02 15:40:09 palli Exp $
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
import java.util.Hashtable;
import java.util.Enumeration;
import java.io.InputStream;
import java.io.FileNotFoundException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.IOException;
import com.idega.builder.data.IBPage;
import com.idega.exception.PageDoesNotExist;
import com.idega.jmodule.object.Page;

/**
 * A class that reads XML page descriptions from the database and returns
 * the elements/modules/applications it contains.
 *
 * @author <a href="mailto:palli@idega.is">Pall Helgason</a>, <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0alpha
 */

public class IBXMLPage {
  private final static String EMPTY = "";
  private SAXBuilder _builder = null;
  private Document _xmlDocument = null;
  private Element _rootElement = null;
  private Page _populatedPage = null;
  private String _key;

  private String _type = XMLConstants.PAGE_TYPE_PAGE;

  /**
   * @todo Verð að gera þetta öðru vísi
   */
  private Hashtable _children = null;

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
        setType(XMLConstants.PAGE_TYPE_PAGE);
      if (ibpage.getType().equals(ibpage.DRAFT))
        setType(XMLConstants.PAGE_TYPE_DRAFT);
      if (ibpage.getType().equals(ibpage.TEMPLATE))
        setType(XMLConstants.PAGE_TYPE_TEMPLATE);
      else
        setType(XMLConstants.PAGE_TYPE_PAGE);
    }
    catch(PageDoesNotExist pe) {
      int template = ibpage.getTemplateId();
      String templateString = null;
      if (template != -1)
        templateString = Integer.toString(template);
      if (ibpage.getType().equals(ibpage.PAGE))
        setPageAsEmptyPage(XMLConstants.PAGE_TYPE_PAGE,templateString);
      if (ibpage.getType().equals(ibpage.DRAFT))
        setPageAsEmptyPage(XMLConstants.PAGE_TYPE_DRAFT,templateString);
      if (ibpage.getType().equals(ibpage.TEMPLATE))
        setPageAsEmptyPage(XMLConstants.PAGE_TYPE_TEMPLATE,templateString);
      else
        setPageAsEmptyPage(XMLConstants.PAGE_TYPE_PAGE,templateString);
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
  public void addChild(String id) {
    if (_children == null)
      _children = new java.util.Hashtable();

    _children.put(id,EMPTY);
  }

  /**
   *
   */
  public java.util.Map getChildren() {
    //Skítamix
    _children = null;
    if (_children == null)
      findAllChildren();
    return(_children);
  }

  private void findAllChildren() {
    _children = new java.util.Hashtable();
    try {
      String templateId = _key;
      java.util.List l = com.idega.data.EntityFinder.findAllByColumn(new com.idega.builder.data.IBPage(),com.idega.builder.data.IBPage.getColumnTemplateID(),Integer.parseInt(templateId));
      if (l == null)
        return;
      java.util.Iterator i = l.iterator();
      while (i.hasNext()) {
        com.idega.builder.data.IBPage p = (com.idega.builder.data.IBPage)i.next();
        _children.put(Integer.toString(p.getID()),EMPTY);
      }
    }
    catch(java.sql.SQLException e) {}
  }

  private void invalidateChildren() {
    Hashtable h = (Hashtable)getChildren();
    if (h != null) {
      Enumeration e = h.keys();
      while (e.hasMoreElements()) {
        String invalid = (String)e.nextElement();
        PageCacher.flagPageInvalid(invalid);
        IBXMLPage child = PageCacher.getXML(invalid);
        if (child.getType() == XMLConstants.PAGE_TYPE_TEMPLATE)
          child.invalidateChildren();
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
    if (_type == XMLConstants.PAGE_TYPE_TEMPLATE)
      invalidateChildren();

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
      System.err.println("JDOM Exception: " + e.getMessage());
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
      System.err.println("JDOM Exception: " + e.getMessage());
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

    if ((type.equals(XMLConstants.PAGE_TYPE_DRAFT)) ||
        (type.equals(XMLConstants.PAGE_TYPE_PAGE)) ||
        (type.equals(XMLConstants.PAGE_TYPE_TEMPLATE))) {
      pageElement.addAttribute(XMLConstants.PAGE_TYPE,type);
      setType(type);
    }
    else {
      pageElement.addAttribute(XMLConstants.PAGE_TYPE,XMLConstants.PAGE_TYPE_PAGE);
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
    if ((type == XMLConstants.PAGE_TYPE_PAGE) ||
        (type == XMLConstants.PAGE_TYPE_TEMPLATE) ||
        (type == XMLConstants.PAGE_TYPE_DRAFT))
      _type = type;
    else
      _type = XMLConstants.PAGE_TYPE_PAGE;
  }

  /**
   *
   */
  public String getType() {
    return(_type);
  }
}