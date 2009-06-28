<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="quotidian.model.Quote" %>
<%@ page import="quotidian.web.controller.QuoteController" %>
<%@ page import="scala.List" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01//EN">
<html>
	<head>
		<meta http-equiv="content-type" content="text/html; charset=utf-8">
		<title>Hello Quotes</title>
	</head>
	<body>
		<h1>Hello Quotes!</h1>
		<form action="/quote" method="post">
			<label for="text">Text</label>
			<textarea name="text" id="text"></textarea>
			<label for="source">Source</label>
			<input type="text" name="source" id="source" />
			<label for="context">Context</label>
			<input type="text" name="context" id="context" />
			<input type="submit" name="submit" value="Add Quote" />
		</form>
	</body>
</html>
