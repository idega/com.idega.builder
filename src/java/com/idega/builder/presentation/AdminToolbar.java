/**
 * 
 */
package com.idega.builder.presentation;

import javax.faces.context.FacesContext;

import com.idega.block.login.presentation.Login2;
import com.idega.block.web2.business.Web2Business;
import com.idega.builder.bean.AdminToolbarSession;
import com.idega.builder.business.BuilderConstants;
import com.idega.core.accesscontrol.business.StandardRoles;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.IWContext;
import com.idega.presentation.Layer;
import com.idega.presentation.PresentationObjectTransitional;
import com.idega.presentation.text.Heading1;
import com.idega.presentation.text.ListItem;
import com.idega.presentation.text.Lists;
import com.idega.presentation.text.Paragraph;
import com.idega.presentation.text.Text;
import com.idega.util.CoreConstants;
import com.idega.util.PresentationUtil;
import com.idega.util.expression.ELUtil;


/**
 * <p>
 * TODO laddi Describe Type AdminToolbar
 * </p>
 *  Last modified: $Date: 2009/01/04 10:33:57 $ by $Author: valdas $
 * 
 * @author <a href="mailto:laddi@idega.com">laddi</a>
 * @version $Revision: 1.7 $
 */
public class AdminToolbar extends PresentationObjectTransitional {

	/* (non-Javadoc)
	 * @see com.idega.presentation.PresentationObjectTransitional#initializeComponent(javax.faces.context.FacesContext)
	 */
	@Override
	protected void initializeComponent(FacesContext context) {
		IWContext iwc = IWContext.getIWContext(context);
		
		IWResourceBundle iwrb = getResourceBundle(iwc);
		Web2Business business = ELUtil.getInstance().getBean(Web2Business.class);
		PresentationUtil.addStyleSheetToHeader(iwc, getBundle(iwc).getVirtualPathWithFileNameString("style/admin-core.css"));
		PresentationUtil.addJavaScriptSourceLineToHeader(iwc, business.getBundleURIToJQueryLib());
		PresentationUtil.addJavaScriptSourceLineToHeader(iwc, CoreConstants.DWR_ENGINE_SCRIPT);
		PresentationUtil.addJavaScriptSourceLineToHeader(iwc, CoreConstants.DWR_UTIL_SCRIPT);
		PresentationUtil.addJavaScriptSourceLineToHeader(iwc, "/dwr/interface/AdminToolbarSession.js");
		PresentationUtil.addJavaScriptSourceLineToHeader(iwc, iwc.getIWMainApplication().getBundle(CoreConstants.CORE_IW_BUNDLE_IDENTIFIER)
																									.getVirtualPathWithFileNameString("javascript/AdminCore.js"));
		
		Layer layer = new Layer();
		layer.setID("adminTopLayer");
		add(layer);
		
		Login2 login = new Login2();
		login.setURLToRedirectToOnLogoff("/pages/");
		layer.add(login);
		
		Lists list = new Lists();
		layer.add(list);
		
		ListItem themes = new ListItem();
		themes.setStyleClass("adminThemesMode");
		themes.add(new Text(iwrb.getLocalizedString("admin_mode.themes", "Themes")));
		if (iwc.hasRole(StandardRoles.ROLE_KEY_ADMIN) || iwc.hasRole(StandardRoles.ROLE_KEY_EDITOR)) {
			list.add(themes);
		}
		
		Layer themesHelp = new Layer();
		themesHelp.setStyleClass("modeHelper");
		themesHelp.add(new Heading1(iwrb.getLocalizedString("admin_mode.themes_help", "Themes mode")));
		Paragraph paragraph = new Paragraph();
		paragraph.add(new Text(iwrb.getLocalizedString("admin_mode.themes_help_text", "Themes help text")));
		themesHelp.add(paragraph);
		themes.add(themesHelp);
		
		ListItem edit = new ListItem();
		edit.setStyleClass("adminEditMode");
		edit.add(new Text(iwrb.getLocalizedString("admin_mode.edit", "Edit")));
		if (iwc.hasRole(StandardRoles.ROLE_KEY_ADMIN) || iwc.hasRole(StandardRoles.ROLE_KEY_EDITOR)) {
			list.add(edit);
		}
		
		Layer editHelp = new Layer();
		editHelp.setStyleClass("modeHelper");
		editHelp.add(new Heading1(iwrb.getLocalizedString("admin_mode.edit_help", "Edit mode")));
		paragraph = new Paragraph();
		paragraph.add(new Text(iwrb.getLocalizedString("admin_mode.edit_help_text", "Edit help text")));
		editHelp.add(paragraph);
		edit.add(editHelp);
		
		ListItem content = new ListItem();
		content.setStyleClass("adminContentMode");
		content.add(new Text(iwrb.getLocalizedString("admin_mode.content", "Content")));
		list.add(content);
		
		Layer contentHelp = new Layer();
		contentHelp.setStyleClass("modeHelper");
		contentHelp.add(new Heading1(iwrb.getLocalizedString("admin_mode.content_help", "Content mode")));
		paragraph = new Paragraph();
		paragraph.add(new Text(iwrb.getLocalizedString("admin_mode.content_help_text", "Content help text")));
		contentHelp.add(paragraph);
		content.add(contentHelp);
		ListItem preview = new ListItem();
		preview.setStyleClass("adminPreviewMode");
		preview.add(new Text(iwrb.getLocalizedString("admin_mode.preview", "Preview")));
		list.add(preview);
		
		Layer previewHelp = new Layer();
		previewHelp.setStyleClass("modeHelper");
		previewHelp.add(new Heading1(iwrb.getLocalizedString("admin_mode.preview_help", "Preview mode")));
		paragraph = new Paragraph();
		paragraph.add(new Text(iwrb.getLocalizedString("admin_mode.preview_help_text", "Preview help text")));
		previewHelp.add(paragraph);
		preview.add(previewHelp);
		
		AdminToolbarSession session = ELUtil.getInstance().getBean(AdminToolbarSession.class);
		if (session.getMode() != null) {
			String mode = session.getMode();
			if (mode.equals("isThemesAdmin")) {
				themes.setStyleClass("selected");
			}
			else if (mode.equals("isEditAdmin")) {
				edit.setStyleClass("selected");
			}
			else if (mode.equals("isContentAdmin")) {
				content.setStyleClass("selected");
			}
			else {
				preview.setStyleClass("selected");
			}
			
			PresentationUtil.addJavaScriptActionToBody(iwc, new StringBuilder("AdminCoreHelper.currentMode = '").append(mode).append("';").toString());
		}
		else {
			preview.setStyleClass("selected");
		}		
	}

	/* (non-Javadoc)
	 * @see com.idega.presentation.PresentationObject#getBundleIdentifier()
	 */
	@Override
	public String getBundleIdentifier() {
		return BuilderConstants.IW_BUNDLE_IDENTIFIER;
	}
}