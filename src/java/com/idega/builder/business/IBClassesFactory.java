package com.idega.builder.business;

import com.idega.core.builder.business.BuilderClassesFactory;
import com.idega.core.builder.business.BuilderFileChooser;
import com.idega.core.builder.business.BuilderImageInserter;
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
public class IBClassesFactory implements BuilderClassesFactory {
	
	/* (non-Javadoc)
	 * @see com.idega.core.builder.business.BuilderClassesFactory#createImageInserterImpl()
	 */
	public BuilderImageInserter createImageInserterImpl() {
		try {
			BuilderImageInserter inserter = (BuilderImageInserter) ImplementorRepository.getInstance().getImplementor(BuilderImageInserter.class, this.getClass());
			return inserter;
		}
		catch (ClassNotFoundException ex) {
			throw new RuntimeException("[IBClassesFactory] A BuilderImageInserter could not be created");
		}
	}

	/* (non-Javadoc)
	 * @see com.idega.core.builder.business.BuilderClassesFactory#createFileChooserImpl()
	 */
	public BuilderFileChooser createFileChooserImpl() {
		try {
			BuilderFileChooser fileChooser = (BuilderFileChooser) ImplementorRepository.getInstance().getImplementor(BuilderFileChooser.class, this.getClass());
			return fileChooser;
		}
		catch (ClassNotFoundException ex) {
			throw new RuntimeException("[IBClassesFactory] A BuilderFileChooser could not be created");
		}
	}

}
