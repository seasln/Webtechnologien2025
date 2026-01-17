<script setup lang="ts">
import type {Category} from "../domain/category.ts";
import type {TodoEntry} from "../domain/todo-entry.ts";
import {getCategories} from "../services/category-service.ts";
import {addTodo, getTodos} from "../services/todo-service.ts";
import {computed, onMounted, ref, watch} from "vue";
import TodoCard from "../components/TodoCard.vue";
import {PriorityEnum} from "../domain/priority.enum.ts";
import {useSnackbar} from "@/util/useSnackbar";

const todos = ref<TodoEntry[]>([]);
const filteredTodos = ref<TodoEntry[]>([]);
const categories = ref<Category[]>([]);
const dialog = ref(false);
const valid = ref(false);
const filterSelection = ref('all');
const sortSelection = ref<'createdAt' | 'dueDate' | 'priority' | 'title'>('createdAt');
const sortDirection = ref<'asc' | 'desc'>('desc');
const searchQuery = ref('');
const titleError = ref('');
const descriptionError = ref('');
const priorityMeta: Record<PriorityEnum, { label: string }> = {
  [PriorityEnum.LOW]: {label: 'Niedrig'},
  [PriorityEnum.MEDIUM]: {label: 'Mittel'},
  [PriorityEnum.HIGH]: {label: 'Hoch'},
};
const priorityRank: Record<PriorityEnum, number> = {
  [PriorityEnum.LOW]: 1,
  [PriorityEnum.MEDIUM]: 2,
  [PriorityEnum.HIGH]: 3,
};
const priorityItems = Object.values(PriorityEnum).map((value) => ({
  value,
  label: priorityMeta[value].label,
}));
const emptyTodo = {
  title: '',
  description: '',
  dueDate: '',
  priority: PriorityEnum.MEDIUM,
  category: null as Category | null,
}
const todoForm = ref({...emptyTodo});
const {showSnackbar, showError} = useSnackbar();
const totalTodos = computed(() => todos.value.length);
const completedTodos = computed(() => todos.value.filter((todo) => todo.done).length);
const progressPercent = computed(() => {
  if (!totalTodos.value) {
    return 0;
  }
  return Math.round((completedTodos.value / totalTodos.value) * 100);
});
const sortDirectionIcon = computed(() =>
    sortDirection.value === 'asc' ? 'mdi-sort-ascending' : 'mdi-sort-descending',
);
const sortDirectionLabel = computed(() =>
    sortDirection.value === 'asc' ? 'Aufsteigend' : 'Absteigend',
);

onMounted(async () => {
  await Promise.all([getTodoEntries(), fetchCategories()])
})

watch([todos, filterSelection, searchQuery, sortSelection, sortDirection], () => {
  applyFilters();
}, {immediate: true});

async function getTodoEntries() {
  try {
    todos.value = await getTodos()
    applyFilters();
  } catch (error) {
    showError('Todos konnten nicht geladen werden.');
  }
}

async function fetchCategories() {
  try {
    categories.value = await getCategories()
  } catch (error) {
    showError('Kategorien konnten nicht geladen werden.');
  }
}

function openNewTodoDialog() {
  dialog.value = true;
}

function validateForm(): boolean {
  titleError.value = '';
  descriptionError.value = '';

  if (todoForm.value.title && todoForm.value.title.length > 25) {
    return false;
  }

  if (todoForm.value.description && todoForm.value.description.length > 200) {
    return false;
  }

  return true;
}

async function createTodo() {
  if (!validateForm()) {
    return;
  }

  dialog.value = false;
  const newTodo: TodoEntry = {
    ...todoForm.value
  }
  try {
    await addTodo(newTodo); // Rufe Post Route vom Backend auf
    showSnackbar('Todo wurde erstellt.');
    closeDialog()
    getTodoEntries()
  } catch (error) {
    showError('Todo konnte nicht erstellt werden.');
  }
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
  const query = (searchQuery.value ?? '').trim().toLowerCase();
  const filtered = todos.value.filter((todo) => {
    if (selection !== 'done' && selection !== 'all' && todo.done) {
      return false;
    }
    if (selection === 'open') {
      return matchesQuery(todo, query);
    }
    if (selection === 'done') {
      return todo.done === true && matchesQuery(todo, query);
    }
    if (selection === 'today') {
      return isOnOrBeforeToday(todo.dueDate) && matchesQuery(todo, query);
    }
    if (selection === 'high') {
      return todo.priority === PriorityEnum.HIGH && matchesQuery(todo, query);
    }
    return matchesQuery(todo, query);
  });
  filteredTodos.value = sortTodos(filtered, sortSelection.value, sortDirection.value);
}

function matchesQuery(todo: TodoEntry, query: string) {
  if (!query) {
    return true;
  }
  const title = todo.title?.toLowerCase() ?? '';
  const description = todo.description?.toLowerCase() ?? '';
  return title.includes(query) || description.includes(query);
}

function sortTodos(
    list: TodoEntry[],
    sortKey: 'createdAt' | 'dueDate' | 'priority' | 'title',
    direction: 'asc' | 'desc',
) {
  const sorted = [...list];
  sorted.sort((a, b) => compareTodos(a, b, sortKey, direction));
  return sorted;
}

