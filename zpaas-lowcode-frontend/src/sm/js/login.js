import { createApp } from 'vue'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import '../css/index.css'
import '../css/iconfont.css'
import { Menu as IconMenu, Message, Setting, Grid, Lock, User } from '@element-plus/icons-vue'


import App from '../components/login.vue'

const app = createApp(App)
app.use(ElementPlus)
app.component('icon-menu', IconMenu)
app.component('message', Message)
app.component('setting', Setting)
app.component('grid', Grid)
app.component('lock', Lock)
app.component('user', User)
app.mount('#app')


