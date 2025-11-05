<template>
  <div class="app-container">
    <Sidebar 
      :tasks="tasks"
      :selectedFilter="selectedFilter"
      @filter-change="handleFilterChange"
    />
    <TaskList 
      :tasks="filteredTasks"
      :selectedFilter="selectedFilter"
      @add-task="handleAddTask"
      @toggle-task="handleToggleTask"
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
        }
      ]
    }
  },
  computed: {
    filteredTasks() {
      if (this.selectedFilter === 'all') {
        return this.tasks.filter(t => !t.completed)
      } else if (this.selectedFilter === 'completed') {
        return this.tasks.filter(t => t.completed)
      } else if (this.selectedFilter === 'important') {
        return this.tasks.filter(t => t.priority === 'High Priority')
      } else if (this.selectedFilter === 'today') {
        return this.tasks.filter(t => t.dueDate === 'today')
      } else if (this.selectedFilter === 'week') {
        return this.tasks.filter(t => t.dueDate === 'this week' || t.dueDate === 'next week')
      }
      return this.tasks
    }
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
        project: task.project || 'No Project',
        priority: task.priority || null,
        priorityColor: task.priorityColor || null
      })
    },
    handleToggleTask(taskId) {
      const task = this.tasks.find(t => t.id === taskId)
      if (task) {
        task.completed = !task.completed
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
}
</style>

