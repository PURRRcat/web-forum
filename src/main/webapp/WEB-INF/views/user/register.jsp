<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ include file="/WEB-INF/views/layout/header.jspf" %>

<div class="row justify-content-center">
    <div class="col-md-6">
        <div class="card shadow-sm">
            <div class="card-header"><h4 class="mb-0">Регистрация</h4></div>
            <div class="card-body">
                <c:if test="${not empty error}">
                    <div class="alert alert-danger">${error}</div>
                </c:if>
                <form method="post" action="${pageContext.request.contextPath}/user/register">
                    <div class="mb-3">
                        <label class="form-label">Имя пользователя</label>
                        <input type="text" name="username" class="form-control"
                               value="${username}" required maxlength="50" autofocus>
                    </div>
                    <div class="mb-3">
                        <label class="form-label">E-mail</label>
                        <input type="email" name="email" class="form-control"
                               value="${email}" required maxlength="100">
                    </div>
                    <div class="mb-3">
                        <label class="form-label">Пароль</label>
                        <input type="password" name="password" class="form-control" required minlength="6">
                    </div>
                    <div class="mb-3">
                        <label class="form-label">Подтверждение пароля</label>
                        <input type="password" name="confirmPassword" class="form-control" required>
                    </div>
                    <button class="btn btn-primary w-100">Зарегистрироваться</button>
                </form>
            </div>
            <div class="card-footer text-center">
                <small>Уже есть аккаунт?
                    <a href="${pageContext.request.contextPath}/user/login">Войти</a>
                </small>
            </div>
        </div>
    </div>
</div>

<%@ include file="/WEB-INF/views/layout/footer.jspf" %>
