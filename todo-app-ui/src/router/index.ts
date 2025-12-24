import {createRouter, createWebHistory} from 'vue-router'
import TodoList from '../components/TodoList.vue';
import CategoryList from '../components/CategoryList.vue';

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [
        {
            path: '/',
            name: 'todolist',
            component: TodoList
        },
        {
            path: '/categories',
            name: 'category-list',
            component: CategoryList
        }
    ],
})

export default router
