INSERT INTO zpaas_lowcode_revise.pub_dict (id,dict_code,dict_name,parent_dict_code,parent_dict_value,dict_value,dict_value_label,dict_value2,dict_value3,display_order,status,create_time,update_time) VALUES
	 ('90001','AI_FLOW_COMPOSER_TIPS','AI流程编排提示词配置','','','NODE_COMPOSER_TPL','节点编排提示词模板','','#以下是低代码平台业务逻辑编排的流程节点类型说明，包括nodeTypeId、name、description信息：
${Flow_Node_List}
请使用以上的节点来进行用户业务逻辑编排的需求，输出编排好的流程节点的列表。

#示例1:用户的编排需求“请请使用前端提供的44类流程节点，帮我编排根据输入条件查询用户表数据的业务流程，输入的查询条件为用户对象queryUser，支持用户名userName和联系电话contactPhone的模糊查询，输出满足条件的用户列表”，输入参数及结构信息为：
用户对象public class User {
	private String id;
	private String userName;
	private String contactPhone;
	private String name;
	private String birthday;
}

输出的编排结果示例如下：
######
[
	{
		"nodeTypeId": 4,
		"flowDesc": "校验查询条件用户名和联系电话是否都为空，如果都为空则中断业务流"
	},
	{
		"nodeTypeId": 5,
		"flowDesc": "创建用户PO对象，并将不这空的用户名和联系电话的设置到用户PO对象对应的属性中"
		}
	},
	{
		"nodeTypeId": 1,
		"flowDesc": "以用户PO对象作为输入，通过数据库存取节点，使用ORM的方法从数据库中查询满足条件的用户列表并返回"
	}
]
######

#示例2:用户编排需求为"请使用前面提供的44类流程节点，帮我编排用户信息创建的业务流程，输入的条件为用户对象user，其中userName、contactPhone和name不能为空;如果id为空则新增用户；如果id不为空，则更新用户数据，其中空的属性不更新到数据库中；最终返回新增或更新的记录数；"，输入参数及结构信息为：
用户对象public class User {
	private String id;
	private String userName;
	private String contactPhone;
	private String name;
	private String birthday;
}
输出的编排结果示例如下：
######
[
	{
		"nodeTypeId": 4,
		"flowDesc": "校验查询条件用户名、联系电话和姓名是否为空，如果为空则中断业务流"
		}
	},
	{
		"nodeTypeId": 5,
		"flowDesc": "创建用户PO对象，并将user的所有属性值都设置到用户PO对象对应的属性中"
		}
	},
	{
		"nodeTypeId": 6,
		"flowDesc": "如果userPO的id字段为空，则新增",
		"subBusinessFlow":[
			{
				"nodeTypeId": 1,
				"flowDesc": "以用户PO对象作为输入，通过数据库存取节点，使用ORM的方法往数据库中新增一条用户记录并返回"
			}
		]
	},
	{
		"nodeTypeId": 6,
		"flowDesc": "如果userPO的id字段不为空，则更新",
		"subBusinessFlow":[
			{
				"nodeTypeId": 1,
				"flowDesc": "以用户PO对象作为输入，通过数据库存取节点，使用ORM的方法更新用户信息并返回"
			}
		]
	}
]
######

#示例3:用户编排需求为"请使用前面提供的44类流程节点，帮我编排根据用户id（userId）删除用户信息的业务流程，输入的条件为用户标识String id，id字段不能为空；完成删除操作后，如果删除的记录数为1，则返回成功；如果删除的记录数不为1，则抛出异常提示删除失败"，输入参数及结构信息为：
用户对象public class User {
	private String id;
	private String userName;
	private String contactPhone;
	private String name;
	private String birthday;
}
输出的编排结果示例如下：
######
[
	{
		"nodeTypeId": 4,
		"flowDesc": "校验用户id字段是否为空，如果为空则中断业务流"
	},
	{
		"nodeTypeId": 6,
		"flowDesc": "校验用户id字段是否为空，如果为空则中断业务流",
		"subBusinessFlow":[
			{
				"nodeTypeId": 14,
				"flowDesc": "抛出异常"
			}
		]
	},
	{
		"nodeTypeId": 5,
		"flowDesc": "创建用户PO对象，并将用户id设置到用户PO对象对应的id属性中"
		}
	},
	{
		"nodeTypeId": 1,
		"flowDesc": "以用户PO对象为条件，通过数据库存取节点，使用ORM的方法从数据库中删除满足条件用户记录"
	},
	{
		"nodeTypeId": 6,
		"flowDesc": "如果records等于1，则返回成功",
		"subBusinessFlow":[
			{
				"nodeTypeId": 10,
				"flowDesc": "返回true"
			}
		]
	},
	{
		"nodeTypeId": 6,
		"flowDesc": "如果records不等于1，则抛出异常提示删除用户失败",
		"subBusinessFlow":[
			{
				"nodeTypeId": 14,
				"flowDesc": "抛出异常"
			}
		]
	}
]
######


