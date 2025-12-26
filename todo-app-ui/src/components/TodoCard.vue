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
const todoForm = ref({
  title: '',
  description: '',
  dueDate: null as string | null,
  priority: PriorityEnum.MEDIUM,
  category: null as Category | null,
});

async function todoUpdate() {
  await updateTodo(props.todo);
  emit('updatedTodo');
}

function openEditDialog() {
  todoForm.value = {
    title: props.todo.title ?? '',
    description: props.todo.description ?? '',
    dueDate: props.todo.dueDate ?? null,
    priority: props.todo.priority ?? PriorityEnum.MEDIUM,
    category: props.todo.category ?? null,
  };
  dialog.value = true;
}

async function saveEdit() {
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
    <v-card-subtitle v-if="!!todo.dueDate">{{ todo.dueDate }}</v-card-subtitle>
    <v-card-subtitle v-if="todo.category?.name" class="category-row">
      <span class="category-dot" :style="{ backgroundColor: todo.category.colorHex || '#000000' }"></span>
      <span>{{ todo.category.name }}</span>
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
          ></v-text-field>
          <v-text-field
              v-model="todoForm.description"
              label="Beschreibung"
          ></v-text-field>
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
              :items="Object.values(PriorityEnum)"
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
            :disabled="!todoForm.title.trim()"
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

.new-todo-dialog {
  width: 400px;
}
</style>
