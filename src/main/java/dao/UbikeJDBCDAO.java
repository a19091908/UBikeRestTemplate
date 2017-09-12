package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import model.Ubike;

public class UbikeJDBCDAO {
	Connection conn = null;
	Statement stmt = null;
	PreparedStatement pstmt = null;
	ResultSet rs = null;

	/*
	 * 新增Ubike資料
	 */
	public int insert(List<Ubike> ubikeList) {
		StringBuilder builder = new StringBuilder();
		builder.append("insert into Ubike (sno,sna , tot ,sbi ,sarea");
		builder.append(",mday,lat,lng,ar,sareaen,snaen,aren,bemp,act )");
		builder.append("values (?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

		try {
			this.conn = ConnectionHelper.getConnection();
			pstmt = conn.prepareStatement(builder.toString());

			for (int i = 0; i < ubikeList.size(); i++) {
				Ubike ubike = ubikeList.get(i);
				pstmt.setString(1, ubike.getSno());
				pstmt.setString(2, ubike.getSna());
				pstmt.setInt(3, ubike.getTot());
				pstmt.setInt(4, ubike.getSbi());
				pstmt.setString(5, ubike.getSarea());
				pstmt.setTimestamp(6, new java.sql.Timestamp(ubike.getMday().getTime()));
				pstmt.setDouble(7, ubike.getLat());
				pstmt.setDouble(8, ubike.getLng());
				pstmt.setString(9, ubike.getAr());
				pstmt.setString(10, ubike.getSareaen());
				pstmt.setString(11, ubike.getSnaen());
				pstmt.setString(12, ubike.getAren());
				pstmt.setInt(13, ubike.getBemp());
				pstmt.setString(14, ubike.getAct());
				pstmt.addBatch();
			}
			int i[] = pstmt.executeBatch();
			System.out.println("新增 " + i.length + " 筆資料");

			return i.length;
		} catch (SQLException e) {
			throw new RuntimeException("資料庫錯誤. " + e.getMessage());
		} finally {
			close();
		}
	}

	/*
	 * 搜尋指定地點(英文)
	 */
	public List<String[]> search(String place) {
		List<String[]> searchList = new ArrayList<String[]>();
		StringBuilder builder = new StringBuilder();

		// 搜尋欄位數
		int select = 8; 
			
		//搜尋站點代號、名稱、地區、地址、可借車輛、可還車位、資料更新時間、是否運行
		builder.append("select sno,sna,sarea,ar,sbi,bemp,mday,act ");
		builder.append("from ubike ");
		builder.append("where sarea like ? or sareaen like ?");
		builder.append("order by 1 ");

		try {
			this.conn = ConnectionHelper.getConnection();
			pstmt = conn.prepareStatement(builder.toString());

			pstmt.setString(1, place);
			pstmt.setString(2, place);
			
			rs = pstmt.executeQuery();
			while (rs.next()) {
				String[] array = new String[select];

				for (int i = 0; i < select; i++) {
					array[i] = rs.getString(i + 1);
				}

				searchList.add(array);
			}
			if (searchList.isEmpty()) {
				System.out.println("no data be found");
			}

			return searchList;
		} catch (SQLException e) {
			throw new RuntimeException("資料庫錯誤. " + e.getMessage());
		} finally {
			close();
		}

	}

	/*
	 * 關閉方法
	 */
	private void close() {
		if (rs != null)
			try {
				rs.close();
			} catch (SQLException e) {

				e.printStackTrace(System.err);
			}
		if (stmt != null)
			try {
				stmt.close();
			} catch (SQLException e) {

				e.printStackTrace(System.err);
			}
		if (pstmt != null)
			try {
				pstmt.close();
			} catch (SQLException e) {

				e.printStackTrace(System.err);
			}
		if (conn != null)
			try {
				conn.close();
			} catch (SQLException e) {

				e.printStackTrace(System.err);
			}
	}

}
