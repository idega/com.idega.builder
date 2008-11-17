package com.idega.builder.bean;

import com.idega.business.SpringBeanName;

/**
 * @author <a href="mailto:laddi@idega.com">laddi</a>
 * @version $Revision: 1.1 $
 */
@SpringBeanName("adminToolbarSession")
public interface AdminToolbarSession {

	public String getMode();

	public void setMode(String mode);

}