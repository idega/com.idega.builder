/*
 * $Id: IBXMLFragment.java,v 1.1 2001/10/30 17:41:40 palli Exp $
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
import com.idega.builder.data.IBObjectLibrary;
import com.idega.presentation.PresentationObjectContainer;

/**
 * @author <a href="mailto:palli@idega.is">Pall Helgason</a>
 * @version 1.0
 */
public class IBXMLFragment {
/*  public final static String TYPE_PAGE = XMLConstants.PAGE_TYPE_PAGE;
  public final static String TYPE_TEMPLATE = XMLConstants.PAGE_TYPE_TEMPLATE;
  public final static String TYPE_DRAFT = XMLConstants.PAGE_TYPE_DRAFT;
  private final static String EMPTY = "";
  private SAXBuilder _builder = null;
  private Document _xmlDocument = null;
  private Element _rootElement = null;
  private Page _populatedPage = null;
  private String _key;
  private IBPage _ibPage;

  private String _type = TYPE_PAGE;

  private List _usingTemplate = null;*/

  private SAXBuilder _builder = null;
//  private Document _xmlDocument = null;
  private Element _rootElement = null;

  /*
   *
   */
  private IBXMLFragment(boolean verify) {
    _builder = new SAXBuilder(verify);
  }

  /**
   *
   */
  public IBXMLFragment(boolean verify, String key) {
/*    this(verify);
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
    setPopulatedPage(XMLReader.getPopulatedPage(this));  */
  }
}