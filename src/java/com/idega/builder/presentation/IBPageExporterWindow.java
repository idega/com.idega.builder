package com.idega.builder.presentation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.ejb.FinderException;

import com.idega.builder.business.BuilderLogic;
import com.idega.builder.business.IBPageExportBusiness;
import com.idega.builder.business.PageTreeNode;
import com.idega.business.IBOLookup;
import com.idega.business.IBOLookupException;
import com.idega.core.builder.data.ICDomain;
import com.idega.idegaweb.IWApplicationContext;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.idegaweb.presentation.IWAdminWindow;
import com.idega.presentation.IWContext;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.Table;
import com.idega.presentation.text.Link;
import com.idega.presentation.ui.Form;
import com.idega.presentation.ui.SelectionBox;
import com.idega.presentation.ui.SelectionDoubleBox;
import com.idega.presentation.ui.SubmitButton;

/**
 * <p>Title: idegaWeb</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: idega Software</p>
 * @author <a href="thomas@idega.is">Thomas Hilbig</a>
 * @version 1.0
 * Created on Mar 4, 2004
 */
public class IBPageExporterWindow extends IWAdminWindow {

  protected static final String IW_BUNDLE_IDENTIFIER  = "com.idega.builder";
  
  public static final String SUBMIT_EXPORT_KEY = "ib_page_export_submit_key";
  public static final String SUBMIT_CLOSE_KEY = "ib_page_close_submit_key";
  public static final String PAGE_KEY = "ib_page_export_key";
  public static final String TEMPLATE_KEY = "ib_page_export_template_key";
  
  public static final String CLOSE_ACTION = "ib_page_close_action";
  public static final String EXPORT_ACTION = "ib_page_export_action";
  
  public static final String NODE_DELIMITER = " > ";
  
  // list of page ids (type Integer) 
  private List pageIds = null; 
  // list of template ids (type Intger)
	//private List templateIds = null;
	// download link
	private String downloadLink = null;
	
	private IBPageExportBusiness pageExportBusiness = null;
	
	public String getBundleIdentifier() {
    return IW_BUNDLE_IDENTIFIER;
  }
	
  public IBPageExporterWindow() {
    setWidth(300);
    setHeight(400);
    setScrollbar(true);
    setResizable(true);
  }

  public void main(IWContext iwc) throws Exception {
  	String action = parseAction(iwc);
		IWResourceBundle resourceBundle = getResourceBundle(iwc);
		doAction(action, iwc);
		getContent(resourceBundle, iwc);

  }
  
  private void getContent(IWResourceBundle resourceBundle, IWContext iwc) {
  	Form form = new Form();
  	ICDomain domain = BuilderLogic.getCurrentDomain(iwc);
  	int startPageId = domain.getStartPageID();
  	int startTemplateId = domain.getStartTemplateID();
  	PresentationObject pageViewer = getPageViewer(startPageId, PAGE_KEY, iwc);
  	PresentationObject templateViewer = getPageViewer(startTemplateId, TEMPLATE_KEY, iwc);
  	form.add(pageViewer);
  	form.add(templateViewer);
  	form.add(getButtons(resourceBundle));
  	add(form);
  	if (downloadLink != null) {
  		String downloadText = resourceBundle.getLocalizedString("ib_page_export_download", "Download");
  		add(new Link(downloadText, downloadLink));
  	}
  }
  	
  private String parseAction(IWContext iwc) {
  	String action = null;
  	pageIds = getSelectedIds(PAGE_KEY, iwc);
  	//templateIds = getSelectedIds(TEMPLATE_KEY, iwc);
  	if (iwc.isParameterSet(SUBMIT_EXPORT_KEY)) {
  		action = EXPORT_ACTION;
  	}
  	if (iwc.isParameterSet(SUBMIT_CLOSE_KEY)) {
  		action = CLOSE_ACTION;
  	}
  	return action;
  }
  
  private boolean doAction(String action, IWApplicationContext iwac) throws IOException, FinderException {
  	if (CLOSE_ACTION.equals(action)) {
  		close();
  	}
  	else if (EXPORT_ACTION.equals(action)) {
 			downloadLink = exportPages(iwac);
  	}
  	return true;
  }

  private List getSelectedIds(String key, IWContext iwc) {
  	List list = null;
  	if (iwc.isParameterSet(key)) {
  		String[] selectedPageIds = iwc.getParameterValues(key);
  		list = new ArrayList(selectedPageIds.length);
  		for (int i = 0; i < selectedPageIds.length; i++) {
				String string = selectedPageIds[i];
				list.add(new Integer(string));
  		}
  	}
  	return list;
  }
  
  private String exportPages(IWApplicationContext iwac) throws IOException, FinderException {
  	if (pageIds == null || pageIds.isEmpty()) {
  		return null;
  	}
  	return getPageExportBusiness(iwac).exportPages(pageIds);
  }
  		
  		
				
  private PresentationObject getButtons(IWResourceBundle resourceBundle) {
  	SubmitButton exportButton = 
      new SubmitButton(resourceBundle.getLocalizedString("ib_page_export_Export","Export"), SUBMIT_EXPORT_KEY, "true");
  	exportButton.setAsImageButton(true);
  	SubmitButton closeButton = 
      new SubmitButton(resourceBundle.getLocalizedString("ib_page_export_Close", "Close"), SUBMIT_CLOSE_KEY, "true");
  	closeButton.setAsImageButton(true);
  	Table table = new Table(2,1);
  	table.add(exportButton, 1,1);
  	table.add(closeButton, 2,1);
  	return table;
  }
  	
  		
	private PresentationObject getPageViewer( int pageId, String key, IWContext iwc) {
		PageTreeNode pageTreeNode = new PageTreeNode(pageId, iwc);
		return getDoubleSelectionBox(pageTreeNode, key);
  }

	private SelectionDoubleBox getDoubleSelectionBox( PageTreeNode node, String rightSelectionKey) {
		// create selection double box and set parameter string
		SelectionDoubleBox selectionDoubleBox = new SelectionDoubleBox(rightSelectionKey);
    // set size
		SelectionBox rightBox = selectionDoubleBox.getRightBox();
		SelectionBox leftBox = selectionDoubleBox.getLeftBox();
    rightBox.setHeight("20");
    leftBox.setHeight("20");   
		// submit selection on right box
		rightBox.selectAllOnSubmit();
		addItems(leftBox, node, "");
		return selectionDoubleBox;
	}

	private void addItems(SelectionBox selectionBox, PageTreeNode node, String parentNodeDescription) {
		String id = Integer.toString(node.getNodeID());
		String nodeName = node.getNodeName();
		StringBuffer buffer = new StringBuffer(parentNodeDescription);
		buffer.append(NODE_DELIMITER);
		buffer.append(nodeName);
		String nodeNameDescription = buffer.toString();
		selectionBox.addMenuElement(id, nodeNameDescription);
		Iterator childrenIterator = node.getChildren();
		if (childrenIterator == null) {
			return;
		}
		else {
			while (childrenIterator.hasNext()) {
				PageTreeNode childNode = (PageTreeNode) childrenIterator.next();
				addItems(selectionBox, childNode, nodeNameDescription);
			}
		}
	}
		
	private IBPageExportBusiness getPageExportBusiness(IWApplicationContext iwac) throws IBOLookupException {
		if (pageExportBusiness == null) {
			pageExportBusiness =  (IBPageExportBusiness) IBOLookup.getServiceInstance(iwac,IBPageExportBusiness.class);
		}
		return pageExportBusiness;
	}
		
}
