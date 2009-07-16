<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
	<head>
		<meta http-equiv="content-type" content="text/html; charset=utf-8">
		<title>Hello Quotes</title>
	</head>
	<body>
		<h1>Hello Quotes!</h1>
		<p><c:out value="${test}" /></p>
		<form action="/quote" method="post">
			<label for="text">Text</label>
			<textarea name="text" id="text"></textarea>
			<label for="source">Source</label>
			<input type="text" name="source" id="source" />
			<label for="context">Context</label>
			<input type="text" name="context" id="context" />
			<input type="submit" name="submit" value="Add Quote" />
		</form>
		<table>
			<tr>
				<th>Quote</th>
				<th>Source</th>
				<th>Context</th>
			</tr>
			<c:forEach items="${quotes}" var="quote" varStatus="status">
				<tr>
					<td><c:out value="${quote.text}" /></td>
					<td><c:out value="${quote.soruce}" /></td>
					<td><c:out value="${quote.context}" /></td>
				</tr>
			</c:forEach>
		</table>
	</body>
</html>
