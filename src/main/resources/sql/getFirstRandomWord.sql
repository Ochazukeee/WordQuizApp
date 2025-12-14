SELECT TOP 1 
	id
	, japanese_word AS japaneseWord
	, english_word AS englishWord
	, hint
	, note
	, created_date
	, updated_date
	, deleted_flag
	, deleted_date
	FROM WordQuizApp
ORDER BY NEWID();