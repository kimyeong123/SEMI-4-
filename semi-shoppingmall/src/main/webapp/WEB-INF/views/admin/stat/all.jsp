<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<jsp:include page="/WEB-INF/views/template/header.jsp"></jsp:include>

    <style>

    </style>

    <!-- chartjs cdn -->
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>

    <script type="text/javascript">
        $(function () {

            //bar, doughnut, line, pie
            createChart("http://localhost:8080/rest/admin/stat/join", "#join-chart", "line", "가입자수")
            createChart("http://localhost:8080/rest/admin/stat/member", "#member-chart", "bar", "구매 개수")
            createChart("http://localhost:8080/rest/admin/stat/product", "#product-chart", "bar", "판매 개수")
            createChart("http://localhost:8080/rest/admin/stat/status", "#status-chart", "bar", "상품 개수")

            //차트를 생성하는 함수
            function createChart(url, selector, chartType, label) {
                $.ajax({
                    url: url,
                    method: "post",
                    //data 없음
                    success: function (response) {
                        var labels = [], data = [];
                        for (var i = 0; i < response.length; i++) {
                            labels[i] = response[i].title;//labels.push(response[i].title);
                            data[i] = response[i].value;//data.push(response[i].value);
                        }
                        //차트를 이곳에서 생성
                        new Chart($(selector)[0], {
                            type: chartType,//차트 유형(bar/line/pie/doughnut)
                            //차트의 데이터 및 라벨
                            data: {
                                //라벨은 x축의 제목
                                labels: labels,
                                //데이터셋은 실제로 차트에 표시할 데이터"들"
                                datasets: [
                                    //첫 번째 데이터셋
                                    {
                                        label: label,//데이터의 이름(제목)
                                        data: data,//실제 데이터 수치
                                        borderWidth: 3, //테두리 두께(px)
                                    },
                                ]
                            },
                            //차트의 옵션
                            options: {
                                scales: {
                                    y: {
                                        beginAtZero: true,//y축을 반드시 0부터 시작하도록 처리
                                    }
                                }
                            }
                        });
                    }
                });
            }
        });
    </script>
</head>

<body>
    <div class="container w-800">
        <div class="cell center">
            <h1>홈페이지 현황</h1>
        </div>

        <!-- 차트 영역 -->
    <div class="cell">
      <div class="flex-box">
        <div class="w-100">
          <!-- 포켓몬 현황 -->
          <div class="cell center">
            <h2>일자별 가입자수</h2>
          </div>
          <div class="cell">
            <canvas id="join-chart"></canvas>
          </div>

          <!-- 도서 현황 -->
          <div class="cell center mt-50">
            <h2>많이 구매한 이용자</h2>
          </div>
          <div class="cell">
            <canvas id="member-chart"></canvas>
          </div>
        </div>

        <div class="w-100">
          <!-- 학생 등록 현황 -->
          <div class="cell center">
            <h2>많이 구매된 제품</h2>
          </div>
          <div class="cell">
            <canvas id="product-chart"></canvas>
          </div>

          <!-- 회원 현황 -->
          <div class="cell center mt-50">
            <h2>주문 현황 그래프</h2>
          </div>
          <div class="cell mb-50">
            <canvas id="status-chart"></canvas>
          </div>
        </div>
      </div>
    </div>
    </div>
</body>


<jsp:include page="/WEB-INF/views/template/footer.jsp"></jsp:include>