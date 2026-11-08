package br.pucminas.moeda.web.sessao;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AutenticacaoInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        String path = caminho(request);
        if (publico(request, path)) {
            return true;
        }
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute(SessaoUsuario.CHAVE) instanceof SessaoUsuario) {
            return true;
        }
        response.sendRedirect(request.getContextPath() + "/login");
        return false;
    }

    private String caminho(HttpServletRequest request) {
        String uri = request.getRequestURI();
        String context = request.getContextPath();
        if (context != null && !context.isEmpty() && uri.startsWith(context)) {
            uri = uri.substring(context.length());
        }
        int parametro = uri.indexOf(';');
        if (parametro >= 0) {
            uri = uri.substring(0, parametro);
        }
        return uri;
    }

    private boolean publico(HttpServletRequest request, String path) {
        if (path.equals("/")
                || path.equals("/login")
                || path.equals("/logout")
                || path.equals("/error")
                || path.equals("/favicon.ico")
                || path.startsWith("/css/")
                || path.startsWith("/uploads/")) {
            return true;
        }
        if (path.equals("/alunos/novo") || path.equals("/empresas/nova")) {
            return true;
        }
        if ("POST".equalsIgnoreCase(request.getMethod())
                && (path.equals("/alunos/salvar") || path.equals("/empresas/salvar"))) {
            String id = request.getParameter("id");
            return id == null || id.isBlank();
        }
        return false;
    }
}
