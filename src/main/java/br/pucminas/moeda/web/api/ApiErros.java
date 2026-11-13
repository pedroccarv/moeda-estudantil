package br.pucminas.moeda.web.api;

import br.pucminas.moeda.web.dto.Contratos;
import br.pucminas.moeda.web.sessao.AcessoNegadoException;
import br.pucminas.moeda.web.sessao.NaoAutenticadoException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;

@RestControllerAdvice
public class ApiErros {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Contratos.Mensagem> regra(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(Contratos.Mensagem.de(ex.getMessage()));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Contratos.Mensagem> estado(IllegalStateException ex) {
        return ResponseEntity.badRequest().body(Contratos.Mensagem.de(ex.getMessage()));
    }

    @ExceptionHandler(NaoAutenticadoException.class)
    public ResponseEntity<Contratos.Mensagem> naoAutenticado(NaoAutenticadoException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Contratos.Mensagem.de(ex.getMessage()));
    }

    @ExceptionHandler(AcessoNegadoException.class)
    public ResponseEntity<Contratos.Mensagem> acessoNegado(AcessoNegadoException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Contratos.Mensagem.de(ex.getMessage()));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Contratos.Mensagem> corpoIlegivel() {
        return ResponseEntity.badRequest().body(Contratos.Mensagem.de("Não foi possível ler os dados enviados."));
    }

    @ExceptionHandler({MaxUploadSizeExceededException.class, MultipartException.class})
    public ResponseEntity<Contratos.Mensagem> arquivo(Exception ex) {
        if (ex instanceof MaxUploadSizeExceededException || causaDeTamanho(ex)) {
            return ResponseEntity.badRequest().body(Contratos.Mensagem.de("A foto deve ter no máximo 5 MB."));
        }
        return ResponseEntity.badRequest().body(Contratos.Mensagem.de("Não foi possível receber a foto."));
    }

    private boolean causaDeTamanho(Throwable ex) {
        Throwable atual = ex;
        while (atual != null) {
            if (atual instanceof MaxUploadSizeExceededException) {
                return true;
            }
            if (atual.getMessage() != null && atual.getMessage().toLowerCase().contains("size")) {
                return true;
            }
            atual = atual.getCause();
        }
        return false;
    }
}
