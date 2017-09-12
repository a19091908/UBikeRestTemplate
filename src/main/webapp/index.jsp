<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<link
	href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css"
	rel="stylesheet">
<link href="css/index.css" rel="stylesheet">
<script src="https://code.jquery.com/jquery-3.2.1.min.js"></script>
</head>
<body>
	<h2>UBike</h2>
	<hr>
	<div>
		<form action="./UBikeCtl?action=insertUbikeNewTaipei" method="post">
			<button type="submit">新北新增資料</button>
		</form>
	</div>
	<div>
		<form action="./UBikeCtl?action=insertUbikeTaipei" method="post">
			<button type="submit">台北新增資料</button>
		</form>
	</div>
	<hr>
	<form action="./UBikeCtl?action=searchUbike" method="post">
		<div class="tName">
			<b>站點資料查詢:</b>
		</div>
		<input type="text" name="place" id="place" placeholder="輸入站點地區(如:新莊 )">
		<button type="submit">送出</button>
	</form>

	<!-- 新增資料 -->
	<c:if test="${not empty insertNum}">
		<script type="text/javascript">
			alert('新增${insertNum}筆資料成功');
		</script>
	</c:if>
	<!--搜尋資料 -->
	<c:if test="${not empty searchList}">
		<table>
			<thead>
				<tr>
					<th>代號</th>
					<th>名稱</th>
					<th>地區</th>
					<th>地址</th>
					<th>可借車輛</th>
					<th>可還車位</th>
					<th>更新時間</th>
					<th>狀態</th>
				</tr>
			</thead>
			<c:forEach var="searchData" items="${searchList}">
				<!--每筆資料 8個欄位 -->
				<tr>
					<c:forEach var="i" begin="0" end="6">
						<td>${searchData[i]}</td>
					</c:forEach>
					<!-- 站點狀態 searchData[欄位-1] -->
					<c:choose>
						<c:when test="${searchData[7]==1}">
							<td>正常</td>
						</c:when>
						<c:otherwise>
							<td class="status_error">暫停</td>
						</c:otherwise>
					</c:choose>
				</tr>
			</c:forEach>
		</table>
	</c:if>



</body>

</html>

