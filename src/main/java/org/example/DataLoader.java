package org.example;
import org.neo4j.driver.*;
import java.io.*;
import java.util.*;
import static org.neo4j.driver.Values.parameters;

public class DataLoader {
    private static final String URI = "bolt://localhost:7687";
    private static final String USER = "neo4j";
    private static final String PASSWORD = "Estructuras123";

    private static final Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        Driver driver = GraphDatabase.driver(URI, AuthTokens.basic(USER, PASSWORD));

        while (true) {
            System.out.println("\n--- Menú del sistema de recomendación de películas ---");
            System.out.println("1. Agregar película manualmente");
            System.out.println("2. Agregar películas desde archivo CSV");
            System.out.println("3. Agregar usuario");
            System.out.println("4. Salir");
            System.out.print("Seleccione una opción: ");
            int opcion = Integer.parseInt(sc.nextLine());

            try (Session sesion = driver.session()) {
                switch (opcion) {
                    case 1 -> addMovieManually(sesion);
                    case 2 -> loadMoviesFromFile(sesion);
                    case 3 -> addUser(sesion);
                    case 4 -> {
                        driver.close();
                        return;
                    }
                    default -> System.out.println("Opción no válida.");
                }
            }
        }
    }

    private static void addMovieManually(Session sesion) {
        System.out.print("Nombre de la película: ");
        String nombre = sc.nextLine();

        double rating = promptDouble("Rating: ");
        System.out.print("Duración (Corta/Larga): ");
        String duracion = sc.nextLine();
        System.out.print("Géneros (separados por |): ");
        List<String> generos = Arrays.asList(sc.nextLine().split("\\|"));
        System.out.print("Plataformas (separadas por |): ");
        List<String> plataformas = Arrays.asList(sc.nextLine().split("\\|"));
        System.out.print("Actores (separados por |): ");
        List<String> actores = Arrays.asList(sc.nextLine().split("\\|"));

        createMovieNode(sesion, nombre, rating, duracion, generos, plataformas, actores);
    }

    private static void loadMoviesFromFile(Session sesion) {
        System.out.print("Ruta del archivo CSV: ");
        String ruta = sc.nextLine();

        try (BufferedReader br = new BufferedReader(new FileReader(ruta))) {
            String linea;
            boolean encabezadoSaltado = false;
            while ((linea = br.readLine()) != null) {
                if (!encabezadoSaltado) {
                    encabezadoSaltado = true;
                    continue;
                }

                String[] partes = linea.split(",", -1);
                String nombre = partes.length > 0 ? partes[0] : "SinNombre";
                double rating = parseDoubleSafe(partes, 1);
                String duracion = partes.length > 2 ? partes[2] : "";
                List<String> generos = partes.length > 3 ? Arrays.asList(partes[3].split("\\|")) : new ArrayList<>();
                List<String> plataformas = partes.length > 4 ? Arrays.asList(partes[4].split("\\|")) : new ArrayList<>();
                List<String> actores = partes.length > 5 ? Arrays.asList(partes[5].split("\\|")) : new ArrayList<>();

                createMovieNode(sesion, nombre, rating, duracion, generos, plataformas, actores);
            }
        } catch (IOException e) {
            System.out.println("Error leyendo archivo: " + e.getMessage());
        }
    }

    private static void addUser(Session sesion) {
        System.out.print("ID del usuario: ");
        String id = sc.nextLine();
        System.out.print("Nombre del usuario: ");
        String nombre = sc.nextLine();

        sesion.writeTransaction(tx -> {
            tx.run("MERGE (:Usuario {id: $id, nombre: $nombre})",
                    parameters("id", id, "nombre", nombre));
            System.out.println("Usuario creado: " + nombre);
            return null;
        });
    }

    private static double promptDouble(String mensaje) {
        System.out.print(mensaje);
        try {
            return Double.parseDouble(sc.nextLine());
        } catch (Exception e) {
            return 0.0;
        }
    }

    private static double parseDoubleSafe(String[] arr, int index) {
        if (index >= arr.length || arr[index].isBlank()) return 0.0;
        try {
            return Double.parseDouble(arr[index]);
        } catch (Exception e) {
            return 0.0;
        }
    }

    private static void createMovieNode(Session sesion, String nombre, double rating, String duracion,
                                        List<String> generos, List<String> plataformas, List<String> actores) {
        sesion.writeTransaction(tx -> {
            tx.run("CREATE (m:Película {nombre: $nombre, rating: $rating, duracion: $duracion})",
                    parameters("nombre", nombre, "rating", rating, "duracion", duracion));

            for (String genero : generos) {
                if (!genero.isBlank()) {
                    tx.run("""
                        MERGE (g:Género {nombre: $genero})
                        WITH g
                        MATCH (m:Película {nombre: $nombre})
                        MERGE (m)-[:ES_DEL_GENERO]->(g)
                    """, parameters("nombre", nombre, "genero", genero.trim()));
                }
            }

            for (String plataforma : plataformas) {
                if (!plataforma.isBlank()) {
                    tx.run("""
                        MERGE (p:Plataforma {nombre: $plataforma})
                        WITH p
                        MATCH (m:Película {nombre: $nombre})
                        MERGE (m)-[:ESTÁ_EN]->(p)
                    """, parameters("nombre", nombre, "plataforma", plataforma.trim()));
                }
            }

            for (String actor : actores) {
                if (!actor.isBlank()) {
                    tx.run("""
                        MERGE (a:Actor {nombre: $actor})
                        WITH a
                        MATCH (m:Película {nombre: $nombre})
                        MERGE (a)-[:ACTUÓ_EN]->(m)
                    """, parameters("nombre", nombre, "actor", actor.trim()));
                }
            }

            System.out.println("Película creada: " + nombre);
            return null;
        });
    }
}