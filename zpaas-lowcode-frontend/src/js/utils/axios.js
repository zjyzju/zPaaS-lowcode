import axios from "axios";
import { ElMessage } from 'element-plus'
//import JSONbig from 'json-bigint'

const axiosClient = axios.create({
    withCredentials: true, // 跨域请求时是否需要使用凭证
    timeout: 30000, // 请求超时时间
    // transformResponse: [function (data) {//处理Long类型精度丢失问题
    //     try {
    //         console.log(data);
    //         var result = JSONbig.parse(data)
    //         console.log(result);
    //         return result;
    //     }catch(e){
    //         return {
    //             data
    //         }
    //     }
    // }]
});

// 请求拦截器配置
axiosClient.interceptors.request.use(config => {
        //config.headers.Authorization = window.sessionStorage.getItem('token')
        //console.log(config);
        return config;
    }, error => {
        console.log(error);
        return Promise.error(error);
    }
)

// 响应拦截器配置
axiosClient.interceptors.response.use(response => {
    if ("NOT_LOGON" == response.data) {
        top.window.location = "/login.html";
    } else if(response.data != null && response.data.status !=  null && response.data.status != "200" && response.data.message != null) {
        ElMessage.error(`错误编码：` + response.data.status + ` 错误信息：` + response.data.message);
        return response;
    } else {
        return response
    }
}, error => {
    /* switch (error.response.status) {
         case 401:
             console.log("无权访问")
             router.push({ path: '/login' })
             break
         case 403:
             console.log("token过期啦")
             router.push({ path: '/login' })
             break
         case 404:
             console.log("404啦")
             router.push({ path: '/404' })
             break
         default:
             return Promise.reject(error)
     }*/
    return Promise.reject(error)
});

export default axiosClient;