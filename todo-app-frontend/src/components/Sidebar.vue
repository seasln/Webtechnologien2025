<template>
  <div class="sidebar" :class="{ 'dark-mode': isDarkMode }">
    <div class="sidebar-header">
      <div class="logo">
        <img src="/icons/favicon.svg" alt="TaskFlow Logo" class="logo-icon" />
        <div class="logo-text">
          <h1>TaskFlow</h1>
          <p class="slogan">Stay organized</p>
        </div>
      </div>
    </div>

    <nav class="sidebar-nav">
      <div 
        v-for="filter in filters" 
        :key="filter.id"
        class="nav-item"
        :class="{ active: selectedFilter === filter.id }"
        @click="$emit('filter-change', filter.id)"
      >
        <span class="nav-icon">{{ filter.icon }}</span>
        <span class="nav-label">{{ filter.label }}</span>
        <span class="nav-badge">{{ getFilterCount(filter.id) }}</span>
      </div>
    </nav>

    <div class="projects-section">
      <div class="section-header">
        <h3 class="section-title">PROJECTS</h3>
        <button class="add-project-btn" @click="showAddProjectModal = true" title="Add new project">
          <img src="/icons/plus-line-icon.png" alt="Add project" class="add-project-icon" />
        </button>
      </div>
      <div 
        v-for="project in projects" 
        :key="project.name"
        class="project-item"
        :class="{ 
          active: selectedFilter === `project:${project.name}`,
          'is-default': project.isDefault
        }"
      >
        <div class="project-item-content" @click="$emit('filter-change', `project:${project.name}`)">
          <span class="project-dot" :style="{ backgroundColor: project.color }"></span>
          <span class="project-name">{{ project.name }}</span>
          <span v-if="project.isDefault" class="default-indicator" title="Default project (cannot be deleted)">•</span>
          <span class="project-count">{{ getProjectCount(project.name) }}</span>
        </div>
        <button 
          v-if="!project.isDefault"
          class="delete-project-btn" 
          @click.stop="showDeleteProjectConfirm(project)"
          title="Delete project"
        >
          <img src="/icons/minus-line-icon.png" alt="Delete" class="delete-project-icon" />
        </button>
      </div>
    </div>

    <!-- Delete Project Confirmation Modal -->
    <div v-if="showDeleteProjectModal" class="modal-overlay" @click="cancelDeleteProject">
      <div class="modal-content delete-project-modal" @click.stop>
        <div class="modal-header">
          <h2>Delete Project</h2>
          <button class="close-btn" @click="cancelDeleteProject">×</button>
        </div>
        <div class="modal-body">
          <p class="delete-message">
            Are you sure you want to delete this project?
          </p>
          <div class="delete-project-info">
            <span class="project-dot" :style="{ backgroundColor: projectToDelete?.color }"></span>
            <p class="delete-project-name">"{{ projectToDelete?.name }}"</p>
          </div>
          <p class="delete-warning" v-if="getProjectCount(projectToDelete?.name) > 0">
            This project has {{ getProjectCount(projectToDelete?.name) }} task(s). They will be moved to "No Project".
          </p>
        </div>
        <div class="modal-footer">
          <button class="cancel-btn" @click="cancelDeleteProject">Cancel</button>
          <button class="delete-confirm-btn" @click="confirmDeleteProject">Delete</button>
        </div>
      </div>
    </div>

    <!-- Add Project Modal -->
    <div v-if="showAddProjectModal" class="modal-overlay" @click="closeAddProjectModal">
      <div class="modal-content project-modal" @click.stop>
        <div class="modal-header">
          <h2>Create New Project</h2>
          <button class="close-btn" @click="closeAddProjectModal">×</button>
        </div>
        <div class="modal-body">
          <div class="form-group">
            <label for="project-name">Project Name</label>
            <input 
              id="project-name"
              type="text" 
              placeholder="Enter project name..." 
              v-model="newProjectName"
              @keyup.enter="createProject"
              class="modal-input"
              autofocus
            />
          </div>
          <div class="form-group">
            <label for="project-color">Project Color</label>
            <div class="color-picker-container">
              <input 
                id="project-color"
                type="color" 
                v-model="newProjectColor"
                class="color-picker"
              />
              <div class="color-preview" :style="{ backgroundColor: newProjectColor }"></div>
              <span class="color-value">{{ newProjectColor }}</span>
            </div>
          </div>
        </div>
        <div class="modal-footer">
          <button class="cancel-btn" @click="closeAddProjectModal">Cancel</button>
          <button class="submit-btn" @click="createProject">Create Project</button>
        </div>
      </div>
    </div>

    <div class="user-profile">
      <div class="profile-avatar">
        <img src="https://img.freepik.com/free-psd/3d-illustration-person-with-sunglasses_23-2149436188.jpg" alt="Sefa" />
      </div>
      <div class="profile-info">
        <div class="profile-name">Sefa</div>
        <div class="profile-plan">Free Plan</div>
      </div>
      <div class="profile-settings">⚙</div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Sidebar',
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
      filters: [
        { id: 'all', label: 'All Tasks', icon: '☰' },
        { id: 'today', label: 'Today', icon: '🕐' },
        { id: 'week', label: 'This Week', icon: '📅' },
        { id: 'important', label: 'Important', icon: '⭐' },
        { id: 'completed', label: 'Completed', icon: '✓' }
      ],
      showAddProjectModal: false,
      newProjectName: '',
      newProjectColor: '#3b82f6',
      showDeleteProjectModal: false,
      projectToDelete: null
    }
  },
  methods: {
    getFilterCount(filterId) {
      if (filterId === 'all') {
        return this.tasks.filter(t => !t.completed).length
      } else if (filterId === 'completed') {
        return this.tasks.filter(t => t.completed).length
      } else if (filterId === 'important') {
        return this.tasks.filter(t => t.priority === 'High Priority').length
      } else if (filterId === 'today') {
        return this.tasks.filter(t => t.dueDate === 'today').length
      } else if (filterId === 'week') {
        return this.tasks.filter(t => t.dueDate === 'this week' || t.dueDate === 'next week').length
      }
      return 0
    },
    getProjectCount(projectName) {
      // For "No Project", count tasks with null or 'No Project' as project
      if (projectName === 'No Project') {
        return this.tasks.filter(t => !t.project || t.project === 'No Project').length
      }
      return this.tasks.filter(t => t.project === projectName).length
    },
    createProject() {
      if (this.newProjectName.trim()) {
        // Check if project name already exists
        if (this.projects.find(p => p.name === this.newProjectName.trim())) {
          alert('A project with this name already exists!')
          return
        }
        this.$emit('add-project', {
          name: this.newProjectName.trim(),
          color: this.newProjectColor
        })
        this.closeAddProjectModal()
      }
    },
    closeAddProjectModal() {
      this.showAddProjectModal = false
      this.newProjectName = ''
      this.newProjectColor = '#3b82f6'
    },
    handleEscapeKey(event) {
      if (event.key === 'Escape' && this.showAddProjectModal) {
        this.closeAddProjectModal()
      } else if (event.key === 'Escape' && this.showDeleteProjectModal) {
        this.cancelDeleteProject()
      }
    },
    showDeleteProjectConfirm(project) {
      this.projectToDelete = project
      this.showDeleteProjectModal = true
    },
    cancelDeleteProject() {
      this.showDeleteProjectModal = false
      this.projectToDelete = null
    },
    confirmDeleteProject() {
      if (this.projectToDelete) {
        this.$emit('delete-project', this.projectToDelete.name)
        this.cancelDeleteProject()
      }
    }
  },
  mounted() {
    document.addEventListener('keydown', this.handleEscapeKey)
  },
  beforeUnmount() {
    document.removeEventListener('keydown', this.handleEscapeKey)
  }
}
</script>

