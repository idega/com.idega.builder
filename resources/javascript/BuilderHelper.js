var ADD_NEW_COMPONENT_WINDOW_LINK = '/workspace/window/';
var EDIT_COMPONENT_WINDOW_LINK = '/workspace/window/';

var ADD_NEW_COMPONENT_IMAGE = '/idegaweb/bundles/com.idega.builder.bundle/resources/add.png';
var COMPONENT_INFORMATION_IMAGE = '/idegaweb/bundles/com.idega.builder.bundle/resources/information.png';
var REMOVE_BUILDER_PROPERTY_IMAGE = '/idegaweb/bundles/com.idega.builder.bundle/resources/remove.png';

var ADD_NEW_COMPONENT_LABEL = 'Add a new Module';
var COMPONENT_INFORMATION_LABEL = 'Set module properties';
var NO_IDS_ERROR_MESSAGE = 'Error occurred while inserting selected module!';
var ADDING_MODULE_LABEL = 'Adding...';
var REGION_LABEL = 'Region';
var DELETING_LABEL = 'Deleting...';
var ARE_YOU_SURE_MESSAGE = 'Are you sure?';
var SAVING_LABEL = 'Saving...';
var LOADING_LABEL = 'Loading...';
var RELOADING_LABEL = 'Reloading...';
var MOVING_LABEL = 'Moving...';
var DROP_MODULE_HERE_LABEL = 'Drop module into';
var COPYING_LABEL = 'Copying...';
var SMALL_REGION_LABEL = 'region';
var REMOVE_LABEL = 'Remove';

var PROPERTY_NAME = null;
var INSTANCE_ID = null;
var PARENT_ID = null;
var EXTRA_PARENT_ID = null;
var PAGE_KEY = null;
var REGION_ID = null;
var MODULE_CONTENT_ID = null;
var COPIED_MODULE_ID = null;
var CUT_MODULE_ID = null;

var VISIBLE_PASTE_ICON = false;

var ACTIVE_PROPERTY_SETTER_BOX = null;

var IC_OBJECT_INSTANCE_ID_PARAMETER = 'ic_object_instance_id_par';
var MODULE_NAME_PARAMETER = 'moduleName';
var IB_PAGE_PARAMETER = 'ib_page';
var HANLDER_VALUE_OBJECTS_STYLE_CLASS = 'handlerValueObjects';

var PROPERTIES_SHOWN = new Array();
var PROPERTY_BOX_SHOWN = new Array();
var OBJECTS_TO_RERENDER = new Array();
var ELEMENTS_WITH_TOOLTIP = new Array();
var PASTE_ICONS_SLIDERS = new Array();
var EXTRACTED_VALUES = new Array();
var SPECIAL_OBJECTS = ['com.idega.block.article.component.ArticleItemViewer', 'com.idega.user.presentation.group.GroupInfoViewer',
	 'com.idega.user.presentation.group.GroupUsersViewer', 'com.idega.block.media.presentation.VideoViewer'];
	 
var MOODALBOX_WINDOW_WIDTH = 650;
var MOODALBOX_WINDOW_HEIGHT = 500;

function getBuilderInitInfo() {
	BuilderEngine.getBuilderInitInfo(getBuilderInitInfoCallback);
}

function getBuilderInitInfoCallback(list) {
	if (list == null) {
		return false;
	}
	if (list.length != 25) {
		return false;
	}
	
	ADD_NEW_COMPONENT_WINDOW_LINK = list[0];
	ADD_NEW_COMPONENT_LABEL = list[1];
	COMPONENT_INFORMATION_LABEL = list[2];
	ADD_NEW_COMPONENT_IMAGE = list[3];
	COMPONENT_INFORMATION_IMAGE = list[4];
	NO_IDS_ERROR_MESSAGE = list[5];
	PAGE_KEY = list[6];
	ADDING_MODULE_LABEL = list[7];
	REGION_LABEL = list[8];
	EDIT_COMPONENT_WINDOW_LINK = list[9];
	IC_OBJECT_INSTANCE_ID_PARAMETER = list[10];
	MODULE_NAME_PARAMETER = list[11];
	DELETING_LABEL = list[12];
	ARE_YOU_SURE_MESSAGE = list[13];
	SAVING_LABEL = list[14];
	LOADING_LABEL = list[15];
	IB_PAGE_PARAMETER = list[16];
	HANLDER_VALUE_OBJECTS_STYLE_CLASS = list[17];
	RELOADING_LABEL = list[18];
	MOVING_LABEL = list[19];
	DROP_MODULE_HERE_LABEL = list[20];
	COPYING_LABEL = list[21];
	SMALL_REGION_LABEL = list[22];
	REMOVE_BUILDER_PROPERTY_IMAGE = list[23];
	REMOVE_LABEL = list[24];
	
}

function existsSliderForElement(id) {
	try {
		for (var i = 0; i < PASTE_ICONS_SLIDERS.length; i++) {
			if (id == PASTE_ICONS_SLIDERS[i].element.id) {
				return true;
			}
		}
	} catch(e) {
		return false;
	}
	return false;
}

function hidePasteIcons(useSlideOut) {
	PASTE_ICONS_SLIDERS = new Array();
	hideNewestPasteIcons(useSlideOut);
}

