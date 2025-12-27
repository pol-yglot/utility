<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">

    <!-- CSRF (없어도 JS 안 터지게 방어) -->
    <meta name="_csrf" content="${_csrf.token}"/>
    <meta name="_csrf_header" content="${_csrf.headerName}"/>

    <title>Key Info</title>

    <style>
        table { border-collapse: collapse; width: 100%; }
        th, td { border: 1px solid #ddd; padding: 8px; }
        th { background-color: #f2f2f2; }

        .modal {
            display: none;
            position: fixed;
            z-index: 999;
            left: 0; top: 0;
            width: 100%; height: 100%;
            background-color: rgba(0,0,0,0.4);
        }
        .modal-content {
            background-color: #fff;
            margin: 15% auto;
            padding: 20px;
            width: 320px;
            text-align: center;
        }
        .error-message {
            color: red;
            font-size: 12px;
            margin-top: 5px;
        }
    </style>

    <script>
        /* ===============================
           공통 유틸
        =============================== */

        function getCsrfHeaders() {
            var headers = { "Content-Type": "application/json" };

            try {
                var csrfMeta = document.querySelector('meta[name="_csrf"]');
                var csrfHeaderMeta = document.querySelector('meta[name="_csrf_header"]');

                if (csrfMeta && csrfHeaderMeta) {
                    headers[csrfHeaderMeta.getAttribute('content')] =
                        csrfMeta.getAttribute('content');
                }
            } catch (e) {
                console.warn("CSRF meta error", e);
            }

            return headers;
        }

        function safeJson(response) {
            if (!response.ok) {
                throw new Error("HTTP status " + response.status);
            }
            return response.json().catch(function () {
                throw new Error("Invalid JSON response");
            });
        }

        /* ===============================
           페이징
        =============================== */

        function goPage(p) {
            var params = new URLSearchParams(window.location.search);
            params.set("page", p);
            window.location.search = params.toString();
        }

        /* ===============================
           모달 제어
        =============================== */

        function openBusinessNoModal() {
            var modal = document.getElementById("businessNoModal");
            var input = document.getElementById("businessNoInput");
            var error = document.getElementById("errorMessage");

            if (!modal || !input || !error) {
                alert("화면 구성 오류입니다.");
                return;
            }

            input.value = "";
            error.textContent = "";
            modal.style.display = "block";
        }

        function closeModal() {
            var modal = document.getElementById("businessNoModal");
            if (modal) modal.style.display = "none";
        }

        /* ===============================
           사업자번호 검증 + 다운로드
        =============================== */

        function validateAndDownload() {
            var input = document.getElementById("businessNoInput");
            var error = document.getElementById("errorMessage");

            if (!input || !error) return;

            var businessNo = input.value.replace(/\s/g, "");

            if (!businessNo) {
                error.textContent = "사업자번호를 입력해주세요";
                return;
            }

            if (!/^\d{10}$/.test(businessNo)) {
                error.textContent = "사업자번호는 10자리 숫자입니다";
                return;
            }

            fetch("/api/v1/validate-business-no", {
                method: "POST",
                headers: getCsrfHeaders(),
                body: JSON.stringify({ businessNo: businessNo })
            })
            .then(safeJson)
            .then(function (data) {
                // 방어적 접근
                var isValid = data && (data.valid === true || data.isValid === true);

                if (isValid) {
                    closeModal();

                    var statusEl = document.getElementById("status");
                    var status = statusEl ? statusEl.value : "";

                    window.location.href =
                        "/api/v1/keys/export?status=" + encodeURIComponent(status);
                } else {
                    error.textContent = data && data.message
                        ? data.message
                        : "사업자번호를 확인해주세요";
                }
            })
            .catch(function (err) {
                console.error(err);
                error.textContent = "서버 오류가 발생했습니다";
            });
        }

        /* ===============================
           DOM Ready
        =============================== */

        document.addEventListener("DOMContentLoaded", function () {
            var statusEl = document.getElementById("status");
            if (statusEl) {
                statusEl.addEventListener("change", function () {
                    // 필요 시 확장
                });
            }
        });
    </script>
</head>

<body>

<h1>Security Keys</h1>

<form method="get" action="/corp/keyInfo">
    <label>Filter by status:</label>
    <select id="status" name="status">
        <option value="">-- all --</option>
        <option value="발급" ${status == '발급' ? 'selected' : ''}>발급</option>
        <option value="갱신" ${status == '갱신' ? 'selected' : ''}>갱신</option>
        <option value="폐기" ${status == '폐기' ? 'selected' : ''}>폐기</option>
    </select>

    <button type="submit">Apply</button>
    <button type="button" onclick="openBusinessNoModal()">Download Excel</button>
</form>

<table>
    <thead>
        <tr>
            <th>id</th>
            <th>serviceCd</th>
            <th>status</th>
        </tr>
    </thead>
    <tbody>
        <c:forEach var="k" items="${keys}">
            <tr>
                <td>${k.id}</td>
                <td>${k.serviceCd}</td>
                <td>${k.status}</td>
            </tr>
        </c:forEach>
    </tbody>
</table>

<!-- 모달 -->
<div id="businessNoModal" class="modal">
    <div class="modal-content">
        <h3>사업자번호 입력</h3>
        <input id="businessNoInput" type="text" maxlength="10" placeholder="10자리 숫자">
        <div id="errorMessage" class="error-message"></div>
        <button type="button" onclick="closeModal()">닫기</button>
        <button type="button" onclick="validateAndDownload()">확인</button>
    </div>
</div>

<!-- 테스트 사업자번호 안내 -->
<div style="margin-top: 20px; padding: 10px; background-color: #f9f9f9; border: 1px solid #ddd; border-radius: 4px;">
    <strong>테스트용 사업자번호:</strong> 1234567890
</div>

</body>
</html>
