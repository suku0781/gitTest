package com.jspminiproj.vo;

public class SearchCriteria {
	private String searchWord;
	private String searchType;
	
	public SearchCriteria(String searchWord, String searchType) {
		super();
		this.searchWord = searchWord;
		this.searchType = searchType;
	}

	public String getSearchWord() {
		return searchWord;
	}

	public void setSearchWord(String searchWord) {
		this.searchWord = searchWord;
	}

	public String getSearchType() {
		return searchType;
	}

	public void setSearchType(String searchType) {
		this.searchType = searchType;
	}

	@Override
	public String toString() {
		return "SearchCriteria [searchWord=" + searchWord + ", searchType=" + searchType + "]";
	}
	
	
	
	
	
	
	
}
