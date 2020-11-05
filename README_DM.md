# moappteamproject
모앱 공공데이터활용 Data Management 기술 문서

==Usage==   
-안심식당 위치 정보를 로드할 때  
*FetchItemTask ft = new FetchItemTask();*  
*ft.execute();*  

-사용자 주변의 식당 정보를 얻고 싶을 때   
*getRestaurants(Location location)*     
추후 구체화할 예정  

-검색 조건을 이용해 식당 정보를 얻고 싶을 때  
*getRestaurants(Condition condition)*   
같은 method를 overload해서 사용. 추후 구체화할 예정

==요청 URL==  
*"http://211.237.50.150:7080/openapi/"+ APIKEY + "/xml/Grid_20200713000000000605_1/"+startIndex+"/"+endIndex+"?&RELAX_USE_YN=Y"*  
각자의 실행환경에서는 APIKEY를 알맞게 넣어서 요청해야 합니다.  
URL 마지막의 RELAX_USE_YN = Y의 조건은 안심식당에서 등록해제된 식당을 제외하라는 의미이며 이런 방식으로 조건을 추가할 수 있습니다.  
또한 검색할 수 있는 최대 길이는 1000입니다. (endIndex-startIndex<1000)  

==Jsoup==   
Jsoup를 이용해 조건을 뒤에 붙여서 GET방식으로 xml파일을 요청합니다.  
응답할 경우 result tag에는 검색 조건에 맞는 식당의 entity가 몇 개인지 나타냅니다.  
따라서 우선 처음에는 startindex와 endIndex를 1로 설정한 뒤 총 몇 개의 entity가 있는지 확인하고 이후에 모든 정보를 가져옵니다.   
xml의 format은 공공데이터 기술 문서에 설명되어 있으므로 이를 참조하기 바랍니다.(https://data.mafra.go.kr/opendata/data/indexOpenDataDetail.do?data_id=20200713000000001391&filter_ty=O&getBack=&sort_id=regist_dt&s_data_nm=&instt_id=201410120001&cl_code=&shareYn=)   

==해결할 문제==   
사용자의 위치와 식당의 주소의 형식이 다르므로 이를 변환할 방법이 필요함.     
사용자의 위치를 기반으로 거리를 얼마나 설정하고 계산할 것인지 결정해야 함.

