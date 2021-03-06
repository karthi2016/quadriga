<%@ page language="java" contentType="text/html;"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<link rel="stylesheet"
	href="${pageContext.servletContext.contextPath}/resources/txt-layout/css/style.min.css" />

<!-- CSS Files -->
<link type="text/css"
	href="${pageContext.servletContext.contextPath}/resources/css/base.css"
	rel="stylesheet" />
	
<link type="text/css"
	href="${pageContext.servletContext.contextPath}/resources/css/ForceDirected.css"
	rel="stylesheet" />
<link type="text/css"
	href="${pageContext.servletContext.contextPath}/resources/css/d3.css"
	rel="stylesheet" />

<script
	src="${pageContext.servletContext.contextPath}/resources/js/cytoscape/dist/cytoscape.js"></script>

<div class="row" id="message" style="display: none;">
</div>

<p id="network" style="display: none;">
    <a onclick="goFullscreen('networkBox')" style="float: left" title="Switch to fullscreen">
        <i class="fa fa-arrows-alt"></i>
    </a>
	
	<div class="row">
		<div id="networkBox" class="col-sm-12"
			style="min-height: 500px; height: 100%; text-align: left;"></div>
	</div>

	<div id="inner-details" class="row"></div>
	<div id="allannot_details" class="row">
		<div class="row">
			<table id="annotationsTable"></table>
		</div>
	</div>
</p>
<div id="log" class="row"></div>

<div id="loading">
  <p>Please wait while the data is loading... </p>
  <center>
  	<img src="${pageContext.servletContext.contextPath}/resources/public/imgs/ajax-loader.gif" />
  </center>
</div>
<script type="text/javascript">
//# sourceURL=dynamicScript.js 

  function goFullscreen(id) {
	var element = document.getElementById(id);
    if (element.mozRequestFullScreen) {
    	// This is how to go into fullscren mode in Firefox
        // Note the "moz" prefix, which is short for Mozilla.
        element.mozRequestFullScreen();
    } else if (element.webkitRequestFullScreen) {
      // This is how to go into fullscreen mode in Chrome and Safari
      // Both of those browsers are based on the Webkit project, hence the same prefix.
      element.webkitRequestFullScreen();
    }
  }

</script>

<script type="text/javascript">
//# sourceURL=dynamicScript2.js 
function clear()
{
	var element=document.getElementById('networkBox');
	element.style.removeProperty('position');//=null;
	element.style.top=null;
	element.style.bottom=null;
	element.style.right=null;
	element.style.left=null;
}

</script>

<script>
if (document.addEventListener)
{
    document.addEventListener('webkitfullscreenchange', exitHandler, false);
    document.addEventListener('mozfullscreenchange', exitHandler, false);
    document.addEventListener('fullscreenchange', exitHandler, false);
    document.addEventListener('MSFullscreenChange', exitHandler, false);
}

function exitHandler()
{
    if (document.webkitIsFullScreen || document.mozFullScreen || document.msFullscreenElement !== null)
    {
    	if(window.innerWidth == screen.width && window.innerHeight == screen.height) {
    	}
    	else
    	{
    		clear();
    	}
    }
}
</script>

<script
	src="https://cdn.rawgit.com/cytoscape/cytoscape.js-cose-bilkent/1.0.2/cytoscape-cose-bilkent.js"></script>
<script src="${pageContext.servletContext.contextPath}/resources/js/cytoscape/publicNetwork.js" ></script>
<script type="text/javascript">
//# sourceURL=test.js

var cy;

function buildCytoscape(jsonstring) {
	var container = document.getElementById('networkBox');

	cy = cytoscape({
	    container: container, // container to render in

	    elements: jsonstring,
	    layout: {
	        name: 'cose',
	        idealEdgeLength: 5
	      },
	    style: [ // the stylesheet for the graph
	             {
	               selector: 'node',
	               style: {
	                 'background-color': 'mapData(group, 0, 1, #E1CE7A, #FDD692)',
	                 'border-color' : '#B98F88',
	                 'border-width' : 1,
	                 'font-family': 'Open Sans',
	                 'font-size': '12px',
	                 'font-weight' : 'bold',
	                 'color': 'mapData(group, 0, 1, #666, #333)',
	                 'label': 'data(conceptName)',
	                 'width':'mapData(group, 0, 1, 40, 55)',
	                 "height":"mapData(group, 0, 1, 40, 55)",
	                 'text-valign' : 'center',
	               }
	             },

	             {
	               selector: 'edge',
	               style: {
	                 'width': 1,
	                 'line-color': '#754F44',
	                 'target-arrow-shape': 'none'
	               }
	             }
	           ]
	});

	defineListeners(cy, '${pageContext.servletContext.contextPath}', '');
}


$( document ).ready(function() { 
	$('#loading').show();
	$.ajax({
		url: "${pageContext.servletContext.contextPath}/public/loadnetworks",
		type: "GET",
		success: function(result) {
			checkStatus();
		},
		error: function(error) {
			$('#loading').hide();
			$("#message").html("<div class='alert alert-danger'>"+error+"</div>");
			$("#message").show();
		}
	});
});

function checkStatus() {
	$.ajax({
		url: "${pageContext.servletContext.contextPath}/public/networkloadstatus",
		type: "GET",
		success: function(result) {
			if("RUNNING" === result) {
				setTimeout(checkStatus, 5000);
			} else if("COMPLETED" === result){
				buildNetwork();
				$('#loading').hide();
			}
		}
	});
}

function buildNetwork() {
	$.ajax({
		url: "${pageContext.servletContext.contextPath}/public/networks",
		type: "GET",
		success: function(result) {
				if(!result) {
					$("#message").html("<div class='alert alert-info'>Could not find any nodes for the search term in the project</div>");
					$("#message").show();
				} else {
					buildCytoscape(JSON.parse(result));
					$("#network").show();
					$("#rightpanel").show();
				}
			}
		});
}

$( document ).ready(function() {
	$('#exportJson').on('click', function() {
		var json = cy.json();
		window.open('data:application/json,' +
        encodeURIComponent(JSON.stringify(json), '_blank'));
	});
});

$( document ).ready(function() {
    $('#exportPng').on('click', function() {
        var png = cy.png({'scale' : 5});
        window.open(png, '_blank');
    });
});

</script>