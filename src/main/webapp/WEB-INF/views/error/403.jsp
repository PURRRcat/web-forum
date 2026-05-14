<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/layout/header.jspf" %>

<div class="text-center py-5">
    <div class="display-1 text-danger fw-bold">403</div>
    <h2 class="mt-3">Доступ запрещён</h2>
    <p class="text-muted">У вас нет прав для выполнения этого действия.</p>
    <a href="${pageContext.request.contextPath}/" class="btn btn-primary mt-2">
        На главную
    </a>
</div>

<%@ include file="/WEB-INF/views/layout/footer.jspf" %>
