<script setup lang="ts">
import type {TodoEntry} from "../domain/todo-entry.ts";
import {PriorityEnum} from "../domain/priority.enum.ts";
import {addTodo, getTodos} from "../services/todo-service.ts";
import {onMounted, ref} from "vue";
import TodoCard from "../components/TodoCard.vue";

const todos = ref<TodoEntry[]>([]);

onMounted(async () => {
  todos.value = await getTodos()
})

async function createTestTask() {
  const demoTodo: TodoEntry = {
    title: "Demotask",
    description: "Dies ist ein Demotask",
    dueDate: new Date(),
    done: false,
    priority: PriorityEnum.LOW
  };
  const newTodo = await addTodo(demoTodo); // Rufe Post Route vom Backend auf
}
</script>

<template>
  <div class="title-button-container">
    <h1>Aufgabenliste</h1>
    <v-btn @click="createTestTask">
      <v-icon start>mdi-plus</v-icon>
      Hinzufügen
    </v-btn>

  </div>
  <div class="todo-card-container">
    <TodoCard v-for="todo in todos" :key="todo.id" :todo="todo"/>
  </div>
</template>

<style scoped>
.title-button-container {
  display: flex;
  justify-content: space-between;
}

.todo-card-container {
  display: flex;
  justify-content: center;
}
</style>