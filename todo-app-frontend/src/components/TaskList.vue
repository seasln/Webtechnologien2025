<template>
  <div class="task-list-container" :class="{ 'dark-mode': isDarkMode }">
    <div class="header">
      <div class="header-top">
        <div class="header-text">
          <h1>{{ getHeaderTitle() }}</h1>
          <p class="subtitle">Manage your daily activities.</p>
        </div>
        <div class="header-actions">
          <div class="search-bar">
            <span class="search-icon">🔍</span>
            <input type="text" placeholder="Search tasks..." v-model="searchQuery" />
          </div>
          <button 
            class="dark-mode-btn" 
            @click="$emit('toggle-dark-mode')" 
            :title="isDarkMode ? 'Light mode' : 'Dark mode'"
          >
            {{ isDarkMode ? '☀️' : '🌙' }}
          </button>
          <div class="filter-buttons">
            <button 
              v-for="filter in availableViewFilters" 
              :key="filter"
              :class="['filter-btn', { active: activeViewFilter === filter }]"
              @click="activeViewFilter = filter"
            >
              {{ filter }}
            </button>
          </div>
          <button class="new-task-btn" @click="showAddTask = true">
            + New Task
          </button>
        </div>
      </div>
    </div>

    <div class="tasks-container">
      <div 
        v-for="task in displayedTasks" 
        :key="task.id"
        class="task-item"
        :class="{ completed: task.completed }"
      >
        <div class="task-checkbox" @click="$emit('toggle-task', task.id)">
          <div v-if="task.completed" class="checkbox-checked">✓</div>
          <div v-else class="checkbox-unchecked"></div>
        </div>
        <div class="task-content">
          <div class="task-title">{{ task.title }}</div>
          <div class="task-meta">
            <span class="task-due">Due {{ task.dueDate }}</span>
            <div class="task-tags">
              <span 
                v-if="task.project || task.project === null"
                class="task-tag"
                :style="getProjectStyle(task.project)"
              >
                {{ task.project || 'No Project' }}
              </span>
              <span 
                v-if="task.priority"
                class="task-tag"
                :class="getPriorityClass(task.priorityColor)"
              >
                {{ task.priority }}
              </span>
              <span v-if="task.completed" class="task-tag completed-tag">
                Completed today
              </span>
            </div>
          </div>
        </div>
        <div class="task-actions">
          <button class="edit-btn" @click="showEditTask(task)" title="Edit task">
            <img src="/icons/edit-icon.png" alt="Edit" class="edit-icon" />
          </button>
          <button class="delete-btn" @click="showDeleteConfirm(task)" title="Delete task">
            <img src="/icons/delete_icon.png" alt="Delete" class="delete-icon" />
          </button>
        </div>
      </div>
    </div>

    <!-- Success Notification -->
    <div v-if="showSuccessNotification" class="success-notification">
      <div class="success-content">
        <span class="success-icon">✓</span>
        <span class="success-text">Successfully deleted</span>
      </div>
    </div>

    <div class="pagination">
      <button class="page-btn">‹</button>
      <span class="page-info">1/1</span>
      <button class="page-btn">›</button>
    </div>

    <!-- Edit Task Modal -->
    <div v-if="showEditModal" class="modal-overlay" @click="closeEditModal">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h2>Edit Task</h2>
          <button class="close-btn" @click="closeEditModal">×</button>
        </div>
        <div class="modal-body">
          <div class="form-group">
            <label for="edit-task-title">Task Title</label>
            <input 
              id="edit-task-title"
              type="text" 
              placeholder="Task title..." 
              v-model="editTaskTitle"
              @keyup.enter="saveTask"
              class="modal-input"
              autofocus
            />
          </div>
          <div class="form-group">
            <label for="edit-task-project">Project</label>
            <select id="edit-task-project" v-model="editTaskProject" class="modal-select">
              <option value="No Project">No Project</option>
              <option 
                v-for="project in projects" 
                :key="project.name"
                :value="project.name"
              >
                {{ project.name }}
              </option>
            </select>
          </div>
          <div class="form-group">
            <label for="edit-task-due-date">Due Date</label>
            <input 
              id="edit-task-due-date"
              type="date" 
              v-model="editTaskDueDate"
              class="modal-input"
            />
          </div>
        </div>
        <div class="modal-footer">
          <button class="cancel-btn" @click="closeEditModal">Cancel</button>
          <button class="submit-btn" @click="saveTask">Save Changes</button>
        </div>
      </div>
    </div>

    <!-- Delete Confirmation Modal -->
    <div v-if="showDeleteModal" class="modal-overlay" @click="cancelDelete">
      <div class="modal-content delete-modal" @click.stop>
        <div class="modal-header">
          <h2>Delete Task</h2>
          <button class="close-btn" @click="cancelDelete">×</button>
        </div>
        <div class="modal-body">
          <p class="delete-message">
            Are you sure you want to delete this task?
          </p>
          <p class="delete-task-name">"{{ taskToDelete?.title }}"</p>
        </div>
        <div class="modal-footer">
          <button class="cancel-btn" @click="cancelDelete">Cancel</button>
          <button class="delete-confirm-btn" @click="confirmDelete">Delete</button>
        </div>
      </div>
    </div>

    <!-- Modal Overlay -->
    <div v-if="showAddTask" class="modal-overlay" @click="closeModal">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h2>Create New Task</h2>
          <button class="close-btn" @click="closeModal">×</button>
        </div>
        <div class="modal-body">
          <div class="form-group">
            <label for="task-title">Task Title</label>
            <input 
              id="task-title"
              type="text" 
              placeholder="Add a new task..." 
              v-model="newTaskTitle"
              @keyup.enter="addTask"
              class="modal-input"
              autofocus
            />
          </div>
          <div class="form-group">
            <label for="task-project">Project</label>
            <select id="task-project" v-model="newTaskProject" class="modal-select">
              <option value="No Project">No Project</option>
              <option 
                v-for="project in projects" 
                :key="project.name"
                :value="project.name"
              >
                {{ project.name }}
              </option>
            </select>
          </div>
          <div class="form-group">
            <label for="task-due-date">Due Date</label>
            <input 
              id="task-due-date"
              type="date" 
              v-model="newTaskDueDate"
              class="modal-input"
            />
          </div>
          <div class="form-group">
            <label for="task-priority">Priority</label>
            <select 
              id="task-priority" 
              v-model="newTaskPriority" 
              class="modal-select priority-select"
              :data-selected="newTaskPriority"
            >
              <option value="">No Priority</option>
              <option value="High Priority" data-color="red">High Priority</option>
              <option value="Medium Priority" data-color="yellow">Medium Priority</option>
              <option value="Low Priority" data-color="light-green">Low Priority</option>
            </select>
          </div>
        </div>
        <div class="modal-footer">
          <button class="cancel-btn" @click="closeModal">Cancel</button>
          <button class="submit-btn" @click="addTask">Create Task</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'TaskList',
  props: {
    tasks: {
      type: Array,
      required: true
    },
    selectedFilter: {
      type: String,
      default: 'all'
    },
    isDarkMode: {
      type: Boolean,
      default: false
    },
    projects: {
      type: Array,
      required: true
    }
  },
  data() {
    return {
      searchQuery: '',
      activeViewFilter: 'All',
      viewFilters: ['All', 'Active', 'Completed'],
      showAddTask: false,
      newTaskTitle: '',
      newTaskProject: 'No Project',
      newTaskDueDate: '',
      newTaskPriority: '',
      showDeleteModal: false,
      taskToDelete: null,
      showSuccessNotification: false,
      showEditModal: false,
      taskToEdit: null,
      editTaskTitle: '',
      editTaskProject: 'No Project',
      editTaskDueDate: ''
    }
  },
  computed: {
    availableViewFilters() {
      // Hide "Active" and "Completed" buttons when "completed" filter is selected from sidebar
      if (this.selectedFilter === 'completed') {
        return ['All']
      }
      return this.viewFilters
    },
    filteredTasks() {
      let filtered = this.tasks

      if (this.searchQuery) {
        filtered = filtered.filter(t => 
          t.title.toLowerCase().includes(this.searchQuery.toLowerCase())
        )
      }

      if (this.activeViewFilter === 'Active') {
        filtered = filtered.filter(t => !t.completed)
      } else if (this.activeViewFilter === 'Completed') {
        filtered = filtered.filter(t => t.completed)
      }

      return filtered
    },
    displayedTasks() {
      return this.filteredTasks
    }
  },
  watch: {
    selectedFilter(newFilter) {
      // Reset to 'All' when switching to 'completed' filter
      if (newFilter === 'completed') {
        this.activeViewFilter = 'All'
      }
    }
  },
  mounted() {
    document.addEventListener('keydown', this.handleEscapeKey)
  },
  beforeUnmount() {
    document.removeEventListener('keydown', this.handleEscapeKey)
  },
  methods: {
    getHeaderTitle() {
      const titles = {
        all: 'All Tasks',
        today: 'Today',
        week: 'This Week',
        important: 'Important',
        completed: 'Completed'
      }
      if (this.selectedFilter.startsWith('project:')) {
        const projectName = this.selectedFilter.replace('project:', '')
        return projectName
      }
      return titles[this.selectedFilter] || 'All Tasks'
    },
    getProjectStyle(projectName) {
      // Handle null or undefined project names - treat as "No Project"
      const actualProjectName = projectName || 'No Project'
      const project = this.projects.find(p => p.name === actualProjectName)
      if (project) {
        // Convert hex to RGB for background with opacity
        let hex = project.color.replace('#', '')
        // Handle 3-character hex codes
        if (hex.length === 3) {
          hex = hex.split('').map(char => char + char).join('')
        }
        const r = parseInt(hex.substring(0, 2), 16)
        const g = parseInt(hex.substring(2, 4), 16)
        const b = parseInt(hex.substring(4, 6), 16)
        // Use a lighter version for background
        return {
          backgroundColor: `rgba(${r}, ${g}, ${b}, 0.15)`,
          color: project.color
        }
      }
      // Fallback for projects not found
      return {
        backgroundColor: '#f3f4f6',
        color: '#374151'
      }
    },
    getPriorityClass(priorityColor) {
      const classes = {
        'red': 'priority-high',
        'yellow': 'priority-medium',
        'light-green': 'priority-low'
      }
      return classes[priorityColor] || ''
    },
    addTask() {
      if (this.newTaskTitle.trim()) {
        // Format due date
        let formattedDueDate = 'no date'
        if (this.newTaskDueDate) {
          const selectedDate = new Date(this.newTaskDueDate)
          const today = new Date()
          const tomorrow = new Date(today)
          tomorrow.setDate(tomorrow.getDate() + 1)
          
          const diffTime = selectedDate - today
          const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24))
          
          if (diffDays === 0) {
            formattedDueDate = 'today'
          } else if (diffDays === 1) {
            formattedDueDate = 'tomorrow'
          } else if (diffDays >= 2 && diffDays <= 7) {
            formattedDueDate = 'this week'
          } else if (diffDays > 7) {
            formattedDueDate = 'next week'
          } else {
            formattedDueDate = this.newTaskDueDate
          }
        }
        
        // Map priority to priorityColor
        const priorityColorMap = {
          'High Priority': 'red',
          'Medium Priority': 'yellow',
          'Low Priority': 'light-green'
        }
        
        this.$emit('add-task', {
          title: this.newTaskTitle,
          project: this.newTaskProject === 'No Project' ? 'No Project' : this.newTaskProject,
          dueDate: formattedDueDate,
          priority: this.newTaskPriority || null,
          priorityColor: priorityColorMap[this.newTaskPriority] || null
        })
        this.closeModal()
      }
    },
    closeModal() {
      this.showAddTask = false
      this.newTaskTitle = ''
      this.newTaskProject = 'No Project'
      this.newTaskDueDate = ''
      this.newTaskPriority = ''
    },
    handleEscapeKey(event) {
      if (event.key === 'Escape' && this.showAddTask) {
        this.closeModal()
      } else if (event.key === 'Escape' && this.showDeleteModal) {
        this.cancelDelete()
      } else if (event.key === 'Escape' && this.showEditModal) {
        this.closeEditModal()
      }
    },
    showDeleteConfirm(task) {
      this.taskToDelete = task
      this.showDeleteModal = true
    },
    cancelDelete() {
      this.showDeleteModal = false
      this.taskToDelete = null
    },
    confirmDelete() {
      if (this.taskToDelete) {
        this.$emit('delete-task', this.taskToDelete.id)
        this.cancelDelete()
        this.showSuccessMessage()
      }
    },
    showSuccessMessage() {
      this.showSuccessNotification = true
      setTimeout(() => {
        this.showSuccessNotification = false
      }, 3000) // Hide after 3 seconds
    },
    showEditTask(task) {
      this.taskToEdit = task
      this.editTaskTitle = task.title
      this.editTaskProject = task.project || 'No Project'
      // Convert due date format to date input format
      if (task.dueDate && task.dueDate !== 'no date' && task.dueDate !== 'today' && task.dueDate !== 'tomorrow' && task.dueDate !== 'this week' && task.dueDate !== 'next week') {
        // If it's already a date string, try to parse it
        const dateMatch = task.dueDate.match(/\d{4}-\d{2}-\d{2}/)
        if (dateMatch) {
          this.editTaskDueDate = dateMatch[0]
        } else {
          this.editTaskDueDate = ''
        }
      } else {
        this.editTaskDueDate = ''
      }
      this.showEditModal = true
    },
    closeEditModal() {
      this.showEditModal = false
      this.taskToEdit = null
      this.editTaskTitle = ''
      this.editTaskProject = 'No Project'
      this.editTaskDueDate = ''
    },
    saveTask() {
      if (this.taskToEdit && this.editTaskTitle.trim()) {
        // Format due date
        let formattedDueDate = 'no date'
        if (this.editTaskDueDate) {
          const selectedDate = new Date(this.editTaskDueDate)
          const today = new Date()
          const tomorrow = new Date(today)
          tomorrow.setDate(tomorrow.getDate() + 1)
          
          const diffTime = selectedDate - today
          const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24))
          
          if (diffDays === 0) {
            formattedDueDate = 'today'
          } else if (diffDays === 1) {
            formattedDueDate = 'tomorrow'
          } else if (diffDays >= 2 && diffDays <= 7) {
            formattedDueDate = 'this week'
          } else if (diffDays > 7) {
            formattedDueDate = 'next week'
          } else {
            formattedDueDate = this.editTaskDueDate
          }
        }
        
        this.$emit('update-task', {
          id: this.taskToEdit.id,
          title: this.editTaskTitle.trim(),
          project: this.editTaskProject === 'No Project' ? 'No Project' : this.editTaskProject,
          dueDate: formattedDueDate
        })
        this.closeEditModal()
      }
    }
  }
}
</script>

