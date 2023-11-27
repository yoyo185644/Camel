package yyy.ts.compress.camel;


import org.postgresql.util.PGobject;

import java.sql.*;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

public class TimeScaleDBDriver {
    private final static String url = "jdbc:postgresql://127.0.0.1:5432/postgres";
    private final static String user = "yaoyuanyuan";
    private final static String password = "";
    public static Connection TimeScaleConnect(){
        // JDBC URL, username, and password of PostgreSQL server
        try {
            // Establish a connection
            Connection connection = DriverManager.getConnection(url, user, password);
            return connection;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

    }

    public static void selectTable(Connection connection) throws SQLException {
        // Create a statement
        Statement statement = connection.createStatement();

        // Execute a query to get the names of all tables in the database
        String query = "SELECT table_name FROM information_schema.tables WHERE table_schema = 'public'";
        ResultSet resultSet = statement.executeQuery(query);

        // Process the result set
        while (resultSet.next()) {
            String tableName = resultSet.getString("table_name");
            System.out.println("Table Name: " + tableName);
        }

        // Close the result set, statement, and connection when done
        resultSet.close();
        statement.close();
        connection.close();
    }

    // 根据index值查询时间戳
    public static double selectTime(Connection connection, String tableName, String searchColumn, String indexColumn, Double value){
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            // Define the SQL query with placeholders for parameters
            String selectQuery = "SELECT " +  searchColumn + " FROM "+  tableName + " WHERE " + indexColumn + " = ?";

            // Create a prepared statement
            preparedStatement = connection.prepareStatement(selectQuery);

            // Set the value for the placeholder
            preparedStatement.setDouble(1, value);

            // Execute the query
            resultSet = preparedStatement.executeQuery();

            // Process the result set
            while (resultSet.next()) {
                Time res = resultSet.getTime(searchColumn);
                System.out.println("Value: " + res);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Close the result set, statement, and connection when done
            try {
                if (resultSet != null) resultSet.close();
                if (preparedStatement != null) preparedStatement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }


    // 从#datasetname_camel中查询压缩后小数字的数据(decimal)
    private static String selectDecimalData(Connection connection, String tableName, String searchColumn, String indexColumn, String bits_int ) throws SQLException {
        String queryDataSQL = "SELECT " + searchColumn + " FROM " + tableName + " where " + indexColumn + "= ?";
        List<String> res = new ArrayList<>();
        PreparedStatement preparedStatement = null;
        try {
            // Create a prepared statement
            preparedStatement = connection.prepareStatement(queryDataSQL);

            PGobject bitObject_int = new PGobject();
            bitObject_int.setType("bit");
            bitObject_int.setValue(bits_int);
            // Set the byte array as a binary parameter
            preparedStatement.setObject(1, bitObject_int);

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                PGobject obj = (PGobject) resultSet.getObject(searchColumn);

                    // Process the PGobject as needed
                if (obj != null) {
                        // Access the value of the PGobject
                    String bytes = obj.getValue();
                    System.out.println("Bit array as string: " + bytes);
                    return bytes;
                }

            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Close the result set, statement, and connection when done
            try {
                if (preparedStatement != null) preparedStatement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    // 从#datasetname_camel中查询压缩后的数据(int/decimal)
    private static List<String> selectBinaryData(Connection connection, String tableName, String column, int num) throws SQLException {
        String queryDataSQL = "SELECT " + column + " FROM " + tableName + " limit " + num;
        List<String> res = new ArrayList<>();
        try (PreparedStatement preparedStatement = connection.prepareStatement(queryDataSQL);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                PGobject obj = (PGobject) resultSet.getObject(column);

                // Process the PGobject as needed
                if (obj != null) {
                    // Access the value of the PGobject
                    String bytes = obj.getValue();
                    res.add(bytes);
                    System.out.println("Bit array as string: " + bytes);
                }

            }
        }
        return res;
    }

    // 从#datasetname_camel中查询压缩后的数据(int&&decimal)
    private static List<List<String>> selectAllBinaryData(Connection connection, String tableName, String column1, String column2, int num) throws SQLException {
        String queryDataSQL = "SELECT " + column1 + "," + column2 + " FROM " + tableName + " limit " + num;
        List<List<String>> res = new ArrayList<>();
        try (PreparedStatement preparedStatement = connection.prepareStatement(queryDataSQL);
             ResultSet resultSet = preparedStatement.executeQuery()) {
             List<String> int_list = new ArrayList<>();
             List<String> decimal_list = new ArrayList<>();
             while (resultSet.next()) {
                PGobject obj_int = (PGobject) resultSet.getObject(column1);
                PGobject obj_decimal = (PGobject) resultSet.getObject(column2);

                // Process the PGobject as needed
                if (obj_int != null && obj_decimal != null) {
                    // Access the value of the PGobject
                    String bytes_int = obj_int.getValue();
                    String bytes_decimal = obj_decimal.getValue();
                    int_list.add(bytes_int);
                    decimal_list.add(bytes_decimal);
                    System.out.println("Bit array as string: " + bytes_int + bytes_decimal);
                }
             }
             res.add(int_list);
             res.add(decimal_list);
        }

        return res;
    }

    // 插入压缩后的数据到#datasetname_camel中
    public static void insertCompressValue(Connection connection, String tableName, String column1, String column2, String bytes_int, String bytes_decimal) throws SQLException {
        String insertQuery = "INSERT INTO " + tableName + " (" + column1 + "," + column2 + ") " + "VALUES (?, ?)";
        PreparedStatement preparedStatement = null;
        try {
            // Create a prepared statement
            preparedStatement = connection.prepareStatement(insertQuery);

            PGobject bitObject_int = new PGobject();
            bitObject_int.setType("bit");
            bitObject_int.setValue(bytes_int);

            PGobject bitObject_decimal = new PGobject();
            bitObject_decimal.setType("bit");
            bitObject_decimal.setValue(bytes_decimal);

            // Set the byte array as a binary parameter
            preparedStatement.setObject(1, bitObject_int);
            preparedStatement.setObject(2, bitObject_decimal);


            // Execute the insert statement
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Close the result set, statement, and connection when done
            try {
                if (preparedStatement != null) preparedStatement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // 插入原始数据到dataset表中
    public static void insertValue(Connection connection, String tableName, String column, Double value){
        String insertQuery = "INSERT INTO " + tableName + " (" + column + ") " + "VALUES (?)";
        PreparedStatement preparedStatement = null;
        try {
            // Create a prepared statement
            preparedStatement = connection.prepareStatement(insertQuery);

            // Set the byte array as a binary parameter
            preparedStatement.setDouble(1, value);

            // Execute the insert statement
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Close the result set, statement, and connection when done
            try {
                if (preparedStatement != null) preparedStatement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    public static void main(String[] args) throws SQLException {
       Connection connection = TimeScaleConnect();
//     insertValue(connection, "init", "data", 234.22443424);

//     insertCompressValue(connection, "init_camel", "compress_int", "compress_decimal", "11", "111111111111");

//        selectAllBinaryData(connection, "init_camel", "compress_int", "compress_decimal", 3);
//        selectBinaryData(connection, "init_camel", "compress_int", 2);

//        selectTime(connection, "init", "time", "data", 3.1243424);
//        selectDecimalData(connection, "init_camel", "compress_decimal", "compress_int", "11");
    }

}
