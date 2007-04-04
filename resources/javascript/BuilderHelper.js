//var COMPONENT_INFO_CONTAINER = "builderRegionInfoImageContainer";
//var REGION_ADD_COMPONENT_CONTAINER = "builderRegionAddComponentContainer";
//var ACTIVE_ADD_COMPONENT_ELEMENT_ID = "addComponentToActiveRegionId";
//var ACTIVE_ADD_INFO_ELEMENT_ID = "addInfoPanelToActiveRegionId";

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

var INSTANCE_ID = null;
var PARENT_ID = null;
var PAGE_KEY = null;

var IC_OBJECT_INSTANCE_ID_PARAMETER = "ic_object_instance_id_par";
var MODULE_NAME_PARAMETER = "moduleName";

//var EXISTING_CONTAINERS_ID = new Array();
var PROPERTIES_SHOWN = new Array();

function getBuilderInitInfo() {
	BuilderEngine.getBuilderInitInfo(getBuilderInitInfoCallback);
}

function getBuilderInitInfoCallback(list) {
	if (list == null) {
		return;
	}
	if (list.length != 14) {
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
}

function registerBuilderActions() {
	var builderRules = {
		'div.moduleContainer' : function(element) {
			element.onmouseover = function() {
				showAllComponentsLabels(element);
				showComponentInfoImage(element);
			},
			element.onmouseout = function() {
				hideOldLabels();
				hideComponentInfoImage(element);
			}
		},
		'div.regionLabel' : function(element) {
			var parentElement = element.parentNode;
			if (parentElement == null) {
				return;
			}
			var regionLabel = "";
			var inputs = element.getElementsByTagName("input");
			if (inputs != null) {
				if (inputs.length > 0) {
					var input = inputs[0];
					if (input.type != null) {
						if (input.type == "hidden") {
							regionLabel = input.value;
						}
					}
				}
			}
			parentElement.onmouseover = function() {
				showAddComponentImage(parentElement, element, regionLabel);
			},
			parentElement.onmouseout = function() {
				closeAddComponentContainer(element.id);
			}
		}
	};
	Behaviour.register(builderRules);
	Behaviour.apply();
}

function showAllComponentsLabels(element) {
	if (element == null) {
		return;
	}
	hideOldLabels();
	
	var children = getNeededBuilderElements(element, "DnDAreaTable");
	if (children == null) {
		return;
	}
	var child = null;
	var elementsToHighlight = null;
	for (var i = 0; i < children.length; i++) {
		child = children[i];
		child.style.visibility = "visible";
		elementsToHighlight = getNeededBuilderElementsFromList(child.getElementsByTagName("div"), "moduleName");
		for (var j = 0; j < elementsToHighlight.length; j++) {
			new Effect.Highlight(elementsToHighlight[j]);
		}
	}
}

function closeAddComponentContainer(id) {
	var container = document.getElementById(id);
	if (container == null) {
		return;
	}
	//new Effect.Fade(container);	// Changes DOM
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
	/*
	// Removing old containers
	//removeAddComponentImage(element);
	
	// Checking if container allready exists in this element
	var children = getNeededBuilderElementsFromListById(element.childNodes, REGION_ADD_COMPONENT_CONTAINER + regionLabel);
	if (children.length > 0) {
		new Effect.Appear(REGION_ADD_COMPONENT_CONTAINER + regionLabel);
		return;
	}
	
	element.className = "regionContainer";
	
	//EXISTING_CONTAINERS_ID.push(regionLabel);
	
	// Main container
	var container = document.createElement("div");
	container.setAttribute("id", REGION_ADD_COMPONENT_CONTAINER + regionLabel);
	container.className = "regionComponentImageContainer";
	
	// Region name
	var regionName = document.createTextNode(REGION_LABEL + ": " + regionLabel + " ");
	container.appendChild(regionName);
	
	// Link
	var linkToComponents = document.createElement("a");
	linkToComponents.setAttribute("title", ADD_NEW_COMPONENT_LABEL);
	linkToComponents.className = "lbOn";
	linkToComponents.setAttribute("href", ADD_NEW_COMPONENT_WINDOW_LINK);
	
	// Image
	var image = document.createElement("img");
	image.setAttribute("src", ADD_NEW_COMPONENT_IMAGE);
	image.setAttribute("title", ADD_NEW_COMPONENT_LABEL);
	linkToComponents.appendChild(image);
	if (typeof container.attachEvent == "undefined") {
		image.addEventListener("click", function(e){setPropertiesForAddModule(element);}, false);
	} else {
		image.attachEvent("onclick", function(e){setPropertiesForAddModule(element);});
	}
	
	// Appending elements to document
	container.appendChild(linkToComponents);
	element.appendChild(container);	
	
	// Initializing Lightbox
	addLightboxMarkup();
	modulesWindow = new lightbox(linkToComponents);
	roundModulesListCorners();*/
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
	var found = false;
	for (var i = 0; (i < children.length && !found); i++) {
		child = children[i];
		if (child.className) {
			if (child.className == "moduleContainer") {
				found = analyzeModuleContainerInputs(child.childNodes);
			}
		}
	}

	if (!found) {
		PARENT_ID = null;
		INSTANCE_ID = null;
		return;
	}
}

function analyzeModuleContainerInputs(inputs) {
	if (inputs == null) {
		return false;			
	}
	var setInstanceId = false;
	var setParentId = false;
	for (var i = 0; i < inputs.length; i++) {
		input = inputs[i];
		if (input.id != null) {
			if (input.id.indexOf("instanceId") != -1) {
				INSTANCE_ID = input.value;
				setInstanceId = true;
			}
			if (input.id.indexOf("parentId") != -1) {
				PARENT_ID = input.value;
				setParentId = true;
			}
		}
	}
	if (setInstanceId && setParentId) {
		return true;
	}
	return false;
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
	
	var list = element.getElementsByClassName("regionInfoImageContainer");
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
	
	var list = element.getElementsByClassName("regionInfoImageContainer");
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
		var moduleNameContainerList = element.getElementsByClassName("moduleName");
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
		link.className = "lbOn";
		var uri = EDIT_COMPONENT_WINDOW_LINK + "?" + MODULE_NAME_PARAMETER + "=" + moduleName + "&" + IC_OBJECT_INSTANCE_ID_PARAMETER +
		 "=" + instanceId;
		link.setAttribute("href", uri);
		
		addLightboxMarkup();
		editWindow = new lightbox(link);
		roundModulesListCorners();
	}
	
	container.style.visibility = "visible";
	new Effect.Pulsate(link);
}

