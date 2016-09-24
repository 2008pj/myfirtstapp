package lucene_3_tika;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import javax.tools.Tool;

import myfirstapp.sys.App;
import myfirstapp.sysdeal.CopiedSentent;
import myfirstapp.sysdeal.FromInfo;
import myfirstapp.sysdeal.PropertiesReader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.vectorhighlight.BaseFragmentsBuilder;
import org.apache.lucene.search.vectorhighlight.FastVectorHighlighter;
import org.apache.lucene.search.vectorhighlight.FieldQuery;
import org.apache.lucene.search.vectorhighlight.FragListBuilder;
import org.apache.lucene.search.vectorhighlight.FragmentsBuilder;
import org.apache.lucene.search.vectorhighlight.ScoreOrderFragmentsBuilder;
import org.apache.lucene.search.vectorhighlight.SimpleFragListBuilder;
import org.apache.lucene.store.FSDirectory;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

/** Simple command-line based search demo. */
public class SearchFiles {
	// 初始化索引路径
	String indexPath = App.indexPath;

	// 初始化分析器
	Analyzer analyzer = App.App_lucene_analyzer;
	Table fromTabel = null;
	Browser browser;
	SimpleDateFormat sdfFileNameDate = new SimpleDateFormat("YYYY/MM/dd");
	public SearchFiles(Table table) {
		// 初始化信息输出窗口
		this.fromTabel = table;
	}

	public SearchFiles() {
	}

	public List<FromInfo> search(String destFilePath) {
		// if (table != null)
		// resultText.append("【检查文件】： " + destFilePath + "\n\r");
		Tika tika = new Tika();
		String sourceFileText = "";
		List<FromInfo> fromInfoList = new LinkedList<>();
		try {
			sourceFileText = tika.parseToString(new File(destFilePath));
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (TikaException e1) {
			e1.printStackTrace();
		}
		Date start = new Date();
		try {
			String field = "contents";

			// 1、定义reader
			// System.out.println(indexPath);
			IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths
					.get(this.indexPath)));
			// 2、定义searcher
			IndexSearcher searcher = new IndexSearcher(reader);

			// 3、定义parser
			QueryParser parser = new QueryParser(field, analyzer);

			// 4、定义query
			BooleanQuery.setMaxClauseCount(99999); // 修改默认参数1024，解决大文档报错
			String afterEscape = QueryParser.escape(sourceFileText);
			Query query = parser.parse(afterEscape);
			// Query query = parser.parse(sourceFileText);

			// 5、TopDocs
			int topDocumentNum=Integer.parseInt(PropertiesReader.getInstance().readKey("topDocumentNum"));
			TopDocs results = searcher.search(query,topDocumentNum ); // 前3篇最相似的文章
			ScoreDoc[] hits = results.scoreDocs;

			// 高亮部分
			FragListBuilder fragListBuilder = new SimpleFragListBuilder();
			// String[] highlighterColor = {
			// "<span style=\"background:yellow\">",
			// "<span style=\"background:red\">" };
			String[] highlighterColor = {
					"<span style=\"color:"
							+ PropertiesReader.getInstance().readKey(
									"exportColor1") + "\">",
					"<span style=\"color:"
							+ PropertiesReader.getInstance().readKey(
									"exportColor2") + "\">" };
			String[] font_tag = { "</span>" };
			FragmentsBuilder fragmentsBuilder = new ScoreOrderFragmentsBuilder(
					highlighterColor, font_tag);
			// FragmentsBuilder fragmentsBuilder = new
			// ScoreOrderFragmentsBuilder(
			// BaseFragmentsBuilder.COLORED_PRE_TAGS,
			// BaseFragmentsBuilder.COLORED_POST_TAGS);
			FastVectorHighlighter fastVectorHighlighter = new FastVectorHighlighter(
					true, true, fragListBuilder, fragmentsBuilder);
			FieldQuery fieldQuery = fastVectorHighlighter.getFieldQuery(query);

			for (int i = 0; i < hits.length; i++) {
				FromInfo fromInfo = new FromInfo();
				Document doc = searcher.doc(hits[i].doc);
				String path = doc.get("path");
				String creatDateLong=doc.get("creatDateLong");
				if (path != null) {
					if (fromTabel != null) {
						TableItem item = new TableItem(fromTabel, SWT.NONE);
						item.setText(new String[] { Integer.toString(i + 1),
								Float.toString(hits[i].score), path,sdfFileNameDate.format(new Date(Long.parseLong(creatDateLong)))});

						fromTabel.update();
					}

					String[] highlighterStrings = fastVectorHighlighter
							.getBestFragments(fieldQuery,
									searcher.getIndexReader(), hits[i].doc,
									field, 9000, 20);// 显示2000个片段，每个片段5000个字符(18个中文字符)，共100万字
					// System.out.println("*高亮片段个数*：" +
					// highlighterStrings.length);
					String highlighterString = "";
					for (int x = 0; x < highlighterStrings.length; x++) {
						highlighterString += highlighterStrings[x];

						// 输出html报告
						// writeToReport(highlighterString, "d:\\xxxxxxxxxxx" +
						// i
						// + ".html");
					}
					highlighterString = "<p style=\"font-size:13px;line-height:20px;\">"
							+ highlighterString + "</p>";
					fromInfo.setScore(hits[i].score);
					fromInfo.setFrom(path);
					fromInfo.setHighlighters(highlighterString);
					fromInfo.setCreatDate(doc.get("creatDateLong"));
					fromInfoList.add(fromInfo);
				} else {
					System.out.println((i + 1) + ". "
							+ "No path for this document");
				}
			}
			reader.close();
		} catch (IOException | ParseException e) {

			e.printStackTrace();
		}
		Date end = new Date();
		System.out.println("耗时 " + (end.getTime() - start.getTime()) + " 秒");

