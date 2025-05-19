package org.example;

public class Usuario {
    private String id;
    private String nombre;
    private String contraseña;

    public Usuario(String id, String nombre, String contraseña) {
        this.id = id;
        this.nombre = nombre;
        this.contraseña = contraseña;
    }

    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getContraseña() {
        return contraseña;
    }
}