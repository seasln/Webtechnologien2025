const API_BASE_URL = 'https://webtechnologien2025-backend.onrender.com';

// Alle Todos abrufen
export async function getAllTodos() {
    try {
        const response = await fetch(`${API_BASE_URL}/todos`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
            }
        });

        if (!response.ok) {
            throw new Error(`HTTP error! Status: ${response.status}`);
        }

        const tasks = await response.json();
        console.log('Tasks erfolgreich abgerufen:', tasks);
        return tasks;
    } catch (error) {
        console.error('Fehler beim Abrufen der Tasks:', error);
        throw error;
    }
}

// Einzelnen Task nach ID abrufen
export async function getTaskById(id) {
    try {
        const response = await fetch(`${API_BASE_URL}/tasks/${id}`);

        if (!response.ok) {
            throw new Error(`HTTP error! Status: ${response.status}`);
        }

        const task = await response.json();
        return task;
    } catch (error) {
        console.error('Fehler beim Abrufen des Tasks:', error);
        throw error;
    }
}

// Beispiel: Verwendung in deiner App
export async function loadTasksIntoUI() {
    try {
        const tasks = await getAllTodos();

        // Hier würdest du die Tasks in deine UI einfügen
        // Beispiel für Vanilla JavaScript:
        const taskContainer = document.querySelector('.task-list');

        if (taskContainer) {
            taskContainer.innerHTML = ''; // Leeren

            tasks.forEach(task => {
                const taskElement = createTaskElement(task);
                taskContainer.appendChild(taskElement);
            });
        }

        return tasks;
    } catch (error) {
        console.error('Fehler beim Laden der Tasks:', error);
        // Zeige Fehlermeldung in der UI
        alert('Tasks konnten nicht geladen werden. Bitte versuche es später erneut.');
    }
}

