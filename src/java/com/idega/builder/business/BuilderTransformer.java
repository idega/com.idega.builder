package com.idega.builder.business;

import com.idega.business.SpringBeanName;

@SpringBeanName("builderTransformer")
public interface BuilderTransformer {
	
	public void markRegionAsTranformed(String key);

	public boolean isRegionTransformed(String key);
	
}