<style scoped>
.task-list-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  background: white;
  height: 100vh;
  overflow-y: auto;
  transition: background-color 0.3s ease, color 0.3s ease;
}

.task-list-container.dark-mode {
  background: #181818;
  color: #f1f5f9;
}

.header {
  padding: 32px 40px 24px;
  border-bottom: 1px solid #e5e7eb;
  transition: border-color 0.3s ease;
}

.task-list-container.dark-mode .header {
  border-bottom-color: #282828;
}

.header-top {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}

.header-text h1 {
  font-size: 28px;
  font-weight: 700;
  color: #111827;
  margin: 0 0 4px 0;
  transition: color 0.3s ease;
}

.task-list-container.dark-mode .header-text h1 {
  color: #f1f5f9;
}

.subtitle {
  font-size: 14px;
  color: #6b7280;
  margin: 0;
  transition: color 0.3s ease;
}

.task-list-container.dark-mode .subtitle {
  color: #94a3b8;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 16px;
}

.search-bar {
  position: relative;
  display: flex;
  align-items: center;
}

.search-icon {
  position: absolute;
  left: 12px;
  color: #9ca3af;
  font-size: 16px;
  transition: color 0.3s ease;
}

.task-list-container.dark-mode .search-icon {
  color: #64748b;
}

