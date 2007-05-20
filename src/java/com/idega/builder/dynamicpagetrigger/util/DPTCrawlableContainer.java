package com.idega.builder.dynamicpagetrigger.util;

import java.util.Collection;

public interface DPTCrawlableContainer {

	public String getId();
	public int getICObjectInstanceID();
	
	public Collection getDPTCrawlables();
}
