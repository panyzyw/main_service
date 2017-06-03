package com.zccl.ruiqianqi.tools;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *  .	匹配任何单个字符。例如正则表达式“b.g”能匹配如下字符串：“big”、“bug”、“b g”，但是不匹配“buug”。
 *	$	匹配行结束符。例如正则表达式“EJB$”能够匹配字符串“I like EJB”的末尾，但是不能匹配字符串“J2EE Without EJBs！”。
 *	^	匹配一行的开始。例如正则表达式“^Spring”（整个Spring做匹配）能够匹配字符串“Spring is a J2EE framework”的开始，但是不能匹配“I use Spring in my project”。
 *	*	匹配0至多个在它之前的字符。例如正则表达式“zo*”能匹配“z”以及“zoo”；正则表达式“.*”意味着能够匹配任意字符串。
 *	\	转义符，用来将元字符当作普通的字符来进行匹配。例如正则表达式\$被用来匹配美元符号，而不是行尾；正则表达式\.用来匹配点字符，而不是任何字符的通配符。
 *	[]	匹配括号中的任何一个字符。例如正则表达式“b[aui]g”匹配bug、big和bug，但是不匹配beg。可以在括号中使用连字符“-”来指定字符的区间来简化表示，例如正则表达式[0-9]可以匹配任何数字字符，这样正则表达式“a[]c”就可以匹配“a0c”、“a1c”、“a2c”等字符串；还可以制定多个区间，例如“[A-Za-z]”可以匹配任何大小写字母。还有一个相配合使用的元字符“^”，用在这里并不像前边的那个“^”一样表示匹配行开始，而是表示“排除”，要想匹配除了指定区间之外的字符，就可以在左边的括号和第一个字符之间使用^字符，例如“[^163A-Z]”将能偶匹配除了1、6、3和所有大写字母之外的任何字符。
 *	()	将 () 之间括起来的表达式定义为“组”(group)，并且将匹配这个表达式的字符保存到一个临时区域,这个元字符在字符串提取的时候非常有用。
 *	|	将两个匹配条件进行逻辑“或”运算。'z|food' 能匹配 "z" 或 "food"。'(z|f)ood' 则匹配 "zood" 或 "food"。
 *	+	匹配前面的子表达式一次或多次。例如正则表达式9+匹配9、99、999等。
 *	?	匹配前面的子表达式零次或一次。例如，"do(es)?" 可以匹配 "do" 或 "does" 中的"do" 。此元字符还有另外一个用途，就是表示非贪婪模式匹配，后边将有介绍
 *	{n}		匹配确定的 n 次。例如，“e{2}”不能匹配“bed”中的“d”，但是能匹配“seed”中的两个“e”。
 *	{n,}	至少匹配n次。例如，“e{2,}”不能匹配“bed”中的“e”，但能匹配“seeeeeeeed”中的所有“e”。
 *	{n,m}	最少匹配 n 次且最多匹配 m 次。“e{1,3}”将匹配“seeeeeeeed”中的前三个“e”。
 *
 *	\t：	制表符，等同于\u0009
 *	\n：	换行符，等同于\u000A
 *	\d：	代表一个数字，等同于[0-9]
 *	\D：	代表非数字，等同于[^0-9]
 *	\s：	代表换行符、Tab制表符等   空白字符
 *	\S：	代表非空白字符
 *	\w：	字母字符，等同于[a-zA-Z_0-9] 包括a-z的小写字符，A-Z的大写字符，下划线和数字。
 *	\W：	非字母字符，等同于[^\w]
 *
 *  正则表达式中每个"()"内的部分算作一个捕获组，每个捕获组都有一个编号，从1,2...，编号0代表整个匹配到的内容。
 *  至于非捕获组，只需要将捕获组中"()"变为"(?:)"即可
 *
 *  String text = "<textarea rows=\"20\" cols=\"70\">nexus maven repository index properties updating index central</textarea>";
 *  下面的正则表达式中共有二个捕获组：(.*?)和整个匹配到的内容，两个非捕获组:(?:</textarea>)和(?:<textarea.*?>)
 *  String reg = "(?:<textarea.*?>)(.*?)(?:</textarea>)";
 *
 *	贪婪匹配：
 *	源字符串：aa<div>test1</div>bb<div>test2</div>cc
 *	正则表达式一：<div>.*</div>
 *	匹配结果一：<div>test1</div>bb<div>test2</div>
 *	正则表达式二：<div>.*?</div>
 *	匹配结果二：<div>test1</div>（这里指的是一次匹配结果，所以没包括<div>test2</div>）
 *
 * [abc] a或b或c
 * . 任意单个字符
 * a? 零个或一个a
 * [^abc] 任意不是abc的字符
 * \s 空格
 * a* 零个或多个a
 * [a-z] a-z的任意字符
 * \S 非空格
 * a+ 一个或多个a
 * [a-zA-Z] a-z或A-Z
 * \d 任意数字
 * a{n} 正好出现n次a
 * ^ 一行开头
 * \D 任意非数字
 * a{n,} 至少出现n次a
 * $ 一行末尾
 * \w 任意字母数字或下划线
 * a{n,m} 出现n-m次a
 * (...) 括号用于分组
 * \W 任意非字母数字或下划线
 * a*? 零个或多个a(非贪婪)
 * (a|b) a或b
 * \b 单词边界 (a)...\1 引用分组
 * (?=a) 前面有a
 * (?!a) 前面没有a
 * \B 非单词边界
 *
 * 正则表达式中有(?=a)和(?!a)来表示我们是否需要匹配某个东西。
 * 要匹配不含hello的字符串 ^(?!.*hello)
 *
 * 一些常用的正则匹配规则:
 　　匹配中文字符的正则表达式： [u4e00-u9fa5]
 　　评注：匹配中文还真是个头疼的事，有了这个表达式就好办了
 　　匹配双字节字符(包括汉字在内)：[^x00-xff]
 　　评注：可以用来计算字符串的长度(一个双字节字符长度计2，ASCII字符计1)
 　　匹配空白行的正则表达式：ns*r
 　　评注：可以用来删除空白行
 　　匹配HTML标记的正则表达式：<(S*?)[^>]*>.*?|<.*? />
 　　评注：网上流传的版本太糟糕，上面这个也仅仅能匹配部分，对于复杂的嵌套标记依旧无能为力
 　　匹配首尾空白字符的正则表达式：^s*|s*$
 　　评注：可以用来删除行首行尾的空白字符(包括空格、制表符、换页符等等)，非常有用的表达式
 　　匹配Email地址的正则表达式：w+([-+.]w+)*@w+([-.]w+)*.w+([-.]w+)*
 　　评注：表单验证时很实用
 　　匹配网址URL的正则表达式：[a-zA-z]+://[^s]*
 　　评注：网上流传的版本功能很有限，上面这个基本可以满足需求
 　　匹配帐号是否合法(字母开头，允许5-16字节，允许字母数字下划线)：^[a-zA-Z][a-zA-Z0-9_]{4,15}$
 　　评注：表单验证时很实用
 　　匹配国内电话号码：d{3}-d{8}|d{4}-d{7}
 　　评注：匹配形式如 0511-4405222 或 021-87888822
 　　匹配腾讯QQ号：[1-9][0-9]{4,}
 　　评注：腾讯QQ号从10000开始
 　　匹配中国邮政编码：[1-9]d{5}(?!d)
 　　评注：中国邮政编码为6位数字
 　　匹配身份证：d{15}|d{18}
 　　评注：中国的身份证为15位或18位
 　　匹配ip地址：d+.d+.d+.d+
 　　评注：提取ip地址时有用

 * @author zc
 *
 */
