<script setup lang="ts">
import {onMounted, ref} from "vue";
import type {Category} from "../domain/category.ts";
import {addCategory, deleteCategory, getCategories, updateCategory} from "../services/category-service.ts";

const categories = ref<Category[]>([]);
const dialog = ref(false);
const isEditing = ref(false);
const valid = ref(false);
const emptyForm = {
  name: '',
  colorHex: '#4f46e5',
};
const categoryForm = ref({...emptyForm});
const editingId = ref<number | null>(null);

onMounted(() => {
  fetchCategories()
})

async function fetchCategories() {
  categories.value = await getCategories()
}

function openCreateDialog() {
  isEditing.value = false;
  editingId.value = null;
  categoryForm.value = {...emptyForm};
  dialog.value = true;
}

function openEditDialog(category: Category) {
  isEditing.value = true;
  editingId.value = category.id ?? null;
  categoryForm.value = {
    name: category.name ?? '',
    colorHex: category.colorHex ?? emptyForm.colorHex,
  };
  dialog.value = true;
}

function onDialogUpdate(value: boolean) {
  if (!value) {
    resetForm();
  }
}

function closeDialog() {
  dialog.value = false;
  onDialogUpdate(false);
}

function resetForm() {
  categoryForm.value = {...emptyForm};
  editingId.value = null;
  isEditing.value = false;
}

async function saveCategory() {
  const payload: Category = {
    name: categoryForm.value.name.trim(),
    colorHex: categoryForm.value.colorHex.trim(),
  };

  if (isEditing.value) {
    payload.id = editingId.value ?? undefined;
    await updateCategory(payload);
  } else {
    await addCategory(payload);
  }

  dialog.value = false;
  resetForm();
  await fetchCategories()
}

async function removeCategory(category: Category) {
  if (category.id == null) {
    return;
  }
  const confirmed = window.confirm(`Kategorie "${category.name}" wirklich loeschen?`);
  if (!confirmed) {
    return;
  }
  await deleteCategory(category.id)
  await fetchCategories()
}
</script>

<template>
  <v-card class="category-card">
    <v-card-title class="title-row">
      <span>Kategorien</span>
      <v-btn @click="openCreateDialog">
        <v-icon start>mdi-plus</v-icon>
        Hinzufuegen
      </v-btn>
    </v-card-title>
    <v-card-text>
      <v-alert v-if="!categories.length" type="info" variant="tonal">
        Noch keine Kategorien vorhanden.
      </v-alert>
      <v-list v-else>
        <v-list-item
            v-for="category in categories"
            :key="category.id ?? category.name"
        >
          <template #prepend>
            <span class="color-dot" :style="{ backgroundColor: category.colorHex }"></span>
          </template>
          <v-list-item-title>{{ category.name }}</v-list-item-title>
          <v-list-item-subtitle>{{ category.colorHex }}</v-list-item-subtitle>
          <template #append>
            <v-btn
                icon="mdi-pencil"
                variant="text"
                @click="openEditDialog(category)"
            ></v-btn>
            <v-btn
                icon="mdi-delete"
                variant="text"
                color="red"
                @click="removeCategory(category)"
            ></v-btn>
          </template>
        </v-list-item>
      </v-list>
    </v-card-text>
  </v-card>

  <v-dialog class="category-dialog"
            v-model="dialog"
            @update:modelValue="onDialogUpdate"
  >
    <v-card>
      <v-card-title>
        {{ isEditing ? 'Kategorie bearbeiten' : 'Kategorie hinzufuegen' }}
      </v-card-title>
      <v-card-text>
        <v-form v-model="valid">
          <v-text-field
              v-model="categoryForm.name"
              label="Name"
              required
          ></v-text-field>
          <v-text-field
              v-model="categoryForm.colorHex"
              label="Farbe (Hex)"
              placeholder="#AABBCC"
          ></v-text-field>
          <v-color-picker
              v-model="categoryForm.colorHex"
              hide-inputs
              show-swatches
          ></v-color-picker>
          <div class="color-preview">
            <span class="color-dot" :style="{ backgroundColor: categoryForm.colorHex }"></span>
            <span class="color-value">{{ categoryForm.colorHex }}</span>
          </div>
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
            :disabled="!categoryForm.name.trim() || !categoryForm.colorHex.trim()"
            :text="isEditing ? 'Speichern' : 'Erstellen'"
            @click="saveCategory"
        ></v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<style scoped>
.category-card {
  display: flex;
  flex-direction: column;
}

.title-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.color-dot {
  display: inline-block;
  width: 14px;
  height: 14px;
  border-radius: 999px;
  border: 1px solid rgba(0, 0, 0, 0.2);
  margin-right: 8px;
}

.color-preview {
  display: flex;
  align-items: center;
  gap: 8px;
  padding-top: 8px;
}

.color-value {
  font-size: 0.9rem;
  color: rgba(0, 0, 0, 0.6);
}

.category-dialog {
  width: 420px;
}
</style>
