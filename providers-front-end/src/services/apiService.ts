import axios from 'axios';
import { Provider, ProviderCreate, ProvidersListResponse, AppInfo, MessageResponse, PaginationParams } from '../types';

// Configuración base de axios
// En Docker, usar el nombre del servicio backend; en desarrollo local, usar localhost
const API_BASE_URL = window.location.hostname === 'localhost' 
  ? 'http://localhost:8080/api/v1'
  : 'http://localhost:8080/api/v1'; // Temporalmente usar localhost para pruebas

const apiClient = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
  timeout: 10000,
});

// Interceptor para manejo de errores globales
apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    console.error('API Error:', error.response?.data || error.message);
    return Promise.reject(error);
  }
);

// Servicios de la API
export const apiService = {
  // Obtener información de la aplicación
  async getAppInfo(): Promise<AppInfo> {
    try {
      const response = await apiClient.get<AppInfo>('/gapsi/info');
      return response.data;
    } catch (error) {
      throw this.handleError(error);
    }
  },

  // Obtener lista paginada de proveedores
  async getProviders(params: PaginationParams): Promise<ProvidersListResponse> {
    try {
      const response = await apiClient.get<ProvidersListResponse>('/gapsi/providers', {
        params: {
          page: params.page,
          size: params.size,
        },
      });
      return response.data;
    } catch (error) {
      throw this.handleError(error);
    }
  },

  // Crear un nuevo proveedor
  async createProvider(provider: ProviderCreate): Promise<Provider> {
    try {
      const response = await apiClient.post<Provider>('/gapsi/providers', provider);
      return response.data;
    } catch (error) {
      throw this.handleError(error);
    }
  },

  // Eliminar un proveedor
  async deleteProvider(id: string): Promise<MessageResponse> {
    try {
      const response = await apiClient.delete<MessageResponse>(`/gapsi/providers/${id}`);
      return response.data;
    } catch (error) {
      throw this.handleError(error);
    }
  },

  // Manejo centralizado de errores
  handleError(error: any): Error {
    if (axios.isAxiosError(error)) {
      const errorMessage = error.response?.data?.error || error.message || 'Error desconocido';
      const errorCode = error.response?.data?.code || 'UNKNOWN_ERROR';
      
      return new Error(`${errorCode}: ${errorMessage}`);
    }
    
    return new Error('Error de conexión con el servidor');
  },
};

export default apiService;