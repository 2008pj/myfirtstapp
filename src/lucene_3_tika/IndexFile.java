package lucene_3_tika;

import myfirstapp.sys.App;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.Field.TermVector;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.widgets.Text;

import java.awt.Button;
import java.awt.Checkbox;
import java.awt.Choice;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.List;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Date;

public class IndexFile {
	String indexPath;
	String docsPath;
	Analyzer analyzer;
	static Text showResultText;
	static int count = 0;
	Tika tika = new Tika();

	public IndexFile(String docPath, Text showResultText) {
		this.analyzer = App.App_lucene_analyzer;
		this.indexPath = App.indexPath;
		this.docsPath = docPath;
		IndexFile.showResultText = showResultText;
	}

	public void index(String indexMode) {
		final Path docDir = Paths.get(docsPath);

		Date start = new Date();
		try {
			// if (showResultText != null)
			// IndexFile.showResultText.append("开始分析文档并创建索引至目录：'" + indexPath
			// + "'...\n\r");

			// 1.定义directory
			Directory dir = FSDirectory.open(Paths.get(indexPath));
			// 3、定义iwc配置
			IndexWriterConfig iwc = new IndexWriterConfig(analyzer);

			if (indexMode.equals(App.INDEX_MODE_CREAT)) {
				iwc.setOpenMode(OpenMode.CREATE);
			} else {
				iwc.setOpenMode(OpenMode.APPEND);
			}

			// 4.定义writer
			IndexWriter writer = new IndexWriter(dir, iwc);
			indexDocs(writer, docDir, tika);

			writer.close();

			Date end = new Date();

			// if (showResultText != null)
			// IndexFile.showResultText.append("分析" + this.count + "篇文档，共耗时 "
			// + (end.getTime() - start.getTime()) / 1000 + " 秒\n\r");
		} catch (IOException e) {
			System.out.println(" caught a " + e.getClass()
					+ "\n with message: " + e.getMessage());
		}

	}

	static void indexDocs(final IndexWriter writer, Path path, final Tika tika)
			throws IOException {
		if (Files.isDirectory(path)) {
			Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult visitFile(Path file,
						BasicFileAttributes attrs) throws IOException {
					try {
						indexDoc(writer, file, attrs.lastModifiedTime()
								.toMillis(), tika);
					} catch (IOException ignore) {
					}
					return FileVisitResult.CONTINUE;
				}
			});
		} else {
			indexDoc(writer, path, Files.getLastModifiedTime(path).toMillis(),
					tika);
		}
	}

	/** Indexes a single document */
	static void indexDoc(IndexWriter writer, Path file, long lastModified,
			Tika tika) throws IOException {
		try (InputStream stream = Files.newInputStream(file)) {
			String fileName = file.toFile().getName();
			Document doc = new Document();
			doc.add(new StringField("fileName", fileName, Field.Store.YES));
			Field pathField = new StringField("path", file.toString(),
					Field.Store.YES);

			// tika直接读取work内容为String
			String temp = "";
			try {
				temp = tika.parseToString(file);
			} catch (TikaException e) {
				e.printStackTrace();
			}
			// 索引构建日期
			doc.add(new LongField("creatDateLong", new Date().getTime(),
					Field.Store.YES));
			doc.add(pathField);
			doc.add(new LongField("modified", lastModified, Field.Store.NO));
			doc.add(new Field("contents", temp, Store.YES, Index.ANALYZED,
					TermVector.WITH_POSITIONS_OFFSETS)); // 存储正文内容，供高亮显示
			if (writer.getConfig().getOpenMode() == OpenMode.CREATE) {
				if (showResultText != null)
					IndexFile.showResultText.append("新建： " + file + "\n\r");
				writer.addDocument(doc);
			} else {
				if (showResultText != null)
					IndexFile.showResultText.append("更新： " + file+ "\n\r");
				writer.updateDocument(new Term("path", file.toString()), doc);
			}
		}
		IndexFile.count++;
	}

}
