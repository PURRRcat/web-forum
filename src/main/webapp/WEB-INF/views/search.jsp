<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="my" tagdir="/WEB-INF/tags" %>
<%@ include file="/WEB-INF/views/layout/header.jspf" %>

<h1 class="mb-4">Поиск</h1>

<form method="get" action="${pageContext.request.contextPath}/search" class="mb-4">
    <div class="input-group">
        <input type="text" name="q" class="form-control form-control-lg"
               value="${q}" placeholder="Введите ключевые слова…" autofocus>
        <button class="btn btn-primary px-4">Найти</button>
    </div>
</form>

<c:if test="${not empty q}">
    <h5 class="mt-4">Темы</h5>
    <c:choose>
        <c:when test="${empty topicResults}">
            <p class="text-muted">Тем не найдено.</p>
        </c:when>
        <c:otherwise>
            <div class="list-group mb-4">
                <c:forEach var="t" items="${topicResults}">
                    <a href="${pageContext.request.contextPath}/topic/${t.id}"
                       class="list-group-item list-group-item-action d-flex justify-content-between">
                        <span>${t.title}</span>
                        <small class="text-muted">${t.category.title}</small>
                    </a>
                </c:forEach>
            </div>
        </c:otherwise>
    </c:choose>

    <h5>Сообщения</h5>
    <c:choose>
        <c:when test="${empty postResults}">
            <p class="text-muted">Сообщений не найдено.</p>
        </c:when>
        <c:otherwise>
            <c:forEach var="p" items="${postResults}">
                <div class="card post-card shadow-sm mb-2">
                    <div class="card-body">
                        <div class="d-flex justify-content-between mb-1">
                            <a href="${pageContext.request.contextPath}/topic/${p.topic.id}"
                               class="fw-bold text-decoration-none">${p.topic.title}</a>
                            <small class="text-muted">
                                ${p.user.username} &nbsp;·&nbsp;
                                <my:dt value="${p.createdAt}" pattern="dd.MM.yyyy"/>
                            </small>
                        </div>
                        <p class="mb-0 text-truncate">${p.content}</p>
                    </div>
                </div>
            </c:forEach>
        </c:otherwise>
    </c:choose>
</c:if>

<%@ include file="/WEB-INF/views/layout/footer.jspf" %>
