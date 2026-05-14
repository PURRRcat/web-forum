<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ include file="/WEB-INF/views/layout/header.jspf" %>

<h1 class="mb-4">Разделы форума</h1>

<c:choose>
    <c:when test="${empty categories}">
        <div class="alert alert-info">Разделов пока нет.</div>
    </c:when>
    <c:otherwise>
        <div class="list-group">
            <c:forEach var="cat" items="${categories}">
                <a href="${pageContext.request.contextPath}/category/${cat.id}"
                   class="list-group-item list-group-item-action py-3">
                    <div class="d-flex justify-content-between align-items-start">
                        <div>
                            <h5 class="mb-1">${cat.title}</h5>
                            <p class="mb-0 text-muted small">
                                <c:choose>
                                    <c:when test="${not empty cat.description}">${cat.description}</c:when>
                                    <c:otherwise><em>Описание отсутствует</em></c:otherwise>
                                </c:choose>
                            </p>
                        </div>
                        <span class="badge bg-secondary rounded-pill">&#128193;</span>
                    </div>
                </a>
            </c:forEach>
        </div>
    </c:otherwise>
</c:choose>

<%@ include file="/WEB-INF/views/layout/footer.jspf" %>
