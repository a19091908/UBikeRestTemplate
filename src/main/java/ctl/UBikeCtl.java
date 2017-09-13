package ctl;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.SSLContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.TrustStrategy;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

import dao.UbikeJDBCDAO;
import model.Ubike;
import tools.EmptyStringToNumberTypeAdapter;
import tools.ToStringFromGzipUrl;

/**
 * Servlet implementation class UBikeCtl
 */
public class UBikeCtl extends HttpServlet {
	private static final long serialVersionUID = 1L;
	UbikeJDBCDAO ubikeDAO = new UbikeJDBCDAO();

	private String page = "index.jsp";
	private String url = null;
	private static RestTemplate restTemplate = new RestTemplate();
	HttpHeaders httpHeaders = new HttpHeaders();

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		// request編碼 UTF-8
		request.setCharacterEncoding("utf-8");

		String action = request.getParameter("action");

		switch (action) {
		case "insertUbikeNewTaipei":// 新增新北UBike
			page = doInsertNewTaipei(request);
			break;
		case "updateUbikeNewTaipei":// 更新新北UBike
			page = doUpdateNewTaipei(request);
			break;
		case "insertUbikeTaipei":// 新增台北UBike
			page = doInsertTaipei(request);
			break;
		case "updateUbikeTaipei":// 更新台北UBike
			page = doUpdateTaipei(request);
			break;
		case "searchUbike":// 搜尋UBike站點資料
			page = doSearch(request);
			break;
		default:
			System.out.println("error");
			break;
		}
		request.getRequestDispatcher(page).forward(request, response);
	}

	/*
	 * 取得新北UBike資料List
	 */
	protected List<Ubike> getNewTaipeiList() {
		List<Ubike> uBikeList = new ArrayList<Ubike>();
		GeometryFactory factory = new GeometryFactory();

		//設定表頭瀏覽器使存取URL成功
		httpHeaders.set("User-Agent", "Chrome/60.0.3112.113");
		HttpEntity<String> entity = new HttpEntity<String>(httpHeaders);

		// 設定新北市UBike的URL
		url = "http://data.ntpc.gov.tw/api/v1/rest/datastore/382000000A-000352-001";

		// 使用restTemplate取得response
		// 並取得其body內容
		ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
		String jsonString = responseEntity.getBody();

		// 設定JSON給定的日期格式
		// 並讓null的Integer,double變成0
		Gson gson = new GsonBuilder().setDateFormat("yyyyMMddHHmmss")
				.registerTypeAdapter(Integer.class, new EmptyStringToNumberTypeAdapter()).create();

		// String 轉成 JSON Object
		JsonObject jsonObject = gson.fromJson(jsonString, JsonObject.class);

		// 取得所有站點資料
		JsonArray dataArray = jsonObject.get("result").getAsJsonObject().get("records").getAsJsonArray();
		System.out.println("Length:" + dataArray.size());

		// 用GSON自動將JSON資料對照進 Ubike的 Model中
		// 並存入uBikeList中
		for (int i = 0; i < dataArray.size(); i++) {

			// 將lat和lng以外的資料放入Model中
			Ubike ubike = gson.fromJson(dataArray.get(i), Ubike.class);

			// 另外產生座標(Point)
			// 並放入Model中
			Point poi = factory.createPoint(new Coordinate(dataArray.get(i).getAsJsonObject().get("lng").getAsDouble(),
					dataArray.get(i).getAsJsonObject().get("lat").getAsDouble()));
			ubike.setPoi(poi);

			uBikeList.add(ubike);
		}
		return uBikeList;
	}

	/*
	 * 新增新北資料
	 */
	protected String doInsertNewTaipei(HttpServletRequest request) {

		List<Ubike> uBikeList = getNewTaipeiList();

		// 新增JSON資料
		int insertNum = ubikeDAO.insert(uBikeList);

		// 新增數量放入request中
		request.setAttribute("insertNum", insertNum);

		return "index.jsp";
	}

	/*
	 * 更新新北資料
	 */
	protected String doUpdateNewTaipei(HttpServletRequest request) {

		List<Ubike> uBikeList = getNewTaipeiList();

		int updateNum = ubikeDAO.update(uBikeList);

		// 更新數量放入request中
		request.setAttribute("insertNum", updateNum);
		return "index.jsp";
	}

	/*
	 * 取得台北UBike資料
	 */
	protected List<Ubike> getTaipeiList() {
		List<Ubike> uBikeList = new ArrayList<Ubike>();
		GeometryFactory factory = new GeometryFactory();
		String jsonString = null;
		URL jsonUrl;
		try {
			// 設定台北市UBike的URL
			// 並產生轉換instant
			jsonUrl = new URL("https://tcgbusfs.blob.core.windows.net/blobyoubike/YouBikeTP.gz");
			ToStringFromGzipUrl toStringFromGzipUrl = new ToStringFromGzipUrl(jsonUrl);

			// 將.gz之URL轉換成JSON String
			jsonString = toStringFromGzipUrl.gzipUrlToString();

		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		// 設定JSON給定的日期格式
		// 並讓null的Integer,double變成0
		Gson gson = new GsonBuilder().setDateFormat("yyyyMMddHHmmss")
				.registerTypeAdapter(Integer.class, new EmptyStringToNumberTypeAdapter()).create();

		// String 轉成 JSON Object
		JsonObject jsonObject = gson.fromJson(jsonString, JsonObject.class);

		// 轉成Set以便找出Key所對之Value
		Set<Entry<String, JsonElement>> keySet = jsonObject.get("retVal").getAsJsonObject().entrySet();
		System.out.println("keySize:" + keySet.size());

		// 取得每一站的資料
		// 用GSON自動將JSON資料對照進 UBike的 Model中
		// 並存入uBikeList中
		for (Entry<String, JsonElement> eachData : keySet) {

			// 將lat和lng以外的資料放入Model中
			Ubike ubike = gson.fromJson(eachData.getValue(), Ubike.class);

			// 將資料轉成JSON Object
			JsonObject eachJson = gson.fromJson(eachData.getValue(), JsonObject.class);

			// 另外產生座標(Point)
			// 並放入Model中
			Point poi = factory
					.createPoint(new Coordinate(eachJson.get("lng").getAsDouble(), eachJson.get("lat").getAsDouble()));
			ubike.setPoi(poi);

			uBikeList.add(ubike);
		}
		return uBikeList;
	}

	/*
	 * 新增台北資料
	 */
	protected String doInsertTaipei(HttpServletRequest request) {

		List<Ubike> uBikeList = getTaipeiList();
		int insertNum = ubikeDAO.insert(uBikeList);

		// 新增數量放入request中
		request.setAttribute("insertNum", insertNum);

		return "index.jsp";
	}

	/*
	 * 更新台北資料
	 */
	protected String doUpdateTaipei(HttpServletRequest request) {

		List<Ubike> uBikeList = getNewTaipeiList();

		int updateNum = ubikeDAO.update(uBikeList);

		// 更新數量放入request中
		request.setAttribute("insertNum", updateNum);
		return "index.jsp";
	}

	/*
	 * 搜尋UBike資料
	 */
	protected String doSearch(HttpServletRequest request) {

		// 取得place參數
		String place = request.getParameter("place");

		// place參數若為空 則搜尋所有站點
		place = (place.equals("") ? "%" : "%" + place + "%");
		System.out.println("Query string:" + place);

		// 搜尋資料
		List<String[]> searchList = ubikeDAO.search(place);

		// 搜尋資料放入request中
		request.setAttribute("searchList", searchList);

		return "index.jsp";
	}

}
