package cn.zpaas.lowcode.utils;

import java.lang.management.ManagementFactory;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 通用工具类
 *
 * @author zjy
 * createTime 2025年04月14日-18:06:57
 */
public class CommonUtils {
    public static final Logger logger = LoggerFactory.getLogger(CommonUtils.class);

    private static final char AT_FLAG = '@';
    private static final String SEMICOLON = ";";
    private static final String UNKNOWN_HOST_STRING = "unknown host";

    /**
     * 私有化构造方法
     */
    private CommonUtils() {
        // do nothing
    }

    /**
     * 获取IPV4地址
     * 
     * @param ipv4
     * @return
     */
    public static String getHostAddr(boolean ipv4) {
        try {
            Enumeration<NetworkInterface> allNetInterfaces = NetworkInterface.getNetworkInterfaces();
            InetAddress ip = null;
            StringBuilder ipAddrBuilder = new StringBuilder();
            String ipAddr = null;
            while (allNetInterfaces.hasMoreElements()) {
                NetworkInterface netInterface = allNetInterfaces.nextElement();
                Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    ip = addresses.nextElement();
                    ipAddr = getIpAddress(ipv4, ip);
                    if(!StringUtils.isBlank(ipAddr)) {
                        ipAddrBuilder.append(ipAddr).append(SEMICOLON);
                    }
                }
            }
            return ipAddrBuilder.toString();
        } catch (Exception e) {
            logger.error("get ip addr failed! ", e);
            return UNKNOWN_HOST_STRING;
        }
    }

    private static String getIpAddress(boolean ipv4, InetAddress inetAddress) {
        if(inetAddress == null || inetAddress.isLoopbackAddress()) {
            return null;
        }
        if(ipv4) {
            if(inetAddress instanceof Inet4Address) {
                return inetAddress.getHostAddress();
            }
        }else {
            if(inetAddress instanceof Inet6Address) {
                return inetAddress.getHostAddress();
            }
        }
        return null;
    }

    /**
     * 16进制转AscII码
     * @param in
     * @return
     */
    public static byte[] hex2Ascii(byte[] in) {
        byte[] temp1 = new byte[1];
        byte[] temp2 = new byte[1];
        byte[] out = new byte[in.length * 2];
        int j = 0;
        for (int i = 0; i < in.length; ++i) {
            temp1[0] = in[i];
            temp1[0] = (byte) (temp1[0] >>> 4);
            temp1[0] = (byte) (temp1[0] & 0xF);
            temp2[0] = in[i];
            temp2[0] = (byte) (temp2[0] & 0xF);
            if ((temp1[0] >= 0) && (temp1[0] <= 9))
                out[j] = (byte) (temp1[0] + 48);
            else if ((temp1[0] >= 10) && (temp1[0] <= 15)) {
                out[j] = (byte) (temp1[0] + 87);
            }

            if ((temp2[0] >= 0) && (temp2[0] <= 9))
                out[(j + 1)] = (byte) (temp2[0] + 48);
            else if ((temp2[0] >= 10) && (temp2[0] <= 15)) {
                out[(j + 1)] = (byte) (temp2[0] + 87);
            }
            j += 2;
        }
        return out;
    }

    /**
     * AscII码转16进制
     * @param in
     * @return
     */
    public static byte[] ascii2Hex(byte[] in) {
        byte[] temp1 = new byte[1];
        byte[] temp2 = new byte[1];
        int i = 0;
        byte[] out = new byte[in.length / 2];
        for (int j = 0; i < in.length; ++j) {
            temp1[0] = in[i];
            temp2[0] = in[(i + 1)];
            if ((temp1[0] >= 48) && (temp1[0] <= 57)) {
                int tmp5352 = 0;
                byte[] tmp5351 = temp1;
                tmp5351[tmp5352] = (byte) (tmp5351[tmp5352] - 48);
                temp1[0] = (byte) (temp1[0] << 4);

                temp1[0] = (byte) (temp1[0] & 0xF0);
            } else if ((temp1[0] >= 97) && (temp1[0] <= 102)) {
                int tmp101100 = 0;
                byte[] tmp10199 = temp1;
                tmp10199[tmp101100] = (byte) (tmp10199[tmp101100] - 87);
                temp1[0] = (byte) (temp1[0] << 4);
                temp1[0] = (byte) (temp1[0] & 0xF0);
            }

            if ((temp2[0] >= 48) && (temp2[0] <= 57)) {
                int tmp149148 = 0;
                byte[] tmp149146 = temp2;
                tmp149146[tmp149148] = (byte) (tmp149146[tmp149148] - 48);

                temp2[0] = (byte) (temp2[0] & 0xF);
            } else if ((temp2[0] >= 97) && (temp2[0] <= 102)) {
                int tmp192191 = 0;
                byte[] tmp192189 = temp2;
                tmp192189[tmp192191] = (byte) (tmp192189[tmp192191] - 87);

                temp2[0] = (byte) (temp2[0] & 0xF);
            }
            out[j] = (byte) (temp1[0] | temp2[0]);
            i += 2;
        }
        return out;
    }

    /**
     * 获取进程标识
     * 
     * @return
     */
    public static String getProcessId() {
        String processName = ManagementFactory.getRuntimeMXBean().getName();
        int index = processName.indexOf(AT_FLAG);
        if (index == -1) {
            return StringUtils.emptyString();
        }
        return processName.substring(0, index);
    }

}
