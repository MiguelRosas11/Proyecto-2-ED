package org.example;

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Session;

public class ConexionNeo4j {
    private static ConexionNeo4j instance;
    private final Driver driver;

    private ConexionNeo4j() {
        driver = GraphDatabase.driver("bolt://localhost:7687",
            AuthTokens.basic("neo4j","Estructuras123"));
    }

    public static ConexionNeo4j getInstance() {
        if (instance == null) instance = new ConexionNeo4j();
        return instance;
    }

    public Session getSession() { return driver.session(); }
    public void close() { driver.close(); }
}
