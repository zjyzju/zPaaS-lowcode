
import { createApp } from 'vue'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import { Menu as IconMenu, Message, Location,Document,Setting, Grid, Histogram , Plus, Coin} from '@element-plus/icons-vue'


import App from '../components/tabbedComboFunc/tabbedComboFunc.vue'

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
app.mount('#app')