function hideNewestPasteIcons(useSlideOut) {
	VISIBLE_PASTE_ICON = false;
	COPIED_MODULE_ID = null;
	CUT_MODULE_ID = null;
	$$('div.pasteModuleIconContainer').each(
		function(element) {
			if (!existsSliderForElement(element.id)) {
				try {
					var slider = new Fx.Slide(element.id, {mode: 'horizontal'});
					if (useSlideOut) {
						slider.slideOut();
					}
					else {
						slider.hide();
					}
				} catch(e){}
				PASTE_ICONS_SLIDERS.push(slider);
			}
		}
	);
}

function registerBuilderActions() {
	var errorHanlder = function() {
		reloadPage();
	}
	DWREngine.setErrorHandler(errorHanlder);
	
	addEventsToBuilderElements();
	
	hidePasteIcons(false);
}

function intializeMoodalboxInBuilder() {
	MOOdalBox.init({resizeDuration: 50, evalScripts: true, defContentsWidth: MOODALBOX_WINDOW_WIDTH, defContentsHeight: MOODALBOX_WINDOW_HEIGHT});
}

function addEventsToBuilderElements() {
	$$('div.moduleContainer').each(
		function(element) {
			element.addEvent('mouseenter', function() {
				try {
					showModuleContainerTop(element);
					showAllComponentsLabels(element);
					showComponentInfoImage(element);
				} catch(err) {}
			});
			element.addEvent('mouseleave', function() {
				try {
					hideModuleContainerTop(element);
					hideOldLabels(element);
					hideComponentInfoImage(element);
					element.removeAttribute('style');
				} catch(err) {}
			});
    	}
    );
    
    $$('div.regionInfoImageContainer').each(
    	function(element) {
			element.addEvent('click', function() {
				setRegionAndModuleContentId(element);
			});
		}
	);
	
	$$('input.modulePropertySetter').each(
		function(element) {
			element.removeEvents('blur');
			element.removeEvents('keyup');
			element.removeEvents('click');
			
			element.addEvents({
				'blur': function(e) {
					if (element.type == 'text' || element.type == 'password') {
						e = new Event(e);
						saveModuleProperty(e, element);
						e.stop();
					}
				},
				'keyup': function(e) {
					element.setProperty('valuechanged', true);
					e = new Event(e);
					saveModuleProperty(e, element);
					e.stop();
				},
				'click': function(e) {
					if (element.type == 'radio' || element.type == 'checkbox') {
						element.setProperty('valuechanged', true);
					}
					e = new Event(e);
					saveModuleProperty(e, element);
					e.stop();
				}
			});
		}
	);
	$$('select.modulePropertySetter').each(
		function(element) {
			element.removeEvents('change');
			
			element.addEvent('change', function(e) {
				element.setProperty('valuechanged', true);
				e = new Event(e);
				saveModuleProperty(e, element);
				e.stop();
			});
		}
	);
	
	$$('img.imageWithMootoolsTooltips').each(
		function(element) {
			if (!existsElementInArray(ELEMENTS_WITH_TOOLTIP, element)) {
				ELEMENTS_WITH_TOOLTIP.push(element);
				initToolTipForElement(element);
			}
		}
	);
	
	$$('img.add_article_module_to_region_image').each(
		function(element) {
			addActionsForArticleButton(element);
		}
	);
}

function addActionsForArticleButton(element) {
	if (!existsElementInArray(ELEMENTS_WITH_TOOLTIP, element)) {
		ELEMENTS_WITH_TOOLTIP.push(element);
		initToolTipForElement(element);
	}
	
	element.removeEvents('click');	//	Do not want to duplicate events
	
	element.addEvent('click', function() {
		addConcreteModule(element);
	});
}

function addConcreteModule(element) {
	if (element == null) {
		return false;
	}
	
	var regionContainerId = getMarkupAttributeValue(element, 'regioncontainerid');
	setPropertiesForAddModule(regionContainerId);
	
	var objectId = getMarkupAttributeValue(element, 'icobjectid');
	var objectClass = getMarkupAttributeValue(element, 'icobjectclass');

	if (objectId == null || objectClass == null) {
		return false;
	}

	addSelectedModule(objectId, objectClass);
}

function closeAddComponentContainer(id) {
	var container = $(id);
	if (container == null) {
		return false;
	}
	container.style.visibility = 'hidden';
}

function existsValueInList(list, value) {
	if (list == null || value == null) {
		return false;
	}
	for (var i = 0; i < list.length; i++) {
		if (value == list[i]) {
			return true;
		}
	}
	return false;
}

function showAddComponentImage(parentElement, element, regionLabel) {
	if (element == null) {
		return false;
	}
	
	element.style.visibility = 'visible';	
}

