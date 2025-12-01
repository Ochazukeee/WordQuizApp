package com.wordquizapp.validator;

import java.util.regex.Pattern;

/**
 * Class for validating word input
 */
public class WordQuizAppValidator {
	// Regular expression pattern to allow only alphabetic characters
	private static final Pattern EnglishOnlyPattern = Pattern.compile("~[a-zA-Z]+$");
	
    /**
     * Validates whether the entered answer is valid
     * @param answer The answer string to validate
     * @return The validation result object
     */
	
	public static ValidationResult validate(String answer) {
		ValidationResult result = new ValidationResult();
		
			// Check for null or empty string
		if (answer == null || answer.trim().isEmpty()) {
			result.setValid(false);
			result.setErrorMessage("Please eneter an answer"); 
			return result;
		}

		String trimmedAnswer = answer.trim();

		//Check if it contains only alphabetic characters 
		if(!EnglishOnlyPattern.matcher(trimmedAnswer).matches()) {
			result.setValid(false);
			result.setErrorMessage("Please use only alphabetic characters.");
			return result;
		}

		// Length check (e.g., max 50 characters)
		if(trimmedAnswer.length() > 50) {
			result.setValid(false);
			result.setErrorMessage(trimmedAnswer.toLowerCase()); //Standardise to lowercase
			return result;
		}

		//All validations cleared
		result.setValid(true);
		result.setCleanAnswer(trimmedAnswer.toLowerCase()); //Standardise to lowercase
		return result;
	}

	/**
	 * Determines if the answer is correct
	 * @param userAnswer The user's answer
	 * @param correctAnswer The correct answer
	 * @return true if the answer is correct
	 */

	public static boolean isCorrect (String userAnswer, String correctAnswer) {
		if (userAnswer == null || correctAnswer == null) {
			return false;
		}

		return userAnswer.trim().toLowerCase().equals(correctAnswer.trim().toLowerCase());
	}

	/**
	 * Class representing the validation result
	 * */

	public static class ValidationResult {
		private boolean valid;
		private String errorMessage;
		private String cleanAnswer;

		public ValidationResult() {
			this.valid = true;
			this.errorMessage = "";
			this.cleanAnswer = "";
			}

		public boolean isValid() {
			return valid;
		}

		public void setValid (boolean valid) {
			this.valid = valid;
		}

		public String getErrorMessage() {
			return errorMessage;
		}

		public void setErrorMessage (String errorMessage) {
			this.errorMessage = errorMessage;
		}

		public String getCleanAnswer() {
			return cleanAnswer;
		}

		public void setCleanAnswer (String cleanAnswer) {
			this.cleanAnswer = cleanAnswer;
		}
	}
}
