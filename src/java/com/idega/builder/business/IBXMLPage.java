/*
 * $Id: IBXMLPage.java,v 1.5 2001/09/19 01:06:19 palli Exp $
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
  private SAXBuilder builder = null;
  private Document xmlDocument = null;
  private Element rootElement = null;
  private Page populatedPage = null;
  private String key;

  /**
   * @todo Verð að gera þetta öðru vísi (á morgun).
   */
  private java.util.Hashtable _children = null;


  private IBXMLPage(boolean verify) {
    builder = new SAXBuilder(verify);
  }

  /*public IBXMLPage(boolean verify,InputStream stream)throws PageDoesNotExist{
    this(verify);
    setXMLPageDescriptionFile(stream);
  }*/

  public IBXMLPage(boolean verify, String key) {
    this(verify);
    this.key = key;

    IBPage ibpage = null;
      try{
        ibpage = new IBPage(Integer.parseInt(key));
        setXMLPageDescriptionFile(ibpage.getPageValue());
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
        try{
          InputStream stream = new FileInputStream(key);
          setXMLPageDescriptionFile(stream);
        }
        catch(FileNotFoundException fnfe){
          fnfe.printStackTrace();
        }
        catch(PageDoesNotExist pe){
          setPageAsEmptyPage(null,null);
        }
      }
      catch(Exception e){
        e.printStackTrace();
      }
      setPopulatedPage(XMLReader.getPopulatedPage(this));
  }

  public void addChild(String id) {
    if (_children == null)
      _children = new java.util.Hashtable();

    _children.put(id,null);
  }

  public java.util.Map getChildren() {
System.out.println("Getting children");
    if (_children == null)
      findAllChildren();
    return(_children);
  }

  private void findAllChildren() {
    _children = new java.util.Hashtable();
    try {
      String templateId = key;
      java.util.List l = com.idega.data.EntityFinder.findAllByColumn(new com.idega.builder.data.IBPage(),com.idega.builder.data.IBPage.getColumnTemplateID(),Integer.parseInt(templateId));
      java.util.Iterator i = l.iterator();
      while (i.hasNext()) {
        com.idega.builder.data.IBPage p = (com.idega.builder.data.IBPage)i.next();
        System.out.println("id = " + p.getID());
        _children.put(Integer.toString(p.getID()),new String(""));
      }
    }
    catch(java.sql.SQLException e) {}
  }


  public boolean update(){
      try{
        IBPage ibpage = new IBPage(Integer.parseInt(key));
        OutputStream stream = ibpage.getPageValueForWrite();
        store(stream);
        ibpage.update();
System.out.println("Getting children!!!");
        java.util.Hashtable h = (java.util.Hashtable)getChildren();
        if (h != null) {
          java.util.Enumeration e = h.keys();
          while (e.hasMoreElements()) {
            PageCacher.flagPageInvalid((String)e.nextElement());
          }
        }
        //ibpage.setPageValue(xmlDocument.);
        //setXMLPageDescriptionFile(ibpage.getPageValue());
      }
      catch(NumberFormatException ne){
        try{
          //InputStream stream = new FileInputStream(key);
          //setXMLPageDescriptionFile(stream);
          OutputStream stream = new FileOutputStream(key);
          store(stream);
        }
        catch(FileNotFoundException fnfe){
          fnfe.printStackTrace();
        }
      }
      catch(Exception e){
        e.printStackTrace();
      }
      setPopulatedPage(XMLReader.getPopulatedPage(this));
      return true;
  }

  private void store(OutputStream stream) {
    try {
      XMLOutputter outputter = new XMLOutputter("  ",true);
      outputter.setLineSeparator(System.getProperty("line.separator"));
      outputter.setTrimText(true);
      outputter.output(xmlDocument,stream);
      stream.close();
    }
    catch(IOException e) {
      e.printStackTrace();
    }
  }

  public void setPopulatedPage(Page page){
    populatedPage=page;
  }

  public Page getPopulatedPage(){
    return populatedPage;
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
      xmlDocument = builder.build(URI);
      rootElement = xmlDocument.getRootElement();
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
      xmlDocument = builder.build(stream);
      stream.close();
      rootElement = xmlDocument.getRootElement();
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
          System.out.println("Closing page inputstream");
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
    System.out.println("setPageAsEmptyPage");
    rootElement = new Element(XMLConstants.ROOT_STRING);
    Element pageElement = new Element(XMLConstants.PAGE_STRING);

    if (type == null)
      type = XMLConstants.PAGE_TYPE_PAGE;

    if ((type.equals(XMLConstants.PAGE_TYPE_DRAFT)) ||
        (type.equals(XMLConstants.PAGE_TYPE_PAGE)) ||
        (type.equals(XMLConstants.PAGE_TYPE_TEMPLATE)))
      pageElement.addAttribute(XMLConstants.PAGE_TYPE,type);
    else
      pageElement.addAttribute(XMLConstants.PAGE_TYPE,XMLConstants.PAGE_TYPE_PAGE);

    if (template != null)
      pageElement.addAttribute(XMLConstants.TEMPLATE_STRING,template);


    rootElement.addContent(pageElement);
    xmlDocument = new Document(rootElement);
    setPopulatedPage(XMLReader.getPopulatedPage(this));
  }

  /**
   * A function that returns the root element for the given page description file.
   *
   * @return The root element. Null if the page description file is not set.
   * @todo Wrap the Element class to hide all implementation of the XML parser.
   */
  Element getRootElement() {
    return(rootElement);
  }

  Element getPageRootElement(){
    if(rootElement!=null){
      return rootElement.getChild(XMLConstants.PAGE_STRING);
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
}

/*      SAXParserFactory f = SAXParserFactory.newInstance();
      f.setValidating(true);
      SAXParser p = f.newSAXParser();
      Parser p1 = p.getParser();
      p1.parse();*/
//      System.out.println("Document = " + doc.toString());
