/*
 * $Id: SimpleTemplateCreationManagedBean.java,v 1.2 2005/05/14 14:32:37 laddi Exp $
 * Created on 4.5.2005
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.builder.bean;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.ejb.EJBException;
import javax.ejb.FinderException;
import javax.faces.component.UICommand;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ActionListener;
import javax.faces.model.SelectItem;
import com.idega.builder.business.BuilderLogic;
import com.idega.builder.business.IBPageHelper;
import com.idega.builder.business.PageTreeNode;
import com.idega.builder.jsp.JSPDocument;
import com.idega.core.builder.data.ICPage;
import com.idega.core.builder.data.ICPageHome;
import com.idega.core.component.data.ICObject;
import com.idega.core.component.data.ICObjectBMPBean;
import com.idega.core.component.data.ICObjectHome;
import com.idega.data.IDOLookup;
import com.idega.data.IDOLookupException;
import com.idega.exception.IWBundleDoesNotExist;
import com.idega.presentation.IWContext;
import com.idega.xml.XMLNamespace;
import com.idega.xml.XMLOutput;


/**
 * 
 *  Last modified: $Date: 2005/05/14 14:32:37 $ by $Author: laddi $
 * 
 * @author <a href="mailto:gummi@idega.com">Gudmundur Agust Saemundsson</a>
 * @version $Revision: 1.2 $
 */
public class SimpleTemplateCreationManagedBean implements ActionListener {


	private static String NEW_SIMPLE_TEMPLATE_SELECT_ITEM_VALUE = "ib_new_simple_template";
	private static String NO_COMPONENT_SELECTED = "no_component_selected";
	
	private String simpleTemplateName = "Untitled";
	private String simpleTemplateIdentifier = NEW_SIMPLE_TEMPLATE_SELECT_ITEM_VALUE;
	private String parentTemplateIdentifier = null;
	private String selectedRegion = null;
	private String selectedComponent = null;
	private ICPage parentICPage = null;
	private ICPage currentICPage = null;
	
	/**
	 * 
	 */
	public SimpleTemplateCreationManagedBean() {
		super();
	}

	/* (non-Javadoc)
	 * @see javax.faces.event.ActionListener#processAction(javax.faces.event.ActionEvent)
	 */
	public void processAction(ActionEvent actionEvent) throws AbortProcessingException {
		IWContext iwc = IWContext.getInstance();
		UICommand command = (UICommand)actionEvent.getComponent();
		System.out.println("UICommand.action:"+command.getAction());
		System.out.println("UICommand.value:"+command.getValue());
		System.out.println("UICommand.attribute.action:"+command.getAttributes().get("action"));
		System.out.println("UICommand.action.expressionString:"+command.getAction().getExpressionString());
		
		if("saveCommand".equals(command.getId())){
			try {
				String stringSourceMarkup = getPageSource();

				System.out.println("---------JSP Page----------");
				System.out.println(stringSourceMarkup);
				System.out.println("---------JSP Page Ends----------");

				
				if(NEW_SIMPLE_TEMPLATE_SELECT_ITEM_VALUE.equals(getSimpleTemplateIdentifier())){
					//Create new page
					String id = createSimpleTemplate(iwc, getParentTemplateIdentifier(), getSimpleTemplateName(), getParentTemplateIdentifier());
					setSimpleTemplateIdentifier(id);
				} 
//				else {
//					ICPage tPage = getSimpleTemplateICPage();
//					if(getSimpleTemplateName()!=null && !getSimpleTemplateName().equals(tPage.getName())){
//						tPage.setName(getSimpleTemplateName());
//						tPage.store();
//						getBuilderLogic().getPageCacher().flagPageInvalid(getSimpleTemplateIdentifier());
//					}
//				}
	
				try {
					if(getSimpleTemplateIdentifier() != null){
						getBuilderLogic().getPageCacher().storePage(getSimpleTemplateIdentifier(),getBuilderLogic().PAGE_FORMAT_JSP_1_2,stringSourceMarkup);
					} else {
						throw new AbortProcessingException("Page identifier is null. Most likely explaination is that creating new page failed");
					}
				}
				catch (Exception e) {
					if(e instanceof AbortProcessingException){
						throw (AbortProcessingException)e;
					} else {
						throw new AbortProcessingException(e);
					}
				}
			}
			catch (IOException e) {
				throw new AbortProcessingException(e);
			}
		}

		
	}
	
	
	/**
	 * @return
	 * @throws IOException
	 */
	private String getPageSource() throws IOException {
		
		boolean addFormElement = true;
		
		XMLNamespace builderNamespace = new XMLNamespace("b","http://xmlns.idega.com/com.idega.builder");
		
		JSPDocument jspDoc = new JSPDocument();	
		
		//jsf view 
		jspDoc.startElement("view",jspDoc.getJsfCoreNamespace());
		
		//page element
		jspDoc.startElement("page",builderNamespace);
		jspDoc.setAttribute("type","page");
		jspDoc.setAttribute("template",getParentTemplateIdentifier());
		jspDoc.setAttribute("locked","false");
		
		//region element
		jspDoc.startElement("region",builderNamespace);
		jspDoc.setAttribute("label","main");
		

		if(addFormElement){
			//form element
			jspDoc.startElement("form",jspDoc.getJsfHtmlNamespace());
			jspDoc.setAttribute("id","form");
		}
		
		
		//Add component
		if(!NO_COMPONENT_SELECTED.equals(getSelectedComponent())){
			jspDoc.startElement("module",builderNamespace);
			jspDoc.setAttribute("componentClass",getComponentClassName());
			jspDoc.endElement("module");
		}
		
		if(addFormElement){
			jspDoc.endElement("form");
		}
		jspDoc.endElement("region");
		jspDoc.endElement("page");
		jspDoc.endElement("view");
			
		XMLOutput output = new XMLOutput();
		output.setLineSeparator(System.getProperty("line.separator"));
		output.setTextNormalize(true);
		output.setEncoding("UTF-8");
		return output.outputString(jspDoc);
	}
	
