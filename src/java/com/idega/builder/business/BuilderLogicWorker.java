package com.idega.builder.business;

import com.idega.slide.business.IWSlideSession;

public class BuilderLogicWorker implements Runnable {
	
	private IBXMLPage page = null;
	private IWSlideSession session = null;
	
	public BuilderLogicWorker(IBXMLPage page, IWSlideSession session) {
		this.page = page;
		this.session = session;
	}

	public void run() {
		if (page == null) {
			return;
		}
		try {
			page.store(session);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
