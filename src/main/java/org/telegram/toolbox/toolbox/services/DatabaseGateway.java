package org.telegram.toolbox.toolbox.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.toolbox.toolbox.configuration.Settings;
import org.telegram.toolbox.toolbox.models.Event;
import org.telegram.toolbox.toolbox.models.Source;
import org.telegram.toolbox.toolbox.models.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Service
public class DatabaseGateway<T> {
    private final String user;
    private final String password;
    private final String path;

    @Autowired
    public DatabaseGateway(final Settings settings) {
        user = settings.getDbUser();
        password = settings.getDbPassword();
        path = settings.getDbPath();
    }

    private ResultSet getEntities(final String query) throws SQLException {
        Connection conn = DriverManager.getConnection("jdbc:mariadb://" + path, user, password);

        return conn.createStatement().executeQuery(query);
    }

    private int execStatement(final String query) throws SQLException {
        Connection conn = DriverManager.getConnection("jdbc:mariadb://" + path, user, password);
        int res = conn.createStatement().executeUpdate(query);
        conn.commit();
        return res;
    }

    public Source getSource(final String author, final String label) {
        try {
            ResultSet rs = getEntities(String.format("SELECT * FROM source WHERE author='%s' and label='%s'", author, label));
            while (rs.next()) {
                return new Source().setAuthor(rs.getString("author"))
                        .setLabel(rs.getString("label"))
                        .setAccess((rs.getInt("access") == 0 ? Source.Access.PUBLIC : Source.Access.PRIVATE))
                        .setType(rs.getString("type"))
                        .setFileId(rs.getString("file_id"))
                        .setTimestamp(rs.getTimestamp("creation_date").getTime());
            }
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public User getUser(final String author) {
        try {
            ResultSet rs = getEntities(String.format("SELECT * FROM user WHERE id='%s'", author));
            while (rs.next()) {
                return new User().setId(rs.getString("id"))
                        .setCarma(rs.getInt("carma"))
                        .setTimestamp(rs.getTimestamp("creation_date").getTime());
            }
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<Event> getEvents(final String author) {
        try {
            ResultSet rs = getEntities(String.format("SELECT * FROM event WHERE author='%s'", author));
            List<Event> arr = new ArrayList<>();

            while (rs.next()) {
                arr.add(new Event().setAuthor(rs.getString("author"))
                        .setProperties(rs.getString("properties"))
                        .setType(rs.getString("type"))
                        .setTimestamp(rs.getTimestamp("creation_date").getTime()));
            }

            return arr;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

  public void insertOrUpdate(final User user) {
        User old = getUser(user.getId());
        String query;
        if (old == null) {
            query = String.format("INSERT INTO user (id, carma, creation_date) values ('%s', %s, NOW())", user.getId(), user.getCarma());
        } else {
            query = String.format("UPDATE user SET carma = %s WHERE id = '%s'", user.getCarma(), user.getId());
        }

      try {
          execStatement(query);
      } catch (SQLException e) {
          e.printStackTrace();
      }
  }

  public void insert(final Source source) {
        String query = String.format("INSERT INTO source (label, author, access, type, file_id, creation_date) values ('%s', '%s', %s, '%s', '%s', NOW())",
                source.getLabel(), source.getAuthor(), (source.getAccess() == Source.Access.PUBLIC ? 0 : 1), source.getType(), source.getFileId());
      try {
          execStatement(query);
      } catch (SQLException e) {
          e.printStackTrace();
      }
  }

    public void insert(final Event event) {
        String query = String.format("INSERT INTO event (author, type, properties, creation_date) values ('%s', '%s', '%s', NOW())",
                event.getAuthor(),  event.getType(), event.getProperties());
        try {
            execStatement(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