function setPropertiesForAddModule(id) {
	PARENT_ID = null;
	INSTANCE_ID = null;
	EXTRA_PARENT_ID = null;
	
	if (id == null) {
		return false;
	}
	var element = $(id);
	if (element == null) {	
		return false;
	}
	
	PARENT_ID = getInputValue(element.getElementsByTagName('input'), 'parentKey');
	
	var region = element.getParent();
	
	var modules = getElementsByClassName(region, 'div', 'moduleContainer');
	if (modules.length > 0) {
		var lastModule = modules[modules.length - 1];
		INSTANCE_ID = getMarkupAttributeValue(lastModule, 'instanceid');

		if (PARENT_ID == null || PARENT_ID == '') {
			PARENT_ID = getMarkupAttributeValue(lastModule, 'parentid');
		}
	}
	
	if (PARENT_ID == null || PARENT_ID == '') {
		PARENT_ID = getMarkupAttributeValue(element, 'instanceid');
		if (PARENT_ID != null && PARENT_ID != '') {
			var modulesContainer = null;
			var parentElement = element.getParent();
			while (modulesContainer == null && parentElement != null) {
				var modulesContainers = getElementsByClassName(parentElement, 'div', 'moduleContent');
				if (modulesContainers.length > 0) {
					modulesContainer = modulesContainers[0];
				}
				parentElement = parentElement.getParent();
			}
			if (modulesContainer != null) {
				if (modulesContainer.id) {
					EXTRA_PARENT_ID = modulesContainer.id;
				}
			}
		}
	}
	
	if (PARENT_ID == null || PARENT_ID == '') {
		if (region.id == null || region.id == '') {
			PARENT_ID = 'region_' + new Date().getTime();
		}
		else {
			PARENT_ID = region.id;
		}
	}
}

function getInputValue(inputs, inputName) {
	if (inputs == null || inputName == null) {
		return false;			
	}

	for (var i = 0; i < inputs.length; i++) {
		input = inputs[i];
		if (input.name != null) {
			if (input.name.indexOf(inputName) != -1) {
				return input.value;
			}
		}
	}
	return null;
}

function getFirstElementFromList(elements) {
	if (elements == null) {
		return null;
	}
	if (elements.length == 0) {
		return null;
	}
	return $(elements[0]);
}

function hideComponentInfoImage(element) {
	if (element == null) {
		return false;
	}
	
	var list = getElementsByClassName(element, '*', 'regionInfoImageContainer');
	var container = getFirstElementFromList(list);
	if (container == null) {
		return false;
	}
	
	container.style.visibility = 'hidden';
}

function showModuleContainerTop(element) {
	var list = getElementsByClassName(element, '*', 'moduleContainerTop');
	var container = getFirstElementFromList(list);
	if (container == null) {
		return false;
	}
	
	container.style.visibility = 'visible';
}

function hideModuleContainerTop(element) {
	var list = getElementsByClassName(element, '*', 'moduleContainerTop');
	var container = getFirstElementFromList(list);
	if (container == null) {
		return false;
	}
	
	container.style.visibility = 'hidden';
}

function showComponentInfoImage(element) {
	if (element == null) {
		return false;
	}
	
	var list = getElementsByClassName(element, '*', 'regionInfoImageContainer');
	var container = getFirstElementFromList(list);
	if (container == null) {
		return false;
	}
	
	var link = getFirstElementFromList(container.getElementsByTagName('a'));
	if (link == null) {
		return false;
	}
	
	if (container.style.visibility == '') {	// If it is the first time	
		var moduleName = 'Undefined';
		var moduleNameSpans = getElementsByClassName(element, 'span', 'moduleNameTooltip');
		if (moduleNameSpans.length > 0) {
			var moduleNameSpan = moduleNameSpans[0];
			if (moduleNameSpan != null) {
				var spanChildren = moduleNameSpan.childNodes;
				if (spanChildren != null) {
					if (spanChildren.length > 0) {
						moduleName = spanChildren[0].nodeValue;
					}
				}
			}
		}
		
		var instanceId = null;
		var inputs = element.getElementsByTagName('input');
		if (inputs != null) {
			var input = null;
			var foundInstance = false;
			for (var i = 0; (i < inputs.length && !foundInstance); i++) {
				input = inputs[i];
				if (input.id != null) {
					if (input.id.indexOf('instanceId') != -1) {
						instanceId = input.value;
						foundInstance = true;
					}
				}
			}
		}

		link.removeAttribute('href');
		
		var uri = EDIT_COMPONENT_WINDOW_LINK + '&' + MODULE_NAME_PARAMETER + '=' + moduleName + '&' + IC_OBJECT_INSTANCE_ID_PARAMETER +
		 '=' + instanceId + '&' + IB_PAGE_PARAMETER + '=' + PAGE_KEY;
		link.setProperty('href', uri);
		
		//	Link will be registered to MOOdalBox (if needed)
		MOOdalBox.register(link);
	}
	
	container.style.visibility = 'visible';
}

function removeOldContainer(element, id) {
	var oldContainer = $(id);
	if (oldContainer != null) {
		var parentElement = oldContainer.parentNode;
		if (parentElement != null) {
			if (parentElement == element) {
				return false;
			}
			parentElement.removeChild(oldContainer);
			return true;
		}
	}
}

function addSelectedModule(newObjectId, className) {
	if (PARENT_ID == null || PAGE_KEY == null) {
		alert(NO_IDS_ERROR_MESSAGE);
		return false;
	}
	
	if (INSTANCE_ID == null) {
		INSTANCE_ID = PARENT_ID;
	}
	
	var index = 0;
	var container = $(PARENT_ID);
	if (container != null) {
		var modules = getNeededElementsFromList(container.childNodes, 'moduleContainer');
		if (modules != null) {
			index = modules.length;
		}
	}
	
	showLoadingMessage(ADDING_MODULE_LABEL);
	
	if (existsValueInList(SPECIAL_OBJECTS, className)) {
		BuilderEngine.addModule(PAGE_KEY, PARENT_ID, INSTANCE_ID, newObjectId, false, {
			callback: function(uuid) {
				addConcreteModuleCallback(uuid, index, PARENT_ID);
			}
		});
		return true;
	}

	BuilderEngine.addSelectedModule(PAGE_KEY, INSTANCE_ID, newObjectId, PARENT_ID, className, index, true, {
		callback: function(component) {
			addSelectedModuleCallback(component, PARENT_ID);
		}
	});
}

