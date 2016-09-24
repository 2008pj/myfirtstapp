package myfirstapp.sysdeal;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import javax.swing.JTextPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

import lucene_3_tika.SearchFiles;
import myfirstapp.sys.App;

import org.apache.lucene.analysis.cn.smart.Utility;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.usermodel.Paragraph;
import org.apache.poi.hwpf.usermodel.Range;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.eclipse.core.runtime.IProgressMonitor;
import org.json.JSONObject;

public class Tool {
	SearchFiles searchFile = new SearchFiles();
	AttributeSet aset = null;// JTextPanel颜色属性
	Document swingDoc = new DefaultStyledDocument();
	JTextPane editorPane;

	/**
	 * 将段落拆分成句子，返回 list
	 * 
	 * @param paragraph
	 * @return
	 */
	public List<String> parseToSentences(String paragraph) {
		// 分词符号
		String PUNCTION = "。，！？；,!?;……";
		List<String> list = new LinkedList<String>();
		int ci;
		char ch;
		StringBuilder buffer = new StringBuilder();
		String sentent = "";
		for (int i = 0; i < paragraph.length(); i++) {
			ch = paragraph.charAt(i);
			buffer.append(ch);

			// 句子结束
			if (PUNCTION.indexOf(ch) != -1 || i + 1 == paragraph.length()) {
				sentent = buffer.toString().trim().replace("　", "");
				if (sentent.length() > 0)// 排除空行
					list.add(sentent);
				buffer.setLength(0);
			}
		}
		return list;
	}

	/**
	 * 根据给定的字符串，判断中文汉字和中文符号数量
	 * 
	 * @param str
	 * @return
	 */
	public int checkChinesWordNum(String str) {
		int num = 0;
		char[] ch = str.toCharArray();
		for (int i = 0; i < ch.length; i++) {
			char c = ch[i];
			if (isChinese(c)) {
				num++;
			}
		}
		return num;
	}

