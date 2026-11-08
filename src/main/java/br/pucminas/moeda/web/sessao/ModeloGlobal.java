package br.pucminas.moeda.web.sessao;

import jakarta.servlet.http.HttpSession;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
public class ModeloGlobal {

    @ModelAttribute
    public void sessao(HttpSession session, Model model) {
        Object valor = session.getAttribute(SessaoUsuario.CHAVE);
        if (valor instanceof SessaoUsuario sessao) {
            model.addAttribute("sessao", sessao);
        }
    }

    public static SessaoUsuario atual(HttpSession session) {
        Object valor = session.getAttribute(SessaoUsuario.CHAVE);
        if (valor instanceof SessaoUsuario sessao) {
            return sessao;
        }
        return null;
    }

    public static String exigir(HttpSession session, SessaoUsuario.Papel papel, RedirectAttributes redirect) {
        SessaoUsuario sessao = atual(session);
        if (sessao == null) {
            return "redirect:/login";
        }
        if (sessao.getPapel() != papel) {
            redirect.addFlashAttribute("erro", "Essa área é de outro perfil.");
            return "redirect:" + sessao.getDestino();
        }
        return null;
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public String arquivoGrande(RedirectAttributes redirect) {
        redirect.addFlashAttribute("erro", "A foto deve ter no máximo 5 MB.");
        return "redirect:/empresa/vantagens/nova";
    }
}
