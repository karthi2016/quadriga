<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page import = "edu.asu.spring.quadriga.domain.impl.workspace.TextFile" %>

<script
    src="${pageContext.servletContext.contextPath}/resources/js/cytoscape/dist/cytoscape.js"></script>
<script src="${pageContext.servletContext.contextPath}/resources/js/cytoscape/publicNetwork.js" ></script>


<div class="container">
    <div class="row">
    
        <div class="col-md-8">
            <div class="row">
		        <div class="col-sm-12 search-wrapper" style="position: relative">
		            <h2>Text Search</h2>
		    
		            <div id="search-form" class="form-inline" style="margin-top: 20px;">
		                <div class="form-group search-input" style="width: 100%;">
		                    <label for="search-term">What concept are you looking for?</label>
		                    <div class="input-group" style="width: 100%;">
			                    <input placeholder="Enter search term" type="text" class="form-control" id="search-term" autocomplete="off">
			                    <div div class="input-group-addon" style="width: 40px;"><div style="background: url('${pageContext.servletContext.contextPath}/resources/txt-layout/images/throbber.gif');"
			                          id="ajax-loader" class="search-loader"></div></div>
		                    </div>
		                </div>
		            </div>
		            <div id="search-results-wrapper" style="display: none;">
		                <div class="col-sm-12 search-results">
		                    <div class="list-group" style="border: 1px solid #dddddd; max-height: 300px; overflow-y:scroll;" id="search-results-items">
		                    </div>
		                    <div style="display: none;">
		                        <a href="#" class="list-group-item" id="search-item-template">
		                            <span class="search-name text-primary"><strong></strong></span> <span class="search-type label label-primary"></span> 
		                            <span class="search-pos label label-info text-lowercase"></span>
		                            <small><br><span class="search-desc text-muted"></span></small>
		                        </a>
		                    </div>
		                </div>
		            </div>
		        </div>
		    </div>
		    
		    <div class="row">
                <div class="col-sm-12"> 
                
                <c:if test="${not empty concept}">
                    <h3 style="margin-bottom: 0px;">Results for:</h3>
                    <h4 style="margin-bottom: 20px;">${concept.lemma}
                    <small><span class="label label-default">${concept.type}</span>
                    <br>${concept.description}
                    </small>
                    </h4>
                </c:if>
                <div class="list-group">
                <c:if test="${empty texts and empty references and not empty concept}">
                <div class="panel panel-default">
                    <div class="panel-body">Your search has no results.</div>
                </div>
                </c:if>
                <c:forEach items="${texts}" var="textfile">
                    <div class="list-group-item">
                    <p class="pull-right">
                       <a href="${textfile.refId}" title="Go to original" target="_blank">
                          <i class="fa fa-share" aria-hidden="true"></i>
                       </a>
                    </p>
                    <h4>
                    <a href="#" data-toggle="modal"
                       data-target="#txtModal" data-txtid="${textfile.textId}"
                       data-txtname="${textfile.fileName}" data-txttitle="${textfile.title}"
                       data-txtauthor="${textfile.author}" data-txtdate="${textfile.creationDate}">
                       <i class="fa fa-file-text-o" aria-hidden="true"></i>
                        <c:if test="${not empty textfile.author}">${textfile.author}, </c:if>
                        <c:if test="${not empty textfile.title}"><em>${textfile.title}</em></c:if>
                        <c:if test="${not empty textfile.creationDate}"> (${textfile.creationDate})</c:if>
                        <c:if test="${empty textfile.author and empty textfile.title and empty textfile.creationDate}">No author and title information provided.</c:if>
                    </a>
                    </h4>
                    
                    <p style="margin-bottom: 0px; line-height: 1.3"><small>${textfile.snippet}</small></p>
                    </div>
                </c:forEach>
                
                <c:if test="${not empty references}">
                <h4 style="margin-top: 30px;">External Resources</h4>
                <c:forEach items="${references}" var="ref">
                  <div class="list-group-item">
                      <h4>
                        <a href="${ref}" target="_blank"><i class="fa fa-share" aria-hidden="true"></i> ${ref}</a>
                      </h4>
                  </div>
                </c:forEach>
                </c:if>
                
                </div>
             </div>
        </div>
    </div>
    
     <div class="col-md-4" style="padding-top: 100px;">
        <div class="row">
            <div class="col-sm-12">
            <c:if test="${jsonstring != '[]'}">
                <small>Click on a node to search for its concept.</small>
            </c:if>
            </div>
            <div class="col-sm-12" style="min-height: 45px;">
		       <small>
		          <div id="searchForNode" style="display: none; ">
	           Search for concept <em id="nodeName"></em>?&nbsp;&nbsp; <a id="searchNodeLink"><strong>Yes, search!</strong></a>
	               </div>
	           </small>
	         </div>
	         <div class="col-sm-12">
		      <div id="networkBox" style="min-height: 500px; width: 100%; text-align: left;"></div>  
	         </div>
	     </div>
	 </div>

    </div>
</div>

<div class="modal text-modal" id="txtModal" tabindex="-1" role="dialog"
    aria-labelledby="txtModal" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content ">
            <div class="modal-header">
                <h4 class="modal-title" id="myModalLabel"></h4>
            </div>
            <div class="modal-body" style="height: 500px; overflow-y: scroll;"></div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
            </div>
        </div>
    </div>
</div>

<script>

var container = document.getElementById('networkBox');

