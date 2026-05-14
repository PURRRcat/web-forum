<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="my" tagdir="/WEB-INF/tags" %>
<%@ include file="/WEB-INF/views/layout/header.jspf" %>

<div class="row">
    <div class="col-md-4">
        <div class="card shadow-sm mb-4">
            <div class="card-body text-center">
                <div class="display-4 mb-2">&#128100;</div>
                <h4>${profileUser.username}</h4>
                <span class="badge bg-${profileUser.role == 'admin' ? 'danger' :
                                         profileUser.role == 'moderator' ? 'warning text-dark' : 'secondary'}">
                    ${profileUser.role}
                </span>
                <hr>
                <dl class="text-start mb-0">
                    <dt>Зарегистрирован</dt>
                    <dd><my:dt value="${profileUser.registeredAt}" pattern="dd.MM.yyyy"/></dd>
                    <dt>Сообщений</dt>
                    <dd>${postCount}</dd>
                    <dt>О себе</dt>
                    <dd>${not empty profileUser.about ? profileUser.about : '—'}</dd>
                </dl>
                <c:if test="${sessionScope.userId == profileUser.id}">
                    <a href="${pageContext.request.contextPath}/user/cabinet"
                       class="btn btn-outline-primary btn-sm mt-2 w-100">Редактировать профиль</a>
                </c:if>
            </div>
        </div>
    </div>

    <div class="col-md-8">
        <h5 class="mb-3">Темы пользователя</h5>
        <c:choose>
            <c:when test="${empty userTopics}">
                <div class="alert alert-info">Пользователь ещё не создавал тем.</div>
            </c:when>
            <c:otherwise>
                <div class="list-group">
                    <c:forEach var="t" items="${userTopics}">
                        <a href="${pageContext.request.contextPath}/topic/${t.id}"
                           class="list-group-item list-group-item-action d-flex justify-content-between">
                            <span>${t.title}</span>
                            <small class="text-muted">
                                <my:dt value="${t.createdAt}" pattern="dd.MM.yyyy"/>
                                &nbsp;·&nbsp;${t.category.title}
                            </small>
                        </a>
                    </c:forEach>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</div>

<%@ include file="/WEB-INF/views/layout/footer.jspf" %>
