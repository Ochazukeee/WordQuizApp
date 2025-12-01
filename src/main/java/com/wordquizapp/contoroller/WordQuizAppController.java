package com.wordquizapp.contoroller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import com.wordquizapp.model.WordQuizApp;
import com.wordquizapp.model.WordQuizAppDAO;
import com.wordquizapp.validator.WordQuizAppValidator;
import com.wordquizapp.validator.WordQuizAppValidator.ValidationResult;

/**
 * Main controller for the word quiz application.
 */
@WebServlet("/WordQuizAppContoroller")
public class WordQuizAppController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private WordQuizAppDAO wordQuizAppDAO;
	
	// Logger for outputting logs
	private static final Logger logger = Logger.getLogger(WordQuizAppController.class.getName());
	
	@Override
	public void init() throws ServletException {
		super.init();
		logger.info("=== WordController Initialisation Started ===");
		wordQuizAppDAO = new WordQuizAppDAO();
		logger.info("WordQuizAppDAO instance created successfully.");
		logger.info("=== WordController Initialisation Complete ===");
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		String action = request.getParameter("action");
		logger.info("=== doGet Method Called ===");
		logger.info("Received action: " + action);
		
		if(action == null) {
			action = "start";
			logger.info("action is null, setting to 'start'.");
			
			try {
				switch (action) {
					case "start":
						logger.info("Calling startQuiz method. ");
						startQuiz(request, response);
						break;
					case "next":
						logger.info("Calling nextQuestion method. ");
						nextQuestion(request, response);
						break;
					case "result":
						logger.info("Calling showResult method. ");
						showResult(request, response);
						break;
					default:
						logger.warning("Unknown action: " + action + "- Executing startQuiz.");
						startQuiz(request, response);
						break;				
				}
			} catch (Exception e) {
				logger.log(Level.SEVERE, "An error occurred during doGet processing.", e);
				throw e;
			}
			logger.info("=== doGet Method Processing Complete ===");
		}
	}	
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		String action = request.getParameter("action");
		logger.info("=== doPost Method Called ===");
		logger.info("Received action: " + action);
		
		if(action == null) {
			action = "start";
			logger.info("action is null, setting to 'start'.");
			
			try {
				switch (action) {
					case "check":
						logger.info("Calling startQuiz method. ");
						checkAnswer(request, response);
						break;
					case "hint":
						logger.info("Calling hint method. ");
						showHint(request, response);
						break;
					default:
						logger.warning("Unknown action: " + action + "- Forwarding to doGet");
						doGet(request, response);
						break;
				}
			} catch (Exception e) {
				logger.log(Level.SEVERE, "An error occurred during doPost processing.", e);
				throw e;
			}
			logger.info("=== doGet Method Processing Complete ===");
		}
	}	
	
    /**
     * Starts the quiz.
     */
	private void startQuiz(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		logger.info("--- startQuiz Method Started ---");

		HttpSession session = request.getSession();
		logger.info("Session ID:" + session.getId());
		
		session.setAttribute("currentQuestion", 1);
		session.setAttribute("correctAnswer", 0);
		session.setAttribute("usedWordIds", new ArrayList<Integer>());
		session.setAttribute("quizResults", new ArrayList<String>());
		logger.info("Session initialization complete.");
		
		// Get the first question
		try {
			WordQuizApp wordQuizApp = wordQuizAppDAO.getRandomWord(null);
			if (wordQuizApp != null) {
				logger.info("Word retrieved: ID=" + wordQuizApp.getId() + 
						", Japanese=" + wordQuizApp.getJapaneseWord() + 
						", English=" + wordQuizApp.getEnglishWord());
				session.setAttribute("currentWord", wordQuizApp);
				session.setAttribute("showHint", wordQuizApp);
				session.setAttribute("answerd", wordQuizApp);
				session.setAttribute("isCorrect", wordQuizApp);
			} else {
				logger.severe("Could not retrieve a word from the database.");
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, "An error occurred while retrieving the word.", e);
		}
		
		logger.info("Forwarding to quiz.jsp.");
		RequestDispatcher dispatcher = request.getRequestDispatcher("/quiz.jsp");
		dispatcher.forward(request, response);
		
		logger.info("--- startQuiz Method Finished ---");
	}

	
	private void checkAnswer(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		logger.info("--- checkAnswer Method Started ---");

		HttpSession session = request.getSession();
		String userAnswer = request.getParameter("answer");
		WordQuizApp currentWord = (WordQuizApp) session.getAttribute("currentWord");

		logger.info("User answer: '" + userAnswer + "'");
		
		if(currentWord == null) {
			logger.warning("currentWord is null, restarting quiz.");
			startQuiz(request, response);
			return;
		}
		
		logger.info("Current question:" +  currentWord.getJapaneseWord() + 
				" (Correct: " + currentWord.getEnglishWord() + ")");
		ValidationResult validation = WordQuizAppValidator.validate(userAnswer);
		logger.info("Validation result:" + (validation.isValid() ? "Valid" : "Invaild"));
		
		if(!validation.isValid()) {
			logger.warning("Validation error:" + validation.getErrorMessage());
			request.setAttribute("errorMessage", validation.getErrorMessage());
			session.setAttribute("answered", false);
		} else{
			logger.info("Cleaned answer: '" + validation.getCleanAnswer() + "'");
			
			//Dterrmin if thte naswer is correct
			boolean isCorrect = WordQuizAppValidator.isCorrect(validation.getCleanAnswer(), 
					currentWord.getEnglishWord());
			
			logger.info("Correctness Check" + (isCorrect ? "Correct" : "Incorrect"));
			
			session.setAttribute("answered", true);
			session.setAttribute("isCorrect", isCorrect);
			session.setAttribute("isCorrect", isCorrect);			
			session.setAttribute("userAnswer", validation.getCleanAnswer());
			
			if(isCorrect) {
				int correctAnswers = (Integer) session.getAttribute("correctAmswers");
				correctAnswers++;
				session.setAttribute("correctAnswers", correctAnswers);
				logger.info("現在の正解数" + correctAnswers);
			}
			
			 // Record the result
			@SuppressWarnings("unchecked")
			List<String> results = (List<String>) session.getAttribute("qusestionResults");
			int currentQuestion = (Integer) session.getAttribute("currentQuestion");
			
			String result = String.format("question%d: %s → %s (%s)",
					currentQuestion,
					currentWord.getJapaneseWord(),
					validation.getCleanAnswer(),
					isCorrect ? "correct" : "incorrect");
			results.add(result);
			logger.info("ResultRecord:" + result);
			
			logger.info("Forwarding to quiz.jsp.");
			RequestDispatcher dispatcher = request.getRequestDispatcher("/quiz.jsp");
			
			logger.info("--- checkAnswer Method Finished ---");
			
		}
	}
	
	private void showHint(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		logger.info("--- startHint Method Started ---");

		HttpSession session = request.getSession();
		session.setAttribute("showHint", true);
		
		WordQuizApp currentWord = (WordQuizApp)session.getAttribute("currentWord");
		if(currentWord != null) {
			logger.info("show hint: " + currentWord.getHint());
		}

		RequestDispatcher dispatcher = request.getRequestDispatcher("/quiz.jsp");
		dispatcher.forward(request, response);
		
		logger.info("--- showhint Method Finished ---");
	}
	
	private void nextQuestion(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		logger.info("--- nextQuestion Method Started ---");

		HttpSession session = request.getSession();
		int currentQuestion = (Integer)session.getAttribute("currentQuestion");

		logger.info("Current question number: "+ currentQuestion);
		
		if(currentQuestion >= 10) { 
			logger.info("10 questions completed - Moving to results screen.");
			showResult(request, response);
			return;
		}
		
		// Get the list of used word IDs
		@SuppressWarnings("unchecked")
		List<Integer> usedWordIds = (List<Integer>) session.getAttribute("usedWordIds");
		WordQuizApp currentWord = (WordQuizApp) session.getAttribute("currentWord");
		
		if (currentWord != null) {
			usedWordIds.add(currentWord.getId());
			logger.info("Added used ID: " + currentWord.getId());
		}
		
		logger.info("Added used ID: " + usedWordIds.size());
		
		// Get the next question
		try {
			WordQuizApp nextWord = wordQuizAppDAO.getRandomWord(usedWordIds);

			if (nextWord != null) {
				logger.info("Next question retrieved: ID=" + nextWord.getId() + 
						", Japanese=" + nextWord.getJapaneseWord() + 
						", English=" + nextWord.getEnglishWord());
				session.setAttribute("currentWord", nextWord);
				session.setAttribute("currentQuestion", currentQuestion + 1);
				session.setAttribute("showHint", false);
				session.setAttribute("answerd", false);
				session.setAttribute("isCorrect", false);
				session.setAttribute("userAnswer", "");				
			} else {
				logger.severe("Could not retrieve a word from the database.");
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, "An error occurred while retrieving the word.", e);
		}

		RequestDispatcher dispatcher = request.getRequestDispatcher("/quiz.jsp");
		dispatcher.forward(request, response);
		
		logger.info("--- nextQuiz Method Finished ---");
	}
	
	/**
	 * Displays the results.
	 * */
	private void showResult(HttpServletRequest request, HttpServletResponse response)
	        throws ServletException, IOException {
	    
	    logger.info("--- showResult Method Started ---");
	    
	    HttpSession session = request.getSession();
	    
	    
	    Integer correctAnswers = (Integer) session.getAttribute("correctAnswers");
	    @SuppressWarnings("unchecked")
	    List<String> questionResults = (List<String>) session.getAttribute("questionResults");
	    
	    // nullチェック
	    if (correctAnswers == null) {
	        correctAnswers = 0;
	    }
	    
	    
	    double percentage = (correctAnswers * 100.0) / 10.0;
	    
	   
	    String grade = "";
	    if (percentage >= 90) {
	        grade = "Excellent!";
	    } else if (percentage >= 70) {
	        grade = "Well done!";
	    } else if (percentage >= 50) {
	        grade = "Not bad.";
	    } else {
	        grade = "Keep practicing.";
	    }
	    
	    
	    String encouragement = "";
	    if (percentage < 70) {
	        encouragement = "With consistent practice, you'll definitely improve. Don't give up!";
	    } else {
	        encouragement = "Fantastic job! Keep up the great work with your English studies!";
	    }
	    
	    logger.info("Final correct answers: " + correctAnswers);
	    logger.info("Percentage: " + percentage + "%, Grade: " + grade);
	    
	    
	    request.setAttribute("correctAnswers", correctAnswers);
	    request.setAttribute("questionResults", questionResults);
	    request.setAttribute("percentage", percentage);
	    request.setAttribute("grade", grade);
	    request.setAttribute("encouragement", encouragement);
	    
	    RequestDispatcher dispatcher = request.getRequestDispatcher("/result.jsp");
	    dispatcher.forward(request, response);
	    
	    logger.info("--- showResult Method Finished ---");
	}
}
