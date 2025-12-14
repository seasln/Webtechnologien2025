import axios from 'axios'
import type {TodoEntry} from "@/domain/todo-entry.ts";

const API_URL = import.meta.env.VITE_API_BASE_URL + '/todos'

export async function getTodos() {
    const response = await axios.get(API_URL)
    return response.data
}

export async function addTodo(todo: TodoEntry) {
    const response = await axios.post(API_URL, todo)
    return response.data
}

export async function deleteTodo(id: number) {
    await axios.delete(`${API_URL}/${id}`)
}
