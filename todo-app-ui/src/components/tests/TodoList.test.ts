import {flushPromises, mount} from '@vue/test-utils'
import {describe, expect, it, vi} from 'vitest'
import TodoList from '../TodoList.vue'
import {PriorityEnum} from '../../domain/priority.enum'
import {vuetifyStubs} from '../../test/vuetify-stubs'

const mocks = vi.hoisted(() => ({
    getTodos: vi.fn().mockResolvedValue([]),
    addTodo: vi.fn(),
    getCategories: vi.fn().mockResolvedValue([]),
}))

vi.mock('../../services/todo-service.ts', () => ({
    getTodos: mocks.getTodos,
    addTodo: mocks.addTodo,
}))

vi.mock('../../services/category-service.ts', () => ({
    getCategories: mocks.getCategories,
}))

describe('TodoList', () => {
    it('loads todos and categories on mount', async () => {
        mocks.getTodos.mockResolvedValue([
            {id: 1, title: 'First', done: false, priority: PriorityEnum.LOW},
            {id: 2, title: 'Second', done: true, priority: PriorityEnum.HIGH},
        ])
        mocks.getCategories.mockResolvedValue([{id: 1, name: 'Work', colorHex: '#000000'}])

        const wrapper = mount(TodoList, {
            global: {stubs: vuetifyStubs, renderStubDefaultSlot: true},
        })

        await flushPromises()

        const vm = wrapper.vm as any
        expect(vm.todos).toHaveLength(2)
        expect(vm.filteredTodos).toHaveLength(2)
        expect(mocks.getTodos).toHaveBeenCalledTimes(1)
        expect(mocks.getCategories).toHaveBeenCalledTimes(1)
    })

    it('filters by high priority', () => {
        const wrapper = mount(TodoList, {
            global: {stubs: vuetifyStubs, renderStubDefaultSlot: true},
        })

        const vm = wrapper.vm as any
        vm.todos = [
            {id: 1, title: 'Low', done: false, priority: PriorityEnum.LOW},
            {id: 2, title: 'High', done: false, priority: PriorityEnum.HIGH},
        ]
        vm.filterSelection = 'high'
        vm.applyFilters()

        expect(vm.filteredTodos).toHaveLength(1)
        expect(vm.filteredTodos[0].title).toBe('High')
    })
})
