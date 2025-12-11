<script setup lang="ts">
import type {TodoEntry} from "../domain/todo-entry.ts";

const props = defineProps<{
  todo: TodoEntry
}>();
</script>

<template>
  <v-card class="todo-card">
    <v-card-title>{{ todo.title }}</v-card-title>
    <v-card-subtitle>{{ todo.description }}</v-card-subtitle>
    <v-checkbox class="checkbox"></v-checkbox>
    <v-card-actions class="actions">
      <v-btn>Löschen</v-btn>
      <v-btn>Bearbeiten</v-btn>
    </v-card-actions>
  </v-card>
</template>

<style scoped>
.todo-card {
  width: 600px;
}

.actions {
  margin-top: -36px;
  display: flex;
  justify-content: end;
}
</style>