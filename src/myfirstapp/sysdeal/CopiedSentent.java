package myfirstapp.sysdeal;

public class CopiedSentent {

	String originalSentents;
	String highlighterString;
	String fileName;
	String id;

	public CopiedSentent(String originalSentents, String highlighterString,
			String fileName, String id) {
		this.originalSentents = originalSentents;
		this.highlighterString = highlighterString;
		this.fileName = fileName;
		this.id = id;

	}

	public String getOriginalSentents() {
		return originalSentents;
	}

	public void setOriginalSentents(String originalSentents) {
		this.originalSentents = originalSentents;
	}


	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getHighlighterString() {
		return highlighterString;
	}

	public void setHighlighterString(String highlighterString) {
		this.highlighterString = highlighterString;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

}
