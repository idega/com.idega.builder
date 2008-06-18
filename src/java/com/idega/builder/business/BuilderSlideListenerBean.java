/*
 * $Id: BuilderSlideListenerBean.java,v 1.4 2008/06/18 14:12:33 valdas Exp $
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
import com.idega.util.CoreConstants;


/**
 * <p>
 * TODO tryggvil Describe Type BuilderSlideListener
 * </p>
 *  Last modified: $Date: 2008/06/18 14:12:33 $ by $Author: valdas $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.4 $
 */
public class BuilderSlideListenerBean extends IBOServiceBean implements IWSlideChangeListener {
	
	/**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = 2891763087583075763L;

	public void onSlideChange(IWContentEvent contentEvent){
		String uri = contentEvent.getContentEvent().getUri();
		if (uri.startsWith(CoreConstants.PAGES_PATH) && uri.indexOf("idega_theme") == -1 && uri.indexOf("article_viewer_template") == -1) {
			getBuilderLogic().clearAllCachedPages();
		}
	}
	
	protected BuilderLogic getBuilderLogic(){
		return BuilderLogic.getInstance();
	}
}
