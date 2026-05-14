<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ include file="/WEB-INF/views/layout/header.jspf" %>

<div class="row justify-content-center">
    <div class="col-md-7">
        <div class="card shadow-sm">
            <div class="card-header d-flex align-items-center gap-2">
                <span class="fs-2">&#128100;</span>
                <h4 class="mb-0">${profileUser.username}</h4>
                <span class="badge bg-${profileUser.role == 'admin' ? 'danger' :
                                        profileUser.role == 'moderator' ? 'warning text-dark' : 'secondary'} ms-auto">
                    ${profileUser.role}
                </span>
            </div>
            <div class="card-body">
                <dl class="row mb-0">
                    <dt class="col-sm-4">E-mail</dt>
                    <dd class="col-sm-8">${profileUser.email}</dd>

                    <dt class="col-sm-4">Зарегистрирован</dt>
                    <dd class="col-sm-8">
                        <fmt:formatDate value="${profileUser.registeredAt}" pattern="dd.MM.yyyy"/>
                    </dd>

                    <dt class="col-sm-4">О себе</dt>
                    <dd class="col-sm-8">
                        <c:choose>
                            <c:when test="${not empty profileUser.about}">${profileUser.about}</c:when>
                            <c:otherwise><em class="text-muted">не указано</em></c:otherwise>
                        </c:choose>
                    </dd>
                </dl>
            </div>
        </div>
    </div>
</div>

<%@ include file="/WEB-INF/views/layout/footer.jspf" %>
