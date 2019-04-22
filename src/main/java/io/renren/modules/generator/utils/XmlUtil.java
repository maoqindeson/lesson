package io.renren.modules.generator.utils;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import lombok.extern.slf4j.Slf4j;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 输出xml和解析xml的工具类
 * 
 * @ClassName:XmlUtil
 * @author: leo Email: hujingleo01@163.com
 * @date :2016-9-29 上午9:51:28
 * @Description:TODO
 */
@Slf4j
public class XmlUtil {
	/**
	 * java 转换成xml
	 * 
	 * @Title: toXml
	 * @Description: TODO
	 * @param obj
	 *            对象实例
	 * @return String xml字符串
	 */
	public static String toXml(Object obj) {
		XStream xstream = new XStream();
		// XStream xstream=new XStream(new DomDriver()); //直接用jaxp dom来解释
		// XStream xstream=new XStream(new DomDriver("utf-8"));
		// //指定编码解析器,直接用jaxp dom来解释

		//// 如果没有这句，xml中的根元素会是<包.类名>；或者说：注解根本就没生效，所以的元素名就是类的属性
		xstream.processAnnotations(obj.getClass()); // 通过注解方式的，一定要有这句话
		return xstream.toXML(obj);
	}

	/**
	 * 将传入xml文本转换成Java对象
	 * 
	 * @Title: toBean
	 * @Description: TODO
	 * @param xmlStr
	 * @param cls
	 *            xml对应的class类
	 * @return T xml对应的class类的实例对象
	 * 
	 *         调用的方法实例：PersonBean person=XmlUtil.toBean(xmlStr,
	 *         PersonBean.class);
	 */
	public static <T> T toBean(String xmlStr, Class<T> cls) {
		// 注意：不是new Xstream(); 否则报错：java.lang.NoClassDefFoundError:
		// org/xmlpull/v1/XmlPullParserFactory
		XStream xstream = new XStream(new DomDriver());
		// @SuppressWarnings("deprecation")
		// XStream xstream=new XStream(null, (Mapper)null, new XppDriver());
		// XStream xStream2 = new XStream(new XppDriver());
		// xml解析升级到1.4.6使用ignoreUnknownElements()函数
		xstream.ignoreUnknownElements();
		xstream.processAnnotations(cls);
		T obj = (T) xstream.fromXML(xmlStr);
		return obj;
	}

	/**
	 * 写到xml文件中去
	 * 
	 * @Title: writeXMLFile
	 * @Description: TODO
	 * @param obj
	 *            对象
	 * @param absPath
	 *            绝对路径
	 * @param fileName
	 *            文件名
	 * @return boolean
	 */

	public static boolean toXMLFile(Object obj, String absPath, String fileName) {
		String strXml = toXml(obj);
		String filePath = absPath + fileName;
		File file = new File(filePath);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				return false;
			}
		} // end if
		OutputStream ous = null;
		try {
			ous = new FileOutputStream(file);
			ous.write(strXml.getBytes());
			ous.flush();
		} catch (Exception e1) {
			return false;
		} finally {
			if (ous != null)
				try {
					ous.close();
				} catch (IOException e) {
				}
		}
		return true;
	}

	public static Map<String, String> dom2Map(String xml) {
		try {
			Document doc = DocumentHelper.parseText(xml);
			Map<String, String> map = new HashMap<String, String>();
			if (doc == null)
				return map;
			Element root = doc.getRootElement();
			for (Iterator iterator = root.elementIterator(); iterator.hasNext(); ) {
				Element e = (Element) iterator.next();
				List list = e.elements();
				map.put(e.getName(), e.getText());
			}
			return map;
		} catch (Exception e) {
			log.error("xml转map转换异常，异常信息为：" + e.getMessage());
			e.printStackTrace();
		}
		return new HashMap<>();

	}

}