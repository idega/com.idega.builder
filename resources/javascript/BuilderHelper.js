var ADD_NEW_COMPONENT_WINDOW_LINK = "/workspase/window/";
var EDIT_COMPONENT_WINDOW_LINK = "/workspase/window/";

var ADD_NEW_COMPONENT_IMAGE = "/idegaweb/bundles/com.idega.builder.bundle/resources/add.png";
var COMPONENT_INFORMATION_IMAGE = "/idegaweb/bundles/com.idega.builder.bundle/resources/information.png";

var ADD_NEW_COMPONENT_LABEL = "Add a new Module";
var COMPONENT_INFORMATION_LABEL = "Set module properties";
var NO_IDS_ERROR_MESSAGE = "Error occurred while inserting selected module!";
var ADDING_MODULE_LABEL = "Adding...";
var REGION_LABEL = "Region";
var DELETING_LABEL = "Deleting...";
var ARE_YOU_SURE_MESSAGE = "Are You sure?";
var SAVING_LABEL = "Saving...";
var LOADING_LABEL = "Loading...";
var RELOADING_LABEL = "Reloading...";

var PROPERTY_NAME = null;
var INSTANCE_ID = null;
var PARENT_ID = null;
var PAGE_KEY = null;
var REGION_ID = null;
var MODULE_CONTENT_ID = null;

var ACTIVE_PROPERTY_SETTER_BOX = null;

var IC_OBJECT_INSTANCE_ID_PARAMETER = "ic_object_instance_id_par";
var MODULE_NAME_PARAMETER = "moduleName";
var IB_PAGE_PARAMETER = "ib_page";
var HANLDER_VALUE_OBJECTS_STYLE_CLASS = "handlerValueObjects";

var PROPERTIES_SHOWN = new Array();
var PROPERTY_BOX_SHOWN = new Array();
var OBJECTS_TO_RERENDER = new Array();

function getBuilderInitInfo() {
	BuilderEngine.getBuilderInitInfo(getBuilderInitInfoCallback);
}

function getBuilderInitInfoCallback(list) {
	if (list == null) {
		return;
	}
	if (list.length != 19) {
		return;
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
	
}

function registerBuilderActions() {
	$$('div.moduleContainer').each(
		function(element) {
			element.onmouseover = function() {
				showAllComponentsLabels(element);
				showComponentInfoImage(element);
			},
			element.onmouseout = function() {
				hideOldLabels();
				hideComponentInfoImage(element);
			}
    	}
    );

	//setActionsForRegion();
    
    $$('div.regionInfoImageContainer').each(
    	function(element) {
			element.onclick = function() {
				setRegionAndModuleContentId(element);
			}
		}
	);
	
	$$('input.modulePropertySetter').each(
		function(element) {
			element.onblur = function(event) {
				if (element.type == "text") {
					saveModuleProperty(null, element);
				}
			},
			element.onkeypress = function(event) {
				saveModuleProperty(event, element);
			}
			element.onclick = function(event) {
				saveModuleProperty(event, element);
			}
		}
	);
}

function showAllComponentsLabels(element) {
	if (element == null) {
		return;
	}
	hideOldLabels();
	
	var children = getNeededElements(element, "DnDAreaTable");
	if (children == null) {
		return;
	}
	var child = null;
	var elementsToHighlight = null;
	for (var i = 0; i < children.length; i++) {
		child = children[i];
		child.style.visibility = "visible";
		/*elementsToHighlight = getNeededElementsFromList(child.getElementsByTagName("div"), "moduleName");
		for (var j = 0; j < elementsToHighlight.length; j++) {
			highlightElement(elementsToHighlight[j], 1000, "#FFFFFF");
		}*/
	}
}

function closeAddComponentContainer(id) {
	var container = document.getElementById(id);
	if (container == null) {
		return;
	}
	container.style.visibility = "hidden";
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
		return;
	}
	
	element.style.visibility = "visible";	
}

