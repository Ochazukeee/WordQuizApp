<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Vocabulary Quiz App</title>
    <link rel="stylesheet" type="text/css" href="css/style.css">
</head>
<body>
    <div class="container">
        <h1>Vocabulary Quiz App</h1>
        <div class="welcome-message">
            <p>Look at the Japanese words and enter the corresponding English translation.</p>
            <p>There are 10 questions in total. Good luck!</p>
        </div>
        
        <div class="start-section">
            <a href="WordController?action=start" class="btn btn-primary">Start Quiz</a>
        </div>
        
        <div class="rules">
            <h3>Rules</h3>
            <ul>
                <li>A Japanese word will be displayed</li>
                <li>Enter the corresponding English word</li>
                <li>Use alphabetic characters only</li>
                <li>Click the "Check" button to verify your answer</li>
                <li>Click the "Hint" button to display a hint</li>
                <li>Click the "Next" button to proceed to the next question</li>
                <li>Your results will be displayed after completing all 10 questions</li>
            </ul>
        </div>
    </div>
</body>
</html>