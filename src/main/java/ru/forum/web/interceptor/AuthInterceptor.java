package ru.forum.web.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Перенаправляет неаутентифицированных пользователей на страницу входа
 * при попытке обратиться к защищённым ресурсам (/topic/new/**, /post/**).
 * Пути задаются в spring-mvc.xml через &lt;mvc:interceptor&gt;.
 */
public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request,
                              HttpServletResponse response,
                              Object handler) throws Exception {
        if (request.getSession().getAttribute("userId") == null) {
            response.sendRedirect(request.getContextPath() + "/user/login");
            return false;
        }
        return true;
    }
}
