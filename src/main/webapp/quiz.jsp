<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Word Quiz</title>
    <link rel="stylesheet" type="text/css" href="css/style.css">
</head>
<body>
    <div class="container">
        <h1>Word Quiz</h1>
        
        <div class="progress">
            <p>Question ${currentQuestion} / 10</p>
        </div>

        <c:choose>
            <c:when test="${not empty currentWord}">
                <div class="question-section">
                    <h2>What's the English word for this Japanese word?</h2>
                    <div class="japanese-word">
                        ${currentWord.japaneseWord}
                    </div>

                    <%-- show hint --%>
                    <c:if test="${showHint}">
                        <div class="hint">
                            <strong>Hint:</strong> ${currentWord.hint}
                        </div>
                    </c:if>
                
                    <%-- show error message --%>
                    <c:if test="${not empty errorMessage}">
                        <div class="error-message">
                            ${errorMessage}
                        </div>
                    </c:if>

                    <%-- already answerd --%>
                    <c:choose>
                        <c:when test="${answered}">
                            <div class="answer-result">
                                <p>Your answer: <strong>${userAnswer}</strong></p>
                                <p>Correct answer: <strong>${currentWord.englishWord}</strong></p>
                                <div class="result ${isCorrect ? 'correct' : 'incorrect'}">
                                    ${isCorrect ? 'Correct!' : 'Incorrect.'}
                                </div>
                            </div>

                        <div class="action-buttons">
                            <c:choose>
                                <c:when test="${currentQuestion < 10}">
                                    <a href="WordController?action=next" class="btn btn-primary">Next Question</a>
                                </c:when>
                                <c:otherwise>
                                    <a href="WordController?action=result" class="btn btn-primary">View Results</a>
                                </c:otherwise>
                            </c:choose>
                        </div>

                    <%-- Not answerd yet--%> 
                        <c:otherwise>
                            <form method="post" action="WordController">
                                <input type="hidden" name="action" value="check">
                                <div class="input-section"><input type="text" name="answer" id="answer" placeholder="Enter in English" required>
										<div class="button-group">
											<button type="submit" class="btn btn-primary">Check</button>
											<button type="submit" name="action" value="hint" class="btn btn-secondary">Hint</button>
                                    	</div>
                                    </div>
								</div>
                            </form>
                        </c:otherwise>
                    </c:choose>
                </div>
            </c:when>
            <c:otherwise>
                <div class="error-section">
                    <p>Failed to load question.</p>
                    <a href="WordController?action=start" class="btn btn-primary">Start from Beginning</a>
                </div>
            </c: otherwise>
        </c:choose>
        
        <div class="navigation">
            <a href="index.jsp" class="btn btn-link">Back to Home</a>
        </div>
    </div>
    
    <script>
        // Set focus to the input field
        document.addEventListener('DOMContentLoaded', function() {
            const answerInput = document.getElementById('answer');
            if (answerInput) {
                answerInput.focus();
            }
        });
    </script>
</body>
</html>