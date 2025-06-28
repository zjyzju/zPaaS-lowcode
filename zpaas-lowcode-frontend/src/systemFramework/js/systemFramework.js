
import { createApp } from 'vue'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import microApp from '@micro-zoe/micro-app'
import { Menu as IconMenu, Message, Location,Document,Setting, Grid, Histogram , Plus, Coin, MessageBox, Tools, Folder} from '@element-plus/icons-vue'
import App from '../components/systemFramework.vue'

microApp.start({
    plugins: {
      modules: {
        "systemFramework-app": [ // /basename/
          {
            loader(code) {
              if (process.env.NODE_ENV === "development") {
                // 这里 /basename/ 需要和子应用vite.config.js中base的配置保持一致
                code = code.replace(
                  /(from|import)(\s*['"])(\/child\/systemFramework-app\/)/g,
                  (all) => {
                    return all.replace(
                      "/child/systemFramework-app/",
                      "https://localhost:13043"
                    );
                  }
                );
              }
              return code;
            },
          },
        ],
      },
    },
    //'disable-memory-router': true,
    //'disable-patch-request': true
  }
 );
const app = createApp(App)
app.use(ElementPlus)
app.component('icon-menu', IconMenu)
app.component('message', Message)
app.component('setting', Setting)
app.component('grid', Grid)
app.component('plus', Plus)
app.component('histogram', Histogram)
app.component('location', Location)
app.component('document', Document)
app.component('coin', Coin)
app.component('MessageBox', MessageBox)
app.component('Tools', Tools)
app.component('Folder', Folder)
app.mount('#app')


