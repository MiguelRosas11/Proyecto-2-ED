package org.example;

import java.nio.file.*;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class GestorCredenciales {
    private static final String CSV = "usuarios.csv";
    private static final List<String> HEADER = Collections.singletonList("nombre,password,rol");

    public static Usuario autenticar(String nombre, String password) {
        Path path = Path.of(CSV);
        if (!Files.exists(path)) {
            try {
                Files.write(path, HEADER, StandardOpenOption.CREATE);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        try {
            List<String> lines = Files.readAllLines(path);
            for (String line : lines.subList(1, lines.size())) {
                String[] p = line.split(",");
                if (p.length >= 3 && p[0].equals(nombre) && p[1].equals(password)) {
                    return new Usuario(p[0], p[1], p[2]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void registrar(String nombre, String password) {
        Path path = Path.of(CSV);
        String line = nombre + "," + password + ",usuario";
        try {
            Files.writeString(path, System.lineSeparator() + line, StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
