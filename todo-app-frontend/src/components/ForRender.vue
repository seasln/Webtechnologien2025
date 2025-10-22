<script setup lang="ts">
let list = ['String 1', 'String 2', 'String 3'];
</script>

<template>
  <div v-for="string of list">{{string}}</div>
</template>

<style scoped>

</style>
