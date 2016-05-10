package edu.pitt.dbmi.deid.comparison.orm;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

public class ConnectivityTester {
	
	private Connection pubDbConnection = null;
	private Connection pvtDbConnection = null;

	public static void main(String[] args) {
		ConnectivityTester connectivityTester = new ConnectivityTester();
		connectivityTester.execute();
	}

	public ConnectivityTester() {
		
	}

	public void execute() {		
		try {
			StringBuilder sb = null;
			
			sb = new StringBuilder();
			sb.append("jdbc:mysql://ties-db.isd.upmc.edu/ties_private?");
			sb.append("user=caties&password=d3m06at135");
			pvtDbConnection = tryConnection(sb.toString());		
			
			sb = new StringBuilder();
			sb.append("jdbc:mysql://ties-db.isd.upmc.edu/ties_public?");
			sb.append("user=caties&password=d3m06at135");
			pubDbConnection = tryConnection(sb.toString());
			
			Statement pubStmt = pubDbConnection.createStatement();
			ResultSet rs = pubStmt.executeQuery("SELECT uuid FROM document where application_status = 'IDLING' limit 50");
			final List<String> pubUuids = new ArrayList<String>();
 			while (rs.next()) {
 				String pubUuid = rs.getString("uuid");
 				pubUuids.add(pubUuid);
 			}
			for (int idx = 0; idx < pubUuids.size(); idx++) {
				String pubUuid = pubUuids.get(idx);
				System.out.println(StringUtils.leftPad(idx+"", 2, "0") + ") " + pubUuid);
			}
			
			String pvtSql = "SELECT uuid FROM ident_document where application_status = 'IDLING' limit 50";
			PreparedStatement pStmt = pvtDbConnection.prepareStatement(pvtSql);

			pStmt.executeQuery();
			
 			pubDbConnection.close();
 			pvtDbConnection.close();
 			
 			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private Connection tryConnection(String rdbmsUrl) throws SQLException {
		Connection conn = DriverManager
				.getConnection(rdbmsUrl);
		if (conn == null) {
			System.err.println("FAILED Connect " + rdbmsUrl);
		} else {
			System.err.println("SUCCESS Connect " + rdbmsUrl);
		}
		return conn;
	}

}
