package com.toni.issue_parser;

public class ProcessedComment {
	private String text;
	private String authorName;
	
	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getAuthorName() {
		return authorName;
	}

	public void setAuthorName(String authorName) {
		this.authorName = authorName;
	}

	public ProcessedComment(String text, String authorName){
		this.text = text;
		this.authorName = authorName;
		
	}

}
