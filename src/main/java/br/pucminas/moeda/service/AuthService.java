package br.pucminas.moeda.service;

import br.pucminas.moeda.domain.Aluno;
import br.pucminas.moeda.domain.EmpresaParceira;
import br.pucminas.moeda.domain.Professor;
import br.pucminas.moeda.domain.Usuario;
import br.pucminas.moeda.repository.UsuarioRepository;
import br.pucminas.moeda.web.sessao.SessaoUsuario;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;

    public AuthService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional(readOnly = true)
    public SessaoUsuario autenticar(String email, String senha) {
        if (email == null || email.isBlank() || senha == null || senha.isBlank()) {
            throw new IllegalArgumentException("Informe e-mail e senha");
        }
        String normalizado = email.trim().toLowerCase();
        Usuario usuario = usuarioRepository.findByEmail(normalizado)
                .orElseThrow(() -> new IllegalArgumentException("E-mail ou senha inválidos"));
        if (!usuario.autenticar(senha)) {
            throw new IllegalArgumentException("E-mail ou senha inválidos");
        }
        return new SessaoUsuario(usuario.getId(), usuario.getNome(), usuario.getEmail(), papelDe(usuario));
    }

    @Transactional(readOnly = true)
    public void validarEmailDisponivel(String email, Long proprioId) {
        usuarioRepository.findByEmail(email).ifPresent(existente -> {
            if (proprioId == null || !proprioId.equals(existente.getId())) {
                throw new IllegalArgumentException("E-mail já cadastrado");
            }
        });
    }

    private SessaoUsuario.Papel papelDe(Usuario usuario) {
        if (usuario instanceof Aluno) {
            return SessaoUsuario.Papel.ALUNO;
        }
        if (usuario instanceof Professor) {
            return SessaoUsuario.Papel.PROFESSOR;
        }
        if (usuario instanceof EmpresaParceira) {
            return SessaoUsuario.Papel.EMPRESA;
        }
        throw new IllegalStateException("Tipo de usuário desconhecido");
    }
}
