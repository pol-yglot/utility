<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Key Info</title>
    <style>
        table { border-collapse: collapse; width: 100%; }
        th, td { border: 1px solid #ddd; padding: 8px; }
        th { background-color: #f2f2f2; }
        .controls { margin: 12px 0; }
        .pager a { margin: 0 4px; }
    </style>
    <script>
        function goPage(p) {
            const params = new URLSearchParams(window.location.search);
            params.set('page', p);
            window.location.search = params.toString();
        }

        function updateExportLink() {
            const status = document.getElementById('status').value;
            const serviceCd = document.getElementById('serviceCd').value;
            const link = document.getElementById('exportLink');
            link.href = '/api/v1/keys/export?status=' + encodeURIComponent(status) + '&serviceCd=' + encodeURIComponent(serviceCd);
        }

        document.addEventListener('DOMContentLoaded', function() {
            document.getElementById('status').addEventListener('change', updateExportLink);
            document.getElementById('serviceCd').addEventListener('change', updateExportLink);
            updateExportLink(); // Initial update
        });
    </script>
</head>
<body>
    <h1>Security Keys</h1>

    <div class="controls">
        <form method="get" action="/corp/keyInfo">
            <label for="status">Filter by status:</label>
            <select id="status" name="status">
                <option value="">-- all --</option>
                <option value="발급" ${status == '발급' ? 'selected' : ''}>발급</option>
                <option value="갱신" ${status == '갱신' ? 'selected' : ''}>갱신</option>
                <option value="폐기" ${status == '폐기' ? 'selected' : ''}>폐기</option>
            </select>
            <input type="hidden" name="size" value="${pageSize != null ? pageSize : 10}" />
            <button type="submit">Apply</button>
            <a href="/corp/keyInfo"><button type="button">Refresh</button></a>
            <a href="/api/v1/keys/export?status=${fn:escapeXml(status)}&serviceCd=${fn:escapeXml(serviceCd)}" style="margin-left:8px;">Download Excel</a>
        </form>
    </div>

    <table id="keysTable">
        <thead>
            <tr>
                <th>id</th>
                <th>serviceCd</th>
                <th>prtnrId</th>
                <th>acntId</th>
                <th>ci</th>
                <th>cs</th>
                <th>ms</th>
                <th>status</th>
                <th>createdAt</th>
                <th>updatedAt</th>
                <th>deletedAt</th>
            </tr>
        </thead>
        <tbody>
        <c:forEach var="k" items="${keys}">
            <tr>
                <td><c:out value="${k.id}"/></td>
                <td><c:out value="${k.serviceCd}"/></td>
                <td><c:out value="${k.prtnrId}"/></td>
                <td><c:out value="${k.acntId}"/></td>
                <td><c:out value="${k.ci}"/></td>
                <td><c:out value="${k.cs}"/></td>
                <td><c:out value="${k.ms}"/></td>
                <td><c:out value="${k.status}"/></td>
                <td><fmt:formatDate value="${k.createdAtAsDate}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
                <td><fmt:formatDate value="${k.updatedAtAsDate}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
                <td><c:out value="${k.deletedAt}"/></td>
            </tr>
        </c:forEach>
        </tbody>
    </table>

    <c:if test="${totalPages != null}">
        <div>
            페이지: ${currentPage + 1} / ${totalPages}
        </div>
        <div class="pager">
            <c:if test="${currentPage > 0}">
                <a href="/corp/keyInfo?page=${currentPage - 1}&size=${pageSize}&status=${fn:escapeXml(status)}">Previous</a>
            </c:if>

            <c:forEach var="i" begin="0" end="${totalPages - 1}">
                <c:choose>
                    <c:when test="${i == currentPage}">
                        <strong>${i + 1}</strong>
                    </c:when>
                    <c:otherwise>
                        <a href="/corp/keyInfo?page=${i}&size=${pageSize}&status=${fn:escapeXml(status)}">${i + 1}</a>
                    </c:otherwise>
                </c:choose>
            </c:forEach>

            <c:if test="${currentPage + 1 < totalPages}">
                <a href="/corp/keyInfo?page=${currentPage + 1}&size=${pageSize}&status=${fn:escapeXml(status)}">Next</a>
            </c:if>
        </div>
    </c:if>

</body>
</html>