下面正式开始进行用户需求的业务逻辑编排，用户编排需求为"${Business_Requirement}"，输入参数及结构信息为：
${Param_Info}
请输出编排好的流程节点的列表。请不要输出思考或推理的内容。',1,'1',NULL,NULL),
	 ('90002','AI_FLOW_COMPOSER_TIPS','AI流程编排提示词配置','','','CFG_COMPOSER_TPL','节点配置信息编排提示词模板','','#以下是用到的低代码平台业务逻辑编排的流程节点类型说明，包括nodeTypeId、name、description和cfgRequirement信息，cfgRequirement是JSON格式的文本：
"nodeTypeId","name","description","cfgRequirement"
${Node_Cfg_List}

:"源文本的获取属性，参见业务逻辑流程节点中数据获取方式说明","signTextSource":"签名文本的获取来源，可选值包括：I、P、D、N、F、","signTextKey":"签名文本的获取关键字，参见业务逻辑流程节点中数据获取方式说明","signTextAttr":"签名文本的获取属性，参见业务逻辑流程节点中数据获取方式说明"}}

#业务逻辑流程节点中数据获取方式说明：可以从多个数据来源获取数据
数据来源可选值包括：I（从输入参数中获取）；P（从业务流上下文过程数据中获取）；D（从业务流上下文中的领域对象中获取）；F（固定值）；N（从预处理产生的参数中获取）；U（从上传的Multipart文件对象中获取）；O（从业务流的属主领域对象中获取）；
获取数据可以通过一个三元组、四元组、六元组来获取，三元组为数据获取来源、数据获取关键字、数据获取属性，四元组为数据获取来源、数据获取关键字、数据获取属性、数据是否列表，六元组相比三元组、四元组，其数据获取属性通过另一个三元组的方式来动态获取，为数据获取来源、数据获取关键字、数据获取属性来源、数据获取属性关键字、数据获取属性属性、数据是否列表；当数据获取来源为I、P、D时，数据获取关键字指定要获取数据在对应来源中的Key，如果还要获取的数据对应下级属性，则可以通过数据获取属性来指定对应的属性；当数据获取来源为N、O时，数据获取关键字无效，如果数据获取属性也为空则表示直接获取对应来源的根对象，如果不为空，则表示获取对应属性的值；当数据获取来源为U时，数据获取关键字对应具体的固定值，指定要获取数据在对应来源中的Key，数据获取属性无效；当数据获取来源为F时，数据获取关键字对应具体的固定值，支持null：空值、now()：当前时间、UUID()：取UUID值、snowFlake()：取雪花id值、""：空字符串、其他值表示具体的字符串；
四元组比三元组多是否列表属性，该属性指定获取数据的类型是否是列表，取值可选值包括：true、false
数据获取来源：I、数据获取关键字：userVO、数据获取属性：name，表示获取输入参数userVO的name属性值
数据获取来源：P、数据获取关键字：users、数据获取属性：空、是否列表：true，表示获取过程数据中获取users对象，该对象是列表
数据获取来源：F、数据获取关键字：now()、数据获取属性：空，表示获取当前系统时间
数据获取来源：P、数据获取关键字：users、数据获取属性来源：P、数据获取属性关键字：dynamicVo、数据获取属性属性：dynamicAttr、是否列表：true，表示获取过程数据中获取users对象中通过过程数据中dynamicVo的dynamicAttr属性值指定的属性值，获取的属性值是列表

#对象类型说明：J（JDK原生对象）、D（领域对象）、V（值传递对象）

#节点类型的cfgRequirement字段配置说明：
{"节点预处理参数":"参见节点预处理参数配置说明","节点配置参数":"参见各节点类型相应的说明","节点后处理参数":"参见节点后处理参数配置说明"}

#节点预处理参数配置说明：用于节点要使用数据的预处理
{"toObjectType":"目标参数存放的容器类型，可选值包括：JSON（目前只支持JSON）","paramRules":[{"isListType":"源对象是否是列表类型，true、false，默认为false","fromObjectType":"源对象的获取类型，参见业务逻辑流程节点中数据获取方式说明，可选值包括：I、P、D、F","fromObjectKey":"参见业务逻辑流程节点中数据获取方式说明","attrMappings":[{"fromAttrPath":"源对象属性对应的json path，如果是“$”，则表示源对象本身","toObjectAttr":"目标参数的属性名，也需要支持json path格式"}]}]}

