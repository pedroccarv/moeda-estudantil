package br.pucminas.moeda.service;

import br.pucminas.moeda.domain.Aluno;
import br.pucminas.moeda.domain.Professor;
import br.pucminas.moeda.domain.Vantagem;
import org.springframework.stereotype.Component;

/**
 * Pure Fabrication + Protected Variations (GRASP): centraliza a formatação
 * das mensagens de notificação por e-mail, desacoplando os serviços de domínio
 * (ProfessorService e ResgateService) dos detalhes de apresentação textual.
 */
@Component
public class FormatadorNotificacao {

    public record ConteudoEmail(String assunto, String corpo) {
    }

    public ConteudoEmail recebimentoMoedas(Professor professor, Integer valor, String motivo) {
        String assunto = "Moeda Estudantil — você recebeu moedas";
        String corpo = "Olá.\n\n"
                + "Você recebeu " + valor + " moedas de " + professor.getNome()
                + " (" + professor.getInstituicao().getNome() + ").\n\n"
                + "Motivo: " + motivo + ".\n\n"
                + "Consulte o extrato no Sistema de Moeda Estudantil.";
        return new ConteudoEmail(assunto, corpo);
    }

    public ConteudoEmail cupomAluno(String nomeAluno, Vantagem vantagem, String codigo) {
        String assunto = "Moeda Estudantil — cupom de resgate";
        String corpo = "Olá, " + nomeAluno + ".\n\n"
                + "Seu resgate foi confirmado.\n\n"
                + "Vantagem: " + vantagem.getDescricao() + "\n"
                + "Empresa: " + vantagem.getEmpresa().getNome() + "\n"
                + "Custo: " + vantagem.getCustoEmMoedas() + " moedas\n"
                + "Código do cupom: " + codigo + "\n\n"
                + "Apresente este código na troca presencial.";
        return new ConteudoEmail(assunto, corpo);
    }

    public ConteudoEmail cupomEmpresa(Aluno aluno, Vantagem vantagem, String codigo) {
        String assunto = "Moeda Estudantil — cupom para conferência";
        String corpo = "Olá, " + vantagem.getEmpresa().getNome() + ".\n\n"
                + "O aluno " + aluno.getNome() + " (" + aluno.getEmail() + ") resgatou a vantagem \""
                + vantagem.getDescricao() + "\".\n\n"
                + "Código do cupom: " + codigo + "\n\n"
                + "Use o código para conferir a troca presencial.";
        return new ConteudoEmail(assunto, corpo);
    }
}
