<%@ tag language="java" pageEncoding="UTF-8" %>
<%@ attribute name="value" required="true" type="java.lang.Object" %>
<%@ attribute name="pattern" required="false" type="java.lang.String" %>
<%
    Object v = jspContext.getAttribute("value");
    String p = (String) jspContext.getAttribute("pattern");
    if (p == null || p.isEmpty()) p = "dd.MM.yyyy HH:mm";
    if (v instanceof java.time.LocalDateTime) {
        out.print(((java.time.LocalDateTime) v)
            .format(java.time.format.DateTimeFormatter.ofPattern(p)));
    } else if (v instanceof java.time.LocalDate) {
        out.print(((java.time.LocalDate) v)
            .format(java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy")));
    } else if (v != null) {
        out.print(v);
    }
%>