public class RegexUtils {

	public RegexUtils() {

	}

	/**
	 * A匹配B
	 * @param str "@Shang Hai Hong Qiao Fei Ji Chang"
	 * @param regex "a|F" 表示a或F
	 * @return
	 */
	public static boolean AMatchB(String str, String regex){
		Pattern pat = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
		Matcher mat = pat.matcher(str);
		boolean rs = mat.matches();
		return rs;
	}

	/**
	 * A中有B
	 * @param str "@Shang Hai Hong Qiao Fei Ji Chang"
	 * @param regex "a|F" 表示a或F
	 * @return
	 */
	public static boolean fromAFindB(String str, String regex){
		Pattern pat = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
		Matcher mat = pat.matcher(str);
		boolean rs = mat.find();
		return rs;
	}

	/**
	 * 移除HTML中所有标签
	 * @param html
	 * @return
	 */
	public static String removeHtml(String html){
		//Pattern pattern = Pattern.compile("<(.*)>(.*)<\\/(.*)>|<(.*)\\/>");//移除所有标签及文字
		Pattern pattern = Pattern.compile("<.+?>", Pattern.DOTALL);
		Matcher matcher = pattern.matcher(html);
		String string = matcher.replaceAll("");
		return string;
	}

    /**
     * 验证Email
     * @param email
     */
    public static boolean validateEmail(String email){
        String regex = "[\\w\\.\\-]+@([\\w\\-]+\\.)+[\\w\\-]+";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);
        if(matcher.matches()){
            return true;
        }else{
            return false;
        }
    }

	/**
     * 找到字符串中所有的中文
     * @param str
     */
    public static String getChinese(String str){
        String regex = "[\\u4E00-\\u9FFF]+";//[\u4E00-\u9FFF]为汉字
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);
        StringBuffer sb = new StringBuffer();
        while(matcher.find()){
            sb.append(matcher.group()+"|");
        }
        return sb.toString();
    }

    /**
     * 验证身份证
     * @param a
     * @return
     */
    public static boolean checkID(String a){
    	Pattern pat = Pattern.compile("^\\d{15}|\\d{18}$");
		Matcher mat = pat.matcher(a);
    	return mat.matches();
    }

    /**
     * 捕获--符合表达式的值
     * @param a
     * @param regex
     * @return
     */
	public static int[] fromAGetB(String a, String regex){
		Pattern pat = Pattern.compile(regex);
		Matcher mat = pat.matcher(a);
		String dev = null;
		String dev_tem = null;
		String dev_touch = null;
		int tem = 0;

		//非捕获组
		while(mat.find()){
			dev_tem = mat.group();
			
			if(dev_tem.contains("ABS_MT_POSITION")){//再匹配到它
				dev_touch = dev;
			}else if(dev_tem.contains("/dev/input")){//因为先匹配到它
				tem++;
				dev = dev_tem;
			}

        }

		pat = Pattern.compile("/dev/input/event(\\d+)",Pattern.CASE_INSENSITIVE);
		mat = pat.matcher(dev_touch);
		//捕获组
		if(mat.find()){
			//整个字符串
			//mat.group(0);

			return new int[]{Integer.valueOf(mat.group(1)),tem};
		}

		return new int[]{-1, -1};
	}
}
