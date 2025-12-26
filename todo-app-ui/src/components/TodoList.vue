<script setup lang="ts">
import type {Category} from "../domain/category.ts";
import type {TodoEntry} from "../domain/todo-entry.ts";
import {getCategories} from "../services/category-service.ts";
import {addTodo, getTodos} from "../services/todo-service.ts";
import {onMounted, ref} from "vue";
import TodoCard from "../components/TodoCard.vue";
import {PriorityEnum} from "../domain/priority.enum.ts";

const todos = ref<TodoEntry[]>([]);
const filteredTodos = ref<TodoEntry[]>([]);
const categories = ref<Category[]>([]);
const dialog = ref(false);
const valid = ref(false);
const emptyTodo = {
  title: '',
  description: '',
  dueDate: null,
  priority: PriorityEnum.MEDIUM,
  category: null as Category | null,
}
const todoForm = ref({...emptyTodo});

onMounted(async () => {
  await Promise.all([getTodoEntries(), fetchCategories()])
})

async function getTodoEntries() {
  todos.value = await getTodos()
  // das ist nur ein example
  // filteredTodos.value = filteredTodos.value.filter(todo => !todo.done);
  filteredTodos.value = todos.value;
}

async function fetchCategories() {
  categories.value = await getCategories()
}

function openNewTodoDialog() {
  // await addTodo(demoTodo); // Rufe Post Route vom Backend auf
  dialog.value = true;
}

async function createTodo() {
  dialog.value = false;
  const newTodo: TodoEntry = {
    ...todoForm.value
  }
  await addTodo(newTodo); // Rufe Post Route vom Backend auf
  getTodoEntries()
}

function onDialogUpdate(value: boolean) {
  if (!value) {
    todoForm.value = {...emptyTodo};
  }
}

function closeDialog() {
  dialog.value = false;
  onDialogUpdate(false);
}

</script>

<template>
  <div class="title-button-container">
    <h1>Aufgabenliste</h1>
    <v-btn @click="openNewTodoDialog">
      <v-icon start>mdi-plus</v-icon>
      Hinzufügen
    </v-btn>
  </div>

  <div class="todo-card-container">
    <TodoCard v-for="todo in filteredTodos" :key="todo.id" :todo="todo"
              :categories="categories"
              @updated-todo="getTodoEntries"/>
  </div>

  <v-dialog class="new-todo-dialog"
            v-model="dialog"
            @update:modelValue="onDialogUpdate"
  >
    <v-card>
      <v-card-title>
        Todo hinzufügen
      </v-card-title>
      <v-card-text>
        <v-form v-model="valid">
          <v-text-field
              v-model="todoForm.title"
              label="Titel"
              required
          ></v-text-field>
          <v-text-field
              v-model="todoForm.description"
              label="Beschreibung"
          ></v-text-field>
          <v-date-input
              v-model="todoForm.dueDate"
              label="Fällig am"
              prepend-icon=""
          ></v-date-input>
          <v-select
              v-model="todoForm.category"
              label="Kategorie"
              :items="categories"
              item-title="name"
              return-object
              clearable
              :disabled="!categories.length"
          ></v-select>
          <v-select
              v-model="todoForm.priority"
              label="Priorität"
              :items="Object.values(PriorityEnum)"
          ></v-select>
        </v-form>
      </v-card-text>
      <v-card-actions>
        <v-btn
            text="Schliessen"
            type="button"
            @click="closeDialog"
        ></v-btn>
        <v-btn
            color="primary"
            :disabled="!todoForm.title.trim()"
            text="Erstellen"
            @click="createTodo"
        ></v-btn>
      </v-card-actions>

    </v-card>
  </v-dialog>
</template>

<style scoped>
.new-todo-dialog {
  width: 400px;
}

.title-button-container {
  display: flex;
  justify-content: space-between;
}

.todo-card-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
}
</style>
