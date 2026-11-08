package br.pucminas.moeda.config;

import br.pucminas.moeda.domain.InstituicaoEnsino;
import br.pucminas.moeda.domain.Professor;
import br.pucminas.moeda.repository.InstituicaoEnsinoRepository;
import br.pucminas.moeda.repository.ProfessorRepository;
import br.pucminas.moeda.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {

    public static final String PROFESSOR_EMAIL = "ana.souza@pucminas.br";
    public static final String PROFESSOR_SENHA = "prof123";
    public static final String PROFESSOR_2_EMAIL = "carlos.lima@ufmg.br";

    private final InstituicaoEnsinoRepository instituicaoRepository;
    private final ProfessorRepository professorRepository;
    private final UsuarioRepository usuarioRepository;

    public DataLoader(InstituicaoEnsinoRepository instituicaoRepository,
                      ProfessorRepository professorRepository,
                      UsuarioRepository usuarioRepository) {
        this.instituicaoRepository = instituicaoRepository;
        this.professorRepository = professorRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public void run(String... args) {
        criarInstituicao("PUC Minas");
        criarInstituicao("UFMG");
        criarInstituicao("CEFET-MG");
        criarInstituicao("UNA");

        criarProfessor("Ana Souza", PROFESSOR_EMAIL, PROFESSOR_SENHA,
                "111.111.111-11", "Computação", "PUC Minas");
        criarProfessor("Carlos Lima", PROFESSOR_2_EMAIL, PROFESSOR_SENHA,
                "222.222.222-22", "Matemática", "UFMG");
    }

    private void criarInstituicao(String nome) {
        if (!instituicaoRepository.existsByNome(nome)) {
            instituicaoRepository.save(new InstituicaoEnsino(nome));
        }
    }

    private void criarProfessor(String nome, String email, String senha, String cpf,
                                String departamento, String instituicaoNome) {
        if (usuarioRepository.existsByEmail(email)) {
            return;
        }
        InstituicaoEnsino instituicao = instituicaoRepository.findByNome(instituicaoNome)
                .orElseThrow(() -> new IllegalStateException("Instituição não encontrada: " + instituicaoNome));
        professorRepository.save(new Professor(nome, email, senha, cpf, departamento, instituicao));
    }
}
