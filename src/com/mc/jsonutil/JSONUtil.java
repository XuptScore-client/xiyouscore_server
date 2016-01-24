package com.mc.jsonutil;

import java.io.IOException;
import java.io.StringWriter;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.ser.CustomSerializerFactory;

public class JSONUtil {

	private static ObjectMapper objectMapper;

	/**
	 * ���赥��ģʽ�õ�ObjectMapperʵ�� �˶���ΪJackson�ĺ���
	 */
	private static ObjectMapper getMapper() {
		if (objectMapper == null) {
			objectMapper = new ObjectMapper();
			// ���Ҳ�����Ӧ�����л���ʱ ���Դ��ֶ�
			objectMapper.configure(
					SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS, false);
			// ʹJackson JSON֧��Unicode�����ASCII�ַ�
			CustomSerializerFactory serializerFactory = new CustomSerializerFactory();
			serializerFactory.addSpecificMapping(String.class,
					new StringUnicodeSerializer());
			objectMapper.setSerializerFactory(serializerFactory);
			// ֧�ֽ���
		}
		return objectMapper;
	}

	/**
	 * ����JSON�������ľ�̬����
	 * 
	 * @param content
	 *            JSON�ַ���
	 * @return
	 */
	private static JsonParser getParser(String content) {
		try {
			return getMapper().getJsonFactory().createJsonParser(content);
		} catch (IOException ioe) {
			return null;
		}
	}

	/**
	 * ����JSON�������ľ�̬����, ʹ�ñ�׼���
	 * 
	 * @return
	 */
	private static JsonGenerator getGenerator(StringWriter sw) {
		try {
			return getMapper().getJsonFactory().createJsonGenerator(sw);
		} catch (IOException e) {
			return null;
		}
	}

	/**
	 * JSON�������л�
	 */
	public static String toJSON(Object obj) {
		StringWriter sw = new StringWriter();
		JsonGenerator jsonGen = getGenerator(sw);
		if (jsonGen == null) {
			try {
				sw.close();
			} catch (IOException e) {
			}
			return null;
		}
		try {
			// ������getGenerator������ָ����OutputStreamΪsw
			// ��˵���writeObject�Ὣ���������sw
			jsonGen.writeObject(obj);
			// ���ڲ�����ʽ��� �������Ϻ������ջ��������ر������
			jsonGen.flush();
			jsonGen.close();
			return sw.toString();
		} catch (JsonGenerationException jge) {
			System.out.println("JSON���ɴ���" + jge.getMessage());
		} catch (IOException ioe) {
			System.out.println("JSON�����������" + ioe.getMessage());
		}
		return null;
	}

	/**
	 * JSON�������л�
	 */
	public static <T> T fromJSON(String json, Class<T> clazz) {
		try {
			JsonParser jp = getParser(json);
			return jp.readValueAs(clazz);
		} catch (JsonParseException jpe) {
			System.out.println(String.format("�����л�ʧ��, ����ԭ��:%s",
					jpe.getMessage()));
		} catch (JsonMappingException jme) {
			System.out.println(String.format("�����л�ʧ��, ����ԭ��:%s",
					jme.getMessage()));
		} catch (IOException ioe) {
			System.out.println(String.format("�����л�ʧ��, ����ԭ��:%s",
					ioe.getMessage()));
		}
		return null;
	}
}