.search-bar input {
  padding: 10px 12px 10px 36px;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  font-size: 14px;
  width: 240px;
  background: #f9fafb;
  color: #111827;
  transition: all 0.3s ease;
}

.task-list-container.dark-mode .search-bar input {
  background: #282828;
  border-color: #3a3a3a;
  color: #f1f5f9;
}

.task-list-container.dark-mode .search-bar input::placeholder {
  color: #94a3b8;
}

.search-bar input:focus {
  outline: none;
  border-color: #9333ea;
  background: white;
}

.task-list-container.dark-mode .search-bar input:focus {
  background: #2f2f2f;
  border-color: #a855f7;
}

.dark-mode-btn {
  width: 40px;
  height: 40px;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  background: white;
  font-size: 18px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s;
  padding: 0;
}

.dark-mode-btn:hover {
  background: #f9fafb;
  border-color: #d1d5db;
  transform: scale(1.05);
}

.task-list-container.dark-mode .dark-mode-btn {
  background: #282828;
  border-color: #3a3a3a;
  color: #fbbf24;
}

.task-list-container.dark-mode .dark-mode-btn:hover {
  background: #2f2f2f;
  border-color: #404040;
}

.filter-buttons {
  display: flex;
  gap: 8px;
}

.filter-btn {
  padding: 8px 16px;
  border: 1px solid #e5e7eb;
  border-radius: 6px;
  background: white;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s;
  color: #111827;
}

