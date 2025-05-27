package org.example;

import org.neo4j.driver.Session;
import org.neo4j.driver.Values;
import java.util.*;
import java.util.stream.Collectors;

public class AlgoritmoRecomendacion {
    public List<String> recomendar(String nombreUsuario) {
        Map<String, Double> mapScores = new HashMap<>();
        try (Session session = ConexionNeo4j.getInstance().getSession()) {
            // 1) Colaborativo: vecinos con al menos películas en común
            session.run(
                            "MATCH (u1:Usuario {nombre:$n}) " +
                                    "MATCH (u1)-[:VIÓ]->(p:Película)<-[:VIÓ]-(v:Usuario) " +
                                    "WITH u1, v, collect(p) AS comunes, " +
                                    "size([(u1)-[:VIÓ]->(x) | x]) AS t1, " +
                                    "size([(v)-[:VIÓ]->(x) | x]) AS t2 " +
                                    "WITH u1, v, toFloat(size(comunes))/sqrt(t1*t2) AS sim " +
                                    "MATCH (v)-[:VIÓ]->(rec:Película) " +
                                    "WHERE NOT (u1)-[:VIÓ]->(rec) " +
                                    "RETURN rec.nombre AS nom, sim AS score " +
                                    "LIMIT 50",
                            Values.parameters("n", nombreUsuario)
                    ).list(r -> Map.of("nom", r.get("nom").asString(), "score", r.get("score").asDouble()))
                    .forEach(m -> mapScores.merge((String)m.get("nom"), (Double)m.get("score"), Double::sum));

            // 2) Content-based: géneros favoritos
            session.run(
                            "MATCH (u:Usuario {nombre:$n})-[:PREFIERE_GÉNERO]->(g:Género)<-[:PERTENECE_A]-(rec:Película) " +
                                    "WHERE NOT (u)-[:VIÓ]->(rec) " +
                                    "RETURN DISTINCT rec.nombre AS nom, 0.5 AS score",
                            Values.parameters("n", nombreUsuario)
                    ).list(r -> Map.of("nom", r.get("nom").asString(), "score", r.get("score").asDouble()))
                    .forEach(m -> mapScores.merge((String)m.get("nom"), (Double)m.get("score"), Double::sum));
        }


        List<String> sorted = mapScores.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .map(Map.Entry::getKey).limit(10).collect(Collectors.toList());
        Collections.shuffle(sorted);
        return sorted.stream().limit(5).collect(Collectors.toList());
    }
}