package net.cleverleo.common;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {

	private static Connection conn = null;
	private static String addr = "127.0.0.1";
	private static String port = "3306";
	private static String username = "ebook";
	private static String pwd = "19910804";
	private static String db = "ebook";

	public static Statement getStatement() throws SQLException {
		if (Database.conn == null) {
			try {
				Class.forName("com.mysql.jdbc.Driver");
			} catch (ClassNotFoundException e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();
			}
			Database.conn = DriverManager.getConnection(String.format(
					"jdbc:mysql://%s:%s/%s?characterEncoding=utf-8", addr,
					port, db), username, pwd);
		}

		return Database.conn.createStatement();
	}
}
