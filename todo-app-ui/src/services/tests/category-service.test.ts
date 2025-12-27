import axios from 'axios'
import {beforeEach, describe, expect, it, vi} from 'vitest'

vi.mock('axios', () => ({
    default: {
        get: vi.fn(),
        post: vi.fn(),
        put: vi.fn(),
        delete: vi.fn(),
    },
}))

type AxiosMock = {
    get: ReturnType<typeof vi.fn>
    post: ReturnType<typeof vi.fn>
    put: ReturnType<typeof vi.fn>
    delete: ReturnType<typeof vi.fn>
}

const mockedAxios = axios as unknown as AxiosMock

beforeEach(() => {
    vi.clearAllMocks()
    vi.unstubAllEnvs()
    vi.resetModules()
})

describe('category-service', () => {
    it('getCategories requests the categories endpoint', async () => {
        vi.stubEnv('VITE_API_BASE_URL', 'http://api.test')
        mockedAxios.get.mockResolvedValue({data: [{id: 1}]})

        const {getCategories} = await import('../category-service')
        const result = await getCategories()

        expect(mockedAxios.get).toHaveBeenCalledWith('http://api.test/categories')
        expect(result).toEqual([{id: 1}])
    })

    it('addCategory posts the payload', async () => {
        vi.stubEnv('VITE_API_BASE_URL', 'http://api.test')
        mockedAxios.post.mockResolvedValue({data: {id: 2}})

        const {addCategory} = await import('../category-service')
        const payload = {name: 'Home', colorHex: '#fff'}
        const result = await addCategory(payload)

        expect(mockedAxios.post).toHaveBeenCalledWith('http://api.test/categories', payload)
        expect(result).toEqual({id: 2})
    })

    it('updateCategory throws when id is missing', async () => {
        vi.stubEnv('VITE_API_BASE_URL', 'http://api.test')

        const {updateCategory} = await import('../category-service')
        await expect(updateCategory({name: 'Oops', colorHex: '#000'}))
            .rejects.toThrow('Category id is required for update')
    })

    it('deleteCategory hits the delete endpoint', async () => {
        vi.stubEnv('VITE_API_BASE_URL', 'http://api.test')

        const {deleteCategory} = await import('../category-service')
        await deleteCategory(4)

        expect(mockedAxios.delete).toHaveBeenCalledWith('http://api.test/categories/4')
    })
})
