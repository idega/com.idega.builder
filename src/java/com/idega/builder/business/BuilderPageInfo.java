/*
 * $Id: BuilderPageInfo.java,v 1.1 2004/12/20 08:55:07 tryggvil Exp $
 * Created on 17.12.2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.builder.business;


/**
 * 
 *  Last modified: $Date: 2004/12/20 08:55:07 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:tryggvil@idega.com">Tryggvi Larusson</a>
 * @version $Revision: 1.1 $
 */
public interface BuilderPageInfo {
	
	public String getPageFormat();
	public String getPageId();
	public void load();
	public boolean isComponentBased();
	public boolean isUriBased();
}
