package com.idega.builder;

import com.idega.builder.business.IBClassesFactory;
import com.idega.core.builder.business.BuilderClassesFactory;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWBundleStartable;

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
		BuilderClassesFactory.setBuilderClassesFactory(IBClassesFactory.class);
	}

	
	public void stop(IWBundle starterBundle) {
		// nothing to do
	}
}