function addConcreteModuleCallback(uuid, index, id) {
	if (uuid == null) {
		closeAllLoadingMessages();
		return false;
	}
	
	BuilderEngine.getRenderedModule(PAGE_KEY, uuid, index, id, {
		callback: function(componentContainer) {
			addSelectedModuleCallback(componentContainer, id);
		}
	});
}

function addSelectedModuleCallback(component, id) {
	closeAllLoadingMessages();
	
	if (id == null) {
		executeActionsBeforeReloading();
		return false;
	}
	
	if (component == null) {
		executeActionsBeforeReloading();
		return false;
	}
	
	if (id.indexOf('.') != -1) {
		var idParts = id.split('.');
		id = 'id';
		for (var i = 0; i < idParts.length; i++) {
			id += idParts[i];
			if ((i + 1) < idParts.length) {
				id += '_';
			}
		}
	}
	
	var container = $(id);
	if (container == null) {
		if (EXTRA_PARENT_ID != null && EXTRA_PARENT_ID != '') {
			container = $(EXTRA_PARENT_ID);
			EXTRA_PARENT_ID = null;
		}
		if (container == null) {
			executeActionsBeforeReloading();
			return false;
		}
	}
	
	var children = component.childNodes;
	if (children == null) {
		executeActionsBeforeReloading();
		return false;
	}
	if (children.length == 0) {
		executeActionsBeforeReloading();
		return false;
	}

	// Making copy
	var allNodes = getTransformedDocumentToDom(component);
	
	// Finding place where to put new module
	var elementToInsertBefore = null;
	var modules = getNeededElementsFromList(container.childNodes, 'moduleContainer');
	if (modules == null) {
		executeActionsBeforeReloading();
		return false;
	}

	var lastModule = null;
	if (modules.length == 0) {
		elementToInsertBefore = container.firstChild;
	}
	else {
		lastModule = modules[modules.length - 1];
		var elementAfterLastModule = lastModule.nextSibling;	// The proper place - after the last module container
		if (elementAfterLastModule == null) {
			elementToInsertBefore = container.lastChild;
		}
		else {
			elementToInsertBefore = elementAfterLastModule;
		}
	}
	
	//	Removing old last drop area
	markModuleContainerAsNotLast(lastModule, getDropAreaFromElement(lastModule));
	
	// Inserting nodes
	var activeNode = null;
	var realNode = null;
	var elementToHighlight = null;
	for (var i = 0; i < allNodes.length; i++) {
		activeNode = allNodes[allNodes.length - (i + 1)];
		realNode = createRealNode(activeNode);
		if (realNode.className) {
			if (realNode.className == 'moduleContainer') {
				elementToHighlight = realNode;
			}
		}
		container.insertBefore(realNode, elementToInsertBefore);
		elementToInsertBefore = realNode;
	}
	
	addDropAreaToTheLastModuleContainer($(container));
	
	// Need to re-register Builder actions
	registerBuilderActions();
	
	//	Registering actions for Drag&Drop
	modules = getNeededElementsFromList(container.childNodes, 'moduleContainer');
	var newModule = $(modules[modules.length - 1]);
	var moduleNames = getElementsByClassName(newModule, 'div', 'moduleName');
	for (var i = 0; i < moduleNames.length; i++) {
		registerDragAndDropActionsForModuleNameElement($(moduleNames[i]));
	}
	var moduleDropAreas = getElementsByClassName(newModule, 'div', 'moduleDropArea');
	for (var i = 0; i < moduleDropAreas.length; i++) {
		registerForDropSingleElement($(moduleDropAreas[i]));
	}
	var moduleTitles = getElementsByClassName(newModule, 'span', 'moduleNameTooltip');
	for (var i = 0; i < moduleTitles.length; i++) {
		initToolTipForElement($(moduleTitles[i]));
	}
	
	//	Registering links with MOOdalBox
	var linksForNewModuleWindow = getElementsByClassName(newModule, 'a', 'addModuleLinkStyleClass');
	for (var i = 0; i < linksForNewModuleWindow.length; i++) {
		MOOdalBox.register(linksForNewModuleWindow[i]);
	}
	
	MOOdalBox.close();
	if (elementToHighlight != null) {
		highlightElement(elementToHighlight, 4000, '#ffffff');
	}
}

function manageComponentPropertiesList(id) {	
	if (existsValueInList(PROPERTIES_SHOWN, id)) {
		removeElementFromArray(PROPERTIES_SHOWN, id);
		
		closeComponentPropertiesList(id);
	}
	else {
		PROPERTIES_SHOWN.push(id);
		
		var el = $(id);
		el.style.display = 'block';	
	}
}