<style scoped>
.sidebar {
  width: 280px;
  background: white;
  border-right: 1px solid #e5e7eb;
  display: flex;
  flex-direction: column;
  height: 100vh;
  overflow-y: auto;
  transition: background-color 0.3s ease, border-color 0.3s ease;
}

.sidebar.dark-mode {
  background: #181818;
  border-right-color: #282828;
}

.sidebar-header {
  padding: 24px 20px;
  border-bottom: 1px solid #e5e7eb;
  transition: border-color 0.3s ease;
}

.sidebar.dark-mode .sidebar-header {
  border-bottom-color: #282828;
}

.logo {
  display: flex;
  align-items: center;
  gap: 12px;
}

.logo-icon {
  width: 40px;
  height: 40px;
  object-fit: contain;
  flex-shrink: 0;
}

.logo-text h1 {
  font-size: 20px;
  font-weight: 700;
  color: #111827;
  margin: 0;
  transition: color 0.3s ease;
}

.sidebar.dark-mode .logo-text h1 {
  color: #f1f5f9;
}

.slogan {
  font-size: 12px;
  color: #6b7280;
  margin: 2px 0 0 0;
  transition: color 0.3s ease;
}

.sidebar.dark-mode .slogan {
  color: #94a3b8;
}

.sidebar-nav {
  padding: 16px 12px;
  flex: 1;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 10px 12px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;
  margin-bottom: 4px;
  position: relative;
  color: #374151;
}

