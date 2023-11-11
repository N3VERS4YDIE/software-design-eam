package controller;

import connection.DB;
import dao.DAO;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ReportController {

    /**
     * Generates an HTML report from the dao
     *
     * @return an html as string
     * @throws SQLException
     * @throws IOException
     */
    public static int generateReport(DAO<?> dao, String title, String additionalQuery)
        throws SQLException, IOException {
        if (additionalQuery == null) {
            additionalQuery = "";
        }
        ResultSet rs = DB
            .getInstance()
            .prepareStatement("SELECT * FROM " + dao.tableName + " " + additionalQuery)
            .executeQuery();
        String html = Files.readString(Paths.get("app/src/main/resources/template.html"));
        int rows = 0;

        StringBuilder table = new StringBuilder();
        //headers
        table.append("<tr>");
        for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
            table.append("<th>").append(rs.getMetaData().getColumnName(i)).append("</th>");
        }
        table.append("</tr>");
        //data
        while (rs.next()) {
            table.append("<tr>");
            for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                table.append("<td>").append(rs.getString(i)).append("</td>");
            }
            table.append("</tr>");
            rows++;
        }

        if (rows == 0) {
            return 0;
        }

        String currentDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        html = html.replace("$title", title);
        html = html.replace("$date", currentDate);
        html = html.replace("$table", table);

        String pathString = "reports/" + title + " " + currentDate + ".html";
        Path path = Paths.get(pathString);
        Files.write(path, html.getBytes());

        String os = System.getProperty("os.name").toLowerCase();
        ProcessBuilder processBuilder;
        if (os.contains("win")) {
            processBuilder = new ProcessBuilder("cmd.exe", "/c", "start", pathString);
        } else if (os.contains("mac")) {
            processBuilder = new ProcessBuilder("open", pathString);
        } else {
            processBuilder = new ProcessBuilder("xdg-open", pathString);
        }
        processBuilder.start();

        return rows;
    }
}
