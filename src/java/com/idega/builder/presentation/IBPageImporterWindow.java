package com.idega.builder.presentation;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Iterator;
import java.util.List;

import javax.ejb.FinderException;

import com.idega.builder.business.IBPageHelper;
import com.idega.builder.business.IBPageImportBusiness;
import com.idega.builder.business.PageTreeNode;
import com.idega.business.IBOLookup;
import com.idega.business.IBOLookupException;
import com.idega.data.IDOLookupException;
import com.idega.idegaweb.IWApplicationContext;
import com.idega.idegaweb.IWConstants;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.io.UploadFile;
import com.idega.presentation.IWContext;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.Table;
import com.idega.presentation.text.Link;
import com.idega.presentation.text.Text;
import com.idega.presentation.ui.CheckBox;
import com.idega.presentation.ui.FileInput;
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
public class IBPageImporterWindow extends IBPageWindow {

  public static final String IW_BUNDLE_IDENTIFIER  = "com.idega.builder";
  
  public static final String IMPORT_KEY = "ib_page_import_key";
  public static final String TOP_LEVEL_KEY = "ib_page_import_top_level_key";
  
  public static final String SUBMIT_IMPORT_KEY = "ib_page_import_submit_key";
  
  public static final String SUBMIT_CLOSE_KEY = "ib_page_import_close_submit_key";
  public static final String PAGE_KEY = "ib_page_import_key";
  public static final String TEMPLATE_KEY = "ib_page_import_template_key";
  
  public static final String CLOSE_ACTION = "ib_page_import_close_action";
  public static final String IMPORT_ACTION = "ib_page_import_action";
  
  public static final String NODE_DELIMITER = " > ";
  
  boolean topLevelIsChosen = false;
  private int parentPageId = -1; 
  
  // list of page ids (type Integer) 
  //private List pageIds = null; 
  // list of template ids (type Intger)
	//private List templateIds = null;
	// download link
	private String downloadLink = null;
	
	private IBPageImportBusiness pageImportBusiness = null;
	
	public String getBundleIdentifier() {
    return IW_BUNDLE_IDENTIFIER;
  }
	
