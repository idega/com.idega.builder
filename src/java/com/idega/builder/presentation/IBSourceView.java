package com.idega.builder.presentation;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.myfaces.component.html.ext.HtmlInputTextarea;

import com.idega.block.web2.business.Web2Business;
import com.idega.builder.business.BuilderLogic;
import com.idega.builder.business.HtmlTemplateGrabber;
import com.idega.business.SpringBeanLookup;
import com.idega.core.builder.data.ICPage;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.IWContext;
import com.idega.presentation.Layer;
import com.idega.presentation.Page;
import com.idega.presentation.text.Text;
import com.idega.presentation.ui.DropdownMenu;
import com.idega.presentation.ui.Form;
import com.idega.presentation.ui.SubmitButton;
import com.idega.presentation.ui.TextInput;
import com.idega.presentation.ui.Window;

/**
 * Title: idegaclasses Description: Copyright: Copyright (c) 2001 Company: idega
 * 
 * @author <a href="tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */
public class IBSourceView extends Window {
	public final static String IW_BUNDLE_IDENTIFIER = "com.idega.builder";
	private static final String SOURCE_PARAMETER = "ib_page_source";
	private static final String IB_SOURCE_ACTION = "ib_page_source_action";
	private static final String IB_PAGE_FORMAT = "ib_page_fomat";
	private static final String PARAM_TEMPLATEURL = "templateurl";

	public IBSourceView() {
		setWidth(700);
		setHeight(600);
	}

	public void main(IWContext iwc) {
		this.setStyleAttribute("margin:0px;overflow:hidden;background-color:#ffffff;");
		
		Web2Business web2 = SpringBeanLookup.getInstance().getSpringBean(iwc, Web2Business.class);
		this.getParentPage().addJavascriptURL(web2.getCodePressScriptFilePath());
				
		String action = iwc.getParameter(IB_SOURCE_ACTION);
		if (action != null) {
			if (action.equals("update")) {
				try {
					String stringRep = iwc.getParameter(SOURCE_PARAMETER);
					if (stringRep != null) {
						String format = iwc.getParameter(IB_PAGE_FORMAT);
						doPageSourceUpdate(stringRep, format, iwc);
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
		
		IWBundle iwb = this.getBundle(iwc);
		IWResourceBundle iwrb = this.getResourceBundle(iwc);
		
		addStyleSheetURL(iwb.getVirtualPathWithFileNameString("style/builder.css"));
		Layer sourceView = new Layer();
		sourceView.setStyleClass("sourceView");
		add(sourceView);
		
		Layer sourceViewButtonsLeft = new Layer();
		sourceViewButtonsLeft.setStyleClass("sourceViewButtonsLeft");
	
		Layer sourceViewButtonsRight = new Layer();
		sourceViewButtonsRight.setStyleClass("sourceViewButtonsRight");
		
		//temporary hack for opera and safari to fix their flawed handling of css height attribute
		boolean isFubarBrowserForNow = iwc.isSafari() || iwc.isOpera();
		if(isFubarBrowserForNow){
			this.setDoctype(Page.DOCTYPE_HTML_4_0_1_TRANSITIONAL);
			sourceViewButtonsLeft.setStyleAttribute("height", "15%");
			sourceViewButtonsRight.setStyleAttribute("height", "15%");
		}
		////////////////
	
		Form form = new Form();
		//sourceView.add(form);
		
		
		try {
			getBuilderLogic().getCurrentIBPageEntity(iwc).getFormat();
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
			HtmlInputTextarea area = new HtmlInputTextarea();
			area.setId(SOURCE_PARAMETER);
			area.setWrap("OFF");
			
			//enable syntax coloring!
			area.setStyleClass("codepress html linenumbers-on");
			
			
			if(isFubarBrowserForNow){
				area.setStyle("height: 83%");
			}
			area.setValue(source);
			
//				TextArea area = new TextArea("test_source", source);
//				area.setWrap(false);
//				
//				if(isFubarBrowserForNow){
//					area.setStyleAttribute("height", "83%");
//				}
				form.add(area);
			//}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		form.add(sourceViewButtonsLeft);
		form.add(sourceViewButtonsRight);
		
		//right
		String templateString = iwrb.getLocalizedString("sourceview.template_text","HTML template regions are defined like so:<br/><code>&lt;!-- TemplateBeginEditable name=\"MyUniqueRegionId1\" --&gt;<br/>MyUniqueRegionId1<br/>&lt;!-- TemplateEndEditable --&gt;</code>");
		Text templateText = new Text(templateString);
		templateText.setStyleClass("helpText");
		sourceViewButtonsRight.add(templateText);
		
		SubmitButton button = new SubmitButton("Save", IB_SOURCE_ACTION, "update");
		button.setAccessKey("s");
		sourceViewButtonsRight.add(button);
		
		DropdownMenu menu = getFormatDropdown(iwc);
		sourceViewButtonsRight.add(menu);
		
		//left
		
		TextInput templateGrabInput = new TextInput(PARAM_TEMPLATEURL,"http://");
		sourceViewButtonsLeft.add(templateGrabInput);
		SubmitButton templateGrabButton = new SubmitButton(iwrb.getLocalizedString("sourceview.grab_button_text","Grab template from URL"), IB_SOURCE_ACTION, "grab");
		sourceViewButtonsLeft.add(templateGrabButton);
		
		String templateGrabString =  iwrb.getLocalizedString("sourceview.grab_warning","Warning: Grabbing the url will get the html and write over this template and change its type to HTML");
		Text grabText = new Text(templateGrabString);
		grabText.setStyleClass("helpText");
		sourceViewButtonsLeft.add(grabText);
		sourceView.add(form);
		
	}

	private void doPageSourceUpdate(String sourceString, String pageFormat, IWContext iwc) throws Exception {
		BuilderLogic.getInstance().setPageSource(iwc, pageFormat, sourceString);
	}

	private void doPageTemplateGrab(String url, IWContext iwc) throws Exception {
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
		Map formats = getBuilderLogic().getPageFormatsSupportedAndDescription();
		Set keySet = formats.keySet();
		for (Iterator iter = keySet.iterator(); iter.hasNext();) {
			String format = (String) iter.next();
			String description = (String) formats.get(format);
			menu.addMenuElement(format,description);
		}
		/*for (int i = 0; i < formats.length; i++) {
			String formatKey = formats[i];
			menu.addMenuElement(formatKey);
		}*/
		// menu.addMenuElement("IBXML");
		// menu.addMenuElement("HTML");
		menu.setSelectedElement(pageFormat);
		return menu;
	}

	protected BuilderLogic getBuilderLogic() {
		return BuilderLogic.getInstance();
	}
	
	public String getBundleIdentifier(){
		return IW_BUNDLE_IDENTIFIER;
	}
}
