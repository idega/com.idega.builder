/*
 * $Id: BuilderRepositoryListenerBean.java,v 1.6 2008/07/11 07:31:00 valdas Exp $
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

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.idega.util.CoreConstants;

@Service
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class BuilderRepositoryListenerBean implements BuilderRepositoryListener {

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
					BuilderLogic.getInstance().clearAllCachedPages();
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