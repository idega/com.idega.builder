package com.idega.builder.business;

import com.idega.presentation.IWContext;
import com.idega.idegaweb.IWMainApplication;
import java.util.Locale;
import java.util.Comparator;

import com.idega.core.component.data.ICObject;
import com.idega.exception.IWBundleDoesNotExist;
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

  private Locale locale;
  private IWMainApplication iwma;

  public ModuleComparator(IWContext iwc) {
     locale = iwc.getCurrentLocale();
     iwma = iwc.getIWMainApplication();
  }

  public int compare(Object o1, Object o2) {
    ICObject obj1 = (ICObject) o1;
    ICObject obj2 = (ICObject) o2;

    String one = obj1.getName();
		try {
			try{
				one = obj1.getBundle(iwma).getComponentName(obj1.getClassName(), locale);
			}
			catch(IWBundleDoesNotExist iwbne){
				System.err.println("com.idega.builder.business.ModuleComparator: "+iwbne.getLocalizedMessage()+". Please remove all references in the IC_OBJECT table");
			}
		}
		catch (NullPointerException e) {
			one = obj1.getName();
		}

		String two = obj2.getName();
		try {
			try{
				two = obj2.getBundle(iwma).getComponentName(obj2.getClassName(),locale);
			}
			catch(IWBundleDoesNotExist iwbne){
				System.err.println("com.idega.builder.business.ModuleComparator: "+iwbne.getLocalizedMessage()+". Please remove all references in the IC_OBJECT table");
			}
		}
		catch (NullPointerException e) {
			two = obj2.getName();
		}

		if ( one == null )
			one = obj1.getName();
    if ( two == null )
      two = obj2.getName();
      
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
