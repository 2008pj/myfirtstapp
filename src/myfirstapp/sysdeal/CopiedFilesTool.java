package myfirstapp.sysdeal;

import java.util.LinkedList;
import java.util.List;

public class CopiedFilesTool {
	private static CopiedFilesTool copiedFileTool;
	public static List filesList=new LinkedList<CopiedFileSentents>();
	public static CopiedFilesTool getInstance(){
		if(copiedFileTool==null){
			return new CopiedFilesTool();
		}else{
			return copiedFileTool;
		}
	}
	
	public void addFile(CopiedFileSentents copiedFileSentents){
		filesList.add(copiedFileSentents);
	}
	
	public void clearAllFile(){
		filesList.clear();
	}
	public List getFilesList(){
		return filesList;
	}

}
