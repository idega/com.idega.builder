package com.idega.builder.presentation;

import com.idega.builder.business.IBPageHelper;
import com.idega.core.builder.business.ICBuilderConstants;
import com.idega.presentation.IWContext;
import com.idega.presentation.Layer;
import com.idega.presentation.text.Link;
import com.idega.presentation.ui.TreeViewer;
import com.idega.presentation.ui.util.AbstractChooserBlock;

public class IBPageChooserBlock extends AbstractChooserBlock {
	
	private boolean useSiteTree = true;
	
	public IBPageChooserBlock() {
		super();
	}
	
	public IBPageChooserBlock(String idAttribute, String valueAttribute) {
		super(idAttribute, valueAttribute);
	}
	
	public void main(IWContext iwc) {
		super.main(iwc);
		
		Layer container = getMainContaier();
		this.add(container);

		TreeViewer viewer = getTreeViewer(iwc, false);
		container.add(viewer);
		
		viewer.setCloseOrOpenNodesHref("javascript:void(0)");
		
		Link link = new Link();
		link.setMarkupAttribute("href", "javascript:void(0)");
		link.setNoTextObject(true);
		viewer.setLinkOpenClosePrototype(link);
		
		StringBuffer action = new StringBuffer();
		//	Action to remove old value
		action.append(getRemoveSelectedPropertyAction());
		//	Action to add new Value
		boolean simpleAction = getHiddenInputAttribute() == null ? true : false;
		action.append(getChooserObjectAction(simpleAction));
		// Action to set view
		action.append(getChooserViewAction());
		
		link = new Link();
		link.setURL("javascript:void(0)");
		link.setNoTextObject(true);
		link.setOnClick(action.toString());
		viewer.setLinkPrototype(link);
	}
	
	public TreeViewer getTreeViewer(IWContext iwc, boolean setDefaultParameters) {
		TreeViewer viewer = IBPageHelper.getInstance().getTreeViewer(iwc, setDefaultParameters, useSiteTree);
		
		viewer.setAddPageIdAtribute(true);
		viewer.setAddPageNameAttribute(true);
		
		//	Setting all nodes open
		viewer.setDefaultOpenLevel(Integer.MAX_VALUE);
		
		return viewer;
	}
	
	public boolean getChooserAttributes() {
		//	Setting default values
		if (getIdAttribute() == null) {
			setIdAttribute(ICBuilderConstants.PAGE_ID_ATTRIBUTE);
		}
		if (getValueAttribute() == null) {
			setValueAttribute(ICBuilderConstants.PAGE_NAME_ATTRIBUTE);
		}
		
		return true;
	}

	public void setUseSiteTree(boolean useSiteTree) {
		this.useSiteTree = useSiteTree;
	}

}
