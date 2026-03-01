package com.motorjava;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class DbCheckMySQL {
    public static void main(String[] args) {
        String dbUrlBase = "jdbc:mysql://localhost:3306/vivo_aging";
        String user = "root";
        String p = "";

        try {
            Connection conn = DriverManager.getConnection(dbUrlBase, user, p);
            try (Statement stmt = conn.createStatement()) {
                System.out.println("--- COLUMNS FOR MAQUINARIOS ---");
                ResultSet rs = stmt.executeQuery(
                        "SELECT * FROM maquinarios LIMIT 1");
                java.sql.ResultSetMetaData rsmd = rs.getMetaData();
                int columnsNumber = rsmd.getColumnCount();
                System.out.println("No. of Columns = " + columnsNumber);
                for (int i = 1; i <= columnsNumber; i++) {
                    System.out.println(rsmd.getColumnName(i) + " | " + rsmd.getColumnTypeName(i));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
