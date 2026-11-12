package com.sally.socialartist.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.UUID;

@Service
public class AlmacenamientoService {

    private static final Map<String, String> EXTENSIONES = Map.of(
            "image/jpeg", ".jpg",
            "image/png", ".png",
            "image/webp", ".webp",
            "image/gif", ".gif");

    private final Path raiz;

    public AlmacenamientoService(@Value("${app.uploads.dir}") String directorio) {
        this.raiz = Paths.get(directorio).toAbsolutePath().normalize();
        try {
            Files.createDirectories(raiz);
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo crear la carpeta de imágenes: " + raiz, e);
        }
    }

    public Path getRaiz() {
        return raiz;
    }

    /** Guarda la imagen con un nombre generado y devuelve ese nombre. */
    public String guardar(MultipartFile archivo) {
        if (archivo == null || archivo.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Selecciona una imagen");
        }
        String extension = EXTENSIONES.get(archivo.getContentType());
        if (extension == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Solo se permiten imágenes JPG, PNG, WEBP o GIF");
        }
        String nombre = UUID.randomUUID() + extension;
        try (InputStream entrada = archivo.getInputStream()) {
            Files.copy(entrada, raiz.resolve(nombre));
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "No se pudo guardar la imagen");
        }
        return nombre;
    }

    public void eliminar(String nombre) {
        try {
            Files.deleteIfExists(raiz.resolve(nombre).normalize());
        } catch (IOException e) {
            // Si no se puede borrar el archivo no se interrumpe la operacion
        }
    }
}
