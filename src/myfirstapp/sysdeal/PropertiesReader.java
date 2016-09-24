package myfirstapp.sysdeal;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.Properties;
import java.util.ResourceBundle;

import javax.imageio.stream.FileImageInputStream;

import org.eclipse.core.runtime.Platform;

import myfirstapp.ui.MultiFileCompare;

public class PropertiesReader {
	private static PropertiesReader propertiesReader;
	Properties properties = new Properties();
	InputStream is = null;
	File file;
	PropertiesReader() {
		Reader in;
		try {
			file=new File(System.getProperty("user.dir")+"\\properties.properties");
			is = new FileInputStream(file);
		} catch (FileNotFoundException e2) {
			e2.printStackTrace();
		}
		if(is==null){
			System.out.println("空");
		}
		try {
			in = new InputStreamReader(is,
			// ClassLoader
			// .getSystemResourceAsStream("properties.properties"),
					"UTF-8");
			properties.load(in);
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static PropertiesReader getInstance() {
		if (propertiesReader == null) {
			return new PropertiesReader();
		} else {
			return propertiesReader;
		}
	}

	public String readKey(String key) {
		return properties.getProperty(key);
	}
	public  void writeProperties(String keyname,String keyvalue) {       
        try {
        	
            OutputStream fos = new FileOutputStream(file);
            properties.setProperty(keyname, keyvalue);
            // 以适合使用 load 方法加载到 Properties 表中的格式，
            // 将此 Properties 表中的属性列表（键和元素对）写入输出流
            properties.store(fos, "Update '" + keyname + "' value");
        } catch (IOException e) {
            System.err.println("属性文件更新错误");
        }
	}
}
