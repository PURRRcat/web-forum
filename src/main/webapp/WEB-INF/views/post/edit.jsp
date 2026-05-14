<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ include file="/WEB-INF/views/layout/header.jspf" %>

<div class="row justify-content-center">
    <div class="col-md-8">
        <div class="card shadow-sm">
            <div class="card-header"><h4 class="mb-0">Редактировать сообщение</h4></div>
            <div class="card-body">
                <form method="post"
                      action="${pageContext.request.contextPath}/post/edit/${post.id}">
                    <div class="mb-3">
                        <label class="form-label">Текст сообщения</label>
                        <textarea name="content" class="form-control" rows="8"
                                  required>${post.content}</textarea>
                    </div>
                    <div class="d-flex gap-2">
                        <button class="btn btn-primary">Сохранить</button>
                        <a href="${pageContext.request.contextPath}/topic/${post.topic.id}"
                           class="btn btn-outline-secondary">Отмена</a>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>

<%@ include file="/WEB-INF/views/layout/footer.jspf" %>
