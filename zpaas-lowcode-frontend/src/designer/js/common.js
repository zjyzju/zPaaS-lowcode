/**
 * 设计器公共函数
 */


/**
 * 格式化统计报表的展示
 * @param {*} attrAssigns 属性列表
 * @returns [{attrAssigns:[{attrAssigns:[]}]}]
 */
export function formatReportDisplay(attrAssigns){
    var attrAssignList = [];
    if(attrAssigns != null && attrAssigns.length > 0) {
        //格式化表单展示
        var attrAssignMap = {};
        for(var i in attrAssigns) {
            var attr = attrAssigns[i];
            attrAssignMap[attr.id] = attr;
        }
        for(var i in attrAssigns) {
            var attr = attrAssigns[i];
            var parentAssignId = attr.displayCfgJSON.parentAssignId;
            if(parentAssignId != null && parentAssignId != "" && attrAssignMap[parentAssignId] != null) {
                if(attrAssignMap[parentAssignId].attrAssigns == null) {
                    attrAssignMap[parentAssignId].attrAssigns = [];
                }
                attrAssignMap[parentAssignId].attrAssigns.push(attr);
            }
        }
        for(var key in attrAssignMap) {
            if(attrAssignMap[key].displayCfgJSON.parentAssignId == null || attrAssignMap[key].displayCfgJSON.parentAssignId == "") {
                attrAssignList.push(attrAssignMap[key]);
            }
        }
    }
    
    return attrAssignList;
}

/**
 * 格式化表单的展示
 * @param {*} attrAssigns 属性列表
 * @returns {rowIndex: 0, attrAssigns:['attr']}
 */
export function formatFormDisplay(attrAssigns){
    //生成表单属性列表（分行）
    var attrAssignList = [];
    if(attrAssigns.length == 0) {
        attrAssignList.push({rowIndex: 0, attrAssigns:[]});
    }else {
        //格式化表单展示
        var attrAssignMap = {};
        attrAssignMap = formatFormAttrDisplayInfo(attrAssigns);
        for(var key in attrAssignMap) {
            if(attrAssignMap[key] != null && attrAssignMap[key].length != 0) {
                var attrAssignRow = {
                    rowIndex: key,
                    attrAssigns: attrAssignMap[key]
                };
                attrAssignList.push(attrAssignRow);
            }
        }
    }
    
    return attrAssignList;
}

/**
 * 构建属性列表
 * @param {*} formatAttrAssigns {rowIndex: 0, attrAssigns:['attr']}
 * @returns 返回属性的列表
 */
export function getAllAttrAssigns(formatAttrAssigns) {
    var allFilterAttrs = [];
    var index = 1;
    //处理绑定属性
    for(var i in formatAttrAssigns) {
        if(formatAttrAssigns[i].rowIndex != null) {
            for(var j in formatAttrAssigns[i].attrAssigns) {
                formatAttrAssigns[i].attrAssigns[j].displayOrder = index++;
                allFilterAttrs.push(formatAttrAssigns[i].attrAssigns[j]);
            }
        }else {
            formatAttrAssigns[i].displayOrder = index++;
            allFilterAttrs.push(formatAttrAssigns[i]);
        }
    }
    return allFilterAttrs;
}

/**
 * 表单中属性的格式化显示
 * @param {*} attrAssigns 
 * @returns {"rowIndex": [{attr}]},如{0:[{attr1},{attr2}]}
 */
function formatFormAttrDisplayInfo(attrAssigns) {
    var row = 0;
    var attrMap = {};
    var columnCountOfRow = 0;
    attrMap[0] = [];
    for (var index = 0; index < attrAssigns.length; index++) {
        //规范化一些为空的属性
        if (attrAssigns[index].colSpan == null || attrAssigns[index].colSpan == 0 || attrAssigns[index].colSpan == '0') {
            if (attrAssigns[index].displayType == 'H') {
                attrAssigns[index].colSpan = 0;
            } else {
                attrAssigns[index].colSpan = 8;
            }
        } else {
            attrAssigns[index].colSpan = parseInt(attrAssigns[index].colSpan);
        }
        
        if (attrAssigns[index].displayType == 'H') {
            attrAssigns[index].colSpan = 0;
        } else if (attrAssigns[index].colSpan > 24) {//当某个属性的colspan超过24时，独占一行
            attrAssigns[index].colSpan = 24;//colspan最长支持24
        }
        
        if (attrAssigns[index].rowSpan == null || attrAssigns[index].rowSpan == 0  || attrAssigns[index].rowSpan == '0') {
            attrAssigns[index].rowSpan = 1;
        }else {
            attrAssigns[index].rowSpan = parseInt(attrAssigns[index].rowSpan);
        }

        //当前行放不下的情况，需要先进行换行操作
        if (columnCountOfRow != 0 && (columnCountOfRow + attrAssigns[index].colSpan) > 24) {
            attrAssigns[index - 1].colSpan += (24 - columnCountOfRow)//使前一个属性的colspan占满剩下的空间
            columnCountOfRow = 24;//进行换行操作
            if (columnCountOfRow != 0 && columnCountOfRow % 24 == 0) {
                columnCountOfRow = 0;
                row++;
                attrMap[row] = [];
            }
        }
        //处理当前的属性
        attrMap[row].push(attrAssigns[index]);
        columnCountOfRow = columnCountOfRow + attrAssigns[index].colSpan;
        //进行分行判断
        if (columnCountOfRow != 0 && columnCountOfRow % 24 == 0) {
            columnCountOfRow = 0;
            row++;
            attrMap[row] = [];
        }
    }
    return attrMap;
}

/**
 * 服务单元格数据格式化
 * @param {*} attrAssign 
 * @param {*} cellValue 
 * @returns 
 */
export function reportCellValueFormat(attrAssign, cellValue) {
    if(attrAssign.displayCfgJSON.displayFormat == 'P') {
        var retainSize = 0;
        if(attrAssign.displayCfgJSON.formatCfg != null && attrAssign.displayCfgJSON.formatCfg.trim().length > 0) {
            retainSize = parseInt(attrAssign.displayCfgJSON.formatCfg);
        }
        return Math.round(cellValue * 100 * Math.pow(10, retainSize))/Math.pow(10, retainSize) + '%';
    }else if(attrAssign.displayCfgJSON.displayFormat == 'N') {
        var retainSize = 0;
        if(attrAssign.displayCfgJSON.formatCfg != null && attrAssign.displayCfgJSON.formatCfg.trim().length > 0) {
            retainSize = parseInt(attrAssign.displayCfgJSON.formatCfg);
        }
        return Math.round(cellValue * Math.pow(10, retainSize))/Math.pow(10, retainSize);
    }else {
        return cellValue;
    }
    
}