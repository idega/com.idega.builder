package com.idega.builder.business;

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
public class IBClassesFactory  {
	
	/* (non-Javadoc)
	 * @see com.idega.core.builder.business.BuilderClassesFactory#createImageInserterImpl()
	 */
	public IBImageInserter createImageInserterImpl() {
		try {
			IBImageInserter inserter = (IBImageInserter) ImplementorRepository.getInstance().getImplementor(IBImageInserter.class, this.getClass());
			return inserter;
		}
		catch (ClassNotFoundException ex) {
			throw new RuntimeException("[IBClassesFactory] A IBImageInserter could not be created");
		} 
		catch (InstantiationException e) {
			throw new RuntimeException("[IBClassesFactory] A IBImageInserter could not be instanciated (does a default constructor exist?)");
		} 
		catch (IllegalAccessException e) {
			throw new RuntimeException("[IBClassesFactory] A IBImageInserter could not be instanciated, access problem, (are there only private constructors?)");
		}
	}

	/* (non-Javadoc)
	 * @see com.idega.core.builder.business.BuilderClassesFactory#createFileChooserImpl()
	 */
	public IBFileChooser createFileChooserImpl() {
		try {
			IBFileChooser fileChooser = (IBFileChooser) ImplementorRepository.getInstance().getImplementor(IBFileChooser.class, this.getClass());
			return fileChooser;
		}
		catch (ClassNotFoundException ex) {
			throw new RuntimeException("[IBClassesFactory] A IBFileChooser could not be created");
		} 
		catch (InstantiationException e) {
			throw new RuntimeException("[IBClassesFactory] A IBFileChooser could not be instanciated (does a default constructor exist?)");
		} 
		catch (IllegalAccessException e) {
			throw new RuntimeException("[IBClassesFactory] A IBFileChooser could not be instanciated, access problem, (are there only private constructors?)");
		}
	}

}
