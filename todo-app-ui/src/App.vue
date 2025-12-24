<script setup lang="ts"></script>

<template>
  <v-app>
    <v-main>
      <v-app-bar>
        <v-app-bar-title>Todo App</v-app-bar-title>

      </v-app-bar>
      <div class="router-container">
        <router-view></router-view>
      </div>
    </v-main>
  </v-app>
</template>

<style scoped>
.router-container {
  padding: 16px;
}

</style>
