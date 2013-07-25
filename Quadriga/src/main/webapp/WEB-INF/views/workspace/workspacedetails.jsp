<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>


<c:choose>
	<c:when test="${not empty workspacedetails.bitstreams}">
	<script>
	$(document).ready(function() {
		$('#dspaceSync').click(function dspaceSync() {
		    $( "#dialog-message" ).dialog({
		    		resizable: false,
		        	modal: true,
		        	width:'auto',
			      	open: function(event, ui) { $(".ui-dialog-titlebar-close").hide(); },
			      	buttons: {
			        	Ok: function() {
			          		$( this ).dialog( "close" );
			          		location.href='/quadriga/auth/workbench/workspace/${workspacedetails.id}/updatebitstreams';
			        			}
			      			}
			    });
			});
		});


	function submitClick()
	{
		if($('input:checkbox').is(':checked'))
			{
				$('#bitstream').submit();
			}
		else
			{
				$.alert("Please select atleast one file", "Oops !!!");
				return;
			}
		
	}
	</script>
	</c:when>
</c:choose>
<script>
	$(document).ready(function() {
		$("input[type=button]").button().click(function(event) {
			return;
		});
		
		$("input[type=submit]").button().click(function(event) {
			event.preventDefault();
		});
	});
	
	$(document).ready(function() {
		activeTable = $('.dataTable').dataTable({
			"bJQueryUI" : true,
			"sPaginationType" : "full_numbers",
			"bAutoWidth" : false,
			"iDisplayLength": 3
		});
		
		$("#login-box").dialog({
	        autoOpen: false,
	        modal: false,
	        resizable: false,
	        open: function(event, ui) { $(".ui-dialog-titlebar-close").hide(); },
	        buttons: {
	            Login: function () {
	                $(this).dialog("close");
	            }
	        }
	    });
	    
	    $('a.login-window').click(function() {
	        $( "#login-box" ).dialog( "open" );
	    });
	});
</script>
<table style="width:100%">
  <tr>
    <!-- Display workspace details -->
    <td style="width:90%">
    <h2>Workspace: ${workspacedetails.name}</h2>
<div>${workspacedetails.description}</div>
<hr>
<div class="user">Owned by: ${workspacedetails.owner.name}</div>
<hr>
<a href="${pageContext.servletContext.contextPath}/auth/workbench/workspace/updateworkspacedetails/${workspaceid}">
<input type="button" name="Edit" value="Edit"/>
</a> 

<!-- Dspace Login popup -->
<a href="#login-box" class="login-window">Login / Sign In</a>
<div id="login-box" class="login-popup" title="Dspace Authentication">
<form method="post" class="signin" action="#">
<fieldset class="textbox">
    <label class="username"><span>Dspace UserName:</span>
        <input id="username" name="username" value="" type="text" autocomplete="on" placeholder="Username" />
    </label>
    <label class="password"><span>Dspace Password: </span>
        <input id="password" name="password" value="" type="password" placeholder="Password" />
    </label>
    </fieldset>
</form>
</div>


<br><br>
<c:choose>
	<c:when test="${not empty workspacedetails.bitstreams}">
	<div id="dialog-message" title="Synchronization" style="display:none">
    Please check after few minutes. Data will be synced with Dspace.
</div>
	<font size="2"><input type="submit" value="Sync with Dspace" id="dspaceSync">
	<input type="submit" onclick="submitClick();" value="Delete Dspace Files" /></font> 
	<form id="bitstream" method="POST" action="/quadriga/auth/workbench/workspace/${workspacedetails.id}/deletebitstreams">
		<table  cellpadding="0" cellspacing="0" border="0"
			class="display dataTable" width="100%">
			<thead>
				<tr>
					<th></th>
					<th>Community</th>
					<th>Collection</th>
					<th>Item</th>
					<th>File</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach var="bitstream" items="${workspacedetails.bitstreams}">
					<tr>
						<td><input type="checkbox" class="checkbox" name="bitstreamids" value="${bitstream.id}"></td>
						<td><font size="1"><c:out value="${bitstream.communityName}"></c:out></font></td>
						<td><font size="1"><c:out value="${bitstream.collectionName}"></c:out></font></td>
						<td><font size="1"><c:out value="${bitstream.itemName}"></c:out></font></td>
						<td><font size="1"><c:out value="${bitstream.name}"></c:out></font></td>
					</tr>
				</c:forEach>
			</tbody>
			<tfoot>
				<tr>
					<th></th>
					<th>Community</th>
					<th>Collection</th>
					<th>Item</th>
					<th>File</th>
				</tr>
			</tfoot>
		</table>
		</form>
	</c:when>
	<c:otherwise>
					Workspace does not contain any files from dspace !
				</c:otherwise>
</c:choose>
    </td>
    <!-- Display collaborators -->
    <td style="width:10%"> 
    <c:if test="${not empty workspacedetails.collaborators}">
   <h3 class="major"><span>Collaborators</span></h3>
    <ul class="collaborators">
			<c:forEach var="wscollaborator" items="${workspacedetails.collaborators}">
				<li>
					<c:out value="${wscollaborator.userObj.userName}"></c:out>
				</li>
			</c:forEach>
		</ul>
    </c:if>
    </td>
  </tr>
</table>



