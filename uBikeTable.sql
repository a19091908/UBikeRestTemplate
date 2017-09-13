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
"sno" վ�c��̖ ͬ��
"sna" ��վ���Q(����) ͬ��
"tot" ��վ��ͣ܇�� ͬ��
"sbi" ��վĿǰ܇�v���� �ɽ�܇λ��
"sarea" ��վ�^��(����) ͬ��
"mday" �Y�ϸ��r�g ͬ��
"lat" ���� ͬ��
"lng" ���� ͬ��
"ar" ��ַ(����) ͬ��
"sareaen" ��վ�^��(Ӣ��) ͬ��
"snaen" ��վ���Q(Ӣ��) ͬ��
"aren" ��ַ(Ӣ��) ͬ��
"bemp" ��λ���� ��߀܇λ��
"act" ȫվ���à�B ��վ��ͣ�I�\
*/