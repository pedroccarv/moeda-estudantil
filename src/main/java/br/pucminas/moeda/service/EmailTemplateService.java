package br.pucminas.moeda.service;

import br.pucminas.moeda.domain.Aluno;
import br.pucminas.moeda.domain.Professor;
import br.pucminas.moeda.domain.Vantagem;
import org.springframework.stereotype.Service;

@Service
public class EmailTemplateService {
    public String recebimentoMoedas(Professor professor, Integer valor, String motivo) {
        return "Olá.\n\n"
                + "Você recebeu " + valor + " moedas de " + professor.getNome()
                + " (" + professor.getInstituicao().getNome() + ").\n\n"
                + "Motivo: " + motivo + ".\n\n"
                + "Consulte o extrato no Sistema de Moeda Estudantil.";
    }

    public String cupomParaAluno(String nomeAluno, Vantagem vantagem, String codigo) {
        return "Olá, " + nomeAluno + ".\n\n"
                + "Seu resgate foi confirmado.\n\n"
                + "Vantagem: " + vantagem.getDescricao() + "\n"
                + "Empresa: " + vantagem.getEmpresa().getNome() + "\n"
                + "Custo: " + vantagem.getCustoEmMoedas() + " moedas\n"
                + "Código do cupom: " + codigo + "\n\n"
                + "Apresente este código na troca presencial.";
    }

    public String cupomParaEmpresa(Aluno aluno, Vantagem vantagem, String codigo) {
        return "Olá, " + vantagem.getEmpresa().getNome() + ".\n\n"
                + "O aluno " + aluno.getNome() + " (" + aluno.getEmail() + ") resgatou a vantagem \""
                + vantagem.getDescricao() + "\".\n\n"
                + "Código do cupom: " + codigo + "\n\n"
                + "Use o código para conferir a troca presencial.";
    }
}