var cy = cytoscape({
    container: container, // container to render in

    elements: ${jsonstring},
    layout: {
        name: 'cose',
        idealEdgeLength: 5,
        gravity: 80,
        edgeElasticity: 20,
      },
    style: [ // the stylesheet for the graph
             {
               selector: 'node',
               style: {
                 'background-color': 'mapData(group, 0, 1, #E1CE7A, #FDD692)',
                 'border-color' : '#B98F88',
                 'border-width' : 1,
                 'font-family': 'Raleway, Sans Serif',
                 'font-size': '13px',
                 'color': 'mapData(group, 0, 1, #666, #333)',
                 'label': 'data(conceptName)',
                 'width':'mapData(group, 0, 1, 10, 35)',
                 "height":"mapData(group, 0, 1, 10, 35)",
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

defineFadeListenersSearch(cy, '${pageContext.servletContext.contextPath}');
defineDoubleClickSearch(cy, '${pageContext.servletContext.contextPath}');

</script>


<script>
//# sourceURL=loader.js
    function init() {
        // ajax loader
        var networkURL = '${pageContext.servletContext.contextPath}/search/texts?conceptId=';
        var $searchInput = $('#search-term');
        var $resWrapper = $('#search-results-wrapper');
        var $items = $('#search-results-items');
        var $list = $resWrapper.find('.list-group-item:first');
        var $loader = $('#ajax-loader');
        
        var loader = (function() {
            // var isVisible = false;
            var timeout;
            var interval = 400;
            var fn = function() {
                if ($loader.is(':visible')) {
                    $loader.hide();
                }
            };
            return {
                show: function() {
                    $loader.css('display', 'inline-block');
                },
                hide: function() {
                    // clear timeout
                    clearTimeout(timeout);
                    timeout = setTimeout(fn, interval);
                }
            };
        })();
        // ajax loader
        $(document).on({
            ajaxStart: loader.show,
            ajaxStop: loader.hide
        });
         var triggerChange = (function() {
            var timeout;
            var timeoutInt = 400;
            var prevVal = '';
            var minChars = 2;
            var change = function() {
                $searchInput.trigger('textChange');
            };
            return function(ev) {
                var val = $searchInput.val().trim();
                if (val === prevVal || val.length < minChars) {
                    return;
                }
                prevVal = val;
                // clear this interval
                clearTimeout(timeout);
                timeout = setTimeout(change, timeoutInt);
            }
        })();
        var reqSuccess = function(data) {
            var terms = data.terms || [];
            addTerms(terms);
        };
        var reqFail = function(err) {
            // triggered even when abort is called
            console.log(err);
        };
        var reqAlways = function(obj) {
            // this triggered always
        };
        var addTerms = (function() {
            // clear all the terms
            var $a = $('#search-item-template');
            var maxResults = 15;
            return function(terms) {
                // if terms are empty hide the search wrapper
                if (terms && terms.length === 0) {
                    $resWrapper.hide();
                    return;
                }
                var $link;
                var maxNum = Math.min(terms.length, maxResults);
                $items.html('');
                for (var i = 0; i < maxNum; i++) {
                    $link = $a.clone();
                    $link.attr('id', '');
                    $link.find('.search-name strong').text(terms[i].name);
                    $link.attr('href', networkURL + terms[i].id);
                    $link.find('.search-desc').text(terms[i].description);
                    $link.find('.search-type').text(terms[i].type);
                    $link.find('.search-pos').text(terms[i].pos);
                    $items.append($link);
                }
                $resWrapper.show();
            };
        })();
        var onChange = (function(ev) {
            // cancel the original request
            // and make a new request
            var $xhr;
            var done = function(data) {
                reqSuccess(data);
            };
            var fail = function(err) {
                reqFail(err);
            };
            var always = function(obj) {
                reqAlways(obj);
            };
            return function() {
                // cancel the request
                if ($xhr) {
                    $xhr.abort();
                }
                var searchVal = $searchInput.val();
                if (searchVal.trim().length === 0) {
                    // do not make request
                    // send empty data
                    addTerms([]);
                    return;
                }
                // start a new request
                $xhr = $.ajax({
                    method: 'get',
                    dataType: 'json',
                    url: "${pageContext.servletContext.contextPath}/public/concept/search",
                    data: {
                        searchTerm: searchVal
                    }
                }).done(done).fail(fail).always(always);
            };
        })();
        // custom event for value change
        // check if change works for input
        $searchInput.on('keyup', triggerChange)
                .on('textChange', onChange);
    }
    window.onload = init;
    
    
// text modal
$(document)
            .ready(
                    function() {
                        $('#txtModal')
                                .on(
                                        'show.bs.modal',
                                        function(event) {
                                            var link = $(event.relatedTarget);

                                            var txtid = link.data('txtid');
                                            var txtname = link.data('txtname');
                                            var title = link.data('txttitle');
                                            var author = link.data('txtauthor');
                                            var date = link.data('txtdate');
                                            
                                            var header = "No author and title information provided."
                                            if (title != '' || author != '' || date != '') {
                                                header = '';
                                                if (author != null) {
                                                    header += author + ", ";
                                                }
                                                if (title != null) {
                                                    header += "<em>" + title + "</em> ";
                                                }
                                                if (date != null) {
                                                    header += "(" + date + ")";
                                                }
                                            }
                                            header += "<br><small>" + txtname + "</small>";
                                            $
                                                    .ajax({
                                                        type : "GET",
                                                        url : "${pageContext.servletContext.contextPath}/public/text/view?txtid="
                                                                + txtid,
                                                        contentType : "text/plain",
                                                        success : function(
                                                                details) {
                                                            $('.modal-title')
                                                                    .html(header);
                                                            $('.modal-body')
                                                                    .html(details);
                                                        },

                                                        error : function(xhr,
                                                                ajaxOptions) {
                                                            if (xhr.status == 404) {
                                                                $('.modal-body')
                                                                        .text(
                                                                                "Error while retrieving the text content.");
                                                            }
                                                        }
                                                    });

                                        });
                    });
</script>