  public IBPageImporterWindow() {
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
  
  private void getContent(IWResourceBundle resourceBundle, IWContext iwc) throws IDOLookupException, FinderException {
  	Table table = new Table(1, 4);
  	int row = 1;
  	// add file input
  	table.add(getFileInput(), 1, row++);
  	// top level checkbox
  	table.add(getTopLevelCheckBox(resourceBundle), 1, row++);
		// page chooser
  	if (! topLevelIsChosen) {
  		table.add(getPageChooser(PAGE_CHOOSER_NAME, iwc), 1, row++);
  	}
		// 
  	Form form = new Form();
  	form.add(table);
  	
  	
  	IBPageHelper pageHelper = IBPageHelper.getInstance();
  	List startPages = pageHelper.getFirstLevelPageTreeNodesDomainFirst(iwc);
  	List templateStartPages = pageHelper.getFirstLevelPageTreeNodesTemplateDomainFirst(iwc);
  	PresentationObject pageViewer = getPageViewer(startPages, PAGE_KEY);
  	PresentationObject templateViewer = getPageViewer(templateStartPages, TEMPLATE_KEY);
  	form.add(pageViewer);
  	form.add(templateViewer);
  	form.add(getButtons(resourceBundle));
  	add(form);
  	if (downloadLink != null) {
  		String downloadText = resourceBundle.getLocalizedString("ib_page_export_download", "Download");
  		add(new Link(downloadText, downloadLink));
  	}
  }
  
  private PresentationObject getTopLevelCheckBox(IWResourceBundle resourceBundle) {
  	Table table = new Table(2,1);
 		CheckBox topLevelCheckBox = new CheckBox(TOP_LEVEL_KEY, "true");
		topLevelCheckBox.setChecked(topLevelIsChosen);
		Text topLevelText = new Text(resourceBundle.getLocalizedString(TOP_LEVEL_KEY, "Top level") + ":");
		topLevelText.setFontStyle(IWConstants.BUILDER_FONT_STYLE_LARGE);
		table.add(topLevelText, 1, 1);
		table.add(topLevelCheckBox, 2, 1);
		topLevelCheckBox.setOnClick("this.form.submit()");
		return table;
  }

  
  private FileInput getFileInput() {
  	FileInput fileInput = new FileInput(IMPORT_KEY);
  	return fileInput;
  }
  	
  private String parseAction(IWContext iwc) {
  	String action = null;
  	topLevelIsChosen = (new Boolean(iwc.getParameter(TOP_LEVEL_KEY))).booleanValue();
  	String parentPageIdString = iwc.getParameter(PAGE_CHOOSER_NAME);
  	if (parentPageIdString != null) {
  		try {
  			parentPageId = Integer.parseInt(parentPageIdString);
  		}
  		catch (NumberFormatException ex) {
  			parentPageId = -1;
  		}
  	}
  	
//  	pageIds = getSelectedIds(PAGE_KEY, iwc);
//  	templateIds = getSelectedIds(TEMPLATE_KEY, iwc);
  	if (iwc.isParameterSet(SUBMIT_IMPORT_KEY)) {
  		action = IMPORT_ACTION;
  	}
  	if (iwc.isParameterSet(SUBMIT_CLOSE_KEY)) {
  		action = CLOSE_ACTION;
  	}
  	return action;
  }
  
  private boolean doAction(String action, IWContext iwc) throws IOException {
  	if (CLOSE_ACTION.equals(action)) {
  		close();
  	}
  	else if (IMPORT_ACTION.equals(action)) {
	  	UploadFile file = iwc.getUploadedFile();
	  	if (file != null) {
	  		importPages(iwc, file);
	  	}
  	}
  	return true;
  }

//  private List getSelectedIds(String key, IWContext iwc) {
//  	List list = null;
//  	if (iwc.isParameterSet(key)) {
//  		String[] selectedPageIds = iwc.getParameterValues(key);
//  		list = new ArrayList(selectedPageIds.length);
//  		for (int i = 0; i < selectedPageIds.length; i++) {
//				String string = selectedPageIds[i];
//				list.add(new Integer(string));
//  		}
//  	}
//  	return list;
//  }
  
  private void importPages(IWContext iwc, UploadFile file) throws IBOLookupException, RemoteException, IOException { 
  	getPageImportBusiness(iwc).importPages(file,  parentPageId);
	}
  	
  		
				
  private PresentationObject getButtons(IWResourceBundle resourceBundle) {
  	SubmitButton importButton = 
      new SubmitButton(resourceBundle.getLocalizedString("ib_page_import_Import","Import"), SUBMIT_IMPORT_KEY, "true");
  	importButton.setAsImageButton(true);
  	SubmitButton closeButton = 
      new SubmitButton(resourceBundle.getLocalizedString("ib_page_import_Close", "Close"), SUBMIT_CLOSE_KEY, "true");
  	closeButton.setAsImageButton(true);
  	Table table = new Table(2,1);
  	table.add(importButton, 1,1);
  	table.add(closeButton, 2,1);
  	return table;
  }
  	
  		
	private PresentationObject getPageViewer( List startPages, String key) {
		return getDoubleSelectionBox(startPages, key);
  }

	private SelectionDoubleBox getDoubleSelectionBox(List startPages, String rightSelectionKey) {
		// create selection double box and set parameter string
		SelectionDoubleBox selectionDoubleBox = new SelectionDoubleBox(rightSelectionKey);
    // set size
		SelectionBox rightBox = selectionDoubleBox.getRightBox();
		SelectionBox leftBox = selectionDoubleBox.getLeftBox();
    rightBox.setHeight("20");
    leftBox.setHeight("20");   
		// submit selection on right box
		rightBox.selectAllOnSubmit();
		Iterator iterator = startPages.iterator();
		while (iterator.hasNext()) {
			PageTreeNode pageTreeNode = (PageTreeNode) iterator.next();
			addItems(leftBox, pageTreeNode, "");
		}
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
		
	private IBPageImportBusiness getPageImportBusiness(IWApplicationContext iwac) throws IBOLookupException {
		if (pageImportBusiness == null) {
			pageImportBusiness =  (IBPageImportBusiness) IBOLookup.getServiceInstance(iwac,IBPageImportBusiness.class);
		}
		return pageImportBusiness;
	}
		
}