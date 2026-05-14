<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ include file="/WEB-INF/views/layout/header.jspf" %>

<nav aria-label="breadcrumb" class="mb-3">
    <ol class="breadcrumb">
        <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/">Форум</a></li>
        <li class="breadcrumb-item active">${category.title}</li>
    </ol>
</nav>

<div class="d-flex justify-content-between align-items-center mb-3">
    <div>
        <h1 class="mb-1">${category.title}</h1>
        <p class="text-muted mb-0">${category.description}</p>
    </div>
    <c:if test="${not empty sessionScope.userId}">
        <a href="${pageContext.request.contextPath}/topic/new/${category.id}"
           class="btn btn-primary">+ Новая тема</a>
    </c:if>
</div>

<c:choose>
    <c:when test="${empty topics}">
        <div class="alert alert-info">В этом разделе пока нет тем.</div>
    </c:when>
    <c:otherwise>
        <div class="list-group">
            <c:forEach var="t" items="${topics}">
                <a href="${pageContext.request.contextPath}/topic/${t.id}"
                   class="list-group-item list-group-item-action py-3">
                    <div class="d-flex justify-content-between">
                        <div>
                            <h6 class="mb-1">${t.title}</h6>
                            <small class="text-muted">
                                Автор: <strong>${t.user.username}</strong>
                            </small>
                        </div>
                        <small class="text-muted text-nowrap">
                            <fmt:formatDate value="${t.createdAt}" pattern="dd.MM.yyyy"/>
                        </small>
                    </div>
                </a>
            </c:forEach>
        </div>
    </c:otherwise>
</c:choose>

<%@ include file="/WEB-INF/views/layout/footer.jspf" %>
