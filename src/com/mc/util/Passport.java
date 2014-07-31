package com.mc.util;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import Decoder.BASE64Decoder;
import Decoder.BASE64Encoder;

/**
 * <p>
 * Title: Passport
 * </p>
 * <p>
 * Description: ����˽Կ�Ŀ�������㷨����������Base64��ASCII�ַ������У�
 * </p>
 * <p>
 * Copyright: Copyright (c) 2011
 * </p>
 * <p>
 * Company: ChinaSoft International Ltd.
 * </p>
 * 
 * @author etc
 * @version 1.0
 */
public class Passport {

	public Passport() {
		// TODO �Զ����ɹ��캯�����
	}

	/**
	 * @param args
	 */
	// public static void main(String[] args) {
	// // TODO �Զ����ɷ������
	// Passport passport = new Passport();
	// String txt = "�����ı�";
	// String key = "1a";
	// String jia_str = passport.passport_encrypt(txt, key);
	// String jie_str = passport.passport_decrypt("XIFrBJTAB5TBCujL", key);
	// System.out.println("���ܺ������ԣ�" + jia_str);
	// System.out.println("���ܺ������ԣ�" + jie_str);
	//
	// }

	/**
	 * Md5����
	 * 
	 * @param x
	 * @return
	 * @throws Exception
	 */
	public String md5(String x) {

		MessageDigest m = null;
		try {
			m = MessageDigest.getInstance("MD5");
			m.update(x.getBytes("UTF8")); // ���±��ĸ�������λԪ��
		} catch (NoSuchAlgorithmException e) {
			// ����һ��MD5��Ϣ�ĸ� ��ʱ�����
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// ���±��ĸ�������λԪ�� ��ʱ�����
			e.printStackTrace();
		}
		byte s[] = m.digest(); // ������ʹ��λԪ��ı�����������,Ȼ�������ժ����
		// System.out.println(s); // ������ܺ��λԪ��
		String result = "";
		for (int i = 0; i < s.length; i++) {
			result += Integer.toHexString((0x000000FF & s[i]) | 0xFFFFFF00)
					.substring(6);
			// ����ʮ������ת��
		}

		return result;

	}

	/**
	 * ���������ַ����� MIME BASE64 ���롣�˱��뷽ʽ�����������ֻ���ͼƬҲ����������˳�����䡣�� BASE64
	 * �������ַ���ֻ����Ӣ����ĸ��Сд�����������֡��Ӻ��뷴б�ߣ��� 64 �������ַ�������������������ַ��� �����ȡ��
	 * BASE64���������ַ�����ԭ�����ַ��������ټ� 1/3 ���ҡ������ BASE64 ������Ϣ���Բο� RFC2045 �ļ�֮ 6.8 ��
	 * 
	 * @param txt
	 *            �ȴ������ԭ�ִ�
	 * @return
	 */
	public String base64_decode(String txt) {
		BASE64Decoder base64_decode = new BASE64Decoder();

		String str = "";
		try {
			str = new String(base64_decode.decodeBuffer(txt));
		} catch (IOException e) {
			// TODO �Զ����� catch ��
			e.printStackTrace();
		}
		return str;
	}

	public String base64_encode(String txt) {
		BASE64Encoder base64_encode = new BASE64Encoder();
		return base64_encode.encode(txt.getBytes());
	}

	/**
	 * Passport ���ܺ���
	 * 
	 * @param string
	 *            �ȴ����ܵ�ԭ�ִ�
	 * @param string
	 *            ˽���ܳ�(���ڽ��ܺͼ���)
	 * 
	 * @return string ԭ�ִ�����˽���ܳ׼��ܺ�Ľ��
	 */
	public String passport_encrypt(String txt, String key) {
		Random random = new Random();
		String rad = String.valueOf(random.nextInt(32000));
		// ʹ����������������� 0~32000 ��ֵ�� MD5()
		// srand((double)microtime() * 1000000);
		String encrypt_key = md5(rad);

		// ������ʼ��
		int ctr = 0;
		String tmp = "";

		// for ѭ����$i Ϊ�� 0 ��ʼ����С�� $txt �ִ����ȵ�����
		char encrypt_key_char[] = encrypt_key.toCharArray();
		char txt_char[] = txt.toCharArray();

		for (int i = 0; i < txt.length(); i++) {
			// ��� $ctr = $encrypt_key �ĳ��ȣ��� $ctr ����
			ctr = ctr == encrypt_key_char.length ? 0 : ctr;
			// $tmp �ִ���ĩβ������λ�����һλ����Ϊ $encrypt_key �ĵ� $ctr λ��
			// �ڶ�λ����Ϊ $txt �ĵ� $i λ�� $encrypt_key �� $ctr λȡ���Ȼ�� $ctr = $ctr + 1
			char tmp1 = txt_char[i];
			char tmp4 = encrypt_key_char[ctr];
			char tmp2 = encrypt_key_char[ctr++];
			char tmp3 = (char) (tmp1 ^ tmp2);
			tmp += tmp4 + "" + tmp3;
		}
		// ���ؽ�������Ϊ passport_key() ��������ֵ�� base65 ������
		return base64_encode(passport_key(tmp, key));

	}