.filter-btn:hover {
  background: #f9fafb;
}

.filter-btn.active {
  background: #9571A6;
  color: white;
  border-color: #9571A6;
  font-weight: bold;
}

.task-list-container.dark-mode .filter-btn {
  background: #282828;
  border-color: #3a3a3a;
  color: #f1f5f9;
}

.task-list-container.dark-mode .filter-btn:hover {
  background: #2f2f2f;
}

.task-list-container.dark-mode .filter-btn.active {
  background: #9571A6;
  color: white;
  border-color: #9571A6;
  font-weight: bold;
}

.new-task-btn {
  padding: 10px 20px;
  background: #9333ea;
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: background 0.2s;
}

.new-task-btn:hover {
  background: #7c3aed;
}

/* Modal Styles */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  backdrop-filter: blur(8px);
  -webkit-backdrop-filter: blur(8px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  animation: fadeIn 0.2s ease-out;
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

.modal-content {
  background: white;
  border-radius: 16px;
  width: 90%;
  max-width: 500px;
  box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1), 0 10px 10px -5px rgba(0, 0, 0, 0.04);
  animation: slideUp 0.3s ease-out;
  overflow: hidden;
  transition: background-color 0.3s ease;
}

.task-list-container.dark-mode .modal-content {
  background: #282828;
  box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.3), 0 10px 10px -5px rgba(0, 0, 0, 0.2);
}

