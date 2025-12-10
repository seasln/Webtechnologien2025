<script setup lang="ts"></script>

<template>
  <v-app>
    <v-main>
      <v-app-bar>
        <v-app-bar-title>Todo App</v-app-bar-title>

      </v-app-bar>
      <v-navigation-drawer permanent>
        <v-list-item link title="All Tasks"></v-list-item>
        <v-list-item link title="Future Tasks"></v-list-item>
        <v-list-item link title="Completed"></v-list-item>
      </v-navigation-drawer>
      <router-view></router-view>
    </v-main>
  </v-app>
</template>

<style scoped></style>
