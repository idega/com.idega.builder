/*
 *  $Id: IBApplication.java,v 1.101 2007/05/07 14:34:23 valdas Exp $
 *
 *  Copyright (C) 2001 Idega hf. All Rights Reserved.
 *
 *  This software is the proprietary information of Idega hf.
 *  Use is subject to license terms.
 *
 */
package com.idega.builder.app;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Vector;
import javax.faces.context.FacesContext;
import com.idega.builder.business.BuilderConstants;
import com.idega.builder.business.BuilderLogic;
import com.idega.builder.business.CachedBuilderPage;
import com.idega.builder.business.PageTreeNode;
import com.idega.builder.presentation.IBCreatePageWindow;
import com.idega.builder.presentation.IBDeletePageWindow;
import com.idega.builder.presentation.IBMovePageWindow;
import com.idega.builder.presentation.IBPageExporterWindow;
import com.idega.builder.presentation.IBPageImporterWindow;
import com.idega.builder.presentation.IBPermissionWindow;
import com.idega.builder.presentation.IBPropertiesWindow;
import com.idega.builder.presentation.IBSourceView;
import com.idega.core.accesscontrol.business.AccessController;
import com.idega.core.accesscontrol.business.StandardRoles;
import com.idega.core.builder.business.BuilderService;
import com.idega.core.builder.business.BuilderServiceFactory;
import com.idega.core.localisation.business.ICLocaleBusiness;
import com.idega.core.localisation.business.LocaleSwitcher;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWConstants;
import com.idega.idegaweb.IWMainApplication;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.FrameSet;
import com.idega.presentation.IWContext;
import com.idega.presentation.Image;
import com.idega.presentation.Page;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.Script;
import com.idega.presentation.Table;
import com.idega.presentation.app.IWApplication;
import com.idega.presentation.app.IWApplicationComponent;
import com.idega.presentation.text.Link;
import com.idega.presentation.text.Text;
import com.idega.presentation.ui.DropdownMenu;
import com.idega.presentation.ui.IFrame;
import com.idega.presentation.ui.TreeViewer;
/**
 *@author     <a href="tryggvi@idega.is">Tryggvi Larusson</a>
 *@created    11. mars 2002
 *@version    1.0
 */
public class IBApplication extends IWApplication {
	public static final String TOOLBAR_ITEMS = "ib_application_toolbar";
	private final static String IB_FRAMESET1_FRAME = "ib_frameset1";
	private final static String IB_FRAMESET2_FRAME = "ib_frameset2";
	public final static String IB_TOOLBAR_FRAME = "ib_toolbar";
	public final static String IB_CONTENT_FRAME = "ib_content";
	public final static String IB_STATUS_FRAME = "ib_status";
	public final static String IB_LEFT_MENU_FRAME = "ib_left_menu";
	private final static String LINK_STYLE = "font-family:Arial,Helvetica,sans-serif;font-size:8pt;color:#000000;text-decoration:none;";
	private final static String LINK_HOVER_STYLE = "font-family:Arial,Helvetica,sans-serif;font-size:8pt;color:#FF8008;text-decoration:none;";
	private final static String ACTION_BUILDER = "builder";
	private final static String ACTION_TEMPLATES = "templates";
	private final static String ACTION_SETTINGS = "settings";
	// unused private final static String ACTION_HELP = "help";
	protected static boolean noCurtain = false;
	// unused private static String URL = "";
	public final static String IB_BUNDLE_IDENTIFIER = "com.idega.builder";
	//private final static String CONTENT_PREVIEW_URL = com.idega.idegaweb.IWMainApplication.BUILDER_SERVLET_URL + "?view=preview";
	//private final static String CONTENT_EDIT_URL = com.idega.idegaweb.IWMainApplication.BUILDER_SERVLET_URL + "?view=builder";
	/**
	 *
	 */
	public IBApplication() {
		super("idegaWeb Builder");
		super.setResizable(true);
		super.setWidth(900);
		super.setHeight(700);
	}
	protected static String getContentEditURL(IWContext iwc) {
		//return iwc.getIWMainApplication().getBuilderServletURI() + "?view=builder";
		String urlString = BuilderLogic.getInstance().getCurrentIBPageURL(iwc);
	    //String urlString = iwc.getIWMainApplication().getBuilderPagePrefixURI();
	    if(urlString.indexOf("?")==-1){
			return urlString+ "?view=builder";
		}
		else{
			return urlString+ "&view=builder";
		}
	}
	protected static String getContentPreviewURL(IWContext iwc) {
		//return iwc.getIWMainApplication().getBuilderServletURI() + "?view=preview";
		String urlString = BuilderLogic.getInstance().getCurrentIBPageURL(iwc);
	    //String urlString = iwc.getIWMainApplication().getBuilderPagePrefixURI();
	    if(urlString.indexOf("?")==-1){
			return urlString+ "?view=preview";
		}
		else{
			return urlString+ "&view=preview";
		}
	}
	
