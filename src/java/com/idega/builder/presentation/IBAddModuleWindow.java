/*
 * $Id: IBAddModuleWindow.java,v 1.46 2006/01/15 19:29:33 laddi Exp $
 *
 * Copyright (C) 2001 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package com.idega.builder.presentation;

import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import com.idega.builder.business.BuilderConstants;
import com.idega.builder.business.BuilderLogic;
import com.idega.core.component.data.ICObject;
import com.idega.core.localisation.business.ICLocaleBusiness;
import com.idega.core.localisation.data.ICLocale;
import com.idega.data.EntityFinder;
import com.idega.idegaweb.IWConstants;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.IWContext;
import com.idega.presentation.Image;
import com.idega.presentation.Table;
import com.idega.presentation.text.Link;
import com.idega.presentation.text.Text;
import com.idega.presentation.ui.Form;
import com.idega.presentation.ui.Parameter;
import com.idega.presentation.ui.Window;

/**
 * @author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */

public class IBAddModuleWindow extends IBAdminWindow {
	private static final String IC_OBJECT_INSTANCE_ID_PARAMETER = BuilderLogic.IC_OBJECT_INSTANCE_ID_PARAMETER;
	private static final String IB_PARENT_PARAMETER = BuilderLogic.IB_PARENT_PARAMETER;
	private static final String IB_PAGE_PARAMETER = BuilderConstants.IB_PAGE_PARAMETER;
	private static final String IB_LABEL_PARAMETER = BuilderLogic.IB_LABEL_PARAMETER;
	private static final String IB_CONTROL_PARAMETER = BuilderLogic.IB_CONTROL_PARAMETER;
	private static final String ACTION_EDIT = BuilderLogic.ACTION_EDIT;
	private static final String ACTION_ADD = BuilderLogic.ACTION_ADD;
	private static final String IW_BUNDLE_IDENTIFIER = BuilderLogic.IW_BUNDLE_IDENTIFIER;
	private static final String INTERNAL_CONTROL_PARAMETER = "ib_adminwindow_par";

	public static final String ELEMENT_LIST = "element_list";
	public static final String BLOCK_LIST = "block_list";
	final static String STYLE_NAME = "add_module";
	
	private Image elementImage;
	private Image blockImage;
	
	//private Map bundles;
	//private List failedBundles;
	
	//Image button;

	public IBAddModuleWindow() {
		setWidth(400);
		setHeight(400);
		setResizable(true);
		setScrollbar(true);
		//failedBundles = new ArrayList();
		//bundles = new HashMap();
	}

	/**
	 *
	 */
	public void main(IWContext iwc) throws Exception {
		IWResourceBundle iwrb = iwc.getIWMainApplication().getBundle(BuilderLogic.IW_BUNDLE_IDENTIFIER).getResourceBundle(iwc);
		super.addTitle(iwrb.getLocalizedString("ib_addmodule_window", "Add a new Module"), IWConstants.BUILDER_FONT_STYLE_TITLE);
		setStyles();
		//button = iwc.getApplication().getBundle(BuilderLogic.IW_BUNDLE_IDENTIFIER).getImage("shared/properties/button.gif");

		/*String action = iwc.getParameter(IB_CONTROL_PARAMETER);
		if (action.equals(ACTION_ADD)) {*/
			addNewObject(iwc);
		//}
	}

	/**
	 *
	 */
	public void addNewObject(IWContext iwc) throws Exception {
		String ib_parent_id = (String) iwc.getSessionAttribute(IB_PARENT_PARAMETER);
		if (ib_parent_id == null) {
			ib_parent_id = iwc.getParameter(IB_PARENT_PARAMETER);
			if(ib_parent_id!=null) {
				iwc.setSessionAttribute(IB_PARENT_PARAMETER, ib_parent_id);
			}
		}
		
		String ib_page_id = (String) iwc.getSessionAttribute(IB_PAGE_PARAMETER);
		if (ib_page_id == null) {
			ib_page_id = iwc.getParameter(IB_PAGE_PARAMETER);
			if(ib_page_id!=null) {
				iwc.setSessionAttribute(IB_PAGE_PARAMETER, ib_page_id);
			}
		}
		
		String label = (String) iwc.getSessionAttribute(IB_LABEL_PARAMETER);
		if (label == null) {
			label = iwc.getParameter(IB_LABEL_PARAMETER);
			if( label!=null ) {
				iwc.setSessionAttribute(IB_LABEL_PARAMETER, label);
			}
			else {
				System.err.println("IBAddModuleWindow: Label is null  "+ IB_LABEL_PARAMETER);
			}
		}
	
		if (hasSubmitted(iwc)) {
			Window window = this;
			window.setParentToReload();
			String ic_object_id = iwc.getParameter(IC_OBJECT_INSTANCE_ID_PARAMETER);
			BuilderLogic.getInstance().addNewModule(ib_page_id, ib_parent_id, Integer.parseInt(ic_object_id), label);
			iwc.removeSessionAttribute(IB_PARENT_PARAMETER);
			iwc.removeSessionAttribute(IB_PAGE_PARAMETER);
			iwc.removeSessionAttribute(IB_LABEL_PARAMETER);
			window.close();
		}
		else {
			/*Form form = getForm();
			add(form);
			Table table = new Table(1, 2);
			table.setBorder(0);
			form.add(getComponentList(iwc));*/
			add(getComponentList(iwc));
	
			/*if (ib_parent_id == null) {
				System.out.println("ib_parent_id==null");
			}
			else {
				form.add(new Parameter(IB_PARENT_PARAMETER, ib_parent_id));
			}
			if (ib_page_id == null) {
				System.out.println("ib_page_id==null");
			}
			else {
				form.add(new Parameter(IB_PAGE_PARAMETER, ib_page_id));
			}
			if (control == null) {
				System.out.println("control==null");
			}
			else {
				form.add(new Parameter(IB_CONTROL_PARAMETER, control));
			}
			if (label != null) {
				form.add(new Parameter(IB_LABEL_PARAMETER, label));
			}*/
		}
	}

