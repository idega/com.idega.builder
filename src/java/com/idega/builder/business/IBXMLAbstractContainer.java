/*
 * $Id: IBXMLAbstractContainer.java,v 1.2 2002/04/06 19:07:38 tryggvil Exp $
 *
 * Copyright (C) 2001 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package com.idega.builder.business;

import java.util.List;
import com.idega.xml.XMLParser;
import com.idega.xml.XMLDocument;
import com.idega.xml.XMLElement;

/**
 * @author <a href="mail:palli@idega.is">Pall Helgason</a>
 * @version 1.0
 */
public abstract class IBXMLAbstractContainer implements IBXMLAble {
  protected String _key;
  protected String _type = null;
  protected XMLParser _parser = null;
  protected XMLDocument _xmlDocument = null;
  protected XMLElement _rootElement = null;

  protected IBXMLAbstractContainer() {
    this(false);
  }

  protected IBXMLAbstractContainer(boolean verify) {
    _parser = new XMLParser(verify);
  }

  public void setType(String type) {
    _type = type;
  }

  /**
   * @deprecated Use the getAttributes function in XMLElement instead
   */
  public List getAttributes(XMLElement element) {
    if (element != null)
      return(element.getAttributes());
    else
      return(null);
  }

  public abstract String toString();
  public abstract void setSourceFromString(String xmlRepresentation) throws Exception;
  public abstract XMLElement getPageRootElement();
  public abstract XMLElement getRootElement();
}