function addPropertyIdAndClose(id) {
	if (id == null) {
		return false;
	}
	PROPERTIES_SHOWN.push(id);
	closeComponentPropertiesList(id);
}

function closeComponentPropertiesList(id) {
	if (id == null) {
		return false;
	}
	
	var el = $(id);
	el.style.display = 'none';
}

function exitFromPropertiesWindow() {
	PROPERTIES_SHOWN = new Array();
	editWindow.deactivate();
}

function closeAddModuleWindow() {
	valid.deactivate();
}

function deleteModule(id, instanceId, imageId) {
	var deleteConfirmed = window.confirm(ARE_YOU_SURE_MESSAGE);
	if (deleteConfirmed) {
		var moduleToDelete = $(id);
		if (moduleToDelete == null) {
			return false;
		}
		var pageKey = getMarkupAttributeValue(moduleToDelete, 'pageid');
		var parentId = getMarkupAttributeValue(moduleToDelete, 'parentid');
		
		showLoadingMessage(DELETING_LABEL);
		BuilderEngine.deleteSelectedModule(pageKey, parentId, instanceId, {
  			callback: function(result) {
    			deleteModuleCallback(result, id, instanceId, imageId);
  			}
		});
	}
}

function deleteModuleCallback(result, id, instanceId, imageId) {
	closeAllLoadingMessages();
	if (result) {
		var deleted = $(id);
		if (deleted == null) {
			return false;
		}

		var parentDeleted = $(deleted.parentNode);
		if (parentDeleted == null) {
			return false;
		}
		deleted.remove();
		
		addDropAreaToTheLastModuleContainer(parentDeleted);
		
		if (COPIED_MODULE_ID == instanceId || CUT_MODULE_ID == instanceId) {
			hidePasteIcons(true);
		}
	}
}

function closeOldPropertyBoxes(currentID) {
	if (currentID == null || PROPERTY_BOX_SHOWN == null) {
		return false;
	}
	var box = null;
	var id = null;
	var idsToRemove = new Array();
	for (var i = 0; i < PROPERTY_BOX_SHOWN.length; i++) {
		id = PROPERTY_BOX_SHOWN[i];
		if (id != currentID) {
			box = $(id);
			if (box != null) {
				box.style.display = 'none';
				idsToRemove.push(id);
			}
		}
	}
	for (var i = 0; i < idsToRemove.length; i++) {
		removeElementFromArray(PROPERTY_BOX_SHOWN, idsToRemove[i]);
	}
}

function getActivePropertyBoxId() {
	return ACTIVE_PROPERTY_SETTER_BOX;
}

function getPropertyBox(id, propertyName, objectInstanceId) {
	EXTRACTED_VALUES = new Array();
	ACTIVE_PROPERTY_SETTER_BOX = id;
	PROPERTY_NAME = propertyName;
	INSTANCE_ID = objectInstanceId;
	getBuilderModulePropertyBox(id, propertyName, objectInstanceId, false);
}

function getBuilderModulePropertyBox(id, propertyName, objectInstanceId, reloadBox) {
	var fullId = id + '_property_setter_box';
	closeOldPropertyBoxes(fullId);
	var propertySetterBox = $(fullId);
	
	var openPropertyBox = true;
	if (reloadBox) {
		openPropertyBox = false;
		if (propertySetterBox != null) {
			openPropertyBox = !(propertySetterBox.style.display == 'none');
			propertySetterBox.remove();
			propertySetterBox = null;
		}
	}
	
	if (propertySetterBox == null) {
		showLoadingMessage(LOADING_LABEL);
		BuilderEngine.getPropertyBox(PAGE_KEY, propertyName, objectInstanceId, {
			callback: function(box) {
				getPropertyBoxCallback(id, propertyName, objectInstanceId, box, openPropertyBox);
			}
		});
	}
	else {
		// Box allready exists
		if (propertySetterBox.style.display == 'none') {
			propertySetterBox.style.display = 'block';
			PROPERTY_BOX_SHOWN.push(fullId);
		}
		else {
			propertySetterBox.style.display = 'none';
			removeElementFromArray(PROPERTY_BOX_SHOWN, fullId);
		}
	}
}

function getPropertyBoxCallback(id, propertyName, objectInstanceId, box, openPropertyBox) {
	closeAllLoadingMessages();
	if (id == null) {
		return false;
	}
	var container = $(id);
	if (container == null) {
		return false;
	}
	if (box == null) {
		return false;
	}
	
	var propertySetterBox = new Element('div');
	var fullId = id + '_property_setter_box';
	propertySetterBox.setProperty('id', fullId);
	PROPERTY_BOX_SHOWN.push(fullId);
	
	insertNodesToContainer(box, propertySetterBox);
	if (!openPropertyBox) {
		propertySetterBox.style.display = 'none';
	}
	container.appendChild(propertySetterBox);
	
	addEventsToBuilderElements();	// Need to re-register actions
}

function getMarkupAttributeValue(element, attrName) {
	if (element == null || attrName == null) {
		return null;
	}
	return element.getProperty(attrName);
}

