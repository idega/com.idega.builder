package com.idega.builder.business;

import com.idega.user.data.bean.User;

public class BuilderLogicWorker implements Runnable {

	private IBXMLPage page = null;
	private User user = null;

	public BuilderLogicWorker(IBXMLPage page, User user) {
		this.page = page;
		this.user = user;
	}

	@Override
	public void run() {
		if (page == null || user == null) {
			return;
		}
		try {
			page.store();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}