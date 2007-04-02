package com.idega.builder.presentation;

import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.IWContext;
import com.idega.presentation.Layer;
import com.idega.presentation.Script;
import com.idega.presentation.text.Heading1;
import com.idega.presentation.text.Link;
import com.idega.presentation.text.ListItem;
import com.idega.presentation.text.Lists;
import com.idega.presentation.ui.GenericButton;

public class EditModuleWindow extends IBAdminWindow {
	
	public void main(IWContext iwc) throws Exception {
		IWResourceBundle iwrb = getBuilderLogic().getBuilderBundle().getResourceBundle(iwc);
		
		String name = iwc.getParameter("moduleName");
		
		// Header
		Layer header = new Layer();
		header.add(new Heading1(name));
		header.setId("editModuleHeader");
		this.add(header);
		
		// Menu
//		<div id="menu">
//	    <ul id="nav">
//	        <li id="home" class="activelink"><a href="#">Home</a></li>
//	        <li id="who"><a href="#">About</a></li>
//	        <li id="prod"><a href="#">Product</a></li>
//	        <li id="serv"><a href="#">Services</a></li>
//	        <li id="cont"><a href="#">Contact us</a></li>
//	    </ul>
//	</div>
		Layer menu = new Layer();
		menu.setId("editModuleMenu");
		Lists navigation = new Lists();
		navigation.setId("editModuleMenuNavigation");
		
		ListItem settings = new ListItem();
		Link settingsLink = new Link(iwrb.getLocalizedString("settings", "Settings"), "#");
		settings.add(settingsLink);
		navigation.add(settings);
		
		ListItem settings2 = new ListItem();
		Link settings2Link = new Link(iwrb.getLocalizedString("settings2", "Settings2"), "#");
		settings.add(settings2Link);
		navigation.add(settings2);
		
		menu.add(navigation);
		this.add(menu);
		
		// Cancel button
		Layer closeContainer = new Layer();
		closeContainer.setId("closeButtonContainer");
		closeContainer.setStyleClass("closeButtonContainerStyle");
		GenericButton close = new GenericButton("cancel", iwrb.getLocalizedString("cancel", "Cancel"));
		close.setOnClick("editWindow.deactivate();");
		closeContainer.add(close);
		this.add(closeContainer);
		
		// Be sure 'niftycube.js' and 'BuilderHelper.js' files are added to page
		Script init = new Script();
		init.addScriptLine("initializeEditModuleWindow();");
		this.add(init);
	}

}
