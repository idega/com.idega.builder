/*
 * Created on 17.5.2004
 */
package com.idega.builder.dynamicpagetrigger.presentation;

import com.idega.builder.dynamicpagetrigger.util.DPTCrawlable;
import com.idega.core.builder.data.ICPage;
import com.idega.presentation.IWContext;
import com.idega.presentation.text.Link;



/**
 * Title: DPTCrawlableLink
 * Description:
 * Copyright: Copyright (c) 2004
 * Company: idega Software
 * @author 2004 - idega team - <br><a href="mailto:gummi@idega.is">Gudmundur Agust Saemundsson</a><br>
 * @version 1.0
 */
public class DPTCrawlableLink extends Link implements DPTCrawlable {
	private int dptTemplateId = 0;
	
	
	public void setDPTTemplateId(int id) {
		dptTemplateId = id;
	}

	public int getLinkedDPTTemplateID() {
		return dptTemplateId;
	}

	public void setDPTTemplateId(ICPage page) {
		dptTemplateId = page.getID();
	}
	
	
	/* (non-Javadoc)
	 * @see com.idega.builder.dynamicpagetrigger.util.DPTCrawlable#setLinkedDPTPage(int)
	 */
	public void setLinkedDPTPageID(int pageId) {
		this.setPage(pageId);
	}
	
	public String getLinkedDPTPageName(IWContext iwc) {
		String toReturn = this.getText();
		if(toReturn==null) {
			toReturn = this.getLocalizedText(iwc);
		}
		return toReturn;
	}

}