#节点后处理参数配置说明：用于节点输出数据的配置
{"asInProcessData":"节点的执行结果是否保存到过程数据中，可选值包括：true、false","inProcessDataType":"输出到过程数据对象的类型，可选值包括：J（JDK原生对象）、D（领域对象）、V（值传递对象）","inProcessDataClass":"输出到过程数据对象的实现类，当属性类型为JDK原生对象（J）时，对应的JDK原生对象类型，完整的类名表示；为领域对象（DO）或值传递对象（VO）时有效，对应领域对象或值传递对象的主键，为空时，表示使用原结构输出","inProcessDataKey":"该字段配置存储到过程数据对象中使用的Key值","asDomainObjectValue":"节点的执行结果输出到领域对象中，只针对领域对象的方法调用时有效","domainObjectValueKey":"领域对象保存的Key值","asBusinessProcessResult":"节点的执行结果是否作为业务流的输出结果，可选值包括：true、false"}

请根据以上这些节点的配置信息说明补充完善业务流节点的详细配置信息。

#示例1:用户的编排需求“请请使用前端提供的44类流程节点，帮我编排根据输入条件查询用户表数据的业务流程，输入的查询条件为用户对象queryUser，支持用户名userName和联系电话contactPhone的模糊查询，输出满足条件的用户列表”，输入参数及结构信息为：
用户对象public class User {
	private String id;
	private String userName;
	private String contactPhone;
	private String name;
	private String birthday;
}
已经得到的节点列表信息如下：
[
	{
		"nodeTypeId": 4,
		"flowDesc": "校验查询条件用户名和联系电话是否都为空，如果都为空则中断业务流"
	},
	{
		"nodeTypeId": 5,
		"flowDesc": "创建用户PO对象，并将不这空的用户名和联系电话的设置到用户PO对象对应的属性中"
		}
	},
	{
		"nodeTypeId": 1,
		"flowDesc": "以用户PO对象作为输入，通过数据库存取节点，使用ORM的方法从数据库中查询满足条件的用户列表并返回"
	}
]

输出补充完善配置信息后的流程节点的列表示例如下：
######
[
	{
		"nodeTypeId": 4,
		"flowDesc": "校验查询条件用户名和联系电话是否都为空，如果都为空则中断业务流",
		"nodeCfg": {
			"节点预处理参数": {
			},
			"节点配置参数": {
				"targetObjectInstanceSource": "I",
				"targetObjectInstanceKey": "queryUser",
				"targetObjectInstanceAttr": "",
				"isListType": false,
				"targetObjectType": "D",
				"targetObjectClass": "User",
				"ruleGroupId": "[{''attr'':''userName'',''ruleType'':''可空校验'',''msg'':''用户名不能为空''},{''attr'':''contactPhone'',''ruleType'':''可空校验'',''msg'':''联系电话不能为空''}]",
				"interruptBusinessFlow": true
			},
			"节点后处理参数": {
			}
		}
	},
	{
		"nodeTypeId": 5,
		"flowDesc": "创建用户PO对象，并将不这空的用户名和联系电话的设置到用户PO对象对应的属性中",
		"nodeCfg": {
			"节点预处理参数": {
			},
			"节点配置参数": {
				"toObjectInstanceSource": "",
				"toObjectInstanceKey": "",
				"toObjectInstanceAttr": "",
				"isListType": "",
				"toObjectType": "D",
				"toObjectClass": "User",
				"paramRules": [
					{
						"fromObjectIsList": false,
						"fromObjectType": "I",
						"fromObjectKey": "queryUser",
						"attrMappings": [
							{
								"fromAttrPath": "userName",
								"toObjectAttr": "userName"
							},
							{
								"fromAttrPath": "contactPhone",
								"toObjectAttr": "contactPhone"
							}
						]
					}
				]
			},
			"节点后处理参数": {
				"asInProcessData": false,
				"inProcessDataType": "",
				"inProcessDataClass": "",
				"inProcessDataKey": "",
				"asDomainObjectValue": true,
				"domainObjectValueKey": "user",
				"asBusinessProcessResult": false
			}
		}
	},
	{
		"nodeTypeId": 1,
		"flowDesc": "通过数据库存取节点，使用ORM的方法从数据库中查询满足条件的用户列表并返回",
		"nodeCfg": {
			"节点预处理参数": {
			},
			"节点配置参数": {
				"isListType": false,
				"domainObjectSource": "D",
				"domainObjectKey": "user",
				"domainObjectId": "User",
				"ormId": "11",
				"repositoryOperation": "QC",
				"pageFlag": true,
				"queryOptions": "{''fuzzyQuery'':''userName,contactPhone''}"
			},
			"节点后处理参数": {
				"asInProcessData": false,
				"inProcessDataType": "",
				"inProcessDataClass": "",
				"inProcessDataKey": "",
				"asDomainObjectValue": false,
				"domainObjectValueKey": "",
				"asBusinessProcessResult": true
			}
		}
	}
]
######

下面正式开始进行用户需求的业务逻辑编排，用户编排需求为"#{Business_Requirement}"，输入参数及结构信息为：
${${Param_Info}}

已经得到的节点列表信息如下：
${Node_List}

请输出补充完善配置信息后的流程节点的列表。请不要输出思考或推理的内容。',2,'1',NULL,NULL);
