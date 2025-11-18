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
      tasks: [
        {
          id: 1,
          title: 'Complete project proposal for client meeting',
          completed: false,
          dueDate: 'today',
          project: 'Work Projects',
          priority: 'High Priority',
          priorityColor: 'red'
        },
        {
          id: 2,
          title: 'Review and update website content',
          completed: false,
          dueDate: 'tomorrow',
          project: 'Work Projects',
          priority: 'Medium Priority',
          priorityColor: 'yellow'
        },
        {
          id: 3,
          title: 'Buy groceries for the week',
          completed: true,
          dueDate: 'today',
          project: 'Personal',
          priority: null,
          priorityColor: null
        },
        {
          id: 4,
          title: 'Learn React hooks and context API',
          completed: false,
          dueDate: 'next week',
          project: 'Learning',
          priority: 'Low Priority',
          priorityColor: 'light-green'
        },
        {
          id: 5,
          title: 'Schedule dentist appointment',
          completed: false,
          dueDate: 'this week',
          project: 'Personal',
          priority: null,
          priorityColor: null
        },
        {
          id: 6,
          title: 'Random task without project',
          completed: false,
          dueDate: 'today',
          project: null,
          priority: null,
          priorityColor: null
        }
      ]
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
  mounted() {
    // Convert all tasks with null project to "No Project"
    this.tasks.forEach(task => {
      if (!task.project || task.project === null) {
        task.project = 'No Project'
      }
    })
  },
  methods: {
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

