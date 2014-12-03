package org.openmrs.module.iris;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class _DbConnection {

	private String username;
	private String password;
	private String url;
	private String driverClassName;
	
	public _DbConnection() {}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getDriverClassName() {
		return driverClassName;
	}

	public void setDriverClassName(String driverClassName) {
		this.driverClassName = driverClassName;
	}

	public Connection getConnection() throws Exception{
		Class.forName(driverClassName);
		// connect using Thin driver
		Connection con =   DriverManager.getConnection(url,username,password);
		return con;
	}
	
	public List<Map<String, Object>> getData(String query, Object[] params) throws Exception{
		Connection con = getConnection();
		PreparedStatement ps = con.prepareStatement(query);
		
		if(params != null)
		for (int i = 0; i < params.length; i++) {
			ps.setObject(i, params[i]);
		}

		ResultSet rs = ps.executeQuery();

		String[] colnames = new String[ps.getMetaData().getColumnCount()];
		for (int i = 0; i < ps.getMetaData().getColumnCount(); i++) {
			colnames[i]=ps.getMetaData().getColumnName(i+1);
		}
		
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		while (rs.next()) {
			Map<String, Object> m = new HashMap<String, Object>();
			
			for (String cn : colnames) {
				m.put(cn, rs.getObject(cn));
			}
			
			result.add(m);
		}
		
		 rs.close();
		 ps.close();
		 con.close();
		
		return result;
	}
}
