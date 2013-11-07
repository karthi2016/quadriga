var labelType, useGradients, nativeTextSupport, animate,pathName;

(function() {
	var ua = navigator.userAgent,
	iStuff = ua.match(/iPhone/i) || ua.match(/iPad/i),
	typeOfCanvas = typeof HTMLCanvasElement,
	nativeCanvasSupport = (typeOfCanvas == 'object' || typeOfCanvas == 'function'),
	textSupport = nativeCanvasSupport 
	&& (typeof document.createElement('canvas').getContext('2d').fillText == 'function');
	//I'm setting this based on the fact that ExCanvas provides text support for IE
	//and that as of today iPhone/iPad current text support is lame
	labelType = (!nativeCanvasSupport || (textSupport && !iStuff))? 'Native' : 'HTML';
	nativeTextSupport = labelType == 'Native';
	useGradients = nativeCanvasSupport;
	animate = !(iStuff || !nativeCanvasSupport);
	
	
	
})();



function makeAjaxCall()
{
  //Establish connection to php script
  $.ajax({
      type: 'POST',
      url: $('#annot_form').attr("action")   
  }).done(function(data) { 
	  location.reload();
	  alert(data);
	  var msg = '<span>' + data + '</span>';
      $('#message').html(msg);
	  console.log(data); })
    .fail(function() { alert("error"); })
    .always(function() { alert("complete"); });
}

/*var request = $.ajax({
	url: $('#annot_form').attr("action"),
	type: "post",
	data:   
		{
			'action': 'update',
			'id': suggestion_id,
			'name': selected_text,
			'uri': selected_uri,
			'paper': 0
		}
});
 
request.done(function (response, textStatus, jqXHR) {
	alert("success: annotation created");
	console.log(response)
});

function callAjax(){

	var url_form = $('#annot_form').attr("action");
	//alert(url_form);
	
  var ajaxCallback =  makeAjaxCall(url_form);
  
  ajaxCallback.success(function(data) {
	  alert(data);
		//Load the new text in the corresponding div tag
		if(data == 'success'){
			alert("Annotation added succesfully");
		}
		else
		{
			alert("Failed to add annotation");
		}
								
	});
}

function makeAjaxCall(url_form){
	$.ajax({
	       type: 'POST',
	       url: url_form,
	       error : function(jqXHR, textStatus, errorThrown) {
			alert(errorThrown);
		}
	    });
	
}*/
/*$("#annot_submit").submit(function(event) 
			{
		alert("here");
		var url = path+"/auth/editing/saveAnnotation/"+networkId;
	    $.ajax({
	       type: "POST",
	       beforeSend: function(){},
	       url: url,
	       data: $("#annot_form").serialize(), // serializes the form's elements.
	       success: function(data)
	       {
	    	   alert("success");
	          // document.getElementById("statushidden").value=data;
	          // console.log($("annot_form")[0].submit);
	          // $("annot_form")[0].submit(); ///// SHOWING Error on this line///////      
	       }
	    });

			});*/

var Log = {
		elem: false,
		write: function(text){
			if (!this.elem) 
				this.elem = document.getElementById('log');
			this.elem.innerHTML = text;
			this.elem.style.left = (500 - this.elem.offsetWidth / 2) + 'px';
		}
};


