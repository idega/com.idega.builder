/*
 * $Id: IBXMLPage.java,v 1.2 2001/08/23 18:00:52 tryggvil Exp $
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


  private final static String PAGE_STRING = "page";
  private final static String ROOT_STRING = "xml";

  private IBXMLPage(boolean verify) {
    builder = new SAXBuilder(verify);
  }

  /*public IBXMLPage(boolean verify,InputStream stream)throws PageDoesNotExist{
    this(verify);
    setXMLPageDescriptionFile(stream);
  }*/

  public IBXMLPage(boolean verify,String key){

    this(verify);
    this.key=key;
      try{
        IBPage ibpage = new IBPage(Integer.parseInt(key));
        setXMLPageDescriptionFile(ibpage.getPageValue());
      }
      catch(PageDoesNotExist pe){
        setPageAsEmptyPage();
      }
      catch(NumberFormatException ne){
        try{
          InputStream stream = new FileInputStream(key);
          setXMLPageDescriptionFile(stream);
        }
        catch(FileNotFoundException fnfe){
          fnfe.printStackTrace();
        }
        catch(PageDoesNotExist pe){
          setPageAsEmptyPage();
        }
      }
      catch(Exception e){
        e.printStackTrace();
      }
      setPopulatedPage(XMLReader.getPopulatedPage(this));
  }

  public boolean update(){
      try{
        IBPage ibpage = new IBPage(Integer.parseInt(key));
        OutputStream stream = ibpage.getPageValueForWrite();
        store(stream);
        ibpage.update();
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

  public void setPageAsEmptyPage(){
    System.out.println("setPageAsEmptyPage");
    rootElement = new Element(ROOT_STRING);
    Element pageElement = new Element(PAGE_STRING);
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
      return rootElement.getChild(PAGE_STRING);
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
