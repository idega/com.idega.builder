
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

import com.idega.jmodule.*;
import com.idega.data.*;
import com.idega.jmodule.news.presentation.*;
import com.idega.util.*;
import com.idega.builder.data.*;
import com.idega.core.data.*;
import com.idega.jmodule.object.*;
import com.idega.jmodule.object.textObject.*;
import com.idega.jmodule.object.interfaceobject.*;
import com.idega.builder.business.*;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.idegaweb.presentation.IWAdminWindow;
/**
*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
*@version 1.0 alpha
*/

public class IBAdminWindow extends Window{

  private static final String IW_BUNDLE_IDENTIFIER = "com.idega.builder";

  public void _main(ModuleInfo modinfo)throws Exception{
    super.main(modinfo);
    super._main(modinfo);
  }

  public void main(ModuleInfo modinfo)throws Exception{

  }

  /*public void add(ModuleObject obj){
    super.addBottom(obj);
  }

  public void add(String text){
    super.addBottom(text);
  }

  public String getBundleIdentifier(){
    return IW_BUNDLE_IDENTIFIER;
  }*/
}

