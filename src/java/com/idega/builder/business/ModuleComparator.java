package com.idega.builder.business;

import java.util.Comparator;
import com.idega.core.data.ICObject;
import com.idega.util.IsCollator;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author
 * @version 1.0
 */

public class ModuleComparator implements Comparator {

  public int compare(Object o1, Object o2) {
    ICObject obj1 = (ICObject) o1;
    ICObject obj2 = (ICObject) o2;

    String one = obj1.getName();
    String two = obj2.getName();
    int result = IsCollator.getIsCollator().compare(one,two);

    return result;
  }

  public boolean equals(Object obj) {
    if (compare(this,obj) == 0)
      return(true);
    else
      return(false);
  }
}