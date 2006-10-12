/*
 * $Id: BuilderSlideListenerBean.java,v 1.2 2006/10/12 17:49:12 valdas Exp $
 * Created on 29.5.2006 in project com.idega.builder
 *
 * Copyright (C) 2006 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.builder.business;

import com.idega.business.IBOServiceBean;
import com.idega.slide.business.IWContentEvent;
import com.idega.slide.business.IWSlideChangeListener;


/**
 * <p>
 * TODO tryggvil Describe Type BuilderSlideListener
 * </p>
 *  Last modified: $Date: 2006/10/12 17:49:12 $ by $Author: valdas $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.2 $
 */
public class BuilderSlideListenerBean extends IBOServiceBean implements IWSlideChangeListener {
	
	/**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = 2891763087583075763L;

	public void onSlideChange(IWContentEvent contentEvent){
		String uri = contentEvent.getContentEvent().getUri();
		if(uri.startsWith("/files/cms/pages/")){
			getBuilderLogic().clearAllCachedPages();
		}
	}
	
	protected BuilderLogic getBuilderLogic(){
		return BuilderLogic.getInstance();
	}
}
