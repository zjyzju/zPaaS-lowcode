package cn.zpaas.lowcode.be.engine.command;

/**
 * @author zjy
 *	服务调用请求的Command类
 */
public class RequestCommand extends BaseCommand{
	private String httpMethod;//http请求方法
	private String reqUrl;//请求URL
	
	public String getHttpMethod() {
		return httpMethod;
	}
	public void setHttpMethod(String httpMethod) {
		this.httpMethod = httpMethod;
	}
	public String getReqUrl() {
		return reqUrl;
	}
	public void setReqUrl(String reqUrl) {
		this.reqUrl = reqUrl;
	}
}
