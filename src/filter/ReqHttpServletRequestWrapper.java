package filter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public class ReqHttpServletRequestWrapper extends HttpServletRequestWrapper {
	HttpServletRequest orgRequest = null;

	public ReqHttpServletRequestWrapper(HttpServletRequest request) {
		super(request);
		orgRequest = request;
	}

	/**
	 * ����getParameter���������������Ͳ���ֵ����req���ˡ�<br/>
	 * �����Ҫ���ԭʼ��ֵ����ͨ��super.getParameterValues(name)����ȡ<br/>
	 * getParameterNames,getParameterValues��getParameterMapҲ������Ҫ����
	 */
	@Override
	public String getParameter(String name) {
		String value = super.getParameter(name);
		if (value != null) {
			value = reqEncode(value);
		}
		return value;
	}

	/**
	 * ����getHeader���������������Ͳ���ֵ����req���ˡ�<br/>
	 * �����Ҫ���ԭʼ��ֵ����ͨ��super.getHeaders(name)����ȡ<br/>
	 * getHeaderNames Ҳ������Ҫ����
	 */
	/*
	 * @Override public String getHeader(String name) { String value =
	 * super.getHeader(reqEncode(name)); if (value != null) { value =
	 * reqEncode(value); } return value; }
	 */

	/**
	 * ����������req©���İ���ַ�ֱ���滻��ȫ���ַ�
	 * 
	 * @param s
	 * @return
	 */
	private static String reqEncode(String s) {
		if (s == null || "".equals(s)) {
			return s;
		}
		StringBuilder sb = new StringBuilder(s.length() + 16);
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			switch (c) {
			case '\'':
				sb.append("&prime;");// &acute;");
				break;
			case '��':
				sb.append("&prime;");// &acute;");
				break;
			case '\"':
				sb.append("&quot;");
				break;
			case '��':
				sb.append("&quot;");
				break;
			case '&':
				sb.append("��");
				break;
			case '#':
				sb.append("��");
				break;
			case '\\':
				sb.append('��');
				break;

			default:
				sb.append(c);
				break;
			}
		}
		return sb.toString();
	}

	/**
	 * ��ȡ��ԭʼ��request
	 * 
	 * @return
	 */
	public HttpServletRequest getOrgRequest() {
		return orgRequest;
	}

	/**
	 * ��ȡ��ԭʼ��request�ľ�̬����
	 * 
	 * @return
	 */
	public static HttpServletRequest getOrgRequest(HttpServletRequest req) {
		if (req instanceof ReqHttpServletRequestWrapper) {
			return ((ReqHttpServletRequestWrapper) req).getOrgRequest();
		}

		return req;
	}
}
