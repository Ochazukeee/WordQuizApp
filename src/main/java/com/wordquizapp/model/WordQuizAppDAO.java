package com.wordquizapp.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.wordquizapp.util.DatabaseConnection;
import com.wordquizapp.util.SqlLoader;

public class WordQuizAppDAO {
	private static final String AllWordsSql = SqlLoader.getSql("getAllWords.sql");
	private static final String FirstRandomWordSql = SqlLoader.getSql("getFirstRandomWord.sql");	
	private static final String RandomWordsSql = SqlLoader.getSql("getRandomWords.sql");
	private static final String GetWordByIdSql = SqlLoader.getSql("getRWordById.sql");
    private static final String GetWordCountSql = SqlLoader.getSql("getWordCount.sql");
	
	
    /**
     * get all words 
     * @return　単語リスト
     */
	public List <WordQuizApp> getAllWords(){
		List<WordQuizApp> words = new ArrayList<>();
		
		try (Connection conn = DatabaseConnection.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(AllWordsSql);
				ResultSet rs = pstmt.executeQuery()){
					while (rs.next()) {
						WordQuizApp wordQuizApp = new WordQuizApp();
						wordQuizApp.setId(rs.getInt("id"));
						wordQuizApp.setJapaneseWord(rs.getString("japaneseWord"));
						wordQuizApp.setEnglishWord(rs.getString("englishWord"));
						wordQuizApp.setHint(rs.getString("hint"));
						wordQuizApp.setNote(rs.getString("note"));
						words.add(wordQuizApp);
					}
		} catch (SQLException e) {
			System.err.println("Database error:" + e.getMessage());
		}
		return words;
	}
	
    /**
     * get rondom words
     * @param excludeIds
     * @return 
     */

	public WordQuizApp getRandomWord (List<Integer> excludeIds) {
		String sql;
		if (excludeIds != null && !excludeIds.isEmpty()) {
			String placeholders = String.join((","), java.util.Collections.nCopies(excludeIds.size(), "?"));
			sql = RandomWordsSql.replace("{ids}", placeholders);
		}
		else {
			sql = FirstRandomWordSql;
		}

		// for debug
		System.out.println("=== DEBUG: SQL Content ===");
		System.out.println(sql);
		System.out.println("==========================");

		try (Connection conn = DatabaseConnection.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(sql)) {

					if (excludeIds != null && !excludeIds.isEmpty()) {
						for (int i = 0; i < excludeIds.size(); i++) {
							pstmt.setInt(i + 1, excludeIds.get(i));
						}
					}

					try (ResultSet rs = pstmt.executeQuery()){
						if(rs.next()) {
							WordQuizApp wordQuizApp = new WordQuizApp();
							wordQuizApp.setId(rs.getInt("id"));
							wordQuizApp.setJapaneseWord(rs.getString("japaneseWord"));
							wordQuizApp.setEnglishWord(rs.getString("englishWord"));
							wordQuizApp.setHint(rs.getString("hint"));
							wordQuizApp.setNote(rs.getString("note"));
							return wordQuizApp;
						}
					}
				} catch (SQLException e) {
					System.err.println("DatabaseError:" + e.getMessage());
				}
			return null;
	}


    /**
     * get rondom selected by id
     * @param id 
     * @return word object
     */
	
    public WordQuizApp getWordById(int id) {

    	try (Connection conn = DatabaseConnection.getConnection();

    		PreparedStatement pstmt = conn.prepareStatement(GetWordByIdSql)) {

    		pstmt.setInt(1, id);
    		try (ResultSet rs = pstmt.executeQuery()) {
    			if (rs.next()) {
    				WordQuizApp wordQuizApp = new WordQuizApp();
    				wordQuizApp.setId(rs.getInt("id"));
    				wordQuizApp.setJapaneseWord(rs.getString("japanese_word"));
    				wordQuizApp.setEnglishWord(rs.getString("english_word"));
    				wordQuizApp.setHint(rs.getString("hint"));
    				return wordQuizApp;
    			}
    		}
    	} catch (SQLException e) {
    		System.err.println("database error: " + e.getMessage());
    	}

    	return null;
	}


    /**
     * get count all words
     * @return the number of all words
     */
    public int getWordCount() {
        
        try (Connection conn = DatabaseConnection.getConnection();

        		PreparedStatement pstmt = conn.prepareStatement(GetWordCountSql);
        		ResultSet rs = pstmt.executeQuery()) {

        		if (rs.next()) {
        			return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("database error: " + e.getMessage());
        }
        
        return 0;
    }

}

