/*
 * $Id: JSPDocument.java,v 1.1.2.1 2007/01/12 19:32:17 idegaweb Exp $
 * Created on 8.5.2005
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.builder.jsp;

import java.util.Stack;
import com.idega.xml.XMLAttribute;
import com.idega.xml.XMLDocument;
import com.idega.xml.XMLElement;
import com.idega.xml.XMLNamespace;


/**
 * 
 *  Last modified: $Date: 2007/01/12 19:32:17 $ by $Author: idegaweb $
 * 
 * @author <a href="mailto:gummi@idega.com">Gudmundur Agust Saemundsson</a>
 * @version $Revision: 1.1.2.1 $
 */
public class JSPDocument extends XMLDocument {

	private static XMLNamespace NAMESPACE_JSP = new XMLNamespace("jsp","http://java.sun.com/JSP/Page");
	private static XMLNamespace NAMESPACE_JSF_HTML = new XMLNamespace("h","http://java.sun.com/jsf/html");
	private static XMLNamespace NAMESPACE_JSF_CORE = new XMLNamespace("f","http://java.sun.com/jsf/core");
	
	private Stack elementStack = null;
	private XMLElement currentElement = null;
	
	/**
	 * @param element
	 */
	public JSPDocument() {
		super(new XMLElement("root",NAMESPACE_JSP));
		getRootElement().setAttribute("version","1.2");
	}
	
	public XMLElement getCurrentElement(){
		if(this.currentElement == null){
			this.currentElement = getRootElement();
		}
		return this.currentElement;
	}
	
	private void setCurrentElement(XMLElement element){
		this.currentElement = element;
	}
	
	protected Stack getElementStack(){
		if(this.elementStack==null){
			this.elementStack = new Stack();
		}
		return this.elementStack;
	}
	
	
	public void startElement(String name, XMLNamespace namespace){
		XMLElement element = new XMLElement(name,namespace);
		getRootElement().addNamespaceDeclaration(namespace);
		getCurrentElement().addContent(element);
		getElementStack().push(getCurrentElement());
		setCurrentElement(element);
	}
	
	public void endElement(String name){
		if(!getCurrentElement().getName().equals(name)){
			throw new IllegalStateException("Cannot end '"+name+"' you must end '"+getCurrentElement().getName()+"' first.");
		}
		setCurrentElement((XMLElement) getElementStack().pop());
	}
	
	public void setAttribute(String name, String value){
		getCurrentElement().setAttribute(name,value);
	}
	
	public void setAttribute(XMLAttribute attribute){
		getCurrentElement().setAttribute(attribute);
	}
	
	

	/**
	 * @return Returns the JSF Core Namespace.
	 */
	public XMLNamespace getJsfCoreNamespace() {
		return NAMESPACE_JSF_CORE;
	}
	/**
	 * @return Returns the JSF HTML namespace.
	 */
	public XMLNamespace getJsfHtmlNamespace() {
		return NAMESPACE_JSF_HTML;
	}
	/**
	 * @return Returns the JSP namespace.
	 */
	public XMLNamespace getJspNamespace() {
		return NAMESPACE_JSP;
	}
}