function saveModuleProperties(openedWindow) {
	if (ACTIVE_PROPERTY_SETTER_BOX == null) {
		return false;
	}
	var setters = getElementsByClassName($(ACTIVE_PROPERTY_SETTER_BOX), '*', 'modulePropertySetter');
	if (setters == null || setters.length == 0) {
		return false;
	}
	
	var element = setters[0];
	var moduleId = getMarkupAttributeValue(element, 'moduleid');
	var propertyName = getMarkupAttributeValue(element, 'propname');
	var needsReload = getMarkupAttributeValue(element, 'needsreload');
	var parametersCount = getMarkupAttributeValue(element, 'parameterscount');
	var values = getMultivaluesForModule(setters);
	
	saveValuesForModule(values, moduleId, propertyName, needsReload, openedWindow, parametersCount);
}

function saveModuleProperty(event, element) {
	if (element == null) {
		return false;
	}
	
	if (event != null) {
		if (event.type == 'keyup' && (element.type == 'text' || element.type == 'password')) {
			//	Checking if 'Enter' was pressed
			if (!('enter' == event.key)) {
				return false;
			}
		}
	}
	
	var isValueChanged = element.getProperty('valuechanged');
	if (isValueChanged == null) {
		return false;
	}
	isValueChanged += '';
	if (isValueChanged == 'true') {
		element.setProperty('valuechanged', false);
	}
	else {
		return false;
	}
	
	var moduleId = getMarkupAttributeValue(element, 'moduleid');
	var propertyName = getMarkupAttributeValue(element, 'propname');
	var needsReload = getMarkupAttributeValue(element, 'needsreload');
	var multivalue = getMarkupAttributeValue(element, 'multivalue');
	var parametersCount = getMarkupAttributeValue(element, 'parameterscount');
	var isMultivalue = 'false';
	if (multivalue != null) {
		isMultivalue = multivalue;
	}
	
	var values = null;
	if (isMultivalue == 'true') {
		var elements = getElementsByClassName($(ACTIVE_PROPERTY_SETTER_BOX), '*', 'modulePropertySetter');	// Values setters
		values = getMultivaluesForModule(elements);
	}
	else {
		values = new Array();
		values.push(DWRUtil.getValue(element));
	}
	
	saveValuesForModule(values, moduleId, propertyName, needsReload, null, parametersCount);
}

function saveValuesForModule(values, moduleId, propertyName, needsReload, openedWindow, parametersCount) {
	EXTRACTED_VALUES = new Array();
	if (values == null) {
		return false;
	}
	if (values.length != parametersCount) {
		return false;	//	Too less or to much values
	}
	
	showLoadingMessage(SAVING_LABEL);
	BuilderEngine.setModuleProperty(PAGE_KEY, moduleId, propertyName, values, {
		callback: function(result) {
			if (openedWindow) {
				openedWindow.close();
			}
			
			closeAllLoadingMessages();
			saveModulePropertyCallback(result, moduleId, needsReload, propertyName);
		}
	});
}

function getMultivaluesForModule(elements) {
	if (elements == null || elements.length == 0) {
		return false;
	}
	
	var values = new Array();
	var value = null;
	for (var i = 0; i < elements.length; i++) {
		value = getModulePropertyFromElement(elements[i]);
		if (value != null) {
			values.push(value);
		}
	}
	return values;
}

function getModulePropertyFromElement(element) {
	var value = null;
	
	//	Input
	if (element.tagName == 'input' || element.tagName == 'INPUT') {
		if (element.type) {
			if (element.type == 'radio') {
				var elements = getElementsByClassName($(ACTIVE_PROPERTY_SETTER_BOX), '*', 'modulePropertySetter');
				
				var groupedElements = new Array();
				for (var i = 0; i < elements.length; i++) {
					var sameNameElements = getAllElementsByName(elements, 'ib_property_' + i);
					if (sameNameElements.length > 0) {
						groupedElements.push(sameNameElements);
					}
				}
				
				for (var i = 0; i < groupedElements.length; i++) {
					var sameNameElements = groupedElements[i];
					if (sameNameElements.length == 2) {
						var positive = sameNameElements[0];
						var negative = sameNameElements[1];
						if (positive.checked) {
							value = positive.value;
						}
						else if (negative.checked) {
							value = negative.value;
						}
						else {
							value = 'N';	//	Nothing checked
						}
						EXTRACTED_VALUES.push(positive.name);
					}
				}
			}
			else {
				value = DWRUtil.getValue(element);
			}
		}
	}
	
	//	Select
	if (element.tagName == 'select' || element.tagName == 'SELECT') {
		value = DWRUtil.getValue(element);
	}
	
	if (value == '') {
		value = null;
	}
	return value;
}

function getSameNameElements(elements) {
	var groupedElements = new Array();
	for (var i = 0; i < elements.length; i++) {
		var sameNameElements = getAllElementsByName(elements, 'ib_property_' + i);
		if (sameNameElements.length > 0) {
			for (var j = 0; j < sameNameElements.length; j++) {
				groupedElements.push(sameNameElements[j]);
			}
		}
	}
	return groupedElements;
}

function getAllElementsByName(list, name) {
	var sameNameElements = new Array();
	if (list == null || name == null) {
		return sameNameElements;
	}
	for (var i = 0; i < list.length; i++) {
		if (list[i].name) {
			if (list[i].name == name && !(existsElementInArray(EXTRACTED_VALUES, name))) {
				sameNameElements.push(list[i]);
			}
		}
	}
	return sameNameElements;
}