.nav-item:hover {
  background: #f9fafb;
}

.nav-item.active {
  background: #f3e8ff;
  color: #9571A6;
  font-weight: bold;
}

.nav-item.active .nav-label {
  font-weight: bold;
}

.sidebar.dark-mode .nav-item {
  color: #f1f5f9;
}

.sidebar.dark-mode .nav-item:hover {
  background: #282828;
}

.sidebar.dark-mode .nav-item.active {
  background: #2f2f2f;
  color: #9571A6;
  font-weight: bold;
}

.sidebar.dark-mode .nav-item.active .nav-label {
  font-weight: bold;
}

.nav-icon {
  font-size: 18px;
  width: 20px;
  text-align: center;
}

.nav-label {
  flex: 1;
  font-size: 14px;
  font-weight: 500;
}

.nav-badge {
  background: #9333ea;
  color: white;
  font-size: 11px;
  font-weight: 600;
  padding: 2px 8px;
  border-radius: 12px;
  min-width: 24px;
  text-align: center;
}

.projects-section {
  padding: 16px 12px;
  border-top: 1px solid #e5e7eb;
  border-bottom: 1px solid #e5e7eb;
  transition: border-color 0.3s ease;
}

.sidebar.dark-mode .projects-section {
  border-top-color: #282828;
  border-bottom-color: #282828;
}

.section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin: 0 12px 12px 12px;
  padding-right: 0;
}

.section-title {
  font-size: 11px;
  font-weight: 700;
  color: #9ca3af;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  margin: 0;
  transition: color 0.3s ease;
}

.add-project-btn {
  background: none;
  border: none;
  cursor: pointer;
  padding: 4px;
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s;
  opacity: 0.6;
  width: 24px;
  height: 24px;
}

.add-project-btn:hover {
  opacity: 1;
  background: #f3f4f6;
}

.sidebar.dark-mode .add-project-btn:hover {
  background: #334155;
}

.add-project-icon {
  width: 16px;
  height: 16px;
  object-fit: contain;
}

.sidebar.dark-mode .section-title {
  color: #64748b;
}

.project-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 12px;
  border-radius: 6px;
  transition: background 0.2s;
  position: relative;
}

.project-item.is-default {
  opacity: 0.85;
}

.project-item-content {
  display: flex;
  align-items: center;
  gap: 10px;
  flex: 1;
  cursor: pointer;
  padding-right: 28px;
  min-width: 0;
}

.project-item:hover {
  background: #f9fafb;
}

.project-item.active {
  background: #f3e8ff;
}

.project-item.active .project-name {
  color: #9571A6;
  font-weight: bold;
}

.sidebar.dark-mode .project-item:hover {
  background: #282828;
}

.sidebar.dark-mode .project-item.active {
  background: #2f2f2f;
}

.sidebar.dark-mode .project-item.active .project-name {
  color: #9571A6;
  font-weight: bold;
}

.project-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
}

