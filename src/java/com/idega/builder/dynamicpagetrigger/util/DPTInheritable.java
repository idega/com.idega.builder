/*
 * Created on 17.5.2004
 */
package com.idega.builder.dynamicpagetrigger.util;

import com.idega.builder.dynamicpagetrigger.business.DPTCopySession;


/**
 * Title: DPTCopyHandler
 * Description:
 * Copyright: Copyright (c) 2004
 * Company: idega Software
 * @author 2004 - idega team - <br><a href="mailto:gummi@idega.is">Gudmundur Agust Saemundsson</a><br>
 * @version 1.0
 */
public interface DPTInheritable {
	  public boolean copyICObjectInstance(String pageKey, int newInstanceID, DPTCopySession copySession);
}