function executeActionsBeforeReloading() {
	closeAllLoadingMessages();
	showLoadingMessage(RELOADING_LABEL);
		
	reloadPage();
}

function saveModulePropertyCallback(result, moduleId, needsReload, propertyName) {
	if (!result) {
		closeAllLoadingMessages();
		return false;
	}
	
	if (ACTIVE_PROPERTY_SETTER_BOX != null) {
		var setterBox = $(ACTIVE_PROPERTY_SETTER_BOX);
		if (setterBox != null) {
			setterBox.removeClass('moduleProperty');
			setterBox.addClass('modulePropertyIsSet');
		}
		
		getRemoveBuilderPropertyImage(moduleId, propertyName);
	}
	
	if (needsReload == 'true') {
		closeAllLoadingMessages();
		var actionOnClose = function() {
			executeActionsBeforeReloading();
		};
		addActionForMoodalBoxOnCloseEvent(actionOnClose);
		return false;
	}
	
	closeAllLoadingMessages();
	
	// Re-rendering module
	renderModuleAgain(PAGE_KEY, REGION_ID, moduleId, MODULE_CONTENT_ID);
}

function isComponentMarkedForReRendering(moduleId) {
	if (moduleId == null) {
		return false;
	}
	var object = null;
	for (var i = 0; i < OBJECTS_TO_RERENDER.length; i++) {
		object = OBJECTS_TO_RERENDER[i];
		if (object.moduleId == moduleId) {
			return true;
		}
	}
	return false;
}

function renderModuleAgain(pageKey, regionId, moduleId, moduleContentId) {
	showLoadingMessage(LOADING_LABEL);
	BuilderEngine.reRenderObject(pageKey, moduleId, {
		callback: function(component) {
			reRenderObjectCallback(component, moduleContentId);
		}
	});
}

function renderModulesAgain() {
	var object = null;
	for (var i = 0; i < OBJECTS_TO_RERENDER.length; i++) {
		object = OBJECTS_TO_RERENDER[i];
		BuilderEngine.reRenderObject(object.pageKey, object.moduleId, {
			callback: function(component) {
				reRenderObjectCallback(component, object.moduleContentId);
			}
		});
	}
	OBJECTS_TO_RERENDER = new Array();
}

function reRenderObjectCallback(component, moduleContentId) {	
	var container = $(moduleContentId);
	if (container == null || component == null) {
		closeAllLoadingMessages();
		var actionOnClose = function() {
			executeActionsBeforeReloading();
		};
		addActionForMoodalBoxOnCloseEvent(actionOnClose);
		return false;
	}
	closeAllLoadingMessages();
	
	removeChildren(container);
	
	showLoadingMessage(LOADING_LABEL);
	
	try {
		insertNodesToContainer(component, container);
	} catch(e) {
		closeAllLoadingMessages();
		var actionOnClose = function() {
			executeActionsBeforeReloading();
		};
		addActionForMoodalBoxOnCloseEvent(actionOnClose);
		return false;
	}
	
	closeAllLoadingMessages();
	
	registerBuilderActions();
	//	Registering 'addModule' links with MOOdalBox
	var linksForNewModuleWindow = getElementsByClassName(container, 'a', 'addModuleLinkStyleClass');
	for (var i = 0; i < linksForNewModuleWindow.length; i++) {
		MOOdalBox.register(linksForNewModuleWindow[i]);
	}
}

function setRegionAndModuleContentId(element) {
	if (element == null) {
		REGION_ID = null;
		MODULE_CONTENT_ID = null;
		return false;
	}
	var inputs = element.getElementsByTagName('input');
	if (inputs == null) {
		REGION_ID = null;
		MODULE_CONTENT_ID = null;
		return false;
	}
	
	REGION_ID = getInputValue(inputs, 'regionId');
	MODULE_CONTENT_ID = getInputValue(inputs, 'moduleContentId');
}

function ReRenderObject(pageKey, regionId, moduleId, moduleContentId) {
	this.pageKey = pageKey;
	this.regionId = regionId;
	this.moduleId = moduleId;
	this.moduleContentId = moduleContentId;
}

function copyThisModule(containerId, instanceId) {
	COPIED_MODULE_ID = instanceId;
	CUT_MODULE_ID = null;
	
	var elementToCopy = $(containerId);
	if (elementToCopy == null) {
		return false;
	}
	var pageKey = getMarkupAttributeValue(elementToCopy, 'pageid');
	
	copyModule(containerId, pageKey, null, instanceId, null);
}

function copyModule(containerId, pageKey, parentId, instanceId, id) {
	showLoadingMessage(COPYING_LABEL);
	BuilderEngine.copyModule(pageKey, parentId, instanceId, {
		callback: function(result) {
			copyModuleCallback(result, containerId, id);
		}
	});
}

function copyModuleCallback(result, containerId, id) {
	closeAllLoadingMessages();
	if (result) {
		if (CUT_MODULE_ID != null) {
			var element = $(containerId);
			
			var parentContainer = $(element.parentNode);
			
			element.remove();
			
			addDropAreaToTheLastModuleContainer(parentContainer);
		}
	
		if (!VISIBLE_PASTE_ICON) {
			slideInModulePasteIcons();
		}
	}
}

