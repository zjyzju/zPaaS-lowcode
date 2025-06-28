import { createApp } from 'vue'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import { Search } from '@element-plus/icons-vue'

import App from '../components/businessSystemGrant.vue'
const app = createApp(App)
app.use(ElementPlus)
app.component('Search', Search)
app.mount('#app')


