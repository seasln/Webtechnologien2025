<script setup lang="ts">
import type {Category} from "../domain/category.ts";
import type {TodoEntry} from "../domain/todo-entry.ts";
import {getCategories} from "../services/category-service.ts";
import {addTodo, getTodos} from "../services/todo-service.ts";
import {onMounted, ref, watch} from "vue";
import TodoCard from "../components/TodoCard.vue";
import {PriorityEnum} from "../domain/priority.enum.ts";

const todos = ref<TodoEntry[]>([]);
const filteredTodos = ref<TodoEntry[]>([]);
const categories = ref<Category[]>([]);
const dialog = ref(false);
const valid = ref(false);
const filterSelection = ref('all');
const priorityMeta: Record<PriorityEnum, { label: string }> = {
  [PriorityEnum.LOW]: {label: 'Niedrig'},
  [PriorityEnum.MEDIUM]: {label: 'Mittel'},
  [PriorityEnum.HIGH]: {label: 'Hoch'},
};
const priorityItems = Object.values(PriorityEnum).map((value) => ({
  value,
  label: priorityMeta[value].label,
}));
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

watch([todos, filterSelection], () => {
  applyFilters();
}, {immediate: true});

async function getTodoEntries() {
  todos.value = await getTodos()
  applyFilters();
}

async function fetchCategories() {
  categories.value = await getCategories()
}

function openNewTodoDialog() {
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

function applyFilters() {
  const selection = filterSelection.value;
  filteredTodos.value = todos.value.filter((todo) => {
    if (selection !== 'done' && selection !== 'all' && todo.done) {
      return false;
    }
    if (selection === 'open') {
      return true;
    }
    if (selection === 'done') {
      return todo.done === true;
    }
    if (selection === 'today') {
      return isOnOrBeforeToday(todo.dueDate);
    }
    if (selection === 'high') {
      return todo.priority === PriorityEnum.HIGH;
    }
    return true;
  });
}

function isOnOrBeforeToday(value: unknown): boolean {
  const date = toDate(value);
  if (!date) {
    return false;
  }
  const todayStart = new Date();
  todayStart.setHours(0, 0, 0, 0);
  return date <= todayStart;
}

function toDate(value: unknown): Date | null {
  if (!value) {
    return null;
  }
  if (value instanceof Date) {
    return value;
  }
  if (typeof value === 'string') {
    const isoMatch = value.match(/^(\d{4})-(\d{2})-(\d{2})$/);
    if (isoMatch) {
      const [, year, month, day] = isoMatch;
      return new Date(Number(year), Number(month) - 1, Number(day));
    }
    const parsed = new Date(value);
    if (!Number.isNaN(parsed.getTime())) {
      return parsed;
    }
  }
  return null;
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

  <div class="filter-button-container">
    <v-btn-toggle v-model="filterSelection" mandatory class="filter-toggle">
      <v-btn value="all">Alle</v-btn>
      <v-btn value="open">Offen</v-btn>
      <v-btn value="done">Erledigt</v-btn>
      <v-btn value="today">Heute</v-btn>
      <v-btn value="high">Hohe Priorität</v-btn>
    </v-btn-toggle>
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
          <v-textarea
              v-model="todoForm.description"
              label="Beschreibung"
          ></v-textarea>
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
              :items="priorityItems"
              item-title="label"
              item-value="value"
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

.filter-button-container {
  display: flex;
  margin: 12px 0 16px;
}

.filter-toggle {
  flex-wrap: wrap;
  gap: 8px;
}

.todo-card-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
}
</style>
