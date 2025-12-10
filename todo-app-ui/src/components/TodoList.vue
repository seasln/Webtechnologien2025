<script setup lang="ts">
function createTestTask() {
  console.log("createTestTask");
}
</script>

<template>
  <div class="title-button-container">
    <h1>Aufgabenliste</h1>
    <v-btn @click="createTestTask">Demotask</v-btn>
  </div>
</template>

<style scoped>
.title-button-container {
  display: flex;
  justify-content: space-between;
}
</style>