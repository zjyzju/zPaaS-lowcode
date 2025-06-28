package cn.zpaas.lowcode.be.engine.ability.transfer;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import cn.zpaas.lowcode.utils.JsonUtils;
import cn.zpaas.lowcode.utils.StringUtils;
import cn.zpaas.lowcode.domain.eo.Attribute;
import cn.zpaas.lowcode.exception.EngineException;

/**
 * 字符拼接类转换
 *
 * @author zjy
 * createTime 2025年04月07日-14:29:00
 */
public class MultiConcatTransfer extends Transfer {

    private static final String CONCAT_PATTERN_KEY = "concatPattern";//字符串合并的模式
    private static final String OTHER_CONCAT_ATTRS_KEY = "otherConcatAttrs";//其他需要合并的属性
    private static final String LEFT_PLACEHOLDER = "\\$\\{";//左占位符
    private static final String RIGHT_PLACEHOLDER = "\\}";//右占位符
    
    @Override
    public Object transfer(Attribute fromAttr, Object fromAttrValue, Attribute toAttr, JsonObject transferCfg,
            JsonObject fromObject)  throws EngineException{
        //未配置字符串合并模式时，直接返回源值
        if(transferCfg == null || !transferCfg.has(CONCAT_PATTERN_KEY) 
                || StringUtils.isBlank(JsonUtils.getString(transferCfg, CONCAT_PATTERN_KEY))) {
            return fromAttrValue;        
        }

        //获取合并模式
        String concatPattern = JsonUtils.getString(transferCfg, CONCAT_PATTERN_KEY);
        //合并只能按字符串的方式进行，获取源值的字符值
        String srcValueString = null;
        if(fromAttrValue != null) {
            srcValueString = String.valueOf(fromAttrValue);
        }
        //替换源属性值
        String result = concatPattern.replaceAll(LEFT_PLACEHOLDER+fromAttr.getCode()+RIGHT_PLACEHOLDER, srcValueString);

        //获取其他合并属性列表
        JsonArray otherAttrs = JsonUtils.getJsonArray(transferCfg, OTHER_CONCAT_ATTRS_KEY);
        if(!JsonUtils.isEmpty(otherAttrs)) {
            for(int i=0; i<otherAttrs.size(); i++) {
                String otherAttr = otherAttrs.get(i).getAsString();
                
                //获取属性值
                String otherAttrValue = StringUtils.emptyString();
                Object otherAttrValueObject = JsonUtils.eval(fromObject, otherAttr);
                if(otherAttrValueObject != null) {
                    otherAttrValue = String.valueOf(otherAttrValueObject);
                }
                //替换属性值
                result = result.replaceAll(LEFT_PLACEHOLDER+otherAttr+RIGHT_PLACEHOLDER, otherAttrValue);
            }
        }
        return result;
    }
    
}
