/*
 * $Id: IBXMLFragment.java,v 1.3 2001/12/03 16:18:31 palli Exp $
 *
 * Copyright (C) 2001 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package com.idega.builder.business;

import com.idega.xml.XMLException;
import com.idega.xml.XMLDocument;
import com.idega.xml.XMLElement;
import com.idega.xml.XMLParser;
import java.util.List;
import java.io.InputStream;
import java.io.FileNotFoundException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringReader;
import com.idega.builder.data.IBObjectLibrary;
import com.idega.exception.LibraryDoesNotExist;

/**
 * @author <a href="mailto:palli@idega.is">Pall Helgason</a>
 * @version 1.0
 */
public class IBXMLFragment extends IBXMLAbstractContainer { //implements IBXMLAble {
  public final static String FRAGMENT_TYPE_CLIPBOARD = XMLConstants.FRAGMENT_TYPE_CLIPBOARD;
  public final static String FRAGMENT_TYPE_LIBRARY = XMLConstants.FRAGMENT_TYPE_LIBRARY;
  private final static String EMPTY = "";

/*  private String _key;
  private String _type = FRAGMENT_TYPE_LIBRARY;
  private XMLParser _parser = null;
  private XMLDocument _xmlDocument = null;
  private XMLElement _rootElement = null;*/

  /**
   *
   */
  protected IBXMLFragment(boolean verify) {
    _parser = new XMLParser(verify);
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
      if (lib.getType().equals(lib.LIBRARY))
        setType(FRAGMENT_TYPE_LIBRARY);
      else if (lib.getType().equals(lib.CLIPBOARD))
        setType(FRAGMENT_TYPE_CLIPBOARD);
      else
        setType(FRAGMENT_TYPE_LIBRARY);
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
  }

  /**
   * @param URI The path to the file containing the XML description of the fragment.
   *
   * @throws com.idega.exception.LibraryDoesNotExists The given XML file does not exists.
   */
  public void setXMLLibraryDescriptionFile(String URI) throws LibraryDoesNotExist {
    try {
      _xmlDocument = _parser.parse(URI);
      _rootElement = _xmlDocument.getRootElement();
    }
    catch(XMLException e) {
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
      _xmlDocument = _parser.parse(stream);
      stream.close();
      _rootElement = _xmlDocument.getRootElement();
      streamopen = false;
    }
    catch(XMLException e) {
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

  /**
   *
   */
  public void setType(String type) {
    _type = type;
  }

  /**
   *
   */
  public XMLElement getRootElement() {
    return(_rootElement);
  }

  /**
   *
   */
  public XMLElement getPageRootElement() {
    if (_rootElement != null)
      return(_rootElement.getChild(XMLConstants.PAGE_STRING));
    else
      return(null);
  }

  /**
   *
   */
  public List getAttributes(XMLElement element) {
    if (element != null)
      return(element.getAttributes());
    else
      return(null);
  }

  /**
   *
   */
  public void setSourceFromString(String xmlRepresentation) throws Exception {
    StringReader reader = new StringReader(xmlRepresentation);
    XMLParser parser = new XMLParser();
    XMLDocument doc = parser.parse(reader);
    _rootElement = doc.getRootElement();
    _xmlDocument.setRootElement(_rootElement);
    update();
  }

  public synchronized boolean update() {
    return(true);
  }

  public String toString() {
    return(null);
  }
}