@keyframes slideUp {
  from {
    transform: translateY(20px);
    opacity: 0;
  }
  to {
    transform: translateY(0);
    opacity: 1;
  }
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24px 24px 20px;
  border-bottom: 1px solid #e5e7eb;
  transition: border-color 0.3s ease;
}

.task-list-container.dark-mode .modal-header {
  border-bottom-color: #3a3a3a;
}

.modal-header h2 {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
  color: #111827;
  transition: color 0.3s ease;
}

.task-list-container.dark-mode .modal-header h2 {
  color: #f1f5f9;
}

.close-btn {
  background: none;
  border: none;
  font-size: 28px;
  color: #6b7280;
  cursor: pointer;
  padding: 0;
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 6px;
  transition: all 0.2s;
  line-height: 1;
}

.close-btn:hover {
  background: #f3f4f6;
  color: #111827;
}

.task-list-container.dark-mode .close-btn {
  color: #94a3b8;
}

.task-list-container.dark-mode .close-btn:hover {
  background: #2f2f2f;
  color: #f1f5f9;
}

.modal-body {
  padding: 24px;
}

.form-group {
  margin-bottom: 20px;
}

.form-group:last-child {
  margin-bottom: 0;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  font-size: 14px;
  font-weight: 500;
  color: #374151;
  transition: color 0.3s ease;
}

