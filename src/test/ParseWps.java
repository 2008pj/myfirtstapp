package test;

import java.io.File;
import java.io.IOException;

import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;

public class ParseWps {

	public static void main(String[] args) {
		Tika tika = new Tika();
		File msword=new File("E:\\desktop\\12\\�����ܽ�-�����ֲ�2015��������ǽ�.wps");
		
			String a;
				try {
					a = tika.parseToString(msword);
					System.out.println(a);
				} catch (IOException | TikaException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			
	

	}

}
