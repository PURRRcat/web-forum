<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="my" tagdir="/WEB-INF/tags" %>
<%@ include file="/WEB-INF/views/layout/header.jspf" %>

<h1 class="mb-4">Разделы форума</h1>

<c:choose>
    <c:when test="${empty stats}">
        <div class="alert alert-info">Разделов пока нет.</div>
    </c:when>
    <c:otherwise>
        <div class="table-responsive">
            <table class="table table-hover bg-white shadow-sm rounded">
                <thead class="table-dark">
                    <tr>
                        <th>Раздел</th>
                        <th class="text-center">Тем</th>
                        <th class="text-center">Сообщений</th>
                        <th>Последнее сообщение</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="s" items="${stats}">
                        <tr>
                            <td>
                                <a href="${pageContext.request.contextPath}/category/${s.category.id}"
                                   class="fw-bold text-decoration-none">${s.category.title}</a>
                                <div class="text-muted small">${s.category.description}</div>
                            </td>
                            <td class="text-center">${s.topicCount}</td>
                            <td class="text-center">${s.postCount}</td>
                            <td class="small">
                                <c:choose>
                                    <c:when test="${not empty s.lastAuthor}">
                                        <my:dt value="${s.lastPostAt}" pattern="dd.MM.yyyy HH:mm"/>
                                        <br><span class="text-muted">${s.lastAuthor}</span>
                                    </c:when>
                                    <c:otherwise><span class="text-muted">—</span></c:otherwise>
                                </c:choose>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>
    </c:otherwise>
</c:choose>

<%@ include file="/WEB-INF/views/layout/footer.jspf" %>
