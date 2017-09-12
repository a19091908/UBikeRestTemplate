drop table uBike;

CREATE TABLE uBike(
   sno VARCHAR(4) NOT NULL  PRIMARY KEY,
   sna VARCHAR(100),
   tot INTEGER,
   sbi INTEGER,
   sarea VARCHAR(30),
   mday datetime,
   lat double,
   lng double,
   ar VARCHAR(1000),
   sareaen VARCHAR(100),
   snaen VARCHAR(1000),
   aren VARCHAR(1000),
   bemp INTEGER,
   act VARCHAR(1)
   ); 


/*
"sno" 站點代號 同左
"sna" 場站名稱(中文) 同左
"tot" 場站總停車格 同左
"sbi" 場站目前車輛數量 可借車位數
"sarea" 場站區域(中文) 同左
"mday" 資料更新時間 同左
"lat" 緯度 同左
"lng" 經度 同左
"ar" 地址(中文) 同左
"sareaen" 場站區域(英文) 同左
"snaen" 場站名稱(英文) 同左
"aren" 地址(英文) 同左
"bemp" 空位數量 可還車位數
"act" 全站禁用狀態 場站暫停營運
*/