/*
 * $Id: IBXMLFragment.java,v 1.2 2001/11/01 17:21:07 palli Exp $
 *
 * Copyright (C) 2001 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package com.idega.builder.business;

//import org.jdom.JDOMException;
import org.jdom.Document;
import org.jdom.Element;
//import org.jdom.Attribute;
import org.jdom.input.SAXBuilder;
//import org.jdom.output.XMLOutputter;
//import java.util.List;
//import java.util.Iterator;
//import java.util.Vector;
import java.io.InputStream;
import java.io.FileNotFoundException;
import java.io.FileInputStream;
//import java.io.FileOutputStream;
//import java.io.OutputStream;
import java.io.IOException;
//import java.io.StringReader;
import com.idega.builder.data.IBObjectLibrary;
//import com.idega.presentation.PresentationObjectContainer;
import com.idega.exception.LibraryDoesNotExist;

/**
 * @author <a href="mailto:palli@idega.is">Pall Helgason</a>
 * @version 1.0
 */
public class IBXMLFragment {
  public final static String FRAGMENT_TYPE_LIBRARY = XMLConstants.FRAGMENT_TYPE_LIBRARY;
  public final static String FRAGMENT_TYPE_CLIPBOARD = XMLConstants.FRAGMENT_TYPE_CLIPBOARD;
  private String _key;
  private String _type = FRAGMENT_TYPE_LIBRARY;
  private SAXBuilder _builder = null;
  private Document _xmlDocument = null;
  private Element _rootElement = null;

/*  public final static String TYPE_DRAFT = XMLConstants.PAGE_TYPE_DRAFT;
  private final static String EMPTY = ""; */

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
    this(verify);
    _key = key;

    IBObjectLibrary lib = null;
    try {
      lib = new IBObjectLibrary(Integer.parseInt(key));
      setXMLLibraryDescriptionFile(lib.getPageValue());
/*      if (ibpage.getType().equals(ibpage.PAGE))
        setType(TYPE_PAGE);
      if (ibpage.getType().equals(ibpage.DRAFT))
        setType(TYPE_DRAFT);
      if (ibpage.getType().equals(ibpage.TEMPLATE))
        setType(TYPE_TEMPLATE);
      else
        setType(TYPE_PAGE);*/
    }
    catch(LibraryDoesNotExist ldne) {

    }
    catch(NumberFormatException ne) {
      try {
        InputStream stream = new FileInputStream(key);
        setXMLLibraryDescriptionFile(stream);
      }
      catch(LibraryDoesNotExist ldne) {

      }
      catch(FileNotFoundException fnfe) {
        fnfe.printStackTrace();
      }
    }
    catch(Exception e) {
      e.printStackTrace();
    }
//    setPopulatedPage(XMLReader.getPopulatedPage(this));  */
  }

  /**
   * @param URI The path to the file containing the XML description of the fragment.
   *
   * @throws com.idega.exception.LibraryDoesNotExists The given XML file does not exists.
   */
  public void setXMLLibraryDescriptionFile(String URI) throws LibraryDoesNotExist {
    try {
      _xmlDocument = _builder.build(URI);
      _rootElement = _xmlDocument.getRootElement();
    }
    catch(org.jdom.JDOMException e) {
      throw new LibraryDoesNotExist();
    }
  }

  /**
   * @param stream Stream to the file containing the XML description of the fragment.
   *
   * @throws com.idega.exception.LibraryDoesNotExists The given XML file does not exists.
   */
  public void setXMLLibraryDescriptionFile(InputStream stream) throws LibraryDoesNotExist {
    boolean streamopen = true;
    try {
      _xmlDocument = _builder.build(stream);
      stream.close();
      _rootElement = _xmlDocument.getRootElement();
      streamopen = false;
    }
    catch(org.jdom.JDOMException e) {
      throw new LibraryDoesNotExist();
    }
    catch(java.io.IOException ioe) {
      ioe.printStackTrace();
    }
    finally {
      if (streamopen) {
        try {
          if (stream != null) {
            stream.close();
          }
        }
        catch(IOException e) {
          e.printStackTrace();
        }
      }
    }
  }
}