	protected static String getContentPDFPreviewURL(IWContext iwc) {
			return iwc.getIWMainApplication().getTranslatedURIWithContext("/servlet/IWPDFServlet?view=pdfpreview");
	}
	/**
	 * Description of the Method
	 *
	 * @param  iwc  Description of the Parameter
	 * @return      Description of the Return Value
	 */
	static boolean startupInProgress(IWContext iwc) {
		List l = (List) iwc.getSessionAttribute("ib_startup_class_list");
		if (l == null) {
			return false;
		}
		return true;
	}
	/**
	 *  Description of the Method
	 *
	 * @param  iwc  Description of the Parameter
	 * @param  c    Description of the Parameter
	 */
	static void endStartup(IWContext iwc, Class c) {
		List l = (List) iwc.getSessionAttribute("ib_startup_class_list");
		if (l != null) {
			l.remove(c);
			Iterator iter = l.iterator();
			if (!iter.hasNext()) {
				iwc.removeSessionAttribute("ib_startup_class_list");
			}
		}
	}
	/**
	 *  Description of the Method
	 *
	 *@param  iwc  Description of the Parameter
	 */
	public static void startIBApplication(IWContext iwc) {
		BuilderLogic.getInstance().startBuilderSession(iwc);
		//To prevent constant realoding when many frames are loaded at the same time
		List l = (List) iwc.getSessionAttribute("ib_startup_class_list");
		if (l == null) {
			l = new Vector();
			iwc.setSessionAttribute("ib_startup_class_list", l);
		}
		l.add(IBToolBar.class);
	}
	/**
	 *  Description of the Method
	 *
	 *@param  iwc  Description of the Parameter
	 */
	static void startLeftMenu(IWContext iwc) {
		List l = (List) iwc.getSessionAttribute("ib_startup_class_list");
		if (l == null) {
			l = new Vector();
			iwc.setSessionAttribute("ib_startup_class_list", l);
		}
		l.add(PageTree.class);
		l.add(TemplateTree.class);
	}
	/**
	 *  Description of the Method
	 *
	 *@param  iwc  Description of the Parameter
	 */
	public void main(IWContext iwc) {
		startIBApplication(iwc);
		
	    IWMainApplication iwma = iwc.getIWMainApplication();
	    if(iwma.getProductInfo().isMajorPlatformVersionEqualOrHigherThan(3)){
	    		//Not add the top for new versions
	    }
	    else{
			add(IBBanner.class);
	    }
	    
		if (iwc.getParameter("toolbar") != null) {
			iwc.setSessionAttribute("toolbar", iwc.getParameter("toolbar"));
		}
		if (iwc.getSessionAttribute("toolbar") != null) {
			String toolbar = (String) iwc.getSessionAttribute("toolbar");
			if (toolbar.equalsIgnoreCase("remove")) {
				noCurtain = true;
			}
			else if (toolbar.equalsIgnoreCase("add")) {
				noCurtain = false;
			}
		}
		// unused URL = iwc.getRequestURI();
		if (noCurtain) {
			add(FrameSet2.class);
			setScrolling(2, true);
		}
		else {
			add(FrameSet1.class);
			setScrolling(2, false);
		}
		setSpanPixels(1, 34);
		setScrolling(1, false);
		setSpanAdaptive(2);
		setFrameName(2, IB_FRAMESET1_FRAME);
	}
	/**
	 *@author     palli
	 *@created    11. mars 2002
	 */
	public static class FrameSet1 extends FrameSet {
		/**
		 */
		public FrameSet1() {
			add(IBLeftMenu.class);
			add(FrameSet2.class);
			setScrolling(1, false);
			setFrameName(1, IB_LEFT_MENU_FRAME);
			setFrameName(2, IB_FRAMESET2_FRAME);
			setSpanPixels(1, 180);
			setSpanAdaptive(2);
			setNoresize(1, true);
			setNoresize(2, true);
			setHorizontal();
		}
	}
	/**
	 *@author     palli
	 *@created    11. mars 2002
	 */
	public static class FrameSet2 extends FrameSet {
		/**
		 */
		public FrameSet2() {
			IWContext iwc = IWContext.getInstance();
			initFrames(iwc);
		}
		public void main(IWContext iwc){
			//initFrames(iwc);
		}
		public void initFrames(IWContext iwc) {
			add(IBToolBar.class);
			//add(CONTENT_EDIT_URL);
			add(getContentEditURL(iwc));
			add(IBStatusBar.class);
			super.setFrameName(1, IB_TOOLBAR_FRAME);
			super.setFrameName(2, IB_CONTENT_FRAME);
			super.setFrameName(3, IB_STATUS_FRAME);
			setNoresize(1, true);
			setNoresize(3, true);
			setSpanPixels(1, 24);
			setScrolling(1, false);
			setScrolling(2, true);
			setSpanAdaptive(2);
			setSpanPixels(3, 25);
			setScrolling(3, false);
		}
		
		public void print(IWContext iwc) throws Exception{
			//debug:
			super.print(iwc);
		}
		
