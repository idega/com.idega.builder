package com.idega.builder.business;

import java.util.Comparator;

import com.idega.core.component.data.ICObject;

public class ICObjectComparator implements Comparator<ICObject> {

	public int compare(ICObject obj1, ICObject obj2) {
		int result = 0;
		
		String value1 = obj1.getName();
		String value2 = obj2.getName();
		
		if (value1 == null && value2 == null) {
			result = 0;
		}
		else if (value2 == null) {
			result = 1;
		}
		else if (value1 == null) {
			result = -1;
		}
		else {
			result = value1.compareTo(value2);
		}
		
		return result;
	}

}
