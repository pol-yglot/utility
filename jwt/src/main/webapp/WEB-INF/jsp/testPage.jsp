<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>JWT API Test Page</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 20px; }
        .section { margin-bottom: 30px; border: 1px solid #ccc; padding: 20px; }
        form { margin-bottom: 10px; }
        label { display: inline-block; width: 100px; }
        input[type="text"] { width: 300px; }
        button { padding: 5px 10px; }
        .result { margin-top: 10px; padding: 10px; background-color: #f0f0f0; }
    </style>
</head>
<body>
    <h1>JWT API Test Page</h1>

    <div class="section">
        <h2>Generate JWT Token</h2>
        <form action="${pageContext.request.contextPath}/jwt/generateForm" method="post">
            <label for="subject">Subject:</label>
            <input type="text" id="subject" name="subject" placeholder="Enter subject (e.g., user123)" required>
            <button type="submit">Generate Token</button>
        </form>
        <div class="result">
            <strong>Result:</strong> ${generateResult != null ? generateResult : 'No result yet'}
        </div>
    </div>

    <div class="section">
        <h2>Validate JWT Token</h2>
        <form action="${pageContext.request.contextPath}/jwt/validateForm" method="post">
            <label for="token">Token:</label>
            <input type="text" id="token" name="token" placeholder="Enter JWT token" required>
            <button type="submit">Validate Token</button>
        </form>
        <div class="result">
            <strong>Result:</strong> ${validateResult != null ? validateResult : 'No result yet'}
        </div>
    </div>

    <div class="section">
        <h2>Get Subject from JWT Token</h2>
        <form action="${pageContext.request.contextPath}/jwt/subjectForm" method="post">
            <label for="token">Token:</label>
            <input type="text" id="token" name="token" placeholder="Enter JWT token" required>
            <button type="submit">Get Subject</button>
        </form>
        <div class="result">
            <strong>Result:</strong> ${subjectResult != null ? subjectResult : 'No result yet'}
        </div>
    </div>

    <div class="section">
        <h2>Instructions</h2>
        <ul>
            <li><strong>Generate Token:</strong> Enter a subject (e.g., user ID) and click "Generate Token" to create a new JWT token valid for 10 minutes.</li>
            <li><strong>Validate Token:</strong> Enter a JWT token and click "Validate Token" to check if it's expired or valid.</li>
            <li><strong>Get Subject:</strong> Enter a JWT token and click "Get Subject" to extract the subject from the token.</li>
        </ul>
        <p>Note: Tokens expire after 10 minutes. You can test expiration by waiting or using an old token.</p>
    </div>
</body>
</html>
