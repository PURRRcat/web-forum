<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ include file="/WEB-INF/views/layout/header.jspf" %>

<h2 class="mb-4">Личный кабинет</h2>

<div class="row">
    <div class="col-md-6">
        <div class="card shadow-sm mb-4">
            <div class="card-header"><h5 class="mb-0">Профиль</h5></div>
            <div class="card-body">
                <c:if test="${not empty pwSuccess}"><div class="alert alert-success">${pwSuccess}</div></c:if>
                <form method="post" action="${pageContext.request.contextPath}/user/cabinet">
                    <div class="mb-3">
                        <label class="form-label">Имя пользователя</label>
                        <input type="text" class="form-control" value="${profileUser.username}" disabled>
                    </div>
                    <div class="mb-3">
                        <label class="form-label">E-mail</label>
                        <input type="text" class="form-control" value="${profileUser.email}" disabled>
                    </div>
                    <div class="mb-3">
                        <label class="form-label">О себе</label>
                        <textarea name="about" class="form-control" rows="4">${profileUser.about}</textarea>
                    </div>
                    <div class="mb-3">
                        <label class="form-label">Путь к аватару (URL)</label>
                        <input type="text" name="avatarPath" class="form-control"
                               value="${profileUser.avatarPath}">
                    </div>
                    <button class="btn btn-primary">Сохранить</button>
                    <a href="${pageContext.request.contextPath}/user/${profileUser.id}"
                       class="btn btn-outline-secondary ms-2">Мой профиль</a>
                </form>
            </div>
        </div>
    </div>

    <div class="col-md-6">
        <div class="card shadow-sm">
            <div class="card-header"><h5 class="mb-0">Смена пароля</h5></div>
            <div class="card-body">
                <c:if test="${not empty pwError}"><div class="alert alert-danger">${pwError}</div></c:if>
                <form method="post" action="${pageContext.request.contextPath}/user/cabinet/password">
                    <div class="mb-3">
                        <label class="form-label">Текущий пароль</label>
                        <input type="password" name="oldPassword" class="form-control" required>
                    </div>
                    <div class="mb-3">
                        <label class="form-label">Новый пароль</label>
                        <input type="password" name="newPassword" class="form-control" required minlength="6">
                    </div>
                    <div class="mb-3">
                        <label class="form-label">Подтверждение</label>
                        <input type="password" name="confirmNew" class="form-control" required>
                    </div>
                    <button class="btn btn-warning">Изменить пароль</button>
                </form>
            </div>
        </div>
    </div>
</div>

<%@ include file="/WEB-INF/views/layout/footer.jspf" %>
