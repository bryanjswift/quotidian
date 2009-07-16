<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="quotidian.model.Quote" %>
<%@ page import="quotidian.web.controller.QuoteController" %>
<%@ page import="scala.List" %>
<!DOCTYPE html>
<html>
	<head>
		<meta http-equiv="content-type" content="text/html; charset=utf-8">
		<title>Hello Quotes</title>
	</head>
	<body>
		<h1>Hello Quotes!</h1>
		<table>
			<tr>
				<th>Quote</th>
				<th>Source</th>
				<th>Context</th>
			</tr>
			<%
				List<Quote> quotes = QuoteController.getAll();
				Quote quote = null;
				for (int i = 0; i < quotes.length(); i++) {
					quote = quotes.apply(i);
			%>
				<tr>
					<td><%= quote.text() %></td>
					<td><%= quote.source() %></td>
					<td><%= quote.context() %></td>
				</tr>
			<%
				}
			%>
		</table>
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