function compareTodos(
    a: TodoEntry,
    b: TodoEntry,
    sortKey: 'createdAt' | 'dueDate' | 'priority' | 'title',
    direction: 'asc' | 'desc',
) {
  switch (sortKey) {
    case 'createdAt':
      return compareDates(a.createdAt, b.createdAt, direction);
    case 'dueDate':
      return compareDates(a.dueDate, b.dueDate, direction);
    case 'priority':
      return compareNumbers(
          a.priority ? priorityRank[a.priority] : null,
          b.priority ? priorityRank[b.priority] : null,
          direction,
      );
    case 'title':
      return compareStrings(a.title, b.title, direction);
    default:
      return 0;
  }
}

function compareDates(aValue: unknown, bValue: unknown, direction: 'asc' | 'desc') {
  const aDate = toDate(aValue);
  const bDate = toDate(bValue);
  if (!aDate && !bDate) {
    return 0;
  }
  if (!aDate) {
    return 1;
  }
  if (!bDate) {
    return -1;
  }
  const diff = aDate.getTime() - bDate.getTime();
  return direction === 'asc' ? diff : -diff;
}

function compareNumbers(
    aValue: number | null | undefined,
    bValue: number | null | undefined,
    direction: 'asc' | 'desc',
) {
  if (aValue == null && bValue == null) {
    return 0;
  }
  if (aValue == null) {
    return 1;
  }
  if (bValue == null) {
    return -1;
  }
  const diff = aValue - bValue;
  return direction === 'asc' ? diff : -diff;
}

function compareStrings(
    aValue: string | null | undefined,
    bValue: string | null | undefined,
    direction: 'asc' | 'desc',
) {
  const a = (aValue ?? '').trim().toLowerCase();
  const b = (bValue ?? '').trim().toLowerCase();
  if (!a && !b) {
    return 0;
  }
  if (!a) {
    return 1;
  }
  if (!b) {
    return -1;
  }
  const diff = a.localeCompare(b);
  return direction === 'asc' ? diff : -diff;
}

function toggleSortDirection() {
  sortDirection.value = sortDirection.value === 'asc' ? 'desc' : 'asc';
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
    <div class="filter-controls">
      <v-btn-toggle v-model="filterSelection" mandatory class="filter-toggle">
        <v-btn value="all">Alle</v-btn>
        <v-btn value="open">Offen</v-btn>
        <v-btn value="done">Erledigt</v-btn>
        <v-btn value="today">Fällig</v-btn>
        <v-btn value="high">Hohe Priorität</v-btn>
      </v-btn-toggle>
    </div>
    <div class="sort-controls">
      <span class="sort-label">Sortieren</span>
      <v-btn-toggle v-model="sortSelection" mandatory class="sort-toggle">
        <v-btn value="createdAt">Erstellt</v-btn>
        <v-btn value="dueDate">Fällig</v-btn>
        <v-btn value="priority">Priorität</v-btn>
        <v-btn value="title">Titel</v-btn>
      </v-btn-toggle>
      <v-btn
          icon
          size="small"
          class="sort-direction"
          :aria-label="sortDirectionLabel"
          @click="toggleSortDirection"
      >
        <v-icon size="18">{{ sortDirectionIcon }}</v-icon>
      </v-btn>
    </div>
    <v-text-field
        v-model="searchQuery"
        label="Suche"
        placeholder="Titel oder Beschreibung"
        density="compact"
        variant="outlined"
        clearable
        @click:clear="searchQuery = ''"
        hide-details
        class="search-input"
    ></v-text-field>
  </div>

  <div v-if="totalTodos" class="progress-row">
    <div class="progress-label">
      Erledigt {{ completedTodos }} / {{ totalTodos }}
    </div>
    <v-progress-linear
        :model-value="progressPercent"
        color="green"
        height="8"
        rounded
    ></v-progress-linear>
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
              maxlength="25"
              :error-messages="titleError"
              :hint="todoForm.title.length === 25 ? 'Maximal 25 Zeichen erlaubt' : ''"
              persistent-hint
          ></v-text-field>
          <v-textarea
              v-model="todoForm.description"
              label="Beschreibung"
              maxlength="200"
              :error-messages="descriptionError"
              :hint="todoForm.description && todoForm.description.length === 200 ? 'Maximal 200 Zeichen erlaubt' : ''"
              persistent-hint
          ></v-textarea>
          <v-text-field
              v-model="todoForm.dueDate"
              label="Fällig am"
              type="date"
          ></v-text-field>
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
            :disabled="!todoForm.title.trim() || todoForm.title.length > 25 || (!!todoForm.description && todoForm.description.length > 200)"
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
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  margin: 12px 0 16px;
  gap: 12px;
}

.filter-controls {
  display: inline-flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 6px;
  padding: 4px 8px;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.04);
}

.filter-toggle {
  flex-wrap: wrap;
  gap: 4px;
}

.filter-toggle :deep(.v-btn) {
  min-width: 0;
  padding: 0 8px;
  font-size: 0.75rem;
}

.sort-label {
  font-size: 0.8rem;
  color: rgba(255, 255, 255, 0.65);
}

.sort-controls {
  display: inline-flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 6px;
  padding: 4px 8px;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.04);
}

.sort-toggle {
  flex-wrap: wrap;
  gap: 4px;
}

.sort-toggle :deep(.v-btn) {
  min-width: 0;
  padding: 0 8px;
  font-size: 0.75rem;
}

.sort-direction {
  min-width: 32px;
}

.progress-row {
  margin: 8px 0 16px;
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.progress-label {
  font-size: 0.9rem;
  color: rgba(255, 255, 255, 0.7);
}

.search-input {
  min-width: 220px;
  max-width: 320px;
}

.todo-card-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
}
</style>
