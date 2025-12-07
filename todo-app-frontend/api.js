//npm run dev = localhost
//npm run build = render url
export const API_BASE_URL = import.meta.env.VITE_API_BASE_URL;

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

        return await response.json();
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

        return await response.json();
    } catch (error) {
        console.error('Fehler beim Abrufen des Tasks:', error);
        throw error;
    }
}

// Todo speichern (POST)
export async function createTodo(todo) {
    try {
        const response = await fetch(`${API_BASE_URL}/todos`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(todo)
        });

        if (!response.ok) {
            throw new Error(`Fehler beim Speichern! Status: ${response.status}`);
        }

        return await response.json();
    } catch (error) {
        console.error("Fehler beim Erstellen eines Todos:", error);
        throw error;
    }
}