	/**
	 *
	 */
	private Form getForm() {
		Form form = new Form();
		form.add(new Parameter(INTERNAL_CONTROL_PARAMETER, "submit"));
		return (form);
	}

	/**
	 *
	 */
	private boolean hasSubmitted(IWContext iwc) {
		return (iwc.isParameterSet(INTERNAL_CONTROL_PARAMETER));
	}

	/**
	 *
	 */
	private Table getComponentList(IWContext iwc) {
		IWResourceBundle iwrb = getBundle(iwc).getResourceBundle(iwc);
		Table theReturn = new Table();
		theReturn.setWidth("100%");
		theReturn.setHeight("100%");
		theReturn.setCellpadding(0);
		theReturn.setCellspacing(1);
		theReturn.setWidth(1, "50%");
		theReturn.setWidth(2, "50%");
		theReturn.setColor(1, 1, "#ECECEC");
		theReturn.setColor(2, 1, "#ECECEC");

		ICObject staticICO = (ICObject) com.idega.core.component.data.ICObjectBMPBean.getStaticInstance(ICObject.class);
		try {
			List elements = null;
			List blocks = null;

			try {
				elements = (List) iwc.getApplicationAttribute(ELEMENT_LIST + "_" + iwc.getCurrentLocaleId());
				blocks = (List) iwc.getApplicationAttribute(BLOCK_LIST + "_" + iwc.getCurrentLocaleId());
			}
			catch (Exception e) {
				elements = null;
				blocks = null;
			}

			if (elements == null && blocks == null) {
				elements = EntityFinder.findAllByColumnOrdered(staticICO, com.idega.core.component.data.ICObjectBMPBean.getObjectTypeColumnName(), com.idega.core.component.data.ICObjectBMPBean.COMPONENT_TYPE_ELEMENT, "OBJECT_NAME");
				blocks = EntityFinder.findAllByColumnOrdered(staticICO, com.idega.core.component.data.ICObjectBMPBean.getObjectTypeColumnName(), com.idega.core.component.data.ICObjectBMPBean.COMPONENT_TYPE_BLOCK, "OBJECT_NAME");

				/*ModuleComparator comparator = new ModuleComparator(iwc);
				if (elements != null) {
					java.util.Collections.sort(elements,comparator );
				}
				System.out.println("Sorting elements: " + (System.currentTimeMillis() - time) + " ms");
				time = System.currentTimeMillis();
				if (blocks != null) {
					java.util.Collections.sort(blocks,comparator);
				}
				System.out.println("Sorting blocks: " + (System.currentTimeMillis() - time) + " ms");*/
				iwc.setApplicationAttribute(ELEMENT_LIST + "_" + iwc.getCurrentLocaleId(), elements);
				iwc.setApplicationAttribute(BLOCK_LIST + "_" + iwc.getCurrentLocaleId(), blocks);
				
				/*failedBundles = comparator.getFailedBundles();
				bundles = comparator.getBundles();*/
				
			}

			String sElements = iwrb.getLocalizedString("elements_header", "Elements");
			String sBlocks = iwrb.getLocalizedString("blocks_header", "Blocks");

			addSubComponentList(sElements, elements, theReturn, 1, 1, iwc);
			addSubComponentList(sBlocks, blocks, theReturn, 1, 2, iwc);

			theReturn.setColumnVerticalAlignment(1, "top");
			theReturn.setColumnVerticalAlignment(2, "top");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return (theReturn);
	}

	/**
	 *
	 */
	private void addSubComponentList(String name, List list, Table table, int ypos, int xpos, IWContext iwc) {
		//TODO set the final size
		Table subComponentTable = new Table();
		
		table.add(subComponentTable, xpos, ypos);
		
		//IWMainApplication iwma = iwc.getIWMainApplication();
		//Locale currentLocale = iwc.getCurrentLocale();

		Text header = new Text(name, true, false, false);
		header.setFontSize(Text.FONT_SIZE_12_HTML_3);
		subComponentTable.add(header, 1, ypos);
		subComponentTable.mergeCells(1, ypos, 2, ypos);
		if (list != null) {
			Iterator iter = list.iterator();
			ICObject item;
			//Link iconLink;
			Image iconLink;
			Link link;
			
			ypos++;
			while (iter.hasNext()) {
				item = (ICObject) iter.next();
				try{
					//iconLink = new Link(getIconForObject(item, iwc));
					iconLink = (Image) getIconForObject(item, iwc).clone();
					
					//String bundleIdentifier = item.getBundleIdentifier();
					String objectName = item.getClassName();
					objectName = objectName.substring(objectName.lastIndexOf(".") + 1);
					/*try {
						if (!failedBundles.contains(bundleIdentifier)) {
							IWBundle bundle = (IWBundle) bundles.get(bundleIdentifier);
							if (bundle == null) {
								bundle = iwma.getBundle(bundleIdentifier);
							}
							objectName = bundle.getComponentName(objectName, currentLocale);
						}
					}
					catch (IWBundleDoesNotExist iwbne) {
						failedBundles.add(bundleIdentifier);
						System.err.println("com.idega.builder.business.ModuleComparator: " + iwbne.getLocalizedMessage()
								+ ". Please remove all references in the IC_OBJECT table");
					}
					
					if(objectName==null){
						objectName = item.getClassName();
					}*/
					
					link = new Link(objectName);
					link.setStyle(STYLE_NAME);
					link.addParameter(INTERNAL_CONTROL_PARAMETER, " ");
					link.addParameter(IC_OBJECT_INSTANCE_ID_PARAMETER, item.getPrimaryKey().toString());
	
					subComponentTable.add(iconLink, 1, ypos);
					subComponentTable.add(link, 2, ypos);
	
					ypos++;
				}
				catch(Exception e){
					e.printStackTrace();
				}
			
			}
		}
	}

	private void setStyles() {
		String _linkStyle = "font-family:Arial,Helvetica,sans-serif;font-size:8pt;color:#000000;text-decoration:none;";
		String _linkHoverStyle = "font-family:Arial,Helvetica,sans-serif;font-size:8pt;color:#FF8008;text-decoration:none;";
		if (getParentPage() != null) {
			getParentPage().setStyleDefinition("A." + STYLE_NAME, _linkStyle);
			getParentPage().setStyleDefinition("A." + STYLE_NAME + ":hover", _linkHoverStyle);
		}
	}
	/**
	 *
	 */
	public String getBundleIdentifier() {
		return (IW_BUNDLE_IDENTIFIER);
	}

	public static void removeAttributes(IWContext iwc) {
		Iterator iter = ICLocaleBusiness.listOfLocales(true).iterator();
		while (iter.hasNext()) {
			ICLocale icLocale = (ICLocale) iter.next();
			Locale locale = ICLocaleBusiness.getLocaleFromLocaleString(icLocale.getLocale());
			int localeID = ICLocaleBusiness.getLocaleId(locale);
			iwc.removeApplicationAttribute(ELEMENT_LIST + "_" + Integer.toString(localeID));
			iwc.removeApplicationAttribute(BLOCK_LIST + "_" + Integer.toString(localeID));
		}
	}

	private Image getIconForObject(ICObject obj, IWContext iwc) {
		if (obj.getObjectType().equals(com.idega.core.component.data.ICObjectBMPBean.COMPONENT_TYPE_ELEMENT)) {
			/**
			 *@todo: Make support for dynamic icons
			 */
			if (elementImage == null)
				elementImage = iwc.getIWMainApplication().getCoreBundle().getImage("elementicon16x16.gif");
			return elementImage;
		}
		else if (obj.getObjectType().equals(com.idega.core.component.data.ICObjectBMPBean.COMPONENT_TYPE_BLOCK)) {
			/**
			  *@todo: Make support for dynamic icons
			  */
			if (blockImage == null)
				blockImage = iwc.getIWMainApplication().getCoreBundle().getImage("blockicon16x16.gif");
			return blockImage;
		}
		else {
			if (elementImage == null)
				elementImage = iwc.getIWMainApplication().getCoreBundle().getImage("elementicon16x16.gif");
			return elementImage;
		}
	}

}
