
import { createApp } from 'vue'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import { Menu as IconMenu, Message, Location,Document,Setting, Grid, Histogram , Plus, Coin, Search, Delete, Download, ZoomIn, Close, Check} from '@element-plus/icons-vue'


import App from '../components/moreDomainObjects/moreMgrFunc.vue'

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
app.component('search', Search)
app.component('Delete', Delete)
app.component('Download', Download)
app.component('ZoomIn', ZoomIn)
app.component('Close', Close)
app.component('Check', Check)
app.mount('#app')


