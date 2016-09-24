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
	// ��ʼ������·��
	String indexPath = App.indexPath;

	// ��ʼ��������
	Analyzer analyzer = App.App_lucene_analyzer;
	Table fromTabel = null;
	Browser browser;
	SimpleDateFormat sdfFileNameDate = new SimpleDateFormat("YYYY/MM/dd");
	public SearchFiles(Table table) {
		// ��ʼ����Ϣ�������
		this.fromTabel = table;
	}

	public SearchFiles() {
	}

	public List<FromInfo> search(String destFilePath) {
		// if (table != null)
		// resultText.append("������ļ����� " + destFilePath + "\n\r");
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

			// 1������reader
			// System.out.println(indexPath);
			IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths
					.get(this.indexPath)));
			// 2������searcher
			IndexSearcher searcher = new IndexSearcher(reader);

			// 3������parser
			QueryParser parser = new QueryParser(field, analyzer);

			// 4������query
			BooleanQuery.setMaxClauseCount(99999); // �޸�Ĭ�ϲ���1024��������ĵ�����
			String afterEscape = QueryParser.escape(sourceFileText);
			Query query = parser.parse(afterEscape);
			// Query query = parser.parse(sourceFileText);

			// 5��TopDocs
			int topDocumentNum=Integer.parseInt(PropertiesReader.getInstance().readKey("topDocumentNum"));
			TopDocs results = searcher.search(query,topDocumentNum ); // ǰ3ƪ�����Ƶ�����
			ScoreDoc[] hits = results.scoreDocs;

			// ��������
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
									field, 9000, 20);// ��ʾ2000��Ƭ�Σ�ÿ��Ƭ��5000���ַ�(18�������ַ�)����100����
					// System.out.println("*����Ƭ�θ���*��" +
					// highlighterStrings.length);
					String highlighterString = "";
					for (int x = 0; x < highlighterStrings.length; x++) {
						highlighterString += highlighterStrings[x];

						// ���html����
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
		System.out.println("��ʱ " + (end.getTime() - start.getTime()) + " ��");

		// if (table != null) {
		// TableItem item = new TableItem(table, SWT.NONE);
		// item.setChecked(true);
		// item.setText(new String[] {
		// "",
		// "��ʱ",
		// Integer.toString((int) ((end.getTime() - start.getTime())))
		// + " ��" });
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
	 * �ж��ַ����Ƿ�Ϊ��Ϯ
	 * 
	 * @param sentent
	 * @return
	 */
	public CopiedSentent isCopied(String sentent) {
		CopiedSentent copiedSentent = null;
		try {
			String field = "contents";

			// 1������reader
			IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths
					.get(this.indexPath)));
			// 2������searcher
			IndexSearcher searcher = new IndexSearcher(reader);

			// 3������parser
			QueryParser parser = new QueryParser(field, analyzer);

			// 4������query
			BooleanQuery.setMaxClauseCount(99999); // �޸�Ĭ�ϲ���1024��������ĵ�����
			String afterEscape = QueryParser.escape(sentent);
			Query query = parser.parse(afterEscape);
			// Query query = parser.parse(sourceFileText);

			// 5��TopDocs
			TopDocs results = searcher.search(query, 1); // ǰ1ƪ�����Ƶ�����
			ScoreDoc[] hits = results.scoreDocs;

			// ��������
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

					// System.out.println((i + 1) + ". " + "  ���֣�" +
					// hits[i].score
					// + "  ·����" + path + "\n\r");
					String[] highlighterStrings = fastVectorHighlighter
							.getBestFragments(fieldQuery,
									searcher.getIndexReader(), hits[i].doc,
									field, 9000, 20);// ��ʾ2000��Ƭ�Σ�ÿ��Ƭ��5000���ַ�(18�������ַ�)����100����
					// System.out.println("*����Ƭ�θ���*��" +
					// highlighterStrings.length);
					String highlighterString = "";
					for (int x = 0; x < highlighterStrings.length; x++) {
						highlighterString += highlighterStrings[x];
						// ���html����
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
