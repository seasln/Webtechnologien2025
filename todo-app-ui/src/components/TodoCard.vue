<script setup lang="ts">
import type {Category} from "../domain/category.ts";
import type {TodoEntry} from "../domain/todo-entry.ts";
import {updateTodo} from "../services/todo-service.ts";
import {PriorityEnum} from "../domain/priority.enum.ts";
import {ref} from "vue";

const props = defineProps<{
  todo: TodoEntry
  categories: Category[]
}>();

const emit = defineEmits<{
  (e: 'updatedTodo'): void
}>();

const dialog = ref(false);
const valid = ref(false);
const titleError = ref('');
const descriptionError = ref('');
const todoForm = ref({
  title: '',
  description: '',
  dueDate: '',
  priority: PriorityEnum.MEDIUM,
  category: null as Category | null,
});
const priorityMeta: Record<PriorityEnum, { icon: string; label: string }> = {
  [PriorityEnum.LOW]: {icon: 'mdi-arrow-down', label: 'Niedrig'},
  [PriorityEnum.MEDIUM]: {icon: 'mdi-arrow-right', label: 'Mittel'},
  [PriorityEnum.HIGH]: {icon: 'mdi-arrow-up', label: 'Hoch'},
};
const priorityItems = Object.values(PriorityEnum).map((value) => ({
  value,
  label: priorityMeta[value].label,
}));

function formatDueDate(value: string | Date | null | undefined) {
  if (!value) {
    return '';
  }
  if (typeof value === 'string') {
    const isoMatch = value.match(/^(\d{4})-(\d{2})-(\d{2})$/);
    if (isoMatch) {
      const [, year, month, day] = isoMatch;
      return `${day}.${month}.${year}`;
    }
    const parsed = new Date(value);
    if (!Number.isNaN(parsed.getTime())) {
      return formatDateParts(parsed);
    }
    return value;
  }
  return formatDateParts(value);
}

function formatDateParts(date: Date) {
  const day = String(date.getDate()).padStart(2, '0');
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const year = date.getFullYear();
  return `${day}.${month}.${year}`;
}

async function todoUpdate() {
  await updateTodo(props.todo);
  emit('updatedTodo');
}

function openEditDialog() {
  todoForm.value = {
    title: props.todo.title ?? '',
    description: props.todo.description ?? '',
    dueDate: props.todo.dueDate ?? '',
    priority: props.todo.priority ?? PriorityEnum.MEDIUM,
    category: props.todo.category ?? null,
  };
  dialog.value = true;
}

function validateForm(): boolean {
  titleError.value = '';
  descriptionError.value = '';
  
  if (todoForm.value.title && todoForm.value.title.length > 25) {
    titleError.value = 'Maximum of 25 characters allowed';
    return false;
  }
  
  if (todoForm.value.description && todoForm.value.description.length > 200) {
    descriptionError.value = 'Maximum of 200 characters allowed';
    return false;
  }
  
  return true;
}

async function saveEdit() {
  if (!validateForm()) {
    return;
  }
  
  dialog.value = false;
  const updatedTodo: TodoEntry = {
    ...props.todo,
    ...todoForm.value,
  };
  await updateTodo(updatedTodo);
  emit('updatedTodo');
}

function closeDialog() {
  dialog.value = false;
}

</script>

<template>
  <v-card class="todo-card">
    <v-card-title class="title-row">
      <span :class="{done: todo.done}">{{ todo.title }}</span>
      <v-spacer></v-spacer>
      <v-checkbox
          class="checkbox"
          v-model="todo.done"
          density="compact"
          hide-details
          @update:modelValue="todoUpdate"
      ></v-checkbox>
    </v-card-title>
    <v-card-subtitle v-if="todo.category?.name" class="category-row">
      <span class="category-dot" :style="{ backgroundColor: todo.category.colorHex || '#000000' }"></span>
      <span>{{ todo.category.name }}</span>
    </v-card-subtitle>
    <v-card-subtitle v-if="!!todo.dueDate">Fällig am: {{ formatDueDate(todo.dueDate) }}</v-card-subtitle>
    <v-card-subtitle v-if="todo.priority" class="priority-row">
      Priorität: {{ priorityMeta[todo.priority].label }}
    </v-card-subtitle>
    <v-card-text v-if="!!todo.description">{{ todo.description }}</v-card-text>

    <v-card-actions class="actions">
      <v-btn color="red">Löschen</v-btn>
      <v-btn @click="openEditDialog">Bearbeiten</v-btn>
    </v-card-actions>
  </v-card>

  <v-dialog class="new-todo-dialog"
            v-model="dialog"
  >
    <v-card>
      <v-card-title>
        Todo bearbeiten
      </v-card-title>
      <v-card-text>
        <v-form v-model="valid">
          <v-text-field
              v-model="todoForm.title"
              label="Titel"
              required
              maxlength="25"
              :error-messages="titleError"
              :hint="todoForm.title.length === 25 ? 'Character limit reached' : ''"
              persistent-hint
              @input="titleError = todoForm.title.length > 25 ? 'Maximum of 25 characters allowed' : ''"
          ></v-text-field>
          <v-textarea
              v-model="todoForm.description"
              label="Beschreibung"
              maxlength="200"
              :error-messages="descriptionError"
              :hint="todoForm.description && todoForm.description.length === 200 ? 'Character limit reached' : ''"
              persistent-hint
              @input="descriptionError = todoForm.description && todoForm.description.length > 200 ? 'Maximum of 200 characters allowed' : ''"
          ></v-textarea>
          <v-text-field
              v-model="todoForm.dueDate"
              label="Fällig am"
              type="date"
          ></v-text-field>
          <v-select
              v-model="todoForm.category"
              label="Kategorie"
              :items="props.categories"
              item-title="name"
              return-object
              clearable
              :disabled="!props.categories.length"
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
            text="Schließen"
            type="button"
            @click="closeDialog"
        ></v-btn>
        <v-btn
            color="primary"
            :disabled="!todoForm.title.trim() || todoForm.title.length > 25 || (!!todoForm.description && todoForm.description.length > 200)"
            text="Speichern"
            @click="saveEdit"
        ></v-btn>
      </v-card-actions>

    </v-card>
  </v-dialog>
</template>

<style scoped>
.todo-card {
  width: 600px;
}

.title-row {
  display: flex;
  align-items: center;
  gap: 12px;
}

.done {
  text-decoration: line-through;
  opacity: 0.7;
}

.actions {
  display: flex;
  justify-content: end;
}

.category-row {
  display: flex;
  align-items: center;
  gap: 6px;
}

.category-dot {
  display: inline-block;
  width: 12px;
  height: 12px;
  border-radius: 999px;
  border: 1px solid rgba(0, 0, 0, 0.2);
}

.priority-row {
  text-transform: capitalize;
}

.new-todo-dialog {
  width: 400px;
}
</style>
