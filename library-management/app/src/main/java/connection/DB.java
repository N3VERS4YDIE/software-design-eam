package connection;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.n3vers4ydie.autosqlcrud.SQLDB;
import java.io.File;
import java.io.IOException;

public class DB {

    private static SQLDB instance;

    private DB() {}

    public static SQLDB getInstance() {
        if (instance == null) {
            final String CONFIG_FILE_PATH = "db/config.json";
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                JsonNode configNode = objectMapper.readTree(new File(CONFIG_FILE_PATH));

                String jdbcURL = configNode.get("jdbcUrl").asText();
                String host = configNode.get("host").asText();
                String port = configNode.get("port").asText();
                String user = configNode.get("user").asText();
                String password = configNode.get("password").asText();
                String database = configNode.get("database").asText();

                // instance = new SQLDB(JDBCURL.MARIADB, "localhost:3306/library", "root", "");
                instance = new SQLDB(jdbcURL + host + ":" + port + "/" + database, user, password);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return instance;
    }
}
