package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

import com.vividsolutions.jts.io.WKBWriter;

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
		builder.append(",mday,poi,ar,sareaen,snaen,aren,bemp,act )");
		builder.append("values (?,?,?,?,?,?,PointFromWKB(?),?,?,?,?,?,?)");
		WKBWriter writer = new WKBWriter();

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
				pstmt.setBytes(7, writer.write(ubike.getPoi()));
				pstmt.setString(8, ubike.getAr());
				pstmt.setString(9, ubike.getSareaen());
				pstmt.setString(10, ubike.getSnaen());
				pstmt.setString(11, ubike.getAren());
				pstmt.setInt(12, ubike.getBemp());
				pstmt.setString(13, ubike.getAct());
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
	 * 更新資料
	 */
	public int update(List<Ubike> ubikeList) {
		StringBuilder builder = new StringBuilder();

		// 更新 總停車格、可借車位數
		// 資料更新時間、可還車位數、是否運行
		builder.append("update Ubike set tot =?,sbi=?");
		builder.append(",mday=?,bemp=?,act=? ");
		builder.append("where sno=?");
		try {
			this.conn = ConnectionHelper.getConnection();
			pstmt = conn.prepareStatement(builder.toString());

			for (int i = 0; i < ubikeList.size(); i++) {
				Ubike ubike = ubikeList.get(i);
				pstmt.setInt(1, ubike.getTot());
				pstmt.setInt(2, ubike.getSbi());
				pstmt.setTimestamp(3, new java.sql.Timestamp(ubike.getMday().getTime()));
				pstmt.setInt(4, ubike.getBemp());
				pstmt.setString(5, ubike.getAct());
				pstmt.setString(6, ubike.getSno());

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

		// 搜尋站點代號、名稱、地區、地址、可借車輛、可還車位、資料更新時間、是否運行
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
				// 時間字串修改為UTC+8時間字串
				array[6] = getUTCTimeFromString(array[6]);
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
	 * 將時間字串轉換成UTC+8時間字串 
	 * 格式為 yyyy-MM-dd HH:mm:ss
	 */
	private String getUTCTimeFromString(String updateTime) {
		StringBuilder builder = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		try {
			sdf.setTimeZone(TimeZone.getTimeZone("UTC+8"));
			Date date = sdf.parse(updateTime);
			
			Calendar calendar = new GregorianCalendar();
			calendar.setTime(date);

			builder = new StringBuilder();

			// 串接成所要格式
			builder.append(calendar.get(Calendar.YEAR) + "-");
			builder.append(String.format("%02d", (calendar.get(Calendar.MONTH) + 1)) + "-");
			builder.append(String.format("%02d",calendar.get(Calendar.DAY_OF_MONTH)) + " ");
			builder.append(String.format("%02d",calendar.get(Calendar.HOUR_OF_DAY)) + ":");
			builder.append(String.format("%02d",calendar.get(Calendar.MINUTE)) + ":");
			builder.append(String.format("%02d",calendar.get(Calendar.SECOND)));
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return builder.toString();
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
