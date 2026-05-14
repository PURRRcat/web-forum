<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ include file="/WEB-INF/views/layout/header.jspf" %>

<nav aria-label="breadcrumb" class="mb-3">
    <ol class="breadcrumb">
        <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/">Форум</a></li>
        <li class="breadcrumb-item">
            <a href="${pageContext.request.contextPath}/category/${category.id}">${category.title}</a>
        </li>
        <li class="breadcrumb-item active">Новая тема</li>
    </ol>
</nav>

<div class="row justify-content-center">
    <div class="col-md-8">
        <div class="card shadow-sm">
            <div class="card-header"><h4 class="mb-0">Создать новую тему</h4></div>
            <div class="card-body">
                <form method="post"
                      action="${pageContext.request.contextPath}/topic/new/${category.id}">
                    <div class="mb-3">
                        <label class="form-label">Заголовок темы</label>
                        <input type="text" name="title" class="form-control"
                               required maxlength="255" autofocus
                               placeholder="Введите заголовок темы…">
                    </div>
                    <div class="d-flex gap-2">
                        <button class="btn btn-primary">Создать тему</button>
                        <a href="${pageContext.request.contextPath}/category/${category.id}"
                           class="btn btn-outline-secondary">Отмена</a>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>

<%@ include file="/WEB-INF/views/layout/footer.jspf" %>
