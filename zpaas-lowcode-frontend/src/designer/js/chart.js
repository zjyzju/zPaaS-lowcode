/*
    图表组件的公共函数
*/

/**
 * 图表组件数据处理函数
 * @param {*} keyDataAttrs 图表组件类型的配置数据分类
 * @param {*} chartCfgData 图表组件的配置数据
 * @param {*} dataSetDatas 数据集返回数据
 * @param {*} dataApplyColumnDatas 数据提供字段/属性字典返回的数据
 * @param {*} chartOption 图表的Option数据
 * @param {*} chartType 图表实例
 * @returns 
 */
export function processChartData(keyDataAttrs, chartCfgData, dataSetDatas, dataApplyColumnDatas, chartOption, chartType) {
    //keyDataAttrs中配置了该图表组件可以配置的属性信息，如legend、xAxis、yAxis、series等
    if(keyDataAttrs == null || keyDataAttrs.length == 0) {
        return;//如果未配置的话，不需要进行后续的处理
    }
    //清空原来的图表数据
    chartOption.series = [];//清空原来的图表数据
    if(chartOption.legend != null) {//清空原来的图表数据
        chartOption.legend.data = null;
    }
    
    //用于处理图表数据的维度
    var categoryAttrMap = {};//存放legend、xAxis以及yAxis属性
    var categoryDataMap = {};//存放legend、xAxis以及yAxis属性对应的数据
    var hasLegend = false; //是否有legend属性
    var hasAddedForLegend = false; //用于判断是否已经添加过legend属性
    
    var dataMapForLegend = {};//按legend值拆分后的Map，临时存放series的数据，在存在legend属性的情况下

    //循环该类图表涉及的关键数据列表，如xAxis、yAxis、legend、series等
    for(var index in keyDataAttrs) {
        //当前处理的关键数据
        var keyData = keyDataAttrs[index];
        var dataType = keyData['type'];//数据类型，如xAxis、yAxis、legend、series等
        
        if(chartCfgData[dataType] != null && chartCfgData[dataType].length > 0) {//当前的图表，该类数据有配置属性/指标
            //循环处理为该类数据配置的属性/指标，xAxis、yAxis以及legend只能配置一个属性，series可以配置多个属性
            for(var i in chartCfgData[dataType]) {
                var chartAttr = chartCfgData[dataType][i];//获取配置的属性
                
                var data = [];//初始化该属性对应的数据对象

                //记录legendAttr属性，分类属性（xAxis/yAxis/radar）以及分类属性对应的data数组
                //如果legend属性以及分类属性的数量大于等于2，则需要先按多维的方式处理图表数据
                //如：{legend:'', xAxis: '', yAxis: ''}
                if(dataType == 'legend' || dataType == 'xAxis' || dataType == 'yAxis' || dataType == 'radar') {
                    categoryAttrMap[dataType] = chartAttr;
                    categoryDataMap[dataType] = data;
                }

                /*  data的数据格式说明
                    1、图表数据时-series
                        1.1、饼图：
                            [{'value':'', 'name':''},{'value':'', 'name':''}]
                        1.2、配置了legend/xAxis/yAxis/radar中至少一个
                            [{'value':'', 'xAxis':'', 'yAxis':'', 'legend':'', 'radar':''},{'value':'', 'xAxis':'', 'yAxis':'', 'legend':'', 'radar':''}]
                        1.3、未额外配置legend/xAxis/yAxis/radar
                            [value1,value2,value3,value4,value5]
                    2、其他分类属性legend/xAxis/yAxis/radar，需要去重
                        [value1,value2,value3,value4,value5]
                    3、使用数据提供字段配置时，直接使用后端返回的值，格式要求如上
                */
                //处理并获取当前属性的数据
                processAttrData(chartAttr, chartType, dataType, dataSetDatas, dataApplyColumnDatas, categoryAttrMap, data);
                
                //该类数据的data对象处理好以后，进行图表Option对象属性值的更新
                if('series' == dataType) {//处理图表数据
                    var categorySize = Object.keys(categoryAttrMap).length;//获取分类维度数
                    if(categorySize >= 1 && 'pie' != chartType) {//当存在分类维度时（饼图时已经是目标格式，不需要按legend进行分类）
                        if(categoryAttrMap['legend'] != null) {//判断是否有legend属性，series必须是最后一类处理的数据
                            hasLegend = true; //设置存在legend属性标志为true
                        }
                        var categoryValueLocationMap = {}//如xAxis: {value: location}
                        var categoryCounter = 0; //分类属性的counter
                        //如果有设置legend属性，则需要按legend的数值拆分成数组
                        //拆分后，为保证与坐标系的对应关系不会乱，需要为缺失的位置补0
                        if(hasLegend) {//data的格式为[{'value':'', 'xAxis':'', 'yAxis':'', 'legend':'', 'radar':''}]
                            //循环处理categoryAttrMap（忽略legend属性）
                            for(var key in categoryAttrMap) {
                                if(key == 'legend') {//legend属性忽略
                                    continue;
                                }
                                //构造categoryValueLocationMap用于填补目标数据结构的位置
                                categoryValueLocationMap[key] = {};//初始化对象
                                for(var location in categoryDataMap[key]) {//如{'xAxis':[1,3,5,6]}
                                    //构造{'xAxis':{1:0, 3:1, 5:2, 6:3}}
                                    if('radar' == chartType) {//雷达图的分类属性格式特殊{ name: 'Sales', max: 6500 }
                                        categoryValueLocationMap[key][categoryDataMap[key][location].name] = location;
                                    }else {//其他情况进行了补0操作，按位置进行替换
                                        categoryValueLocationMap[key][categoryDataMap[key][location]] = location;
                                    }
                                }
                                //循环该属性的数据
                                //data的格式为[{'value':'', 'xAxis':'', 'yAxis':'', 'legend':'', 'radar':''}]
                                for(var index in data) {
                                    if(dataMapForLegend[data[index]['legend']] == null) {//如果该legend值的分类对象还未初始化时，进行初始化操作
                                        dataMapForLegend[data[index]['legend']] = {
                                            name: data[index]['legend'],
                                            type: chartType,
                                            data: []
                                        };
                                        //所有分类值对应的位置默认全填0
                                        for(var location in categoryDataMap[key]) {
                                            if(categorySize == 2) {//去除legend后只有一维
                                                dataMapForLegend[data[index]['legend']].data.push('0');//按一维数组处理
                                            }else {//去除legend后超过一维时，数组的元素是数组，多维数组--目前没有作用
                                                if(dataMapForLegend[data[index]['legend']].data[location] == null) {
                                                    dataMapForLegend[data[index]['legend']].data[location] = [];
                                                }
                                                dataMapForLegend[data[index]['legend']].data[location].push('0');
                                            }
                                            
                                        }
                                    }
                                    //将有值的位置填充上具体的值
                                    if(categorySize == 2) {//去除legend后只有一维
                                        dataMapForLegend[data[index]['legend']].data.splice(categoryValueLocationMap[key][data[index][key]],1,data[index].value);
                                    }else {//去除legend后超过一维时，进行了补0操作，按位置进行替换，数组的元素是数组,--目前没有作用
                                        dataMapForLegend[data[index]['legend']].data[categoryValueLocationMap[key][data[index][key]]].splice(categoryCounter,1,data[index].value);
                                    }
                                }
                                categoryCounter++;
                            }
                            //将处理好的dataMapForLegend中分legend值的对象设置到图表Option对象的series中
                            //当series中配置了多个属性时，只需要加一次
                            if(!hasAddedForLegend) {
                                for(var legendValue in dataMapForLegend) {
                                    chartOption.series.push(dataMapForLegend[legendValue]);
                                }
                                hasAddedForLegend = true;
                            }
                            
                        }else {//没有设置legend的情况
                            var seriesRow = {
                                //name: '',
                                type: chartType,
                                data: []
                            };//要构造的目标对象
                            
                            if(categorySize == 1) {//当一维的情况需要补0
                                //循环处理categoryAttrMap，处理补零的逻辑
                                for(var key in categoryAttrMap) {
                                    //构造categoryValueLocationMap用于填补目标数据结构的位置
                                    categoryValueLocationMap[key] = {};//初始化对象
                                    for(var location in categoryDataMap[key]) {//如{'xAxis':[1,3,5,6]}
                                        //构造{'xAxis':{1:0, 3:1, 5:2, 6:3}}
                                        if('radar' == chartType) {//雷达图的分类属性格式特殊{ name: 'Sales', max: 6500 }
                                            categoryValueLocationMap[key][categoryDataMap[key][location].name] = location;
                                        }else {//其他情况进行了补0操作，按位置进行替换
                                            categoryValueLocationMap[key][categoryDataMap[key][location]] = location;
                                        }
                                        seriesRow.data.push('0');//按一维数组处理
                                    }
                                    for(var index in data) {
                                        seriesRow.data.splice(categoryValueLocationMap[key][data[index][key]],1,data[index].value);
                                    }
                                }
                            }else {//多维的情况，如热力图
                                for(var index in data) {
                                    var row = [];
                                    for(var key in categoryAttrMap) {
                                        row.push(data[index][key]);
                                    }
                                    row.push(data[index].value);
                                    seriesRow.data.push(row);
                                }
                            }
                            chartOption.series.push(seriesRow);
                        }
                    }else {//没有设置分类维度或是饼图时，可以直接设置
                        if('pie' == chartType && categoryDataMap['legend'] != null) {//补充缺失的分类值
                            var dataAltered = [];
                            for(var location in categoryDataMap['legend']) {//如[{'value':'', 'name':''},{'value':'', 'name':''}]
                                var found = false;
                                for(var dataIndex in data) {
                                    if(data[dataIndex].name == categoryDataMap['legend'][location]) {
                                        dataAltered.push(data[dataIndex]);
                                        found = true;
                                        break;
                                    }
                                }
                                if(!found) {
                                    dataAltered.push({'name': categoryDataMap['legend'][location], 'value': 0});
                                }
                            }
                            data = dataAltered;
                        }
                        chartOption.series.push({
                            name: chartAttr.displayName,
                            type: chartType,
                            data: data
                        });
                    }
                }else {//处理其他分类数据，主要针对legend/radar/xAxis/yAxis
                    if('pie' != chartType) {//饼图不需要设置额外的这些属性，只设置series即可
                        if(data.length > 0) {//该属性的数据不为空时的逻辑，即有值的情况下
                            if('legend' == dataType) {//legend属性的设置格式
                                chartOption[dataType] = {
                                    'data': data
                                }
                            }else if('radar' == dataType) {//radar属性的设置格式
                                chartOption[dataType] = {
                                    'indicator': data
                                }
                            } else {
                                chartOption[dataType] = {//xAxis/yAxis的设置格式
                                    'type': 'category',
                                    'boundaryGap': true,
                                    'data': data
                                }
                            }
                        }else {//该属性的数据为空时，即没值的情况下，设置为默认的值，主要针对xAxis/yAxis，legend不需要设置默认值，radar是必须配置的属性不会使用该逻辑
                            if('legend' != dataType) {
                                chartOption[dataType] = {
                                    type: 'value'
                                }
                            }
                        }
                    }
                }
            }
        }else {//当前的图表，该类数据没有配置属性/指标，使用默认值或不设置该属性
            if('series' == dataType) {
                chartOption.series.push({
                    type: chartType,
                    data: null
                });
            }else {
                if('legend' != dataType) {
                    chartOption[dataType] = {
                        type: 'value'
                    }
                }
            }
        }
    }
    if(!hasLegend && ('scatter' == chartType || 'candlestick' == chartType)) {//散点图/K线图
        //转换成多维坐标数组,K线图和散点图
        var scatterDatas = [];
        for(var seriesIndex in chartOption.series) {
            var seriesItem = chartOption.series[seriesIndex];
            for(var index in seriesItem.data) {
                if(scatterDatas[index] == null) {
                    scatterDatas[index] = [];
                }
                scatterDatas[index].push(seriesItem.data[index]);
            }
        }
        chartOption.series = [];
        chartOption.series.push({
            type: chartType,
            data: scatterDatas
        });
    }else if('gauge' == chartType) {//仪表盘
        if(chartOption.series.length > 0) {
            var gaugeData = chartOption.series[0];
            gaugeData.progress =  {
                show: true
            };
            gaugeData.detail = {
                valueAnimation: true,
                formatter: '{value}'
            };
            if(gaugeData.data != null && gaugeData.data.length > 0) {
                var value = gaugeData.data[0];
                gaugeData.data = [{
                    'value': value,
                    'name': gaugeData.name
                }];
            }
        }
    }else if('radar' == chartType) {//有分类的雷达图
        var radarSerie = {
            'type': chartType,
            'data': []
        };
        for(var seriesIndex in chartOption.series) {
            var seriesItem = chartOption.series[seriesIndex];
            
            if(seriesItem.name != null) {
                var radarSerieItem = {
                    'name': seriesItem.name,
                    'value': seriesItem.data
                }
                radarSerie.data.push(radarSerieItem);
            }else {
                radarSerie.data.push(seriesItem.data);
            }
        }
        chartOption.series = [];
        chartOption.series.push(radarSerie);
    }
}

