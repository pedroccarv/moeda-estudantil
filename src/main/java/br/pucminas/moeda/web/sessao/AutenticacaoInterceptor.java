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
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("{\"mensagem\":\"Entre para continuar.\"}");
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
        if (!path.startsWith("/api")) {
            return true;
        }
        if ("POST".equalsIgnoreCase(request.getMethod()) && path.equals("/api/sessao")) {
            return true;
        }
        if ("DELETE".equalsIgnoreCase(request.getMethod()) && path.equals("/api/sessao")) {
            return true;
        }
        if ("GET".equalsIgnoreCase(request.getMethod()) && path.equals("/api/instituicoes")) {
            return true;
        }
        return "POST".equalsIgnoreCase(request.getMethod())
                && (path.equals("/api/alunos") || path.equals("/api/empresas"));
    }
}
