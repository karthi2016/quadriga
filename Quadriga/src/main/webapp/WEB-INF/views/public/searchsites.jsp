<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>

<script>
	$(document).ready(function() {
		$('#example1').DataTable({
	        "searching": false
	    });
	});
	
	function handle(e) {
		if (e.keyCode === 13) {
			document.getElementById("sites1").submit();
		}
		return false;
	}
	
	function callMethod() {
		document.getElementById("sites1").submit();
	}
</script>

<form name='searchsites' id="sites1"
	action="${pageContext.servletContext.contextPath}/sites/searchTerm"
	method='POST'>
	<table width="100%">
		<tr>
			<td style="width: 85%"><input type="text" class="form-control"
				placeholder="Search Public Sites" name="searchTerm" tabindex="1"
				onkeypress="handle(event)" value="${searchTerm}"
				autocapitalize="off"></td>
			<td style="width: 2%"></td>
			<td style="width: 25%"><input type="submit" value="Search"
				style="width: 100%; height: 115%; font-weight: bold;" tabindex="2"
				onclick="callMethod()"></td>
		</tr>
	</table>
	</br>
	<table id="example1" class="table table-striped table-bordered"
		cellspacing="0" width="100%">
		<thead>
			<tr>
				<th>Search Results By Project Name</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach var="sitesList" items="${projectList}">
				<tr>
					<td>
						<div
							style="padding-top: 20px; margin-top: 20px; margin-bottom: 10px; border-top: 1px solid #eee;">
							<p>
								<a
									style="color: #4078c0; text-decoration: none; font-weight: bold; line-height: 1.2;"
									href="${pageContext.servletContext.contextPath}/sites/${sitesList.unixName}">${sitesList.projectName}
								</a> <br> <span
									style="font-size: 12px !important; color: #767676 !important"><c:out
										value="Lead by ${sitesList.owner.name}"></c:out> </span>
							</p>
							<div>
								<c:out value="${sitesList.description}"></c:out>
							</div>
						</div>
					</td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
</form>