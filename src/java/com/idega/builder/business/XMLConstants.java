/*
 * $Id: XMLConstants.java,v 1.21 2004/04/27 14:57:40 thomas Exp $
 *
 * Copyright (C) 2001 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package com.idega.builder.business;

/**
 * @author <a href="mailto:palli@idega.is">Pall Helgason</a>
 * @version 1.0
 */
public abstract class XMLConstants {
  public static final String ROOT_STRING = "xml";
  public static final String PAGE_STRING = "page";
  public static final String FRAGMENT_STRING = "fragment";
  public static final String ID_STRING = "id";
  public static final String IC_OBJECT_ID_STRING = "ic_object_id";
  public static final String METHOD_STRING = ":method";
  public static final String TEMPLATE_STRING = "template";
  public static final String REGION_STRING = "region";
  public static final String PROPERTY_STRING = "property";
  public static final String VALUE_STRING = "value";
  public static final String TYPE_STRING = "type";
  public static final String NAME_STRING = "name";
  public static final String MODULE_STRING = "module";
  public static final String ELEMENT_STRING = "element";
  public static final String X_REGION_STRING = "x";
  public static final String Y_REGION_STRING = "y";
  public static final String DOT_REGION_STRING = ".";  // e.g. <region id="7.1.1" label="main">
  public static final String CLASS_STRING = "class";
  public static final String PAGE_TYPE = "type";
  public static final String REGION_LOCKED = "locked";
  public static final String LABEL_STRING = "label";
  public static final String CHANGE_IC_INSTANCE_ID = "changeid";
  public static final String IC_INSTANCE_ID_FROM = "from";
  public static final String IC_INSTANCE_ID_TO = "to";
  public static final String CHANGE_PAGE_LINK = "changelink";
  public static final String LINK_ID_STRING = "id";
  public static final String LINK_TO = "to";

  public static final String PAGE_TYPE_PAGE = "page";
  public static final String PAGE_TYPE_DRAFT = "draft";
  public static final String PAGE_TYPE_TEMPLATE = "template";
  public static final String PAGE_TYPE_DPT_TEMPLATE = "dpt_template";
  public static final String PAGE_TYPE_DPT_PAGE = "dpt_page";

  public static final String FRAGMENT_TYPE_LIBRARY = "library";
  public static final String FRAGMENT_TYPE_CLIPBOARD = "clipboard";
  
  // for exporting, export definition file
  public static final String EXPORT_MODULE = "module";
  public static final String EXPORT_MODULE_CLASS = "class";
  public static final String EXPORT_PROPERTY = "property";
  public static final String EXPORT_PROPERTY_NAME = "name";
  public static final String EXPORT_SOURCE = "source";
  public static final String EXPORT_PROVIDER = "provider";
  public static final String EXPORT_PROVIDER_EJB = "ejb";
  public static final String EXPORT_PROVIDER_CLASS = "class";
  
  // for metadata in zip file
  public static final String FILE_FILES = "files";
  public static final String FILE_FILE = "file";
  public static final String FILE_MODULE = "module";
  public static final String FILE_SOURCE = "source";
  public static final String FILE_NAME = "name";
  public static final String FILE_VALUE = "value";
  public static final String FILE_USED_ID = "identifier";
  public static final String FILE_ORIGINAL_NAME = "original_name";
  public static final String FILE_MIME_TYPE = "mime_type";
  
  // for page tree within metadata in zip file 
  public static final String PAGE_TREE_PAGES = "pages";
  public static final String PAGE_TREE_TEMPLATES = "templates";
  public static final String PAGE_TREE_PAGE = "page";
  public static final String PAGE_TREE_NAME = "name";
  public static final String PAGE_TREE_ID = "id";
  
  // for necessary modules within metadata in zip file
  public static final String MODULE_MODULES = "modules";
  public static final String MODULE_MODULE = "module";
  public static final String MODULE_CLASS = "class";
  public static final String MODULE_BUNDLE = "bundle";
  public static final String MODULE_TYPE = "type";
}
