package br.pucminas.moeda.web.dto;

import br.pucminas.moeda.domain.Aluno;
import br.pucminas.moeda.domain.EmpresaParceira;
import br.pucminas.moeda.domain.InstituicaoEnsino;
import br.pucminas.moeda.domain.NotificacaoEmail;
import br.pucminas.moeda.domain.Professor;
import br.pucminas.moeda.domain.TransacaoEnvio;
import br.pucminas.moeda.domain.TransacaoResgate;
import br.pucminas.moeda.domain.Vantagem;
import br.pucminas.moeda.web.sessao.SessaoUsuario;

import java.time.LocalDateTime;
import java.util.List;

public final class Contratos {

    private Contratos() {
    }

    public record Mensagem(String mensagem, String aviso) {
        public static Mensagem de(String mensagem) {
            return new Mensagem(mensagem, null);
        }

        public static Mensagem de(String mensagem, String aviso) {
            return new Mensagem(mensagem, aviso);
        }
    }

    public record Sessao(Long id, String nome, String email, String papel) {
        public static Sessao de(SessaoUsuario sessao) {
            return new Sessao(sessao.getId(), sessao.getNome(), sessao.getEmail(), sessao.getPapel().name());
        }
    }

    public record Login(String email, String senha) {
    }

    public record Instituicao(Long id, String nome) {
        public static Instituicao de(InstituicaoEnsino instituicao) {
            return new Instituicao(instituicao.getId(), instituicao.getNome());
        }
    }

    public record AlunoPedido(String nome, String email, String senha, String cpf, String rg,
                              String endereco, String curso, Long instituicaoId) {
    }

    public record AlunoDetalhe(Long id, String nome, String email, String cpf, String rg, String endereco,
                               String curso, int saldoMoedas, Long instituicaoId, String instituicaoNome) {
        public static AlunoDetalhe de(Aluno aluno) {
            return new AlunoDetalhe(
                    aluno.getId(), aluno.getNome(), aluno.getEmail(), aluno.getCpf(), aluno.getRg(),
                    aluno.getEndereco(), aluno.getCurso(), saldo(aluno.getSaldoMoedas()),
                    aluno.getInstituicao().getId(), aluno.getInstituicao().getNome());
        }
    }

    public record AlunoOpcao(Long id, String nome, String curso, String instituicaoNome) {
        public static AlunoOpcao de(Aluno aluno) {
            return new AlunoOpcao(aluno.getId(), aluno.getNome(), aluno.getCurso(), aluno.getInstituicao().getNome());
        }
    }

    public record EmpresaPedido(String nome, String email, String senha, String cnpj) {
    }

    public record EmpresaDetalhe(Long id, String nome, String email, String cnpj) {
        public static EmpresaDetalhe de(EmpresaParceira empresa) {
            return new EmpresaDetalhe(empresa.getId(), empresa.getNome(), empresa.getEmail(), empresa.getCnpj());
        }
    }

    public record ProfessorDetalhe(Long id, String nome, String email, String departamento,
                                   String instituicaoNome, int saldoMoedas, String ultimoPeriodoCreditado,
                                   String aviso) {
        public static ProfessorDetalhe de(Professor professor, String aviso) {
            return new ProfessorDetalhe(
                    professor.getId(), professor.getNome(), professor.getEmail(), professor.getDepartamento(),
                    professor.getInstituicao().getNome(), saldo(professor.getSaldoMoedas()),
                    professor.getUltimoPeriodoCreditado(), aviso);
        }
    }

    public record VantagemItem(Long id, String descricao, String fotoUrl, int custoEmMoedas,
                               Long empresaId, String empresaNome) {
        public static VantagemItem de(Vantagem vantagem) {
            return new VantagemItem(
                    vantagem.getId(), vantagem.getDescricao(), vantagem.getFotoUrl(),
                    vantagem.getCustoEmMoedas() == null ? 0 : vantagem.getCustoEmMoedas(),
                    vantagem.getEmpresa().getId(), vantagem.getEmpresa().getNome());
        }
    }

    public record EmailItem(String assunto, LocalDateTime dataEnvio, String statusDescricao,
                            LocalDateTime enviadoEm, String corpo) {
        public static EmailItem de(NotificacaoEmail email) {
            return new EmailItem(
                    email.getAssunto(), email.getDataEnvio(), email.getStatusDescricao(),
                    email.getEnviadoEm(), email.getCorpo());
        }
    }

    public record Recebimento(LocalDateTime dataHora, int valor, String professorNome, String motivo) {
        public static Recebimento de(TransacaoEnvio envio) {
            return new Recebimento(envio.getDataHora(), saldo(envio.getValor()), envio.getProfessor().getNome(), envio.getMotivo());
        }
    }

    public record EnvioLinha(LocalDateTime dataHora, int valor, String alunoNome, String motivo) {
        public static EnvioLinha de(TransacaoEnvio envio) {
            return new EnvioLinha(envio.getDataHora(), saldo(envio.getValor()), envio.getAluno().getNome(), envio.getMotivo());
        }
    }

    public record ResgateLinha(LocalDateTime dataHora, int valor, String vantagemDescricao,
                               String empresaNome, String alunoNome, String codigoCupom) {
        public static ResgateLinha de(TransacaoResgate resgate) {
            return new ResgateLinha(
                    resgate.getDataHora(), saldo(resgate.getValor()),
                    resgate.getVantagem().getDescricao(), resgate.getVantagem().getEmpresa().getNome(),
                    resgate.getAluno().getNome(), resgate.getCodigoCupom());
        }
    }

    public record EnvioPedido(Long alunoId, Integer valor, String motivo) {
    }

    public record EnvioFeito(String mensagem, String aviso, int saldoMoedas) {
    }

    public record ResgateFeito(String mensagem, String codigoCupom) {
    }

    public record VantagensAluno(int saldoMoedas, List<VantagemItem> vantagens) {
    }

    public record ExtratoAluno(int saldoMoedas, List<Recebimento> recebimentos, List<ResgateLinha> resgates,
                               List<EmailItem> emails) {
    }

    public record ExtratoProfessor(int saldoMoedas, String aviso, List<EnvioLinha> envios, List<EmailItem> emails) {
    }

    public record PainelEmpresa(EmpresaDetalhe empresa, List<VantagemItem> vantagens,
                                List<ResgateLinha> resgates, List<EmailItem> emails) {
    }

    private static int saldo(Integer valor) {
        return valor == null ? 0 : valor;
    }
}