		// if (table != null) {
		// TableItem item = new TableItem(table, SWT.NONE);
		// item.setChecked(true);
		// item.setText(new String[] {
		// "",
		// "耗时",
		// Integer.toString((int) ((end.getTime() - start.getTime())))
		// + " 秒" });
		// table.update();
		// }

		System.out.println("--------------------------------------------\n\r");
		// return highlighters;
		return fromInfoList;

	}

	public void writeToReport(String highlighterString, String reportFilePath) {
		try {
			File file = new File(reportFilePath);
			if (!file.exists()) {
				file.createNewFile();
			}
			FileWriter writer = new FileWriter(file);
			writer.write("<html>");
			writer.write("<body style=\"font-size:13px;line-height:20px;\">");
			writer.write(highlighterString);
			writer.write("</body>");
			writer.write("</html>");
			writer.close();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	/**
	 * 判断字符串是否为抄袭
	 * 
	 * @param sentent
	 * @return
	 */
	public CopiedSentent isCopied(String sentent) {
		CopiedSentent copiedSentent = null;
		try {
			String field = "contents";

			// 1、定义reader
			IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths
					.get(this.indexPath)));
			// 2、定义searcher
			IndexSearcher searcher = new IndexSearcher(reader);

			// 3、定义parser
			QueryParser parser = new QueryParser(field, analyzer);

			// 4、定义query
			BooleanQuery.setMaxClauseCount(99999); // 修改默认参数1024，解决大文档报错
			String afterEscape = QueryParser.escape(sentent);
			Query query = parser.parse(afterEscape);
			// Query query = parser.parse(sourceFileText);

			// 5、TopDocs
			TopDocs results = searcher.search(query, 1); // 前1篇最相似的文章
			ScoreDoc[] hits = results.scoreDocs;

			// 高亮部分
			FragListBuilder fragListBuilder = new SimpleFragListBuilder();
			String[] highlighterColor = { "<span style=\"background:yellow\">" };
			String[] font_tag = { "</span>" };
			FragmentsBuilder fragmentsBuilder = new ScoreOrderFragmentsBuilder(
					highlighterColor, font_tag);
			// FragmentsBuilder fragmentsBuilder = new
			// ScoreOrderFragmentsBuilder(
			// BaseFragmentsBuilder.COLORED_PRE_TAGS,
			// BaseFragmentsBuilder.COLORED_POST_TAGS);
			FastVectorHighlighter fastVectorHighlighter = new FastVectorHighlighter(
					true, true, fragListBuilder, fragmentsBuilder);
			FieldQuery fieldQuery = fastVectorHighlighter.getFieldQuery(query);

			for (int i = 0; i < hits.length; i++) {
				Document doc = searcher.doc(hits[i].doc);
				String path = doc.get("path");
				String fileName = doc.get("fileName");
				if (path != null) {

					// System.out.println((i + 1) + ". " + "  评分：" +
					// hits[i].score
					// + "  路径：" + path + "\n\r");
					String[] highlighterStrings = fastVectorHighlighter
							.getBestFragments(fieldQuery,
									searcher.getIndexReader(), hits[i].doc,
									field, 9000, 20);// 显示2000个片段，每个片段5000个字符(18个中文字符)，共100万字
					// System.out.println("*高亮片段个数*：" +
					// highlighterStrings.length);
					String highlighterString = "";
					for (int x = 0; x < highlighterStrings.length; x++) {
						highlighterString += highlighterStrings[x];
						// 输出html报告
						writeToReport(highlighterString, "d:\\xxxxxxxxxxx" + i
								+ ".html");
					}
					copiedSentent = new CopiedSentent(sentent,
							highlighterString, fileName, "1");
				}
			}
			reader.close();
		} catch (IOException | ParseException e) {

			e.printStackTrace();
		}
		return copiedSentent;
	}
}
