import {reactive} from 'vue';

type SnackbarState = {
  open: boolean;
  message: string;
  color: string;
  timeout: number;
};

const snackbarState = reactive<SnackbarState>({
  open: false,
  message: '',
  color: 'success',
  timeout: 3000,
});

function showSnackbar(message: string, color = 'success', timeout = 3000) {
  snackbarState.message = message;
  snackbarState.color = color;
  snackbarState.timeout = timeout;
  snackbarState.open = true;
}

function showError(message: string, timeout = 4000) {
  showSnackbar(message, 'error', timeout);
}

export function useSnackbar() {
  return {
    snackbarState,
    showSnackbar,
    showError,
  };
}
