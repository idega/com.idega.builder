/*
 * $Id: BuilderSlideListenerBean.java,v 1.6 2008/07/11 07:31:00 valdas Exp $
 * Created on 29.5.2006 in project com.idega.builder
 *
 * Copyright (C) 2006 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.builder.business;

import javax.jcr.RepositoryException;
import javax.jcr.observation.Event;
import javax.jcr.observation.EventIterator;

import com.idega.business.IBOServiceBean;
import com.idega.util.CoreConstants;


/**
 * <p>
 * TODO tryggvil Describe Type BuilderSlideListener
 * </p>
 *  Last modified: $Date: 2008/07/11 07:31:00 $ by $Author: valdas $
 *
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.6 $
 */
public class BuilderRepositoryListenerBean extends IBOServiceBean implements BuilderRepositoryListener {

	private static final long serialVersionUID = 2891763087583075763L;

	protected BuilderLogic getBuilderLogic(){
		return BuilderLogic.getInstance();
	}

	@Override
	public void onEvent(EventIterator events) {
		if (events == null)
			return;

		try {
			for (; events.hasNext();) {
				Event event = events.nextEvent();
				String uri = event.getPath();
				if (uri.startsWith(CoreConstants.PAGES_PATH) && uri.indexOf("idega_theme") == -1 && uri.indexOf("article_viewer_template") == -1 &&
						uri.indexOf("idega_video_page") == -1 && uri.indexOf("egov") == -1) {
					getBuilderLogic().clearAllCachedPages();
				}
			}
		} catch (RepositoryException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String getPath() {
		return CoreConstants.PAGES_PATH;
	}

	@Override
	public int getEventTypes() {
		return Event.NODE_ADDED | Event.NODE_MOVED | Event.NODE_REMOVED;
	}
}