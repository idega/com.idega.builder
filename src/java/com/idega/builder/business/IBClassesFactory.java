package com.idega.builder.business;

import com.idega.block.media.presentation.ImageInserter;
import com.idega.core.builder.business.BuilderClassesImplFactory;
import com.idega.core.builder.business.BuilderImageInserter;

/**
 * <p>Title: idegaWeb</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: idega Software</p>
 * @author <a href="thomas@idega.is">Thomas Hilbig</a>
 * @version 1.0
 * Created on Jun 10, 2004
 */
public class IBClassesFactory implements BuilderClassesImplFactory {
	
	/* (non-Javadoc)
	 * @see com.idega.core.builder.business.BuilderClassesImplFactory#createImageInserterImpl()
	 */
	public BuilderImageInserter createImageInserterImpl() {
		return new ImageInserter();
	}
}
