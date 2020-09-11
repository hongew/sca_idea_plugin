package com.seczone.sca.idea.plugin.util;


import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.reflect.ClassPath;
import com.google.common.reflect.ClassPath.ClassInfo;
import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;


public class Utils {
	private static AtomicInteger counter = new AtomicInteger(0);

	private Utils() {
		super();
	}

	private static Logger logger = LoggerFactory.getLogger(Utils.class);

	private static final Base64.Decoder DECODER = Base64.getDecoder();

	private static final Base64.Encoder ENCODER = Base64.getEncoder();

	public static boolean isEmpty(String str) {
		return str == null || str.length() < 1;
	}

	public static boolean isNotEmpty(String str) {
		return !isEmpty(str);
	}

	public static boolean isEmpty(List<?> list) {
		return list == null || list.size() < 1;
	}

	public static boolean isNotEmpty(List<?> list) {
		return !isEmpty(list);
	}

	public static boolean isEmpty(Map<?, ?> map) {
		return map == null || map.size() < 1;
	}

	public static boolean isNotEmpty(Map<?, ?> map) {
		return !isEmpty(map);
	}

	public static boolean isEmpty(Object[] list) {
		return list == null || list.length < 1;
	}

	public static boolean isNotEmpty(Object[] list) {
		return !isEmpty(list);
	}

	public static Pattern numberPattern = Pattern.compile("[0-9]*");

	public static String getSHA256Str(String str) {
		MessageDigest messageDigest;
		String encdeStr = "";
		try {
			messageDigest = MessageDigest.getInstance("SHA-256");
			byte[] hash = messageDigest.digest(str.getBytes("UTF-8"));
			encdeStr = Hex.encodeHexString(hash);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		return encdeStr;
	}


	public static List<String> getClassOfPackage(String packagePath, boolean isRecursived,
	  boolean isSimpleName) {
		final ClassLoader loader = Thread.currentThread().getContextClassLoader();
		List<String> classes = Lists.newArrayList();
		try {
			ClassPath classpath = ClassPath.from(loader);
			ImmutableSet<ClassInfo> classInfos;

			if (isRecursived) {
				classInfos = classpath.getTopLevelClassesRecursive(packagePath);
			} else {
				classInfos = classpath.getTopLevelClasses(packagePath);
			}

			for (ClassInfo classInfo : classInfos) {
				if (classInfo.getSimpleName().endsWith("_")) {
					continue;
				}

				String classFullName = classInfo.getName();
				if (isSimpleName) {
					classFullName = classInfo.getSimpleName();
				}

				classes.add(classFullName);
				logger.info("Found class: " + classFullName);
			}
		} catch (IOException e) {
			logger.error("Cannot get class list from package: " + packagePath);
			e.printStackTrace();
		}

		return classes;
	}


	public static String getUUID() {
		return replaceUUID(UUID.randomUUID());
	}

	public static String replaceUUID(Object uuid) {
		return uuid.toString().replaceAll("\\-", "");
	}

	public static boolean isUUID(String uuid) {
		if (isNotEmpty(uuid) && uuid.length() == 32) {
			return true;
		}
		return false;
	}


	public static boolean isEqualsTwoUUID(String s,String s1) {
		return isUUID(s) && isUUID(s1) && s.equals(s1);
	}


	public static String base64Decoder(String message) {
		try {
			byte[] decoderMessage = message.getBytes("utf-8");
			return new String(DECODER.decode(decoderMessage), "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return message;
	}

	//获取参数时间当天0点的时间
	public static Timestamp caculateTimestamp(Timestamp timestamp){
		long timeLong = timestamp.getTime();
		Date date = new Date(timeLong) ;
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
		String timeString=simpleDateFormat.format(date);
		Timestamp reultTime= Timestamp.valueOf(timeString);
		return reultTime;
	}

	//得到前一天日期的字符串形式
	public static String getYesterdayDate(){
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DATE, -1);
		Date start = c.getTime();
		String qyt= format.format(start);//前一天
		return qyt;
	}

	public static String base64Encoder(String message) {
		try {
			byte[] encoderMessage = message.getBytes("utf-8");
			return ENCODER.encodeToString(encoderMessage);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return message;
	}

	public static String base64Encoder(byte[] message) {
		return ENCODER.encodeToString(message);
	}


	public static boolean isHash(String hash){
		return (null != hash && hash.length() == 40);
	}

	//得到唯一整数
	public static long getAtomicCounter() {
		if (counter.get() > 999999) {
			counter.set(1);
		}
		long time = System.currentTimeMillis();
		long returnValue = time * 100 + counter.incrementAndGet();
		return returnValue;
	}

	//移除list中的null元素
	public static void removeNullInList(List list) {
		Iterator it = list.iterator();
		while (it.hasNext()) {
			if (it.next() == null) {
				it.remove();
			}
		}
	}


	public static Properties loadProperties(String propertiesFileName) {
		try {
			Properties properties = new Properties();
			InputStream fis = Utils.class.getClassLoader().getResourceAsStream(propertiesFileName);
			properties.load(fis);
			return properties;
		} catch (IOException e) {
			logger.error("Configuration file " + propertiesFileName + " is not found");
			return new Properties();
		}
	}

	public static String loadPropertiesByKey(String key) {
		Properties properties = loadProperties("sca.properties");
		return properties.get(key).toString();
	}

	public static int getConnectResponseCode(String url){
		HttpURLConnection conn = null ;
		URL url1;
		int responseCode = 0;
		int retryTimes = 7 ;
		while (retryTimes-- > 0) {
			try {
				url1 = new URL(url);
				conn = (HttpURLConnection) url1.openConnection();
				conn.setConnectTimeout(2*1000);
				conn.setReadTimeout(2*1000);
				responseCode = conn.getResponseCode();
				return responseCode;
			} catch (IOException ioe) {

			}finally {
				if(conn!=null) {
					conn.disconnect();
				}
			}
		}
		logger.error("当前网络无法连通，无法更新信息");
		return responseCode;
	}


	public static boolean isConnect(String ipPath){
		boolean connect = false;
 
        Runtime runtime = Runtime.getRuntime();
        Process process;
        try {
            process = runtime.exec("ping " + ipPath);
            InputStream is = process.getInputStream();
            InputStreamReader isr = new InputStreamReader(is,"GBK");
            BufferedReader br = new BufferedReader(isr);
            String line = null;
            StringBuffer sb = new StringBuffer();
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            is.close();
            isr.close();
            br.close();
 
            if (null != sb && !sb.toString().equals("")) {
                String logString = "";
                if (sb.toString().indexOf("TTL") > 0) {
                    // 网络畅通
                    connect = true;
                } else {
                    // 网络不畅通
                    connect = false;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return connect;
    }

	public static void main(String[] args) throws Exception{
		int lastUnixPos = "sdf342342342343refssdf34234234234/3refsdfsdr2343471rwefsdfqre2eqwewqewqfdfsdfdfsdr23431rwefsdfqre2eqwewqewqfdfsdf.jar".lastIndexOf(47);
		int lastWindowsPos = "sdfsdf.jar".lastIndexOf(92);
		int num = Math.max(lastUnixPos, lastWindowsPos);
		System.out.println(num);

	}

}