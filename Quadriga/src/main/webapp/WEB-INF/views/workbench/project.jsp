<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<script>
$(document).ready(function() {
	$("input[type=button]").button().click(function(event) {
            return;
	});
});

</script>

<h2>Project: ${project.name}</h2>
<div>${project.description}</div>
<hr>
<div class="user">Owned by: ${project.owner.name}</div>
<hr>
<a href="modifyproject/${project.internalid}">
<input type="button" name="Edit" value="Edit"/>
</a>






	