.task-list-container.dark-mode .form-group label {
  color: #f1f5f9;
}

.modal-input,
.modal-select {
  width: 100%;
  padding: 12px 16px;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  font-size: 14px;
  background: white;
  transition: all 0.2s;
  box-sizing: border-box;
  color: #111827;
}

.task-list-container.dark-mode .modal-input,
.task-list-container.dark-mode .modal-select {
  background: #2f2f2f;
  border-color: #3a3a3a;
  color: #f1f5f9;
}

.modal-input:focus,
.modal-select:focus {
  outline: none;
  border-color: #9333ea;
  box-shadow: 0 0 0 3px rgba(147, 51, 234, 0.1);
}

.task-list-container.dark-mode .modal-input:focus,
.task-list-container.dark-mode .modal-select:focus {
  border-color: #a855f7;
  background: #353535;
  box-shadow: 0 0 0 3px rgba(168, 85, 247, 0.2);
}

.modal-select {
  cursor: pointer;
}

.priority-select {
  position: relative;
}

.priority-select option {
  padding: 8px;
}

/* Priority color indicators - using option styling where supported */
.priority-select option[value="High Priority"] {
  color: #991b1b;
}

.priority-select option[value="Medium Priority"] {
  color: #92400e;
}

.priority-select option[value="Low Priority"] {
  color: #065f46;
}

/* Visual indicator for selected priority */
.priority-select[data-selected="High Priority"] {
  border-left: 4px solid #dc2626;
}

.priority-select[data-selected="Medium Priority"] {
  border-left: 4px solid #f59e0b;
}

.priority-select[data-selected="Low Priority"] {
  border-left: 4px solid #10b981;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding: 20px 24px;
  border-top: 1px solid #e5e7eb;
  background: #f9fafb;
  transition: all 0.3s ease;
}

.task-list-container.dark-mode .modal-footer {
  background: #2f2f2f;
  border-top-color: #3a3a3a;
}