	private String getComponentClassName(){
		try {
			ICObjectHome home = (ICObjectHome) IDOLookup.getHome(ICObject.class);
			ICObject obj = home.findByPrimaryKey(getSelectedComponent());
			return obj.getClassName();
		}
		catch (IDOLookupException e) {
			e.printStackTrace();
		}
		catch (FinderException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public String getParentTemplateFormat(){
		ICPage parentICPage = getParentTemplateICPage();
		if(parentICPage!=null){
			return parentICPage.getFormat();
		}
		return null;
	}

	public ICPage getParentTemplateICPage(){
		String parentIdentifier = getParentTemplateIdentifier();
		if(parentICPage == null){
			try {
				parentICPage = ((ICPageHome) IDOLookup.getHome(ICPage.class)).findByPrimaryKey(parentIdentifier);
			}
			catch (IDOLookupException e) {
				e.printStackTrace();
			}
			catch (FinderException e) {
				e.printStackTrace();
			}
		}
		return parentICPage;
	}
	
	public ICPage getSimpleTemplateICPage(){
		String stIdentifier = getSimpleTemplateIdentifier();
		if((currentICPage == null && stIdentifier != null) || (currentICPage != null && !String.valueOf(currentICPage.getPrimaryKey()).equals(stIdentifier))){
			try {
				currentICPage = ((ICPageHome) IDOLookup.getHome(ICPage.class)).findByPrimaryKey(stIdentifier);
			}
			catch (IDOLookupException e) {
				e.printStackTrace();
			}
			catch (FinderException e) {
				e.printStackTrace();
			}
		}
		return currentICPage;
	}
	
	public PageTreeNode getParentTemplatePageTreeNode(){
		IWContext iwc = IWContext.getInstance();
		Map m = PageTreeNode.getTree(iwc);
		return (PageTreeNode) m.get(new Integer(getParentTemplateIdentifier()));
	}
	
	public List getPageRegions(IWContext iwc){
		/*String parentIdentifier = */getParentTemplateIdentifier();

		ICPage parentICPage = getParentTemplateICPage();
		if(parentICPage != null){
		
			String parentPageFormat = parentICPage.getFormat();
			if(getBuilderLogic().PAGE_FORMAT_HTML.equals(parentPageFormat)){
				//Parent is HTML template
				//Page parentPage = getBuilderLogic().getPage(parentIdentifier,iwc);
				
			} else if(getBuilderLogic().PAGE_FORMAT_IBXML.equals(parentPageFormat)){
				//Parent is IBXML template
			} else if(getBuilderLogic().PAGE_FORMAT_JSP_1_2.equals(parentPageFormat)){
				//Parent is JSP 1.2 template
			}
			
		}

		return null;
	}
	
	protected BuilderLogic getBuilderLogic(){
		return BuilderLogic.getInstance();
	}
	
	public List getComponents(){
		return null;
	}
	


	private String createSimpleTemplate(IWContext iwc, String parentPageId, String name, String templateKey) {
		int id=-1;
		if (parentPageId != null) {
			Map tree = PageTreeNode.getTree(iwc);
			id = IBPageHelper.getInstance().createNewPage(parentPageId, name, IBPageHelper.TEMPLATE, templateKey, tree, iwc,IBPageHelper.SUBTYPE_SIMPLE_TEMPLATE);
		}
		if(id != -1){
			return String.valueOf(id);
		}
		return null;
	}

	
	public List getTemplateSelectItemList(){
		IWContext iwc = IWContext.getInstance();
		SelectItem newItem = new SelectItem(NEW_SIMPLE_TEMPLATE_SELECT_ITEM_VALUE,"New");
		List l = new ArrayList();
		l.add(newItem);
		PageTreeNode pNode = getParentTemplatePageTreeNode();
		if(pNode != null){
			for (Iterator iter = pNode.getChildrenIterator(); iter.hasNext();) {
				PageTreeNode sibling = (PageTreeNode) iter.next();
				l.add(new SelectItem(String.valueOf(sibling.getNodeID()),sibling.getNodeName(iwc.getCurrentLocale(),iwc)));
			}
		}
		
		return l;
	}

	
	public List getRegionSelectItemList(){
		
		
		return null;
	}
	
	public List getComponentSelectItemList(){
		List l = new ArrayList();
		l.add(new SelectItem(NO_COMPONENT_SELECTED,"[Select component]"));
		try {
			IWContext iwc = IWContext.getInstance();
			Collection c = getAllComponents();
			for (Iterator iter = c.iterator(); iter.hasNext();) {
				try {
					ICObject item = (ICObject) iter.next();
					l.add(new SelectItem(String.valueOf(item.getPrimaryKey()),item.getBundle(iwc.getIWMainApplication()).getComponentName(item.getClassName(), iwc.getCurrentLocale())));
				}
				catch (EJBException e1) {
					e1.printStackTrace();
				}
				catch (IWBundleDoesNotExist e1) {
//					e1.printStackTrace();
				}
			}
		}
		catch (IDOLookupException e) {
			e.printStackTrace();
		}
		catch (FinderException e) {
			e.printStackTrace();
		}
		
		return l;
	}
	
	private Collection getAllComponents()throws FinderException,IDOLookupException{
		ICObjectHome home = (ICObjectHome) IDOLookup.getHome(ICObject.class);
		return home.findAllByObjectType(ICObjectBMPBean.COMPONENT_TYPE_BLOCK);
	}
	
	
	/**
	 * @return Returns the parentTemplateIdentifier.
	 */
	public String getParentTemplateIdentifier() {
		if(parentTemplateIdentifier==null){
			IWContext iwc = IWContext.getInstance();
			parentTemplateIdentifier = getBuilderLogic().getCurrentIBPage(iwc);
		}
		return parentTemplateIdentifier;
	}
	/**
	 * @param parentTemplateIdentifier The parentTemplateIdentifier to set.
	 */
	public void setParentTemplateIdentifier(String parentTemplateIdentifier) {
		this.parentTemplateIdentifier = parentTemplateIdentifier;
	}
	/**
	 * @return Returns the selectedComponent.
	 */
	public String getSelectedComponent() {
		return selectedComponent;
	}
	/**
	 * @param selectedComponent The selectedComponent to set.
	 */
	public void setSelectedComponent(String selectedComponent) {
		this.selectedComponent = selectedComponent;
	}
	/**
	 * @return Returns the selectedRegion.
	 */
	public String getSelectedRegion() {
		return selectedRegion;
	}
	/**
	 * @param selectedRegion The selectedRegion to set.
	 */
	public void setSelectedRegion(String selectedRegion) {
		this.selectedRegion = selectedRegion;
	}
	/**
	 * @return Returns the simpleTemplateIdentifier.
	 */
	public String getSimpleTemplateIdentifier() {
		return simpleTemplateIdentifier;
	}
	/**
	 * @param simpleTemplateIdentifier The simpleTemplateIdentifier to set.
	 */
	public void setSimpleTemplateIdentifier(String simpleTemplateIdentifier) {
		this.simpleTemplateIdentifier = simpleTemplateIdentifier;
	}
	/**
	 * @return Returns the simpleTemplateName.
	 */
	public String getSimpleTemplateName() {
		return simpleTemplateName;
	}
	/**
	 * @param simpleTemplateName The simpleTemplateName to set.
	 */
	public void setSimpleTemplateName(String simpleTemplateName) {
		this.simpleTemplateName = simpleTemplateName;
	}
}
