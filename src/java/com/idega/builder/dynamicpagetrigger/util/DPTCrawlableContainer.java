package com.idega.builder.dynamicpagetrigger.util;

import java.util.Collection;

public interface DPTCrawlableContainer {

	public void setRootId(int rootId);
	public int getRootId();
	
	public int getICObjectInstanceID();
	
	public Collection getDPTCrawlables();
}