function init(json,networkId,path){
	pathName = path;
	//alert(networkId);
	/*alert("hi");
	alert(path);*/
	// init data
	// end
	// init ForceDirected
	//alert("json");
	  //label placement on edges 
	//alert(path);
	  $jit.ForceDirected.Plot.EdgeTypes.implement({ 
		  'labeled': {
              'render': function(adj, canvas) {
                this.edgeTypes.arrow.render.call(this, adj, canvas);
                var data = adj.data;
                if(data.$labeltext) {
                  var ctx = canvas.getCtx();
                  var posFr = adj.nodeFrom.pos.getc(true);
                  var posTo = adj.nodeTo.pos.getc(true);
                  ctx.fillText(data.$labeltext, (posFr.x + posTo.x)/2, (posFr.y + posTo.y)/2);
                }// if data.labeltext
              }
            }
	  }); 
	  
	var fd = new $jit.ForceDirected({
		//id of the visualization container
		injectInto: 'infovis',
		//Enable zooming and panning
		//by scrolling and DnD
		Navigation: {
			enable: true,
			//Enable panning events only if we're dragging the empty
			//canvas (and not a node).
			panning: 'avoid nodes',
			zooming: 10 //zoom speed. higher is more sensible
		},
		// Change node and edge styles such as
		// color and width.
		// These properties are also set per node
		// with dollar prefixed data-properties in the
		// JSON structure.
		Node: {
		      overridable: true,
		      dim : '10',
		      color : '#8904B1'
		    },
		Edge: {
			overridable: true,
			type : 'arrow',
			color: '#23A4FF',
			lineWidth: 0.4,
			type: 'labeled',
			dim: '15'
		},
		//Native canvas text styling
		Label: {
			type: labelType, //Native or HTML
			size: 10
		},
		//Add Tips
		Tips: {
			enable: true,
			onShow: function(tip, node) {
				//count connections
				var count = 0;
				node.eachAdjacency(function() { count++; });
				//display node info in tooltip
				tip.innerHTML = "<div class=\"tip-title\">" + node.name + "</div>"
				+ "<div class=\"tip-text\"><b>connections:</b> " + count + "</div>";
			}
		},
		// Add node events
		Events: {
			enable: true,
			type: 'Native',
			//Change cursor style when hovering a node
			onMouseEnter: function() {
				fd.canvas.getElement().style.cursor = 'move';
			},
			onMouseLeave: function() {
				fd.canvas.getElement().style.cursor = '';
			},
			//Update node positions when dragged
			onDragMove: function(node, eventInfo, e) {
				var pos = eventInfo.getPos();
				node.pos.setc(pos.x, pos.y);
				fd.plot();
			},
			//Implement the same handler for touchscreens
			onTouchMove: function(node, eventInfo, e) {
				$jit.util.event.stop(e); //stop default touchmove event
				this.onDragMove(node, eventInfo, e);
			},
			//Add also a click handler to nodes
			onClick: function(node) {
				if(!node) return;
				// Build the right column relations list.
				// This is done by traversing the clicked node connections.
				var html = "<h4>" + node.name + "</h4><b> connections:</b><ul><li>",
				list = [];
				node.eachAdjacency(function(adj){
					// Adding arrow label to inner-details
					var str2 = adj.data.$labeltext;
					var str1 = adj.nodeTo.name;
					
					var str3 = str2.concat(" : ");
					str3 = str3.concat(str1);
					list.push(str3);
				});
				//append connections information
				$jit.id('inner-details').innerHTML = html + list.join("</li><li>") + "</li></ul>";
			},
			
			onRightClick: function(node) {
		        if(!node) return;
				//alert("hi");
		        // Build the right column relations list.
		        // This is done by traversing the clicked node connections.
		       // var html = "<h4>" + node.name + "</h4><b> connections:</b><ul><li>",
			   var html = "";
			   
			   html = "<form id='annot_form' action="+path+"/auth/editing/saveAnnotation/" ;
			   		html+= networkId+" method='POST' >";
			   		//html = "<form id='annot_form' action='ggb' method='POST'  >";
			   		html += "<p id='message'></p>";
		         html += "<textarea name='annotText' id='text' cols='15' rows='15'></textarea>";
		        html+= "<input  type='hidden' name='nodename' id='nodename' value="+node.id+" />";
				  html+= "<input type='submit' id='annot_submit' value='submit' onclick='callAjax();'>";
				//  html+= "<input type='submit' id='annot_submit' value='submit' />";
				  html += "</form>";
			  
				  
		         //append connections information
		        $jit.id('inner-details').innerHTML = path+ html ;
		      },
		},
		//Number of iterations for the FD algorithm
		iterations: 200,
		//Edge length
		levelDistance: 130,
		// Add text to the labels. This method is only triggered
		// on label creation and only for DOM labels (not native canvas ones).
		onCreateLabel: function(domElement, node){
			domElement.innerHTML = node.name;
			var style = domElement.style;
			style.fontSize = "0.8em";
			style.color = "#ddd";
		},
		// Change node styles when DOM labels are placed
		// or moved.
		onPlaceLabel: function(domElement, node){
			var style = domElement.style;
			var left = parseInt(style.left);
			var top = parseInt(style.top);
			var w = domElement.offsetWidth;
			style.left = (left - w / 2) + 'px';
			style.top = (top + 10) + 'px';
			style.display = '';
		}
	});
	// load JSON data.
	fd.loadJSON(json);
	// compute positions incrementally and animate.
	fd.computeIncremental({
		iter: 40,
		property: 'end',
		onStep: function(perc){
			Log.write(perc + '% loaded...');
		},
		onComplete: function(){
			Log.write('Loaded completely');
			fd.animate({
				modes: ['linear'],
				transition: $jit.Trans.Elastic.easeOut,
				duration: 2500
			});
		}
	});
	// end
}