package com.idega.builder;

import com.idega.builder.business.BuilderConstants;
import com.idega.builder.business.IBMainServiceBean;
import com.idega.builder.data.IBDomainBMPBean;
import com.idega.builder.data.IBPageBMPBean;
import com.idega.builder.dynamicpagetrigger.data.DynamicPageTrigger;
import com.idega.builder.presentation.InvisibleInBuilder;
import com.idega.business.IBOLookup;
import com.idega.core.builder.business.BuilderService;
import com.idega.core.builder.data.ICBuilderConstants;
import com.idega.core.builder.data.ICDomain;
import com.idega.core.builder.data.ICDynamicPageTrigger;
import com.idega.core.builder.data.ICPage;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWBundleStartable;
import com.idega.presentation.Applet;
import com.idega.presentation.GenericPlugin;
import com.idega.presentation.ui.DropdownMenu;
import com.idega.repository.data.ImplementorRepository;

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

	public void start(IWBundle starterBundle) {
		
		// implementors
		ImplementorRepository repository = ImplementorRepository.getInstance();
		
		repository.addImplementor(InvisibleInBuilder.class, Applet.class);
		repository.addImplementor(InvisibleInBuilder.class, GenericPlugin.class);
		repository.addImplementor(InvisibleInBuilder.class, DropdownMenu.class);
		
		repository.addImplementor(ICDynamicPageTrigger.class, DynamicPageTrigger.class);
		repository.addImplementor(ICBuilderConstants.class, BuilderConstants.class);
		
		// services registration
		IBOLookup.registerImplementationForBean(ICDomain.class, IBDomainBMPBean.class);
		IBOLookup.registerImplementationForBean(ICPage.class, IBPageBMPBean.class);
		IBOLookup.registerImplementationForBean(BuilderService.class, IBMainServiceBean.class);
	}

	
	public void stop(IWBundle starterBundle) {
		// nothing to do
	}
}
