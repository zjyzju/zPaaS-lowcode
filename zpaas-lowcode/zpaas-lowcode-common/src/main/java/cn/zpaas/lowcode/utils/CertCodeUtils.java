package cn.zpaas.lowcode.utils;

import java.util.regex.Pattern;

/**
 * 身份证号码校验工具类
 *
 * @author zjy
 * createTime 2025年04月26日-17:37:51
 */
public class CertCodeUtils {
    // 定义判别用户身份证号的正则表达式（15位或者18位，最后一位可以为字母）
    
    private static final Pattern CERT_CODE_PATTERN = Pattern.compile("(^[1-9]\\d{5}(18|19|20)\\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]$)|(^[1-9]\\d{5}\\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\\d{3}$)");
	
	public static boolean validateCertCode(String certCode) {
        if (certCode == null || certCode.trim().length() == 0) {
            return false;
        }
        
        boolean matches = CERT_CODE_PATTERN.matcher(certCode).matches();
        //判断第18位校验值
        if (matches) {
            if (certCode.length() == 18) {
                try {
                    char[] charArray = certCode.toCharArray();
                    //前十七位加权因子
                    int[] idCardWi = {7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2};
                    //这是除以11后，可能产生的11位余数对应的验证码
                    String[] idCardY = {"1", "0", "X", "9", "8", "7", "6", "5", "4", "3", "2"};
                    int sum = 0;
                    for (int i = 0; i < idCardWi.length; i++) {
                        int current = Integer.parseInt(String.valueOf(charArray[i]));
                        int count = current * idCardWi[i];
                        sum += count;
                    }
                    char idCardLast = charArray[17];
                    int idCardMod = sum % 11;
                    if (idCardY[idCardMod].toUpperCase().equals(String.valueOf(idCardLast).toUpperCase())) {
                        return true;
                    } else {
                        return false;
                    }
 
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            }
        }
        return matches;
    }
}
