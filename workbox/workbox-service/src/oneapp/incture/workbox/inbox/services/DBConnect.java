//package oneapp.incture.workbox.inbox.services;
//
//import java.sql.Connection;
//import java.sql.DriverManager;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.sql.Statement;
//
//public final class DBConnect {
//	
//	public Connection conn;
//	private Statement statement;
//	public static DBConnect db;
//
//	private DBConnect() {
//		
//		String url = "jdbc:oracle:thin:@34.213.118.108:1521:xe";
//		String driver = "oracle.jdbc.driver.OracleDriver";
//		String userName = "SPOTLIGHT";
//		String password = "SPOTLIGHT";
//		try {
//			Class.forName(driver).newInstance();
//			this.conn = (Connection) DriverManager.getConnection(url, userName, password);
//		} catch (Exception sqle) {
//			sqle.printStackTrace();
//		}
//	}
//
//	/**
//	 *
//	 * @return MysqlConnect Database connection object
//	 */
//	public static synchronized DBConnect getDbCon() {
//		if (db == null) {
//			db = new DBConnect();
//		}
//		return db;
//
//	}
//
//	/**
//	 *
//	 * @param query
//	 *            String The query to be executed
//	 * @return a ResultSet object containing the results or null if not
//	 *         available
//	 * @throws SQLException
//	 */
//	public ResultSet query(String query) throws SQLException {
//		statement = db.conn.createStatement();
//		ResultSet res = statement.executeQuery(query);
//		return res;
//	}
//
//	/**
//	 * @desc Method to insert data to a table
//	 * @param insertQuery
//	 *            String The Insert query
//	 * @return boolean
//	 * @throws SQLException
//	 */
//	public int insert(String insertQuery) throws SQLException {
//		statement = db.conn.createStatement();
//		int result = statement.executeUpdate(insertQuery);
//		return result;
//	}
//
//}