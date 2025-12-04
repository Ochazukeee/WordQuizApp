package com.wordquizapp.validator;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;


/**
 * JUnit test class for WordValidator
 */

public class WordQuizAppValidatorTest {
	
    // --- Tests for WordValidator.validate() ---

	/**
	 * [Positive Case] Test for valid alphabetic input
	 */

	@Test
	void testValidateValidInput() {
		// Arrange (Test data setup)
		String input = "Hello";

		// Act (Execute the target method)
		WordQuizAppValidator.ValidationResult result = WordQuizAppValidator.validate(input);

		// Assert (Check if the result is correct)
		assertTrue(result.isValid(), "Valid input should retrun true");
		assertEquals("hello",result.getCleanAnswer(), "The result should be converted to lowercase");
		fail("Not yet implemented");
	}

	
	@Test
	void testIsCorrect() {
		fail("Not yet implemented");
	}

}
