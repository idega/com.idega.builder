package com.idega.builder;

import com.idega.builder.presentation.InvisibleInBuilder;
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
		ImplementorRepository repository = ImplementorRepository.getInstance();
		repository.addImplementor(InvisibleInBuilder.class, Applet.class);
		repository.addImplementor(InvisibleInBuilder.class, GenericPlugin.class);
		repository.addImplementor(InvisibleInBuilder.class, DropdownMenu.class);
	}

	
	public void stop(IWBundle starterBundle) {
		// nothing to do
	}
}
