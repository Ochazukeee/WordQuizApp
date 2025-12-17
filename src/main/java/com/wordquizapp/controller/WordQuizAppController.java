package com.wordquizapp.controller;

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
@WebServlet("/WordQuizAppController")
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
		}	
		try {
			switch (action) {
			case "start":
				logger.info("Calling startQuiz method. ");
				startQuiz(request, response);
				break;
			case "quiz":
				logger.info("Calling prepareQuiz method. ");
				prepareQuiz(request, response);
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
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		String action = request.getParameter("action");
		logger.info("=== doPost Method Called ===");
		logger.info("Received action: " + action);
		
		if(action == null) {
			action = "start";
			logger.info("action is null, setting to 'start'.");
		}	
		try {
			switch (action) {
				case "check":
					logger.info("Calling checkAnswer method. ");
					checkAnswer(request, response);
					break;
				case "hint":
					logger.info("Calling showHint method. ");
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
		logger.info("=== doPost Method Processing Complete ===");
	}

    /**
     * initialisation
     */
	private void initialiseSession(HttpSession session) {
		session.setAttribute("currentQuestion", 1);
		session.setAttribute("correctAnswers", 0);
		session.setAttribute("usedWordIds", new ArrayList<Integer>());
		session.setAttribute("quizResults", new ArrayList<String>());
		logger.info("Session ID:" + session.getId());
		logger.info("Session initialization complete.");
	}
	
    /**
     * set next question to session
     */
	private void setNewQuestion(HttpSession session, WordQuizApp wordQuizApp) {
		session.setAttribute("currentWord", wordQuizApp);
		session.setAttribute("showHint", false);
		session.setAttribute("answered", false);
		session.setAttribute("isCorrect", false);
		session.setAttribute("userAnswer", "");
	}
	
    /**
     * Starts the quiz.
     */
	private void startQuiz(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		logger.info("--- startQuiz Method Started ---");

		HttpSession session = request.getSession();

		initialiseSession(session);
		
		// Get the first question
		try {
			WordQuizApp wordQuizApp = wordQuizAppDAO.getRandomWord(null);
			if (wordQuizApp != null) {
				logger.info("Word retrieved: ID=" + wordQuizApp.getId() + 
						", Japanese=" + wordQuizApp.getJapaneseWord() + 
						", English=" + wordQuizApp.getEnglishWord());
				setNewQuestion(session, wordQuizApp);
			} else {
				logger.severe("Could not retrieve a word from the database.");
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, "An error occurred while retrieving the word.", e);
			throw new ServletException("Failed to start quiz", e);
		}
		
		logger.info("Forwarding to quiz.jsp.");
		RequestDispatcher dispatcher = request.getRequestDispatcher("/quiz.jsp");
		dispatcher.forward(request, response);
		
		logger.info("--- startQuiz Method Finished ---");
	}

	/**
	 * Prepares quiz data for display.
	 */
	private void prepareQuiz(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		logger.info("--- prepareQuiz Method Started ---");
		HttpSession session = request.getSession();

		//retrive each from the session
	Integer currentQuestion = getOrDefault(session, "currentQuestion", 1);
		WordQuizApp currentWord = (WordQuizApp) session.getAttribute("currentWord");
		Boolean showHint = getOrDefault(session, "currentquestion", false);
		Boolean answered = getOrDefault(session, "answered", false);
		Boolean isCorrect = getOrDefault(session, "isCorrect", false);
		String userAnswer = (String) session.getAttribute("userAnswer");	
		String errorMessage = (String) session.getAttribute("errorMessage");
				
		// Set data in request scope
		request.setAttribute("currentQuestion", currentQuestion);
		request.setAttribute("currentWord", currentWord);
		request.setAttribute("showHint", showHint);
		request.setAttribute("answered", answered);
		request.setAttribute("isCorrect", isCorrect);
		request.setAttribute("userAnswer", userAnswer);
		request.setAttribute("errorMessage", errorMessage);

		logger.info("Quiz preparation complete - Forwarding to quiz.jsp");

		RequestDispatcher dispatcher = request.getRequestDispatcher("/quiz.jsp");
		dispatcher.forward(request, response);

		logger.info("--- prepareQuiz Method Finished ---");
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
		logger.info("Validation result:" + (validation.isValid() ? "Valid" : "Invalid"));
		
		if(!validation.isValid()) {
			// Validation error
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
			session.setAttribute("userAnswer", validation.getCleanAnswer());
			
			if(isCorrect) {
				int correctAnswers = (Integer) session.getAttribute("correctAnswers");
				correctAnswers++;
				session.setAttribute("correctAnswers", correctAnswers);
				logger.info("Current correct answer count" + correctAnswers);
			}
			
			 // Record the result
			@SuppressWarnings("unchecked")
			List<String> results = (List<String>) session.getAttribute("questionResults");
			if (results == null) {
  	  			results = new ArrayList<>();
    			session.setAttribute("quizResults", results);
			}

			int currentQuestion = (Integer) session.getAttribute("currentQuestion");
			
			String result = String.format("question%d: %s → %s (%s)",
					currentQuestion,
					currentWord.getJapaneseWord(),
					validation.getCleanAnswer(),
					isCorrect ? "correct" : "incorrect");
			results.add(result);
			logger.info("Result Record:" + result);
			
			logger.info("Forwarding to quiz.jsp.");
			
			logger.info("--- checkAnswer Method Finished ---");
			prepareQuiz(request, response);
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
		if (usedWordIds ==null) {
			usedWordIds = new ArrayList<>();
			session.setAttribute("usedWordIds", usedWordIds);
		}
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
				session.setAttribute("answered", false);
				session.setAttribute("isCorrect", false);
				session.setAttribute("userAnswer", "");				
			} else {
				logger.severe("Could not retrieve a word from the database.");
				throw new ServletException("Falied to retrive the next question.", e);
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, "An error occurred while retrieving the word.", e);
			throw new ServletException("Failed to load next question", e);
		}

		prepareQuiz(request, response);		
		
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

		double percentage = (correctAnswers * 100.0) / 10.0;
		
		String grade = getGrade(percentage);
		String encouragement = getEncouragement(percentage);

		logger.info("Final correct answers: " + correctAnswers + 
					",Percentage: " + percentage + "%, Grade: " + grade);


		request.setAttribute("correctAnswers", correctAnswers);
		request.setAttribute("questionResults", questionResults);
		request.setAttribute("percentage", percentage);
		request.setAttribute("grade", grade);
		request.setAttribute("encouragement", encouragement);

		RequestDispatcher dispatcher = request.getRequestDispatcher("/result.jsp");
		dispatcher.forward(request, response);

		logger.info("--- showResult Method Finished ---");
	}


	@SuppressWarnings("unchecked")	
	private <T> T getOrDefault(HttpSession session, String key, T defaultValue) {
		// TODO Auto-generated method stub
			T value = (T) session.getAttribute(key);
			return value != null ? value : defaultValue;
	}

	private String getGrade(double percentage) { 
		if (percentage >= 90) {
			return "Excellent!";
		} else if (percentage >= 70) {
			return "Well done!";
		} else if (percentage >= 50) {
			return "Not bad.";
		} else {
			return "Keep practicing.";
		}
	}

	private String getEncouragement(double percentage) { 
	String encouragement = "";
		if (percentage < 70) {
			return "Don't give up!";
		} else {
			return "You did a great";
		}
	}
}
