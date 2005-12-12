package com.idega.builder.presentation;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import javax.swing.JButton;
import com.idega.builder.business.BuilderLogic;
import com.idega.builder.business.IBPropertyDescription;
import com.idega.builder.business.IBPropertyDescriptionComparator;
import com.idega.builder.business.IBPropertyHandler;
import com.idega.idegaweb.IWConstants;
import com.idega.idegaweb.IWProperty;
import com.idega.idegaweb.IWPropertyList;
import com.idega.presentation.IWContext;
import com.idega.presentation.Image;
import com.idega.presentation.Page;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.Table;
import com.idega.presentation.text.Link;

/**
 * Title: idegaclasses Description: Copyright: Copyright (c) 2001 Company: idega
 * 
 * @author <a href="tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */
public class IBPropertiesWindowList extends Page {

	public static final String IC_OBJECT_INSTANCE_ID_PARAMETER = IBPropertiesWindow.IC_OBJECT_INSTANCE_ID_PARAMETER;
	public static final String IB_PAGE_PARAMETER = IBPropertiesWindow.IB_PAGE_PARAMETER;
	final static String METHOD_ID_PARAMETER = IBPropertiesWindow.METHOD_ID_PARAMETER;
	final static String VALUE_SAVE_PARAMETER = IBPropertiesWindow.VALUE_SAVE_PARAMETER;
	final static String VALUE_PARAMETER = IBPropertiesWindow.VALUE_PARAMETER;
	static final String LIST_FRAME = "ib_prop_list_frame";
	static final String PROPERTY_FRAME = "ib_prop_frame";
	final static String STYLE_NAME = "properties";
	Image button;
	Image hoverButton;
	JButton jButton1 = new JButton();

	public IBPropertiesWindowList() {
		setAllMargins(0);
		setBackgroundColor(IWConstants.DEFAULT_INTERFACE_COLOR);
		try {
			jbInit();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void main(IWContext iwc) throws Exception {
		button = iwc.getIWMainApplication().getBundle(BuilderLogic.IW_BUNDLE_IDENTIFIER).getImage(
				"shared/properties/button.gif");
		hoverButton = iwc.getIWMainApplication().getBundle(BuilderLogic.IW_BUNDLE_IDENTIFIER).getImage(
				"shared/properties/button_hvr.gif");
		String ic_object_id = getUsedICObjectInstanceID(iwc);
		setStyles();
		if (ic_object_id != null) {
			add(getPropertiesList(ic_object_id, iwc));
			// System.out.println("IBPropertiesWindowList: Getting
			// IC_OBJECT_ID");
		}
		else {
			// System.out.println("IBPropertiesWindowList: Not getting
			// IC_OBJECT_ID");
		}
	}

	public String getUsedICObjectInstanceID(IWContext iwc) {
		return iwc.getParameter(IC_OBJECT_INSTANCE_ID_PARAMETER);
	}

	public PresentationObject getPropertiesList(String instanceId, IWContext iwc) throws Exception {
		Table table = new Table();
		table.setCellpadding(3);
		table.setCellspacing(0);
		table.setWidth("100%");
		String pageKey = BuilderLogic.getInstance().getCurrentIBPage(iwc);
		// int icObjectInstanceID = Integer.parseInt(ic_object_instance_id);
		// List methodList =
		// IBPropertyHandler.getInstance().getMethodsListOrdered(icObjectInstanceID,iwc);
		try {
			List methodList = this.getMethodListOrdered(iwc, instanceId);
			Iterator iter = methodList.iterator();
			int counter = 1;
			while (iter.hasNext()) {
				IBPropertyDescription desc = (IBPropertyDescription) iter.next();
				String methodIdentifier = desc.getMethodIdentifier();
				String methodDescr = desc.getMethodDescription();
				Link link = new Link(methodDescr);
				link.setStyle(STYLE_NAME);
				link.setURL("javascript:parent." + PROPERTY_FRAME + "."
						+ IBPropertiesWindowSetter.CHANGE_PROPERTY_FUNCTION_NAME + "('" + methodIdentifier + "')");
				if (BuilderLogic.getInstance().isPropertySet(pageKey, instanceId, methodIdentifier,
						iwc.getIWMainApplication()))
					table.add((Image) hoverButton.clone(), 1, counter);
				else
					table.add((Image) button.clone(), 1, counter);
				table.add(link, 2, counter);
				counter++;
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		// table.setHorizontalZebraColored("#CCCCCC","#FFFFFF");
		table.setWidth(2, "100%");
		return table;
	}

	/**
	 * Returns a list of IBPropertyDescription objects
	 */
	private List getMethodListOrdered(IWContext iwc, String instanceId) throws Exception {
		List theReturn = new Vector();
		IWPropertyList methodList = IBPropertyHandler.getInstance().getMethods(instanceId, iwc.getIWMainApplication());
		Iterator iter = methodList.iterator();
		while (iter.hasNext()) {
			IWProperty methodProp = (IWProperty) iter.next();
			String methodIdentifier = IBPropertyHandler.getInstance().getMethodIdentifier(methodProp);
			String methodDescr = IBPropertyHandler.getInstance().getMethodDescription(methodProp, iwc);
			IBPropertyDescription desc = new IBPropertyDescription(methodIdentifier);
			desc.setMethodDescription(methodDescr);
			theReturn.add(desc);
		}
		java.util.Collections.sort(theReturn, IBPropertyDescriptionComparator.getInstance());
		return theReturn;
	}

	private void setStyles() {
		String _linkStyle = "font-family:Arial,Helvetica,sans-serif;font-size:8pt;color:#000000;text-decoration:none;";
		String _linkHoverStyle = "font-family:Arial,Helvetica,sans-serif;font-size:8pt;color:#FF8008;text-decoration:none;";
		if (getParentPage() != null) {
			getParentPage().setStyleDefinition("A." + STYLE_NAME, _linkStyle);
			// getParentPage().setStyleDefinition("A."+STYLE_NAME+":visited",_linkStyle);
			// getParentPage().setStyleDefinition("A."+STYLE_NAME+":active",_linkStyle);
			getParentPage().setStyleDefinition("A." + STYLE_NAME + ":hover", _linkHoverStyle);
		}
	}

	private void jbInit() throws Exception {
		jButton1.setText("jButton1");
	}
}