function getNeededBuilderElements(element, className) {
	if (element == null) {
		return null;
	}
	return getNeededBuilderElementsFromList(element.childNodes, className);
}

function getNeededBuilderElementsFromList(list, className) {
	if (list == null || className == null) {
		return new Array();
	}
	var childElement = null;
	var elements = new Array();
	for (var i = 0; i < list.length; i++) {
		childElement = list[i];
		if (childElement != null) {
			if (childElement.className != null) {
				if (childElement.className == className) {
					elements.push(childElement);
				}
			}
		}
	}
	return elements;
}

function getNeededBuilderElementsFromListById(list, id) {
	if (list == null || id == null) {
		return new Array();
	}
	var childElement = null;
	var elements = new Array();
	for (var i = 0; i < list.length; i++) {
		childElement = list[i];
		if (childElement != null) {
			if (childElement.id != null) {
				if (childElement.id == id) {
					elements.push(childElement);
				}
			}
		}
	}
	return elements;
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
	if (INSTANCE_ID == null || PARENT_ID == null || PAGE_KEY == null) {
		alert(NO_IDS_ERROR_MESSAGE);
		return;
	}
	showLoadingMessage(ADDING_MODULE_LABEL);
	BuilderEngine.addSelectedModule(PAGE_KEY, INSTANCE_ID, newObjectId, PARENT_ID, className, {
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
	for (var i = 0; i < children.length; i++) {
		container.appendChild(children[i]);
	}
	valid.deactivate();
}

function reloadPageAfterAddingModule() {
	valid.deactivate();
	window.location.href = window.location.href;
}

function roundModulesListCorners() {
	Nifty("ul#modules_lists h3","top");
	Nifty("ul#modules_lists div","bottom same-height");
}

function roundLightboxWindow() {
	Nifty("div#lightbox","big");
}

function initializeEditModuleWindow() {
	Nifty("div#editModuleHeader","big");
	Nifty("ul#editModuleMenuNavigation a","small transparent top");
	Nifty("div#simple_properties_box","transparent");
	Nifty("div#advanced_properties_box","transparent");
}

function manageComponentPropertiesList(id) {
	if (existsValueInList(PROPERTIES_SHOWN, id)) {
		PROPERTIES_SHOWN.pop(id);
		closeComponentPropertiesList(id);
	}
	else {
		PROPERTIES_SHOWN.push(id);
		new Effect.SlideDown(id);
	}
}

function closeComponentPropertiesList(id) {
	if (id == null) {
		return;
	}
	new Effect.SlideUp(id);
}

function exitFromPropertiesWindow() {
	PROPERTIES_SHOWN = new Array();
	editWindow.deactivate();
}

function closeAddModuleWindow() {
	valid.deactivate();
}

function deleteModule(id, pageKey, parentId, instanceId) {
	var confirmed = confirm(ARE_YOU_SURE_MESSAGE);
	if (!confirmed) {
		return;
	}
	showLoadingMessage(DELETING_LABEL);
	BuilderEngine.deleteSelectedModule(pageKey, parentId, instanceId, {
  		callback: function(result) {
    		deleteModuleCallback(result, id);
  		}
	});
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