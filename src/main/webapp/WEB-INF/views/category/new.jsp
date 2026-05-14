<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ include file="/WEB-INF/views/layout/header.jspf" %>

<div class="row justify-content-center">
    <div class="col-md-7">
        <div class="card shadow-sm">
            <div class="card-header"><h4 class="mb-0">Создать раздел</h4></div>
            <div class="card-body">
                <form method="post"
                      action="${pageContext.request.contextPath}/category/new">
                    <div class="mb-3">
                        <label class="form-label">Название раздела</label>
                        <input type="text" name="title" class="form-control"
                               required maxlength="100" autofocus
                               placeholder="Например: Программирование">
                    </div>
                    <div class="mb-3">
                        <label class="form-label">Описание</label>
                        <textarea name="description" class="form-control" rows="3"
                                  maxlength="500"
                                  placeholder="Краткое описание раздела…"></textarea>
                    </div>
                    <div class="d-flex gap-2">
                        <button class="btn btn-success">Создать раздел</button>
                        <a href="${pageContext.request.contextPath}/"
                           class="btn btn-outline-secondary">Отмена</a>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>

<%@ include file="/WEB-INF/views/layout/footer.jspf" %>
