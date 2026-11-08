package br.pucminas.moeda.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
public class FotoStorage {

    private static final Set<String> EXTENSOES = Set.of(".png", ".jpg", ".jpeg", ".webp", ".gif");

    public String salvar(MultipartFile foto) {
        if (foto == null || foto.isEmpty()) {
            return null;
        }
        String extensao = extensaoDe(foto);
        if (!EXTENSOES.contains(extensao)) {
            throw new IllegalArgumentException("Envie uma imagem PNG, JPEG, WEBP ou GIF");
        }
        try {
            Path diretorio = Path.of("data", "uploads").toAbsolutePath();
            Files.createDirectories(diretorio);
            String nome = UUID.randomUUID() + extensao;
            Path destino = diretorio.resolve(nome);
            try (InputStream entrada = foto.getInputStream()) {
                Files.copy(entrada, destino, StandardCopyOption.REPLACE_EXISTING);
            }
            return "/uploads/" + nome;
        } catch (IOException ex) {
            throw new IllegalArgumentException("Não foi possível salvar a foto");
        }
    }

    private String extensaoDe(MultipartFile foto) {
        String tipo = foto.getContentType() == null ? "" : foto.getContentType().toLowerCase(Locale.ROOT);
        String porTipo = switch (tipo) {
            case "image/png" -> ".png";
            case "image/jpeg" -> ".jpg";
            case "image/webp" -> ".webp";
            case "image/gif" -> ".gif";
            default -> "";
        };
        if (!porTipo.isEmpty()) {
            return porTipo;
        }
        String original = foto.getOriginalFilename();
        if (original == null || !original.contains(".")) {
            return "";
        }
        return original.substring(original.lastIndexOf('.')).toLowerCase(Locale.ROOT);
    }
}
