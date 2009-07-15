/*
 * Created on 28.7.2003 by  tryggvil in project com.project
 */
package com.idega.builder.business;

import com.idega.core.builder.business.ICBuilderConstants;

/**
 * Copyright (C) idega software 2003
 * @author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */
public class BuilderConstants {
	
	public static final String IW_BUNDLE_IDENTIFIER = "com.idega.builder";
	
	public static final String BASE_PAGE_PATH = "/files/cms/pages";
	
	public static final String STANDARD_IW_BUNDLE_IDENTIFIER = IW_BUNDLE_IDENTIFIER;
	public static final String IB_PAGE_PARAMETER = ICBuilderConstants.IB_PAGE_PARAMETER;		//"ib_page";
	public static final String IB_PAGE_PARAMETER_FOR_EDIT_MODULE_BLOCK = IB_PAGE_PARAMETER + "_for_edit_module_block";
	public static final String PRM_HISTORY_ID = ICBuilderConstants.PRM_HISTORY_ID;  			// "ib_history";
	public static final String SESSION_OBJECT_STATE = ICBuilderConstants.SESSION_OBJECT_STATE;	// "obj_inst_state";
	public static final String MODULE_NAME = "moduleName";
	public static final String METHOD_ID_PARAMETER = "iw_method_identifier";
	public static final String VALUE_PARAMETER = "ib_method_value";
	public static final String REGION_NAME = "builderRegionName";
	
	public static final String EMPTY = "";
	public static final String SLASH = "/";
	public static final String DOT = ".";
	
	public static final String ADD_NEW_MODULE_WINDOW_CACHE_KEY = "add_new_module_to_page_window";
	public static final String EDIT_MODULE_WINDOW_CACHE_KEY = "edit_existing_module_properties_window";
	public static final String SET_MODULE_PROPERTY_CACHE_KEY = "set_existing_module_property_box";
	
	public static final String HANLDER_VALUE_OBJECTS_STYLE_CLASS = ICBuilderConstants.HANLDER_VALUE_OBJECTS_STYLE_CLASS;
	
	public static final String IMAGE_WITH_TOOLTIPS_STYLE_CLASS = "imageWithMootoolsTooltips";
	
	public static final String ADD_MODULE_TO_REGION_LOCALIZATION_KEY = "ib_addmodule_window";
	public static final String ADD_MODULE_TO_REGION_LOCALIZATION_VALUE = "Add a new module";

	public static final String CURRENT_COMPONENT_IS_IN_FRAME = "currentComponentIsInFrame";
	public static final String BUILDER_MODULE_PROPERTY_HAS_BOOLEAN_TYPE_ATTRIBUTE = "builderModulePropertyHasBooleanTypeAttribute";
	
	public static final String TRANSFORM_PAGE_TO_BUILDER_PAGE_ATTRIBUTE = "transformPageToBuilderPage";
}
