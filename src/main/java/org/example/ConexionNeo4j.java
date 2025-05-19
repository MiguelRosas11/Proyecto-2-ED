package org.example;
import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Session;

import java.util.Map;

public class ConexionNeo4j {
    public static void main(String[] args) {
        Driver driver = GraphDatabase.driver("bolt://localhost:7687", AuthTokens.basic("neo4j", "Estructuras123"));

        try (Session session = driver.session()) {
            String greeting = session.writeTransaction(tx -> {
                tx.run("CREATE (n:Person {name: $name})",
                        Map.of("name", "Alice"));
                return "Node created";
            });
            System.out.println(greeting);
        }

        driver.close();
    }
}