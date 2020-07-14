# OnePoint

## 서비스 개요 
*계약을 맺은 가맹점과 고객간의 포인트를 적립/사용 하는 회원포인트 시스템?? 

## Micro Serive 소개 
* Billing
	* 가맹점 별 월 단위 정산을 도와주는 시스템 
	* 주요기능 : 정산년월, 정산가맹점 번호를 입력받아 해당 정산년월의 정산금액을 반환  
* BillingAmountView
	*  정산을 위해 거래 발생 시, 실패하지 않은 사용 거래에 대해서만 Data를 쌓아 놓은 View 
* Point
	* 회원 별 보유포인트 금액 을 관리하는 시스템 
	* Deal 시스템에서 성공 거래가 일어날 시, 보유포인트를 증가/감소 시켜 줌 
* Member
	* 회원 정보 관리 시스템으로 보유 포인트에 대한 정보가 아닌 전화번호, 이메일주소 등 변경이 잦지 않은 회원정보를 관리하는 시스템
	* 회원가입 시 Point 시스템에 회원-Point 생성을 요청 함 
	* 회원 탈퇴 시 Point 시스템에 회원-Point 삭제를 요청 함 
* Deal 
	* 거래관리 시스템. 성공/실패 거래를 모두 관리하는 시스템. 
	* 적립거래 발생 시 Point 시스템에 적립된 금액 만큼에 대한 보유포인트 증가를 요청함 
	* 사용거래 발생 시 Point 시스템과 동기 통신을 통해 사용가능한 포인트인지 확인하고, 거래 성공 시 Point 시스템에 보유포인트 차감을 요청함  
	* 사용거래 발생 시 거래가 성공인 case에 대해서만 BillingAmountView 시스템에 거래를 누적시킴

##

## 테스트 시나리오 
1. 회원을 등록/ 확인

|요청 | Point | status |dealDate |
|-------------------------------------------------------------------------------------------------|:-------:|------:|------------:|
|http POST http://localhost:8082/members name="안서연" phone="01011111111" address="용인시 기흥구 구갈동" | | | |
|http POST http://localhost:8082/members name="김준혁" phone="0102223111" address="경기도 하남시"        | | | |  
|http get http://localhost:8082/members/1                                                             | | | |
|http get http://localhost:8082/members/2                                                             | | | |

2. 적립거래 발생 : memberId 0001 사용자가 적립거래를 10000원 발생 적립률(0.01)은 고정이고 거래금액*적립률 만큼의 Point가 쌓임. 

|요청 | Point | status |dealDate |
|-------------------------------------------------------------------------------------------------|:-------:|------:|------------:|
|http POST http://localhost:8085/deals memberId=0001 merchantId=20 dealAmount=100000 type="save"                     |1000원| | |
|http GET http://localhost:8081/points/0001                                                                          |1000원| | | 
|http GET http://localhost:8085/deals/1                                                                              |1000원| | | 
|http get http://localhost:8083/billingAmountViews/1                                                                 | | | |
|http http://localhost:8083/billingAmountViews/1 (Id는 deal을 따라감)                                     		 | | | |

3. 적립거래 취소

|요청 | Point | status |dealDate |
|-------------------------------------------------------------------------------------------------|:-------:|------:|------------:|
|http POST http://localhost:8085/deals memberId=0001 merchantId=20 point=1000 type="saveCancel" 		     | | | |
|http GET http://localhost:8081/points/0001 : 다시 원복되는지 확인 (0원) 						     | | | |
|http GET http://localhost:8085/deals/2 									     | | | |
|http http://localhost:8083/billingAmountViews/2								     | | | |


4. 적립거래 발생  memberId 0001 사용자가 적립거래를 10000원 발생 적립률(0.01)은 고정이고 거래금액*적립률 만큼의 Point가 쌓임 

|요청 | Point | status |dealDate |
|-------------------------------------------------------------------------------------------------|:-------:|------:|------------:|
|http POST http://localhost:8085/deals memberId=0001 merchantId=20 dealAmount=100000 type="save"  			| | | |
|http GET http://localhost:8081/points/0001  :  다시 천원 		                          			     | | | |
|http GET http://localhost:8085/deals/3 		                                          			 | | | |
|http http://localhost:8083/billingAmountViews/3 		                                  			 | | | |


5. 사용거래 발생  memberId 0002 사용자가 사용거래를 발생 :100 원 사용 

|요청 | Point | status |dealDate |
|-------------------------------------------------------------------------------------------------|:-------:|------:|------------:|
|http POST http://localhost:8085/deals memberId=0001 merchantId=20 point=100 type="use" 				| | | |
|http GET http://localhost:8081/points/0001 : 100원 써서 900원 							     | | | |
|http GET http://localhost:8085/deals/4  point=" 200" 									| | | |
|http GET http://localhost:8083/billingAmountViews/4 									| | | |


6. 사용거래 발생 : 가진포인트보다 많이 쓰려고 하는 경우 

|요청 | Point | status |dealDate |
|-------------------------------------------------------------------------------------------------|:-------:|------:|------------:|
|http POST http://localhost:8085/deals memberId=0001 merchantId=20 point=10000 type="use" 				| | | |
|http GET http://localhost:8081/points/0001 : 여전히 900원 ( 못썻으니까) 						 | | | |
|http GET http://localhost:8085/deals/5 : fail 거리 남 								      | | | |
|http GET http://localhost:8083/billingAmountViews/5 // fail 거래는 view에 쌓지 않음 404 에러 				| | | |


7. 정산요청 

|요청 | Point | status |dealDate |
|-------------------------------------------------------------------------------------------------|:-------:|------:|------------:|
|http POST http://localhost:8084/billings mercharntId=20 billingMonth="202007"   98원 정산 금액 나옴 			| | | |
|http GET http://localhost:8085/deals/4 // 사용거래 ! 이므로 정산 여부 yes로 바꾼다 					  | | | |


8. 회원탈퇴 : 아래 회원 상태가다 바뀌어야 됨

|요청 | Point | status |dealDate |
|-------------------------------------------------------------------------------------------------|:-------:|------:|------------:|
|http DELETE http://localhost:8082/members/1 										| | | |
|http GET http://localhost:8083/billingAmountViews/1 									| | | |
|http GET http://localhost:8083/billingAmountViews/2 									| | | |
|http GET http://localhost:8083/billingAmountViews/3 									| | | |
|http GET http://localhost:8083/billingAmountViews/4 									| | | |