	/**
	 * Passport ���ܺ���
	 * 
	 * @param string
	 *            ���ܺ���ִ�
	 * @param string
	 *            ˽���ܳ�(���ڽ��ܺͼ���)
	 * 
	 * @return string �ִ�����˽���ܳ׽��ܺ�Ľ��
	 */
	public String passport_decrypt(String txt, String key) {

		// $txt �Ľ��Ϊ���ܺ���ִ����� base64 ���룬Ȼ����˽���ܳ�һ��
		// ���� passport_key() ���������ķ���ֵ

		txt = passport_key(base64_decode(txt), key);

		// ������ʼ��
		String tmp = "";

		// for ѭ����$i Ϊ�� 0 ��ʼ����С�� $txt �ִ����ȵ�����
		char txt_char[] = txt.toCharArray();
		for (int i = 0; i < txt.length(); i++) {
			// $tmp �ִ���ĩβ����һλ��������Ϊ $txt �ĵ� $i λ��
			// �� $txt �ĵ� $i + 1 λȡ���Ȼ�� $i = $i + 1
			tmp += (char) (txt_char[i] ^ txt_char[++i]);
		}

		// ���� $tmp ��ֵ��Ϊ���
		return tmp;

	}

	/**
	 * Passport �ܳ״�����
	 * 
	 * @param string
	 *            �����ܻ�����ܵ��ִ�
	 * @param string
	 *            ˽���ܳ�(���ڽ��ܺͼ���)
	 * 
	 * @return string �������ܳ�
	 */
	String passport_key(String txt, String encrypt_key) {

		// �� $encrypt_key ��Ϊ $encrypt_key �� md5() ���ֵ
		encrypt_key = md5(encrypt_key);
		// ������ʼ��
		int ctr = 0;
		String tmp = "";

		// for ѭ����$i Ϊ�� 0 ��ʼ����С�� $txt �ִ����ȵ�����
		char encrypt_key_char[] = encrypt_key.toCharArray();
		char txt_char[] = txt.toCharArray();
		for (int i = 0; i < txt.length(); i++) {
			// ��� $ctr = $encrypt_key �ĳ��ȣ��� $ctr ����
			ctr = ctr == encrypt_key.length() ? 0 : ctr;
			// $tmp �ִ���ĩβ����һλ��������Ϊ $txt �ĵ� $i λ��
			// �� $encrypt_key �ĵ� $ctr + 1 λȡ���Ȼ�� $ctr = $ctr + 1
			char c = (char) (txt_char[i] ^ encrypt_key_char[ctr++]);
			tmp = tmp + c;
		}

		// ���� $tmp ��ֵ��Ϊ���
		return tmp;

	}

	/**
	 * Passport ��Ϣ(����)���뺯��
	 * 
	 * @param array
	 *            �����������
	 * 
	 * @return string ���龭�������ִ�
	 */
	String passport_encode(String array[]) {

		// ���������ʼ��
		String arrayenc[] = new String[array.length];

		// �������� $array������ $key Ϊ��ǰԪ�ص��±꣬$val Ϊ���Ӧ��ֵ
		for (int i = 0; i < array.length; i++) {
			String val = array[i];
			// $arrayenc ��������һ��Ԫ�أ�������Ϊ "$key=���� urlencode() ��� $val ֵ"
			arrayenc[i] = i + "=" + URLEncoder.encode(val);
		}

		// ������ "&" ���ӵ� $arrayenc ��ֵ(implode)������ $arrayenc = array('aa', 'bb',
		// 'cc', 'dd')��
		// �� implode('&', $arrayenc) ��Ľ��Ϊ ��aa&bb&cc&dd"
		return implode("&", arrayenc);

	}

	/**
	 * ������ "&" ���ӵ� $arrayenc ��ֵ(implode)������ $arrayenc = array('aa', 'bb','cc',
	 * 'dd')���� implode('&', $arrayenc) ��Ľ��Ϊ ��aa&bb&cc&dd"
	 * 
	 * @param array
	 * @return
	 */
	public String implode(String str, String array[]) {
		String result = "";
		for (int i = 0; i < array.length; i++) {
			if (i < array.length - 1) {
				result += array[i] + str;
			} else {
				result += array[i];
			}
		}
		return result;
	}
	
	/**
	 * ����
	 * @param string ��������
	 * @return
	 */
	public static String jiami(String string){
		//�Ƚ���base64���� Ȼ����м���
		String str1 = string;
		try {
			str1 = BASE64.encryptBASE64(string.getBytes());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new Passport().passport_encrypt(str1, "mc123456");
	}
	/**
	 * jiemi 
	 * @param args
	 */
	public static String jiemi(String string){
		//�Ƚ���base64���� Ȼ����м���
		String str1 = string;
		try {
			str1 = new Passport().passport_decrypt(str1, "mc123456");
			return new String(BASE64.decryptBASE64(str1));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return str1;
	}
	public static void main(String[] args) {
		
		/*//����
		System.out.println(new Passport().md5("123"));
		System.out.println();
		//����
		System.out.println(new Passport().passport_decrypt("V2QBPwQw", "wwwww"));*/
		System.out.println(jiemi("V00JEVVRDH8ESVQHVkEGLFRKAz0Cags6BgJdVw=="));
	}
}
