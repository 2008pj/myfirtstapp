package myfirstapp.sys;

import lucene_3_tika.IndexFile;
import lucene_3_tika.MyAnalyzer;
import myfirstapp.sysdeal.PropertiesReader;

import org.apache.lucene.analysis.Analyzer;
import org.wltea.analyzer.lucene.IKAnalyzer;

public interface App {
	String INDEX_MODE_CREAT="INDEX_MODE_CREAT";  //新建索引
	String INDEX_MODE_APPEND="INDEX_MODE_APPEND";//追加索引
	final String indexPath = IndexFile.class.getClassLoader()
			.getResource("/indexPath").getFile().toString();
	String App_ID="myfirstapp";
	String App_view_menuList_ID="myfirstapp.menuList";
	String App_view_mainView_ID="myfirstapp.mainView";
	String App_view_singleFileCompareView_ID="myfirstapp.singleFileCompare";
	String App_view_multiFileCompareView_ID="myfirstapp.multiFileCompare";
	String App_view_creatIndex_ID="myfirstapp.creatIndex";
	String App_view_detail_ID="myfirstapp.detail";
	
	
	String App_lucene_indexPath="indexPath";
	Analyzer App_lucene_analyzer = new MyAnalyzer();
	
	//int min_wordSize=7; //最小词汇单元字数
	
	
	String PDFREPORT_TITLE="四川省电力检修公司论文查重报告";
	String RESULTSTRING_PASS="检测通过";
	String RESULTSTRING_FAIL="涉嫌抄袭";
	String [] vaildFileType={"doc","docx","wps"};
	String [] filterExt = {"*.doc;*.docx;*.wps"};
	final String electricLOGO = IndexFile.class.getClassLoader()
			.getResource("img/electricLOGO.png").getFile().toString();//电力公司LOGO
	//float PassPercent=40;
}
