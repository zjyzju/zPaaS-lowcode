import { createApp } from 'vue'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import { Menu as IconMenu, Message, Setting, Grid, Plus, Delete, EditPen, PieChart, View, Discount, More } from '@element-plus/icons-vue'


import App from '../components/businessSystem.vue'

const app = createApp(App)
app.use(ElementPlus)
app.component('icon-menu', IconMenu)
app.component('message', Message)
app.component('setting', Setting)
app.component('grid', Grid)
app.component('plus', Plus)
app.component('Delete', Delete)
app.component('EditPen', EditPen)
app.component('PieChart', PieChart)
app.component('View', View)
app.component('Discount', Discount)
app.component('More', More)
app.mount('#app')


