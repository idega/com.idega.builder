/*
 * Created on 28.7.2003 by  tryggvil in project com.project
 */
package com.idega.builder.business;

import com.idega.core.builder.data.ICBuilderConstants;

/**
 * This is a temporary class and will be removed in future versions of idegaWeb.
 * <br><br>Developers should avoid to use constants in this class.
 * Copyright (C) idega software 2003
 * @author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */
public class BuilderConstants implements ICBuilderConstants
{
	public static final String STANDARD_IW_BUNDLE_IDENTIFIER="com.idega.builder";
	public static final String IC_OBJECT_INSTANCE_ID_PARAMETER = "ic_object_instance_id_par";
	public static final String IB_PAGE_PARAMETER = "ib_page";
	public static final String PRM_HISTORY_ID = "ib_history";
	public static final String SESSION_OBJECT_STATE = "obj_inst_state";
	/* (non-Javadoc)
	 * @see com.idega.core.builder.data.ICBuilderConstants#getHistoryIdParameter()
	 */
	public String getHistoryIdParameter() {
		return PRM_HISTORY_ID;
	}
	/* (non-Javadoc)
	 * @see com.idega.core.builder.data.ICBuilderConstants#getSessionObjectInstanceParameter()
	 */
	public String getSessionObjectInstanceParameter() {
		return SESSION_OBJECT_STATE;
	}
	/* (non-Javadoc)
	 * @see com.idega.core.builder.data.ICBuilderConstants#getPageParameter()
	 */
	public String getPageParameter() {
		return IB_PAGE_PARAMETER;
	}	
}
