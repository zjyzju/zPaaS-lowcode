package cn.zpaas.lowcode.be.engine.ability;

import cn.zpaas.lowcode.bean.PageParam;

/**
 * @author zjy
 * 分页能力提供辅助类
 */
public class PageAbility {
	private static ThreadLocal<PageParam> pageParams = new ThreadLocal<>();
	
	public static PageParam getPageParam() {
		return pageParams.get();
	}
	
	public static void setPageParam(PageParam pageParam) {
		pageParams.set(pageParam);
	}

	public static void remove() {
		pageParams.remove();
	}
}
