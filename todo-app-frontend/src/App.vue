<template>
  <div class="app-container" :class="{ 'dark-mode': isDarkMode }">
    <Sidebar 
      :tasks="tasks"
      :selectedFilter="selectedFilter"
      :isDarkMode="isDarkMode"
      :projects="projects"
      @filter-change="handleFilterChange"
      @add-project="handleAddProject"
      @delete-project="handleDeleteProject"
    />
    <TaskList 
      :tasks="filteredTasks"
      :selectedFilter="selectedFilter"
      :isDarkMode="isDarkMode"
      :projects="projects"
      @add-task="handleAddTask"
      @toggle-task="handleToggleTask"
      @delete-task="handleDeleteTask"
      @update-task="handleUpdateTask"
      @toggle-dark-mode="toggleDarkMode"
    />
  </div>
</template>

<script>
import Sidebar from './components/Sidebar.vue'
import TaskList from './components/TaskList.vue'
import { getAllTodos } from "../api.js";

export default {
  name: 'App',
  components: {
    Sidebar,
    TaskList
  },
  data() {
    return {
      selectedFilter: 'all',
      isDarkMode: false,
      projects: [
        { name: 'No Project', color: '#9ca3af', isDefault: true },
        { name: 'Work Projects', color: '#3b82f6' },
        { name: 'Personal', color: '#10b981' },
        { name: 'Learning', color: '#9333ea' }
      ],


      tasks: []
    }
  },
  computed: {
    filteredTasks() {
      if (this.selectedFilter === 'all') {
        return this.tasks // Return all tasks, let TaskList handle Active/Completed filtering
      } else if (this.selectedFilter === 'completed') {
        return this.tasks.filter(t => t.completed)
      } else if (this.selectedFilter === 'important') {
        return this.tasks.filter(t => t.priority === 'High Priority')
      } else if (this.selectedFilter === 'today') {
        return this.tasks.filter(t => t.dueDate === 'today')
      } else if (this.selectedFilter === 'week') {
        return this.tasks.filter(t => t.dueDate === 'this week' || t.dueDate === 'next week')
      } else if (this.selectedFilter.startsWith('project:')) {
        const projectName = this.selectedFilter.replace('project:', '')
        // For "No Project", show tasks with null or 'No Project' as project
        if (projectName === 'No Project') {
          return this.tasks.filter(t => !t.project || t.project === 'No Project')
        }
        return this.tasks.filter(t => t.project === projectName)
      }
      return this.tasks
    }
  },
  async mounted() {
    // todos vom backend laden
    console.log('Lade Todos vom Backend...')
    try{
      const todosFromBackend = await getAllTodos()
      console.log('✅ Todos vom Backend erhalten:', todosFromBackend);

      //backend daten in frontend format umwandeln
      this.tasks = todosFromBackend.map(todo => ({
        id: todo.id,
        title: todo.description,
        completed: todo.done,
        dueDate: todo.dueDate || 'Today',
        priority: todo.priority,
        priorityColor: this.getPriorityColor(todo.priority)
      }))

      console.log('✅ Tasks erfolgreich geladen:', this.tasks)
    } catch (error) {
      console.error('❌ Fehler beim Laden der Todos:', error)
      // Fallback: Zeige Fehlermeldung oder lade lokale Dummy-Daten
      alert('Todos konnten nicht vom Backend geladen werden. Bitte Backend-Verbindung prüfen!')
    }
  },
  methods: {
    getPriorityColor(priority){
      const colorMap = {
        'High Priority': 'red',
        'Medium Priority': 'yellow',
        'Low Priority': 'light-green',
      }
      return colorMap[priority] || null
    },
    handleFilterChange(filter) {
      this.selectedFilter = filter
    },
    handleAddTask(task) {
      this.tasks.push({
        id: this.tasks.length + 1,
        title: task.title,
        completed: false,
        dueDate: task.dueDate || 'no date',
        project: task.project && task.project !== 'No Project' ? task.project : 'No Project',
        priority: task.priority || null,
        priorityColor: task.priorityColor || null
      })
    },
    handleToggleTask(taskId) {
      const task = this.tasks.find(t => t.id === taskId)
      if (task) {
        task.completed = !task.completed
      }
    },
    handleDeleteTask(taskId) {
      const index = this.tasks.findIndex(t => t.id === taskId)
      if (index !== -1) {
        this.tasks.splice(index, 1)
      }
    },
    handleUpdateTask(updatedTask) {
      const task = this.tasks.find(t => t.id === updatedTask.id)
      if (task) {
        task.title = updatedTask.title
        task.project = updatedTask.project && updatedTask.project !== 'No Project' ? updatedTask.project : 'No Project'
        task.dueDate = updatedTask.dueDate
      }
    },
    toggleDarkMode() {
      this.isDarkMode = !this.isDarkMode
    },
    handleAddProject(project) {
      // Check if project name already exists
      if (!this.projects.find(p => p.name === project.name)) {
        this.projects.push({
          name: project.name,
          color: project.color
        })
      }
    },
    handleDeleteProject(projectName) {
      // Prevent deletion of default projects
      const project = this.projects.find(p => p.name === projectName)
      if (project && project.isDefault) {
        return
      }
      // Remove project from projects list
      const projectIndex = this.projects.findIndex(p => p.name === projectName)
      if (projectIndex !== -1) {
        // Move all tasks from this project to "No Project"
        this.tasks.forEach(task => {
          if (task.project === projectName) {
            task.project = 'No Project'
          }
        })
        // Remove the project
        this.projects.splice(projectIndex, 1)
        // If the deleted project was selected, switch to "all" filter
        if (this.selectedFilter === `project:${projectName}`) {
          this.selectedFilter = 'all'
        }
      }
    }
  }
}
</script>

<style scoped>
.app-container {
  display: flex;
  height: 100vh;
  width: 100%;
  background: #ffffff;
  transition: background-color 0.3s ease;
}

.app-container.dark-mode {
  background: #181818;
}
</style>

