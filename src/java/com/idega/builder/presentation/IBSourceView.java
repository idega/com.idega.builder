package com.idega.builder.presentation;

import com.idega.builder.business.BuilderLogic;
import com.idega.builder.business.HtmlTemplateGrabber;
import com.idega.core.builder.data.ICPage;
import com.idega.presentation.IWContext;
import com.idega.presentation.Table;
import com.idega.presentation.text.PreformattedText;
import com.idega.presentation.ui.DropdownMenu;
import com.idega.presentation.ui.Form;
import com.idega.presentation.ui.SubmitButton;
import com.idega.presentation.ui.TextArea;
import com.idega.presentation.ui.TextInput;
import com.idega.presentation.ui.Window;

/**
 * Title: idegaclasses Description: Copyright: Copyright (c) 2001 Company: idega
 * 
 * @author <a href="tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */
public class IBSourceView extends Window {

	private static final String SOURCE_PARAMETER = "ib_page_source";
	private static final String IB_SOURCE_ACTION = "ib_page_source_action";
	private static final String IB_PAGE_FORMAT = "ib_page_fomat";
	private static final String PARAM_TEMPLATEURL = "templateurl";

	public IBSourceView() {
		setWidth(700);
		setHeight(600);
	}

	public void main(IWContext iwc) {
		String action = iwc.getParameter(IB_SOURCE_ACTION);
		if (action != null) {
			if (action.equals("update")) {
				try {
					String stringRep = iwc.getParameter(SOURCE_PARAMETER);
					if (stringRep != null) {
						String format = iwc.getParameter(IB_PAGE_FORMAT);
						doPageSourceUpdate(stringRep, format, iwc);
						this.setParentToReload();
					}
				}
				catch (Exception e) {
					add("Error when saving: " + e.getMessage());
					e.printStackTrace();
				}
			}
			else if (action.equals("grab")) {
				try {
					String sUrl = iwc.getParameter(PARAM_TEMPLATEURL);
					if (sUrl != null && !"".equals(sUrl)) {
						doPageTemplateGrab(sUrl, iwc);
						this.setParentToReload();
					}
				}
				catch (Exception e) {
					add("Error when grabbing: " + e.getMessage());
					e.printStackTrace();
				}
			}
		}
		Form form = new Form();
		Table table = new Table(1, 6);
		form.add(table);
		// form.addParameter(IB_SOURCE_ACTION,"update");
		add(form);
		String format;
		try {
			format = getBuilderLogic().getCurrentIBPageEntity(iwc).getFormat();
			String source = BuilderLogic.getInstance().getPageSource(iwc);
//			if (BuilderLogic.getInstance().PAGE_FORMAT_HTML.equals(format)) {
//				//HTMLArea area = new HTMLArea(SOURCE_PARAMETER, source, "100%", "500");
//				HTMLArea area = new HTMLArea();
//				area.addPlugin(HTMLArea.PLUGIN_TABLE_OPERATIONS);
//				area.addPlugin(HTMLArea.PLUGIN_DYNAMIC_CSS, "3");
//				area.addPlugin(HTMLArea.PLUGIN_CSS, "3");
//				area.addPlugin(HTMLArea.PLUGIN_CONTEXT_MENU);
//				area.addPlugin(HTMLArea.PLUGIN_LIST_TYPE);
//				area.addPlugin(HTMLArea.PLUGIN_CHARACTER_MAP);
//				area.setAllowFontSelection(false);
//				
//			
//				area.setFullHTMLPageSupport(true);
//				table.add(area, 1, 1);
//			}
//			else {
				TextArea area = new TextArea(SOURCE_PARAMETER, source);
				area.setWidth("100%");
				area.setHeight("500");
				area.setWrap(false);
				table.add(area, 1, 1);
			//}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		DropdownMenu menu = getFormatDropdown(iwc);
		table.add(menu, 1, 2);
		SubmitButton button = new SubmitButton("Save", IB_SOURCE_ACTION, "update");
		table.add(button, 1, 3);
		String templateString = "Note: Template regions in HTML templates are defined like this:\n<code>&lt;!-- TemplateBeginEditable name=\"MyUniqueRegionId1\" --&gt;MyUniqueRegionId1&lt;!-- TemplateEndEditable --&gt;</code>";
		PreformattedText helpText = new PreformattedText(templateString);
		table.add(helpText, 1, 4);
		TextInput templateGrabInput = new TextInput(PARAM_TEMPLATEURL);
		templateGrabInput.setLength(60);
		table.add(templateGrabInput, 1, 5);
		SubmitButton templateGrabButton = new SubmitButton("Grab template from URL", IB_SOURCE_ACTION, "grab");
		table.add(templateGrabButton, 1, 5);
		String templateGrabString = "Warning: This will get the template from URL and write over the template and set type to HTML";
		// PreformattedText templateGrapHelpText = new
		// PreformattedText(templateGrabString);
		table.add(templateGrabString, 1, 6);
	}

	private void doPageSourceUpdate(String sourceString, String pageFormat, IWContext iwc) throws Exception {
		// IBXMLPage page = BuilderLogic.getInstance().getCurrentIBXMLPage(iwc);
		// page.setSourceFromString(sourceString);
		BuilderLogic.getInstance().setPageSource(iwc, pageFormat, sourceString);
	}

	private void doPageTemplateGrab(String url, IWContext iwc) throws Exception {
		// IBXMLPage page = BuilderLogic.getInstance().getCurrentIBXMLPage(iwc);
		// page.setSourceFromString(sourceString);
		String pageKey = BuilderLogic.getInstance().getCurrentIBPage(iwc);
		new HtmlTemplateGrabber(url, pageKey);
	}

	// public void setSource(TextArea area,IWContext iwc){
	// //IBXMLPage page = BuilderLogic.getInstance().getCurrentIBXMLPage(iwc);
	// try{
	// String source = BuilderLogic.getInstance().getPageSource(iwc);
	// area.setContent(source);
	// }
	// catch(Exception e){
	// add(e);
	// }
	// }
	public DropdownMenu getFormatDropdown(IWContext iwc) {
		ICPage page;
		// The default format:
		String pageFormat = getBuilderLogic().getDefaultPageFormat();
		try {
			page = getBuilderLogic().getCurrentIBPageEntity(iwc);
			pageFormat = page.getFormat();
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		DropdownMenu menu = new DropdownMenu(IB_PAGE_FORMAT);
		String[] formats = getBuilderLogic().getPageFormatsSupported();
		for (int i = 0; i < formats.length; i++) {
			String formatKey = formats[i];
			menu.addMenuElement(formatKey);
		}
		// menu.addMenuElement("IBXML");
		// menu.addMenuElement("HTML");
		menu.setSelectedElement(pageFormat);
		return menu;
	}

	protected BuilderLogic getBuilderLogic() {
		return BuilderLogic.getInstance();
	}
}
