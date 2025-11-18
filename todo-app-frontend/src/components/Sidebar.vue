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
      <h3 class="section-title">PROJECTS</h3>
      <div 
        v-for="project in projects" 
        :key="project.name"
        class="project-item"
        :class="{ active: selectedFilter === `project:${project.name}` }"
        @click="$emit('filter-change', `project:${project.name}`)"
      >
        <span class="project-dot" :style="{ backgroundColor: project.color }"></span>
        <span class="project-name">{{ project.name }}</span>
        <span class="project-count">{{ getProjectCount(project.name) }}</span>
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
      projects: [
        { name: 'Work Projects', color: '#3b82f6' },
        { name: 'Personal', color: '#10b981' },
        { name: 'Learning', color: '#9333ea' }
      ]
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
      return this.tasks.filter(t => t.project === projectName).length
    }
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

.section-title {
  font-size: 11px;
  font-weight: 700;
  color: #9ca3af;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  margin: 0 0 12px 12px;
  transition: color 0.3s ease;
}

.sidebar.dark-mode .section-title {
  color: #64748b;
}

.project-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 8px 12px;
  border-radius: 6px;
  cursor: pointer;
  transition: background 0.2s;
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
}

.sidebar.dark-mode .project-name {
  color: #f1f5f9;
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
</style>

