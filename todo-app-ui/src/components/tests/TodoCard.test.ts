import {mount} from '@vue/test-utils'
import {describe, expect, it, vi} from 'vitest'
import TodoCard from '../TodoCard.vue'
import {PriorityEnum} from '../../domain/priority.enum'
import {vuetifyStubs} from '../../test/vuetify-stubs'

vi.mock('../../services/todo-service.ts', () => ({
    updateTodo: vi.fn(),
}))

describe('TodoCard', () => {
    it('renders the title with done styling', () => {
        const todo = {
            id: 1,
            title: 'Finish tests',
            done: true,
            priority: PriorityEnum.LOW,
        }

        const wrapper = mount(TodoCard, {
            props: {todo, categories: []},
            global: {stubs: vuetifyStubs, renderStubDefaultSlot: true},
        })

        const titleNode = wrapper.findAll('span')
            .find((node) => node.text() === 'Finish tests')

        expect(titleNode).toBeTruthy()
        expect(titleNode?.classes()).toContain('done')
    })

    it('prepares the edit dialog form', () => {
        const todo = {
            id: 2,
            title: 'Review PR',
            description: 'Check updates',
            dueDate: '2025-02-10',
            done: false,
            priority: PriorityEnum.HIGH,
            category: {id: 7, name: 'Work', colorHex: '#000000'},
        }

        const wrapper = mount(TodoCard, {
            props: {todo, categories: [todo.category]},
            global: {stubs: vuetifyStubs, renderStubDefaultSlot: true},
        })

        const vm = wrapper.vm as any
        vm.openEditDialog()

        expect(vm.dialog).toBe(true)
        expect(vm.todoForm.title).toBe('Review PR')
        expect(vm.todoForm.priority).toBe(PriorityEnum.HIGH)
        expect(vm.todoForm.category).toEqual(todo.category)
    })

    it('updates the todo and emits an event', async () => {
        const todo = {
            id: 3,
            title: 'Ship release',
            done: false,
            priority: PriorityEnum.MEDIUM,
        }

        const wrapper = mount(TodoCard, {
            props: {todo, categories: []},
            global: {stubs: vuetifyStubs, renderStubDefaultSlot: true},
        })

        const service = await import('../../services/todo-service.ts')
        const updateTodo = service.updateTodo as ReturnType<typeof vi.fn>

        await (wrapper.vm as any).todoUpdate()

        expect(updateTodo).toHaveBeenCalledWith(todo)
        expect(wrapper.emitted('updatedTodo')).toHaveLength(1)
    })
})
