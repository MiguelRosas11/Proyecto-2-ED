:begin
CREATE CONSTRAINT UNIQUE_IMPORT_NAME FOR (node:`UNIQUE IMPORT LABEL`) REQUIRE (node.`UNIQUE IMPORT ID`) IS UNIQUE;
:commit
CALL db.awaitIndexes(300);
:begin
UNWIND [{_id:19, properties:{nombre:"Netflix"}}, {_id:20, properties:{nombre:"HBO Max"}}, {_id:21, properties:{nombre:"Disney+"}}, {_id:22, properties:{nombre:"Amazon Prime Video"}}] AS row
CREATE (n:`UNIQUE IMPORT LABEL`{`UNIQUE IMPORT ID`: row._id}) SET n += row.properties SET n:Plataforma;
UNWIND [{_id:6, properties:{rating:8.8, duracion:"Larga", nombre:"Inception"}}, {_id:7, properties:{rating:6.1, duracion:"Corta", nombre:"Yes Day"}}, {_id:8, properties:{rating:8.7, duracion:"Larga", nombre:"The Matrix"}}, {_id:9, properties:{rating:7.6, duracion:"Corta", nombre:"The Mitchells vs. The Machines"}}, {_id:10, properties:{rating:8.0, duracion:"Larga", nombre:"The Revenant"}}, {_id:11, properties:{rating:7.4, duracion:"Corta", nombre:"John Wick"}}] AS row
CREATE (n:`UNIQUE IMPORT LABEL`{`UNIQUE IMPORT ID`: row._id}) SET n += row.properties SET n:Película;
UNWIND [{_id:23, properties:{nombre:"Leonardo DiCaprio"}}, {_id:24, properties:{nombre:"Keanu Reeves"}}, {_id:25, properties:{nombre:"Jennifer Garner"}}, {_id:26, properties:{nombre:"Olivia Colman"}}, {_id:27, properties:{nombre:"Tom Hardy"}}, {_id:28, properties:{nombre:"Abbi Jacobson"}}] AS row
CREATE (n:`UNIQUE IMPORT LABEL`{`UNIQUE IMPORT ID`: row._id}) SET n += row.properties SET n:Persona;
UNWIND [{_id:12, properties:{nombre:"Ciencia Ficción"}}, {_id:13, properties:{nombre:"Comedia"}}, {_id:14, properties:{nombre:"Animación"}}, {_id:15, properties:{nombre:"Drama"}}, {_id:16, properties:{nombre:"Acción"}}, {_id:17, properties:{nombre:"Suspenso"}}, {_id:18, properties:{nombre:"Aventura"}}] AS row
CREATE (n:`UNIQUE IMPORT LABEL`{`UNIQUE IMPORT ID`: row._id}) SET n += row.properties SET n:Género;
:commit
:begin
UNWIND [{start: {_id:6}, end: {_id:12}, properties:{}}, {start: {_id:7}, end: {_id:13}, properties:{}}, {start: {_id:8}, end: {_id:12}, properties:{}}, {start: {_id:9}, end: {_id:13}, properties:{}}, {start: {_id:9}, end: {_id:14}, properties:{}}, {start: {_id:10}, end: {_id:15}, properties:{}}, {start: {_id:10}, end: {_id:18}, properties:{}}, {start: {_id:11}, end: {_id:16}, properties:{}}, {start: {_id:11}, end: {_id:17}, properties:{}}] AS row
MATCH (start:`UNIQUE IMPORT LABEL`{`UNIQUE IMPORT ID`: row.start._id})
MATCH (end:`UNIQUE IMPORT LABEL`{`UNIQUE IMPORT ID`: row.end._id})
CREATE (start)-[r:ES_DEL_GENERO]->(end) SET r += row.properties;
UNWIND [{start: {_id:23}, end: {_id:6}, properties:{}}, {start: {_id:23}, end: {_id:10}, properties:{}}, {start: {_id:24}, end: {_id:8}, properties:{}}, {start: {_id:24}, end: {_id:11}, properties:{}}, {start: {_id:25}, end: {_id:7}, properties:{}}, {start: {_id:26}, end: {_id:9}, properties:{}}, {start: {_id:27}, end: {_id:6}, properties:{}}, {start: {_id:28}, end: {_id:9}, properties:{}}] AS row
MATCH (start:`UNIQUE IMPORT LABEL`{`UNIQUE IMPORT ID`: row.start._id})
MATCH (end:`UNIQUE IMPORT LABEL`{`UNIQUE IMPORT ID`: row.end._id})
CREATE (start)-[r:ACTUÓ_EN]->(end) SET r += row.properties;
UNWIND [{start: {_id:6}, end: {_id:20}, properties:{}}, {start: {_id:7}, end: {_id:19}, properties:{}}, {start: {_id:8}, end: {_id:20}, properties:{}}, {start: {_id:9}, end: {_id:19}, properties:{}}, {start: {_id:10}, end: {_id:20}, properties:{}}, {start: {_id:10}, end: {_id:21}, properties:{}}, {start: {_id:11}, end: {_id:20}, properties:{}}, {start: {_id:11}, end: {_id:22}, properties:{}}] AS row
MATCH (start:`UNIQUE IMPORT LABEL`{`UNIQUE IMPORT ID`: row.start._id})
MATCH (end:`UNIQUE IMPORT LABEL`{`UNIQUE IMPORT ID`: row.end._id})
CREATE (start)-[r:ESTA_EN]->(end) SET r += row.properties;
:commit
:begin
MATCH (n:`UNIQUE IMPORT LABEL`)  WITH n LIMIT 20000 REMOVE n:`UNIQUE IMPORT LABEL` REMOVE n.`UNIQUE IMPORT ID`;
:commit
:begin
DROP CONSTRAINT UNIQUE_IMPORT_NAME;
:commit
