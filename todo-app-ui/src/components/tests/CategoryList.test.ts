import {describe, expect, it, vi} from 'vitest'
import CategoryList from '../CategoryList.vue'
import {vuetifyStubs} from '../../test/vuetify-stubs'
import {flushPromises, mount} from "@vue/test-utils";

vi.mock('../../services/category-service.ts', () => ({
    getCategories: vi.fn(),
    addCategory: vi.fn(),
    updateCategory: vi.fn(),
    deleteCategory: vi.fn(),
}))

describe('CategoryList', () => {
    it('creates a new category from the form', async () => {
        const service = await import('../../services/category-service.ts')
        const getCategories = service.getCategories as ReturnType<typeof vi.fn>
        const addCategory = service.addCategory as ReturnType<typeof vi.fn>

        getCategories.mockResolvedValue([])

        const wrapper = mount(CategoryList, {
            global: {stubs: vuetifyStubs, renderStubDefaultSlot: true},
        })

        await flushPromises()

        const vm = wrapper.vm as any
        vm.categoryForm.name = ' Home '
        vm.categoryForm.colorHex = ' #123456 '

        await vm.saveCategory()

        expect(addCategory).toHaveBeenCalledWith({
            name: 'Home',
            colorHex: '#123456',
        })
    })

    it('updates an existing category when editing', async () => {
        const service = await import('../../services/category-service.ts')
        const getCategories = service.getCategories as ReturnType<typeof vi.fn>
        const updateCategory = service.updateCategory as ReturnType<typeof vi.fn>

        getCategories.mockResolvedValue([])

        const wrapper = mount(CategoryList, {
            global: {stubs: vuetifyStubs, renderStubDefaultSlot: true},
        })

        await flushPromises()

        const vm = wrapper.vm as any
        vm.isEditing = true
        vm.editingId = 5
        vm.categoryForm.name = 'Work'
        vm.categoryForm.colorHex = '#000000'

        await vm.saveCategory()

        expect(updateCategory).toHaveBeenCalledWith({
            id: 5,
            name: 'Work',
            colorHex: '#000000',
        })
    })

    it('deletes a category after confirmation', async () => {
        const service = await import('../../services/category-service.ts')
        const getCategories = service.getCategories as ReturnType<typeof vi.fn>
        const deleteCategory = service.deleteCategory as ReturnType<typeof vi.fn>

        getCategories.mockResolvedValue([])
        vi.spyOn(window, 'confirm').mockReturnValueOnce(true)

        const wrapper = mount(CategoryList, {
            global: {stubs: vuetifyStubs, renderStubDefaultSlot: true},
        })

        await flushPromises()

        const vm = wrapper.vm as any
        await vm.removeCategory({id: 9, name: 'Old', colorHex: '#ffffff'})

        expect(deleteCategory).toHaveBeenCalledWith(9)
    })
})
