package cn.zpaas.lowcode.be.engine.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;

/**
 * Hessian序列化器
 *
 * @author zjy
 * createTime 2025年04月27日-17:23:37
 */
public class HessianSerializer {
    private static Logger logger = LoggerFactory.getLogger(HessianSerializer.class);

	/**
	 * 序列化对象
	 * 
	 * @param obj
	 * @return
	 * @throws IOException
	 */
	public static byte[] serialize(Object obj) throws IOException {
		if (obj == null) {
			return null;
		}
		ByteArrayOutputStream byteArrayOutputStream = null;
		Hessian2Output hessian2Output = null;
		try {
			byteArrayOutputStream = new ByteArrayOutputStream();
			hessian2Output = new Hessian2Output(byteArrayOutputStream);
			hessian2Output.writeObject(obj);
			hessian2Output.flush();
			return byteArrayOutputStream.toByteArray();
		} catch (IOException ex) {
			logger.error("serialize object failed!", ex);
			throw ex;
		} finally {
			if (hessian2Output != null) {
				try {
					hessian2Output.close();
				} catch (IOException e) {

				}
			}
			if (byteArrayOutputStream != null) {
				try {
					byteArrayOutputStream.close();
				} catch (IOException e) {

				}
			}
		}
	}
	
	/**
	 * 反序列化对象
	 * @param bytes
	 * @return
	 * @throws IOException
	 */
	public static Object deserialize(byte[] bytes) throws IOException {
		if (bytes == null) {
			return null;
		}
		ByteArrayInputStream byteArrayInputStream = null;
		Hessian2Input hessian2Input = null;

		try {
			byteArrayInputStream = new ByteArrayInputStream(bytes);
			hessian2Input = new Hessian2Input(byteArrayInputStream);
			return hessian2Input.readObject();
		} catch (IOException ex) {
			logger.error("deserialize object failed!", ex);
			throw ex;
		} finally {
			if (hessian2Input != null) {
				try {
					hessian2Input.close();
				} catch (IOException e) {

				}
			}
			if (byteArrayInputStream != null) {
				try {
					byteArrayInputStream.close();
				} catch (IOException e) {

				}
			}
		}
	}
}
