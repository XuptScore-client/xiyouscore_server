package com.mc.jsonutil;

import java.io.IOException;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.impl.JsonWriteContext;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.util.CharTypes;

public class StringUnicodeSerializer extends JsonSerializer<String> {

	private final char[] HEX_CHARS = "0123456789ABCDEF".toCharArray();
	private final int[] ESCAPE_CODES = CharTypes.getOutputEscapes();

	private void writeUnicodeEscape(JsonGenerator gen, char c)
			throws IOException {
		gen.writeRaw('\\');
		gen.writeRaw('u');
		gen.writeRaw(HEX_CHARS[(c >> 12) & 0xF]);
		gen.writeRaw(HEX_CHARS[(c >> 8) & 0xF]);
		gen.writeRaw(HEX_CHARS[(c >> 4) & 0xF]);
		gen.writeRaw(HEX_CHARS[c & 0xF]);
	}

	private void writeShortEscape(JsonGenerator gen, char c) throws IOException {
		gen.writeRaw('\\');
		gen.writeRaw(c);
	}

	@Override
	public void serialize(String str, JsonGenerator gen,
			SerializerProvider provider) throws IOException,
			JsonProcessingException {
		int status = ((JsonWriteContext) gen.getOutputContext()).writeValue();
		switch (status) {
		case JsonWriteContext.STATUS_OK_AFTER_COLON:
			gen.writeRaw(':');
			break;
		case JsonWriteContext.STATUS_OK_AFTER_COMMA:
			gen.writeRaw(',');
			break;
		case JsonWriteContext.STATUS_EXPECT_NAME:
			throw new JsonGenerationException("Can not write string value here");
		}
		gen.writeRaw('"');// д��JSON���ַ����Ŀ�ͷ����
		for (char c : str.toCharArray()) {
			if (c >= 0x80) {
				writeUnicodeEscape(gen, c); // Ϊ���з�ASCII�ַ�����ת���unicode�ַ�
			} else {
				// ΪASCII�ַ���ǰ128���ַ�ʹ��ת���unicode�ַ�
				int code = (c < ESCAPE_CODES.length ? ESCAPE_CODES[c] : 0);
				if (code == 0) {
					gen.writeRaw(c); // �˴�����ת��
				} else if (code < 0) {
					writeUnicodeEscape(gen, (char) (-code - 1)); // ͨ��ת���ַ�
				} else {
					writeShortEscape(gen, (char) code); // ��ת���ַ� (\n \t ...)
				}
			}
		}
		gen.writeRaw('"');// д��JSON���ַ����Ľ�������
	}

}
