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
              v-for="filter in viewFilters" 
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
                v-if="task.project"
                class="task-tag"
                :class="getProjectClass(task.project)"
              >
                {{ task.project }}
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
      </div>
    </div>

    <div class="pagination">
      <button class="page-btn">‹</button>
      <span class="page-info">1/1</span>
      <button class="page-btn">›</button>
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
              <option value="Work Projects">Work Projects</option>
              <option value="Personal">Personal</option>
              <option value="Learning">Learning</option>
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
      newTaskPriority: ''
    }
  },
  computed: {
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
    getProjectClass(project) {
      const classes = {
        'Work Projects': 'project-work',
        'Personal': 'project-personal',
        'Learning': 'project-learning'
      }
      return classes[project] || ''
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
          project: this.newTaskProject === 'No Project' ? null : this.newTaskProject,
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
  background: #919191;
  color: white;
  border-color: #919191;
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
  background: #919191;
  color: white;
  border-color: #919191;
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

.project-work {
  background: #dbeafe;
  color: #1e40af;
}

.project-personal {
  background: #d1fae5;
  color: #065f46;
}

.project-learning {
  background: #e9d5ff;
  color: #6b21a8;
}

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
</style>