/*  data的数据格式说明
    1、图表数据时-series
        1.1、饼图：
            [{'value':'', 'name':''},{'value':'', 'name':''}]
        1.2、配置了legend/xAxis/yAxis/radar中至少一个
            [{'value':'', 'xAxis':'', 'yAxis':'', 'legend':'', 'radar':''},{'value':'', 'xAxis':'', 'yAxis':'', 'legend':'', 'radar':''}]
        1.3、未额外配置legend/xAxis/yAxis/radar
            [value1,value2,value3,value4,value5]
    2、其他分类属性legend/xAxis/yAxis/radar，需要去重
        [value1,value2,value3,value4,value5]
    3、使用数据提供字段配置时，直接使用后端返回的值，格式要求如上
*/
/**
 * 处理并获取属性数据
 * @param {*} chartAttr 当前处理的属性
 * @param {*} chartType 图表组件类型
 * @param {*} dataType 图表数据分类类型
 * @param {*} dataSetDatas 数据集返回的数据
 * @param {*} dataApplyColumnDatas 数据提供字段/属性字典返回的数据
 * @param {*} categoryAttrMap 分类属性Map
 * @param {*} data 用于返回数据的对象
 */
function processAttrData(chartAttr, chartType, dataType, dataSetDatas, dataApplyColumnDatas, categoryAttrMap, data) {
    //循环从后端获取的数据集，拼装该类数据的data数组
    if(chartAttr.dataSetDetail != null && dataApplyColumnDatas[chartAttr.dataSetDetail.content.code] != null) {//数据提供字段/配置了字典，从dataApplyColumnDatas中取数
        //使用数据提供字段配置/配置了字典时，直接使用后端返回的值
        for(var dataCount in dataApplyColumnDatas[chartAttr.dataSetDetail.content.code]) {
            data.push(dataApplyColumnDatas[chartAttr.dataSetDetail.content.code][dataCount]);
        }
    }else if(chartAttr.id != null && dataApplyColumnDatas[chartAttr.id] != null) {//数据提供字段/配置了字典，从dataApplyColumnDatas中取数(引擎端)
        //使用数据提供字段配置/配置了字典时，直接使用后端返回的值
        for(var dataCount in dataApplyColumnDatas[chartAttr.id]) {
            data.push(dataApplyColumnDatas[chartAttr.id][dataCount]);
        }
    }else if(chartAttr.dataSetDetail != null && chartAttr.dataSetDetail.id != null) {//数据集的属性，从dataSetDatas里取数
        //获取字段编码
        var columnAlias = chartAttr.dataSetDetail.content.code;
        if(chartAttr.dataSetDetail.detailContentAlias != null && chartAttr.dataSetDetail.detailContentAlias.length > 0) {//如果设置了别名
            columnAlias = chartAttr.dataSetDetail.detailContentAlias;
        }
        for(var j in dataSetDatas) {
            //当前数据类型是图表数据且分类属性大于等于1时，需要按多维数据的方式处理；饼图的数据也走该逻辑
            if('series' == dataType && (Object.keys(categoryAttrMap).length >= 1 || 'pie' == chartType)) {
                //数据格式为{'value':'','categoryKey':''/'name':''}
                var row = {};
                //设置当前数据类型的值
                row['value'] = dataSetDatas[j][columnAlias];
                if('pie' == chartType) {//如果是饼图
                    //设置分类属性的值,饼图只有一个分类属性
                    //数据格式为{'value':'', 'name':''}
                    for(var categoryKey in categoryAttrMap) {
                        var categoryAttrColumnAlias = categoryAttrMap[categoryKey].dataSetDetail.content.code;
                        if(categoryAttrMap[categoryKey].dataSetDetail.detailContentAlias != null && categoryAttrMap[categoryKey].dataSetDetail.detailContentAlias.length > 0) {
                            categoryAttrColumnAlias = categoryAttrMap[categoryKey].dataSetDetail.detailContentAlias;
                        }
                        row['name'] = dataSetDatas[j][categoryAttrColumnAlias];
                    }
                }else {//非饼图的情况
                    //设置分类属性的值，可能会存在多个分类属性
                    //数据格式为{'value':'', 'xAxis':'', 'yAxis':'', 'legend':'', 'radar':''}
                    for(var categoryKey in categoryAttrMap) {
                        var categoryAttrColumnAlias = categoryAttrMap[categoryKey].dataSetDetail.content.code;
                        if(categoryAttrMap[categoryKey].dataSetDetail.detailContentAlias != null && categoryAttrMap[categoryKey].dataSetDetail.detailContentAlias.length > 0) {
                            categoryAttrColumnAlias = categoryAttrMap[categoryKey].dataSetDetail.detailContentAlias;
                        }
                        row[categoryKey] = dataSetDatas[j][categoryAttrColumnAlias];
                    }
                }
                data.push(row);//设置到属性对应的数据对象中
            }else {//其他情况，不需要进行分类处理
                if('series' == dataType) {//图表数据时
                    data.push(dataSetDatas[j][columnAlias]);
                }else {//其他情况下，需要去除重复值，主要是各类分类属性
                    if(!data.includes(dataSetDatas[j][columnAlias])) {
                        data.push(dataSetDatas[j][columnAlias]);
                    }
                }
            }
        }
    }
}

