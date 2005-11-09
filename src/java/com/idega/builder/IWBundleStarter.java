package com.idega.builder;

import java.util.Collection;
import java.util.Iterator;
import java.util.logging.Logger;
import javax.ejb.FinderException;
import com.idega.builder.business.ComponentPropertyHandler;
import com.idega.builder.business.IBMainServiceBean;
import com.idega.builder.business.IBPropertyHandler;
import com.idega.builder.business.PageUrl;
import com.idega.builder.data.IBDomainBMPBean;
import com.idega.builder.data.IBPageBMPBean;
import com.idega.builder.dynamicpagetrigger.data.DynamicPageTrigger;
import com.idega.builder.presentation.InvisibleInBuilder;
import com.idega.builder.view.BuilderRootViewNode;
import com.idega.business.IBOLookup;
import com.idega.core.builder.business.BuilderService;
import com.idega.core.builder.data.ICDomain;
import com.idega.core.builder.data.ICDynamicPageTrigger;
import com.idega.core.builder.data.ICPage;
import com.idega.core.builder.data.ICPageHome;
import com.idega.core.view.KeyboardShortcut;
import com.idega.core.view.ViewManager;
import com.idega.data.IDOLookup;
import com.idega.data.IDOLookupException;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWBundleStartable;
import com.idega.idegaweb.IWMainApplication;
import com.idega.presentation.Applet;
import com.idega.presentation.GenericPlugin;
import com.idega.presentation.ui.DropdownMenu;
import com.idega.repository.data.ImplementorRepository;
import com.idega.repository.data.SingletonRepository;

/**
 * <p>Title: idegaWeb</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: idega Software</p>
 * @author <a href="thomas@idega.is">Thomas Hilbig</a>
 * @version 1.0
 * Created on Jun 10, 2004
 */
public class IWBundleStarter implements IWBundleStartable {
	
	public static final String BUILDER_ROOT_VIEW_NODE_NAME = "pages";

	static Logger log = Logger.getLogger(IWBundleStarter.class.getName());
	
	public void start(IWBundle starterBundle) {
		
		// implementors
		ImplementorRepository repository = ImplementorRepository.getInstance();
		
		repository.addImplementor(InvisibleInBuilder.class, Applet.class);
		repository.addImplementor(InvisibleInBuilder.class, GenericPlugin.class);
		repository.addImplementor(InvisibleInBuilder.class, DropdownMenu.class);
		
		repository.addImplementor(ICDynamicPageTrigger.class, DynamicPageTrigger.class);
		
		// services registration
		IBOLookup.registerImplementationForBean(ICDomain.class, IBDomainBMPBean.class);
		IBOLookup.registerImplementationForBean(ICPage.class, IBPageBMPBean.class);
		IBOLookup.registerImplementationForBean(BuilderService.class, IBMainServiceBean.class);
		
		//Registering the views:
		//This is the way it should be but doesn't work because of the startTemporaryBundleStarers() method in IWMainApplicationStarter
		if(starterBundle!=null){
			IWMainApplication iwma = starterBundle.getApplication();
			//IWMainApplication iwma = IWMainApplication.getDefaultIWMainApplication();
			ViewManager viewManager = ViewManager.getInstance(iwma);
			BuilderRootViewNode pagesViewNode = new BuilderRootViewNode(BUILDER_ROOT_VIEW_NODE_NAME,viewManager.getApplicationRoot());
			pagesViewNode.setKeyboardShortcut(new KeyboardShortcut("p"));
			
		}
		
		updateBuilderPageUris();
		
	}

	/**
	 * This method updates the ib_page table with creating a generated URI for all pages that had it set null.
	 */
	private void updateBuilderPageUris() {
		
		try {
			ICPageHome pHome = (ICPageHome)IDOLookup.getHome(ICPage.class);
			Collection pages = pHome.findAllPagesWithoutUri();
			int domainId=-1;
			for (Iterator iter = pages.iterator(); iter.hasNext();) {
				ICPage page = (ICPage) iter.next();
				//TODO: implemennt support for domainId:
				try{
					PageUrl newUrl = new PageUrl(page,domainId);
					String newGeneratedUri = newUrl.getGeneratedUrlFromName();
					page.setDefaultPageURI(newGeneratedUri);
					log.info("Updating Builder Page with uri="+newGeneratedUri);
					page.store();
				}
				catch(Exception e){
					log.throwing("IWBundleStarter","updateBuilderPageUris",e);
				}
			}
			
		}
		catch (IDOLookupException e) {
		}
		catch (FinderException e) {
			log.throwing("IWBundleStarter","updateBuilderPageUris",e);
		}
		
	}

	public void stop(IWBundle starterBundle) {
		SingletonRepository repository = SingletonRepository.getRepository();
		repository.unloadInstance(ComponentPropertyHandler.class);
		repository.unloadInstance(IBPropertyHandler.class);
	}
}
