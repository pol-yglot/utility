<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>계약종류별 합계 사용이력 통계</title>
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
    <style>
        body { font-family: Arial, sans-serif; margin: 20px; }
        h1 { color: #333; }
        .controls { margin-bottom: 20px; }
        canvas { max-width: 600px; margin: 20px 0; }
    </style>
</head>
<body>
    <h1>계약종류별 합계 사용이력 통계</h1>
    <div class="controls">
        <label for="period">기간:</label>
        <select id="period">
            <option value="daily">일별</option>
            <option value="monthly">월별</option>
        </select>
        <label for="startDate">시작일:</label>
        <input type="date" id="startDate">
        <label for="endDate">종료일:</label>
        <input type="date" id="endDate">
        <button onclick="updateCharts()">업데이트</button>
    </div>
    <h2>바차트</h2>
    <canvas id="barChart"></canvas>
    <h2>파이차트</h2>
    <canvas id="pieChart"></canvas>

    <script>
        const categories = ['로그인', '전자서명', '본인확인', '마이데이터', '전분야마이데이터'];
        const colors = ['#007bff', '#dc3545', '#28a745', '#ffc107', '#6f42c1'];

        // 샘플 데이터 (합계)
        const sampleData = {
            daily: [500, 300, 200, 150, 100],
            monthly: [15000, 9000, 6000, 4500, 3000]
        };

        let barChart, pieChart;

        function createCharts(data) {
            const ctxBar = document.getElementById('barChart').getContext('2d');
            barChart = new Chart(ctxBar, {
                type: 'bar',
                data: {
                    labels: categories,
                    datasets: [{
                        label: '합계 사용이력',
                        data: data,
                        backgroundColor: colors
                    }]
                }
            });

            const ctxPie = document.getElementById('pieChart').getContext('2d');
            pieChart = new Chart(ctxPie, {
                type: 'pie',
                data: {
                    labels: categories,
                    datasets: [{
                        data: data,
                        backgroundColor: colors
                    }]
                }
            });
        }

        function updateCharts() {
            const period = document.getElementById('period').value;
            const data = sampleData[period];
            if (barChart) barChart.destroy();
            if (pieChart) pieChart.destroy();
            createCharts(data);
        }

        // 초기 로드
        updateCharts();
    </script>
</body>
</html>