const chartSampleDatas = {//图表组件的示例数据
    'line': {
        tooltip: {
            trigger: 'axis'
        },
        xAxis: {
            type: 'category',
            data: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun']
        },
        yAxis: {
            type: 'value'
        },
        series: [
            {
                data: [150, 230, 224, 218, 135, 147, 260],
                type: 'line'
            }
        ]
    },
    'bar': {
        tooltip: {
            trigger: 'axis'
        },
        xAxis: {
            type: 'category',
            data: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun']
        },
        yAxis: {
            type: 'value'
        },
        series: [
            {
                data: [120, 200, 150, 80, 70, 110, 130],
                type: 'bar'
            }
        ]
    },
    'pie': {
        tooltip: {
            trigger: 'item'
        },
        legend: {
            orient: 'vertical',
            left: 'left'
        },
        series: [
            {
            name: 'Access From',
            type: 'pie',
            radius: '50%',
            data: [
                { value: 1048, name: 'Search Engine' },
                { value: 735, name: 'Direct' },
                { value: 580, name: 'Email' },
                { value: 484, name: 'Union Ads' },
                { value: 300, name: 'Video Ads' }
            ],
            emphasis: {
                itemStyle: {
                shadowBlur: 10,
                shadowOffsetX: 0,
                shadowColor: 'rgba(0, 0, 0, 0.5)'
                }
            }
            }
        ]
    },
    'scatter': {
        tooltip: {
            trigger: 'axis'
        },
        xAxis: {},
        yAxis: {},
        series: [
            {
                symbolSize: 20,
                data: [
                    [10.0, 8.04],
                    [8.07, 6.95],
                    [13.0, 7.58],
                    [9.05, 8.81],
                    [11.0, 8.33],
                    [14.0, 7.66],
                    [13.4, 6.81],
                    [10.0, 6.33],
                    [14.0, 8.96],
                    [12.5, 6.82],
                    [9.15, 7.2],
                    [11.5, 7.2],
                    [3.03, 4.23],
                    [12.2, 7.83],
                    [2.02, 4.47],
                    [1.05, 3.33],
                    [4.05, 4.96],
                    [6.03, 7.24],
                    [12.0, 6.26],
                    [12.0, 8.84],
                    [7.08, 5.82],
                    [5.02, 5.68]
                ],
                type: 'scatter'
            }
        ]
    },
    'candlestick': {
        tooltip: {
            trigger: 'axis'
        },
        xAxis: {
            data: ['2017-10-24', '2017-10-25', '2017-10-26', '2017-10-27']
        },
        yAxis: {},
        series: [
            {
                type: 'candlestick',
                data: [
                    [20, 34, 10, 38],
                    [40, 35, 30, 50],
                    [31, 38, 33, 44],
                    [38, 15, 5, 42]
                ]
            }
        ]
    },
    'radar': {
        tooltip: {
            trigger: 'item'
        },
        legend: {
            data: ['Allocated Budget', 'Actual Spending']
        },
        radar: {
            // shape: 'circle',
            indicator: [
                { name: 'Sales', max: 6500 },
                { name: 'Administration', max: 16000 },
                { name: 'Information Technology', max: 30000 },
                { name: 'Customer Support', max: 38000 },
                { name: 'Development', max: 52000 },
                { name: 'Marketing', max: 25000 }
            ]
        },
        series: [
            {
            name: 'Budget vs spending',
            type: 'radar',
            data: [
                {
                    value: [4200, 3000, 20000, 35000, 50000, 18000],
                    name: 'Allocated Budget'
                },
                {
                    value: [5000, 14000, 28000, 26000, 42000, 21000],
                    name: 'Actual Spending'
                }
            ]
            }
        ]
    },
    'heatmap': {
        tooltip: {
            position: 'top'
        },
        grid: {
            height: '50%',
            top: '10%'
        },
        xAxis: {
            type: 'category',
            data: [
                '12a', '1a', '2a', '3a', '4a', '5a', '6a',
                '7a', '8a', '9a', '10a', '11a',
                '12p', '1p', '2p', '3p', '4p', '5p',
                '6p', '7p', '8p', '9p', '10p', '11p'
            ],
            splitArea: {
                show: true
            }
        },
        yAxis: {
            type: 'category',
            data: [
                'Saturday', 'Friday', 'Thursday',
                'Wednesday', 'Tuesday', 'Monday', 'Sunday'
            ],
            splitArea: {
                show: true
            }
        },
        visualMap: {
            min: 0,
            max: 10,
            calculable: true,
            orient: 'horizontal',
            left: 'center',
            bottom: '15%'
        },
        series: [
            {
            name: 'Punch Card',
            type: 'heatmap',
            data: [[0, 0, 5], [0, 1, 1], [0, 2, 0], [0, 3, 0], [0, 4, 0], [0, 5, 0], [0, 6, 0], [0, 7, 0], [0, 8, 0], [0, 9, 0], [0, 10, 0], [0, 11, 2], [0, 12, 4], [0, 13, 1], [0, 14, 1], [0, 15, 3], [0, 16, 4], [0, 17, 6], [0, 18, 4], [0, 19, 4], [0, 20, 3], [0, 21, 3], [0, 22, 2], [0, 23, 5], [1, 0, 7], [1, 1, 0], [1, 2, 0], [1, 3, 0], [1, 4, 0], [1, 5, 0], [1, 6, 0], [1, 7, 0], [1, 8, 0], [1, 9, 0], [1, 10, 5], [1, 11, 2], [1, 12, 2], [1, 13, 6], [1, 14, 9], [1, 15, 11], [1, 16, 6], [1, 17, 7], [1, 18, 8], [1, 19, 12], [1, 20, 5], [1, 21, 5], [1, 22, 7], [1, 23, 2], [2, 0, 1], [2, 1, 1], [2, 2, 0], [2, 3, 0], [2, 4, 0], [2, 5, 0], [2, 6, 0], [2, 7, 0], [2, 8, 0], [2, 9, 0], [2, 10, 3], [2, 11, 2], [2, 12, 1], [2, 13, 9], [2, 14, 8], [2, 15, 10], [2, 16, 6], [2, 17, 5], [2, 18, 5], [2, 19, 5], [2, 20, 7], [2, 21, 4], [2, 22, 2], [2, 23, 4], [3, 0, 7], [3, 1, 3], [3, 2, 0], [3, 3, 0], [3, 4, 0], [3, 5, 0], [3, 6, 0], [3, 7, 0], [3, 8, 1], [3, 9, 0], [3, 10, 5], [3, 11, 4], [3, 12, 7], [3, 13, 14], [3, 14, 13], [3, 15, 12], [3, 16, 9], [3, 17, 5], [3, 18, 5], [3, 19, 10], [3, 20, 6], [3, 21, 4], [3, 22, 4], [3, 23, 1], [4, 0, 1], [4, 1, 3], [4, 2, 0], [4, 3, 0], [4, 4, 0], [4, 5, 1], [4, 6, 0], [4, 7, 0], [4, 8, 0], [4, 9, 2], [4, 10, 4], [4, 11, 4], [4, 12, 2], [4, 13, 4], [4, 14, 4], [4, 15, 14], [4, 16, 12], [4, 17, 1], [4, 18, 8], [4, 19, 5], [4, 20, 3], [4, 21, 7], [4, 22, 3], [4, 23, 0], [5, 0, 2], [5, 1, 1], [5, 2, 0], [5, 3, 3], [5, 4, 0], [5, 5, 0], [5, 6, 0], [5, 7, 0], [5, 8, 2], [5, 9, 0], [5, 10, 4], [5, 11, 1], [5, 12, 5], [5, 13, 10], [5, 14, 5], [5, 15, 7], [5, 16, 11], [5, 17, 6], [5, 18, 0], [5, 19, 5], [5, 20, 3], [5, 21, 4], [5, 22, 2], [5, 23, 0], [6, 0, 1], [6, 1, 0], [6, 2, 0], [6, 3, 0], [6, 4, 0], [6, 5, 0], [6, 6, 0], [6, 7, 0], [6, 8, 0], [6, 9, 0], [6, 10, 1], [6, 11, 0], [6, 12, 2], [6, 13, 1], [6, 14, 3], [6, 15, 4], [6, 16, 0], [6, 17, 0], [6, 18, 0], [6, 19, 0], [6, 20, 1], [6, 21, 2], [6, 22, 2], [6, 23, 6]]
                .map(function (item) {
                return [item[1], item[0], item[2] || '-'];}),
            label: {
                show: true
            },
            emphasis: {
                itemStyle: {
                shadowBlur: 10,
                shadowColor: 'rgba(0, 0, 0, 0.5)'
                }
            }
            }
        ]
    },
    'gauge':  {
        tooltip: {
            formatter: '{a} <br/>{b} : {c}%'
        },
        series: [
            {
                name: 'Pressure',
                type: 'gauge',
                progress: {
                    show: true
                },
                detail: {
                    valueAnimation: true,
                    formatter: '{value}'
                },
                data: [
                    {
                        value: 50,
                        name: 'SCORE'
                    }
                ]
            }
        ]
    }
};

