package com.idega.builder.presentation;

import com.idega.block.media.presentation.FileChooser;


/**
 * Title:        IBFileChooser
 * Description: see com.idega.block.media.presentation.FileChooser
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href="eiki@idega.is">Eirikur Hrafnsson</a>
 * @version 1.0
 */

public class IBFileChooser extends FileChooser {
  public IBFileChooser(String chooserName) {
    super(chooserName);
  }

  public IBFileChooser(String chooserName,String style){
    super(chooserName, style);
  }
}