function setPropertiesForAddModule(element) {
	PARENT_ID = null;
	INSTANCE_ID = null;
	if (element == null) {	
		return;
	}
	
	var linkContainer = element.parentNode;
	if (linkContainer == null) {
		return null;
	}
	var region = linkContainer.parentNode;
	if (region == null) {
		return;
	}
	
	var children = region.childNodes;
	if (children == null) {
		return;
	}
	
	var child = null;
	var foundInstance = false;
	var foundParent = false;
	var inputs = null;
	for (var i = 0; (i < children.length && !foundInstance && !foundParent); i++) {
		child = children[i];
		if (child.className) {
			if (child.className == "moduleContainer") {
				inputs = child.getElementsByTagName("input");
				INSTANCE_ID = getInputValue(inputs, "instanceId");
				if (INSTANCE_ID != null) {
					foundInstance = true;
				}
				PARENT_ID = getInputValue(inputs, "parentId");
				if (PARENT_ID != null) {
					foundParent = true;
				}
			}
		}
	}
	
	if (!foundParent) {
		PARENT_ID = getInputValue(region.getElementsByTagName("input"), "parentKey");
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
	return elements[0];
}

function hideComponentInfoImage(element) {
	if (element == null) {
		return;
	}
	
	var list = getElementsByClassName(element, "*", "regionInfoImageContainer");
	var container = getFirstElementFromList(list);
	if (container == null) {
		return;
	}
	
	container.style.visibility = "hidden";
}

function showComponentInfoImage(element) {
	if (element == null) {
		return;
	}
	
	var list = getElementsByClassName(element, "*", "regionInfoImageContainer");
	var container = getFirstElementFromList(list);
	if (container == null) {
		return;
	}
	
	var link = getFirstElementFromList(container.getElementsByTagName("a"));
	if (link == null) {
		return;
	}
	if (container.style.visibility == "") {	// If it is the first time
		
		var moduleName = "Undefined";
		var moduleNameContainerList = getElementsByClassName(element, "*", "moduleName");
		if (moduleNameContainerList != null) {
			if (moduleNameContainerList.length > 0) {
				if (moduleNameContainerList[0].childNodes != null) {
					if (moduleNameContainerList[0].childNodes.length > 0) {
						moduleName = moduleNameContainerList[0].childNodes[0].nodeValue;
					}
				}
			}
		}
		
		var instanceId = null;
		var inputs = element.getElementsByTagName("input");
		if (inputs != null) {
			var input = null;
			var foundInstance = false;
			for (var i = 0; (i < inputs.length && !foundInstance); i++) {
				input = inputs[i];
				if (input.id != null) {
					if (input.id.indexOf("instanceId") != -1) {
						instanceId = input.value;
						foundInstance = true;
					}
				}
			}
		}

		link.removeAttribute("href");
		
		var uri = EDIT_COMPONENT_WINDOW_LINK + "&" + MODULE_NAME_PARAMETER + "=" + moduleName + "&" + IC_OBJECT_INSTANCE_ID_PARAMETER +
		 "=" + instanceId + "&" + IB_PAGE_PARAMETER + "=" + PAGE_KEY;
		link.setAttribute("href", uri);
		
		//	Link will be registered to MOOdalBox (if needed)
		MOOdalBox.register(link);
	}
	
	container.style.visibility = "visible";
}

function removeOldContainer(element, id) {
	var oldContainer = document.getElementById(id);
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

function hideOldLabels() {
	var children = document.getElementsByClassName("DnDAreaTable");
	if (children == null) {
		return;
	}
	var element = null;
	for (var i = 0; i < children.length; i++) {
		element = children[i];
		element.style.visibility = "hidden";
	}
}

function addSelectedModule(newObjectId, className) {
	if (PARENT_ID == null || PAGE_KEY == null) {
		alert(NO_IDS_ERROR_MESSAGE);
		return;
	}
	
	if (INSTANCE_ID == null) {
		INSTANCE_ID = PARENT_ID;
	}
	
	var index = 0;
	var container = document.getElementById(PARENT_ID);
	if (container != null) {
		var modules = getNeededElementsFromList(container.childNodes, "moduleContainer");
		if (modules != null) {
			index = modules.length;
		}
	}
	
	showLoadingMessage(ADDING_MODULE_LABEL);
	BuilderEngine.addSelectedModule(PAGE_KEY, INSTANCE_ID, newObjectId, PARENT_ID, className, index, {
		callback: function(component) {
			addSelectedModuleCallback(component, PARENT_ID);
		}
	});
}

function addSelectedModuleCallback(component, id) {
	closeLoadingMessage();
	if (component == null) {
		reloadPageAfterAddingModule();
		return;
	}
	var container = document.getElementById(id);
	if (container == null) {
		reloadPageAfterAddingModule();
		return;
	}
	var children = component.childNodes;
	if (children == null) {
		reloadPageAfterAddingModule();
		return;
	}
	if (children.length == 0) {
		reloadPageAfterAddingModule();
		return;
	}

	// Making copy
	var allNodes = getTransformedDocumentToDom(component);
	
	// Finding place where to put new module
	var elementToInsertBefore = null;
	var modules = getNeededElementsFromList(container.childNodes, "moduleContainer");
	if (modules == null) {
		reloadPageAfterAddingModule();
		return;
	}

	if (modules.length == 0) {
		elementToInsertBefore = container.firstChild;
	}
	else {
		var lastModule = modules[modules.length - 1];
		var elementAfterLastModule = lastModule.nextSibling;	// The proper place - after the last module container
		if (elementAfterLastModule == null) {
			elementToInsertBefore = container.lastChild;
		}
		else {
			elementToInsertBefore = elementAfterLastModule;
		}
	}
	
	// Inserting nodes
	var activeNode = null;
	var realNode = null;
	for (var i = 0; i < allNodes.length; i++) {
		activeNode = allNodes[allNodes.length - (i + 1)];
		realNode = createRealNode(activeNode);
		container.insertBefore(realNode, elementToInsertBefore);
		elementToInsertBefore = realNode;
	}
	
	registerBuilderActions();	// Need to re-register actions
	
	MOOdalBox.close();
}

function manageComponentPropertiesList(id) {	
	if (existsValueInList(PROPERTIES_SHOWN, id)) {
		removeElementFromArray(PROPERTIES_SHOWN, id);
		
		closeComponentPropertiesList(id);
	}
	else {
		PROPERTIES_SHOWN.push(id);
		
		var el = document.getElementById(id);
		el.style.display = "block";	
	}
}

function addPropertyIdAndClose(id) {
	if (id == null) {
		return;
	}
	PROPERTIES_SHOWN.push(id);
	closeComponentPropertiesList(id);
}

function closeComponentPropertiesList(id) {
	if (id == null) {
		return;
	}
	
	var el = document.getElementById(id);
	el.style.display = "none";
}

function exitFromPropertiesWindow() {
	PROPERTIES_SHOWN = new Array();
	editWindow.deactivate();
}

function closeAddModuleWindow() {
	valid.deactivate();
}

function deleteModule(id, pageKey, parentId, instanceId) {
	var deleteConfirmed = window.confirm(ARE_YOU_SURE_MESSAGE);
	if (deleteConfirmed) {
		showLoadingMessage(DELETING_LABEL);
		BuilderEngine.deleteSelectedModule(pageKey, parentId, instanceId, {
  			callback: function(result) {
    			deleteModuleCallback(result, id);
  			}
		});
	}
}

function deleteModuleCallback(result, id) {
	closeLoadingMessage();
	if (result) {
		var deleted = document.getElementById(id);
		if (deleted == null) {
			return;
		}
		var parentDeleted = deleted.parentNode;
		if (parentDeleted == null) {
			return;
		}
		parentDeleted.removeChild(deleted);
	}
}

function closeOldPropertyBoxes(currentID) {
	if (currentID == null || PROPERTY_BOX_SHOWN == null) {
		return;
	}
	var box = null;
	var id = null;
	var idsToRemove = new Array();
	for (var i = 0; i < PROPERTY_BOX_SHOWN.length; i++) {
		id = PROPERTY_BOX_SHOWN[i];
		if (id != currentID) {
			box = document.getElementById(id);
			if (box != null) {
				box.style.display = "none";
				idsToRemove.push(id);
			}
		}
	}
	for (var i = 0; i < idsToRemove.length; i++) {
		removeElementFromArray(PROPERTY_BOX_SHOWN, idsToRemove[i]);
	}
}

function getPropertyBox(id, propertyName, objectInstanceId) {
	ACTIVE_PROPERTY_SETTER_BOX = id;
	PROPERTY_NAME = propertyName;
	INSTANCE_ID = objectInstanceId;
	var fullId = id + "_property_setter_box";
	closeOldPropertyBoxes(fullId);
	var propertySetterBox = document.getElementById(fullId) ;
	if (propertySetterBox == null) {
		showLoadingMessage(LOADING_LABEL);
		BuilderEngine.getPropertyBox(PAGE_KEY, propertyName, objectInstanceId, {
			callback: function(box) {
				getPropertyBoxCallback(id, propertyName, objectInstanceId, box);
			}
		});
	}
	else {
		// Box allready exists
		if (propertySetterBox.style.display == "none") {
			propertySetterBox.style.display = "block";
			PROPERTY_BOX_SHOWN.push(fullId);
		}
		else {
			propertySetterBox.style.display = "none";
			removeElementFromArray(PROPERTY_BOX_SHOWN, fullId);
		}
	}
}

function getPropertyBoxCallback(id, propertyName, objectInstanceId, box) {
	closeLoadingMessage();
	if (id == null) {
		return;
	}
	var container = document.getElementById(id);
	if (container == null) {
		return;
	}
	if (box == null) {
		// TODO: add error
		return;
	}
	
	var propertySetterBox = document.createElement("div");
	var fullId = id + "_property_setter_box";
	propertySetterBox.setAttribute("id", fullId);
	PROPERTY_BOX_SHOWN.push(fullId);
	
	insertNodesToContainer(box, propertySetterBox);	
	container.appendChild(propertySetterBox);
	
	registerBuilderActions();	// Need to re-register actions
}

function saveModuleProperty(event, element) {
	if (element == null) {
		return;
	}
	if (event != null) {
		if (element.type == "text") {
			//	Checking if "Enter" was pressed
			if (!isEnterEvent(event)) {
				return;
			}
		}
	}
	
	var attr = element.attributes;	
	var moduleId = null;
	if (attr.getNamedItem("moduleid") != null) {
		moduleId = attr.getNamedItem("moduleid").value;
	}
	var propertyName = null;
	if (attr.getNamedItem("propname") != null) {
		propertyName = attr.getNamedItem("propname").value;
	}
	var needsReload = null;
	if (attr.getNamedItem("needsreload") != null) {
		needsReload = attr.getNamedItem("needsreload").value;
	}
	
	showLoadingMessage(SAVING_LABEL);
	BuilderEngine.setSimpleModuleProperty(PAGE_KEY, moduleId, propertyName, element.value, {
		callback: function(result) {
			saveModulePropertyCallback(result, moduleId, needsReload);
		}
	});
}

function reloadPageAfterAddingModule() {
	closeLoadingMessage();
	showLoadingMessage(RELOADING_LABEL);
		
	reloadPage();
}

function saveModulePropertyCallback(result, moduleId, needsReload) {
	if (!result) {
		closeLoadingMessage();
		return;
	}
	
	if (needsReload == "true") {
		reloadPageAfterAddingModule();
		return;
	}
	
	if (ACTIVE_PROPERTY_SETTER_BOX != null) {
		var setterBox = document.getElementById(ACTIVE_PROPERTY_SETTER_BOX);
		if (setterBox != null) {
			setterBox.className = "modulePropertyIsSet";
		}
	}
	
	closeLoadingMessage();
	
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
	BuilderEngine.reRenderObject(pageKey, regionId, moduleId, {
		callback: function(component) {
			reRenderObjectCallback(component, moduleContentId);
		}
	});
}

function renderModulesAgain() {
	var object = null;
	for (var i = 0; i < OBJECTS_TO_RERENDER.length; i++) {
		object = OBJECTS_TO_RERENDER[i];
		BuilderEngine.reRenderObject(object.pageKey, object.regionId, object.moduleId, {
			callback: function(component) {
				reRenderObjectCallback(component, object.moduleContentId);
			}
		});
	}
	OBJECTS_TO_RERENDER = new Array();
}

function reRenderObjectCallback(component, moduleContentId) {
	closeLoadingMessage();
	
	var container = document.getElementById(moduleContentId);
	if (container == null) {
		return;
	}
	
	removeChildren(container);
	
	showLoadingMessage(LOADING_LABEL);
	
	insertNodesToContainer(component, container);
	
	closeLoadingMessage();
}

function setRegionAndModuleContentId(element) {
	if (element == null) {
		REGION_ID = null;
		MODULE_CONTENT_ID = null;
		return;
	}
	var inputs = element.getElementsByTagName("input");
	if (inputs == null) {
		REGION_ID = null;
		MODULE_CONTENT_ID = null;
		return;
	}
	
	REGION_ID = getInputValue(inputs, "regionId");
	MODULE_CONTENT_ID = getInputValue(inputs, "moduleContentId");
}

function ReRenderObject(pageKey, regionId, moduleId, moduleContentId) {
	this.pageKey = pageKey;
	this.regionId = regionId;
	this.moduleId = moduleId;
	this.moduleContentId = moduleContentId;
}