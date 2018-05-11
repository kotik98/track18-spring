package examples;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.sun.istack.internal.NotNull;

public class ConnectWithStatements {


    public static void main(String[] args) {
        //connect();

        final long limit = 10;
        List<Message> allMessages = new ArrayList<>((int) limit * 3);
        for (int i = 1; i <= 3; i++) {
            Connection conn = RemoteDbExample.getConnection(i);
            ConversationService service = new ConversationService(conn);


            List<Message> history = service.getHistory(0, System.currentTimeMillis(), limit);
            //System.out.println(history);
            allMessages.addAll(history);

//            selectMessages(conn);
        }

        allMessages.sort((m1, m2) -> {
            if (m1.ts > m2.ts) return 1;
            else if (m1.ts < m2.ts) return -1;
            return 0;
        });

        System.out.println(allMessages);
        System.out.println("\n\n\n----\n");

        List<Message> result = allMessages.subList(0, (int) Math.min(limit, allMessages.size()));
        System.out.println("SIZE: " + result.size());
        System.out.println(result);


    }

    static class Message {
        long id;
        String text;
        String username;
        long ts;

        public Message(long id, String text, String username, long ts) {
            this.id = id;
            this.text = text;
            this.username = username;
            this.ts = ts;
        }

        @Override
        public String toString() {
            return "Message{" +
                    "text='" + text + '\'' +
                    ", username='" + username + '\'' +
                    ", ts=" + new Timestamp(ts) +
                    '}';
        }
    }

    static class ConversationService {

        Connection conn;

        public ConversationService(Connection conn) {
            this.conn = conn;
        }

        @NotNull
        List<Message> getHistory(long from, long to, long limit) {
            try {
                PreparedStatement select = conn.prepareStatement(
                        "SELECT * FROM messages " +
                                "WHERE ts > ? AND ts < ? " +
                                "ORDER BY ts ASC LIMIT ?;");

                select.setTimestamp(1, new Timestamp(from));
                select.setTimestamp(2, new Timestamp(to));
                select.setLong(3, limit);

//                System.out.println(select.toString());

                ResultSet resultSet = select.executeQuery();

                MsgResultHandler handler = new MsgResultHandler();
                return handler.handle(resultSet);

            } catch (SQLException e) {
                e.printStackTrace();
            }
            return Collections.emptyList();
        }

    }


    interface ResultHandler<T> {
        T handle(ResultSet rs);
    }

    static class MsgResultHandler implements ResultHandler<List<Message>> {
        @Override
        public List<Message> handle(ResultSet rs) {
            try {
                List<Message> list = new ArrayList<>();
                while (rs.next()) {
                    String userName = rs.getString("user_name");
                    String text = rs.getString("text");
                    Timestamp ts = rs.getTimestamp("ts");

                    Message msg = new Message(0, text, userName, ts.getTime());
//                    System.out.println(msg);
                    list.add(msg);
                }
                return list;
            } catch (SQLException e) {
                e.printStackTrace();
                return Collections.emptyList();
            }
        }
    }

    private static void selectMessages(Connection conn) {
        try {
            Statement statement = conn.createStatement();
            ResultSet countRs = statement.executeQuery("select count(*) from messages");
            Long rows = null;
            if (countRs.next()) {
                rows = countRs.getLong(1);
            }

            PreparedStatement select = null;
            select = conn.prepareStatement("SELECT * FROM messages ORDER BY ts desc LIMIT 100");
            ResultSet resultSet = select.executeQuery();

            System.out.print(String.format("\nDB: %s, rows: %d\n",
                    conn.getMetaData().getURL(), rows));
            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                String userName = resultSet.getString("user_name");
                String text = resultSet.getString("text");
                Timestamp ts = resultSet.getTimestamp("ts");

                System.out.print(String.format("%d:%s %s %s\n", id, userName, text, ts));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
