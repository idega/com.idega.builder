/*
 * $Id: IBXMLAble.java,v 1.1 2001/12/03 16:18:31 palli Exp $
 *
 * Copyright (C) 2001 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package com.idega.builder.business;

import java.util.List;
import com.idega.xml.XMLElement;

/**
 * @author <a href="mail:palli@idega.is">Pall Helgason</a>
 * @version 1.0
 */
public interface IBXMLAble {
  public void setSourceFromString(String xmlRepresentation) throws Exception;
  public String toString();
  public List getAttributes(XMLElement element);
  public XMLElement getPageRootElement();
  public XMLElement getRootElement();
  public void setType(String type);
}