import axios from 'axios'
import type {Category} from "@/domain/category.ts";

const API_URL = import.meta.env.VITE_API_BASE_URL + '/categories'

export async function getCategories() {
    const response = await axios.get(API_URL)
    return response.data
}

export async function addCategory(category: Category) {
    const response = await axios.post(API_URL, category)
    return response.data
}

export async function updateCategory(category: Category) {
    if (category.id == null) {
        throw new Error('Category id is required for update')
    }
    const response = await axios.put(`${API_URL}/${category.id}`, category)
    return response.data
}

export async function deleteCategory(id: number) {
    await axios.delete(`${API_URL}/${id}`)
}
