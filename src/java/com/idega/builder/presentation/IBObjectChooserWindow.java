/*
 * Created on Jun 21, 2003
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code Template
 */
package com.idega.builder.presentation;

import java.util.Collection;
import java.util.Iterator;

import javax.ejb.FinderException;

import com.idega.builder.business.BuilderLogic;
import com.idega.core.component.data.ICObject;
import com.idega.core.component.data.ICObjectBMPBean;
import com.idega.core.component.data.ICObjectHome;
import com.idega.data.IDOLookup;
import com.idega.data.IDOLookupException;
import com.idega.idegaweb.IWConstants;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.IWContext;
import com.idega.presentation.Table;
import com.idega.presentation.text.Link;
import com.idega.presentation.ui.AbstractChooserWindow;
import com.idega.presentation.ui.DropdownMenu;
import com.idega.presentation.ui.Form;

/**
 * <p>Title: idegaWeb</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: idega Software</p>
 * @author aron 
 * @version 1.0
 */
public class IBObjectChooserWindow extends AbstractChooserWindow {
	
	public static String PRM_FILTER = "ico_filter";
	private static final int _width = 280;
	private static final int _height = 400;
	private static final String _linkStyle = "font-family:Arial,Helvetica,sans-serif;font-size:8pt;color:#000000;text-decoration:none;";
	
	public IBObjectChooserWindow() {
			setTitle("Object chooser");
			setWidth(_width);
			setHeight(_height);
			setCellpadding(5);
			setScrollbar(true);
			this.getLocation().setApplicationClass(this.getClass());
			this.getLocation().isInPopUpWindow(true);
	}
	
	/* (non-Javadoc)
	 * @see com.idega.presentation.ui.AbstractChooserWindow#displaySelection(com.idega.presentation.IWContext)
	 */
	public void displaySelection(IWContext iwc) {
		IWResourceBundle iwrb = iwc.getIWMainApplication().getBundle(BuilderLogic.IW_BUNDLE_IDENTIFIER).getResourceBundle(iwc);
		addTitle(iwrb.getLocalizedString("select_object", "Select object"), IWConstants.BUILDER_FONT_STYLE_TITLE);
		setStyles();
		debugParameters(iwc);
		boolean useClassValue = iwc.isParameterSet(IBObjectChooser.USE_CLASS_VALUE);
		Form form = new Form();
		Table table = new Table();
		String[] filters = iwc.getParameterValues(PRM_FILTER);
		
		try {
			if(filters!=null){
				Collection collection;
				Link icoLink;
				int col;
				for (int i = 0; i < filters.length; i++) {
					int row = 1;
					col = i+1;
					table.add(filters[i],col,row++);
					collection = getFilteredComponents(filters[i]);
					for (Iterator iter = collection.iterator(); iter.hasNext();) {
						ICObject item = (ICObject) iter.next();
						icoLink  = new Link(item.getBundle(iwc.getIWMainApplication()).getComponentName(item.getClassName(), iwc.getCurrentLocale()));
						//icoLink.setOnClick(getOnSelectionCode(  item.getPrimaryKey().toString(),item.getName()));
						//icoLink.setOnClick(getOnSelectionCode( "'"+item.getName()+"'", item.getPrimaryKey().toString() ));
						//icoLink.setOnClick(getOnSelectionCode(item.getPrimaryKey().toString()));
						if(useClassValue) {
							icoLink.setOnClick(getOnSelectionCode("'"+item.getName()+"'","'"+item.getClassName()+"'"));
						}
						else {
							icoLink.setOnClick(getOnSelectionCode( "'"+item.getName()+"'", item.getPrimaryKey().toString() ));
						}
						
						icoLink.setURL(Link.JAVASCRIPT);
						table.add(icoLink,col,row++);
						//addComponentToTable(element,table,i+1,row++);
					}
					
				}
			}
			else {
				DropdownMenu drp = getTypeFiltersDrop();
				drp.setToSubmit(true);
				form.add(drp);
			}
		}
		catch (IDOLookupException e) {
			add(e.getMessage());
			e.printStackTrace();
		}
		catch (FinderException e) {
			add(e.getMessage());
			e.printStackTrace();
		}
		form.maintainParameter(FORM_ID_PARAMETER);
		form.maintainParameter(SCRIPT_SUFFIX_PARAMETER);
		form.maintainParameter(DISPLAYSTRING_PARAMETER_NAME);
		form.maintainParameter(VALUE_PARAMETER_NAME);
		form.maintainParameter(IBObjectChooser.USE_CLASS_VALUE);
		
		form.add(table);
		add(form);
		
	}
	
	private void addComponentToTable(ICObject object,Table table, int column,int row){
		
	}
	
	private DropdownMenu getTypeFiltersDrop(){
		DropdownMenu drp = new DropdownMenu(PRM_FILTER);
		Collection types = ICObjectBMPBean.getAvailableComponentTypes();
		drp.addDisabledMenuElement("","Types");
		for (Iterator iter = types.iterator(); iter.hasNext();) {
			String element = (String) iter.next();
			drp.addMenuElement(element);
		}
		return drp;
	}
	
	private Collection getFilteredComponents(String filter)throws FinderException,IDOLookupException{
		ICObjectHome home = (ICObjectHome) IDOLookup.getHome(ICObject.class);
		return home.findAllByObjectType(filter);
	}
	
	private void setStyles() {
			String _linkStyle = "font-family:Arial,Helvetica,sans-serif;font-size:8pt;color:#000000;text-decoration:none;";
			String _linkHoverStyle = "font-family:Arial,Helvetica,sans-serif;font-size:8pt;color:#FF8008;text-decoration:none;";
			if (getParentPage() != null) {
				getParentPage().setStyleDefinition("A", _linkStyle);
				//getParentPage().setStyleDefinition("A."+STYLE_NAME+":visited",_linkStyle);
				//getParentPage().setStyleDefinition("A."+STYLE_NAME+":active",_linkStyle);
				getParentPage().setStyleDefinition("A:hover", _linkHoverStyle);
			}
		}
}
