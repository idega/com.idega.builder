package com.idega.builder.business;

import java.util.ArrayList;
import java.util.List;

public class BuilderTransformerBean implements BuilderTransformer {
	
	private List<String> transformedRegions = new ArrayList<String>();

	public boolean isRegionTransformed(String key) {
		if (key == null) {
			return false;
		}
		
		if (transformedRegions.contains(key)) {
			System.out.println("**************************************   REGION: " + key + " was transformed!   **********************************************");
			return true;
		}
		return false;
	}

	public void markRegionAsTranformed(String key) {
		if (key == null) {
			return;
		}
		
		transformedRegions.add(key);
	}

}