.project-name {
  flex: 1;
  font-size: 14px;
  color: #374151;
  transition: color 0.3s ease;
  min-width: 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.sidebar.dark-mode .project-name {
  color: #f1f5f9;
}

.default-indicator {
  font-size: 16px;
  color: #9ca3af;
  margin: 0 2px;
  flex-shrink: 0;
  line-height: 1;
}

.sidebar.dark-mode .default-indicator {
  color: #6b7280;
}

.project-count {
  font-size: 12px;
  color: #6b7280;
  transition: color 0.3s ease;
}

.sidebar.dark-mode .project-count {
  color: #94a3b8;
}

.user-profile {
  padding: 16px 20px;
  display: flex;
  align-items: center;
  gap: 12px;
  border-top: 1px solid #e5e7eb;
  transition: border-color 0.3s ease;
}

.sidebar.dark-mode .user-profile {
  border-top-color: #282828;
}

.profile-avatar img {
  width: 40px;
  height: 40px;
  border-radius: 50%;
}

.profile-info {
  flex: 1;
}

.profile-name {
  font-size: 14px;
  font-weight: 600;
  color: #111827;
  transition: color 0.3s ease;
}

.sidebar.dark-mode .profile-name {
  color: #f1f5f9;
}

.profile-plan {
  font-size: 12px;
  color: #6b7280;
  transition: color 0.3s ease;
}

.sidebar.dark-mode .profile-plan {
  color: #94a3b8;
}

.profile-settings {
  font-size: 18px;
  color: #6b7280;
  cursor: pointer;
  padding: 4px;
  border-radius: 4px;
  transition: all 0.2s;
}

.profile-settings:hover {
  background: #f3f4f6;
}

.sidebar.dark-mode .profile-settings {
  color: #94a3b8;
}

.sidebar.dark-mode .profile-settings:hover {
  background: #282828;
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

.project-modal,
.delete-project-modal {
  background: white;
  border-radius: 16px;
  width: 90%;
  max-width: 400px;
  box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1), 0 10px 10px -5px rgba(0, 0, 0, 0.04);
  animation: slideUp 0.3s ease-out;
  overflow: hidden;
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

.sidebar.dark-mode .project-modal,
.sidebar.dark-mode .delete-project-modal {
  background: #282828;
  box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.3), 0 10px 10px -5px rgba(0, 0, 0, 0.2);
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24px 24px 20px;
  border-bottom: 1px solid #e5e7eb;
  transition: border-color 0.3s ease;
}

.sidebar.dark-mode .modal-header {
  border-bottom-color: #3a3a3a;
}

.modal-header h2 {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
  color: #111827;
  transition: color 0.3s ease;
}

.sidebar.dark-mode .modal-header h2 {
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

.sidebar.dark-mode .close-btn {
  color: #94a3b8;
}

.sidebar.dark-mode .close-btn:hover {
  background: #475569;
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

.sidebar.dark-mode .form-group label {
  color: #f1f5f9;
}

.modal-input {
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

.sidebar.dark-mode .modal-input {
  background: #2f2f2f;
  border-color: #3a3a3a;
  color: #f1f5f9;
}

.modal-input:focus {
  outline: none;
  border-color: #9333ea;
  box-shadow: 0 0 0 3px rgba(147, 51, 234, 0.1);
}

.sidebar.dark-mode .modal-input:focus {
  border-color: #a855f7;
  background: #353535;
  box-shadow: 0 0 0 3px rgba(168, 85, 247, 0.2);
}

.color-picker-container {
  display: flex;
  align-items: center;
  gap: 12px;
}

.color-picker {
  width: 60px;
  height: 40px;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  cursor: pointer;
  background: none;
  padding: 0;
}

.sidebar.dark-mode .color-picker {
  border-color: #3a3a3a;
}

.color-preview {
  width: 40px;
  height: 40px;
  border-radius: 8px;
  border: 1px solid #e5e7eb;
  flex-shrink: 0;
}

.sidebar.dark-mode .color-preview {
  border-color: #3a3a3a;
}

.color-value {
  font-size: 14px;
  color: #6b7280;
  font-family: monospace;
  transition: color 0.3s ease;
}

.sidebar.dark-mode .color-value {
  color: #94a3b8;
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

.sidebar.dark-mode .modal-footer {
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

.sidebar.dark-mode .cancel-btn {
  background: #353535;
  color: #f1f5f9;
  border-color: #404040;
}

.sidebar.dark-mode .cancel-btn:hover {
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

.delete-project-btn {
  background: none;
  border: none;
  cursor: pointer;
  padding: 4px;
  border-radius: 4px;
  transition: all 0.2s;
  opacity: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  position: absolute;
  right: 12px;
  top: 50%;
  transform: translateY(-50%);
  width: 24px;
  height: 24px;
}

.project-item:hover .delete-project-btn {
  opacity: 0.6;
}

.delete-project-btn:hover {
  opacity: 1 !important;
  background: #fee2e2;
}

.sidebar.dark-mode .delete-project-btn:hover {
  background: #3a2a2a;
}

.delete-project-icon {
  width: 14px;
  height: 14px;
  object-fit: contain;
}

.delete-message {
  font-size: 16px;
  color: #374151;
  margin: 0 0 12px 0;
  text-align: center;
}

.sidebar.dark-mode .delete-message {
  color: #f1f5f9;
}

.delete-project-info {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  background: #f9fafb;
  border-radius: 8px;
  margin: 12px 0;
}

.sidebar.dark-mode .delete-project-info {
  background: #2f2f2f;
}

.delete-project-name {
  font-size: 16px;
  font-weight: 600;
  color: #111827;
  margin: 0;
}

.sidebar.dark-mode .delete-project-name {
  color: #f1f5f9;
}

.delete-warning {
  font-size: 13px;
  color: #dc2626;
  margin: 8px 0 0 0;
  padding: 8px;
  background: #fee2e2;
  border-radius: 6px;
}

.sidebar.dark-mode .delete-warning {
  color: #fca5a5;
  background: #3a2a2a;
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
</style>

