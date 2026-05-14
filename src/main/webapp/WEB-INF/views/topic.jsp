<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ include file="/WEB-INF/views/layout/header.jspf" %>

<nav aria-label="breadcrumb" class="mb-3">
    <ol class="breadcrumb">
        <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/">Форум</a></li>
        <li class="breadcrumb-item">
            <a href="${pageContext.request.contextPath}/category/${topic.category.id}">${topic.category.title}</a>
        </li>
        <li class="breadcrumb-item active">${topic.title}</li>
    </ol>
</nav>

<h1 class="mb-4">${topic.title}</h1>

<!-- Список сообщений -->
<c:choose>
    <c:when test="${empty posts}">
        <div class="alert alert-info">Сообщений пока нет. Будьте первым!</div>
    </c:when>
    <c:otherwise>
        <c:forEach var="post" items="${posts}">
            <div class="card post-card shadow-sm mb-3">
                <div class="card-body">
                    <div class="d-flex justify-content-between mb-2">
                        <strong>
                            <a href="${pageContext.request.contextPath}/user/${post.user.id}"
                               class="text-decoration-none">${post.user.username}</a>
                        </strong>
                        <small class="text-muted">
                            <fmt:formatDate value="${post.createdAt}" pattern="dd.MM.yyyy HH:mm"/>
                        </small>
                    </div>
                    <p class="card-text mb-2" style="white-space:pre-wrap">${post.content}</p>
                    <c:if test="${sessionScope.userId == post.user.id or sessionScope.role == 'admin'}">
                        <div class="d-flex gap-2">
                            <a href="${pageContext.request.contextPath}/post/edit/${post.id}"
                               class="btn btn-outline-secondary btn-sm">Редактировать</a>
                            <form method="post"
                                  action="${pageContext.request.contextPath}/post/delete/${post.id}"
                                  onsubmit="return confirm('Удалить сообщение?')">
                                <button class="btn btn-outline-danger btn-sm">Удалить</button>
                            </form>
                        </div>
                    </c:if>
                </div>
            </div>
        </c:forEach>
    </c:otherwise>
</c:choose>

<!-- Форма добавления сообщения -->
<c:choose>
    <c:when test="${not empty sessionScope.userId}">
        <div class="card shadow-sm mt-4">
            <div class="card-header"><h5 class="mb-0">Добавить сообщение</h5></div>
            <div class="card-body">
                <form method="post"
                      action="${pageContext.request.contextPath}/post/new/${topic.id}">
                    <div class="mb-3">
                        <textarea name="content" class="form-control" rows="5"
                                  placeholder="Текст сообщения…" required></textarea>
                    </div>
                    <button class="btn btn-primary">Отправить</button>
                </form>
            </div>
        </div>
    </c:when>
    <c:otherwise>
        <div class="alert alert-secondary mt-4">
            <a href="${pageContext.request.contextPath}/user/login">Войдите</a>, чтобы оставить сообщение.
        </div>
    </c:otherwise>
</c:choose>

<%@ include file="/WEB-INF/views/layout/footer.jspf" %>
