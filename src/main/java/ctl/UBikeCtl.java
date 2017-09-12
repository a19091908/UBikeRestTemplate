package ctl;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.zip.GZIPInputStream;
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
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

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
	private static SSLConnectionSocketFactory csf;
	private static CloseableHttpClient httpClient = null;
	private static HttpComponentsClientHttpRequestFactory requestFactory = null;
	private static RestTemplate restTemplate = null;

	// 讓 http開頭的URL可以被存取
	static {
		TrustStrategy acceptingTrustStrategy = (X509Certificate[] chain, String authType) -> true;

		SSLContext sslContext = null;
		try {
			sslContext = org.apache.http.ssl.SSLContexts.custom().loadTrustMaterial(null, acceptingTrustStrategy)
					.build();
		} catch (KeyManagementException | NoSuchAlgorithmException | KeyStoreException e) {
			e.printStackTrace();
		}

		csf = new SSLConnectionSocketFactory(sslContext);

		httpClient = HttpClients.custom().setSSLSocketFactory(csf).build();

		requestFactory = new HttpComponentsClientHttpRequestFactory();

		requestFactory.setHttpClient(httpClient);

		restTemplate = new RestTemplate(requestFactory);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		//request編碼 UTF-8
		request.setCharacterEncoding("utf-8");
		
		String action = request.getParameter("action");

		switch (action) {
		case "insertUbikeNewTaipei":// 新增新北UBike
			page = doInsertNewTaipei(request);
			break;
		case "insertUbikeTaipei":// 新增台北UBike
			page = doInsertTaipei(request);
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

	private String doInsertTaipei(HttpServletRequest request) {
		List<Ubike> uBikeList = new ArrayList<Ubike>();
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
				.registerTypeAdapter(Integer.class, new EmptyStringToNumberTypeAdapter())
				.registerTypeAdapter(double.class, new EmptyStringToNumberTypeAdapter())
				// .serializeNulls()
				.create();

		// String 轉成 JSON Object
		JsonObject jsonObject = gson.fromJson(jsonString, JsonObject.class);

		// 轉成Set以便找出Key所對之Value
		Set<Entry<String, JsonElement>> keySet = jsonObject.get("retVal").getAsJsonObject().entrySet();
		System.out.println("keySize:" + keySet.size());

		// 取得每一站的資料
		// 用GSON自動將JSON資料對照進 UBike的 Model中
		// 並存入uBikeList中
		for (Entry<String, JsonElement> eachData : keySet) {
			Ubike ubike = gson.fromJson(eachData.getValue(), Ubike.class);
			uBikeList.add(ubike);
		}

		int insertNum = ubikeDAO.insert(uBikeList);

		// 新增數量放入request中
		request.setAttribute("insertNum", insertNum);

		return "index.jsp";
	}

	protected String doInsertNewTaipei(HttpServletRequest request) {
		List<Ubike> uBikeList = new ArrayList<Ubike>();

		// 設定新北市UBike的URL
		url = "http://data.ntpc.gov.tw/api/v1/rest/datastore/382000000A-000352-001";

		// 使用restTemplate抓取Json資料
		String jsonString = restTemplate.getForObject(url, String.class);

		// 設定JSON給定的日期格式
		// 並讓null的Integer,double變成0
		Gson gson = new GsonBuilder().setDateFormat("yyyyMMddHHmmss")
				.registerTypeAdapter(Integer.class, new EmptyStringToNumberTypeAdapter())
				.registerTypeAdapter(double.class, new EmptyStringToNumberTypeAdapter())
				// .serializeNulls()
				.create();

		// String 轉成 JSON Object
		JsonObject jsonObject = gson.fromJson(jsonString, JsonObject.class);

		// 取得所有站點資料
		JsonArray dataArray = jsonObject.get("result").getAsJsonObject().get("records").getAsJsonArray();
		System.out.println("Length:" + dataArray.size());

		// 用GSON自動將JSON資料對照進 Ubike的 Model中
		// 並存入uBikeList中
		for (int i = 0; i < dataArray.size(); i++) {
			Ubike ubike = gson.fromJson(dataArray.get(i), Ubike.class);
			uBikeList.add(ubike);
		}

		// 新增JSON資料
		int insertNum = ubikeDAO.insert(uBikeList);

		// 新增數量放入request中
		request.setAttribute("insertNum", insertNum);

		return "index.jsp";
	}

	protected String doSearch(HttpServletRequest request) {
		
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
