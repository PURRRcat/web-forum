<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="my" tagdir="/WEB-INF/tags" %>
<%@ include file="/WEB-INF/views/layout/header.jspf" %>

<h1 class="mb-4">Непрочитанные темы</h1>

<c:choose>
    <c:when test="${empty topics}">
        <div class="alert alert-success">Все темы прочитаны!</div>
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
                                ${t.category.title} &nbsp;·&nbsp; ${t.user.username}
                            </small>
                        </div>
                        <small class="text-muted text-nowrap">
                            <my:dt value="${t.createdAt}" pattern="dd.MM.yyyy"/>
                        </small>
                    </div>
                </a>
            </c:forEach>
        </div>
    </c:otherwise>
</c:choose>

<%@ include file="/WEB-INF/views/layout/footer.jspf" %>