	/**
	 * 通过给定的char判断是否为中文汉字或中文符号
	 * 
	 * @param c
	 * @return
	 */
	public boolean isChinese(char c) {
		Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
		if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
				|| ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
				|| ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
				|| ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B
				|| ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
				|| ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
				|| ub == Character.UnicodeBlock.GENERAL_PUNCTUATION) {
			return true;
		}
		return false;
	}

	/**
	 * 获取字符串中英文单词数量。排除中文字符和中文标点
	 * 
	 * @param str
	 * @return
	 */
	public int checkEnglishWordNum(String str) {
		int count = 0;
		Scanner s = new Scanner(str).useDelimiter(" |,|\\?|\\.");
		while (s.hasNext()) {
			count++;
			s.next();
		}
		return count;
	}

	public int wordsNum(String str) {
		return 0;
	}

	public int checkWordsNum(String str) {
		StringBuffer enBuffer = new StringBuffer();
		StringBuffer cnBuffer = new StringBuffer();
		char[] ch = str.toCharArray();

		for (int i = 0; i < ch.length; i++) {
			char c = ch[i];
			if (isChinese(c)) {
				cnBuffer.append(c);
			} else {
				enBuffer.append(c);
			}
		}
		int cnCount = checkChinesWordNum(cnBuffer.toString());
		int enCount = checkEnglishWordNum(enBuffer.toString());

		// System.out.println("中文字符数： "+cnCount
		// +"   英文单词数： "+enCount+"  中英文单词总数： "+(cnCount+enCount));
		return cnCount + enCount;
	}

	/**
	 * 
	 * 分析输入的文件。
	 * 
	 * @param editorPane
	 * @param filePath
	 */
	public Map paseDocument(String filePath, JTextPane editorPane) {
		System.out.println("正在处理：" + filePath);
		List CopiedSententsList = new LinkedList();
		Map<String, Object> map = new HashMap<String, Object>();
		this.editorPane = editorPane;

		// 单文件比对时，需要用到JTextPane来动态显示原始论文数据
		if (editorPane != null) {
			swingDoc = new DefaultStyledDocument();
			editorPane.setDocument(swingDoc);
			StyleContext sc = StyleContext.getDefaultStyleContext();// 添加一个可以设置样式的类
			java.awt.Color color = new java.awt.Color(237, 82, 16);
			aset = sc.addAttribute(SimpleAttributeSet.EMPTY,
					StyleConstants.Foreground, color);// 为所添加的样式类添加字体颜色l
		}

		String fileType = filePath.substring(filePath.lastIndexOf("."),
				filePath.length()).toLowerCase();
		if (fileType.endsWith(".doc")||fileType.endsWith(".wps")) {
			try {
				// json = docFileType(filePath,CopiedSententsList);
				map = docFileType(filePath, CopiedSententsList);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (fileType.endsWith(".docx")) {
			try {
				// json = docxFileType(filePath,CopiedSententsList);
				map = docxFileType(filePath, CopiedSententsList);
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
		}
		CopiedFileSentents copiedFileSentents = new CopiedFileSentents();
		copiedFileSentents.setCopiedStentsList(CopiedSententsList);
		map.put("copiedFileSentents", copiedFileSentents);
		map.put("swingDoc", swingDoc);

		return map;
	}

	/**
	 * 读取doc文件
	 * 
	 * @param filePath
	 * @return
	 * @throws Exception
	 */
	public Map docFileType(String filePath, List CopiedSententsList)
			throws Exception {
		// JSONObject json;
		Map<String, Object> map = new HashMap<String, Object>();
		InputStream in = new FileInputStream(filePath);
		HWPFDocument doc = new HWPFDocument(in);
		Range r = doc.getRange();
		String paragraphContext = "";
		int copiedWordNum = 0;// 重复字数
		int countNum = 0;// 文章字数=中文字数+中文符号+英文单词数
		int copiedSententsNum = 0;// 重复的句子数
		int paragraphCountNum = r.numParagraphs();// 段落总数
		map.put("paragraphCount", paragraphCountNum);
		map.put("countNum", countNum);
		map.put("copiedSententsNum", copiedSententsNum);
		map.put("copiedWordNum", copiedWordNum);

		for (int x = 0; x < paragraphCountNum; x++) {
			Paragraph p = r.getParagraph(x);
			paragraphContext = p.text();
			map = dealWithParagraphContext(map, paragraphContext,
					CopiedSententsList);
		}

		return map;
	}

	/**
	 * 读取DOCX文件
	 * 
	 * @param filePath
	 * @return
	 * @throws BadLocationException
	 */
	public Map docxFileType(String filePath, List CopiedSententsList)
			throws BadLocationException {
		XWPFDocument docx = null;
		JSONObject json;
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			InputStream in = new FileInputStream(filePath);
			docx = new XWPFDocument(in);
		} catch (IOException e) {
			e.printStackTrace();
		}
		List<XWPFParagraph> paragraphs = docx.getParagraphs();
		String paragraphContext = "";
		int copiedWordNum = 0;// 重复字数
		int countNum = 0;// 文章字数=中文字数+中文符号+英文单词数
		int copiedSententsNum = 0;// 重复的句子数
		int paragraphCountNum = paragraphs.size();// 段落总数

		// json = new JSONObject(//
		// "{'paragraphCount':" + paragraphCountNum + ","// 段落总数
		// + "'countNum':" + countNum + ","// 字数
		// + "'copiedSententsNum':" + copiedSententsNum + ","// 重复语句数
		// + "'copiedWordNum':" + copiedWordNum + "}"// 重复字数
		// );
		map.put("paragraphCount", paragraphCountNum);
		map.put("countNum", countNum);
		map.put("copiedSententsNum", copiedSententsNum);
		map.put("copiedWordNum", copiedWordNum);

		for (XWPFParagraph p : paragraphs) {
			paragraphContext = p.getText();

			map = dealWithParagraphContext(map, paragraphContext,
					CopiedSententsList);

		}

		return map;
	}

	/**
	 * 处理每个段落文本
	 * 
	 * @param json
	 * @param paragraphContext
	 * @return
	 * @throws BadLocationException
	 */
	private Map dealWithParagraphContext(Map map, String paragraphContext,
			List CopiedSententsList) throws BadLocationException {
		// int countNum = json.getInt("countNum");
		// int copiedSententsNum = json.getInt("copiedSententsNum");
		// int copiedWordNum = json.getInt("copiedWordNum");
		int countNum = (int) map.get("countNum");
		int copiedSententsNum = (int) map.get("copiedSententsNum");
		int copiedWordNum = (int) map.get("copiedWordNum");

		// 字数统计,包含中文汉字、中文符号、英文单词
		countNum += checkWordsNum(paragraphContext);// 所有段落中文字数相加

		// 将段落拆分为句子
		List<String> sententList = new Tool()
				.parseToSentences(paragraphContext);

		// 遍历所有句子，以句子为单位进行查重，如果发现为重复，则改变文字颜色
		if (!paragraphContext.equals("\r"))
			swingDoc.insertString(swingDoc.getLength(), "\r\n     ", null);
		for (String sentent : sententList) {
			/**
			 * 查重核心方法，如果重复，则返回一个对象，包含1原始句子、2高亮文章、3出处标题、4Id
			 */
			CopiedSentent copiedSentent = searchFile.isCopied(sentent);
			// 重复
			if (copiedSentent != null) {
				CopiedSententsList.add(copiedSentent);
				copiedSententsNum++;
				copiedWordNum += checkWordsNum(sentent);// 包含中文汉字、中文符号、英文单词
				if (editorPane != null)
					swingDoc.insertString(swingDoc.getLength(), sentent, aset);
				// 不重复
			} else {
				if (editorPane != null)
					swingDoc.insertString(swingDoc.getLength(), sentent, null);
			}
		}
		map.put("countNum", countNum);
		map.put("copiedSententsNum", copiedSententsNum);
		map.put("copiedWordNum", copiedWordNum);

		return map;
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

	public String getTitleName(String str) {
		return str.substring(str.lastIndexOf("\\") + 1, str.length());
	}

	int fileNum;

	public int getDirectFileNum(Path p) {
		fileNum=0;
		if (Files.isDirectory(p)) {
			try {
				Files.walkFileTree(p, new SimpleFileVisitor<Path>() {
					@Override
					public FileVisitResult visitFile(Path file,
							BasicFileAttributes attrs) throws IOException {
						
						if(checkFileType(file.toFile())){
							fileNum++;
						}
						return FileVisitResult.CONTINUE;
					}
				});
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		return fileNum;
	}
	/**
	 * 读取文件夹内所有层级下的文件加入List
	 */
	
	public List<File> filesToList(Path filePathStr,List<File> list) {
		
		File file = filePathStr.toFile();
		if (file.exists()) {
			File[] files = file.listFiles();
			if (files.length == 0) {
				System.out.println("文件夹是空的!");
			} else {
				for (File file2 : files) {
					if (file2.isDirectory()) {
						System.out.println("文件夹:" + file2.getAbsolutePath());
						filesToList(file2.toPath(),list);
					} else {
						list.add(file2);

					}
				}
			}
		} else {
			System.out.println("文件不存在!");
		}
		return list;
	}
	/**
	 * 检查文件后缀
	 * @param file
	 * @return
	 */
	boolean checkFileType(File file){
		String filePath=file.toString();
		String fileType = filePath.substring(filePath.lastIndexOf(".")+1,
				filePath.length()).toLowerCase();
		System.out.println(fileType);
		return Arrays.asList(App.vaildFileType).contains(fileType);
	}
}