function slideInModulePasteIcons() {
	var slider = null;
	for (var i = 0; i < PASTE_ICONS_SLIDERS.length; i++) {
		try {
			slider = PASTE_ICONS_SLIDERS[i];
			slider.slideIn();
		} catch(e) {}
	}
	VISIBLE_PASTE_ICON = true;
}

function pasteCopiedModule(id) {
	var pasteIconContainer = $(id);
	if (pasteIconContainer == null) {
		return false;
	}
	
	//	Looking for region
	var regionLabelContainer = null;
	var containerParentNode = $(pasteIconContainer.parentNode);
	while (regionLabelContainer == null && containerParentNode != null) {
		if (containerParentNode.hasClass('regionLabel')) {
			regionLabelContainer = containerParentNode;
		}
		else {
			containerParentNode = $(containerParentNode.parentNode);
		}
	}
	if (regionLabelContainer == null) {
		return false;
	}
	var regionContainer = $(regionLabelContainer.parentNode);
	if (regionContainer == null) {
		return false;
	}
	
	//	Modules count in current region
	var allModules = getNeededElementsFromList(regionContainer.childNodes, 'moduleContainer');
	
	//	Looking for region's id
	var parentId = getInputValue(regionLabelContainer.getElementsByTagName('INPUT'), 'parentKey');
	if (parentId == null) {
		parentId = getMarkupAttributeValue(regionLabelContainer, 'instanceid');
	}
	if (parentId == null) {
		return false;
	}
	
	showLoadingMessage(LOADING_LABEL);
	BuilderEngine.pasteModule(PAGE_KEY, parentId, allModules.length, COPIED_MODULE_ID != null, {
		callback: function(module) {
			addSelectedModuleCallback(module, regionContainer.id);
		}
	});
}

function showMessageForUnloadingPage() {
	showLoadingMessage(LOADING_LABEL);
}

function cutThisModule(id, containerId, instanceId) {
	COPIED_MODULE_ID = null;
	CUT_MODULE_ID = instanceId;
	
	var moduleToCut = $(containerId);
	if (moduleToCut == null) {
		return false;
	}
	var pageKey = getMarkupAttributeValue(moduleToCut, 'pageid');
	var parentId = getMarkupAttributeValue(moduleToCut, 'parentid');
	
	copyModule(containerId, pageKey, parentId, instanceId, id);
}

function showOrHideModulePasteIcons() {
	BuilderEngine.isModuleInClipboard(isModuleInClipboardCallback);
}

function isModuleInClipboardCallback(ids) {
	if (ids == null) {
		COPIED_MODULE_ID = null;
		return false;
	}
	
	if (ids[1] != null) {
		COPIED_MODULE_ID = null;
		CUT_MODULE_ID = ids[1];
		if (!VISIBLE_PASTE_ICON) {
			slideInModulePasteIcons();
		}
		return false;
	}
	
	if (ids[0] != null) {
		COPIED_MODULE_ID = ids[0];
		CUT_MODULE_ID = null;
		if (!VISIBLE_PASTE_ICON) {
			slideInModulePasteIcons();
		}
		return false;
	}
}

function removeBuilderModuleProperty(id, boxId, moduleId, propertyName) {
	var confirmed = window.confirm(ARE_YOU_SURE_MESSAGE);
	if (!confirmed) {
		return false;
	}
	
	showLoadingMessage(SAVING_LABEL);
	BuilderEngine.removeProperty(PAGE_KEY, moduleId, propertyName, {
		callback: function(result) {
			closeAllLoadingMessages();
			if (!result) {
				return false;
			}
			
			var image = $(id);
			image.remove();
			
			var container = $(boxId);
			container.removeClass('modulePropertyIsSet');
			container.addClass('moduleProperty');
			
			getBuilderModulePropertyBox(boxId, propertyName, moduleId, true);
			renderModuleAgain(PAGE_KEY, REGION_ID, moduleId, MODULE_CONTENT_ID);
		}
	});
}

function getRemoveBuilderPropertyImage(moduleId, propertyName) {
	var needImage = false;
	var boxId = ACTIVE_PROPERTY_SETTER_BOX;
	var container = $(boxId);
	var removeImagesInContainer = getElementsByClassName(container, 'img', 'removeBuilderModulePropertyStyle');
	if (removeImagesInContainer == null) {
		needImage = true;
	}
	if (removeImagesInContainer.length == 0) {
		needImage = true;
	}
	
	if (!needImage) {
		return false;
	}
	
	var image = new Element('img');
	var id = 'id' + new Date().getTime();
	image.setProperty('id', id);
	image.setProperty('src', REMOVE_BUILDER_PROPERTY_IMAGE);
	image.setProperty('alt', REMOVE_LABEL);
	image.setProperty('title', REMOVE_LABEL);
	image.setProperty('height', 16);
	image.setProperty('width', 16);
	image.addClass('removeBuilderModulePropertyStyle');
	
	
	image.addEvent('click', function() {
		removeBuilderModuleProperty(id, boxId, moduleId, propertyName);
	});
	
	image.injectAfter(container.getFirst());
}

function createTabsWithMootabs(id) {
	var tabs = new mootabs(id, {width: MOODALBOX_WINDOW_WIDTH + 'px', height: (MOODALBOX_WINDOW_HEIGHT - 50) + 'px'});	//	http://www.silverscripting.com/mootabs/
}