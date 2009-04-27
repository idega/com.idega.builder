package com.idega.builder.presentation;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.myfaces.component.html.ext.HtmlInputTextarea;

import com.idega.block.web2.business.Web2Business;
import com.idega.builder.business.BuilderConstants;
import com.idega.builder.business.BuilderLogic;
import com.idega.builder.business.HtmlTemplateGrabber;
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
import com.idega.util.PresentationUtil;
import com.idega.webface.WFUtil;

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

	@Override
	public void main(IWContext iwc) {
		setPrintScriptSourcesDirectly(false);
		this.setStyleAttribute("margin:0px;overflow:hidden;background-color:#ffffff;");
		
		Layer sourceView = new Layer();
		sourceView.setStyleClass("sourceView");
		add(sourceView);
		
		IWBundle iwb = this.getBundle(iwc);
		
		Web2Business web2 = WFUtil.getBeanInstance(iwc, Web2Business.SPRING_BEAN_IDENTIFIER);
		sourceView.add(PresentationUtil.getJavaScriptSourceLine(web2.getCodePressScriptFilePath()));
		PresentationUtil.addStyleSheetToHeader(iwc, iwb.getVirtualPathWithFileNameString("style/builder.css"));
		
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
		
		IWResourceBundle iwrb = this.getResourceBundle(iwc);
		
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
	
		Form form = new Form();
		try {
			String pageKey = getCurrentPageKey(iwc);
			String source = pageKey == null ? BuilderLogic.getInstance().getPageSource(iwc) : BuilderLogic.getInstance().getPageSource(pageKey);
			HtmlInputTextarea area = new HtmlInputTextarea();
			area.setId(SOURCE_PARAMETER);
			area.setWrap("OFF");
			
			//enable syntax coloring!
			area.setStyleClass("codepress html linenumbers-on");
			
			if(isFubarBrowserForNow){
				area.setStyle("height: 83%");
			}
			area.setValue(source);

			form.add(area);
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
	
	private String getCurrentPageKey(IWContext iwc) {
		return iwc.getParameter("pageForSourceId");
	}

	private void doPageSourceUpdate(String sourceString, String pageFormat, IWContext iwc) throws Exception {
		String pageKey = getCurrentPageKey(iwc);
		if (pageKey == null) {
			getBuilderLogic().setPageSource(iwc, pageFormat, sourceString);
			return;
		}
		
		getBuilderLogic().setPageSource(pageKey, pageFormat, sourceString);
	}

	private void doPageTemplateGrab(String url, IWContext iwc) throws Exception {
		String pageKey = getBuilderLogic().getCurrentIBPage(iwc);
		new HtmlTemplateGrabber(url, pageKey);
	}
	
	public DropdownMenu getFormatDropdown(IWContext iwc) {
		ICPage page;
		// The default format:
		String pageFormat = getBuilderLogic().getDefaultPageFormat();
		try {
			page = getBuilderLogic().getCurrentIBPageEntity(iwc);
			pageFormat = page.getFormat();
		}
		catch (Exception e) {
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
		menu.setSelectedElement(pageFormat);
		return menu;
	}

	protected BuilderLogic getBuilderLogic() {
		return BuilderLogic.getInstance();
	}
	
	@Override
	public String getBundleIdentifier(){
		return BuilderConstants.IW_BUNDLE_IDENTIFIER;
	}
}
