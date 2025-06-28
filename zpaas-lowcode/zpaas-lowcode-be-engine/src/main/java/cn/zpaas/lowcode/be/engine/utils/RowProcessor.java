package cn.zpaas.lowcode.be.engine.utils;

import com.google.gson.JsonObject;

import cn.zpaas.lowcode.exception.EngineException;

/**
 * description
 *
 * @author zjy
 * createTime 2025年04月27日-09:55:20
 */
@FunctionalInterface
public interface RowProcessor {
    public void process(int rowIndex, JsonObject row) throws EngineException;
}
