/*
 * Created on 11.5.2004
 */
package com.idega.builder.dynamicpagetrigger.util;

import com.idega.core.builder.data.ICPage;
import com.idega.presentation.IWContext;


/**
 * Title: DPTCrawlable
 * Description:
 * Copyright: Copyright (c) 2004
 * Company: idega Software
 * @author 2004 - idega team - <br><a href="mailto:gummi@idega.is">Gudmundur Agust Saemundsson</a><br>
 * @version 1.0
 */
public interface DPTCrawlable {
	public int getLinkedDPTTemplateID();
	public String getLinkedDPTPageName(IWContext iwc);
	public void setLinkedDPTPage(ICPage page);
}
