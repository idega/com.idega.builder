/*
 * $Id: BuilderPage.java,v 1.2.2.1 2007/01/12 19:32:40 idegaweb Exp $
 * Created on 16.12.2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.builder.tag;

import java.util.Map;
import com.idega.presentation.BuilderPageFacetMap;
import com.idega.presentation.Page;


/**
 * 
 *  Last modified: $Date: 2007/01/12 19:32:40 $ by $Author: idegaweb $
 * 
 * @author <a href="mailto:tryggvil@idega.com">Tryggvi Larusson</a>
 * @version $Revision: 1.2.2.1 $
 */
public class BuilderPage extends Page {

	/**
	 * 
	 */
	public BuilderPage() {
		super();
		//setTransient(true);
	}

	/**
	 * @param s
	 */
	public BuilderPage(String s) {
		super(s);
		// TODO Auto-generated constructor stub
	}
	
	
	public Map getFacets(){
		if(this.facetMap==null){
			this.facetMap = new BuilderPageFacetMap(this);
		}
		return this.facetMap;
	}
}
