/**
 * 
 */
package com.idega.builder.presentation;

import java.util.logging.Level;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import com.idega.block.web2.business.Web2Business;
import com.idega.builder.bean.AdminToolbarSession;
import com.idega.builder.business.BuilderConstants;
import com.idega.core.accesscontrol.business.StandardRoles;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.IWContext;
import com.idega.presentation.Layer;
import com.idega.presentation.PresentationObjectTransitional;
import com.idega.presentation.text.ListItem;
import com.idega.presentation.text.Lists;
import com.idega.presentation.text.Text;
import com.idega.util.CoreConstants;
import com.idega.util.PresentationUtil;
import com.idega.util.expression.ELUtil;


/**
 * <p>
 * TODO laddi Describe Type AdminToolbar
 * </p>
 *  Last modified: $Date: 2008/11/17 08:42:42 $ by $Author: laddi $
 * 
 * @author <a href="mailto:laddi@idega.com">laddi</a>
 * @version $Revision: 1.1 $
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
		PresentationUtil.addJavaScriptSourceLineToHeader(iwc, getBundle(iwc).getVirtualPathWithFileNameString("javascript/AdminCore.js"));
		
		Layer layer = new Layer();
		layer.setID("adminTopLayer");
		add(layer);
		
		try{
			UIComponent login = (UIComponent) Class.forName("com.idega.block.login.presentation.Login2").newInstance();
			layer.add(login);
		}
		catch(ClassNotFoundException cnfe){
			this.getLogger().log(Level.SEVERE, cnfe.getMessage(), cnfe);
		}
		catch (InstantiationException ie) {
			this.getLogger().log(Level.SEVERE, ie.getMessage(), ie);
		}
		catch (IllegalAccessException iae) {
			this.getLogger().log(Level.SEVERE, iae.getMessage(), iae);
		}
		
		Lists list = new Lists();
		layer.add(list);
		
		ListItem edit = new ListItem();
		edit.setStyleClass("adminEditMode");
		edit.add(new Text(iwrb.getLocalizedString("admin_mode.edit", "Edit")));
		if (iwc.hasRole(StandardRoles.ROLE_KEY_ADMIN) || iwc.hasRole(StandardRoles.ROLE_KEY_EDITOR)) {
			list.add(edit);
		}
		
		ListItem content = new ListItem();
		content.setStyleClass("adminContentMode");
		content.add(new Text(iwrb.getLocalizedString("admin_mode.content", "Content")));
		list.add(content);
		
		ListItem preview = new ListItem();
		preview.setStyleClass("adminPreviewMode");
		preview.add(new Text(iwrb.getLocalizedString("admin_mode.preview", "Preview")));
		list.add(preview);
		
		AdminToolbarSession session = ELUtil.getInstance().getBean(AdminToolbarSession.class);
		if (session.getMode() != null) {
			String mode = session.getMode();
			if (mode.equals("isEditAdmin")) {
				edit.setStyleClass("selected");
			}
			else if (mode.equals("isContentAdmin")) {
				content.setStyleClass("selected");
			}
			else {
				preview.setStyleClass("selected");
			}
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