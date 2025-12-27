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

describe('todo-service', () => {
    it('getTodos requests the todos endpoint', async () => {
        vi.stubEnv('VITE_API_BASE_URL', 'http://api.test')
        mockedAxios.get.mockResolvedValue({data: [{id: 1}]})

        const {getTodos} = await import('../todo-service')
        const result = await getTodos()

        expect(mockedAxios.get).toHaveBeenCalledWith('http://api.test/todos')
        expect(result).toEqual([{id: 1}])
    })

    it('addTodo posts the payload', async () => {
        vi.stubEnv('VITE_API_BASE_URL', 'http://api.test')
        mockedAxios.post.mockResolvedValue({data: {id: 2}})

        const {addTodo} = await import('../todo-service')
        const payload = {title: 'New todo'}
        const result = await addTodo(payload)

        expect(mockedAxios.post).toHaveBeenCalledWith('http://api.test/todos', payload)
        expect(result).toEqual({id: 2})
    })

    it('updateTodo targets the id endpoint', async () => {
        vi.stubEnv('VITE_API_BASE_URL', 'http://api.test')
        mockedAxios.put.mockResolvedValue({data: {id: 3}})

        const {updateTodo} = await import('../todo-service')
        const payload = {id: 3, title: 'Updated'}
        await updateTodo(payload)

        expect(mockedAxios.put).toHaveBeenCalledWith('http://api.test/todos/3', payload)
    })

    it('deleteTodo hits the delete endpoint', async () => {
        vi.stubEnv('VITE_API_BASE_URL', 'http://api.test')

        const {deleteTodo} = await import('../todo-service')
        await deleteTodo(5)

        expect(mockedAxios.delete).toHaveBeenCalledWith('http://api.test/todos/5')
    })
})
