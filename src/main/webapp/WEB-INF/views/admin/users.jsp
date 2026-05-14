<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ include file="/WEB-INF/views/layout/header.jspf" %>

<h1 class="mb-4">&#128100; Управление пользователями</h1>

<table class="table table-hover table-bordered bg-white shadow-sm">
    <thead class="table-dark">
        <tr>
            <th>#</th>
            <th>Имя пользователя</th>
            <th>E-mail</th>
            <th>Роль</th>
            <th>Дата регистрации</th>
            <th>Действия</th>
        </tr>
    </thead>
    <tbody>
        <c:forEach var="u" items="${users}">
            <tr>
                <td>${u.id}</td>
                <td>
                    <a href="${pageContext.request.contextPath}/user/${u.id}">${u.username}</a>
                </td>
                <td>${u.email}</td>
                <td>
                    <span class="badge bg-${u.role == 'admin' ? 'danger' :
                                            u.role == 'moderator' ? 'warning text-dark' : 'secondary'}">
                        ${u.role}
                    </span>
                </td>
                <td><fmt:formatDate value="${u.registeredAt}" pattern="dd.MM.yyyy"/></td>
                <td>
                    <c:if test="${u.id != sessionScope.userId}">
                        <form method="post"
                              action="${pageContext.request.contextPath}/admin/users/${u.id}/delete"
                              onsubmit="return confirm('Удалить пользователя ${u.username}?')">
                            <button class="btn btn-danger btn-sm">Удалить</button>
                        </form>
                    </c:if>
                </td>
            </tr>
        </c:forEach>
    </tbody>
</table>

<%@ include file="/WEB-INF/views/layout/footer.jspf" %>
