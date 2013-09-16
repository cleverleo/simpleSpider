package net.cleverleo.common;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class Model {
	private String tableName;
	private Statement db;
	private String where = "";
	private String order = "";

	private static Map<String, Set<String>> _fieldsmc = new HashMap<String, Set<String>>();

	private Set<String> fields = null;
	private String limit = "";

	public Model(String table) throws SQLException {
		this.tableName = table;
		this.db = Database.getStatement();
		this.clean();
	}

	public void close() throws SQLException {
		this.db.close();
	}

	public int add(Map<String, String> data) throws SQLException {
		this.columns();
		String keys = null, values = null;

		Iterator<String> it = this.fields.iterator();
		while (it.hasNext()) {
			String key = it.next();

			if (keys == null) {
				keys = '`' + key + '`';
				String value = data.get(key);
				values = value == null ? "null"
						: ("'" + this.safeCode(value) + "'");
			} else {
				keys += ",`" + key + '`';
				String value = data.get(key);
				values += ","
						+ (value == null ? "null"
								: ("'" + this.safeCode(value) + "'"));
			}
		}

		String sql = String.format("INSERT INTO %s (%s) VALUES (%s)",
				this.tableName, keys, values);
		// System.out.println(sql);
		this.db.executeUpdate(sql);
		ResultSet rs = this.db.executeQuery("SELECT LAST_INSERT_ID( )");
		rs.next();
		return rs.getInt(1);
	}

	public ResultSet select() throws SQLException {
		this.columns();

		String keys = null;
		Iterator<String> it = this.fields.iterator();
		while (it.hasNext()) {
			String key = it.next();
			if (keys == null) {
				keys = "`" + key + "`";
			} else {
				keys += ",`" + key + "`";
			}
		}

		String sql = String.format("SELECT %s FROM %s "
				+ (this.where.length() == 0 ? "" : " WHERE " + this.where)
				+ (this.order.length() == 0 ? "" : " ORDER BY " + this.order)
				+ (this.limit.length() == 0 ? "" : " LIMIT " + this.limit),
				keys, this.tableName);

		ResultSet rs = this.db.executeQuery(sql);
		this.clean();
		return rs;
	}

	public ResultSet find() throws SQLException {
		this.columns();
		ResultSet data = this.limit(0, 1).select();
		return data.next() ? data : null;
	}

	public Model where(String key, String value) {
		if (this.where.equals("")) {
			this.where = key + "='" + this.safeCode(value) + "'";
		} else {
			this.where += " AND " + key + "='" + this.safeCode(value) + "'";
		}
		return this;
	}

	public Model where(String key, String value, String logic) {
		if (this.where.equals("")) {
			this.where = key + "='" + this.safeCode(value) + "'";
		} else {
			this.where += " " + logic + " " + key + "='" + this.safeCode(value)
					+ "'";
		}
		return this;
	}

	public Model where(String key, String value, boolean fun) {
		if (!fun)
			return this.where(key, value);

		if (this.where.equals("")) {
			this.where = key + "='" + this.safeCode(value) + "'";
		} else {
			this.where += " AND " + key + "='" + this.safeCode(value) + "'";
		}
		return this;
	}

	public Model where(String key, String value, String logic, boolean fun) {
		if (!fun)
			return this.where(key, value, logic);

		if (this.where.equals("")) {
			this.where = key + "='" + this.safeCode(value) + "'";
		} else {
			this.where += " " + logic + " " + key + "='" + this.safeCode(value)
					+ "'";
		}
		return this;
	}

	public Model where(String where) {
		this.where = where;
		return this;
	}

	public Model order(String order) {
		this.order = order;
		return this;
	}

	private void columns() throws SQLException {
		if (this.fields == null
				&& (this.fields = Model._fieldsmc.get(this.tableName)) == null) {

			this.fields = new HashSet<String>();

			String sql = String.format("SHOW COLUMNS FROM %s", this.tableName);
			ResultSet rs = this.db.executeQuery(sql);
			while (rs.next()) {
				this.fields.add(rs.getString(1));
			}
			Model._fieldsmc.put(this.tableName, this.fields);
		}
	}

	public Model limit(int start, int per) {
		this.limit = String.format("%s,%s", start, per);
		return this;
	}

	public void clean() {
		this.where = "";
		this.order = "";
	}

	private String safeCode(String str) {
		return str.replace("\\", "\\\\").replace("\'", "\\\'")
				.replace("\"", "\\\"").replace("\n", "\\n")
				.replace("\t", "\\t");
	}

	public int save(Map<String, String> data) throws SQLException {
		Iterator<String> it = data.keySet().iterator();
		String update = null;
		while (it.hasNext()) {
			String key = it.next();
			String value = data.get(key);
			if (update == null) {
				update = String.format("`%s`='%s'", key, this.safeCode(value));
			} else {
				update += String
						.format(",`%s`='%s'", key, this.safeCode(value));
			}
		}

		String sql = String.format("UPDATE %s SET %s WHERE %s", this.tableName,
				update, this.where);
		return this.db.executeUpdate(sql);

	}

	public int setField(String key, String value) throws SQLException {
		HashMap<String, String> data = new HashMap<String, String>();
		data.put(key, value);
		return this.save(data);
	}

	public int count() throws SQLException {
		String sql = String.format("SELECT COUNT(*) FROM %s WHERE %s",
				this.tableName, this.where);
		ResultSet rs = this.db.executeQuery(sql);
		rs.next();
		return rs.getInt(1);
	}
}