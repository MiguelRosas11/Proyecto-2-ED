package org.example;

public class Usuario {
    private String nombre, password, rol;

    public Usuario() {}
    public Usuario(String nombre, String password, String rol) {
        this.nombre = nombre; this.password = password; this.rol = rol;
    }
    public String getNombre() { return nombre; }
    public String getRol() { return rol; }
    public boolean esAdmin() { return "admin".equalsIgnoreCase(rol); }
}
