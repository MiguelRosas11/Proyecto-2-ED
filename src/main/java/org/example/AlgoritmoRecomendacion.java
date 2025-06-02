package org.example;

import org.neo4j.driver.Session;
import org.neo4j.driver.Values;
import org.neo4j.driver.Record;

import java.util.*;

public class AlgoritmoRecomendacion {

    public List<String> recomendar(String nombreUsuario) {
        List<String> vistas;
        Map<String, Double> mapScores = new HashMap<>();
        Set<String> candidatas = new LinkedHashSet<>();

        try (Session session = ConexionNeo4j.getInstance().getSession()) {

            // Películas vistas
            vistas = session.run(
                    "MATCH (u:Usuario {nombre:$n})-[:VIÓ]->(p:Película) RETURN p.nombre AS nom",
                    Values.parameters("n", nombreUsuario)
            ).list(r -> r.get("nom").asString());

            // Conteo de preferencias
            Record prefs = session.run(
                    "MATCH (u:Usuario {nombre:$n}) " +
                            "RETURN count { (u)-[:PREFIERE_GÉNERO]->() } AS g, " +
                            "       count { (u)-[:PREFIERE_ACTOR]->() } AS a, " +
                            "       count { (u)-[:PREFIERE_PLATAFORMA]->() } AS p",
                    Values.parameters("n", nombreUsuario)
            ).single();

            int g = prefs.get("g").asInt();
            int a = prefs.get("a").asInt();
            int p = prefs.get("p").asInt();
            double total = g + a + p;
            double pesoG = total > 0 ? g / total : 0.0;
            double pesoA = total > 0 ? a / total : 0.0;
            double pesoP = total > 0 ? p / total : 0.0;

            // Colaborativa
            session.run(
                    "MATCH (u1:Usuario {nombre:$n}) " +
                            "MATCH (u1)-[:VIÓ]->(p:Película)<-[:VIÓ]-(v:Usuario) " +
                            "WITH u1, v, collect(p) AS comunes, " +
                            "count { (u1)-[:VIÓ]->(:Película) } AS t1, " +
                            "count { (v)-[:VIÓ]->(:Película) } AS t2 " +
                            "WITH u1, v, toFloat(size(comunes))/sqrt(t1*t2) AS sim " +
                            "MATCH (v)-[:VIÓ]->(rec:Película) " +
                            "WHERE NOT (u1)-[:VIÓ]->(rec) " +
                            "RETURN rec.nombre AS nom, sim AS score LIMIT 50",
                    Values.parameters("n", nombreUsuario)
            ).list().forEach(r -> {
                String nom = r.get("nom").asString();
                double score = r.get("score").asDouble();
                mapScores.put(nom, score);
            });

            // Géneros
            session.run(
                    "MATCH (u:Usuario {nombre:$n})-[:PREFIERE_GÉNERO]->(g:Género)<-[:PERTENECE_A]-(rec:Película) " +
                            "WHERE NOT (u)-[:VIÓ]->(rec) RETURN DISTINCT rec.nombre AS nom",
                    Values.parameters("n", nombreUsuario)
            ).list().forEach(r -> {
                String nom = r.get("nom").asString();
                mapScores.put(nom, mapScores.getOrDefault(nom, 0.0) + pesoG);
            });

            // Actores
            session.run(
                    "MATCH (u:Usuario {nombre:$n})-[:PREFIERE_ACTOR]->(a:Actor)<-[:ACTÚA_EN]-(rec:Película) " +
                            "WHERE NOT (u)-[:VIÓ]->(rec) RETURN DISTINCT rec.nombre AS nom",
                    Values.parameters("n", nombreUsuario)
            ).list().forEach(r -> {
                String nom = r.get("nom").asString();
                mapScores.put(nom, mapScores.getOrDefault(nom, 0.0) + pesoA);
            });

            // Plataformas
            session.run(
                    "MATCH (u:Usuario {nombre:$n})-[:PREFIERE_PLATAFORMA]->(pl:Plataforma)<-[:ESTÁ_EN]-(rec:Película) " +
                            "WHERE NOT (u)-[:VIÓ]->(rec) RETURN DISTINCT rec.nombre AS nom",
                    Values.parameters("n", nombreUsuario)
            ).list().forEach(r -> {
                String nom = r.get("nom").asString();
                mapScores.put(nom, mapScores.getOrDefault(nom, 0.0) + pesoP);
            });

            // Populares
            List<String> populares = session.run(
                    "MATCH (u:Usuario {nombre:$n}) " +
                            "MATCH (p:Película) " +
                            "WHERE NOT (u)-[:VIÓ]->(p) " +
                            "RETURN p.nombre AS nom ORDER BY p.rating DESC LIMIT 10",
                    Values.parameters("n", nombreUsuario)
            ).list(r -> r.get("nom").asString());

            for (Map.Entry<String, Double> entry : mapScores.entrySet()) {
                if (!vistas.contains(entry.getKey())) {
                    candidatas.add(entry.getKey());
                }
            }

            for (String pop : populares) {
                if (!vistas.contains(pop) && !candidatas.contains(pop)) {
                    candidatas.add(pop);
                }
                if (candidatas.size() >= 5) break;
            }
        }

        List<String> listaFinal = new ArrayList<>(candidatas);
        Collections.shuffle(listaFinal);
        return listaFinal.stream().limit(5).toList();
    }
}