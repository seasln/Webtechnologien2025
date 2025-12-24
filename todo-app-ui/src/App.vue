<script setup lang="ts">
import {ref} from 'vue';

const drawer = ref(true);
</script>

<template>
  <v-app>
    <v-app-bar>
      <v-app-bar-nav-icon @click="drawer = !drawer"></v-app-bar-nav-icon>
      <v-app-bar-title>Todo App</v-app-bar-title>
    </v-app-bar>
    <v-navigation-drawer v-model="drawer">
      <v-list density="compact" nav>
        <v-list-item title="Todos" to="/"></v-list-item>
        <v-list-item title="Categories" to="/categories"></v-list-item>
      </v-list>
    </v-navigation-drawer>
    <v-main>
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
