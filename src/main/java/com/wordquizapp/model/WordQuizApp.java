package com.wordquizapp.model;

import java.sql.Timestamp;

public class WordQuizApp {
	private int id;
	private String japaneseWord;
	private String englishWord;
	private String hint;
	private String comment;
	private Timestamp createdTime;
	private Timestamp updatedTime;
	private int deletedFlag;
	private Timestamp deletedTime;
	
	//default constructot
	public WordQuizApp() {
	}
	
	//parametor constructor
	public WordQuizApp (int id, String japaneseWord, String englishWord, String hint, String comment,
			Timestamp createdTime, Timestamp updatedTime, int deletedFlag, Timestamp deletedTime) {
		this.id = id;
		this.japaneseWord = japaneseWord;
		this.englishWord = englishWord;
		this.hint = hint;
		this.comment = comment;
		this.createdTime = createdTime;
		this.updatedTime = updatedTime;
		this.deletedFlag = deletedFlag;
		this.deletedTime = deletedTime;		
	}
	
	public int getId() {
		return id;
	}
	
	public void setId (int id) {
		this.id = id;
	}
	
	public String getJapaneseWord() {
		return japaneseWord;
	}
	
	public void setJapaneseWord (String japaneseWord) {
		this.japaneseWord = japaneseWord;
	}
	
	public String getEnglishWord() {
		return englishWord;
	}
	
	public void setEnglishWord (String englishWord) {
		this.englishWord = englishWord;
	}
	
	public String getHint() {
		return hint;
	}
	
	public void setHint (String hint) {
		this.hint = hint;
	}
	
	public String getComment() {
		return comment;
	}
	
	public void setComment (String comment) {
		this.comment = comment;
	}	
	
	public Timestamp getcCeatedTime() {
		return createdTime;
	}
	
	public void setCreatedTime (Timestamp createdTime) {
		this.createdTime = createdTime;
	}	
	
	public Timestamp getcUpdatedTime() {
		return updatedTime;
	}
	
	public void setUpdatedTime (Timestamp updatedTime) {
		this.updatedTime = updatedTime;
	}	
	
	public int getDeletedFlag() {
		return deletedFlag;
	}
	
	public void setDeletedFlag (int deletedFlag) {
		this.deletedFlag = deletedFlag;
	}
	
	public Timestamp getDeletedTime() {
		return deletedTime;
	}
	
	public void setDeletedTime (Timestamp deletedTime) {
		this.deletedTime = deletedTime;
	}
	
	@Override
	public String toString() {
		return "Word{" + 
				"id	=" + id + 
				", japaneseWord='" + japaneseWord + '\'' +
				", englishWord='" + englishWord + '\'' +
				", hint = '" + hint + '\'' +
				", comment = '" + comment + '\'' +
				'}';
	}
}
