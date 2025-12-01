package com.wordquizapp.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;


public class SqlLoader {
	private static final Map<String, String> sqlCache = new HashMap<>();
	
	public static String getSql(String fileName) {
		String cacheKey = fileName;
		if (sqlCache.containsKey(cacheKey)) {
			return sqlCache.get(cacheKey);
		}
		try(InputStream is = SqlLoader.class.getClassLoader().getResourceAsStream("sql/" + fileName)) {
			if(is == null) {
				throw new IOException ("SQL file not found: ");
			}
			String sql = new String(is.readAllBytes(), StandardCharsets.UTF_8).trim();
			sqlCache.put(cacheKey, sql);
			return sql;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
}