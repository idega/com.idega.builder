var ORIGINAL = null;
var CLONE = null;
var DROPPABLES = new Array();

function registerBuilderDragDropActions() {
	DROPPABLES = new Array();
	$$('div.moduleName').each(
		function(element) {
			element.addEvent('mousedown', function(e) {
				e = new Event(e).stop();

				ORIGINAL = element.parentNode.parentNode;
				CLONE = ORIGINAL.clone();
				CLONE.setStyle('moduleContainer');
				CLONE.setStyles(this.getCoordinates());
				CLONE.setStyles({'opacity': 0.8, 'position': 'absolute', 'z-index': 9999});
				CLONE.addEvent('emptydrop', function() {
					this.remove();
					manageDropAreas(false);
				}).inject(document.body);
		
				manageDropAreas(true);
				var drag = CLONE.makeDraggable({
					droppables: DROPPABLES
				});
				drag.start(e);
			});
		}
	);
	
	$$('div.moduleDropArea').each(
		function(element) {
			var dropFx = element.effect('background-color', {wait: false});
			element.addEvents({
				'drop': function() {
					var dropArea = getMarkupAttributeValue(element, 'insertbefore');
					var container = element.parentNode;
					
					var instanceId = ORIGINAL.getProperty('instanceid');
					var pageKey = ORIGINAL.getProperty('pageid');
					var formerParentId = ORIGINAL.getProperty('parentid');
					var newParentId = container.getProperty('parentid');
					var insertAbove = dropArea == 'true';
					var neighbourInstanceId = container.getProperty('instanceid');
					
					CLONE.remove();
					showLoadingMessage(MOVING_LABEL);
					BuilderEngine.moveModule(instanceId, pageKey, formerParentId, newParentId, neighbourInstanceId, insertAbove, {
						callback: function(result) {
							moveModuleCallback(result, element, container, dropFx, insertAbove);
						}
					});
				},
				'over': function() {
					dropFx.start('98B5C1');
				},
				'leave': function() {
					dropFx.start('ffffff');
				}
			});
			DROPPABLES.push(element);
		}
	);
}

function moveModuleCallback(result, element, container, dropFx, insertAbove) {
	closeAllLoadingMessages();
	
	if (!result) {
		return false;
	}
	
	if (insertAbove) {
		ORIGINAL.injectBefore(container);
	}
	else {
		ORIGINAL.injectAfter(container);
	}
	dropFx.start('7389AE').chain(dropFx.start.pass('ffffff', dropFx));
	manageDropAreas(false);
	ORIGINAL = null;
	CLONE = null;
}

function manageDropAreas(needToShow) {
	$$('div.moduleDropArea').each(
		function(element) {
			var canSetStyle = true;
			var parentNode = element.parentNode;
			canSetStyle = parentNode != null;
			if (canSetStyle) {
				if (ORIGINAL.id != parentNode.id) {
					if (needToShow) {
						element.setStyle('visibility', 'visible');
					}
					else {
						element.setStyle('visibility', 'hidden');
					}
				}
			}
		}
	);
}