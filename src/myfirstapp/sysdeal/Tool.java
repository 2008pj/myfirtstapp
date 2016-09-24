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
	AttributeSet aset = null;// JTextPanel��ɫ����
	Document swingDoc = new DefaultStyledDocument();
	JTextPane editorPane;

	/**
	 * �������ֳɾ��ӣ����� list
	 * 
	 * @param paragraph
	 * @return
	 */
	public List<String> parseToSentences(String paragraph) {
		// �ִʷ���
		String PUNCTION = "����������,!?;����";
		List<String> list = new LinkedList<String>();
		int ci;
		char ch;
		StringBuilder buffer = new StringBuilder();
		String sentent = "";
		for (int i = 0; i < paragraph.length(); i++) {
			ch = paragraph.charAt(i);
			buffer.append(ch);

			// ���ӽ���
			if (PUNCTION.indexOf(ch) != -1 || i + 1 == paragraph.length()) {
				sentent = buffer.toString().trim().replace("��", "");
				if (sentent.length() > 0)// �ų�����
					list.add(sentent);
				buffer.setLength(0);
			}
		}
		return list;
	}

	/**
	 * ���ݸ������ַ������ж����ĺ��ֺ����ķ�������
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
	 * ͨ��������char�ж��Ƿ�Ϊ���ĺ��ֻ����ķ���
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
	 * ��ȡ�ַ�����Ӣ�ĵ����������ų������ַ������ı��
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

		// System.out.println("�����ַ����� "+cnCount
		// +"   Ӣ�ĵ������� "+enCount+"  ��Ӣ�ĵ��������� "+(cnCount+enCount));
		return cnCount + enCount;
	}

	/**
	 * 
	 * ����������ļ���
	 * 
	 * @param editorPane
	 * @param filePath
	 */
	public Map paseDocument(String filePath, JTextPane editorPane) {
		System.out.println("���ڴ���" + filePath);
		List CopiedSententsList = new LinkedList();
		Map<String, Object> map = new HashMap<String, Object>();
		this.editorPane = editorPane;

		// ���ļ��ȶ�ʱ����Ҫ�õ�JTextPane����̬��ʾԭʼ��������
		if (editorPane != null) {
			swingDoc = new DefaultStyledDocument();
			editorPane.setDocument(swingDoc);
			StyleContext sc = StyleContext.getDefaultStyleContext();// ���һ������������ʽ����
			java.awt.Color color = new java.awt.Color(237, 82, 16);
			aset = sc.addAttribute(SimpleAttributeSet.EMPTY,
					StyleConstants.Foreground, color);// Ϊ����ӵ���ʽ�����������ɫl
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
	 * ��ȡdoc�ļ�
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
		int copiedWordNum = 0;// �ظ�����
		int countNum = 0;// ��������=��������+���ķ���+Ӣ�ĵ�����
		int copiedSententsNum = 0;// �ظ��ľ�����
		int paragraphCountNum = r.numParagraphs();// ��������
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
	 * ��ȡDOCX�ļ�
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
		int copiedWordNum = 0;// �ظ�����
		int countNum = 0;// ��������=��������+���ķ���+Ӣ�ĵ�����
		int copiedSententsNum = 0;// �ظ��ľ�����
		int paragraphCountNum = paragraphs.size();// ��������

		// json = new JSONObject(//
		// "{'paragraphCount':" + paragraphCountNum + ","// ��������
		// + "'countNum':" + countNum + ","// ����
		// + "'copiedSententsNum':" + copiedSententsNum + ","// �ظ������
		// + "'copiedWordNum':" + copiedWordNum + "}"// �ظ�����
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
	 * ����ÿ�������ı�
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

		// ����ͳ��,�������ĺ��֡����ķ��š�Ӣ�ĵ���
		countNum += checkWordsNum(paragraphContext);// ���ж��������������

		// ��������Ϊ����
		List<String> sententList = new Tool()
				.parseToSentences(paragraphContext);

		// �������о��ӣ��Ծ���Ϊ��λ���в��أ��������Ϊ�ظ�����ı�������ɫ
		if (!paragraphContext.equals("\r"))
			swingDoc.insertString(swingDoc.getLength(), "\r\n     ", null);
		for (String sentent : sententList) {
			/**
			 * ���غ��ķ���������ظ����򷵻�һ�����󣬰���1ԭʼ���ӡ�2�������¡�3�������⡢4Id
			 */
			CopiedSentent copiedSentent = searchFile.isCopied(sentent);
			// �ظ�
			if (copiedSentent != null) {
				CopiedSententsList.add(copiedSentent);
				copiedSententsNum++;
				copiedWordNum += checkWordsNum(sentent);// �������ĺ��֡����ķ��š�Ӣ�ĵ���
				if (editorPane != null)
					swingDoc.insertString(swingDoc.getLength(), sentent, aset);
				// ���ظ�
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
	 * ��ȡ�ļ��������в㼶�µ��ļ�����List
	 */
	
	public List<File> filesToList(Path filePathStr,List<File> list) {
		
		File file = filePathStr.toFile();
		if (file.exists()) {
			File[] files = file.listFiles();
			if (files.length == 0) {
				System.out.println("�ļ����ǿյ�!");
			} else {
				for (File file2 : files) {
					if (file2.isDirectory()) {
						System.out.println("�ļ���:" + file2.getAbsolutePath());
						filesToList(file2.toPath(),list);
					} else {
						list.add(file2);

					}
				}
			}
		} else {
			System.out.println("�ļ�������!");
		}
		return list;
	}
	/**
	 * ����ļ���׺
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
