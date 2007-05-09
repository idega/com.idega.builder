package com.idega.builder.presentation;

import com.idega.builder.business.IBPageHelper;
import com.idega.core.builder.business.ICBuilderConstants;
import com.idega.presentation.Block;
import com.idega.presentation.IWContext;
import com.idega.presentation.Layer;
import com.idega.presentation.text.Link;
import com.idega.presentation.ui.TreeViewer;

public class IBPageChooserBlock extends Block {
	
	public void main(IWContext iwc) {
		Layer container = new Layer();
		container.setId("chooser_presentation_object");
		container.setStyleAttribute("display: block;");
		this.add(container);

		TreeViewer viewer = IBPageHelper.getInstance().getPageTreeViewer(iwc, false);
		container.add(viewer);

		viewer.setAddPageIdAtribute(true);
		viewer.setAddPageNameAttribute(true);
		
		//	Setting all nodes open
		viewer.setDefaultOpenLevel(Integer.MAX_VALUE);
		
		Link link = new Link();
		link = new Link();
		link.setURL(new StringBuffer("#").append(container.getId()).toString());
		link.setNoTextObject(true);
		viewer.setLinkOpenClosePrototype(link);
		
		StringBuffer action = new StringBuffer();
		//	Action to remove old value
		action.append("removeAdvancedProperty('").append(ICBuilderConstants.PAGE_ID_ATTRIBUTE).append("');");
		
		//	Action to add new Value
		action.append("chooseObject(this, '").append(ICBuilderConstants.PAGE_ID_ATTRIBUTE);
		action.append("', '").append(ICBuilderConstants.PAGE_NAME_ATTRIBUTE).append("');");
		
		// Action to set view
		action.append("setChooserView(this, '").append(ICBuilderConstants.PAGE_NAME_ATTRIBUTE).append("');");
		
		link.setOnClick(action.toString());
		viewer.setLinkPrototype(link);
	}

}
