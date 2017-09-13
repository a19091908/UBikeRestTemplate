drop table uBike;

CREATE TABLE uBike(
   sno VARCHAR(4) NOT NULL  PRIMARY KEY,
   sna VARCHAR(100),
   tot INTEGER,
   sbi INTEGER,
   sarea VARCHAR(30),
   mday DATETIME,
   poi POINT,
   ar VARCHAR(1000),
   sareaen VARCHAR(100),
   snaen VARCHAR(1000),
   aren VARCHAR(1000),
   bemp INTEGER,
   act VARCHAR(1)
   ); 
   
/*
sno：站點代號、
sna：場站名稱(中文)
tot：場站總停車格
sbi：場站目前車輛數量
sarea：場站區域(中文)
mday：資料更新時間
lat：緯度
lng：經度
ar：地(中文)
sareaen：場站區域(英文)
snaen：場站名稱(英文)
aren：地址(英文)
bemp：空位數量
act：全站禁用狀態
*/