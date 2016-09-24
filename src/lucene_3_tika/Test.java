package lucene_3_tika;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.wltea.analyzer.lucene.IKAnalyzer;
import org.eclipse.swt.widgets.Text;
/**
 * 测试
 */
public class Test {

	String indexPath = "C:/lucene/indexPath";
	String docsPath = "C:/lucene/docsPath";
	Analyzer analyzer = new IKAnalyzer(true);

//	@org.junit.Test
	public void index() {
//		IndexFile index = new IndexFile(indexPath, docsPath, analyzer);
//		index.index();
	}

//	@org.junit.Test
	public void search() {
		List<String> list = new ArrayList<String>();

		Tika tika = new Tika();
		String test4 = "";
		try {
			test4 = tika
					.parseToString(new File(
							"E:\\desktop\\论文去重v1.6\\论文去重v1.6\\Documents\\高级工程师技术工作总结-变电中心旷峰2014 (1).doc"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TikaException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		list.add(test4);
//		SearchFiles searchFile = new SearchFiles(Text a);
//		for (String s : list)
//			searchFile.search(s);
	}

}