/**
 * 返回图表的样例数据
 * @param {*} chartType 
 * @returns 
 */
export function getSampleChartData(chartType) {
    return JSON.parse(JSON.stringify(chartSampleDatas[chartType]));
}

const chartComponents = [
    {
        name: '折线图',
        type: 'line',
        keyDataAttrs: [{name:'X轴',type:'xAxis'},{name:'Y轴',type:'yAxis'},{name:'分类',type:'legend'},{name:'数据',type:'series', width:1}],
        paths: ['M25.2739189,111.655778 C26.7855309,111.657407 28.2007506,110.913642 29.0567232,109.667735 L69.3606163,51.4346379 L108.642876,94.5825372 C109.596174,95.6275458 110.977093,96.1760957 112.387618,96.0700817 C113.798143,95.9640677 115.081554,95.2152689 115.86794,94.0395069 L167.15209,17.582681 C168.126301,16.2187314 168.281888,14.4329668 167.558237,12.9210891 C166.834585,11.4092114 165.346152,10.4103472 163.672804,10.3136363 C161.999456,10.2169253 160.40586,11.0376639 159.512851,12.4561069 L111.496086,84.0348638 L72.2782537,40.9605957 C71.3313898,39.926718 69.968454,39.3754562 68.5690807,39.4603596 C67.1694417,39.5586882 65.8899144,40.2864616 65.0900052,41.4391988 L21.4911146,104.439918 C20.5200574,105.846972 20.4090348,107.676368 21.202809,109.190526 C21.9965833,110.704685 23.5643138,111.65402 25.2739189,111.655778','M170.272214,127.854648 L9.20390349,127.854648 L9.20390349,5.00465881 C9.20390349,2.46307104 7.14353951,0.402707066 4.60195174,0.402707066 C2.06036398,0.402707066 3.11254732e-16,2.46307104 0,5.00465881 L0,132.4566 C3.11254728e-16,134.998188 2.06036395,137.058552 4.60195168,137.058552 L170.272214,137.058552 C172.813801,137.058552 174.874165,134.998188 174.874165,132.4566 C174.874165,129.915012 172.813801,129.257355 170.272214,129.257355']
    },
    {
        name: '柱状图',
        type: 'bar',
        keyDataAttrs: [{name:'X轴',type:'xAxis'},{name:'Y轴',type:'yAxis'},{name:'分类',type:'legend'},{name:'数据',type:'series', width:1}],
        paths: ['M121.488231,0 L102.050114,0 C99.36627,-1.1969592e-16 97.1905846,2.17568537 97.1905846,4.85952929 L97.1905846,133.140471 C97.1905846,135.824315 99.36627,138 102.050114,138 L121.488231,138 C124.172075,138 126.34776,135.824315 126.34776,133.140471 L126.34776,4.85952929 C126.34776,2.17568537 124.172075,1.1969592e-16 121.488231,0 Z M170.083523,30.1571753 L150.645406,30.1571753 C147.961562,30.1571753 145.785877,32.3328607 145.785877,35.0167046 L145.785877,132.207289 C145.785877,134.891133 147.961562,137.066819 150.645406,137.066819 L170.083523,137.066819 C172.767367,137.066819 174.943052,134.891133 174.943052,132.207289 L174.943052,35.0167046 C174.943052,32.3328607 172.767367,30.1571753 170.083523,30.1571753 Z M53.4548215,39.8762339 C50.7709777,39.876234 48.5952924,42.0519193 48.5952924,44.7357631 L48.5952924,132.207289 C48.5952924,134.891133 50.7709777,137.066818 53.4548215,137.066819 L72.8929386,137.066819 C75.5767824,137.066818 77.7524677,134.891133 77.7524677,132.207289 L77.7524677,44.7357631 C77.7524676,42.0519193 75.5767824,39.876234 72.8929386,39.8762339 L53.4548215,39.8762339 Z M29.1571753,67.17388 L29.1571753,132.207289 C29.1571753,134.891133 26.9814901,137.066818 24.2976462,137.066819 L4.85952929,137.066819 C2.17568537,137.066819 3.28676086e-16,134.891133 0,132.207289 L0,67.17388 C7.33863613e-08,64.4900361 2.17568542,62.3143508 4.85952929,62.3143508 L24.2976462,62.3143508 C26.98149,62.3143509 29.1571753,64.4900362 29.1571753,67.17388 Z']
    },
    {
        name: '饼状图',
        type: 'pie',
        keyDataAttrs: [{name:'分类',type:'legend'},{name:'数据',type:'series', width:1}],
        paths: ['M72.1778824,12.9606446 L72.1778824,73.6462877 C72.1779317,75.129331 72.9386663,76.5065116 74.1881954,77.2856176 C75.4377246,78.0647237 76.9977039,78.1345538 78.3105882,77.4701504 L128.354824,52.1402676 L132.666353,49.9503148 C134.757503,48.8920984 137.302156,49.7461658 138.350118,51.857969 L138.468706,52.1060495 L138.782118,52.8930638 C140.942118,58.6416896 144,65.8873535 144,83.8518095 C144,116.316148 115.123765,152.612903 73.2282353,152.612903 C31.3242354,152.612903 0,119.994584 0,80.8406245 C0,44.5353146 27.216,13.7391044 62.2588236,9.22232682 C64.122353,8.98280072 65.9265883,8.80315611 67.6715294,8.69194766 C68.8395088,8.61630135 69.986418,9.03213213 70.8400527,9.84074781 C71.6936873,10.6493635 72.1779411,11.7786714 72.1778824,12.9606446 Z M90.0254118,0.659269609 C88.9814749,0.488081123 87.9163292,0.795987208 87.1201941,1.49909291 C86.324059,2.20219861 85.8798887,3.22724309 85.9087059,4.29493319 L85.9087059,54.6039644 C85.9087059,56.9393437 87.6028236,57.8717845 89.5680001,56.7511446 L133.149176,31.9088684 C134.092985,31.4117209 134.762111,30.50855 134.968019,29.4538349 C135.173928,28.3991197 134.894328,27.3070442 134.208,26.4853136 C134.208,26.4853136 133.538824,25.5357637 132.446118,24.3381334 C127.296,18.6921614 122.832,14.7399812 116.860235,10.9417819 C109.345997,6.07770841 100.978781,2.71218494 92.2108235,1.0271132 C89.9745882,0.599388123 90.0254118,0.659269609 90.0254118,0.659269609 L90.0254118,0.659269609 Z']
    },
    {
        name: '散点图',
        type: 'scatter',
        keyDataAttrs: [{name:'数据',type:'series', width:1}],
        paths: ['M173.526285,129.202529 C172.663252,128.339495 171.492727,127.854648 170.272214,127.854648 L9.20390349,127.854648 L9.20390349,4.60195168 C9.20390349,2.06036391 7.14353951,-6.74112924e-08 4.60195174,-6.74112925e-08 C2.06036398,-6.74112927e-08 3.11254732e-16,2.06036391 0,4.60195168 L0,132.4566 C3.11254728e-16,134.998188 2.06036395,137.058552 4.60195168,137.058552 L170.272214,137.058552 C171.492727,137.058552 172.663252,136.573705 173.526285,135.710671 C174.389318,134.847638 174.874165,133.677113 174.874165,132.4566 C174.874165,131.236086 174.389318,130.065562 173.526285,129.202529 Z M151.864407,12.8058552 C155.15291,12.8054572 158.191783,14.5596242 159.836149,17.4074853 C161.480516,20.2553464 161.480516,23.7641708 159.836149,26.6120319 C158.191783,29.459893 155.15291,31.21406 151.864407,31.213662 C146.781666,31.2130468 142.661617,27.0924991 142.661617,22.0097586 C142.661617,16.9270181 146.781666,12.8064703 151.864407,12.8058552 Z M142.660503,58.8253723 C147.743679,58.8253723 151.864407,62.9461003 151.864407,68.0292758 C151.864407,73.1124513 147.743679,77.2331793 142.660503,77.2331793 C137.577328,77.2331793 133.4566,73.1124513 133.4566,68.0292758 C133.4566,62.9461003 137.577328,58.8253723 142.660503,58.8253723 Z M124.252696,40.4175655 C127.5412,40.4171675 130.580072,42.1713345 132.224439,45.0191956 C133.868806,47.8670567 133.868806,51.3758811 132.224439,54.2237422 C130.580072,57.0716033 127.5412,58.8257703 124.252696,58.8253723 C119.169956,58.8247572 115.049907,54.7042094 115.049907,49.6214689 C115.049907,44.5387284 119.169956,40.4181807 124.252696,40.4175655 L124.252696,40.4175655 Z M105.84489,22.0097586 C109.133393,22.0093606 112.172266,23.7635277 113.816632,26.6113888 C115.460999,29.4592498 115.460999,32.9680743 113.816632,35.8159354 C112.172266,38.6637965 109.133393,40.4179635 105.84489,40.4175655 C100.762149,40.4169503 96.6421001,36.2964026 96.6421001,31.2136621 C96.6421001,26.1309216 100.762149,22.0103738 105.84489,22.0097586 L105.84489,22.0097586 Z M105.84489,58.8253723 C110.928065,58.8253723 115.048793,62.9461003 115.048793,68.0292758 C115.048793,73.1124513 110.928065,77.2331793 105.84489,77.2331793 C100.761714,77.2331793 96.6409861,73.1124513 96.6409861,68.0292758 C96.6409861,62.9461003 100.761714,58.8253723 105.84489,58.8253723 L105.84489,58.8253723 Z M87.4370826,77.2331793 C90.8200008,77.0784213 94.0146452,78.7938663 95.7543045,81.6993216 C97.4939639,84.6047768 97.4975937,88.2308616 95.7637545,91.1397939 C94.0299154,94.0487262 90.8387117,95.7705634 87.4554905,95.6225784 C82.5370015,95.4074394 78.6587219,91.3598697 78.6537937,86.4366802 C78.6488656,81.5134907 82.5190342,77.4581647 87.4370826,77.2331793 L87.4370826,77.2331793 Z M59.8253723,68.0292758 C63.1138756,68.0288778 66.1527483,69.7830448 67.7971148,72.6309059 C69.4414814,75.478767 69.4414814,78.9875915 67.7971148,81.8354525 C66.1527483,84.6833136 63.1138756,86.4374807 59.8253723,86.4370826 C54.7426318,86.4364675 50.6225828,82.3159197 50.6225828,77.2331792 C50.6225828,72.1504387 54.7426318,68.029891 59.8253723,68.0292758 L59.8253723,68.0292758 Z M32.213662,95.6409861 C35.5021653,95.6405881 38.541038,97.3947552 40.1854045,100.242616 C41.8297711,103.090477 41.8297711,106.599302 40.1854045,109.447163 C38.541038,112.295024 35.5021653,114.049191 32.213662,114.048793 C27.1309215,114.048178 23.0108725,109.92763 23.0108725,104.84489 C23.0108725,99.762149 27.1309215,95.6416013 32.213662,95.6409861 L32.213662,95.6409861 Z']
    },
    {
        name: 'K线图',
        type: 'candlestick',
        keyDataAttrs: [{name:'X轴',type:'xAxis'},{name:'Y轴',type:'yAxis'},{name:'数据',type:'series', width:1}],
        paths: ['M121.508001,34.0222403 L116.647681,33.0222403 L116.647681,4.86032 C116.647681,2.17603934 114.471642,-7.11961547e-08 111.787361,-7.11961549e-08 C109.10308,-7.1196155e-08 106.927041,2.17603934 106.927041,4.86032 L106.927041,33.0222403 L102.066721,33.0222403 C100.777684,33.0222403 99.5414424,33.5343082 98.6299556,34.4457951 C97.7184688,35.3572819 97.2064009,36.5935234 97.2064009,37.8825604 L97.2064009,105.927041 C97.2064009,107.216078 97.7184688,108.452319 98.6299556,109.363806 C99.5414424,110.275293 100.777684,110.787361 102.066721,110.787361 L106.927041,110.787361 L106.927041,125.572455 C106.927041,128.256735 109.10308,130.432775 111.787361,130.432775 C114.471642,130.432775 116.647681,128.256735 116.647681,125.572455 L116.647681,110.787361 L121.508001,110.787361 C122.797038,110.787361 124.03328,110.275293 124.944766,109.363806 C125.856253,108.452319 126.368321,107.216078 126.368321,105.927041 L126.368321,37.8825604 C126.368321,36.5935233 125.856253,35.3572819 124.944766,34.445795 C124.03328,33.5343082 122.797038,33.0222402 121.508001,33.0222403 L121.508001,34.0222403 Z M170.111202,41.6032004 L165.250881,41.6032004 L165.250881,27.0222403 C165.250881,24.3379598 163.074842,22.1619207 160.390561,22.1619207 C157.706281,22.1619207 155.530242,24.3379598 155.530241,27.0222403 L155.530241,41.6032004 L150.669921,41.6032004 C149.380884,41.6032004 148.144643,42.1152684 147.233156,43.0267552 C146.321669,43.938242 145.809601,45.1744835 145.809601,46.4635205 L145.809601,75.6254408 C145.809601,78.3097214 147.985641,80.4857608 150.669921,80.4857608 L155.530241,80.4857608 L155.530241,104.729037 C155.530241,107.413318 157.706281,109.589357 160.390561,109.589357 C163.074842,109.589357 165.250881,107.413318 165.250881,104.729037 L165.250881,80.4857608 L170.111202,80.4857608 C172.795482,80.4857608 174.971522,78.3097214 174.971522,75.6254408 L174.971522,46.4635205 C174.971522,45.1744835 174.459454,43.938242 173.547967,43.0267552 C172.63648,42.1152684 171.400239,41.6032004 170.111202,41.6032004 L170.111202,41.6032004 Z M72.9048007,84.3460808 L68.0444807,84.3460808 L68.0444807,55.1841605 C68.0444805,52.4998799 65.8684412,50.3238407 63.1841606,50.3238407 C60.49988,50.3238407 58.3238406,52.4998799 58.3238405,55.1841605 L58.3238405,84.3460808 L53.4635205,84.3460808 C52.1744835,84.3460807 50.938242,84.8581487 50.0267552,85.7696355 C49.1152683,86.6811224 48.6032003,87.9173639 48.6032004,89.2064009 L48.6032004,118.368321 C48.6032004,119.657358 49.1152684,120.8936 50.0267552,121.805086 C50.938242,122.716573 52.1744835,123.228641 53.4635205,123.228641 L58.3238405,123.228641 L58.3238405,132.949281 C58.3238406,135.633562 60.49988,137.809601 63.1841606,137.809601 C65.8684412,137.809601 68.0444805,135.633562 68.0444807,132.949281 L68.0444807,123.228641 L72.9048007,123.228641 C75.5890813,123.228641 77.7651206,121.052602 77.7651206,118.368321 L77.7651206,89.2064009 C77.7651207,87.9173639 77.2530527,86.6811224 76.3415659,85.7696356 C75.4300791,84.8581488 74.1938377,84.3460808 72.9048007,84.3460808 Z M29.1619203,41.6032004 L29.1619203,90.2064009 C29.1619203,92.8906815 26.9858809,95.0667209 24.3016003,95.0667209 L19.4412801,95.0667209 L19.4412801,119.572455 C19.4412801,122.256735 17.2652407,124.432775 14.5809601,124.432775 C11.8966795,124.432775 9.72064013,122.256735 9.72064013,119.572455 L9.72064013,95.0667209 L4.86032,95.0667209 C2.17603938,95.0667209 3.28729566e-16,92.8906815 0,90.2064009 L0,41.6032004 C-3.28729566e-16,38.9189198 2.17603938,36.7428804 4.86032,36.7428804 L9.72064013,36.7428804 L9.72064013,12.4412801 C9.72064013,9.75699951 11.8966795,7.58096013 14.5809601,7.58096013 C17.2652407,7.58096013 19.4412801,9.75699951 19.4412801,12.4412801 L19.4412801,36.7428804 L24.3016003,36.7428804 C26.9858809,36.7428804 29.1619203,38.9189198 29.1619203,41.6032004 Z']
    },
    {
        name: '雷达图',
        type: 'radar',
        keyDataAttrs: [{name:'雷达维度',type:'radar'},{name:'分类',type:'legend'},{name:'数据',type:'series', width:1}],
        paths: ['M79.573911,32.0195055 C79.7511468,32.0807796 79.9252225,32.1507623 80.0954971,32.2291958 L126.376239,53.5077701 C128.225278,54.3572545 129.374745,56.2354539 129.285085,58.2607506 L126.927916,110.733256 C126.832633,112.851954 125.405699,114.680595 123.367088,115.296516 L76.444394,129.315811 C75.5054275,129.595861 74.5046062,129.595861 73.5656397,129.315811 L32.4706683,117.053921 C30.8367372,116.566659 29.5635821,115.286115 29.0909814,113.654618 C28.6183807,112.023122 29.0111288,110.264354 30.133561,108.985836 L59.2822032,75.635091 C59.7218624,75.142978 60.0532763,74.5647698 60.2551619,73.937598 L73.2246026,35.1848307 C74.1062345,32.570839 76.9431645,31.1565423 79.573911,32.0195055 L79.573911,32.0195055 Z M139.476076,121.926724 L75.0000016,158.991984 L10.5239274,121.926724 L10.5239274,47.816176 L75.0000016,10.7509165 L139.476076,47.816176 L139.476076,121.926724 Z M146.998953,40.6068232 L77.5076273,0.665810956 C75.9548023,-0.221936985 74.0452007,-0.221936985 72.4923757,0.665810956 L3.00105014,40.6068232 C1.44784731,41.4971353 0.491395456,43.1462367 0.493424274,44.9304378 L0.493424274,124.812462 C0.493424274,126.599823 1.44632208,128.247389 3.00105014,129.136077 L72.4923757,169.077089 C74.047355,169.95676 75.9526481,169.95676 77.5076273,169.077089 L146.998953,129.136077 C148.550638,128.244253 149.506533,126.596111 149.506579,124.812462 L149.506579,44.9304378 C149.506533,43.1467894 148.550638,41.4986473 146.998953,40.6068232 L146.998953,40.6068232 Z']
    },
    {
        name: '热力图',
        type: 'heatmap',
        keyDataAttrs: [{name:'X轴',type:'xAxis'},{name:'Y轴',type:'yAxis'},{name:'数据',type:'series', width:1}],
        paths: ['M44.5999999,89.4742765 L5.40000005,89.4742765 C2.6938048,89.4742765 0.500000074,87.2916647 0.5,84.5992766 L0.5,55.3492765 C0.500000074,52.6568883 2.6938048,50.4742765 5.40000005,50.4742765 L44.5999999,50.4742765 C47.3061952,50.4742765 49.4999999,52.6568883 49.5,55.3492765 L49.5,84.5992766 C49.4999999,87.2916647 47.3061952,89.4742765 44.5999999,89.4742765','M102.6,89.4742765 L63.3999999,89.4742765 C60.6938047,89.4742765 58.5000001,87.2916647 58.5,84.5992766 L58.5,55.3492765 C58.5000001,52.6568884 60.6938047,50.4742766 63.3999999,50.4742765 L102.6,50.4742765 C105.306195,50.4742765 107.5,52.6568883 107.5,55.3492765 L107.5,84.5992766 C107.5,87.2916647 105.306195,89.4742765 102.6,89.4742765','M160.6,89.4742765 L121.4,89.4742765 C118.693805,89.4742765 116.5,87.2916647 116.5,84.5992766 L116.5,55.3492765 C116.5,52.6568884 118.693805,50.4742766 121.4,50.4742765 L160.6,50.4742765 C163.306195,50.4742766 165.5,52.6568884 165.5,55.3492765 L165.5,84.5992766 C165.5,87.2916647 163.306195,89.4742765 160.6,89.4742765','M44.5999999,137.474277 L5.40000005,137.474277 C2.69380475,137.474277 0.5,135.291665 0.5,132.599276 L0.5,103.349277 C0.5,100.656888 2.69380475,98.4742765 5.40000005,98.4742765 L44.5999999,98.4742765 C45.8995608,98.4742765 47.1458949,98.9878911 48.0648232,99.902131 C48.9837515,100.816371 49.5,102.056346 49.5,103.349277 L49.5,132.599276 C49.5,135.291665 47.3061953,137.474277 44.5999999,137.474277','M102.6,137.474277 L63.3999999,137.474277 C60.6938047,137.474276 58.5,135.291665 58.5,132.599276 L58.5,103.349277 C58.5,100.656888 60.6938047,98.4742766 63.3999999,98.4742765 L102.6,98.4742765 C105.306195,98.4742765 107.5,100.656888 107.5,103.349277 L107.5,132.599276 C107.5,135.291665 105.306195,137.474277 102.6,137.474277','M160.6,137.474277 L121.4,137.474277 C118.693805,137.474276 116.5,135.291665 116.5,132.599276 L116.5,103.349277 C116.5,100.656888 118.693805,98.4742766 121.4,98.4742765 L160.6,98.4742765 C163.306195,98.4742766 165.5,100.656888 165.5,103.349277 L165.5,132.599276 C165.5,135.291665 163.306195,137.474276 160.6,137.474277','M5.40000005,0.474276527 L44.5999999,0.474276527 C47.3061953,0.474276527 49.5,2.65688839 49.5,5.34927658 L49.5,34.5992765 C49.5,37.2916647 47.3061953,39.4742765 44.5999999,39.4742765 L5.40000005,39.4742765 C2.69380475,39.4742765 0.5,37.2916647 0.5,34.5992765 L0.5,5.34927658 C0.5,2.65688839 2.69380475,0.474276527 5.40000005,0.474276527','M102.6,39.4742765 L63.3999999,39.4742765 C60.6938047,39.4742765 58.5,37.2916646 58.5,34.5992765 L58.5,5.34927658 C58.5,2.65688845 60.6938047,0.474276601 63.3999999,0.474276527 L102.6,0.474276527 C105.306195,0.474276527 107.5,2.65688839 107.5,5.34927658 L107.5,34.5992765 C107.5,37.2916647 105.306195,39.4742765 102.6,39.4742765','M160.6,39.4742765 L121.4,39.4742765 C118.693805,39.4742765 116.5,37.2916646 116.5,34.5992765 L116.5,5.34927658 C116.5,2.65688845 118.693805,0.474276601 121.4,0.474276527 L160.6,0.474276527 C163.306195,0.474276601 165.5,2.65688845 165.5,5.34927658 L165.5,34.5992765 C165.5,37.2916646 163.306195,39.4742765 160.6,39.4742765']
    },
    /*{
        name: '关系图',
        type: 'graph',
        keyDataAttrs: [{name:'X轴',type:'xAxis'},{name:'Y轴',type:'yAxis'},{name:'数据',type:'series', width:1}],
        paths: ['M171,100 C171,106.075 166.075,111 160,111 C154.016,111 149.158,106.219 149.014,100.27 L114.105,83.503 C111.564,86.693 108.179,89.18 104.282,90.616 L108.698,124.651 C112.951,126.172 116,130.225 116,135 C116,141.075 111.075,146 105,146 C98.925,146 94,141.075 94,135 C94,131.233 95.896,127.912 98.781,125.93 L94.364,91.896 C82.94,90.82 74,81.206 74,69.5 C74,69.479 74.001,69.46 74.001,69.439 L53.719,64.759 C50.642,70.269 44.76,74 38,74 C36.07,74 34.215,73.689 32.472,73.127 L20.624,90.679 C21.499,92.256 22,94.068 22,96 C22,102.075 17.075,107 11,107 C4.925,107 0,102.075 0,96 C0,89.925 4.925,85 11,85 C11.452,85 11.895,85.035 12.332,85.089 L24.184,67.531 C21.574,64.407 20,60.389 20,56 C20,48.496 24.594,42.07 31.121,39.368 L29.111,21.279 C24.958,19.707 22,15.704 22,11 C22,4.925 26.925,0 33,0 C39.075,0 44,4.925 44,11 C44,14.838 42.031,18.214 39.051,20.182 L41.061,38.279 C49.223,39.681 55.49,46.564 55.95,55.011 L76.245,59.694 C79.889,52.181 87.589,47 96.5,47 C100.902,47 105.006,48.269 108.475,50.455 L131.538,27.391 C131.192,26.322 131,25.184 131,24 C131,17.925 135.925,13 142,13 C148.075,13 153,17.925 153,24 C153,30.075 148.075,35 142,35 C140.816,35 139.678,34.808 138.609,34.461 L115.546,57.525 C117.73,60.994 119,65.098 119,69.5 C119,71.216 118.802,72.884 118.438,74.49 L153.345,91.257 C155.193,89.847 157.495,89 160,89 C166.075,89 171,93.925 171,100']
    },
    {
        name: '路径图',
        type: 'lines',
        keyDataAttrs: [{name:'X轴',type:'xAxis'},{name:'Y轴',type:'yAxis'},{name:'数据',type:'series', width:1}],
        paths: ['M118.024118,118.8 C120.522388,118.8 122.566427,120.8475 122.566427,123.35 C122.566427,125.8525 120.522388,127.9 118.024118,127.9 C115.517543,127.894993 113.486807,125.860819 113.481809,123.35 C113.481809,120.8475 115.525848,118.8 118.024118,118.8 Z M142.434485,46 L64.9673569,46 C62.4587091,46 60.4250482,48.0371044 60.4250482,50.55 C60.4250482,53.0628956 62.4587091,55.1 64.9673569,55.1 L142.034762,55.0909 C149.519307,56.1087027 154.7743,62.9956941 153.790257,70.4972 C152.963556,76.6852 148.066948,81.5264 142.434485,82.2544 L20.6642736,82.2726 C14.6272756,82.8301001 9.06337697,85.7822231 5.21133949,90.4717 C0.0521031846,96.7506172 -1.39663076,105.298969 1.40493936,112.931618 C4.20650947,120.564268 10.8382585,126.136619 18.8291809,127.5724 L105.232977,127.8909 C107.143393,133.33221 112.265894,136.980155 118.024118,137 C125.537097,137 131.651044,130.8757 131.651044,123.35 C131.651044,115.8243 125.537097,109.7 118.024118,109.7 C112.110032,109.7 107.113492,113.5129 105.232977,118.8 L21.8634431,118.8728 L20.4099043,118.618 C14.3307333,117.521702 9.72082575,112.509885 9.12680957,106.3512 C8.77445503,102.712306 9.88625731,99.0830035 12.2155795,96.2684 C14.5594108,93.4383 17.8571269,91.6911 21.0912506,91.3544 L142.988646,91.3271 C153.280464,90.058092 161.421536,81.9929721 162.802197,71.6984 C164.446513,59.2314 155.679857,47.729 142.434485,46 Z','M31.9999101,18.2496933 C36.9704563,18.2496933 40.9998801,22.3350263 40.9998801,27.37454 C40.9998801,32.4140537 36.9704563,36.4993867 31.9999101,36.4993867 C27.0293639,36.4993867 22.9999401,32.4140537 22.9999401,27.37454 C22.9999401,22.3350263 27.0293639,18.2496933 31.9999101,18.2496933 Z M16.8799604,56.2181803 C19.4539519,59.9411177 22.1809428,63.6184309 24.9259336,66.9946242 C25.9069304,68.2173537 26.7979274,69.3032104 27.5539249,70.2156951 C28.1029231,70.7814356 28.435922,71.1920537 28.5979214,71.3654258 C30.4699152,73.555389 33.5839048,73.555389 35.4378986,71.3015518 C35.617898,71.1373046 35.932897,70.7266865 36.4188954,70.1518212 C37.2648925,69.2393365 38.1288897,68.1443549 39.0558866,66.9307503 C41.8008774,63.554557 44.5368683,59.8863687 47.1108598,56.1451815 C49.6758512,52.4131192 51.9258437,48.7449309 53.7348377,45.2957388 C57.1368264,38.917471 58.9998202,33.4243133 58.9998202,28.8710148 C59.05382,12.911658 46.9398603,0 31.9909101,0 C17.0509599,0 5,12.911658 5,28.8710148 C5,33.4243133 6.8629938,38.9722201 10.2559825,45.3596128 C12.1279763,48.8088048 14.3689688,52.4678683 16.8799604,56.2181803 Z']
    },
    {
        name: '树形图',
        type: 'tree',
        keyDataAttrs: [{name:'X轴',type:'xAxis'},{name:'Y轴',type:'yAxis'},{name:'数据',type:'series', width:1}],
        paths: ['M174,127.4965 C173.994333,137.928125 165.534,146.380667 155.101667,146.375708 C144.670042,146.370042 136.216792,137.909 136.222458,127.476667 C136.222458,127.470292 136.222458,127.464625 136.222458,127.45825 C136.226708,118.755667 142.117917,111.435042 150.127042,109.248417 L150.127042,80.2563333 L91.4625,80.2563333 L91.4625,109.120208 C99.7407917,111.126208 105.890542,118.582125 105.889125,127.476667 C105.889125,127.483042 105.889125,127.490125 105.889125,127.4965 C105.883458,137.928125 97.4224167,146.380667 86.9907917,146.375708 C76.5584583,146.370042 68.1059167,137.909 68.1115833,127.476667 C68.1122917,118.764875 74.0049167,111.440708 82.0182917,109.254083 L82.0182917,80.2563333 L23.3530417,80.2563333 L23.3530417,109.120917 C31.630625,111.127625 37.7789583,118.582833 37.77825,127.476667 C37.77825,127.478792 37.77825,127.480917 37.77825,127.483042 C37.776125,137.915375 29.3179167,146.37075 18.8862917,146.368625 C8.45395833,146.367208 -0.00141666667,137.909 0,127.476667 C0.00141666667,118.764167 5.89545833,111.439292 13.9088333,109.254083 L13.9088333,80.2563333 C13.9088333,75.0500833 18.1425417,70.812125 23.3530417,70.812125 L82.0182917,70.812125 L82.0182917,37.8179167 C74.0006667,35.6305833 68.106625,28.298625 68.1115833,19.58825 C68.1122917,9.15025 76.5705,0.694875 87.0028333,0.696291667 C97.4344583,0.697708333 105.890542,9.15591667 105.889125,19.58825 C105.889125,19.594625 105.889125,19.601 105.889125,19.607375 C105.884167,28.4976667 99.7358333,35.9465 91.4625,37.9517917 L91.4625,70.812125 L150.127042,70.812125 C155.337542,70.812125 159.57125,75.0500833 159.57125,80.2563333 L159.57125,109.113125 C167.853083,111.119125 174.00425,118.579292 174,127.476667 C174,127.483042 174,127.490125 174,127.4965 Z']
    },
    {
        name: '旭日图',
        type: 'sunburst',
        keyDataAttrs: [{name:'X轴',type:'xAxis'},{name:'Y轴',type:'yAxis'},{name:'数据',type:'series', width:1}],
        paths: ['M119.233629,98.0044542 L110.718691,87.8510962 C116.698944,81.6134116 119.776927,73.1460671 119.198407,64.5239101 L132.221771,61.7588152 C132.389075,63.3350954 132.468325,64.9289877 132.468325,66.5404921 C132.486789,78.3855449 127.713147,89.7343283 119.233629,98.0044542 L119.233629,98.0044542 Z M63.3185781,30.3829135 C74.7931609,22.4121709 89.3686325,20.3583561 102.598787,24.8479794 C115.828942,29.3376027 126.144611,39.8381699 130.399028,53.1465133 L117.38447,55.9116082 C114.163414,47.1482037 107.14477,40.3166874 98.2978797,37.3338959 C89.4509892,34.3511045 79.7290566,35.5384174 71.8599325,40.5626897 L63.3185781,30.3829135 L63.3185781,30.3829135 Z M57.4981296,97.8547516 L67.6773104,89.3128978 C73.1543438,94.3147256 80.4453141,97.3616135 88.4495316,97.3616137 C93.8284022,97.3676833 99.1146581,95.9617789 103.779942,93.2844195 L112.365323,103.508226 C105.247152,108.132446 96.9377023,110.586264 88.4495316,110.570666 C76.8578752,110.587037 65.7306915,106.015602 57.4981296,97.8547516 L57.4981296,97.8547516 Z M51.880208,91.0741048 C47.0035405,83.8229191 44.4062144,75.2791485 44.4219328,66.5404921 C44.4219328,54.7315996 49.0624416,44.0146553 56.6263832,36.1068361 L65.194154,46.3130303 C60.3076881,51.9176198 57.6201378,59.1046557 57.6302124,66.5404921 C57.6302124,72.3876992 59.2592335,77.8562467 62.0858054,82.5146391 L51.880208,91.0741048 L51.880208,91.0741048 Z M40.5915316,112.05008 L50.7354904,103.534644 C60.658337,113.682313 74.2570793,119.394563 88.4495316,119.3767 C99.4212094,119.3767 109.618001,116.030407 118.062495,110.306485 L126.586238,120.468649 C115.441046,128.370969 102.111678,132.606084 88.4495316,132.585752 C70.3533434,132.605952 53.0459635,125.179425 40.5915316,112.05008 L40.5915316,112.05008 Z M0.5,62.1374747 C1.61718015,39.3368661 11.5653323,17.8665508 28.2373873,2.27405071 L36.7435195,12.4097967 C23.0478109,25.4703646 14.8209875,43.2437342 13.7258907,62.1374747 L0.5,62.1374747 Z M0.5,70.9435094 L13.7258907,70.9435094 C14.5580363,85.4042184 19.5854188,99.3091254 28.1933598,110.958131 L18.0582064,119.464761 C7.47186066,105.41813 1.35420505,88.5122858 0.5,70.9435094 L0.5,70.9435094 Z M22.5490216,62.1374747 C23.6176776,45.8469855 30.7006658,30.5346593 42.4230797,19.1728312 L50.9380174,29.3261893 C42.1806215,38.1364527 36.8165849,49.7571326 35.7925234,62.1374747 L22.5490216,62.1374747 L22.5490216,62.1374747 Z M22.5490216,70.9435094 L35.7925234,70.9435094 C36.5571371,80.2216635 39.7688087,89.1310032 45.0999577,96.7628032 L34.9559989,105.278239 C27.6604148,95.2350677 23.3587128,83.3306553 22.5490216,70.9435094 Z M160.064824,88.3354281 C162.208458,81.2692542 163.294499,73.9246962 163.287644,66.5404921 C163.287927,62.7888436 163.014228,59.0421838 162.468731,55.3304099 L175.40404,52.5829271 C177.539642,66.0542744 176.545357,79.8364744 172.498218,92.86173 L160.064824,88.3354281 Z M157.009309,96.5954887 L169.451508,101.130596 C164.453629,112.804913 156.992417,123.261434 147.578597,131.784403 L139.08127,121.666269 C146.731696,114.64362 152.837128,106.105711 157.009309,96.5954887 L157.009309,96.5954887 Z M139.336631,80.7886563 C141.234304,74.0037443 141.75525,66.9076826 140.868791,59.9183539 L153.830516,57.1620651 C155.148964,66.5886629 154.449628,76.187832 151.77883,85.3237642 L139.336631,80.7886563 Z M136.263504,89.0487169 L148.714509,93.5750188 C145.090489,101.633987 139.889912,108.885878 133.419321,114.903235 L124.913189,104.776295 C129.629614,100.274719 133.477161,94.943361 136.263504,89.0487169 L136.263504,89.0487169 Z M84.1260215,141.268503 L84.1260215,154.495167 C61.049526,153.386197 39.3397973,143.226766 23.7025446,126.218989 L33.8288923,117.71236 C46.9380429,131.745712 64.9535467,140.183088 84.1260215,141.268503 L84.1260215,141.268503 Z M104.94227,2.57345585 C127.679056,8.4576749 145.591926,25.956003 152.007773,48.5497632 L139.046048,51.306052 C133.813709,33.9700374 120.081572,20.5128194 102.64403,15.6328053 L104.933465,2.64390412 L104.94227,2.57345585 L104.94227,2.57345585 Z M96.2952498,0.953145396 L96.2600278,1.12046007 L93.9970091,13.9948829 C81.1068487,12.6102912 68.1594065,16.034712 57.6390179,23.6110727 L49.1240802,13.4665207 C60.4957814,5.02434618 74.2870046,0.475385223 88.4495316,0.495231624 C91.0999932,0.495231624 93.7152325,0.644934198 96.2952498,0.953145396 L96.2952498,0.953145396 Z','M88.5,75.295167 C93.3601058,75.295167 97.3,71.3552728 97.3,66.495167 C97.3,61.6350611 93.3601058,57.6951669 88.5,57.6951669 C83.6398942,57.6951669 79.7,61.6350611 79.7,66.495167 C79.7,71.3552728 83.6398942,75.295167 88.5,75.295167 Z M88.5,88.495167 C76.3497355,88.495167 66.5,78.6454315 66.5,66.495167 C66.5,54.3449025 76.3497355,44.495167 88.5,44.495167 C100.650264,44.495167 110.5,54.3449025 110.5,66.495167 C110.5,78.6454315 100.650264,88.495167 88.5,88.495167 Z']
    },*/
    {
        name: '仪表盘',
        type: 'gauge',
        keyDataAttrs: [{name:'数据',type:'series', width:1}],
        paths: ['M143.698895,82.9089367 C142.437361,67.6655629 135.026556,53.5920156 123.171797,43.9267897 L135.7146,25.9137777 C153.409366,39.7333892 164.305583,60.4959162 165.625777,82.9089367 L143.698895,82.9089367 L143.698895,82.9089367 Z M115.601912,38.6960173 C112.342946,36.8541942 108.909619,35.339582 105.352176,34.174346 L107.654453,22.8655635 C107.981818,21.2534161 107.424245,19.5890834 106.191766,18.4995036 C104.959288,17.4099237 103.239147,17.0606298 101.679304,17.5831975 C100.119461,18.1057651 98.9568933,19.4208039 98.6295287,21.0329513 L96.4193432,31.8996968 C93.4676767,31.3974455 90.4804994,31.132603 87.4865101,31.1077136 C77.1817177,31.1042083 67.0761605,33.9468942 58.284434,39.3222365 L45.5021944,21.4934066 C70.6181595,5.43266895 102.701308,5.12868193 128.117087,20.7106326 L115.601912,38.6960173 L115.601912,38.6960173 Z M31.2649159,82.9089367 L9.34724295,82.9089367 C10.6455448,61.0475255 21.0636813,40.7358347 38.0612364,26.9267794 L50.843476,44.7464001 C39.5322634,54.3814577 32.493597,68.101229 31.2649159,82.9089367 L31.2649159,82.9089367 Z M87.4865101,0.0269797767 C39.2492112,0.0269797767 0,39.276191 0,87.5134899 C5.46353103e-08,90.0565144 2.06152873,92.118043 4.60455321,92.118043 L35.685287,92.118043 C38.2283115,92.118043 40.2898401,90.0565144 40.2898401,87.5134899 C40.315223,61.4580101 61.4310302,40.3422029 87.4865101,40.31682 C89.908505,40.31682 92.2384089,40.6759751 94.5683128,41.0351302 L86.9247546,78.6451205 C82.0531374,79.4923583 78.2774037,83.5443651 78.2774037,88.6646282 C78.2774037,94.3864333 82.9158433,99.0248728 88.6376484,99.0248728 C94.3594535,99.0248728 98.997893,94.3864333 98.997893,88.6646282 C98.997893,85.7361324 97.7546637,83.102328 95.793124,81.2236703 L103.501146,43.3005704 C121.633876,49.8942906 134.68318,67.1337376 134.68318,87.5134899 C134.68318,90.0565144 136.744709,92.118043 139.287733,92.118043 L170.368467,92.118043 C172.911491,92.118043 174.97302,90.0565144 174.97302,87.5134899 C174.97302,39.276191 135.723809,0.0269798756 87.4865101,0.0269797767 L87.4865101,0.0269797767 Z']
    }
];

/**
 * 返回图表组件列表
 * @returns 图表组件列表
 */
export function getChartComponents() {
    return chartComponents;
}