		public void encodeBegin(FacesContext context) throws IOException{
			//debug:
			super.encodeBegin(context);
		}
		public void encodeChildren(FacesContext context) throws IOException{
			//debug:
			super.encodeChildren(context);
		}
		public void encodeEnd(FacesContext context) throws IOException{
			//debug:
			super.encodeEnd(context);
		}
		
	}
	/**
	 *@author     palli
	 *@created    11. mars 2002
	 */
	public static class IBBanner extends Page {
		/**
		 */
		public IBBanner() {
		}
		/**
		 *  Description of the Method
		 *
		 *@param  iwc  Description of the Parameter
		 */
		public void main(IWContext iwc) {
			this.setAllMargins(0);
			setBackgroundColor("#0E2456");
			Table table = new Table(2, 1);
			table.setCellpadding(0);
			table.setCellspacing(0);
			add(table);
			Image image = iwc.getIWMainApplication().getBundle(IB_BUNDLE_IDENTIFIER).getImage("shared/banner/logo.gif");
			table.add(image, 1, 1);
			Image image2 = iwc.getIWMainApplication().getBundle(IB_BUNDLE_IDENTIFIER).getImage("shared/banner/top_image.gif");
			table.add(image2, 2, 1);
			table.setWidth("100%");
			table.setHeight("100%");
			table.setAlignment(2, 1, "right");
			table.setVerticalAlignment(2, 1, "bottom");
			table.setCellpadding(0);
			table.setCellpadding(0);
		}
	}
	/**
	 *  Description of the Class
	 *
	 *@author     palli
	 *@created    11. mars 2002
	 */
	public static class PageTree extends Page {
		/**
		 *  Constructor for the PageTree object
		 */
		public PageTree() {
			setBackgroundColor("#FFFFFF");
		}
		/**
		 *  Description of the Method
		 *
		 *@param  iwc  Description of the Parameter
		 */
		public void main(IWContext iwc) {
			setStyles();

			getParentPage().setAllMargins(2);
			try {
				//<<<<<<< IBApplication.java
				TreeViewer viewer = com.idega.builder.business.IBPageHelper.getInstance().getPageTreeViewer(iwc);
				add(viewer);
				viewer.getLocation().setTarget(IB_LEFT_MENU_FRAME + "_pages");
				viewer.getLocation().setApplicationClass(IBApplication.class);
				viewer.getLocation().isInFrameSet(true);
				//        System.out.println("IBApplication: viewer.getLocation() = "+viewer.getLocation());
				
				//String page_id = iwc.getParameter(com.idega.builder.business.BuilderLogic.IB_PAGE_PARAMETER);
				BuilderService bs = BuilderServiceFactory.getBuilderService(iwc);
				String page_id = String.valueOf(bs.getCurrentPageId(iwc));
				if (page_id != null) {
					BuilderLogic.getInstance().setCurrentIBPage(iwc,page_id);
				}
				

				boolean startupInProgress = startupInProgress(iwc);
				if (!startupInProgress && iwc.getParameter("reload") != null) {
					if (noCurtain) {
						
						//getParentPage().setOnLoad("parent.frames['" + IB_FRAMESET1_FRAME + "'].location.reload();");
						
						getParentPage().setOnLoad("parent.frames['" + IB_FRAMESET2_FRAME + "'].frames['" + IB_TOOLBAR_FRAME + "'].location.reload()");
						//getParentPage().setOnLoad("parent.frames['" + IB_FRAMESET2_FRAME + "'].frames['" + IB_CONTENT_FRAME + "'].location.reload()");
						//this is a workaround since the reaload doesn't work:
						getParentPage().setOnLoad("parent.frames['" + IB_FRAMESET2_FRAME + "'].frames['" + IB_CONTENT_FRAME + "'].location.href='"+getContentEditURL(iwc)+"'");
						getParentPage().setOnLoad("parent.frames['" + IB_FRAMESET2_FRAME + "'].frames['" + IB_STATUS_FRAME + "'].location.reload()");
					
					
					}
					else {
						//getParentPage().setOnLoad("parent.parent.frames['" + IB_FRAMESET2_FRAME + "'].location.reload();");
						getParentPage().setOnLoad("parent.parent.frames['" + IB_FRAMESET2_FRAME + "'].frames['" + IB_TOOLBAR_FRAME + "'].location.reload()");
						//getParentPage().setOnLoad("parent.parent.frames['" + IB_FRAMESET2_FRAME + "'].frames['" + IB_CONTENT_FRAME + "'].location.reload()");
						getParentPage().setOnLoad("parent.parent.frames['" + IB_FRAMESET2_FRAME + "'].frames['" + IB_CONTENT_FRAME + "'].location.href='"+getContentEditURL(iwc)+"'");
						getParentPage().setOnLoad("parent.parent.frames['" + IB_FRAMESET2_FRAME + "'].frames['" + IB_STATUS_FRAME + "'].location.reload()");
					}
				}
				
				
				//=======
				//	TreeViewer viewer = com.idega.builder.business.IBPageHelper.getInstance().getPageTreeViewer(iwc);
				//	add(viewer);
				//	viewer.getLocation().setTarget(IB_LEFT_MENU_FRAME+"_pages");
				//	viewer.getLocation().setApplicationClass(IBApplication.class);
				//	viewer.getLocation().isInFrameSet(true);
				//
				//	System.out.println("IBApplication: viewer.getLocation() = "+viewer.getLocation());
				//
				//	String page_id = iwc.getParameter(com.idega.builder.business.BuilderLogic.IB_PAGE_PARAMETER);
				//	if (page_id != null) {
				//	  iwc.setSessionAttribute(com.idega.builder.business.BuilderLogic.SESSION_PAGE_KEY, page_id);
				//	}
				//>>>>>>> 1.67
			}
			catch (Exception e) {
				e.printStackTrace(System.err);
			}
			endStartup(iwc, PageTree.class);
		}
		private void setStyles() {
			if (getParentPage() != null) {
				getParentPage().setStyleDefinition("A", LINK_STYLE);
				//getParentPage().setStyleDefinition("A."+STYLE_NAME+":visited",_linkStyle);
				//getParentPage().setStyleDefinition("A."+STYLE_NAME+":active",_linkStyle);
				getParentPage().setStyleDefinition("A:hover", LINK_HOVER_STYLE);
			}
		}
	}
	/**
	 *  Description of the Class
	 *
	 *@author     palli
	 *@created    11. mars 2002
	 */
	public static class TemplateTree extends Page {
		/**
		 *  Constructor for the TemplateTree object
		 */
		public TemplateTree() {
			setBackgroundColor("#FFFFFF");
		}
		/**
		 *  Description of the Method
		 *
		 *@param  iwc  Description of the Parameter
		 */
		public void main(IWContext iwc) {
			setStyles();
			boolean startupInProgress = startupInProgress(iwc);
			if (!startupInProgress && iwc.getParameter("reload") != null) {
				if (noCurtain) {
					
					//getParentPage().setOnLoad("parent.frames['" + IB_FRAMESET1_FRAME + "'].location.reload();");
					
					getParentPage().setOnLoad("parent.frames['" + IB_FRAMESET2_FRAME + "'].frames['" + IB_TOOLBAR_FRAME + "'].location.reload()");
					//getParentPage().setOnLoad("parent.frames['" + IB_FRAMESET2_FRAME + "'].frames['" + IB_CONTENT_FRAME + "'].location.reload()");
					//this is a workaround since the reaload doesn't work:
					getParentPage().setOnLoad("parent.frames['" + IB_FRAMESET2_FRAME + "'].frames['" + IB_CONTENT_FRAME + "'].location.href='"+getContentEditURL(iwc)+"'");
					getParentPage().setOnLoad("parent.frames['" + IB_FRAMESET2_FRAME + "'].frames['" + IB_STATUS_FRAME + "'].location.reload()");
				
				
				}
				else {
					//getParentPage().setOnLoad("parent.parent.frames['" + IB_FRAMESET2_FRAME + "'].location.reload();");
					getParentPage().setOnLoad("parent.parent.frames['" + IB_FRAMESET2_FRAME + "'].frames['" + IB_TOOLBAR_FRAME + "'].location.reload()");
					//getParentPage().setOnLoad("parent.parent.frames['" + IB_FRAMESET2_FRAME + "'].frames['" + IB_CONTENT_FRAME + "'].location.reload()");
					getParentPage().setOnLoad("parent.parent.frames['" + IB_FRAMESET2_FRAME + "'].frames['" + IB_CONTENT_FRAME + "'].location.href='"+getContentEditURL(iwc)+"'");
					getParentPage().setOnLoad("parent.parent.frames['" + IB_FRAMESET2_FRAME + "'].frames['" + IB_STATUS_FRAME + "'].location.reload()");
				}
			}
			getParentPage().setAllMargins(2);
			try {
				//<<<<<<< IBApplication.java
				TreeViewer viewer = com.idega.builder.business.IBPageHelper.getInstance().getTemplateTreeViewer(iwc);
				add(viewer);
				viewer.getLocation().setTarget(IB_LEFT_MENU_FRAME + "_templates");
				viewer.getLocation().setApplicationClass(IBApplication.class);
				viewer.getLocation().isInFrameSet(true);
				//        System.out.println("IBApplication: viewer.getLocation() = "+viewer.getLocation());
				//String page_id = iwc.getParameter(com.idega.builder.business.BuilderLogic.IB_PAGE_PARAMETER);
				BuilderService bs = BuilderServiceFactory.getBuilderService(iwc);
				String page_id = String.valueOf(bs.getCurrentPageId(iwc));
				if (page_id != null) {
					BuilderLogic.getInstance().setCurrentIBPage(iwc,page_id);
				}
				//=======
				//	TreeViewer viewer = com.idega.builder.business.IBPageHelper.getInstance().getTemplateTreeViewer(iwc);
				//	add(viewer);
				//	viewer.getLocation().setTarget(IB_LEFT_MENU_FRAME+"_templates");
				//	viewer.getLocation().setApplicationClass(IBApplication.class);
				//	viewer.getLocation().isInFrameSet(true);
				//
				//	System.out.println("IBApplication: viewer.getLocation() = "+viewer.getLocation());
				//
				//	String page_id = iwc.getParameter(com.idega.builder.business.BuilderLogic.IB_PAGE_PARAMETER);
				//	if (page_id != null) {
				//	  iwc.setSessionAttribute(com.idega.builder.business.BuilderLogic.SESSION_PAGE_KEY, page_id);
				//	}
				//>>>>>>> 1.67
			}
			catch (Exception e) {
				e.printStackTrace(System.err);
			}
			endStartup(iwc, TemplateTree.class);
		}
		private void setStyles() {
			if (getParentPage() != null) {
				getParentPage().setStyleDefinition("A", LINK_STYLE);
				//getParentPage().setStyleDefinition("A."+STYLE_NAME+":visited",_linkStyle);
				//getParentPage().setStyleDefinition("A."+STYLE_NAME+":active",_linkStyle);
				getParentPage().setStyleDefinition("A:hover", LINK_HOVER_STYLE);
			}
		}
	}
	/**
	 *@author     palli
	 *@created    11. mars 2002
	 */
	public static class IBLeftMenu extends IWApplicationComponent {
		/**
		 */
		public IBLeftMenu() {
		}
		/**
		 *  Description of the Method
		 *
		 *@param  iwc  Description of the Parameter
		 */
		public void main(IWContext iwc) {
			startLeftMenu(iwc);
			setAlignment("left");
			setVerticalAlignment("top");
			//setBackgroundColor(IWConstants.DEFAULT_INTERFACE_COLOR);
			//setLightShadowColor(IWConstants.DEFAULT_LIGHT_INTERFACE_COLOR);
			//setDarkShadowColor(IWConstants.DEFAULT_DARK_INTERFACE_COLOR);
			try {
				Table menuTable = new Table();
//				menuTable.setAlignment(1, 1, "right");
//				Image closeImage = iwc.getIWMainApplication().getBundle(IB_BUNDLE_IDENTIFIER).getImage("toolbar_remove.gif", "toolbar_remove_1.gif", "Hide Curtain", 16, 16);
//				closeImage.setAlignment("right");
//				Link closeLink = new Link(closeImage);
//				closeLink.setTarget(Link.TARGET_PARENT_WINDOW);
//				closeLink.setURL(iwc.getIWMainApplication().getWindowOpenerURI(IBApplication.class));
//				closeLink.addParameter("toolbar", "remove");
//				closeLink.addParameter(Page.IW_FRAME_CLASS_PARAMETER, IBApplication.class);
//				menuTable.add(closeLink, 1, 1);
				Text pageText = new Text("Page Tree:");
				pageText.setStyleAttribute("margin","3px");
				pageText.setFontStyle(IWConstants.BUILDER_FONT_STYLE_SMALL);
				Text templateText = new Text("Template Tree:");
				templateText.setStyleAttribute("margin","3px");
				
				templateText.setFontStyle(IWConstants.BUILDER_FONT_STYLE_SMALL);
				//Text libraryText = new Text("Library Tree:");
				//libraryText.setFontStyle(IWConstants.BUILDER_FONT_STYLE_SMALL);
				IFrame frame = new IFrame("PageTree", PageTree.class);
				frame.setStyleAttribute("margin","3px");
				frame.setWidth(170);
				frame.setHeight(200);
				frame.setScrolling(IFrame.SCROLLING_YES);
				menuTable.add(pageText, 1, 1);
				menuTable.add(Text.getBreak(), 1, 1);
				menuTable.add(frame, 1, 1);
				IFrame frame2 = new IFrame("TemplateTree", TemplateTree.class);
				frame2.setStyleAttribute("margin","3px");
				frame2.setWidth(170);
				frame2.setHeight(200);
				frame2.setScrolling(IFrame.SCROLLING_YES);
				menuTable.add(templateText, 1, 2);
				menuTable.add(Text.getBreak(), 1, 2);
				menuTable.add(frame2, 1, 2);
				/*
				 *  IFrame frame3 = new IFrame("LibraryTree",LibraryTree.class);
				 *  frame3.setWidth(170);
				 *  frame3.setHeight(200);
				 *  frame3.setScrolling(IFrame.SCROLLING_YES);
				 *  menuTable.add(libraryText,1,4);
				 *  menuTable.add(Text.getBreak(),1,4);
				 *  menuTable.add(frame3,1,4);
				 */
				add(menuTable);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	/**
	 *@author     palli
	 *@created    11. mars 2002
	 */
	public static class IBToolBar extends IWApplicationComponent {
		private static final String STYLE_CLASS_TOOLBAR_BUTTON = "toolbarButton";
		private final static String IW_BUNDLE_IDENTIFIER = IB_BUNDLE_IDENTIFIER;
		/**
		 */
		public IBToolBar() {
		}
		/**
		 *@param  iwc  Description of the Parameter
		 */
		public void main(IWContext iwc) {
			/*      if (!startupInProgress) {
				//super.setOnLoad("parent.parent.frames['"+IB_LEFT_MENU_FRAME+"'].location.reload();parent.frames['"+IB_CONTENT_FRAME+"'].location.reload()");
				//the tree reloading in done in the appropriate windows such as in the create new page window
				super.setOnLoad("parent.frames['"+IB_CONTENT_FRAME+"'].location.reload()");
			      }*/
			IWBundle iwb = this.getBundle(iwc);
			IWResourceBundle iwrb = this.getResourceBundle(iwc);
			addStyleSheetURL(iwb.getVirtualPathWithFileNameString("style/builder.css"));
			
			String controlParameter = "builder_controlparameter";
			//setBackgroundColor(IWConstants.DEFAULT_INTERFACE_COLOR);
			//setLightShadowColor(IWConstants.DEFAULT_LIGHT_INTERFACE_COLOR);
			//setDarkShadowColor(IWConstants.DEFAULT_DARK_INTERFACE_COLOR);
			setAllMargins(0);
						
			String action = iwc.getParameter(controlParameter);
			if (action == null) {
				action = ACTION_BUILDER;
			}
			if (action.equals(ACTION_BUILDER)) {
				
				Image separator = iwb.getImage("toolbar_separator.gif");
				separator.setStyleClass(STYLE_CLASS_TOOLBAR_BUTTON);
				
				//Image tool_new = iwb.getImage("shared/toolbar/new.gif", "shared/toolbar/new1.gif", "New Page", 20, 20);
				Image tool_new = iwb.getImage("new_16.gif", iwrb.getLocalizedString("tooltip.new_page","New Page/Template"), 16, 16);
				
				
				Link link_new = new Link(tool_new);
				link_new.setStyleClass(STYLE_CLASS_TOOLBAR_BUTTON);
				
				link_new.setWindowToOpen(IBCreatePageWindow.class);
				add(link_new);
//				
				//BUTTON THAT ARE NOT USED, TODO MAKE SAVE/PUBLISH WORK
//				Image tool_open = iwb.getImage("shared/toolbar/open.gif", "Open Page", 20, 20);
//				tool_open.setHorizontalSpacing(2);
//				Link link_open = new Link(tool_open);
//				toolbarTable.add(tool_open, xpos, 1);
//				Image tool_save = iwb.getImage("shared/toolbar/save.gif", "Save Page", 20, 20);
//				tool_save.setHorizontalSpacing(2);
//				Link link_save = new Link(tool_save);
//				link_save.setWindowToOpen(IBSavePageWindow.class);
//				toolbarTable.add(tool_save, xpos, 1);
//				Image tool_save_as = iwb.getImage("shared/toolbar/saveas.gif", "Save As Page", 20, 20);
//				tool_save_as.setHorizontalSpacing(2);
//				Link link_save_as = new Link(tool_save_as);
//				link_save_as.setWindowToOpen(IBSaveAsPageWindow.class);
//				toolbarTable.add(tool_save_as, xpos, 1);
//				
				
				//Image tool_move = iwb.getImage("shared/toolbar/move.gif", "shared/toolbar/move1.gif", "Move Page", 20, 20);
				Image tool_move = iwb.getImage("move_16.gif", iwrb.getLocalizedString("tooltip.move_page","Move Page/Template"), 16, 16);
			
				Link link_move = new Link(tool_move);
				link_move.setStyleClass(STYLE_CLASS_TOOLBAR_BUTTON);
				link_move.setWindowToOpen(IBMovePageWindow.class);
				add(link_move);
				
				//Image tool_delete = iwb.getImage("shared/toolbar/delete.gif", "shared/toolbar/delete1.gif", "Delete Page", 20, 20);
				Image tool_delete = iwb.getImage("del_16.gif", iwrb.getLocalizedString("tooltip.delete_page","Delete Page/Template"), 16, 16);
				
				Link link_delete = new Link(tool_delete);
				link_delete.setStyleClass(STYLE_CLASS_TOOLBAR_BUTTON);
				link_delete.setWindowToOpen(IBDeletePageWindow.class);
				add(link_delete);
				
				// export pages and templates
				///Image tool_export = iwb.getImage("shared/toolbar/move.gif", "shared/toolbar/move1.gif", "Export Pages", 20, 20);
				Image tool_export = iwb.getImage("exportdata_16.gif", iwrb.getLocalizedString("tooltip.export_pages","Export Pages/Templates"), 16, 16);

				Link link_export = new Link(tool_export);
				link_export.setStyleClass(STYLE_CLASS_TOOLBAR_BUTTON);
				link_export.setWindowToOpen(IBPageExporterWindow.class);
				add(link_export);
				// import pages and templates
				//Image tool_import = iwb.getImage("shared/toolbar/move.gif", "shared/toolbar/move1.gif", "Import Pages", 20, 20);
				Image tool_import = iwb.getImage("importdata_16.gif", iwrb.getLocalizedString("tooltip.import_pages","Import Pages/Templates"), 16, 16);

				Link link_import = new Link(tool_import);
				link_import.setStyleClass(STYLE_CLASS_TOOLBAR_BUTTON);
				link_import.setWindowToOpen(IBPageImporterWindow.class);
				add(link_import);

				PresentationObject propertiesIcon = getPropertiesIcon(iwc);
				add(propertiesIcon);
				PresentationObject permissionIcon = getPermissionIcon(iwc);
				add(permissionIcon);
				add((Image) separator.clone());

//				Image tool_1 = iwb.getImage("shared/toolbar/back.gif", "shared/toolbar/back1.gif", "Go back", 20, 20);
//				tool_1.setHorizontalSpacing(2);
//				Link link_1 = new Link(tool_1);
//				link_1.setURL("javascript:parent.frames['" + IB_CONTENT_FRAME + "'].history.go(-1)");
//				toolbarTable.add(link_1, xpos, 1);
//				Image tool_2 = iwb.getImage("shared/toolbar/forward.gif", "shared/toolbar/forward1.gif", "Go forward", 20, 20);
//				tool_2.setHorizontalSpacing(2);
//				Link link_2 = new Link(tool_2);
//				link_2.setURL("javascript:parent.frames['" + IB_CONTENT_FRAME + "'].history.go(1)");
//				toolbarTable.add(link_2, xpos, 1);
				
				Image tool_3 = iwb.getImage("stop_16.gif", iwrb.getLocalizedString("tooltip.stop_loading","Stop Loading Page"), 16, 16);
				//Image tool_3 = iwb.getImage("shared/toolbar/stop.gif", "shared/toolbar/stop1.gif", "Stop loading", 20, 20);

				Link link_3 = new Link(tool_3);
				link_3.setURL("javascript:parent.frames['" + IB_CONTENT_FRAME + "'].stop()");
				link_3.setStyleClass(STYLE_CLASS_TOOLBAR_BUTTON);
				add(link_3);
				//Image tool_4 = iwb.getImage("shared/toolbar/refresh.gif", "shared/toolbar/refresh1.gif", "Reload page", 20, 20);
				Image tool_4 = iwb.getImage("ref_16.gif", iwrb.getLocalizedString("tooltip.reload_page","Reload Page"), 16, 16);

				Link link_4 = new Link(tool_4);
				link_4.setURL("javascript:parent.frames['" + IB_CONTENT_FRAME + "'].location.reload()");
				link_4.setStyleClass(STYLE_CLASS_TOOLBAR_BUTTON);
				
				add(link_4);
				
				Image leftMenuImage = null;
				if (noCurtain) {
					leftMenuImage = iwb.getImage("shared/toolbar/show_curtain.gif", "shared/toolbar/show_curtain1.gif",iwrb.getLocalizedString("tooltip.show_curtain","Show Curtain"), 16, 16);
				}
				else {
					leftMenuImage = iwb.getImage("shared/toolbar/no_curtain.gif", "shared/toolbar/no_curtain1.gif", iwrb.getLocalizedString("tooltip.show_curtain","Hide Curtain"), 16, 16);
				}
	
				Link leftMenuLink = new Link(leftMenuImage);
				leftMenuLink.setStyleClass(STYLE_CLASS_TOOLBAR_BUTTON);
				leftMenuLink.setTarget(Link.TARGET_PARENT_WINDOW);
				leftMenuLink.setURL(iwc.getIWMainApplication().getWindowOpenerURI(IBApplication.class));
				//leftMenuLink.setTarget(Link.TARGET_TOP_WINDOW);
				if (noCurtain) {
					leftMenuLink.addParameter("toolbar", "add");
				}
				else {
					leftMenuLink.addParameter("toolbar", "remove");
				}
				leftMenuLink.addParameter(Page.IW_FRAME_CLASS_PARAMETER, IBApplication.class);
				
				add(leftMenuLink);
				
				add((Image) separator.clone());
		
				add(getLocaleMenu(iwc));
				/**
				 * @todo Move to extension thingie
				 */
				/*        xpos++;
					toolbarTable.add(separator, xpos, 1);
					xpos++;
					Image tool_export = iwb.getImage("shared/toolbar/delete.gif", "shared/toolbar/delete1.gif", "IShop Export", 20, 20);
					tool_export.setHorizontalSpacing(2);
					Link link_export = new Link(tool_export);
					link_export.setWindowToOpen(is.idega.idegaweb.intershop.presentation.IShopExportPage.class);
					toolbarTable.add(link_export, xpos, 1);*/
				List extension = (List) iwc.getApplicationAttribute(TOOLBAR_ITEMS);
				if (extension != null) {
					Iterator it = extension.iterator();
					while (it.hasNext()) {
						IBToolbarButton b = (IBToolbarButton) it.next();
						
						if (b.getIsSeparator()){
							add((Image) separator.clone());
						}
						else{
							Link bLink = (Link)b.getLink().clone();
							bLink.setStyleClass(STYLE_CLASS_TOOLBAR_BUTTON);
							add(bLink);
						}
					}
				}
			
			}
			endStartup(iwc, IBToolBar.class);
		}
		/**
		 *  Gets the localeMenu attribute of the IBToolBar object
		 *
		 *@param  iwc  Description of the Parameter
		 *@return      The localeMenu value
		 */
		private DropdownMenu getLocaleMenu(IWContext iwc) {
			StringBuffer buffer = new StringBuffer();
			String prefix = iwc.getIWMainApplication().getBuilderPagePrefixURI()+"?";
			buffer.append(IWMainApplication.IdegaEventListenerClassParameter);
			buffer.append("=");
			buffer.append(IWMainApplication.getEncryptedClassName(LocaleSwitcher.class.getName()));
			buffer.append("&");
			buffer.append("view=builder");
			buffer.append("&");
			buffer.append(LocaleSwitcher.languageParameterString);
			buffer.append("=");
			Script script = getParentPage().getAssociatedScript();
			script.addFunction("jumpMenu", "function jumpMenu(targ,selObj,restore){ eval(targ+\".location='\"+selObj.options[selObj.selectedIndex].value+\"'\"); if (restore) selObj.selectedIndex=0; }");
			getParentPage().setAssociatedScript(script);
			String url = prefix + buffer.toString();
			List locales = ICLocaleBusiness.getListOfLocalesJAVA();
			DropdownMenu down = new DropdownMenu(LocaleSwitcher.languageParameterString);
			Iterator iter = locales.iterator();
			while (iter.hasNext()) {
				Locale item = (Locale) iter.next();
				down.addMenuElement(url + item.toString(), item.getDisplayLanguage());
			}
			//IWMainApplication.getBuilderServletURI()
			down.setSelectedElement(url + iwc.getCurrentLocale().toString());
			down.setStyleClass("toolbarLocaleMenu");
			down.setOnChange("javascript:jumpMenu('parent.frames[\\'" + IB_CONTENT_FRAME + "\\']',this,0)");
//			down.setOnChange("javascript:jumpMenu('parent.parent.frames[\\'" + IB_LEFT_MENU_FRAME + "\\']',this,0)");
			return down;
		}
		/**
		 *@param  iwc  Description of the Parameter
		 *@return      The propertiesIcon value
		 */
		public PresentationObject getPropertiesIcon(IWContext iwc) {
			IWBundle iwb = this.getBundle(iwc);
			IWResourceBundle iwrb = this.getResourceBundle(iwc);
			
			//Image image = iwb.getImage("shared/toolbar/page_properties.gif", "shared/toolbar/page_properties1.gif", "Page Properties", 20, 20);
			Image image = iwb.getImage("prefs_16.gif", iwrb.getLocalizedString("tooltip.page_properties","Page Properties"), 16, 16);
			
			Link link = new Link(image);
			link.setStyleClass(STYLE_CLASS_TOOLBAR_BUTTON);
			link.setWindowToOpen(IBPropertiesWindow.class);
			link.addParameter(BuilderConstants.IB_PAGE_PARAMETER, BuilderLogic.getInstance().getCurrentIBPage(iwc));
			link.addParameter(BuilderLogic.IB_CONTROL_PARAMETER, BuilderLogic.ACTION_EDIT);
			//Hardcoded -1 for the top page
			String pageICObjectInstanceID = "-1";
			link.addParameter(BuilderLogic.IC_OBJECT_INSTANCE_ID_PARAMETER, pageICObjectInstanceID);
			return (link);
		}
		/**
		 *  Gets the permissionIcon attribute of the IBToolBar object
		 *
		 *@param  iwc  Description of the Parameter
		 *@return      The permissionIcon value
		 */
		public PresentationObject getPermissionIcon(IWContext iwc) {
			IWBundle iwb = this.getBundle(iwc);
			IWResourceBundle iwrb = this.getResourceBundle(iwc);
			
			//Image image = iwb.getImage("shared/toolbar/permissions.gif", "shared/toolbar/permissions1.gif", "Page Permissions", 20, 20);
			Image image = iwb.getImage("lock_16.gif", iwrb.getLocalizedString("tooltip.page_permissions","Page Permissions"), 16, 16);

			Link link = new Link(image);
			link.setStyleClass(STYLE_CLASS_TOOLBAR_BUTTON);
			link.setWindowToOpen(IBPermissionWindow.class);
			link.addParameter(IBPermissionWindow._PARAMETERSTRING_IDENTIFIER, BuilderLogic.getInstance().getCurrentIBPage(iwc));
			link.addParameter(IBPermissionWindow._PARAMETERSTRING_PERMISSION_CATEGORY, AccessController.CATEGORY_PAGE_INSTANCE);
			return link;
		}
		
/**
		 *@return    The bundleIdentifier value
		 */
		public String getBundleIdentifier() {
			return (IW_BUNDLE_IDENTIFIER);
		}
	}
	/**
	 *@author     palli
	 *@created    11. mars 2002
	 */
	public static class IBStatusBar extends IWApplicationComponent {
		private final static String IW_BUNDLE_IDENTIFIER = IB_BUNDLE_IDENTIFIER;
		/**
		 */
		public IBStatusBar() {
		}
		/**
		 *@param  iwc  Description of the Parameter
		 */
		public void main(IWContext iwc) {
			IWBundle _iwrb = getBundle(iwc);
			String controlParameter = "builder_controlparameter";
			//setBackgroundColor(IWConstants.DEFAULT_INTERFACE_COLOR);
			//setLightShadowColor(IWConstants.DEFAULT_LIGHT_INTERFACE_COLOR);
			//setDarkShadowColor(IWConstants.DEFAULT_DARK_INTERFACE_COLOR);
			Image tilerCell = Table.getTransparentCell(iwc);
			tilerCell.setHeight("100%");
			tilerCell.setWidth(5);
			//setBackgroundColor(com.idega.idegaweb.IWConstants.DEFAULT_LIGHT_INTERFACE_COLOR);
			//Image background = iwb.getImage("status_tiler.gif");
			//setBackgroundImage(background);
			setAllMargins(0);
			Table toolbarTable = new Table(3, 1);
			toolbarTable.setWidth("100%");
			toolbarTable.setHeight("100%");
			toolbarTable.setCellpadding(0);
			toolbarTable.setCellspacing(0);
			toolbarTable.setWidth(2, 1, "100%");
			toolbarTable.setAlignment(3, 1, "right");
			toolbarTable.setVerticalAlignment(3, 1, "top");
			add(toolbarTable);
			String action = iwc.getParameter(controlParameter);
			if (action == null) {
				action = ACTION_BUILDER;
			}
			if (action.equals(ACTION_BUILDER)) {
				/*
				 *  Text text1 = new Text("Status normal"+Text.NON_BREAKING_SPACE+Text.NON_BREAKING_SPACE);
				 *  text1.setFontSize(1);
				 *  text1.setFontColor("Black");
				 */
				Table toolTable = new Table(5, 1);
				toolTable.setWidth("100%");
				toolTable.setCellpadding(0);
				toolTable.setCellspacing(0);
				
				Image editImage = _iwrb.getImage("shared/status/edit1.gif", "Edit", 64, 17);
				editImage.setOnClickImage(_iwrb.getImage("shared/status/edit.gif"));
				Link editLink = new Link(editImage);
				editLink.setTarget(IBApplication.IB_CONTENT_FRAME);
				editLink.setURL(getContentEditURL(iwc));
				toolTable.add(editLink, 1, 1);
				getParentPage().setOnLoad("javascript: swapImage('" + editImage.getName() + "','','" + _iwrb.getImage("shared/status/edit.gif").getURL() + "',1)");
				
				Image previewImage = _iwrb.getImage("shared/status/preview1.gif", "Preview", 64, 17);
				previewImage.setOnClickImage(_iwrb.getImage("shared/status/preview.gif"));
				Link previewLink = new Link(previewImage);
				previewLink.setTarget(IBApplication.IB_CONTENT_FRAME);
				previewLink.setURL(getContentPreviewURL(iwc));
				toolTable.add(previewLink, 2, 1);
				
				/*
				Image previewPDFImage = _iwrb.getImage("shared/status/preview1.gif", "Preview PDF", 64, 17);
				previewPDFImage.setOnClickImage(_iwrb.getImage("shared/status/preview.gif"));
				Link previewPDFLink = new Link(previewImage);
				previewPDFLink.setTarget(IBApplication.IB_CONTENT_FRAME);
				previewPDFLink.setURL(getContentPDFPreviewURL(iwc));
				toolTable.add(previewPDFLink, 3, 1);
				*/
				
				boolean isSuperUser = false;
				isSuperUser = iwc.isSuperAdmin();
				boolean isBuilderUser = iwc.getAccessController().hasRole(StandardRoles.ROLE_KEY_BUILDER,iwc);
				
				/*
				//Display the simple template page
				if (isSuperUser||isBuilderUser) {
					String settingsUrl;
					ViewNode settingsNode = ViewManager.getInstance(iwc).getWorkspaceRoot().getChild("content").getChild("pages").getChild("templatesettings");
					settingsUrl = settingsNode.getURIWithContextPath();
					Image sourceImage = _iwrb.getImage("shared/status/settings1.gif", "Source", 64, 17);
					sourceImage.setOnClickImage(_iwrb.getImage("shared/status/settings.gif"));
					Link sourceLink = new Link(sourceImage);
					sourceLink.setURL(settingsUrl);
					sourceLink.setTarget(IBApplication.IB_CONTENT_FRAME);
					toolTable.add(sourceLink, 4, 1);
				}
				*/
				
				//Display the source tab only if the current user is the SuperUser
				if (isSuperUser||isBuilderUser) {
					Image sourceImage = _iwrb.getImage("shared/status/source1.gif", "Source", 64, 17);
					sourceImage.setOnClickImage(_iwrb.getImage("shared/status/source.gif"));
					Link sourceLink = new Link(sourceImage, IBSourceView.class);
					sourceLink.setTarget(IBApplication.IB_CONTENT_FRAME);
					toolTable.add(sourceLink, 5, 1);
				}
				
				String id=null;
                try {
                    id = Integer.toString(this.getBuilderService(iwc).getCurrentPageId(iwc));
                } catch (RemoteException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                if (id == null) {
					int i_page_id = BuilderLogic.getInstance().getCurrentDomain(iwc).getStartPageID();
					id = Integer.toString(i_page_id);
				}
				String name = null;
				if (id != null && !id.equals("")) {
					java.util.Map tree = PageTreeNode.getTree(iwc);
					Integer pageId = new Integer(id);
					if (tree != null) {
						PageTreeNode node = (PageTreeNode) tree.get(pageId);
						if (node != null) {
							name = Text.NON_BREAKING_SPACE + node.getLocalizedNodeName(iwc);
						}
					}
					if (name == null) {
						tree = PageTreeNode.getTree(iwc);
						if (tree != null) {
							PageTreeNode node = (PageTreeNode) tree.get(pageId);
							if (node != null) {
								name = Text.NON_BREAKING_SPACE + node.getLocalizedNodeName(iwc);
							}
						}
					}
					if (name == null) {
						name = "Page name";
					}
				}
				else {
					name = "Page name";
				}
				//        String name = Text.NON_BREAKING_SPACE + BuilderLogic.getInstance().getCurrentIBXMLPage(iwc).getName();
			
				CachedBuilderPage currentPage = BuilderLogic.getInstance().getPageCacher().getCachedBuilderPage(BuilderLogic.getInstance().getCurrentIBPage(iwc));
				
				String currentPageKey = currentPage.getPageKey();
				String pageUri = currentPage.getPageUri();
				String templateKey = currentPage.getTemplateKey();
				
				//TODO FIX STYLES
				Link uriLink = null;
				if(pageUri!=null){
					String pageUrl = BuilderLogic.getInstance().getIBPageURL(iwc,currentPageKey);
					String linkText = pageUri;
					uriLink = new Link(linkText,pageUrl);
					uriLink.setStyleAttribute(IWConstants.BUILDER_FONT_STYLE_LARGE);
					uriLink.setTarget(Link.TARGET_NEW_WINDOW);
				}
				
				Text pageName = new Text(name);
				pageName.setFontStyle(IWConstants.BUILDER_FONT_STYLE_LARGE);
				toolbarTable.add(tilerCell, 1, 1);
				toolbarTable.add(pageName, 2, 1);
				
				
				if(templateKey!=null){
					CachedBuilderPage template = BuilderLogic.getInstance().getCachedBuilderPage(templateKey);
					if(template!=null){
						String templateNameS = " ["+template.getName()+"]";
						Text templateName = new Text(templateNameS);
						templateName.setFontStyle(IWConstants.BUILDER_FONT_STYLE_LARGE);
						toolbarTable.add(templateName, 2, 1);
					}
				}
				
				if(uriLink!=null){
					Text prefix = new Text("  -  ( ");
					prefix.setFontStyle(IWConstants.BUILDER_FONT_STYLE_LARGE);
					toolbarTable.add(prefix, 2, 1);
					toolbarTable.add(uriLink, 2, 1);
					Text suffix = new Text(" )");
					suffix.setFontStyle(IWConstants.BUILDER_FONT_STYLE_LARGE);
					toolbarTable.add(suffix, 2, 1);
				}
				
				toolbarTable.add(toolTable, 3, 1);
			}
			else if (action.equals(ACTION_TEMPLATES)) {
			}
			else if (action.equals(ACTION_SETTINGS)) {
			}
		}
		/**
		 *@return    The bundleIdentifier value
		 */
		public String getBundleIdentifier() {
			return (IW_BUNDLE_IDENTIFIER);
		}
	}
}