.cancel-btn {
  padding: 10px 20px;
  background: white;
  color: #374151;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}

.cancel-btn:hover {
  background: #f9fafb;
  border-color: #d1d5db;
}

.task-list-container.dark-mode .cancel-btn {
  background: #353535;
  color: #f1f5f9;
  border-color: #404040;
}

.task-list-container.dark-mode .cancel-btn:hover {
  background: #404040;
  border-color: #4a4a4a;
}

.submit-btn {
  padding: 10px 20px;
  background: #9333ea;
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: background 0.2s;
}

.submit-btn:hover {
  background: #7c3aed;
}

.delete-message {
  font-size: 16px;
  color: #374151;
  margin: 0 0 12px 0;
  text-align: center;
}

.task-list-container.dark-mode .delete-message {
  color: #f1f5f9;
}

.delete-task-name {
  font-size: 18px;
  font-weight: 600;
  color: #111827;
  margin: 0;
  text-align: center;
  padding: 12px;
  background: #f9fafb;
  border-radius: 8px;
  word-break: break-word;
}

.task-list-container.dark-mode .delete-task-name {
  color: #f1f5f9;
  background: #2f2f2f;
}

.delete-confirm-btn {
  padding: 10px 20px;
  background: #dc2626;
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: background 0.2s;
}

.delete-confirm-btn:hover {
  background: #b91c1c;
}

.tasks-container {
  flex: 1;
  padding: 24px 40px;
}

.task-item {
  display: flex;
  gap: 16px;
  padding: 16px;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  margin-bottom: 12px;
  background: white;
  transition: all 0.2s;
}

.task-list-container.dark-mode .task-item {
  background: #282828;
  border-color: #3a3a3a;
}

.task-item:hover {
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.task-list-container.dark-mode .task-item:hover {
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.3);
  border-color: #404040;
}

.task-item.completed {
  opacity: 0.7;
}

.task-checkbox {
  width: 24px;
  height: 24px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
}

.checkbox-unchecked {
  width: 20px;
  height: 20px;
  border: 2px solid #d1d5db;
  border-radius: 4px;
}

.checkbox-checked {
  width: 20px;
  height: 20px;
  background: #10b981;
  border-radius: 4px;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  font-weight: bold;
}

.task-content {
  flex: 1;
}

.task-actions {
  display: flex;
  align-items: center;
  gap: 4px;
  align-self: flex-start;
  margin-top: 2px;
}

.edit-btn {
  background: none;
  border: none;
  cursor: pointer;
  padding: 4px 8px;
  border-radius: 6px;
  transition: all 0.2s;
  opacity: 0.6;
  display: flex;
  align-items: center;
  justify-content: center;
}

.edit-icon {
  width: 18px;
  height: 18px;
  object-fit: contain;
}

.edit-btn:hover {
  opacity: 1;
  background: #dbeafe;
  transform: scale(1.1);
}

.task-list-container.dark-mode .edit-btn:hover {
  background: #2a3a4a;
}

.delete-btn {
  background: none;
  border: none;
  cursor: pointer;
  padding: 4px 8px;
  border-radius: 6px;
  transition: all 0.2s;
  opacity: 0.6;
  display: flex;
  align-items: center;
  justify-content: center;
}

.delete-icon {
  width: 18px;
  height: 18px;
  object-fit: contain;
}

.delete-btn:hover {
  opacity: 1;
  background: #fee2e2;
  transform: scale(1.1);
}

.task-list-container.dark-mode .delete-btn:hover {
  background: #3a2a2a;
}

.task-title {
  font-size: 15px;
  font-weight: 500;
  color: #111827;
  margin-bottom: 8px;
  transition: color 0.3s ease;
}

.task-list-container.dark-mode .task-title {
  color: #f1f5f9;
}

.task-item.completed .task-title {
  text-decoration: line-through;
  color: #9ca3af;
}

.task-list-container.dark-mode .task-item.completed .task-title {
  color: #64748b;
}

.task-meta {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}

.task-due {
  font-size: 13px;
  color: #6b7280;
  transition: color 0.3s ease;
}

.task-list-container.dark-mode .task-due {
  color: #94a3b8;
}

.task-tags {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.task-tag {
  padding: 4px 10px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
}

/* Project colors are now dynamic based on project.color */

.priority-high {
  background: #fee2e2;
  color: #991b1b;
}

.priority-medium {
  background: #fef3c7;
  color: #92400e;
}

.priority-low {
  background: #d1fae5;
  color: #065f46;
}

.completed-tag {
  background: #d1fae5;
  color: #065f46;
}

.pagination {
  padding: 24px 40px;
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 16px;
  border-top: 1px solid #e5e7eb;
  transition: border-color 0.3s ease;
}

.task-list-container.dark-mode .pagination {
  border-top-color: #282828;
}

.page-btn {
  width: 36px;
  height: 36px;
  border: 1px solid #e5e7eb;
  border-radius: 6px;
  background: white;
  cursor: pointer;
  font-size: 18px;
  color: #6b7280;
  transition: all 0.2s;
}

.page-btn:hover {
  background: #f9fafb;
  border-color: #d1d5db;
}

.task-list-container.dark-mode .page-btn {
  background: #282828;
  border-color: #3a3a3a;
  color: #94a3b8;
}

.task-list-container.dark-mode .page-btn:hover {
  background: #2f2f2f;
  border-color: #404040;
}

.page-info {
  font-size: 14px;
  color: #6b7280;
  transition: color 0.3s ease;
}

.task-list-container.dark-mode .page-info {
  color: #94a3b8;
}

.success-notification {
  position: fixed;
  bottom: 24px;
  right: 24px;
  background: #10b981;
  color: white;
  padding: 16px 20px;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  z-index: 2000;
  animation: slideInRight 0.3s ease-out;
  min-width: 200px;
}

@keyframes slideInRight {
  from {
    transform: translateX(100%);
    opacity: 0;
  }
  to {
    transform: translateX(0);
    opacity: 1;
  }
}

.success-content {
  display: flex;
  align-items: center;
  gap: 12px;
}

.success-icon {
  width: 24px;
  height: 24px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
  font-weight: bold;
  flex-shrink: 0;
}

.success-text {
  font-size: 14px;
  font-weight: 500;
}
</style>

