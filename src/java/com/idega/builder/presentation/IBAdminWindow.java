

//idega 2001 - Tryggvi Larusson

/*

*Copyright 2001 idega.is All Rights Reserved.

*/

package com.idega.builder.presentation;



//import java.util.*;

import java.lang.reflect.Method;

import java.beans.BeanInfo;

import java.beans.PropertyDescriptor;

import java.beans.Introspector;





import com.idega.util.*;

import com.idega.builder.data.*;

import com.idega.core.data.*;

import com.idega.presentation.*;

import com.idega.presentation.text.*;

import com.idega.presentation.ui.*;

import com.idega.builder.business.*;

import com.idega.idegaweb.IWBundle;

import com.idega.idegaweb.IWResourceBundle;

import com.idega.idegaweb.presentation.IWAdminWindow;

/**

*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>

*@version 1.0 alpha

*/



public class IBAdminWindow extends IWAdminWindow{



  private static final String IW_BUNDLE_IDENTIFIER = "com.idega.builder";



  public void main(IWContext iwc)throws Exception{



  }



  /*public void add(PresentationObject obj){

    super.addBottom(obj);

  }



  public void add(String text){

    super.addBottom(text);

  }



  public String getBundleIdentifier(){

    return IW_BUNDLE_IDENTIFIER;

  }*/

}



